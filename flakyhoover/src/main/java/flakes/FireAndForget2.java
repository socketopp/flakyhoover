package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.MetaData;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class FireAndForget2 extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public FireAndForget2() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "FireAndForget";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<TestSmell>();
		FireAndForget2.ClassVisitor classVisitor;
		classVisitor = new FireAndForget2.ClassVisitor();

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
		TestMethod testMethod;
		private List<MetaData> metaData = new ArrayList<MetaData>();
		private String flakinessType = "async-wait";

		private List<String> asyncWords = Arrays.asList("wait", "await", "async", "asynchronous", "sleep", "receive",
				"Thread.sleep", "waiting", "timeout");
		private String state;
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();
		private Set<String> allClassMethods = new HashSet<String>();

		private List<String> externalCalls = Arrays.asList("server", "client", "context", "database", "soap", "call",
				"job", "request", "con", "executeQuery", "stmt", "getResource", "manager", "factory", "response",
				"cursor", "sql", "read", "http", "socket", "listener", "servlet", "fetch", "receive", "connect",
				"obtain", "db", "aquire", "create", "execute", "getInstance", "load");

		// Create?Nja
//		FileOutputStream
//		getInstance?? getResource??? getInstance

		private int lineNrSync = -1;
		private int lineNrExternal = -1;

		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass = false;

		private boolean containerContain(List<String> container, String keyword) {
			for (int i = 0; i < container.size(); i++) {
				if (keyword.contains(container.get(i))) {
					return true;
				}
			}
			return false;
		}

		public void setState(String state) {
			this.state = state;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {

			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
			}

			super.visit(n, arg);

		}

//		@Override
//		public void visit(ImportDeclaration n, Void arg) {
//			System.out.println("import: " + n);
//			super.visit(n, arg);
//		}

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {
//			System.out.println("MethodDeclaration: " + n.getNameAsString());

			if (isTestClass) {

				switch (state) {
				case "analyze": {
					currentMethod = n;
					initTestSmells(n);

					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
					this.allClassMethods.add(n.getNameAsString());

					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
					break;
				}
				}

				super.visit(n, arg);

				System.out.println("");

				switch (state) {
				case "analyze": {

					if (this.lineNrSync == -1 && this.lineNrExternal != -1) {
						this.hasFlaky = true;
					} else if (this.lineNrExternal > this.lineNrSync) {
						this.hasFlaky = true;
					} else {
						this.hasFlaky = false;
					}

					testMethod.setHasFlaky(hasFlaky);
					if (hasFlaky) {
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);

					}
					testSmell = new TestSmell();
					hasFlaky = false;
					currentMethod = null;
					this.lineNrExternal = -1;
					this.lineNrSync = -1;

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
		public void visit(ExpressionStmt n, Void arg) {
			if (currentMethod != null) {
				System.out.println("ExpressionStmt: " + n);
				Expression e = n.getExpression();
				List<NameExpr> a = e.findAll(NameExpr.class);

				for (NameExpr expr : a) {
					System.out.println("EXPR: " + expr);
				}
				System.out.println("a: " + a.size());
				System.out.println("Expression: " + e);

				if (containerContain(externalCalls, n.toString())) {
					this.lineNrExternal = n.getBegin().get().line;
					metaData.add(
							new MetaData(n.getBegin().get().line, n.getClass().getSimpleName(), n.toString(), true));
				}
				if (containerContain(asyncWords, n.toString())) {
					this.lineNrSync = n.getBegin().get().line;
					if (metaData.size() > 0)
						metaData.get(metaData.size() - 1).setFlaky(false);
				}
			}
			super.visit(n, arg);
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
