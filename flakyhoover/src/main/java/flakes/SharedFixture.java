package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
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
	
	/*
	 * TODO:
	 * 1. Scan several tests classes inside the same file. Fix extending a class?
	 * 2. If the shared variable exists in both setup and teardown, then it should not be regarded as a smell.
	 */

	private final static String COLLECT_STATE = "collectState";
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
		return smellyElementList.stream().filter(AbstractSmellElement::getHasSmell).count() >= 1;
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

		classVisitor.setState(COLLECT_STATE);
		classVisitor.visit(testFileCompilationUnit, null);

		ASTHelper.removeIntersectingVars(classVisitor.varDeclExpr, classVisitor.methodCalls);

		classVisitor.setState("analyzeFixtureState");
		classVisitor.visit(testFileCompilationUnit, null);

		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

//		System.out.println("classVariables: ");
//		classVisitor.classVariables.forEach(a -> System.out.println(a));
//		System.out.println();
//
//		System.out.println("methodCalls: ");
//		classVisitor.methodCalls.forEach((key, value) -> {
//			System.out.println("Key: " + key);
//			System.out.println("Value: " + value);
//			System.out.println("");
//
//		});

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void>  {

		private boolean hasSmell = false;
		private boolean isTestClass = false;

		private TestMethod testMethod;
		private MethodDeclaration currentMethod = null;
		private TestSmell testSmell = new TestSmell();

		private Set<String> allClassMethods = new HashSet<>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<>();

		private ArrayList<String> jClasses = Util.getAllJavaClasses();

		private String flakinessType = "concurrency";
		private String state;

		private Map<String, Set<String>> methodCalls = new HashMap<>();
		private Map<String, Set<String>> varDeclExpr = new HashMap<>();

		private List<String> methodExceptions = new ArrayList<>(
				Arrays.asList("printStackTrace", "toString", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		public void setState(String state) {
			this.state = state;
		}

		private void initTestSmells(String methodName) {
			testSmell = new TestSmell();
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(methodName);
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);
			testSmell.setSmelly(false);

		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (isTestClass && state.equals(COLLECT_STATE)) {
				ASTHelper.addToClassVariabels(n, classVariables);
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass) {

				boolean isTestMethod = ASTHelper.isTestMethod(n);

				addNestedFunctionParameters(n);

				if (isTestMethod) {
					currentMethod = n;

					switch (state) {

						case COLLECT_STATE:
							initVariables();
							break;
	
						case "analyzeFixtureState":
							analyzeMethodFixtures();
							break;
	
						case "analyzeRelationState":
							analyzeMethodCalls();
							break;
	
						default:
							break;
					}
				}
			}
		    /* Explanation of super.visit().
			 * super.visit() recursively iterates all MethodDeclarations which means methods inside methods will be found first. 
			 * Be performing checks before super() is called we assure that we find methods annotated with @Test before possible 
			 * nested declarations. 
			 */
			super.visit(n, arg);
		}
		
		@Override
		public void visit(NameExpr n, Void arg) {
			if (currentMethod != null && state.equals(COLLECT_STATE)) {

				String method = currentMethod.getNameAsString();
				Set<String> methodCallArray = null;

				if (methodCalls.containsKey(method)) {
					methodCallArray = methodCalls.get(method);
					methodCallArray.add(n.getNameAsString());
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {

			if (currentMethod != null && state.equals(COLLECT_STATE)) {
				Set<String> varDeclExprArray = null;

				if (varDeclExpr.containsKey(currentMethod.getNameAsString())) {
					varDeclExprArray = varDeclExpr.get(currentMethod.getNameAsString());
				
					for (VariableDeclarator variableDeclarator : n.getVariables()) {
						varDeclExprArray.add(variableDeclarator.getNameAsString());
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals(COLLECT_STATE)) {

				String method = currentMethod.getNameAsString();
				if (methodCalls.containsKey(method) && !n.getScope().isPresent()) {
					/*
					 * If no scope is present, that means we are calling on a local method in this
					 * class.
					 */
					IntelMethod methodData = ASTHelper.getMethod(method, this.allMethodsData);
					if (methodData != null && !methodExceptions.contains(method)) {
						methodData.addMethod(n.getNameAsString());
					}
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
		
		private void analyzeMethodCalls() {

			if (currentMethod != null) {
				String method = currentMethod.getNameAsString();
				if (!ASTHelper.checkIfSmelly(method, allMethodsData)) {

					Optional<Position> position = currentMethod.getBegin();
					int lineNr = position.isPresent() ? position.get().line : -1;
					
					initTestSmells(method);
					testMethod = new TestMethod(method, lineNr);
					testMethod.setHasSmell(false);
					hasSmell = analyzeRelations(currentMethod);

					if (hasSmell) {

						testMethod.setHasSmell(hasSmell);
						testSmell.setSmelly(hasSmell);
						testSmells.add(testSmell);
						smellyElementList.add(testMethod);
						ASTHelper.setMethodStatusSmelly(currentMethod, allMethodsData, hasSmell);
					}
				}
			}
		}

		private void analyzeMethodFixtures() {
			if (currentMethod != null) {

				String method = currentMethod.getNameAsString();
				
				Optional<Position> position = currentMethod.getBegin();
				int lineNr = position.isPresent() ? position.get().line : -1;
				
				initTestSmells(method);
				testMethod = new TestMethod(method, lineNr);
				testMethod.setHasSmell(false); // default value is false (i.e. no smell)

				hasSmell = ASTHelper.analyzeTestRunWar(classVariables, methodCalls, method);
				if (hasSmell) {
					testMethod.setHasSmell(hasSmell);
					testSmell.setSmelly(hasSmell);
					testSmells.add(testSmell);
					smellyElementList.add(testMethod);
					ASTHelper.setMethodStatusSmelly(currentMethod, allMethodsData, hasSmell);
				}
			}
		}

		private void initVariables() {
			String methodName = currentMethod.getNameAsString();

			if (!allClassMethods.contains(methodName)) {
				allMethodsData.add(new IntelMethod(methodName, false));
				allClassMethods.add(methodName);
			}

			if (!varDeclExpr.containsKey(methodName)) {

				Set<String> parameters = new HashSet<>();

				for (Parameter param : currentMethod.getParameters()) {
					parameters.add(param.getNameAsString());
				}
				varDeclExpr.put(methodName, parameters);
			}

			if (!methodCalls.containsKey(methodName)) {
				methodCalls.put(methodName, new HashSet<>());
			}
		}

		private void addNestedFunctionParameters(MethodDeclaration n) {

			if (currentMethod != null && varDeclExpr.containsKey(currentMethod.getNameAsString())) {

				Set<String> variables = varDeclExpr.get(currentMethod.getNameAsString());

				for (Parameter param : n.getParameters()) {
					variables.add(param.getNameAsString());
				}
				varDeclExpr.put(currentMethod.getNameAsString(), variables);

			}
		}
	}
}
