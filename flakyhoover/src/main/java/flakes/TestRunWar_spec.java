package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.FunctionDecl;
import flakyhoover.FunctionNode;
import flakyhoover.MetaData;
import flakyhoover.MethodData;
import flakyhoover.TestMethod;
import flakyhoover.VariableData;
import util.TestSmell;

public class TestRunWar_spec extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;

	public TestRunWar_spec() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "TestRunWar_spec";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testFileName, String productionFileName) throws FileNotFoundException {
		TestRunWar_spec.ClassVisitor classVisitor;
		classVisitor = new TestRunWar_spec.ClassVisitor();

		classVisitor.visit(testFileCompilationUnit, null);
//		printClassVars(classVisitor.classVariables);

//		tranverseGraph(classVisitor.rootNode.getFunctionNodes());

//		printFunctionNode(classVisitor.rootNode);
//		for (Map.Entry<String, Set<String>> entry : classVisitor.methodGraph.entrySet()) {
//			String key = entry.getKey();
//			Object value = entry.getValue();
//			System.out.println("KEY: " + key + " / " + valu
//		e);
//		}
	}

	public void printClassVars(Set<String> set) {
		for (String s : set) {
			System.out.print("CLASS: " + s + "\n");

		}
	}

	public void printFunctionNode(FunctionNode fn) {

//		System.out.println(fn.getName() + " " + fn.getFunctionNodes());
//		System.out.println();

		for (FunctionNode node : fn.getFunctionNodes()) {
			node.print();
//			printFunctionNode(node);
		}

	}

	public FunctionNode getNode(ArrayList<FunctionNode> nodes, FunctionNode fn) {
		for (FunctionNode node : nodes) {
			if (node.getName().equals(fn.getName())) {
				return node;
			}
		}
		return fn;
	}

