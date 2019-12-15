package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractSmell;
import flakyhoover.AbstractSmellElement;
import flakyhoover.IntelMethod;
import flakyhoover.TestMethod;
import util.ASTHelper;
import util.TestSmell;
import util.Util;

public class ResourceOptimism extends AbstractSmell {

	private List<AbstractSmellElement> smellyElementList;
	private String fileName;
	private String projectName;
	private ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public ResourceOptimism() {
		smellyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public String getSmellName() {
		return "ResourceOptimism";
	}

	@Override
	public List<AbstractSmellElement> getSmellyElements() {
		return smellyElementList;
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		this.fileName = testClassName;
		this.projectName = projectName;
		testSmells = new ArrayList<>();
		ResourceOptimism.ClassVisitor classVisitor;
		classVisitor = new ResourceOptimism.ClassVisitor();

		classVisitor.setState("analyze");
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setState("analyzeRelationState");
		classVisitor.visit(testFileCompilationUnit, null);

	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private boolean hasSmell = false;
		private boolean isTestClass = false;

		private TestMethod testMethod;
		private MethodDeclaration currentMethod = null;
		private TestSmell testSmell = new TestSmell();

		private Set<String> methodVariables = new HashSet<>();
		private Set<String> classVariables = new HashSet<>();
		private List<String> okVariables = new ArrayList<>();
		private Set<IntelMethod> allMethodsData = new HashSet<>();
		private Set<String> allClassMethods = new HashSet<>();
		private Map<String, Set<String>> varDeclExpr = new HashMap<>();

		private String flakinessType = "input-output";
		private String state;

		public void setState(String state) {
			this.state = state;
		}

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getSmellName());
			testSmell.setTestClass(fileName);
			testSmell.setSmelly(false);

		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			if (!isTestClass) {
				isTestClass = Util.isValidTestClass(n);
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			if (isTestClass) {

				switch (state) {
				case "analyze": {
					if (!varDeclExpr.containsKey(n.getNameAsString())) {
						varDeclExpr.put(n.getNameAsString(), new HashSet<String>());
					}

					currentMethod = n;
					this.allMethodsData.add(new IntelMethod(n.getNameAsString(), false));
					this.allClassMethods.add(n.getNameAsString());

					ASTHelper.addParams(n, methodVariables); // Check if necessary
					initTestSmells(n);
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
						methodVariables = new HashSet<>();
						okVariables = new ArrayList<>();
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
					testMethod.addDataItem("ResourceOptimismCount", "0");
					break;

				}
				}
			}
		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {

				Set<String> listOfDeclaredVars = this.varDeclExpr.get(currentMethod.getNameAsString());

				// Check if path/file is used as an argument in the rvalue object.
				if (n.getArguments().size() > 0) {
					for (Expression expr : n.getArguments()) {
						if (expr.isNameExpr() || expr.isFieldAccessExpr()) {
							String argument = expr.toString();
							if ((classVariables.contains(argument) && !okVariables.contains(argument)
									&& !listOfDeclaredVars.contains(argument))
									|| ((methodVariables.contains(argument) && !okVariables.contains(argument)
											&& !listOfDeclaredVars.contains(argument)))) {
								hasSmell = true;
							}
						}
					}
				}

				// If han object is created as an rvalue then in an variableDeclarator, then set
				// it imediately to hasSmell.
				if (n.getParentNode().isPresent()) {

					if (!(n.getParentNode().get() instanceof VariableDeclarator)) {
						if (n.getType().asString().equals("File") || n.getType().asString().equals("Path")) {
							hasSmell = true;
						}
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarator n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				if (n.getTypeAsString().equals("File") || n.getTypeAsString().equals("Path")) {
					methodVariables.add(n.getNameAsString());
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && state.equals("analyze")) {
				for (VariableDeclarator variableDeclarator : n.getVariables()) {
					if (variableDeclarator.getTypeAsString().equals("File")
							|| variableDeclarator.getTypeAsString().equals("Path")) {
						classVariables.add(variableDeclarator.getNameAsString());
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {

			if (currentMethod != null && state.equals("analyze")) {
				String key = currentMethod.getNameAsString();
				for (VariableDeclarator variableDeclarator : n.getVariables()) {
					if (variableDeclarator.getTypeAsString().equals("File")
							|| variableDeclarator.getTypeAsString().equals("Path")) {

						methodVariables.add(variableDeclarator.getNameAsString());
					} else {
						Set<String> varDeclExprArray = varDeclExpr.get(key);
						ASTHelper.addToVariabelsDeclarations(n, varDeclExprArray);
					}
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			if (currentMethod != null && state.equals("analyze")) {
				String callExpr = n.getNameAsString();

				String base = ASTHelper.checkIfClassMember(n);
				if (base.equals("empty")) {
					IntelMethod methodData = ASTHelper.getMethod(currentMethod.getNameAsString(), this.allMethodsData);
					if (!n.getNameAsString().equals(currentMethod.getNameAsString())) {
						methodData.addMethod(n.getNameAsString());
					}
				}

				if (callExpr.equals("exists") || callExpr.equals("isFile") || callExpr.equals("canExecute")
						|| callExpr.equals("canRead") || callExpr.equals("canWrite") || callExpr.equals("notExists")
						|| callExpr.equals("isWritable") || callExpr.equals("isReadable")
						|| callExpr.equals("isExecutable")) {

					// Here we add referenced variables to okVariables so if they are used later
					// they will not indicate resource optimism if used.

					if (n.getScope().isPresent()) {
						if (n.getScope().get() instanceof NameExpr) {
							String okVar = ((NameExpr) n.getScope().get()).getNameAsString();

							// Check for variable f in f.exists()
							if (methodVariables.contains(okVar)) {
								this.methodVariables.remove(okVar);
								okVariables.add(okVar);
							} else if (classVariables.contains(okVar)) {
								okVariables.add(okVar);
								this.classVariables.remove(okVar); // Remove here as well?....

								// Check for Path p in Files.isExecutable(p)
							} else {
								if (n.getArguments().size() > 0) {
									// Maybe should do line below like this with n.getScope().get() instanceof
									// NameExpr style instead of indexing arguments.
									for (Expression expr : n.getArguments()) {
										String path = expr.toString();
										if (methodVariables.contains(path) || classVariables.contains(path)) {
											okVariables.add(path);
											methodVariables.remove(path);
											classVariables.remove(path);
										}
									}

								}
							}
						}
					}
				}

				// other method call that may be using optimistic resources

				else if (n.getScope().isPresent()) {
					// Check inside methods called on objects object.getInstance();

					// Maybe use conventional method to get the arguments?
					// 1. use nameexpr or something similar
					// 2. always just check n.getArguments() ???
//					//	    long fsize = fs.getFileStatus(path).getLen(); dont capture path.

					String referencedVariable = null;
					if (n.getChildNodes().size() > 1) {

						// a.instance.mm() not working with this method. Need to use, get scoep,
						// instance of namexpr

						// Borde kunna göra det här på ett konventioenllt sätt ist
						referencedVariable = n.getChildNodes().get(0).toString();

					}
					ArrayList<String> args = new ArrayList<>();

					// Goes here if for instance we call object.method(params).
					// Then we need to check if object is file/path or any of the params are
					// file/path

					for (Expression argument : n.getArguments()) {
						args.add(argument.toString());
					}
					if (n.getScope().get() instanceof MethodCallExpr) {
						MethodCallExpr method = ((MethodCallExpr) n.getScope().get());
						for (Expression argument : method.getArguments()) {
							args.add(argument.toString());
						}
					}

					Set<String> listOfDeclaredVars = this.varDeclExpr.get(currentMethod.getNameAsString());

					// Check if path/file is used as an argument
					for (String argument : args) {
						if ((classVariables.contains(argument) && !okVariables.contains(argument)
								&& !listOfDeclaredVars.contains(argument))
								|| ((methodVariables.contains(argument) && !okVariables.contains(argument)
										&& !listOfDeclaredVars.contains(argument)))) {
							hasSmell = true;
						}
					}

					// Check if the file/path is called such as file.delete();
					if (referencedVariable != null) {
						if ((classVariables.contains(referencedVariable) && !okVariables.contains(referencedVariable)
								&& !listOfDeclaredVars.contains(referencedVariable))
								|| (methodVariables.contains(referencedVariable)
										&& !okVariables.contains(referencedVariable)
										&& !listOfDeclaredVars.contains(referencedVariable))) {
							hasSmell = true;
						}
					}
				} else {
					// check for parameters inside a normal method such as:
					// timeWrite(path, appendable, true, options.keyLength...)

					Set<String> listOfDeclaredVars = this.varDeclExpr.get(currentMethod.getNameAsString());
					if (n instanceof MethodCallExpr) {
						for (Expression expr : n.getArguments()) {
//							if (expr.isNameExpr() || expr.isFieldAccessExpr()) {}
							String argument = expr.toString();
							if (!Util.checkIntBool(argument)) {
								if ((classVariables.contains(argument) && !okVariables.contains(argument)
										&& !listOfDeclaredVars.contains(argument))
										|| ((methodVariables.contains(argument) && !okVariables.contains(argument)
												&& !listOfDeclaredVars.contains(argument)))) {
									hasSmell = true;
								}
							}
						}
					}
				}
			}
			super.visit(n, arg);
		}

		public boolean analyzeRelations(MethodDeclaration n) {

			for (String method : allClassMethods) {
				if (method.equals(n.getNameAsString())) {
					if (!ASTHelper.checkIfSmelly(method, allMethodsData)) {
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
			}
			return false;
		}
	}
}
