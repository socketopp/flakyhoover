package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class SharedFixture extends AbstractSmell {

	private List<AbstractSmellElement> smellyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public SharedFixture() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "TestRunWar";
	}

	@Override
	public List<AbstractSmellElement> getSmellyElements() {
		return smellyElementList;
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<>();
		SharedFixture.ClassVisitor classVisitor;

		classVisitor = new SharedFixture.ClassVisitor();
		classVisitor.jClasses.add("LOG");

		classVisitor.setState("collectState");
		classVisitor.visit(testFileCompilationUnit, null);

		ASTHelper.removeIntersectingVars(classVisitor.varDeclExpr, classVisitor.methodCalls);

		classVisitor.setState("analyzeFixtureState");
		classVisitor.visit(testFileCompilationUnit, null);

		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);



	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private boolean hasSmell = false;
		private boolean isTestClass = false;

		private TestMethod testMethod;
		private MethodDeclaration prevMethod = null;
		private MethodDeclaration currentMethod = null;
		private TestSmell testSmell = new TestSmell();

		private Set<String> allClassMethods = new HashSet<>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<>();

		private ArrayList<String> jClasses = Util.getAllJavaClasses();

		private String flakinessType = "concurrency";
		private String state;
		private String parentType;
		private int scope = 0;

		private Map<String, Set<String>> methodCalls = new HashMap<>();
		private Map<String, Set<String>> varDeclExpr = new HashMap<>();

		private List<String> methodExceptions = new ArrayList<>(
				Arrays.asList("printStackTrace", "toString", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		public void setState(String state) {
			this.state = state;
		}

		private void initTestSmells(String methodName) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(methodName);
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);
			testSmell.setSmelly(false);

		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			// TODO Scan several tests classes inside the same file
			// TODO Fix extending a class??
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (isTestClass && state.equals("collectState")) {
				// TODO Unconventional use of getchilnodes, should do it some other way
				ASTHelper.addToClassVariabels(n, classVariables);
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass) {

				parentType = n.getParentNode().get().getClass().getSimpleName();

//				prevMethod = parentType.equals("ObjectCreationExpr") ? currentMethod : null;

				currentMethod = n;
				if (parentType.equals("ClassOrInterfaceDeclaration")) {
					prevMethod = currentMethod;
				}

				switch (state) {
				case "collectState": {

					if (parentType.equals("ClassOrInterfaceDeclaration")) {
						this.allClassMethods.add(n.getNameAsString());
						this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));

						if (!varDeclExpr.containsKey(n.getNameAsString())) {
							varDeclExpr.put(n.getNameAsString(), new HashSet<String>());
						}
						if (!methodCalls.containsKey(n.getNameAsString())) {
							methodCalls.put(n.getNameAsString(), new HashSet<String>());
						}
					}

					// TODO Works as a ternary
					String key = currentMethod.getNameAsString();

					if (prevMethod != null) {

						key = prevMethod.getNameAsString();

					}

					Set<String> varDeclExprArray = varDeclExpr.get(key);
					if (varDeclExprArray != null) {
						ASTHelper.addParameterToVariabelsDeclarations(n, varDeclExprArray);
					}

					break;
				}

				case "analyzeFixtureState": {

					String key = prevMethod != null ? prevMethod.getNameAsString() : currentMethod.getNameAsString();
					scope++;

					if (parentType.equals("ClassOrInterfaceDeclaration")) {

						initTestSmells(key);
						testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
						testMethod.setHasSmell(false); // default value is false (i.e. no smell)
					}
					break;
				}
				}

				super.visit(n, arg);

				switch (state) {

				case "collectState": {

					// Reset

					// TODO not sure about this.
//					|| scope == 0
					if (parentType.equals("ClassOrInterfaceDeclaration")) {
						currentMethod = null;
						prevMethod = null;
					}

					break;
				}

				case "analyzeFixtureState": {
					scope--;

					if (testMethod != null) {

						if (testMethod != null) {

							hasSmell = ASTHelper.analyzeTestRunWar(classVariables, methodCalls, n.getNameAsString());
							if (!hasSmell) {
								hasSmell = ASTHelper.analyzeTestRunWarStatic(classVariables, methodCalls,
										n.getNameAsString());
							}

							testMethod.setHasSmell(hasSmell);

							if (hasSmell && scope == 0) {
								testSmell.setSmelly(true);
								testSmells.add(testSmell);
								smellyElementList.add(testMethod);
								ASTHelper.setMethodStatusSmelly(n, allMethodsData, true);
							}
						}
						if (parentType.equals("ClassOrInterfaceDeclaration") || scope == 0) {
							testSmell = new TestSmell();
							hasSmell = false;
							currentMethod = null;
							prevMethod = null;
						}

					}

					break;

				}

				case "analyzeRelationState": {

					String key = n.getNameAsString();

					initTestSmells(key);
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasSmell(false); // default value is false (i.e. no smell)

					// CUrrently working on relationships
					hasSmell = analyzeRelations(n);
					if (hasSmell) {

						testMethod.setHasSmell(true);
						testSmell.setSmelly(true);
						testSmells.add(testSmell);
						smellyElementList.add(testMethod);
					}

					hasSmell = false;
					testSmell = new TestSmell();
					break;
				}
				}

			}

		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {

				if (prevMethod != null) {
					currentMethod = prevMethod;
				}
				String key = currentMethod.getNameAsString();

				Set<String> methodCallArray = null;
				if (methodCalls.containsKey(key)) {
					methodCallArray = methodCalls.get(key);

				}
				Set<String> varDeclExprArray = this.varDeclExpr.get(key);

				String base = ASTHelper.checkIfClassMember(n);

				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(key, this.allMethodsData);
					if (methodData != null) {
						if (!n.getNameAsString().equals(key) && !methodExceptions.contains(n.getNameAsString())) {
							methodData.addMethod(n.getNameAsString());
						}
					}
				}

				// Get paramters.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						String expr_val = expr.toString();

						if ((expr.isNameExpr() || expr.isVariableDeclarationExpr()) && varDeclExprArray != null
								&& !Util.checkIntBool(expr_val) && !Util.isStringUpperCase(expr_val)
								&& !varDeclExprArray.contains(expr_val) && methodCallArray != null) {
							methodCallArray.add(expr_val);

						}
					}
				}

				// get base object obj in: obj.getinstance().call(params...)
				if (n.getScope().isPresent()) {

					if (n.getScope().get() instanceof NameExpr) {

						String callexpr = ((NameExpr) n.getScope().get()).getNameAsString();

//						if (!varDeclExprArray.contains(callexpr) && !this.jClasses.contains(callexpr)
						// TODO : not sure if I should check for jClasses or not....
						// Shared resources could be a file or whatever...
//						if (!this.jClasses.contains(callexpr) && !methodExceptions.contains(n.getNameAsString())) {

						if (!methodExceptions.contains(n.getNameAsString())) {

							// Check for static variables
							if (Character.isUpperCase(n.toString().charAt(0))) {
								if (!n.toString().contains("(") && methodCallArray != null) {
									methodCallArray.add(n.toString());
								}
							} else {
								// Normal variables/
								if (methodCallArray != null) {
									methodCallArray.add(callexpr);

								}
							}
						}
					}
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {

				// Check if parent exist, if they exist, get their name so we can add referenced
				// variables to their scope.
				if (prevMethod != null) {
					currentMethod = prevMethod;
				}
				String key = currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);

				// Get all params for any object creation.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						if (expr.isNameExpr() || expr.isFieldAccessExpr()) {
							String expr_val = expr.toString();
							// only variables/objects, not numbers or booleans
							if (methodCallArray != null && !Util.checkIntBool(expr_val)) {
								methodCallArray.add(expr_val);
							}
						}
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldAccessExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {

				// Check if parent exist, if they exist, get their name so we can add referenced
				// variables to their scope.
				if (prevMethod != null) {
					currentMethod = prevMethod;
				}
				String key = currentMethod.getNameAsString();

				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = this.varDeclExpr.get(key);

				if (varDeclExprArray != null && methodCallArray != null) {
					// Access of a field of an object or a class. In person.name "name" is the name
					// and "person" is the scope.
					if (!(n.findAncestor(MethodCallExpr.class).isPresent())) {
						if (n.getScope().getParentNode().get().getNodesByType(NameExpr.class).size() > 0) {
							String scope = n.getScope().getParentNode().get().getNodesByType(NameExpr.class).get(0)
									.toString();
//							if (!varDeclExprArray.contains(scope) && !n.toString().contains("(")
//									&& !jClasses.contains(scope)) {

							if (!varDeclExprArray.contains(scope) && !n.toString().contains("(")) {
								methodCallArray.add(scope);
							}
						}
					}
				}

				super.visit(n, arg);
			}
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {
				// Get all declarared variables such as Object obj = new Object(); We add obj to
				// varDeclExprArray

				if (prevMethod != null) {
					currentMethod = prevMethod;
				}
				String key = currentMethod.getNameAsString();

//				 Check if parent does not exists. We don't want to add declared variables inside nested functions to the parent functions scope.

				Set<String> varDeclExprArray = varDeclExpr.get(key);
				if (varDeclExprArray != null)
					ASTHelper.addToVariabelsDeclarations(n, varDeclExprArray);

			}
			super.visit(n, arg);
		}

		@Override
		public void visit(AssignExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {

				if (prevMethod != null) {
					currentMethod = prevMethod;
				}
				String key = currentMethod.getNameAsString();

				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = varDeclExpr.get(key);

				String value = n.getTarget().toString();

				if (n.getTarget().isNameExpr() && varDeclExprArray != null && !varDeclExprArray.contains(value)
						&& !Util.checkIntBool(value)) {
					// TODO fixa alla nullpointer fel, inspektera kod och kör projekten.
					methodCallArray.add(value);

				}

			}
			super.visit(n, arg);
		}

		public boolean analyzeRelations(MethodDeclaration n) {

			for (String method : allClassMethods) {
				if (method.equals(n.getNameAsString())) {
					if (!ASTHelper.checkIfSmelly(method, allMethodsData)) {
						IntelMethod intelMethod = ASTHelper.getMethod(method, allMethodsData);
						if (intelMethod != null && !intelMethod.isSmelly()) {
							for (String call : intelMethod.getMethods()) {
								if (ASTHelper.checkIfSmelly(call, allMethodsData)) {
									return true;
								}
							}
						}
					}
				}
			}
			return false;
		}
	}

}
