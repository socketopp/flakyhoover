package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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

		classVisitor.setState("initState");
		classVisitor.visit(testFileCompilationUnit, null);

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

		System.out.println("Declared variables: ");
		Util.genericPrintMap(classVisitor.varDeclExpr);

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

		private String flakinessType = "concurrency";
		private String state;

		private Map<String, Set<String>> methodCalls = new HashMap<String, Set<String>>();
		private Map<String, Set<String>> varDeclExpr = new HashMap<String, Set<String>>();

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			System.out.println("ClassOrInterfaceDeclaration: " + n.getNameAsString());
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (isTestClass && state.equals("initState")) {
				// Unconventional use of getchilnodes, should do it some other way
				System.out.println("YE: " + n);

				ASTHelper.addToClassVariabels(n, classVariables);

			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("");
			System.out.println("MethodDeclaration: " + n.getNameAsString());

			if (isTestClass) {

				switch (state) {
				case "initState": {
					System.out.println("s: " + getState());

//					this.allClassMethods.add(n.getNameAsString());
//					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));

//					if (!varDeclExpr.containsKey(n.getNameAsString())) {
//						varDeclExpr.put(n.getNameAsString(), new HashSet<String>());
//					}
//
//					if (!methodCalls.containsKey(n.getNameAsString())) {
//						methodCalls.put(n.getNameAsString(), new HashSet<String>());
//					}

					break;
				}

				case "collectState": {
					System.out.println("c :" + state);
					currentMethod = n;

					this.allClassMethods.add(n.getNameAsString());
					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));

					if (!varDeclExpr.containsKey(n.getNameAsString())) {
						varDeclExpr.put(n.getNameAsString(), new HashSet<String>());
					}

					if (!methodCalls.containsKey(n.getNameAsString())) {
						methodCalls.put(n.getNameAsString(), new HashSet<String>());
					}

					String key = currentMethod.getNameAsString();

					Set<String> varDeclExprArray = varDeclExpr.get(key);
					ASTHelper.addParameterToVariabelsDeclarations(n, varDeclExprArray);

					// RESET?
					break;
				}

				case "analyzeFixtureState": {

					System.out.println("a:" + state);
					initTestSmells(n);
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
					break;
				}

				case "analyzeRelationState": {
					System.out.println("sa:" + state);
					break;
				}

				}

				super.visit(n, arg);

				switch (state) {
				case "collectState": {
					// Reset
					currentMethod = null;
					break;
				}
				case "analyzeFixtureState": {

					hasFlaky = ASTHelper.analyzeTestRunWar(classVariables, methodCalls, n.getNameAsString());
					testMethod.setHasFlaky(hasFlaky);

					if (hasFlaky) {
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);
					}
					testSmell = new TestSmell();
					hasFlaky = false;
					break;

				}

				case "analyzeRelationState": {
					System.out.println("analyzeRelationState after");
					initTestSmells(n);

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

				System.out.println("");

			}

		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("MethodCallExpr: " + n.getNameAsString());

				String key = currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);

//				if (n.getArguments().size() > 0) {
//					ASTHelper.addArguments(n, methodCallArray);
//				} THis (below) replaces ASTHelper.addArguments

				// Get paramters.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						System.out.println("EXPR: " + expr);
						String expr_val = expr.toString();
						// only variables, not numbers or booleans

						if (!Util.checkIntBool(expr_val)) {
							methodCallArray.add(expr_val);
						}
					}
				}

				// get base object obj in: obj.getinstance().call(params...)
				if (n.getScope().isPresent()) {
					if (n.getScope().get() instanceof NameExpr) {
						String callexpr = ((NameExpr) n.getScope().get()).getNameAsString();
						methodCallArray.add(callexpr);
					}
				} else {
					// method called such as a in: a(x...)
					IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
					if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
						System.out.println("methodData: " + n.getNameAsString());
						methodData.addMethod(n.getNameAsString());
					}
				}

			}
			super.visit(n, arg);
		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {
				String key = currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);
				// Get all params for any object creation.
				System.out.println("ObjectCreationExpr: " + n);
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						String expr_val = expr.toString();

						// only variables/objects, not numbers or booleans
						if (!Util.checkIntBool(expr_val)) {
							methodCallArray.add(expr_val);
						}
					}
				}

			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {

			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("VariableDeclarationExpr: " + n);
				// Get all declarared variables such as Object obj = new Object(); We add obj to
				// varDeclExprArray
				String key = currentMethod.getNameAsString();
				Set<String> varDeclExprArray = varDeclExpr.get(key);
				ASTHelper.addToVariabelsDeclarations(n, varDeclExprArray);
			}
			super.visit(n, arg);
		}

//		Maybe?
		@Override
		public void visit(AssignExpr n, Void arg) {
			if (currentMethod != null && state.equals("collectState")) {
				System.out.println("AssignExpr: " + n);
				System.out.println("n.getRight(): " + n.getTarget());
				String key = currentMethod.getNameAsString();
				Set<String> methodCallArray = methodCalls.get(key);
				Set<String> varDeclExprArray = varDeclExpr.get(key);
				String value = n.getTarget().toString();
				if (!varDeclExprArray.contains(value)) {
					if (!Util.checkIntBool(value)) {
						methodCallArray.add(value);
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