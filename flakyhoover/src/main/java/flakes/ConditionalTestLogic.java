package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class ConditionalTestLogic extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public ConditionalTestLogic() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "ConditionalTestLogic";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<TestSmell>();
		ConditionalTestLogic.ClassVisitor classVisitor;
		classVisitor = new ConditionalTestLogic.ClassVisitor();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private int conditionCount, ifCount, switchCount, forCount, foreachCount, whileCount = 0;
		TestMethod testMethod;
		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass = false;
		private String state;
		private String flakinessType = "test-order-dependency";
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();
		private Set<String> allClassMethods = new HashSet<String>();

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
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

			if (isTestClass) {

				switch (state) {
				case "analyze": {
					currentMethod = n;
					initTestSmells(n);

					System.out.println("MethodDeclaration: " + n.getNameAsString());
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
					this.allClassMethods.add(n.getNameAsString());
					break;
				}
				}

				super.visit(n, arg);

				System.out.println("");

				switch (state) {
				case "analyze": {
					testMethod.setHasFlaky(conditionCount > 0 | ifCount > 0 | switchCount > 0 | foreachCount > 0
							| forCount > 0 | whileCount > 0);
					testMethod.addDataItem("ConditionCount", String.valueOf(conditionCount));
					testMethod.addDataItem("IfCount", String.valueOf(ifCount));
					testMethod.addDataItem("SwitchCount", String.valueOf(switchCount));
					testMethod.addDataItem("ForeachCount", String.valueOf(foreachCount));
					testMethod.addDataItem("ForCount", String.valueOf(forCount));
					testMethod.addDataItem("WhileCount", String.valueOf(whileCount));

					if (testMethod.getHasFlaky()) {
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);
					}

					testSmell = new TestSmell();
					currentMethod = null;
					hasFlaky = false;
					conditionCount = 0;
					ifCount = 0;
					switchCount = 0;
					forCount = 0;
					foreachCount = 0;
					whileCount = 0;
					break;
				}

				// Analyze if a() calls a smelly method b(), then a is also smelly().
				case "analyzeRelationState": {
					System.out.println("analyzeRelationState after");
					initTestSmells(n);

					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false);
					hasFlaky = analyzeRelations(n);

					if (hasFlaky) {
						testMethod.setHasFlaky(true);
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);

					}
					hasFlaky = false;
					testSmell = new TestSmell();
//				testMethod.addMetaDataItem("VariableCond", metaData);

					break;

				}

				}
			}

		}

		@Override
		public void visit(IfStmt n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				System.out.println("IfStmt");

				System.out.println();
//				System.out.println(n.hasElseBlock());
//				System.out.println(n.hasCascadingIfStmt());
//				System.out.println(n.hasElseBranch());
//				System.out.println(n.hasThenBlock());
//				System.out.println(n.getCondition());

				ifCount++;
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(BlockStmt n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {
//				System.out.println("BlockStmt: " + n.getStatements());
				Node nn = n.getParentNode().get();
				if (nn.getClass().getSimpleName().equals("IfStmt") && n.toString().contains("assert")) {

				}

				System.out.println("NN: " + nn.getClass().getSimpleName());
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

			super.visit(n, arg);
			if (currentMethod != null) {
				forCount++;
			}
		}

		@Override
		public void visit(ForEachStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null) {
				foreachCount++;
			}
		}

		@Override
		public void visit(WhileStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null) {
				whileCount++;
			}
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {
				System.out.println("MethodCallExpr: " + n.getNameAsString());
				String base = ASTHelper.checkIfClassMember(n);

				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
					if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
						System.out.println("methodData: " + n.getNameAsString());
						methodData.addMethod(n.getNameAsString());
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
