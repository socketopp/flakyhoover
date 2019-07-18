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
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
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
		private String flakinessType = "async-wait";
		private String state;
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();
		private Set<String> allClassMethods = new HashSet<String>();

		private List<String> externalCalls = Arrays.asList("server", "client", "soap", "call", "request", "response",
				"job", "http", "httprequest", "socket", "listener", "servlet", "fetch", "receive", "connect", "db",
				"execute");

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
				String base = ASTHelper.checkIfClassMember(n);

				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
					if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
						methodData.addMethod(n.getNameAsString());
					}
				} else if (n.getScope().isPresent()) {

					// Look for external calls on object that are followed by Thread.sleep(X).
					for (String expr : externalCalls) {
						if (n.toString().toLowerCase().contains(expr)) {
							this.lineNrExternal = n.getBegin().get().line;
						}
					}

					if (n.toString().contains("Thread.sleep")) {
						int currentLine = n.getBegin().get().line;
						if (this.lineNrExternal < currentLine) {
							this.hasFlaky = true;
						}
					}
				}
			}
			super.visit(n, arg);
		}

		// If there is a thread.sleep in a while, then they're are obvisouly pooling
		// which is a bad pattern
		// Look at https://martinfowler.com/articles/nonDeterminism.html example
		@Override
		public void visit(WhileStmt n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null && state.equals("analyze")) {
				if (n.getBody().toString().contains("Thread.sleep")) {
					this.hasFlaky = true;
				}
			}
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
