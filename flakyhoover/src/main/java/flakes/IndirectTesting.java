package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class IndirectTesting extends AbstractFlaky {

	private List<AbstractFlakyElement> flakyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public IndirectTesting() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "IndirectTesting";
	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		testSmells = new ArrayList<TestSmell>();
		this.fileName = testClassName;
		this.projectName = projectName;

		IndirectTesting.ClassVisitor classVisitor;
		classVisitor = new IndirectTesting.ClassVisitor();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

		// Testing
//		System.out.println("indirectClasses: ");
//		Util.genericPrint(classVisitor.indirectClasses);
//		System.out.println("classVariables: ");
//		Util.genericPrint(classVisitor.classVariables);
//		System.out.println("");
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private boolean isTestClass = false;

		private TestSmell testSmell = new TestSmell();
		private ArrayList<String> jClasses = Util.getAllJavaClasses();
		private List<String> exceptions = new ArrayList<String>(
				Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
						"", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		private TestMethod testMethod;
		private Set<String> indirectClasses = new HashSet<String>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();
		private Set<String> allClassMethods = new HashSet<String>();

		private String flakinessType = "test-order-dependency";
		private String state;
		private String className;

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			System.out.println("ClassOrInterfaceDeclaration: " + n.getNameAsString());
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
				className = Util.removeTest(n.getNameAsString());
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("");
			System.out.println("MethodDeclaration: " + n.getNameAsString());

			if (isTestClass) {

				switch (state) {
				case "analyze": {
					currentMethod = n;

					ASTHelper.addIndirectParams(n, this.indirectClasses, jClasses, className); // Check if necessary

					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
					this.allClassMethods.add(n.getNameAsString());

					initTestSmells(n);
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
					testMethod.addDataItem("ResourceOptimismCount", "0");
//				testMethod.addMetaDataItem("VariableCond", metaData);

					break;

				}
				}
			}
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && state.equals("analyze")) {
				System.out.println("FieldDeclaration: " + n.toString());

				for (VariableDeclarator variableDeclarator : n.getVariables()) {
					String type = variableDeclarator.getTypeAsString();
					String name = variableDeclarator.getNameAsString();

					// Not sure, but check if it is referenced in that case we class it as smelly???
					if (!jClasses.contains(type) && !type.equals(className)) {
						indirectClasses.add(name);
						// Since it is a class member and indirectClass object used in testmethod
						classVariables.add(name);

					}
				}
			}
			super.visit(n, arg);
		}

//		VariableDeclarationExpr
		@Override
		public void visit(VariableDeclarator n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {
				System.out.println("VariableDeclarator: " + n.toString());

				String type = n.getTypeAsString();
				String name = n.getNameAsString();

				if (!jClasses.contains(type) && !type.equals(className)) {
					indirectClasses.add(name);
					hasFlaky = true; // ?
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				System.out.println("MethodCallExpr: " + n.toString());
				if (n.getScope().isPresent()) {
					System.out.println("getScope: " + n.toString());

					// Go through parameters
					if (n.getArguments().size() > 0) {
						for (Expression expr : n.getArguments()) {
							System.out.println("expr1: " + expr);
							if (indirectClasses.contains(expr.toString())) {
								this.hasFlaky = true;
							}
						}
					}

					// Check for exception here Integer.ParseInt

					// Check if object is among indirect classes is used
					// obj.getInstance().someMethod(); get obj (nameexpr) and check if it is in
					// indirectClasses.
					if (n.getScope().get() instanceof NameExpr) {
						NameExpr nameExpr = ((NameExpr) n.getScope().get());
						System.out.println("nameExpr: " + nameExpr);
						// Check for static class references such as = new
						// Subcollection(NutchConfiguration.create());

						if ((indirectClasses.contains(nameExpr.toString()) && !exceptions.contains(nameExpr.toString()))
								|| (Character.isUpperCase(nameExpr.toString().charAt(0))
										&& !exceptions.contains(nameExpr.toString()))) {
							this.hasFlaky = true;
						}
					}

				} else {
					// Go through parameters
					// Check for exception here Integer.
					System.out.println("else methocallexpr");
					if (n.getArguments().size() > 0) {
						for (Expression expr : n.getArguments()) {
							if (indirectClasses.contains(expr.toString()) && !exceptions.contains(expr.toString())) {
								this.hasFlaky = true;
							}
						}
					}

					System.out.println("FOUND  " + n.getNameAsString());

					String base = ASTHelper.checkIfClassMember(n);

					if (base.equals("empty")) {
						System.out.println("BASE: " + base);

						IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(),
								this.allMethodsData);

						if (!n.getNameAsString().equals(currentMethod.getNameAsString())
								&& !exceptions.contains(n.getNameAsString())) {

							System.out.println("methodData: " + n.getNameAsString());
							methodData.addMethod(n.getNameAsString());
						}
					}
				}
			}
			super.visit(n, arg);

		}

//		@Override
//		public void visit(ObjectCreationExpr n, Void arg) {
//// Fix this
//			if (currentMethod != null && state.equals("analyze")) {
//				System.out.println("ObjectCreationExpr: " + n);
//				if (n.getParentNode().isPresent()) {
//					System.out.println("ObjectCreationExpr isPresent: " + n);
//
//					if (!(n.getParentNode().get() instanceof VariableDeclarator)) {
//						System.out.println("hn: " + n);
//						if (n.getType().asString().equals("File") || n.getType().asString().equals("Path")) {
//							hasFlaky = true;
//						}
//					}
//				}
//			}

//		}

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
//public void visit(FieldAccessExpr n, Void arg) {
//	if (currentMethod != null && state.equals("analyze")) {
//
//		String type = n.getScope().get
//		String name = n.getNameAsString();
//		System.out.println("name: " + name);
//		System.out.println("type : " + type);
//	}
//}

//Maybe not necessary?
