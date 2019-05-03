package flakyhoover;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

import util.TestSmell;

public abstract class AbstractFlaky {

	public abstract boolean getHasFlaky();

	public abstract String getFlakyName();

	public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,
			CompilationUnit productionFileCompilationUnit, String testClassName, String projectName)
			throws FileNotFoundException;

	public abstract List<AbstractFlakyElement> getFlakyElements();

	public abstract ArrayList<TestSmell> getTestSmells();

}