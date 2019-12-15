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

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class FireAndForget extends AbstractSmell {
	private List<AbstractSmellElement> smellyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public FireAndForget() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "FireAndForget";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<>();
		FireAndForget.ClassVisitor classVisitor;
		classVisitor = new FireAndForget.ClassVisitor();

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
		TestMethod testMethod;
		private String flakinessType = "async-wait";
		private String state;
		private Set<IntelMethod> allMethodsData = new HashSet<>();
		private Set<String> allClassMethods = new HashSet<>();
		@SuppressWarnings("unused")
		private ArrayList<String> jClasses = Util.getAllJavaClasses();

		@SuppressWarnings("unused")
		private List<String> externalCalls = Arrays.asList("server", "client", "soap", "call", "request", "response",
				"conn", "connection", "job", "http", "httprequest", "socket", "listener", "servlet", "fetch", "receive",
				"connect", "db", "execute");

		private double lineNrExternal = Double.POSITIVE_INFINITY;

		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass = false;

		public void setState(String state) {
			this.state = state;
		}

		@SuppressWarnings("unused")
		public boolean returnSmelly() {
			return hasSmell;
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
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);
			testSmell.setSmelly(false);
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
					testMethod.setHasSmell(false); // default value is false (i.e. no smell)
					break;
				}
				default:
					break;
				}

				super.visit(n, arg);

				switch (state) {
				case "analyze": {

					if (testMethod != null) {

//						if (this.keywordLine != Double.POSITIVE_INFINITY && !n.getBody().toString().contains("sleep")) {
//							hasSmell = true;
//						}

						testMethod.setHasSmell(hasSmell);

						if (hasSmell) {
							testSmell.setSmelly(true);
							testSmells.add(testSmell);
							smellyElementList.add(testMethod);
							ASTHelper.setMethodStatusSmelly(n, allMethodsData, true);

						}
						testSmell = new TestSmell();
						hasSmell = false;
						currentMethod = null;
						this.lineNrExternal = Double.POSITIVE_INFINITY;
					}

					break;
				}

				// Analyze if a() calls a smelly method b(), then a is also smelly().
				case "analyzeRelationState": {
					initTestSmells(n);

					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasSmell(false);
					hasSmell = analyzeRelations(n);

					if (hasSmell) {
						testMethod.setHasSmell(true);
						testSmell.setSmelly(true);
						testSmells.add(testSmell);
						smellyElementList.add(testMethod);
					}
					hasSmell = false;
					testSmell = new TestSmell();
//					testMethod.addMetaDataItem("VariableCond", metaData);
					break;

				}
				default:
					break;

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
//					System.out.println("calling: " + n.toString().contains("Thread.sleep"));
//					for (String expr : externalCalls) {
//						if (n.toString().toLowerCase().contains(expr)) {
//							this.lineNrExternal = n.getBegin().get().line;
//						}
//					}

					// Look for any call expression that is not a sleep before the sleep.
					// TODO bara sleep och inte thread.sleep

//				    private void assertSimpleElasticJobBean() {
//				        while (!FooSimpleElasticJob.isCompleted()) {
//				            sleep(100L);
//				        }
//				        assertTrue(FooSimpleElasticJob.isCompleted());
//				        assertTrue(regCenter.isExisted("/" + simpleJobName + "/sharding"));
//				    }
//				    
//				    private void assertThroughputDataflowElasticJobBean() {
//				        while (!DataflowElasticJob.isCompleted()) {
//				            sleep(100L);
//				        }
//				        assertTrue(DataflowElasticJob.isCompleted());
//				        assertTrue(regCenter.isExisted("/" + throughputDataflowJobName + "/sharding"));
//				    }

//					TODO
//					Externa anrop men ingen sleep??

//					if (this.externalCalls.contains(n.toString())) {
//						keywordLine = n.getBegin().get().line;
//					}

					if (!n.toString().contains("Thread.sleep")) {
						this.lineNrExternal = n.getBegin().get().line;
					}

					if (n.toString().contains("Thread.sleep")) {
						int currentLine = n.getBegin().get().line;
						if (this.lineNrExternal < currentLine) {
							this.hasSmell = true;
						}
					}
//					else if (n.toString().contains("await")) {
//						int currentLine = n.getBegin().get().line;
//						if (this.lineNrExternal < currentLine) {
//							this.hasSmell = true;
//						}
//					}
				}
			}
			super.visit(n, arg);
		}

		// If there is a thread.sleep in a while, then they're are obvisouly pooling
		// which is a bad pattern if they don't have a timeout.
		// Look at https://martinfowler.com/articles/nonDeterminism.html example
//		@Override
//		public void visit(WhileStmt n, Void arg) {
//			super.visit(n, arg);
//			if (currentMethod != null && state.equals("analyze")) {
//				if (n.getBody().toString().contains("Thread.sleep")) {
//					this.hasSmell = true;
//				}
//			}
//		}

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
