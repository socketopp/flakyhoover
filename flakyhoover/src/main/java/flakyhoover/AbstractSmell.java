package flakyhoover;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

import util.TestSmell;

public abstract class AbstractSmell {

	public abstract boolean getHasSmell();

	public abstract String getSmellName();

	public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,
			CompilationUnit productionFileCompilationUnit, String testClassName, String projectName)
			throws FileNotFoundException;

	public abstract List<AbstractSmellElement> getSmellyElements();

	public abstract ArrayList<TestSmell> getTestSmells();

}