package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.TestMethod;
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
		private boolean isTestClass;
		private String flakinessType = "test-order-dependency";
		
		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}


		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			isTestClass = Util.isValidTestClass(n);
			super.visit(n, arg);
		}

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass) {
				initTestSmells(n);
				
				System.out.println("MethodDeclaration: " + n.getNameAsString());
				currentMethod = n;
				testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
				testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
				super.visit(n, arg);

				testMethod.setHasFlaky(conditionCount > 0 | ifCount > 0 | switchCount > 0 | foreachCount > 0
						| forCount > 0 | whileCount > 0);
				testMethod.addDataItem("ConditionCount", String.valueOf(conditionCount));
				testMethod.addDataItem("IfCount", String.valueOf(ifCount));
				testMethod.addDataItem("SwitchCount", String.valueOf(switchCount));
				testMethod.addDataItem("ForeachCount", String.valueOf(foreachCount));
				testMethod.addDataItem("ForCount", String.valueOf(forCount));
				testMethod.addDataItem("WhileCount", String.valueOf(whileCount));

				// testMethod.setHasFlaky(methodVariables.size() >= 1 || hasFlaky==true);

//			System.out.println("METADATA SIZE: " + metaData.size());
//			testMethod.setHasFlaky(metaData.size() > 0);
//			testMethod.addMetaDataItem("VariableCond", metaData);

				if (testMethod.getHasFlaky()) {
//				System.out.println("adding flaky method");
					testSmells.add(testSmell);
					flakyElementList.add(testMethod);
				}

				// reset values for next method
				testSmell = new TestSmell();
				currentMethod = null;
				hasFlaky = false;
				conditionCount = 0;
				ifCount = 0;
				switchCount = 0;
				forCount = 0;
				foreachCount = 0;
				whileCount = 0;
			}

		}

		@Override
		public void visit(IfStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null) {
				ifCount++;
			}
		}

		@Override
		public void visit(SwitchStmt n, Void arg) {

			super.visit(n, arg);
			if (currentMethod != null) {
				switchCount++;
			}
		}

		@Override
		public void visit(ConditionalExpr n, Void arg) {

			super.visit(n, arg);
			if (currentMethod != null) {
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
	}

}
