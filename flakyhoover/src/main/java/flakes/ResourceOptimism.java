package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.TestMethod;
import util.TestSmell;
import util.Util;

public class ResourceOptimism extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

//	DONE
	// Added orders and class referenced variables.
	
//	TO-DO
	// How to evaluate
	
	
//	https://github.com/apache/hbase/blob/0.94/src/test/java/org/apache/hadoop/hbase/io/hfile/TestHFilePerformance.java
//	Valid class but not methods hmmmm.
	
	

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public ResourceOptimism() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "Resource Optimism";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {

		this.fileName = testClassName;
		this.projectName = projectName;

		testSmells = new ArrayList<TestSmell>();

		ResourceOptimism.ClassVisitor classVisitor;
		classVisitor = new ResourceOptimism.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);
	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private int resourceOptimismCount = 0;
		private boolean hasFlaky = false;
		TestMethod testMethod;
		private List<String> methodVariables = new ArrayList<>();
		private List<String> classVariables = new ArrayList<>();
		private List<String> okVariables = new ArrayList<>();
		private TestSmell testSmell = new TestSmell();
		private boolean validTestMethod;
		private List<String> validCalls = new ArrayList<String>(
				Arrays.asList("exists", "isFile", "canExecute", "canRead", "notExists", "canWrite"));
		private boolean isTestClass;

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {
//			System.out.println("MethodDeclaration: " + n.getNameAsString());

			if (isTestClass) {
				currentMethod = n;

				validTestMethod = Util.isValidTestMethod(n);

				System.out.println("validTestMethod: " + validTestMethod);

				ArrayList<Parameter> params = new ArrayList<Parameter>();
				System.out.println("YE buoy");
				if (currentMethod != null && validTestMethod && isTestClass) {

					if (n.getParameters().size() > 0) {
						for (Parameter argument : n.getParameters()) {
							params.add(argument);
						}
					}
					for (Parameter param : params) {
						if (param.getTypeAsString().equals("File") || param.getTypeAsString().equals("Path")) {
							methodVariables.add(param.getNameAsString());
							System.out.println("ADD TO METHODVARS " + param.getNameAsString() + " " + methodVariables);
						}
					}
				}

				testSmell.setFlakinessType("input-output");
				testSmell.setProject(projectName);
				testSmell.setTestMethod(n.getNameAsString());
				testSmell.setSmellType(getFlakyName());
				testSmell.setTestClass(fileName);

				testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
				testMethod.setHasFlaky(false); // default value is false (i.e. no smell)

				super.visit(n, arg);

				if (methodVariables.size() >= 1 || hasFlaky == true) {
					testSmells.add(testSmell);
				}
				System.out.println("methodVariables: " + this.methodVariables);
				System.out.println("hasFlaky: " + hasFlaky);

				testMethod.setHasFlaky(methodVariables.size() >= 1 || hasFlaky == true);

				testMethod.addDataItem("ResourceOptimismCount", String.valueOf(resourceOptimismCount));

				if (testMethod.getHasFlaky()) {
					flakyElementList.add(testMethod);
				}

				// reset values for next method
				currentMethod = null;
				resourceOptimismCount = 0;
				hasFlaky = false;
				methodVariables = new ArrayList<>();
				okVariables = new ArrayList<>();
				testSmell = new TestSmell();
				validTestMethod = false;
				params = new ArrayList<Parameter>();

			}

		}

