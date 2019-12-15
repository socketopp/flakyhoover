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
import flakes.SharedFixture;

public class FlakyDetector {

	private List<AbstractSmell> flakyInst;

	public FlakyDetector(boolean testing, AbstractSmell test) {
		if (testing) {
			flakyInst = new ArrayList<>();
			flakyInst.add(test);

		} else {
			initializeFlakyInstances();

		}
	}

	public FlakyDetector() {
		initializeFlakyInstances();
	}

	private void initializeFlakyInstances() {
		flakyInst = new ArrayList<>();
		flakyInst.add(new ConditionalTestLogic());
		flakyInst.add(new FireAndForget());
		flakyInst.add(new ResourceOptimism());
		flakyInst.add(new IndirectTesting());
		flakyInst.add(new SharedFixture());

	}

	public List<String> getFlakyNames() {
		return flakyInst.stream().map(AbstractSmell::getSmellName).collect(Collectors.toList());
	}

	public TestFile detect(TestFile testFile) throws FileNotFoundException {
		CompilationUnit productionFileCompilationUnit = null;
		@SuppressWarnings("unused")
		FileInputStream testFileInputStream, productionFileInputStream;
		testFileInputStream = new FileInputStream(testFile.getTestFilePath());

		CompilationUnit testFileCompilationUnit = StaticJavaParser.parse(testFileInputStream);

		for (AbstractSmell flaky : flakyInst) {
			try {
				flaky.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit,
						testFile.getTestFilePathWithDot(), testFile.getApp());

			} catch (FileNotFoundException e) {
				testFile.addSmellyInst(null);
				continue;
			}

			testFile.addSmellyInst(flaky);

		}
		return testFile;

	}

}
