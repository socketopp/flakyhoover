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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.MetaData;
import flakyhoover.MethodData;
import flakyhoover.TestMethod;
import util.Util;
import util.TestSmell;

public class IndirectTesting2 extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;
	protected ArrayList<TestSmell> testSmells;
	protected String fileName;
	protected String projectName;

	public ArrayList<TestSmell> getTestSmells() {
		return testSmells;
	}

	public IndirectTesting2() {
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
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		testSmells = new ArrayList<TestSmell>();
		this.fileName = testClassName;
		this.projectName = projectName;

		IndirectTesting2.ClassVisitor classVisitor;
		classVisitor = new IndirectTesting2.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);
		classVisitor.firstRun = false;
		classVisitor.visit(testFileCompilationUnit, null);

	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		private boolean validTestMethod = false;
		private String className;
		private TestSmell testSmell = new TestSmell();
		private boolean isTestClass;

		private String methodName;
		private ArrayList<String> jClasses = Util.getAllJavaClasses();
		private List<String> exceptions = new ArrayList<String>(
				Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
						"", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
						"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

		TestMethod testMethod;
		private Set<String> indirectClasses = new HashSet<String>();
		private Set<String> sut = new HashSet<String>();

		private Set<String> objects = new HashSet<String>();
		private Set<String> methods = new HashSet<String>();
		private String flakinessType = "test-order-dependency";
		private List<MetaData> metaData = new ArrayList<MetaData>();
		private boolean firstRun = true;
		private ArrayList<String> allClassMethods = new ArrayList<String>();
		
		private Map<String, ArrayList<String>> methodCalls = new HashMap<String, ArrayList<String>>();

		@Override
		public void visit(ConstructorDeclaration n, Void arg) {
//			System.out.println("ConstructorDeclaration: " + n.getNameAsString());
			super.visit(n, arg);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			isTestClass = Util.isValidTestClass(n);

			if (Util.isValidTestClass(n)) {
				className = Util.removeTest(n.getNameAsString());
			}

//			ArrayList<String> c = Util.getAllJavaClasses();
//			for(String a : c) {
//				System.out.println(a);
//			}

			// Algorithm
			// If the body have an assert,
			// If the method does not contains a instance of the class it is testing, but
			// other instances i.e testLanguageIndentifier contain LanguageIndentifier
			// Then mark as flaky

			// If the body have an assert,
			// If the method have a class object that is not part of Java (Implies that it
			// is an production class instance)?
			// Then mark as smelly/flaky

			super.visit(n, arg);
		}

		private void initTestSmells(MethodDeclaration n) {
			testSmell.setFlakinessType(flakinessType);
			testSmell.setProject(projectName);
			testSmell.setTestMethod(n.getNameAsString());
			testSmell.setSmellType(getFlakyName());
			testSmell.setTestClass(fileName);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			if (isTestClass && !firstRun) {
				currentMethod = n;
				testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
				testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
				initTestSmells(n);
//				validTestMethod = Util.isValidTestMethod(n);

				methodName = Util.removeTest(n.getNameAsString());
//				System.out.println("\n");
//				System.out.println(n.getNameAsString());

				super.visit(n, arg);

//				testMethod.setHasFlaky(!indirectClasses.isEmpty() || hasFlaky);

				// create kan g�ra massa andra grejer och sen returnera configure. <-- rekursivt
				// g� igenom funktionsanrop?

//				TO-DO else if p�b�rjad...
				// Om vi har sut.isempty OCH indirectCLasses.empty --> ocks� setHasflaky(true);
				// tredje else if indirectaclasses.empty och sut.empty MEN, vi har sutCaller
				// (anrop till objekt som inte skapas i metoden som �r icke indirekta s� �r vi
				// cooL! ) :D
//				System.out.println(indirectClasses.isEmpty());
//				System.out.println(sut.isEmpty());
//				System.out.println(hasFlaky);

				if (!indirectClasses.isEmpty()) {
//					System.out.println("DOWN1");
					testMethod.setHasFlaky(true);
//				} else if (indirectClasses.isEmpty() && sut.isEmpty() && methods.isEmpty() && hasFlaky) {
				} else if (indirectClasses.isEmpty() && hasFlaky) {
//					System.out.println("DOWN2");
					testMethod.setHasFlaky(true);
				}

//				else {
//					testMethod.setHasFlaky(false);
//				}

//				 && !sut.isEmpty()
//				testMethod.setHasFlaky(metaData.size() > 0 && hasFlaky);
//				if (metaData.size() >= 1) {
//					metaData.get(0).setExtra(missingCondVars);
//				}

				if (testMethod.getHasFlaky()) {
					testSmells.add(testSmell);
				}

				testMethod.addMetaDataItem("VariableCond", metaData);

				if (testMethod.getHasFlaky()) {
					flakyElementList.add(testMethod);
				}

				// reset values for next method
				currentMethod = null;
				hasFlaky = false;
				indirectClasses = new HashSet<String>();
				testSmell = new TestSmell();

				sut = new HashSet<String>();
//				validTestMethod = false;
			} else {
				currentMethod = n;
				
				if (!methodCalls.containsKey(n.getNameAsString())) {
					methodCalls.put(n.getNameAsString(), new ArrayList<String>());
				}
				
				allClassMethods.add(n.getNameAsString());
				super.visit(n, arg);
				currentMethod = null;
			}

		}

		@Override
		public void visit(FieldAccessExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null && !firstRun) {
				if (n.getChildNodes().size() > 1) {
					String sut = n.getChildNodes().get(0).toString();
					String indirect = n.getChildNodes().get(1).toString();
					if (sut.equals(methodName) || sut.equals(className)) {
						if (Character.isUpperCase(indirect.charAt(0)) && !jClasses.contains(indirect)) {
//							System.out.println("debug1");
							hasFlaky = true;
							metaData.add(new MetaData(n.getBegin().get().line, sut, n.toString(), true));
						}
					}
				}
			}
			super.visit(n, arg);

		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null && !firstRun) {
//				System.out.println("ObjectCreationExpr: " + n.getTypeAsString());

				if (!jClasses.contains(n.getTypeAsString())
						&& (!n.getTypeAsString().equals(methodName) && !n.getTypeAsString().equals(className))) {
					indirectClasses.add(n.getTypeAsString());
//					System.out.println("1Jclasses NOT contain this: " + n);

					metaData.add(new MetaData(n.getBegin().get().line, n.getTypeAsString(), n.toString(), true));
//					System.out.println("debug2");

					hasFlaky = true;
				} else if (n.getTypeAsString().equals(methodName) || n.getTypeAsString().equals(className)) {
					sut.add(n.getTypeAsString());
				} else {
//					System.out.println("Jclasses contain this: " + n);
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			
//			System.out.println(jClasses)
			if (currentMethod != null && !firstRun) {
				if (!jClasses.contains(n.getElementType().toString())
						&& (!n.getElementType().toString().equals(methodName)
								&& !n.getElementType().toString().equals(className))) {
//					System.out.println("debug3");

					indirectClasses.add(n.getElementType().toString());

//					System.out.println("2Jclasses NOT contain this: " + n);

					metaData.add(
							new MetaData(n.getBegin().get().line, n.getElementType().toString(), n.toString(), true));
					hasFlaky = true;
				} else if (n.getElementType().toString().equals(methodName)
						|| n.getElementType().toString().equals(className)) {
					sut.add(n.getElementType().toString());
				} else {
//					System.out.println("Jclasses contain this: " + n);
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(Parameter n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null && !firstRun) {
				if (!jClasses.contains(n.getTypeAsString()) && !n.getTypeAsString().equals(methodName)
						&& !n.getTypeAsString().equals(className)) {
					indirectClasses.add(n.getTypeAsString());
//					System.out.println("3Jclasses NOT contain this: " + n);
//					System.out.println("debug4");

					metaData.add(new MetaData(n.getBegin().get().line, n.getTypeAsString(), n.toString(), true));
					hasFlaky = true;
				} else if (n.getTypeAsString().equals(methodName) || n.getTypeAsString().equals(className)) {
					sut.add(n.getTypeAsString());
				} else {
//					System.out.println("Jclasses contain this: " + n);
				}
			}
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			if (currentMethod == null && !firstRun) {
				if (n.getVariables().size() > 0 && n.getVariables().get(0).getChildNodes().size() > 1) {
					String fd = n.getElementType().toString();
					String base = n.getVariables().get(0).getChildNodes().get(1).toString();
					if (!jClasses.contains(fd) && fd.equals(methodName) || fd.equals(className)) {
						this.objects.add(base);
					}
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {

			// TO-DO
			// H�mta field declaration, om den refererar till klassen ONLY s� l�gg till den
			// wherever it feels alright,
			// Spara undan alla metodanrop till icke indirekta objekt instanser.
			// Om vi har empty indirekta objekt i denna metod MEN vi anropar en SUT instans,
			// s� �r vi icke flaky, annars label as flaky om vi anropar n�got som inte �r
			// SUT.

//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null && !firstRun) {
//				System.out.println("MethodCallExpr: " + n);
//				System.out.println("getChildNodes: " + n.getChildNodes().get(0).toString());
//				System.out.println("getNameAsString: " + n.getNameAsString());
				if (n.getChildNodes().size() > 0) {
					String methodCall = n.getChildNodes().get(0).toString();
					// check this out
//					System.out.println("methodCall: " + methodCall);
					
					ArrayList<String> calls = methodCalls.get(currentMethod.getNameAsString());

					if (!calls.contains(methodCall) && !this.objects.contains(methodCall) && !exceptions.contains(methodCall)
							&& !jClasses.contains(methodCall) && !allClassMethods.contains(methodCall)) {
//						System.out.println("debug5");

						hasFlaky = true;
					} else {
						sut.add(methodCall);

					}
				}

			} else {
//				System.out.println("something: " + methodCall);
//				System.out.println("something: " + n);

				if (currentMethod != null && n.getChildNodes().size() > 0) {
					
					ArrayList<String> calls = methodCalls.get(currentMethod.getNameAsString());
					
					String methodCall = n.getChildNodes().get(0).toString();

					if(!exceptions.contains(methodCall)	&& !jClasses.contains(methodCall)) {
						calls.add(methodCall);
						methodCalls.put(currentMethod.getNameAsString(), calls);


					}

				}
			}

			super.visit(n, arg);

		}

	}

}

//@Override
//public void visit(ExpressionStmt n, Void arg) {
//	if (currentMethod != null && validTestMethod) {
//		System.out.println("ExpressionStmt: " + n);
//
//	}
//	super.visit(n, arg);
//
//}
