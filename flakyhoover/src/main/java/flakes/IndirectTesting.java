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
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.IntelMethod;
import flakyhoover.Main;
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

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private boolean isTestClass = false;

		private TestSmell testSmell = new TestSmell();
		private ArrayList<String> jClasses = Util.getAllJavaClasses();
		private List<String> exceptions = new ArrayList<String>(
				Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
						"TestRunner", "", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		private TestMethod testMethod;
		private Set<String> indirectClasses = new HashSet<String>();
		private Set<String> classVariables = new HashSet<>();
		private Set<IntelMethod> allMethodsData = new HashSet<IntelMethod>();
		private Set<String> allClassMethods = new HashSet<String>();

		private String flakinessType = "test-order-dependency";
		private String state;
		private String className;
		private String methodName;
		private int scope = 0;
		private MethodDeclaration[] scopes = new MethodDeclaration[1000];
		String parentType = null;

		private void initTestSmells(MethodDeclaration n) {

			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		public void setState(String state) {
			this.state = state;
		}

		// TODO
		// Must check class if they exist in project structure,
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
				className = Util.removeTest(n.getNameAsString());
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass) {
				parentType = n.getParentNode().get().getClass().getSimpleName();
				scope++;
				scopes[scope] = n;

				switch (state) {
				case "analyze": {
					currentMethod = n;
					ASTHelper.addIndirectParams(n, this.indirectClasses, jClasses, className); // Check if necessary
					if (parentType.equals("ClassOrInterfaceDeclaration")) {
						methodName = Util.removeTest(n.getNameAsString());
						this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
						this.allClassMethods.add(n.getNameAsString());
						initTestSmells(n);
						testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
						testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
					}

					break;
				}
				}

				super.visit(n, arg);

				switch (state) {
				case "analyze": {
					scope--;
					testMethod.setHasFlaky(hasFlaky);

					if (hasFlaky && scope == 0) {
						currentMethod = scopes[1];
						testSmells.add(testSmell);
						flakyElementList.add(testMethod);
						ASTHelper.setMethodStatusFlaky(n, allMethodsData, true);
					}

					if (parentType.equals("ClassOrInterfaceDeclaration") || scope == 0) {
						testSmell = new TestSmell();
						currentMethod = null;
						hasFlaky = false;
					}
					break;
				}

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

					if (parentType.equals("ClassOrInterfaceDeclaration")) {
						hasFlaky = false;
						testSmell = new TestSmell();
						testMethod.addDataItem("ResourceOptimismCount", "0");
					}

					break;

				}
				}
			}
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && state.equals("analyze")) {

				for (VariableDeclarator variableDeclarator : n.getVariables()) {
					String type = variableDeclarator.getTypeAsString();
					String name = variableDeclarator.getNameAsString();

					// Not sure, but check if it is referenced in that case we class it as smelly???
					if (!jClasses.contains(type) && ASTHelper.checkIfEqMethodOrClass(type, className, methodName)
							|| Main.getAllProductionFiles().contains(className)) {
						indirectClasses.add(name);
						// Since it is a class member and indirectClass object used in testmethod
						classVariables.add(name);

					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarator n, Void arg) {
			if (currentMethod == null) {
				currentMethod = scopes[scope];
			}
			if (currentMethod != null && state.equals("analyze")) {

				String type = n.getTypeAsString();
				String name = n.getNameAsString();
				type = type.replace("[]", "");
				if (!jClasses.contains(type)) {
					// TODO add checkifEq in this file
					if (ASTHelper.checkIfEqMethodOrClass(type, className, methodName)) {
						indirectClasses.add(name);

						hasFlaky = true;
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod == null) {
				currentMethod = scopes[scope];
			}

			if (currentMethod != null && state.equals("analyze")) {
				if (n.getScope().isPresent()) {

					// Go through parameters
					if (n.getArguments().size() > 0) {
						for (Expression expr : n.getArguments()) {
							if (indirectClasses.contains(expr.toString())) {
								this.hasFlaky = true;
							}
						}
					}

					// Check for exception here Integer.ParseInt
					// Check if object is among indirect classes is used
					// obj.getInstance().someMethod(); get obj (nameexpr) and check if it is in
					// indirectClasses.
					if (n.getScope().get() instanceof ClassExpr) {
						ClassExpr expr = ((ClassExpr) n.getScope().get());
						String classExpr = expr.getTypeAsString();
						if (!jClasses.contains(classExpr) && !classExpr.equals(className)
								&& !classExpr.toLowerCase().equals(methodName)
								|| Main.getAllProductionFiles().contains(className)) {
							this.hasFlaky = true;
						}
					}

					else if (n.getScope().get() instanceof NameExpr) { // && n.findAncestor(VariableDeclarator.class) ==
																		// null) {

						NameExpr nameExpr = ((NameExpr) n.getScope().get());
						// Check for static class references such as = new
						// Subcollection(NutchConfiguration.create());

						if ((indirectClasses.contains(nameExpr.toString()) && !exceptions.contains(nameExpr.toString()))
								|| (Character.isUpperCase(nameExpr.toString().charAt(0))
										&& !jClasses.contains(nameExpr.toString())
										&& !exceptions.contains(nameExpr.toString())
										&& !nameExpr.toString().toLowerCase().equals(className.toLowerCase()))) {

							this.hasFlaky = true;
						}
					}

				} else {
					// Go through parameters
					// Check for exception here Integer.
					if (n.getArguments().size() > 0) {

						for (Expression expr : n.getArguments()) {
							if (indirectClasses.contains(expr.toString()) && !exceptions.contains(expr.toString())) {
								this.hasFlaky = true;

							}
						}
					}
					String base = ASTHelper.checkIfClassMember(n);
					if (base.equals("empty") && parentType.equals("ClassOrInterfaceDeclaration")) {
						IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(),
								this.allMethodsData);
						if (!n.getNameAsString().equals(currentMethod.getNameAsString())
								&& !exceptions.contains(n.getNameAsString())) {
							methodData.addMethod(n.getNameAsString());
						}
					}
				}
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				String type = n.getTypeAsString();
				if (!jClasses.contains(type) && ASTHelper.checkIfEqMethodOrClass(type, className, methodName)
						|| Main.getAllProductionFiles().contains(className)) {
					hasFlaky = true;

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