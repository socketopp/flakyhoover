package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.MetaData;
import flakyhoover.MethodData;
import flakyhoover.TestMethod;
import flakyhoover.VariableData;
import util.TestSmell;
import util.Util;

public class TestRunWarNew extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected String fileName;
	protected String projectName;
	protected ArrayList<TestSmell> testSmells;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

//	TODO FIX NO STATIC CALLS

	public TestRunWarNew() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "TestRunWar";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		TestRunWarNew.ClassVisitor classVisitor;
		classVisitor = new TestRunWarNew.ClassVisitor();
		this.fileName = testClassName;
		this.projectName = projectName;

		testSmells = new ArrayList<TestSmell>();

		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.getAllMethod = false;
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.setRun(false);
		classVisitor.visit(testFileCompilationUnit, null);

		classVisitor.checkSmellyMethods = true;
		classVisitor.visit(testFileCompilationUnit, null);

	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private List<String> exceptions = new ArrayList<String>(Arrays.asList("Boolean", "Byte", "Short", "Character",
				"toString", "Integer", "Long", "Float", "Double", "Lists", "LOG", "log", "", "Collections", "Math",
				"assert", "assertEquals", "assertTrue", "assertFalse", "assertNotNull", "assertNull", "assertSame",
				"assertNotSame", "assertArrayEquals", "fail"));
		private List<String> operators = new ArrayList<String>(Arrays.asList("POSTFIX_INCREMENT", "POSTFIX_DECREMENT",
				"MINUS", "PLUS", "LOGICAL_COMPLEMENT", "PREFIX_INCREMENT", "PREFIX_DECREMENT", "LOGICAL_COMPLEMENT"));
		TestMethod testMethod;
		private List<String> methodVariables = new ArrayList<>();
		private List<String> classVariables = new ArrayList<>();
		private boolean firstRun = true;
		private List<MetaData> metaData = new ArrayList<MetaData>();
		private boolean isTestClass;
		private TestSmell testSmell = new TestSmell();

		private Map<String, ArrayList<VariableData>> methodVars = new HashMap<String, ArrayList<VariableData>>();
		private Map<String, ArrayList<MethodData>> methodCalls = new HashMap<String, ArrayList<MethodData>>();

		private ArrayList<String> allClassMethods = new ArrayList<String>();
		private ArrayList<Method> methods_status = new ArrayList<Method>();
		private boolean checkSmellyMethods = false;
		private boolean getAllMethod = true;

		public class Method {
			public String name;
			public boolean isFlaky;

			public Method(String name, boolean isFlaky) {
				this.name = name;
				this.isFlaky = isFlaky;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public boolean isFlaky() {
				return isFlaky;
			}

			public void setFlaky(boolean isFlaky) {
				this.isFlaky = isFlaky;
			}

		};

		public void setMethodStatusFlaky(MethodDeclaration currentMethod) {
			for (Method m : methods_status) {
				if (m.getName().equals(currentMethod.getNameAsString())) {
					m.setFlaky(true);
					break;
				}
			}
		}

		public boolean checkIfFlaky(String currentMethod) {
			boolean flaky = false;
			for (Method m : methods_status) {
				if (m.getName().equals(currentMethod)) {
					flaky = m.isFlaky();
					break;
				}
			}
			return flaky;
		}

		private void setRun(boolean val) {
			this.firstRun = val;
		}

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType("concurrency");
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
				currentMethod = n;

				if (currentMethod != null && getAllMethod) {

					this.allClassMethods.add(n.getNameAsString());
				} else {

					initTestSmells(n);

//					System.out.println("MethodDeclaration: " + n.getNameAsString());
					testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
					testMethod.setHasFlaky(false); // default value is false (i.e. no smell)

					// List<String> newArrayList = new ArrayList<String>();

					if (!methodVars.containsKey(n.getNameAsString())) {
						methodVars.put(n.getNameAsString(), new ArrayList<VariableData>());
					}
					if (!methodCalls.containsKey(n.getNameAsString())) {
						methodCalls.put(n.getNameAsString(), new ArrayList<MethodData>());
					}
					if (!firstRun) {
						findTestRunWarNew();
//					checkForUniqueValues();
					}

					super.visit(n, arg);

					// testMethod.setHasFlaky(methodVariables.size() >= 1 || hasFlaky==true);
					if (!firstRun) {
//						System.out.println("MethodDeclaration: " + n.getNameAsString());

//						System.out.println("METADATA SIZE: " + metaData.size());
//						testMethod.setHasFlaky(metaData.size() > 0);
						testMethod.setHasFlaky(hasFlaky == true);
						testMethod.addMetaDataItem("VariableCond", metaData);

						metaData = new ArrayList<MetaData>();
					}

					if (testMethod.getHasFlaky() && !firstRun) {

//						System.out.println("adding flaky method");
						testSmells.add(testSmell);

						flakyElementList.add(testMethod);
					}

					if (this.checkSmellyMethods) {

						for (Entry<String, ArrayList<MethodData>> method : methodCalls.entrySet()) {
							if (currentMethod != null) {
								if (method.getKey().equals(currentMethod.getNameAsString())) {
									for (MethodData m : method.getValue()) {
//									

										if (m.getBaseVar().equals("empty")) {
//											System.out.println("checkSmellyMethods1");

											if (checkIfFlaky(m.getFunctionCallName())) {
												testSmells.add(testSmell);
												testMethod.setHasFlaky(true);
											}

											// Determine if one non smelly func calls a smelly func.
//											m.getFunctionCallName()
										}
									}

								}
							}

						}
					}

					// reset values for next method
					currentMethod = null;
					testSmell = new TestSmell();
					hasFlaky = false;
					methodVariables = new ArrayList<>();

					if (methodVars.get(n.getNameAsString()).isEmpty()) {
						methodVars.remove(n.getNameAsString());
					}

					if (methodCalls.get(n.getNameAsString()).isEmpty()) {
						methodCalls.remove(n.getNameAsString());
					}

				}

				// System.out.println("");
			}

		}

		public String checkIfClassMember(MethodCallExpr n) {

			Matcher m = Pattern.compile("(?<=\\[).+?(?=\\])").matcher(n.getScope().toString());
			String base = "empty";

			if (m.find()) {

				if (!exceptions.contains(m.group())) {

					base = m.group();
					if (base.contains(".")) {
						String[] arrOfbase = base.split("\\.");
						base = arrOfbase[0];
					}
				}
			}
			return base;
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			if (currentMethod != null && firstRun && !getAllMethod) {
				System.out.println("MethodCallExpr: " + n);

				String key = currentMethod.getNameAsString();

				if (methodCalls.containsKey(key)) {
					ArrayList<MethodData> arrayList = methodCalls.get(key);

					String base = checkIfClassMember(n);

//					if (!exceptions.contains(n.getNameAsString()) && !allClassMethods.contains(n.getNameAsString())) {
					if (!exceptions.contains(n.getNameAsString())) {

						methods_status.add(new Method(currentMethod.getNameAsString(), false));
//						System.out.println("HODOR1");
						arrayList.add(new MethodData(n.getBegin().get().line, n.toString(), n.getNameAsString(),
								n.getArguments(), base, key));

					} else {
						methods_status.add(new Method(currentMethod.getNameAsString(), false));
//						System.out.println("HODOR2");
						arrayList.add(new MethodData(n.getBegin().get().line, n.toString(), n.getNameAsString(),
								n.getArguments(), base, key));

					}

					methodCalls.put(key, arrayList);
				}

			}

			super.visit(n, arg);

		}

		@Override
		public void visit(FieldAccessExpr n, Void arg) {

			if (currentMethod != null && firstRun) {
				String key = currentMethod.getNameAsString();

				if (methodCalls.containsKey(key)) {
					ArrayList<MethodData> arrayList = methodCalls.get(key);

//					String base = n.getScope().getChildNodes().get(0).toString();
					String base = n.getChildNodes().get(0).toString();
					if (!exceptions.contains(n.getNameAsString()) && !exceptions.contains(base)) {
						arrayList.add(new MethodData(n.getBegin().get().line, n.toString(), n.getNameAsString(), null,
								base, key));
					}

					methodCalls.put(key, arrayList);
				}
			}
			super.visit(n, arg);
		}

		/* Get all variables, objects and method calls from each subroutine. */
		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {
			if (currentMethod != null && firstRun) {
				String key = currentMethod.getNameAsString();
				// System.out.println("VariableDeclarationExpr: " + n);

				if (methodVars.containsKey(key)) {
					ArrayList<VariableData> arrayList = methodVars.get(key);
					ArrayList<Node> nodesList = new ArrayList(Arrays.asList(n.getChildNodes().toArray()));
					tranverseVariableDeclarator(nodesList, arrayList, key);
					// VariableData(int line, String expr, String variable, String funcName)
					// System.out.println("SYSTEM VARIABLES1: " + n.getVariables());
					// System.out.println("SYSTEM VARIABLES2: " + n.toString());
					// arrayList.add(new VariableData(n.getBegin().get().line, n.get));

					methodVars.put(key, arrayList);
				}
			}
			super.visit(n, arg);
		}

		public void tranverseVariableDeclarator(ArrayList<Node> nodes, ArrayList<VariableData> varDeclExprList,
				String currentMethod) {

			if (nodes.size() > 0) {
				Node node = nodes.get(0);

				if (node.getClass().getSimpleName().toString().equals("VariableDeclarator")) {
					// (node.getChildNodes().get(1).toString()
					varDeclExprList.add(new VariableData(node.getBegin().get().line, node.toString(),
							node.getChildNodes().get(1).toString(), currentMethod));
				} else {
					nodes.remove(0);
					tranverseVariableDeclarator(nodes, varDeclExprList, currentMethod);
				}
			}
		}

		@Override
		public void visit(Parameter n, Void arg) {
			if (currentMethod != null && firstRun) {
				String key = currentMethod.getNameAsString();
				if (methodVars.containsKey(key)) {
					ArrayList<VariableData> arrayList = methodVars.get(key);
					int arraySize = n.getChildNodes().size();
					Node node = n.getChildNodes().get(arraySize - 1);

					arrayList.add(new VariableData(node.getBegin().get().line, node.toString(), node.toString(), key));
					methodVars.put(key, arrayList);

				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(AssignExpr n, Void arg) {
			if (currentMethod != null && firstRun) {
				String key = currentMethod.getNameAsString();
				if (methodCalls.containsKey(key)) {
//					System.out.println("AssignExpr: " + n);
//					System.out.println("getValue: " + n.getValue());
//					System.out.println("getOperator: " + n.getOperator());
//					System.out.println("getTarget: " + n.getTarget());
					ArrayList<MethodData> arrayList = methodCalls.get(key);
					arrayList.add(new MethodData(n.getBegin().get().line, n.toString(), n.getValue().toString(), null,
							n.getTarget().toString(), key));
					methodCalls.put(key, arrayList);

				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(UnaryExpr n, Void arg) {
			if (currentMethod != null && firstRun) {
				String key = currentMethod.getNameAsString();
				if (methodCalls.containsKey(key)) {
					if (n.getChildNodes().size() > 0 && !Util.checkIntBool(n.getChildNodes().get(0).toString())) {
						ArrayList<MethodData> arrayList = methodCalls.get(key);
						Matcher m = Pattern.compile("\\w+").matcher(n.getChildNodes().get(0).toString());
						if (m.find()) {
							String base = m.group();
							arrayList.add(new MethodData(n.getBegin().get().line, n.toString(),
									n.getExpression().toString(), null, base, key));
							methodCalls.put(key, arrayList);
						}

					}

				}
			}
			super.visit(n, arg);
		}

		public void removeDeclValues() {
			for (Entry<String, ArrayList<VariableData>> varDat : methodVars.entrySet()) {
//				System.out.println("methodVars: " + varDat.getKey() + ":");
				ArrayList<MethodData> removeValues = new ArrayList<MethodData>();
				for (Entry<String, ArrayList<MethodData>> metDat : methodCalls.entrySet()) {
					if (varDat.getKey().equals(metDat.getKey())) {
						for (VariableData v : varDat.getValue()) {
							for (MethodData m : metDat.getValue()) {
								if (v.getVar().equals(m.getFunctionCallName()) || v.getVar().equals(m.getBaseVar())) {
									removeValues.add(m);
//									metDat.getValue().remove(m);
								}
							}
						}
					}
					metDat.getValue().removeAll(removeValues);
				}
			}

		}

		// G�r ett set till varje metod.
		public void findTestRunWarNew() {
			if (currentMethod != null && !firstRun) {
				for (Entry<String, ArrayList<MethodData>> m1 : methodCalls.entrySet()) {
					for (Entry<String, ArrayList<MethodData>> m2 : methodCalls.entrySet()) {
						if (m1.getKey().equals(currentMethod.getNameAsString()) && !m1.getKey().equals(m2.getKey())) {
							for (MethodData mData1 : m1.getValue()) {
								for (MethodData mData2 : m2.getValue()) {
//									if (mData1.getBaseVar().equals("empty")
//											&& mData1.getFunctionCallName().equals(mData2.getFunctionCallName())) {
//										if (!checkIfFlaky(currentMethod.getNameAsString())) {
//											setMethodStatusFlaky(currentMethod);
//											System.out.println("TestRunWarNew1");
//
//											this.hasFlaky = true;
//										}

//										MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(),
//												mData1.getExpr(), mData2.getExpr(), true);
//										instance.setIsTestRunWar(true);
//										metaData.add(instance);
//									} else 
									if (!mData1.getBaseVar().equals("empty") && !mData2.getBaseVar().equals("empty")
											&& mData1.getBaseVar().equals(mData2.getBaseVar())) {
										if (!checkIfFlaky(currentMethod.getNameAsString())) {
											setMethodStatusFlaky(currentMethod);
											System.out.println("TestRunWarNew2");

											this.hasFlaky = true;
										}
//										MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(),
//												mData1.getExpr(), mData2.getExpr(), true);
//										instance.setIsTestRunWar(true);
//										metaData.add(instance);
									}
								}
							}
						}
					}
				}
			}
		}

		public void checkForUniqueValues() {
			if (currentMethod != null && !firstRun) {
				for (Entry<String, ArrayList<MethodData>> m1 : methodCalls.entrySet()) {
					if (m1.getKey().equals(currentMethod.getNameAsString())) {

						for (MethodData mData1 : m1.getValue()) {
							for (MethodData mData2 : m1.getValue()) {
//								if (mData1.getFunctionName().equals(currentMethod.getNameAsString()) && ! mData1.getFunctionName().equals(mData2.getFunctionName())) {
//								if (! mData1.getFunctionName().equals(mData2.getFunctionName())) {
//								&& !mData1.getFunctionName().equals(mData2.getFunctionName()
								if (mData1.getBaseVar().equals("empty")
										&& mData1.getFunctionCallName().equals(mData2.getFunctionCallName())) {
//									System.out.println("mData1.getFunctionName() : " + mData1.getFunctionName());
//									System.out.println("currentMethod : " + currentMethod.getNameAsString());
//									System.out.println("mData1.getFunctionCallName: " + mData1.getFunctionCallName());
//									System.out.println("mData2.getFunctionCallName: " + mData2.getFunctionCallName());

//										MetaData instance = new MetaData(mData1.getLine(), mData1.getFunctionCallName(), mData1.getExpr(), true);

									MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(),
											mData1.getExpr(), mData2.getExpr(), true);

									instance.setIsTestRunWar(true);
									metaData.add(instance);

								}
//								}
							}
						}
					}
				}
			}
		}
	}
}

//					for (Entry<String, ArrayList<MethodData>> m2 : methodCalls.entrySet()) {
////						if (m1.getKey().equals(currentMethod.getNameAsString())
////								&& m2.getKey().equals(currentMethod.getNameAsString())) {
//							for (MethodData mData1 : m1.getValue()) {
//								for (MethodData mData2 : m2.getValue()) {
//									if (! mData1.getFunctionName().equals(mData2.getFunctionName())) {
//										if (mData1.getBaseVar().equals("empty")) {
//											if (mData1.getFunctionCallName().equals(mData2.getFunctionCallName())) {
//												System.out.println(
//														"mData1.getFunctionCallName: " + mData1.getFunctionCallName());
//												System.out.println(
//														"mData2.getFunctionCallName: " + mData2.getFunctionCallName());
//												MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(),
//														mData1.getExpr(), mData2.getExpr(), true);
//												instance.setIsTestRunWarNew(true);
//												metaData.add(instance);
//											}
//										} else if (mData1.getBaseVar().equals(mData2.getBaseVar())) {
//											System.out.println("mData1.getFunctionName: " + mData1.getFunctionName());
//											System.out.println("mData2.getFunctionName: " + mData2.getFunctionName());
//											System.out.println("mData1.getBaseVar: " + mData1.getBaseVar());
//											System.out.println("mData2.getBaseVar: " + mData2.getBaseVar());
//											MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(),
//													mData1.getExpr(), mData2.getExpr(), true);
//											instance.setIsTestRunWarNew(true);
//											metaData.add(instance);
//
//										}
//									}
//
//								}
//							}
////						}
////					}
//				}
//
//			}

//func1: [a,b,c, func2]
//
//func2: [c,d,e]

//	if(! mData1.getFunctionName().equals(mData2.getFunctionName()))
//	boolean equalFunction = mData1.getFunctionCallName()
//			.equals(mData2.getFunctionCallName());

//	boolean equalObject = ! mData1.getBaseVar().equals("empty") && !mData2.getBaseVar().equals("empty") && mData1.getBaseVar().equals(mData2.getBaseVar());
//	boolean equalObject = mData1.getBaseVar().equals(mData2.getBaseVar());
//	System.out.println("mData1.getFunctionCallName: " + mData1.getFunctionCallName());
//	System.out.println("mData2.getFunctionCallName: " + mData2.getFunctionCallName());
//	System.out.println("equalObject: " + equalObject);
//	System.out.println("");

//	if (equalFunction) {
//		MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(), mData1.getExpr(), mData2.getExpr(), true);
//		instance.setIsTestRunWarNew(true);
//		metaData.add(instance);
////
//	}
//	else if (equalObject) {
//		MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(), mData1.getBaseVar(), mData2.getBaseVar(), true);
//		instance.setIsTestRunWarNew(true);
//		metaData.add(instance);
//	}

//	getBaseVar: reversePolishNotation
//	getExpr: reversePolishNotation.waitForResult()
//	getFunctionCallName: waitForResult
//	getFunctionName(): findAllThings
//	getLine: 36
//	getBaseVar: empty
//	getExpr: doSomething()
//	getFunctionCallName: doSomething
//	getFunctionName(): findAllThings
//	getLine: 38
//}