//	public ArrayList<FunctionNode> tranverseGraph(FunctionNode fn) {
	public void tranverseGraph(ArrayList<FunctionNode> fn) {
		for (FunctionNode node : fn) {
			System.out.println("CUR: " + node.getName());
			if (node.getFunctionNodes().isEmpty()) {
				continue;
			} else {
				FunctionNode n = getNode(fn, node.getFunctionNodes().get(0));

				System.out.println("GET mah name: " + n.getName());
				node.addFunctionNode(n);
//				tranverseGraph()

			}

		}

	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {

		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private List<String> exceptions = new ArrayList<String>(
				Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
						"", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));
		private List<String> operators = new ArrayList<String>(Arrays.asList("POSTFIX_INCREMENT", "POSTFIX_DECREMENT",
				"MINUS", "PLUS", "LOGICAL_COMPLEMENT", "PREFIX_INCREMENT", "PREFIX_DECREMENT", "LOGICAL_COMPLEMENT"));
		TestMethod testMethod;
		private List<String> methodVariables = new ArrayList<>();
//		private List<String> classVariables = new ArrayList<>();

		private Set<String> classVariables = new HashSet<String>();

//		private HashMap<String, ArrayList<String>> methodGraph = new HashMap<String, Set<String>>();

//		private FunctionNode rootNode = new FunctionNode("root", null);

		private ArrayList<FunctionDecl> functions = new ArrayList<FunctionDecl>();
		private FunctionNode rootNode = new FunctionNode("root", null);

		private boolean firstRun = true;
		private List<MetaData> metaData = new ArrayList<MetaData>();

		private Map<String, ArrayList<VariableData>> methodVars = new HashMap<String, ArrayList<VariableData>>();
		private Map<String, ArrayList<MethodData>> methodCalls = new HashMap<String, ArrayList<MethodData>>();

		private void setRun(boolean val) {
			this.firstRun = val;
		}

//		db adapters,
//		keyboard
//		files
//		path

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("MethodDeclaration: " + n.getNameAsString());
//			System.out.println("parameters: " + n.getParameters());
			currentMethod = n;
			testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
			testMethod.setHasFlaky(false); // default value is false (i.e. no smell)

//			System.out.println(rootNode);
			rootNode.addFunctionNode(new FunctionNode(n.getNameAsString(), n.getParameters()));

			if (n.getParameters().size() > 0) {
				System.out.println("parameters: " + n.getParameters().get(0).getType().toString() + " "
						+ n.getParameters().get(0).getNameAsString());

			}
//			functions.add(new FunctionDecl(n.getNameAsString(), n.getParameters()));
//			rootNode.add(new FunctionDecl(n.getNameAsString(), n.getParameters()));
			// List<String> newArrayList = new ArrayList<String>();

//			if (!methodGraph.containsKey(n.getNameAsString())) {
//				methodGraph.put(n.getNameAsString(), new HashSet<String>());
//			}

			super.visit(n, arg);

			// testMethod.setHasFlaky(methodVariables.size() >= 1 || hasFlaky==true);

			testMethod.setHasFlaky(metaData.size() > 0);
			testMethod.addMetaDataItem("VariableCond", metaData);

			if (testMethod.getHasFlaky()) {
				flakyElementList.add(testMethod);
			}

			// reset values for next method
			currentMethod = null;
			hasFlaky = false;
//			methodVariables = new ArrayList<>();
//			metaData = new ArrayList<MetaData>();

			// System.out.println("");

		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			// Adding all variable names for files and paths in the class field declaration.
			if (n.getElementType().toString().equals("File") || n.getElementType().toString().equals("FileSystem")
					|| n.getElementType().toString().equals("Path")) {
				this.classVariables.add(n.getVariables().get(0).getNameAsString());
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			if (currentMethod == null) {
				if (n.getTypeAsString().equals("File") && n.getArguments().size() == 1) {
					// Adding file arguments/paths
					this.classVariables.add(n.getArguments().get(0).toString());
				}
			} else {
				// Have to get objectCreationExpr inside methods
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			String concat = "";
			if (currentMethod == null && (n.getNameAsString().equals("get") || n.getNameAsString().equals("getPath"))
					&& n.getChildNodes().size() >= 1
					&& (n.getChildNodes().get(0).toString().equals("Paths")
							|| n.getChildNodes().get(0).toString().equals("FileSystems.getDefault()")
							|| n.getChildNodes().get(0).toString().equals("Path"))) {
				for (int i = 0; i < n.getArguments().size(); i++) {
					if (i == n.getArguments().size() - 1) {

						concat += n.getArguments().get(i).toString().replace("\"", "");
					} else {
						concat += n.getArguments().get(i).toString().replace("\"", "") + "/";
					}
				}
				this.classVariables.add(concat);

			} else {
//				// Have to get objectCreationExpr inside methods
			}

			super.visit(n, arg);

		}

		/* Get all variables, objects and method calls from each subroutine. */
		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {
			if (currentMethod != null && firstRun) {
				System.out.println("VariableDeclarationExpr: " + n);

//				if(n.getElementType().toString().equals("File")) {
//					System.out.println("File name is: " + n.getChildNodes().get(0).getChildNodes().get(1).toString());
//
//				}
//				System.out.println("");

				// GetElementType = File/Path
				// GetVars/nodechildren, typ samma sak fast nodechildren inneh�ller lista med
				// nodes

			}
			super.visit(n, arg);
		}

//		public FunctionNode tranverse(FunctionNode fn, String key) {
//			for (FunctionNode node : fn.getFunctionNodes()) {
//				if (node.getName().equals(key)) {
//					return node;
//				}
//			}
//			return fn;
//		}

//		public void tranverseGraph(HashMap<String, Set<String>> graph) {

//			for (Map.Entry<String, Set<String>> entry : methodGraph.entrySet()) {
//
//				for(String method : entry.getValue()) {
//					Set<String> methods = getAllMethods(methodGraph, method);
//				}
//			}

//
//
//			String current;
//			for (Map.Entry<String, Set<String>> entry : methodGraph.entrySet()) {
////				if(entry.getKey().equals(current)) {
//
////				}
//				for(String method : entry.getValue()) {
//					current = method;
//				}
//				String key = entry.getKey();
//				Object value = entry.getValue();
//				System.out.println("KEY: " + key + " / " + value);
//			}
//		}

//		public Set<String> getAllMethods(HashMap<String, Set<String>> methodGraph, String methodName) {
//			return methodGraph.get(methodName);
//		}

//		@Override
//		public void visit(FieldAccessExpr n, Void arg) {
//
//			if (currentMethod != null && firstRun) {
//				System.out.println("FieldAccessExpr: " + n);
//			}
//			super.visit(n, arg);
//
//		}

//		@Override
//		public void visit(ExpressionStmt n, Void arg) {
//
//			if (currentMethod != null && firstRun) {
//				System.out.println("ExpressionStmt: " + n);
//				System.out.println("!getChildNodes: " + n.getChildNodes());
//				System.out.println("getClass: " + n.getChildNodes().get(0).getClass().getSimpleName());
//
//
//				System.out.println("");
//
//			}
//			super.visit(n, arg);
//
//		}

//
//		@Override
//		public void visit(Parameter n, Void arg) {
//			if (currentMethod != null && firstRun) {
//
//			}
//			super.visit(n, arg);
//		}
//

//
//		@Override
//		public void visit(AssignExpr n, Void arg) {
//			if (currentMethod != null && firstRun) {
//				System.out.println("AssignExpr: " +n);
//
//			}
//
//			super.visit(n, arg);
//		}

	}

	@Override
	public ArrayList<TestSmell> getTestSmells() {
		// TODO Auto-generated method stub
		return null;
	}
}

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
//		instance.setIsTestRunWar(true);
//		metaData.add(instance);
////
//	}
//	else if (equalObject) {
//		MetaData instance = new MetaData(mData1.getLine(), mData2.getLine(), mData1.getBaseVar(), mData2.getBaseVar(), true);
//		instance.setIsTestRunWar(true);
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

//

//
//@Override
//public void visit(VariableDeclarator n, Void arg) {
//	if (currentMethod != null && firstRun) {
//		System.out.println("VariableDeclarator: " + n);
//
//	}
//	super.visit(n, arg);
//}
