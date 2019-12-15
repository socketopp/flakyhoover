package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.Main;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class IndirectTesting extends AbstractSmell {

	private List<AbstractSmellElement> smellyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public IndirectTesting() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "IndirectTesting";
	}

	@Override
	public List<AbstractSmellElement> getSmellyElements() {
		return smellyElementList;
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		testSmells = new ArrayList<>();
		this.fileName = testClassName;
		this.projectName = projectName;

		IndirectTesting.ClassVisitor classVisitor;
		classVisitor = new IndirectTesting.ClassVisitor();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);

		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private MethodDeclaration prevMethod = null;

		private boolean hasSmell = false;
		private boolean isTestClass = false;

		private TestSmell testSmell = new TestSmell();
		private ArrayList<String> jClasses = Util.getAllJavaClasses();
		private List<String> exceptions = new ArrayList<>(
				Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
						"TestRunner", "", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		private TestMethod testMethod;
		private Set<String> indirectClasses = new HashSet<>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<>();
		private Set<String> allClassMethods = new HashSet<>();

		private String flakinessType = "test-order-dependency";
		private String state;
		private String className;
		private String methodName;
		private int scope = 0;
		private String parentType = null;
		private String classNameTest = null;
		private String parentClass = null;

		private void initTestSmells(String methodName) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(methodName);
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);
			testSmell.setSmelly(false);

		}

		public void setState(String state) {
			this.state = state;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
				className = Util.removeTest(n.getNameAsString());
				classNameTest = n.getNameAsString();
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass) {

				Optional<Node> optNodeParentType = n.getParentNode();
				if (optNodeParentType.isPresent()) {
					parentType = optNodeParentType.get().getClass().getSimpleName();
				}

				Optional<ClassOrInterfaceDeclaration> optNodeParentClass = n
						.findAncestor(ClassOrInterfaceDeclaration.class);
				if (optNodeParentClass.isPresent()) {
					parentClass = optNodeParentClass.get().getNameAsString();
				}

				switch (state) {
				case "analyze": {
					scope++;
					// We check each methods parentType in order to determine which methods are test
					// methods and which are methods inside methods and inside new classes.
					// We are only interested in top level methods, that way we check that it's
					// parentClass is equal to the current test class.
					if (parentType.equals("ClassOrInterfaceDeclaration") && parentClass.equals(classNameTest)) {
						String testName = n.getNameAsString();

						currentMethod = n;
						methodName = Util.removeTest(testName);
						this.allMethodsData.add(new IntelMethod(testName, false));
						this.allClassMethods.add(testName);
						ASTHelper.addIndirectParams(n, this.indirectClasses, jClasses, className);
						initTestSmells(testName);
						testMethod = new TestMethod(testName, n.getBegin().get().line);
						testMethod.setHasSmell(false); // default value is false (i.e. no smell)
					}
					break;

				}
				default:
					break;
				}

			}

			super.visit(n, arg);

			switch (state) {
			case "analyze": {
				scope--;
				if (currentMethod != null) {
					parentType = currentMethod.getParentNode().get().getClass().getSimpleName();
					parentClass = currentMethod.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();

					if (parentType.equals("ClassOrInterfaceDeclaration") && scope == 0) {
						if (hasSmell) {
							testSmell.setSmelly(true);
							testSmells.add(testSmell);
							smellyElementList.add(testMethod);
							testMethod.setHasSmell(hasSmell);
							ASTHelper.setMethodStatusSmelly(n, allMethodsData, true);
							testSmell = new TestSmell();
						}

						currentMethod = null;
						indirectClasses = new HashSet<>();
						hasSmell = false;
					}
				}

				break;
			}

			case "analyzeRelationState": {
				String key = n.getNameAsString();
				initTestSmells(key);
				testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
				testMethod.setHasSmell(false);
				hasSmell = analyzeRelations(n);
				if (hasSmell) {
					testMethod.setHasSmell(true);
					testSmell.setSmelly(true);
					testSmells.add(testSmell);
					smellyElementList.add(testMethod);
				}
				hasSmell = false;
				testSmell = new TestSmell();
				testMethod.addDataItem("IndirectTestingCount", "0");
				break;

			}
			default:
				break;
			}
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && state.equals("analyze")) {

				for (VariableDeclarator variableDeclarator : n.getVariables()) {

					String type = variableDeclarator.getTypeAsString();
					String name = variableDeclarator.getNameAsString();

					// Not sure, but check if it is referenced in that case we class it as smelly???
					boolean equalToClass = type.toLowerCase().equals(className.toLowerCase());
					if (!jClasses.contains(type) && !equalToClass || Main.getAllProductionFiles().contains(className)) {
						// Since it is a class member and indirectClass object used in testmethod
						classVariables.add(name);
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarator n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {

				List<ClassOrInterfaceType> found = n.findAll(ClassOrInterfaceType.class);
				// Look for nested classobjects inside lists: List<ClassObject>
				if (found.size() > 0) {
					for (ClassOrInterfaceType c : found) {
						String object = c.getNameAsString();
						object = object.replace("[]", "");
						if (!jClasses.contains(object) && ASTHelper.checkIfEqMethodOrClass(object, className,
								currentMethod.getNameAsString())) {
							hasSmell = true;
						}
					}
				} else {
					// In case if it is not
					String type = n.getTypeAsString();
					String name = n.getNameAsString();
					type = type.replace("[]", "");
					if (!jClasses.contains(type)
							&& ASTHelper.checkIfEqMethodOrClass(type, className, currentMethod.getNameAsString())) {
						hasSmell = true;
						indirectClasses.add(name);
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {

				if (n.getScope().isPresent()) {

					// Go through parameters
					if (n.getArguments().size() > 0) {

						for (Expression expr : n.getArguments()) {
							if (indirectClasses.contains(expr.toString())) {
								this.hasSmell = true;
							}
						}
					}

					// Check for exception here Integer.ParseInt
					// Check if object is among indirect classes is used
					// obj.getInstance().someMethod(); get obj (nameexpr) and check if it is in
					// indirectClasses.
					if (n.getScope().get() instanceof ClassExpr) {
						ClassExpr expr = ((ClassExpr) n.getScope().get());
						String classExpr = expr.getTypeAsString();
						if (!jClasses.contains(classExpr) && !classExpr.equals(className.toLowerCase())
								&& !classExpr.toLowerCase().equals(Util.removeTest(methodName).toLowerCase())
								|| Main.getAllProductionFiles().contains(className)) {

							this.hasSmell = true;
						}
					}

					// Checkfor all object calls "object.call()"
					else if (n.getScope().get() instanceof NameExpr) {

						// && n.findAncestor(VariableDeclarator.class) ==null) {

						String nameExpr = ((NameExpr) n.getScope().get()).toString();

						if (Character.isUpperCase(nameExpr.charAt(0)) && !jClasses.contains(nameExpr)
								&& !exceptions.contains(nameExpr)) {

							if (ASTHelper.checkIfEqMethodOrClass(nameExpr, className, methodName)) {
								if (!nameExpr.toLowerCase().contains("factory")) {
									this.hasSmell = true;
								}

							}

						} else if (indirectClasses.contains(nameExpr) && !exceptions.contains(nameExpr)
								&& !jClasses.contains(nameExpr) && !Character.isUpperCase(nameExpr.charAt(0))) {

							this.hasSmell = true;

						}

					}

				} else {
					// Go through parameters
					// Check for exception here Integer.
					if (n.getArguments().size() > 0) {

						for (Expression expr : n.getArguments()) {

							if (indirectClasses.contains(expr.toString()) && !exceptions.contains(expr.toString())
									|| classVariables.contains(expr.toString())
											&& !exceptions.contains(expr.toString())) {

								this.hasSmell = true;

							}
						}
					}
					String base = ASTHelper.checkIfClassMember(n);
					if (base.equals("empty") && parentType.equals("ClassOrInterfaceDeclaration")) {

						IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(),
								this.allMethodsData);

						if (methodData != null && !n.getNameAsString().equals(currentMethod.getNameAsString())
								&& !exceptions.contains(n.getNameAsString())) {
							methodData.addMethod(n.getNameAsString());
						}

					}
				}
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				if (prevMethod != null) {
					currentMethod = prevMethod;
				}

				String type = n.getTypeAsString();

				if (!jClasses.contains(type)
						&& ASTHelper.checkIfEqMethodOrClass(type, className, currentMethod.getNameAsString())
						|| Main.getAllProductionFiles().contains(className)) {

					hasSmell = true;

				}

			}
			super.visit(n, arg);

		}

		public boolean analyzeRelations(MethodDeclaration n) {

			for (String method : allClassMethods) {
				if (method.equals(n.getNameAsString()) && !ASTHelper.checkIfSmelly(method, allMethodsData)) {
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
			return false;
		}
	}
}
