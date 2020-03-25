package smells;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class ResourceOptimism extends AbstractSmell {

	private List<AbstractSmellElement> smellyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public ResourceOptimism() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "ResourceOptimism";
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
		ResourceOptimism.ClassVisitor classVisitor;
		classVisitor = new ResourceOptimism.ClassVisitor();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private boolean hasSmell = false;
		private boolean isTestClass = false;

		private TestMethod testMethod;
		private MethodDeclaration currentMethod = null;
		private TestSmell testSmell = new TestSmell();

		private Set<String> declaredVariables = new HashSet<>();
		private Set<String> classVariables = new HashSet<>();
		private List<String> okVariables = new ArrayList<>();
		private Set<IntelMethod> allMethodsData = new HashSet<>();
		private Set<String> allClassMethods = new HashSet<>();
		private List<String> methodExceptions = new ArrayList<>(
				Arrays.asList("printStackTrace", "toString", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		private List<String> checkResourceCalls = new ArrayList<>(Arrays.asList("exists", "isFile", "canExecute",
				"canRead", "canWrite", "notExists", "isWritable", "isReadable", "isExecutable"));

		private String flakinessType = "input-output";
		private String state;

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
		public void visit(MethodDeclaration n, Void arg) {
			if (isTestClass) {
				boolean isTestMethod = ASTHelper.isTestMethod(n);

				if (isTestMethod) {
					currentMethod = n;

					if (state.equals("analyze")) {

						String methodName = currentMethod.getNameAsString();

						ASTHelper.addParams(n, declaredVariables); // Check if necessary

						if (!allClassMethods.contains(methodName)) {
							allMethodsData.add(new IntelMethod(methodName, false));
							allClassMethods.add(methodName);
						}

						Optional<Position> position = currentMethod.getBegin();
						int lineNr = position.isPresent() ? position.get().line : -1;

						initTestSmells(methodName);
						testMethod = new TestMethod(methodName, lineNr);
						testMethod.setHasSmell(false); // default value is false (i.e. no smell)

					}

					super.visit(n, arg);

					if (state.equals("analyze")) {

						testMethod.setHasSmell(hasSmell);
						if (hasSmell) {
							testSmell.setSmelly(true);
							testSmells.add(testSmell);
							smellyElementList.add(testMethod);
							ASTHelper.setMethodStatusSmelly(n, allMethodsData, true);
						}

						restart();

					}
					if (state.equals("analyzeRelationState")) {

						initTestSmells(n.getNameAsString());

						Optional<Position> position = currentMethod.getBegin();
						int lineNr = position.isPresent() ? position.get().line : -1;
						testMethod = new TestMethod(n.getNameAsString(), lineNr);

						testMethod.setHasSmell(false);
						hasSmell = analyzeRelations(n);

						if (hasSmell) {
							testMethod.setHasSmell(true);
							testSmell.setSmelly(true);
							testSmells.add(testSmell);
							smellyElementList.add(testMethod);
						}
						restart();

					}
				}
			}
		}

		private void restart() {
			testSmell = new TestSmell();
			hasSmell = false;
			currentMethod = null;
			declaredVariables = new HashSet<>();
			okVariables = new ArrayList<>();
		}

		/*
		 * If han object is created as an rvalue then in an variableDeclarator, then set
		 * it imediately to hasSmell.
		 */
		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				Optional<Node> parentNode = n.getParentNode();
				if (parentNode.isPresent()) {

					Node node = parentNode.get();
					String type = n.getType().toString();

					if (!(node instanceof VariableDeclarator) && (type.equals("File") || type.equals("Path"))) {

						hasSmell = true;
					}
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(Parameter n, Void arg) {
			if (currentMethod != null && state.equals("analyze") && resourceOptimism(n.getNameAsString())) {
				hasSmell = true;
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarator n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				String type = n.getTypeAsString();
				if (type.equals("File") || type.equals("Path")) {
					declaredVariables.add(n.getNameAsString());
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && state.equals("analyze")) {
				for (VariableDeclarator variableDeclarator : n.getVariables()) {

					String type = variableDeclarator.getTypeAsString();

					if (type.equals("File") || type.equals("Path")) {
						classVariables.add(variableDeclarator.getNameAsString());
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(AssignExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				List<NameExpr> findAll = n.findAll(NameExpr.class);

				for (NameExpr expr : findAll) {
					if (resourceOptimism(expr.toString())) {
						hasSmell = true;
					}
				}
			}
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				String callExpr = n.getNameAsString();

				String method = currentMethod.getNameAsString();

				addLocalClassCalls(n, method);

				/*
				 * Here we are calling one of the methods on the resource to check that it is
				 * available. If not, we have to check if it
				 */

				if (checkResources(callExpr) && ASTHelper.hasNodeNameExpr(n)) {

					NameExpr nameExpr = ASTHelper.getNameExpr(n);

					String okVar = nameExpr.toString();

					isExecutableOrExists(n.getArguments());

					if (declaredVariables.contains(okVar) || classVariables.contains(okVar)) {
						declaredVariables.remove(okVar);
						classVariables.remove(okVar);
						okVariables.add(okVar);
					}
				} else if (ASTHelper.hasNodeNameExpr(n)) {

					String nameExpr = ASTHelper.getNameExpr(n).toString();
					if (resourceOptimism(nameExpr)) {
						hasSmell = true;
					}
				}

				super.visit(n, arg);

				checkParameters(n.getArguments());

			}
		}

		private void isExecutableOrExists(NodeList<Expression> arguments) {

			for (Expression expr : arguments) {

				String okVar = expr.toString();

				if (!okVar.equals("options") && declaredVariables.contains(okVar) || classVariables.contains(okVar)) {
					declaredVariables.remove(okVar);
					classVariables.remove(okVar);
					okVariables.add(okVar);
				}
			}
		}

		/*
		 * If no scope is present, that means we are calling on a local method in this
		 * class.
		 */
		private void addLocalClassCalls(MethodCallExpr n, String method) {
			if (!n.getScope().isPresent()) {
				IntelMethod methodData = ASTHelper.getMethod(method, this.allMethodsData);
				if (methodData != null && !methodExceptions.contains(method)) {
					methodData.addMethod(n.getNameAsString());
				}
				return; // or call super? TODO check this out.
			}
		}

		/*
		 * Iterate the parameters of any method call, if reference to a File or Path is
		 * made before checking its existence, consider it smelly.
		 */
		private void checkParameters(NodeList<Expression> arguments) {
			for (Expression expr : arguments) {

				if (expr instanceof NameExpr && resourceOptimism(expr.toString())) {
					hasSmell = true;
				}
			}
		}

		private boolean resourceOptimism(String resource) {
			return declaredVariables.contains(resource)
					|| classVariables.contains(resource) && !okVariables.contains(resource);
		}

		private boolean checkResources(String callExpr) {

			for (String allowedCall : checkResourceCalls) {

				if (callExpr.equals(allowedCall)) {
					return true;
				}
			}

			return false;
		}

		public boolean analyzeRelations(MethodDeclaration n) {

			for (String method : allClassMethods) {
				if (method.equals(n.getNameAsString()) && !ASTHelper.checkIfSmelly(method, allMethodsData)) {
					IntelMethod intelMethod = ASTHelper.getMethod(method, allMethodsData);
					if (!intelMethod.isSmelly()) {
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
