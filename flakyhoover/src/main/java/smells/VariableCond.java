package smells;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.MetaData;
import flakyhoover.TestMethod;
import util.TestSmell;

public class VariableCond extends AbstractSmell {
	private List<AbstractSmellElement> smellyElementList;
	protected String fileName;
	protected String projectName;
	protected String methodName;
	protected ArrayList<TestSmell> testSmells;

	public VariableCond() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "VariableCond";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {

		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<>();
		VariableCond.ClassVisitor classVisitor;
		classVisitor = new VariableCond.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);
	}

	@Override
	public List<AbstractSmellElement> getSmellyElements() {
		return smellyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private boolean hasSmell = false;
		TestMethod testMethod;
		private List<String> missingCondVars = new ArrayList<>();
		private List<MetaData> metaData = new ArrayList<>();
		private TestSmell testSmell = new TestSmell();

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			currentMethod = n;
			testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
			testMethod.setHasSmell(false); // default value is false (i.e. no smell)

			testSmell.setFlakinessType("concurrency");
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);

			super.visit(n, arg);

			testMethod.setHasSmell(metaData.size() > 0 && hasSmell);

			if (metaData.size() > 0 && hasSmell) {
				testSmells.add(testSmell);
			}

			if (metaData.size() >= 1) {
				metaData.get(0).setExtra(missingCondVars);
			}

			testMethod.addMetaDataItem("VariableCond", metaData);

			if (testMethod.getHasSmell()) {
				smellyElementList.add(testMethod);
			}

			// reset values for next method
			currentMethod = null;
			hasSmell = false;
		}

		private Set<String> getAllVariables(String conditionExpression) {
			Set<String> allMatches = new HashSet<>();
			Matcher m = Pattern.compile("[a-zA-Z$_][a-zA-Z0-9$_]*").matcher(conditionExpression);
			while (m.find()) {
				allMatches.add(m.group());
			}
			return allMatches;
		}

		@Override
		public void visit(WhileStmt n, Void arg) {
			if (currentMethod != null) {
				String conditionExpr = n.getCondition().getChildNodes().toString();
				Set<String> allVariables = getAllVariables(conditionExpr);
				for (String var : allVariables) {
					String newVar = var + " =";
					if (n.getBody().toString().contains(newVar)) {
						continue;
					} else {
						missingCondVars.add(var);
						hasSmell = true;
					}
				}

				if (hasSmell && n.getBody().toString().contains("break")) {
					hasSmell = false;
				} else {
					metaData.add(
							new MetaData(n.getBegin().get().line, n.getClass().getSimpleName(), n.toString(), true));
				}
			}
			super.visit(n, arg);
		}
	}

}
