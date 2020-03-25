package smells;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

/**
 * 
 * @author Socke
 *
 */
public class ConditionalTestLogic extends AbstractSmell {
	private List<AbstractSmellElement> smellyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public ConditionalTestLogic() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "ConditionalTestLogic";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;

		ConditionalTestLogic.ClassVisitor classVisitor;
		classVisitor = new ConditionalTestLogic.ClassVisitor();

		testSmells = new ArrayList<>();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

	}

	@Override
	public List<AbstractSmellElement> getSmellyElements() {
		return smellyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private MethodDeclaration currentMethod = null;
		private boolean hasSmell = false;
		private int conditionCount, ifCount, switchCount, forCount, foreachCount, whileCount, tryStmt = 0;
		private TestMethod testMethod;
		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass = false;
		private String state;
		private String flakinessType = "test-order-dependency";
		private Set<IntelMethod> allMethodsData = new HashSet<>();
		private Set<String> allClassMethods = new HashSet<>();
		private Set<String> allContainers = new HashSet<>();

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
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

		public void setState(String state) {
			this.state = state;
		}

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {

			boolean hasAssert = n.getBody().toString().toLowerCase().contains("assert");
			if (isTestClass && hasAssert) {

				switch (state) {
				case "analyze": {
					currentMethod = n;
					initTestSmells(n);

					testMethod = new TestMethod(n.getNameAsString(), ASTHelper.getLine(currentMethod));
					testMethod.setHasSmell(false); // default value is false (i.e. no smell)
					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
					this.allClassMethods.add(n.getNameAsString());
					break;
				}
				default:
					break;
				}

				super.visit(n, arg);

				switch (state) {
				case "analyze": {

					if (testMethod != null) {

						testMethod.setHasSmell(conditionCount > 0 || ifCount > 0 || switchCount > 0 || foreachCount > 0
								|| forCount > 0 || whileCount > 0 || tryStmt > 0);

						testMethod.addDataItem("ConditionCount", String.valueOf(conditionCount));
						testMethod.addDataItem("IfCount", String.valueOf(ifCount));
						testMethod.addDataItem("SwitchCount", String.valueOf(switchCount));
						testMethod.addDataItem("ForeachCount", String.valueOf(foreachCount));
						testMethod.addDataItem("ForCount", String.valueOf(forCount));
						testMethod.addDataItem("WhileCount", String.valueOf(whileCount));

						if (testMethod.getHasSmell()) {

							testSmell.setSmelly(true);
							testSmells.add(testSmell);

							testMethod.setHasSmell(true);
							smellyElementList.add(testMethod);

							ASTHelper.setMethodStatusSmelly(n, allMethodsData, true);
						}

						testSmell = new TestSmell();
						currentMethod = null;
						hasSmell = false;
						conditionCount = 0;
						tryStmt = 0;
						ifCount = 0;
						switchCount = 0;
						forCount = 0;
						foreachCount = 0;
						whileCount = 0;
					}
					break;
				}

				// Analyze if a() calls a smelly method b(), then a is also smelly().
				case "analyzeRelationState": {
					initTestSmells(n);

					testMethod = new TestMethod(n.getNameAsString(), ASTHelper.getLine(currentMethod));
					testMethod.setHasSmell(false);
					hasSmell = analyzeRelations(n);

					if (hasSmell) {
						testMethod.setHasSmell(true);

						testSmells.add(testSmell);

						smellyElementList.add(testMethod);

					}
					hasSmell = false;
					testSmell = new TestSmell();

					break;

				}
				default:
					break;
				}
			}

		}

		@Override
		public void visit(IfStmt n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				ifCount++;
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(TryStmt n, Void arg) {

			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {

				// TODO investigate getstatements maybe?
				// n.getFinallyBlock().get().getStatements()
				String finallyBlock = n.getFinallyBlock().toString();

				if (n.getTryBlock().toString().contains("assert") && n.getFinallyBlock().toString().contains("remove")
						|| finallyBlock.contains("delete") || finallyBlock.contains("close")
						|| finallyBlock.contains("stop") || finallyBlock.contains("shutdown")
						|| finallyBlock.contains("drop") || finallyBlock.contains("clean")) {

					tryStmt++;
				}
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(SwitchStmt n, Void arg) {

			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {
				switchCount++;
			}
		}

		@Override
		public void visit(ConditionalExpr n, Void arg) {

			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {

				conditionCount++;
			}
		}

		@Override
		public void visit(ForStmt n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {

				// Found a possible harmful for-loop.
				forCount++;
				if (n.getBody().toString().contains("assert")) {
					NameExpr counter = null;
					Optional<NameExpr> findFirst = n.findFirst(NameExpr.class);
					// Get the counter inside the for-loop condition
					if (findFirst.isPresent()) {
						counter = findFirst.get();
					}

					// TODO For next commit refactor these conditions below
					// Check for arrays
					if (n.getBody().findFirst(AssignExpr.class).isPresent()
							|| n.getBody().findFirst(VariableDeclarationExpr.class).isPresent()) {

						if (n.getBody().findFirst(ArrayAccessExpr.class).isPresent()) {

							ArrayAccessExpr arrayAccessExpr = n.getBody().findFirst(ArrayAccessExpr.class).get()
									.asArrayAccessExpr();

							Expression index = arrayAccessExpr.getIndex();
							String containerName = arrayAccessExpr.getName().toString();

							if (this.allContainers.contains(containerName) && counter != null
									&& index.toString().contains(counter.toString())
									&& !n.findFirst(IfStmt.class).isPresent()) {

								forCount--;

							}
							// Check for lists, arraylist, sets and vectors
						} else if (n.getBody().findFirst(MethodCallExpr.class).isPresent()) {
							MethodCallExpr methodCallExpr = n.getBody().findFirst(MethodCallExpr.class).get()
									.asMethodCallExpr();

							if (methodCallExpr.getScope().isPresent()
									&& methodCallExpr.getScope().get() instanceof NameExpr) {

								String containerName = ((NameExpr) methodCallExpr.getScope().get()).getNameAsString();

								if (this.allContainers.contains(containerName)
										&& methodCallExpr.getNameAsString().equals("get")
										&& methodCallExpr.getArguments().size() == 1) {

									String index = methodCallExpr.getArguments().get(0).toString();

									if (counter != null && index != null && index.contains(counter.toString())
											&& !n.findFirst(IfStmt.class).isPresent()) {
										forCount--;
									}
								}
							}

						}
					}
				}

			}
			super.visit(n, arg);

		}

		// TODO Investigate the forEach loop as well? Work in smiliar ways as normal
		// forloop?
		@Override
		public void visit(ForEachStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {
				foreachCount++;
			}
		}

		// TODO Investigate the While loop as well? Work in smiliar ways as normal
		@Override
		public void visit(WhileStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {
				whileCount++;
			}
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (isTestClass && state.equals("analyze")) {
				for (VariableDeclarator varDec : n.getVariables()) {

					String name = varDec.getNameAsString();
					boolean type = varDec.getType().isArrayType();
					String classType = varDec.getTypeAsString();

					if (type || classType.toLowerCase().contains("list") || classType.toLowerCase().contains("set")
							|| classType.toLowerCase().contains("vector")) {
						allContainers.add(name);
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				for (VariableDeclarator varDec : n.getVariables()) {
					String name = varDec.getNameAsString();
					boolean type = varDec.getType().isArrayType();
					String classType = varDec.getTypeAsString();

					if (type || classType.toLowerCase().contains("list") || classType.toLowerCase().contains("set")
							|| classType.toLowerCase().contains("vector")) {

						allContainers.add(name);
					}
				}

			}
			super.visit(n, arg);

		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {
				String base = ASTHelper.checkIfClassMember(n);

				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
					if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
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
