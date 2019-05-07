package flakyhoover;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import flakes.ConditionalTestLogic;
import flakes.FireAndForget;
import flakes.IndirectTesting;
import flakes.ResourceOptimism;

public class FlakyDetector {

	private List<AbstractFlaky> flakyInst;

	public FlakyDetector() {
		initializeFlakyInstances();
	}

	private void initializeFlakyInstances() {
		flakyInst = new ArrayList<>();
//		flakyInst.add(new VariableCond());
//		flakyInst.add(new FlakyAsync());
//		flakyInst.add(new CondTimeout());
//		flakyInst.add(new TestRunWar_spec());
		
		flakyInst.add(new ConditionalTestLogic());
		flakyInst.add(new FireAndForget());
//		flakyInst.add(new TestRunWar());
		flakyInst.add(new ResourceOptimism());
		flakyInst.add(new IndirectTesting());

//		flakyInst.add(new ResourceLeak());

	}

	public List<String> getFlakyNames() {
		return flakyInst.stream().map(AbstractFlaky::getFlakyName).collect(Collectors.toList());
	}

	public TestFile detect(TestFile testFile) throws FileNotFoundException {
		CompilationUnit productionFileCompilationUnit = null;
		FileInputStream testFileInputStream, productionFileInputStream;

		testFileInputStream = new FileInputStream(testFile.getTestFilePath());

//		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();

//        File f = new File("C:\\Users\\Socke\\Documents\\Programming\\javavisit\\beaconsperth\\src");
//        File f = new File("javavisit/beaconsperth/src/beaconsperth");

//        C:\\Users\\Socke\\Documents\\Programming\\javavisit\\beaconsperth\\src\\
//		TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(f);

//		reflectionTypeSolver.setParent(reflectionTypeSolver);
//		CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
//		combinedSolver.add(reflectionTypeSolver);
//		combinedSolver.add(javaParserTypeSolver);

//		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);

//		JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

//		TypeSolver typeSolver = new CombinedTypeSolver();
//		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);

//		JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

//		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
//		combinedTypeSolver.add(new ReflectionTypeSolver());
//		combinedTypeSolver.add(
//				new JavaParserTypeSolver(new File("C:/Users/Socke/Documents/Programming/javavisit/beaconsperth/src")));
//
//		// Configure JavaParser to use type resolution
//		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
//		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

		// CompilationUnit testFileCompilationUnit = JavaParser.parse(new
		// File(testFile.getTestFilePath()));

//        StaticJavaParser
//        CompilationUnit cu = StaticJavaParser.parse("class X { int x() { return 1 + 1.0 - 5; } }");

		CompilationUnit testFileCompilationUnit = StaticJavaParser.parse(testFileInputStream);

		for (AbstractFlaky flaky : flakyInst) {
//			System.out.println("\n");
//			System.out.println("Running: " + flaky.getFlakyName());
//			System.out.println("path: " + testFile.getTestFilePathWithDot());
			
			try {
				flaky.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit,
						testFile.getTestFilePathWithDot(), testFile.getApp());
				
				
			} catch (FileNotFoundException e) {
				testFile.addFlakyInst(null);
				continue;
			}
			testFile.addFlakyInst(flaky);
		}
		return testFile;

	}

}
