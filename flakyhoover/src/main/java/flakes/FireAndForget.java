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
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.MetaData;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class FireAndForget extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public FireAndForget() {
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
		FireAndForget.ClassVisitor classVisitor;
		classVisitor = new FireAndForget.ClassVisitor();

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

		private List<String> externalCalls = Arrays.asList("server", "client", "soap", "call", "request", "response",
				"job", "http", "httprequest", "socket", "listener", "servlet", "fetch", "receive", "connect", "db",
				"execute");
//		Scheduler

		private double lineNrExternal = Double.POSITIVE_INFINITY;

		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass = false;

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

			boolean hasAssert = n.getBody().toString().toLowerCase().contains("assert");
			if (isTestClass && hasAssert) {

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

					testMethod.setHasFlaky(hasFlaky);
					if (hasFlaky) {
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);

					}
					testSmell = new TestSmell();
					hasFlaky = false;
					currentMethod = null;
					this.lineNrExternal = Double.POSITIVE_INFINITY;

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
//					testMethod.addMetaDataItem("VariableCond", metaData);
					break;

				}

				}
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
				} else if (n.getScope().isPresent()) {

					System.out.println("PresentScope: " + n);
					// Look for external calls on object that are followed by Thread.sleep(X).
					for (String expr : externalCalls) {

						if (n.toString().toLowerCase().contains(expr)) {
							System.out.println("KIWI: " + expr);

							this.lineNrExternal = n.getBegin().get().line;
							System.out.println("CAME HERE2: " + lineNrExternal);
						}
					}

					if (n.toString().contains("Thread.sleep")) {
						System.out.println("THREAD:SLEEP: " + n);
						int currentLine = n.getBegin().get().line;
						System.out.println("currentLine: " + currentLine);
						System.out.println("lineNrExternal: " + lineNrExternal);

						if (this.lineNrExternal < currentLine) {
							System.out.println("true right");
							this.hasFlaky = true;
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
//public void visit(ExpressionStmt n, Void arg) {
//	if (currentMethod != null) {
//		System.out.println("ExpressionStmt: " + n);
//
//		if (containerContain(externalCalls, n.toString())) {
//			System.out.println("THISLINE: " + n);
//			this.lineNrExternal = n.getBegin().get().line;
//			metaData.add(
//					new MetaData(n.getBegin().get().line, n.getClass().getSimpleName(), n.toString(), true));
//		}
//		if (containerContain(asyncWords, n.toString())) {
//			if (metaData.size() > 0)
//				metaData.get(metaData.size() - 1).setFlaky(false);
//		}
//	}
//	super.visit(n, arg);
//}

//@Override
//public void visit(SynchronizedStmt n, Void arg) {
//
//	if (currentMethod != null && state.equals("analyze")) {
//		System.out.println("SynchronizedStmt: " + n.toString());
//	}
//
//	super.visit(n, arg);
//}

//Expression e = n.getExpression();
//
//List<NameExpr> a = e.findAll(NameExpr.class);
//for (NameExpr expr : a) {
//	System.out.println("EXPR: " + expr);
//}

//System.out.println("a: " + a.size());
//System.out.println("Expression: " + e);