//		@Override
//		public void visit(VariableDeclarationExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod && isTestClass) {
//				for (VariableDeclarator variableDeclarator : n.getVariables()) {
//					if (variableDeclarator.getTypeAsString().equals("File")
//							|| variableDeclarator.getTypeAsString().equals("Path")) {
//						System.out.println("SET FLAKY EH? LOL12");
//
//						methodVariables.add(variableDeclarator.getNameAsString());
//					}
//				}
//			}
//			super.visit(n, arg);
//		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && validTestMethod && isTestClass) {
				if (n.getParentNode().isPresent()) {
					if (!(n.getParentNode().get() instanceof VariableDeclarator)) { // VariableDeclarator is handled in
																					// the override method
						if (n.getType().asString().equals("File") || n.getType().asString().equals("Path")) {

							hasFlaky = true;
						}
					}
				}
			} else {
//				System.out.println(n.getType());
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {

			isTestClass = Util.isValidTestClass(n);

			super.visit(n, arg);

		}

		
//		is called after methodcallexpr... 3last for some fucking reason?? I need it but...
//		@Override
//		public void visit(Parameter n, Void arg) {
//			System.out.println("PARAM");
//			System.out.println(n.getParentNode().get().getClass());
//			
//			if (currentMethod != null && validTestMethod && isTestClass) {
////				System.out.println("PARAM n " + n.toString());
//				if (n.getType().asString().equals("File") || n.getType().asString().equals("Path")) {
//					methodVariables.add(n.getNameAsString());
//
//				}
//			}
//			super.visit(n, arg);
//
//		}

		@Override
		public void visit(VariableDeclarator n, Void arg) {
			if (currentMethod != null && validTestMethod && isTestClass) {
				if (n.getTypeAsString().equals("File") || n.getTypeAsString().equals("Path")) {

					methodVariables.add(n.getNameAsString());
				}
			}
//			else {
//				if (n.getTypeAsString().equals("File") || n.getTypeAsString().equals("Path")) {
//					System.out.println("VariableDeclarator: " + n.getTypeAsString());
//
//					System.out.println("ADDS from class1");
//					classVariables.add(n.getNameAsString());
//				}
//			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (isTestClass) {
				System.out.println("FieldDeclaration: " + n);
				for (VariableDeclarator variableDeclarator : n.getVariables()) {
					if (variableDeclarator.getTypeAsString().equals("File")
							|| variableDeclarator.getTypeAsString().equals("Path")) {
						System.out.println("Adds from class2");
						classVariables.add(variableDeclarator.getNameAsString());
					}
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			System.out.println("MethodCallExpr: " + n);
			System.out.println("getNameAsString: " + n.getNameAsString());
			System.out.println("getArguments: " + n.getArguments());
			System.out.println("getChildNodes: " + n.getChildNodes());
			System.out.println("methodVariables: " + methodVariables);
			System.out.println("classVars: " + classVariables);
			System.out.println("okVars: " + this.okVariables);
			System.out.println(" ");
			if (currentMethod != null && validTestMethod && isTestClass) {

				if (n.getNameAsString().equals("exists") || n.getNameAsString().equals("isFile")
						|| n.getNameAsString().equals("canExecute") || n.getNameAsString().equals("canRead")
						|| n.getNameAsString().equals("canWrite") || n.getNameAsString().equals("notExists")
						|| n.getNameAsString().equals("isWritable") || n.getNameAsString().equals("isReadable")
						|| n.getNameAsString().equals("isExecutable")) {
					if (n.getScope().isPresent()) {

						String currentVar = n.getScope().get().toString();
						System.out.println("SCOPE: " + n.getScope().get().toString());
						System.out.println("instanceOf: " + n.getScope().get().toString());

						if (n.getScope().get() instanceof NameExpr) {
							System.out.println("SCOPEPRESENT: " + methodVariables);

							System.out.println("interesting" + ((NameExpr) n.getScope().get()).getNameAsString());

							String okVar = ((NameExpr) n.getScope().get()).getNameAsString();

							if (methodVariables.contains(((NameExpr) n.getScope().get()).getNameAsString())) {
								System.out.println("REMOVE1: " + methodVariables);
								System.out.println("okVar: " + okVar);
								System.out.println("namexpr: " + ((NameExpr) n.getScope().get()).getNameAsString());

								this.methodVariables.remove(((NameExpr) n.getScope().get()).getNameAsString());
								okVariables.add(okVar);

								System.out.println("methodVariables: " + methodVariables);

							} else if (classVariables.contains(((NameExpr) n.getScope().get()).getNameAsString())) {
								System.out.println("YOLO");
								okVariables.add(okVar);
							} else {
								System.out.println("it's an else: " + n.getNameAsString());

								if (n.getArguments().size() > 0) {
									String path = n.getArguments().get(0).toString();
									if (methodVariables.contains(path) || classVariables.contains(path)) {
										System.out.println("ITs ok mario: " + path);
										okVariables.add(path);
										methodVariables.remove(path);
									}

								}

							}
						}

					}
				} else if (n.getChildNodes().size() > 1) {

					System.out.println("water");
					String referencedVariable = n.getChildNodes().get(0).toString();
					String callerFunction = n.getChildNodes().get(1).toString();
					ArrayList<String> args = new ArrayList<String>();
					if (n.getArguments().size() > 0) {
						for (Expression argument : n.getArguments()) {
							args.add(argument.toString());
						}
					}

					for (String argument : args) {
						if (classVariables.contains(argument) && !validCalls.contains(argument)
								&& !okVariables.contains(argument)) {
							System.out.println("GOESHERE");

							hasFlaky = true;
						} else if (methodVariables.contains(argument) && !okVariables.contains(argument)) {

							hasFlaky = true;
						}
					}

					if (classVariables.contains(referencedVariable) && !validCalls.contains(callerFunction)
							&& !okVariables.contains(referencedVariable)) {

						hasFlaky = true;
					} else if (methodVariables.contains(referencedVariable)
							&& !okVariables.contains(referencedVariable)) {

						hasFlaky = true;
//						methodVariables.add(referencedVariable);
					}
				}

//				else if (n.getNameAsString().equals("isWritable") || n.getNameAsString().equals("isReadable")
//						|| n.getNameAsString().equals("isExecutable") || n.getNameAsString().equals("exists")) {
//					if (n.getScope().isPresent()) {
//						if (n.getScope().get() instanceof NameExpr) {
//							System.out.println("SCOPE1: " + n.getScope().get().toString());
//
//						}
//
//					}
//
//				}
			}
			super.visit(n, arg);

		}
	}
}

//@Override
//public void visit(ClassOrInterfaceDeclaration n, Void arg) {
//
//	testSmell.setTestClass(n.getNameAsString());
//	super.visit(n, arg);
//}
