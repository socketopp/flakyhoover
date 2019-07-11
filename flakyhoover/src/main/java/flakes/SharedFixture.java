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

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class SharedFixture extends AbstractFlaky {

	private List<AbstractFlakyElement> flakyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;
	private Map<String, Set<String>> methodCalls = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> varDeclExpr = new HashMap<String, Set<String>>();

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public SharedFixture() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "TestRunWar";
	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<TestSmell>();
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

//		Util.genericPrint(classVisitor.allClassMethods);

		System.out.println("classVariables: ");
		Util.genericPrint(classVisitor.classVariables);

		System.out.println("methodCalls: ");
		Util.genericPrintMap(classVisitor.methodCalls);
		this.methodCalls = classVisitor.methodCalls;

		System.out.println("Declared variables: ");
		Util.genericPrintMap(classVisitor.varDeclExpr);
		this.varDeclExpr = classVisitor.varDeclExpr;

		System.out.println("All methods: ");
		for (IntelMethod intel : classVisitor.allMethodsData) {
			System.out.println(intel.getName() + " " + intel.getMethods());
		}

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private boolean hasFlaky = false;
		private boolean isTestClass = false;

		private TestMethod testMethod;
		private MethodDeclaration currentMethod = null;
		private TestSmell testSmell = new TestSmell();

		private Set<String> allClassMethods = new HashSet<String>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();

		private ArrayList<String> jClasses = Util.getAllJavaClasses();

		private String flakinessType = "concurrency";
		private String state;
		private String parentType;
		private String pName = null;

		private Map<String, Set<String>> methodCalls = new HashMap<String, Set<String>>();
		private Map<String, Set<String>> varDeclExpr = new HashMap<String, Set<String>>();

		private List<String> methodExceptions = new ArrayList<String>(
				Arrays.asList("printStackTrace", "toString", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		public void setState(String state) {
			this.state = state;
		}

		private void initTestSmells(String methodName) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(methodName);
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			System.out.println("ClassOrInterfaceDeclaration: " + n.getNameAsString());
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
				System.out.println("FieldDeclaration: " + n.toString());
				// TODO Unconventional use of getchilnodes, should do it some other way
				ASTHelper.addToClassVariabels(n, classVariables);
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("MethodDeclaration: " + n.getNameAsString());

			parentType = n.getParentNode().get().getClass().getSimpleName();

			if (isTestClass) {

				if (parentType.equals("ObjectCreationExpr")) {
					pName = currentMethod.getNameAsString();
				}
				currentMethod = n;

				switch (state) {

				case "collectState": {

					if (!parentType.equals("ObjectCreationExpr")) {
						this.allClassMethods.add(n.getNameAsString());
						this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));

						if (!varDeclExpr.containsKey(n.getNameAsString())) {
							varDeclExpr.put(n.getNameAsString(), new HashSet<String>());
						}
						if (!methodCalls.containsKey(n.getNameAsString())) {
							methodCalls.put(n.getNameAsString(), new HashSet<String>());
						}
					}

					String key = currentMethod.getNameAsString();
					if (pName != null) {
						key = pName;
					}
					Set<String> varDeclExprArray = varDeclExpr.get(key);
					ASTHelper.addParameterToVariabelsDeclarations(n, varDeclExprArray);
					break;
				}

				case "analyzeFixtureState": {

					String key = pName != null ? pName : currentMethod.getNameAsString();
					initTestSmells(key);
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
					break;
				}
				}

				super.visit(n, arg);

				switch (state) {
				case "collectState": {
					// Reset
					pName = null;
					currentMethod = null;
					break;
				}

				case "analyzeFixtureState": {

					hasFlaky = ASTHelper.analyzeTestRunWar(classVariables, methodCalls, n.getNameAsString());
					if (!hasFlaky) {
						hasFlaky = ASTHelper.analyzeTestRunWarStatic(classVariables, methodCalls, n.getNameAsString());
					}

					testMethod.setHasFlaky(hasFlaky);

					if (hasFlaky) {
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);
					}

					// Only reset testSmells when are are analyzing class functions
					if (pName == null) {
						testSmell = new TestSmell();
					}

					hasFlaky = false;
					currentMethod = null;
					pName = null;

					break;

				}

				case "analyzeRelationState": {

					String key = n.getNameAsString();

					initTestSmells(key);
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)

					// CUrrently working on relationships
					hasFlaky = analyzeRelations(n);
					if (hasFlaky) {

						testMethod.setHasFlaky(true);
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
					}

					hasFlaky = false;
					testSmell = new TestSmell();
					break;
				}
				}

			}

		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("MethodCallExpr: " + n.getNameAsString());

				String key = pName != null ? pName : currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = this.varDeclExpr.get(key);

				String base = ASTHelper.checkIfClassMember(n);

				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(key, this.allMethodsData);
					if (!n.getNameAsString().equals(key) && !methodExceptions.contains(n.getNameAsString())) {
						methodData.addMethod(n.getNameAsString());
					}
				}

				// Get paramters.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						String expr_val = expr.toString();
						if (expr.isNameExpr() || expr.isVariableDeclarationExpr()) {
							if (!Util.checkIntBool(expr_val) && !Util.isStringUpperCase(expr_val)
									&& !varDeclExprArray.contains(expr_val)) {
								System.out.println("mm: " + expr_val);
								methodCallArray.add(expr_val);
							}
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

						if (!this.jClasses.contains(callexpr) && !methodExceptions.contains(n.getNameAsString())) {

							// Check for static variables
							if (Character.isUpperCase(n.toString().charAt(0))) {
								if (!n.toString().contains("(")) {
									System.out.println("IGOHERE");
									methodCallArray.add(n.toString());
								}
							} else {
								// Normal variables/
								System.out.println("IGOHEREINSTEAD: " + callexpr);
								System.out.println("IGOHEREINSTEAD: " + n.toString());

								methodCallArray.add(callexpr);
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
				System.out.println("ObjectCreationExpr: " + n.toString());

				// Check if parent exist, if they exist, get their name so we can add referenced
				// variables to their scope.
				String key = pName != null ? pName : currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);

				// Get all params for any object creation.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						if (expr.isNameExpr() || expr.isFieldAccessExpr()) {
							String expr_val = expr.toString();
							// only variables/objects, not numbers or booleans
							if (!Util.checkIntBool(expr_val)) {
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
				System.out.println("FieldAccessExpr: " + n.toString());

				// Check if parent exist, if they exist, get their name so we can add referenced
				// variables to their scope.
				String key = pName != null ? pName : currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = this.varDeclExpr.get(key);

				// Access of a field of an object or a class. In person.name "name" is the name
				// and "person" is the scope.
				if (!(n.findAncestor(MethodCallExpr.class).isPresent())) {
					if (n.getScope().getParentNode().get().getNodesByType(NameExpr.class).size() > 0) {
						String scope = n.getScope().getParentNode().get().getNodesByType(NameExpr.class).get(0)
								.toString();
						if (!varDeclExprArray.contains(scope) && !n.toString().contains("(")
								&& !jClasses.contains(scope)) {
							methodCallArray.add(n.toString());
						}
					}
				}

				super.visit(n, arg);
			}
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("VariableDeclarationExpr: " + n);
				// Get all declarared variables such as Object obj = new Object(); We add obj to
				// varDeclExprArray

//				String key = currentMethod.getNameAsString();
				String key = pName != null ? pName : currentMethod.getNameAsString();

//				 Check if parent does not exists. We don't want to add declared variables inside nested functions to the parent functions scope.
				if (pName == null) {
					Set<String> varDeclExprArray = varDeclExpr.get(key);
					ASTHelper.addToVariabelsDeclarations(n, varDeclExprArray);
				}

			}
			super.visit(n, arg);
		}

		@Override
		public void visit(AssignExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("AssignExpr: " + n.toString());

				String key = pName != null ? pName : currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = varDeclExpr.get(key);

				String value = n.getTarget().toString();
				System.out.println("value: " + n.getValue());
				System.out.println("target: " + n.getTarget());

//				if (n.getTarget().isNameExpr()) {
				if (n.getTarget().isNameExpr()) {
//				if (n.getTarget().isArrayAccessExpr()) {

					if (!varDeclExprArray.contains(value)) {
						if (!Util.checkIntBool(value)) {
							System.out.println("ADDING1: " + value);
							methodCallArray.add(value);
						}
					}
				}

			}
			super.visit(n, arg);
		}

		public boolean analyzeRelations(MethodDeclaration n) {

			for (String method : allClassMethods) {
				if (method.equals(n.getNameAsString())) {
					if (!ASTHelper.checkIfFlaky(method, allMethodsData)) {
						IntelMethod intelMethod = ASTHelper.getMethod(method, allMethodsData);
						if (!intelMethod.isFlaky()) {
							for (String call : intelMethod.getMethods()) {
								if (ASTHelper.checkIfFlaky(call, allMethodsData)) {
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

//@Override
//public void visit(VariableDeclarator n, Void arg) {
//	System.out.println("VariableDeclarator: " + n);
//	super.visit(n, arg);
//}

// No need, this is done in collectState with help of addParameterToVariabelsDeclarations.
//@Override
//public void visit(Parameter n, Void arg) {
//
//	if (currentMethod != null && state.equals("collectState")) {
//		System.out.println("Parameter: " + n);
//		String key = currentMethod.getNameAsString();
//		Set<String> varDeclExprArray = varDeclExpr.get(key);
//		String param = ASTHelper.getParameter(n);
//		if (!param.equals("")) {
//			varDeclExprArray.add(param);
//		}
//	}
//	super.visit(n, arg);
//}

// use new fashion nameExpr check instead to find base.
//String base = ASTHelper.checkIfClassMember(n);
//
//if (!base.equals("empty")) {
//	System.out.println("BASE: " + base);
//} else {
//	IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
//	if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
//		System.out.println("methodData: " + n.getNameAsString());
//		methodData.addMethod(n.getNameAsString());
//	}
//}