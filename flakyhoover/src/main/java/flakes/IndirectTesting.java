package flakes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import flakyhoover.TestMethod;
import util.Util;
import util.TestSmell;

public class IndirectTesting extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;

	public IndirectTesting() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "VariableCond";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit,
			String testClassName, String projectName) throws FileNotFoundException {
		IndirectTesting.ClassVisitor classVisitor;
		classVisitor = new IndirectTesting.ClassVisitor();
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

		private List<MetaData> metaData = new ArrayList<MetaData>();

		@Override
		public void visit(ConstructorDeclaration n, Void arg) {
//			System.out.println("ConstructorDeclaration: " + n.getNameAsString());

			super.visit(n, arg);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
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

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			currentMethod = n;
			testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
			testMethod.setHasFlaky(false); // default value is false (i.e. no smell)
			
//			validTestMethod = Util.isValidTestMethod(n);

			methodName = Util.removeTest(n.getNameAsString());
//			System.out.println("\n");
//			System.out.println(n.getNameAsString());

			super.visit(n, arg);

//			testMethod.setHasFlaky(!indirectClasses.isEmpty() || hasFlaky);

			// create kan g�ra massa andra grejer och sen returnera configure. <-- rekursivt
			// g� igenom funktionsanrop?

//			TO-DO else if p�b�rjad...
			// Om vi har sut.isempty OCH indirectCLasses.empty --> ocks� setHasflaky(true);
			// tredje else if indirectaclasses.empty och sut.empty MEN, vi har sutCaller
			// (anrop till objekt som inte skapas i metoden som �r icke indirekta s� �r vi
			// cooL! ) :D
//			System.out.println(indirectClasses.isEmpty());
//			System.out.println(sut.isEmpty());
//			System.out.println(hasFlaky);

			if (!indirectClasses.isEmpty()) {
//				System.out.println("DOWN1");
				testMethod.setHasFlaky(true);
//			} else if (indirectClasses.isEmpty() && sut.isEmpty() && methods.isEmpty() && hasFlaky) {
			} else if (indirectClasses.isEmpty() && hasFlaky) {
//				System.out.println("DOWN2");
				testMethod.setHasFlaky(true);
			}

//			 && !sut.isEmpty()
//			testMethod.setHasFlaky(metaData.size() > 0 && hasFlaky);
//			if (metaData.size() >= 1) {
//				metaData.get(0).setExtra(missingCondVars);
//			}

			testMethod.addMetaDataItem("VariableCond", metaData);

			if (testMethod.getHasFlaky()) {
				flakyElementList.add(testMethod);
			}

			// reset values for next method
			currentMethod = null;
			hasFlaky = false;
			indirectClasses = new HashSet<String>();
			sut = new HashSet<String>();
//			validTestMethod = false;
		}

		@Override
		public void visit(FieldAccessExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null) {
				if (n.getChildNodes().size() > 1) {
					String sut = n.getChildNodes().get(0).toString();
					String indirect = n.getChildNodes().get(1).toString();
					if (sut.equals(methodName) || sut.equals(className)) {
						if (Character.isUpperCase(indirect.charAt(0)) && !jClasses.contains(indirect)) {
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
			if (currentMethod != null) {

				if (!jClasses.contains(n.getTypeAsString())
						&& (!n.getTypeAsString().equals(methodName) && !n.getTypeAsString().equals(className))) {
					indirectClasses.add(n.getTypeAsString());
//					System.out.println("1Jclasses NOT contain this: " + n);

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
		public void visit(VariableDeclarationExpr n, Void arg) {
//			if (currentMethod != null && validTestMethod) {
			if (currentMethod != null) {
				if (!jClasses.contains(n.getElementType().toString())
						&& (!n.getElementType().toString().equals(methodName)
								&& !n.getElementType().toString().equals(className))) {
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
			if (currentMethod != null) {
				if (!jClasses.contains(n.getTypeAsString()) && !n.getTypeAsString().equals(methodName)
						&& !n.getTypeAsString().equals(className)) {
					indirectClasses.add(n.getTypeAsString());
//					System.out.println("3Jclasses NOT contain this: " + n);

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
			if (currentMethod == null) {
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
			if (currentMethod != null) {
//				System.out.println("MethodCallExpr: " + n);
//				System.out.println("getChildNodes: " + n.getChildNodes().get(0).toString());
//				System.out.println("getNameAsString: " + n.getNameAsString());
				if (n.getChildNodes().size() > 0) {
					String methodCall = n.getChildNodes().get(0).toString();
					// check this out
					if (!this.objects.contains(methodCall) && !exceptions.contains(methodCall)) {
//						System.out.println("1BITCH");
						hasFlaky = true;
					} else {
//						System.out.println("BITCH");
						sut.add(methodCall);

					}
				}

			}

			super.visit(n, arg);

		}

	}

	@Override
	public ArrayList<TestSmell> getTestSmells() {
		// TODO Auto-generated method stub
		return null;
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
