package flakyhoover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.DocumentReader;
import util.DocumentWriter;
import util.FlakyFile;
import util.TestSmell;
import util.Util;

public class Main {

	private static ArrayList<String> allProductionFiles = new ArrayList<>();

	public static List<String> getAllProductionFiles() {
		return allProductionFiles;
	}

	public static void main(String[] args) throws IOException {
		String pathToProject = null;
		String projectName = null;

		try {
			pathToProject = args[0];
			projectName = args[1];
		} catch (Exception e) {
			System.err.println("Missing parameters: " + e);
			System.out.println("Run as flakyhoover.jar 'Project_path' 'Project_Name'");
			return;
		}

		if (pathToProject == null || projectName == null) {
			System.out.println("Missing parameters. Run as flakyhoover.jar 'Project_path' 'Project_Name'");
			return;
		}

		long startTime = System.nanoTime();
		FlakyDetector flakyDetector = new FlakyDetector();

//		ArrayList<String> testFilesPaths = Util.getAllTestFiles(
//				"C:\\Users\\Socke\\Documents\\Examensarbete\\dataset_code\\sources\\apache-cassandra-1.1");

		ArrayList<String> testFilesPaths = Util.getAllTestFiles(pathToProject);

		DocumentWriter.createDocumentWriter(
				new String[] { "projectName", "className", "testMethod", "flakiness-type", "smell-category" },
				projectName + "_results.csv");

		DocumentWriter.setup();

		List<TestFile> testFiles = new ArrayList<>();
		List<List<String>> outputData = new ArrayList<>();

		for (String path : testFilesPaths) {
			String className = TestFile.getTestFileName(path);

			TestFile testFile = new TestFile(projectName, path, "");

			System.out.println("Analyzing " + className);

			flakyDetector.detect(testFile);
			testFiles.add(testFile);

			List<String> fields = new ArrayList<>();
			for (AbstractSmell smellyInst : testFile.getSmellyInst()) {
				if (smellyInst.getHasSmell()) {
					for (TestSmell testSmell : smellyInst.getTestSmells()) {
						if (testSmell.isSmelly()) {
							fields.add(projectName); // Change to projectName
							fields.add(testSmell.getTestClass());
							fields.add(testSmell.getTestMethod());
							fields.add(testSmell.getSmellType());
							fields.add(testSmell.getFlakinessType());
							outputData.add(fields);
						}
						fields = new ArrayList<>();
					}
				}
			}

		}
		DocumentWriter.writeToCSV(outputData);

		long endTime = System.nanoTime();
		double duration = (endTime - startTime) / 1000000000;

		System.out.println("\nAnalysis took " + duration + " seconds and finished with " + outputData.size()
				+ " instances of test smells.");

	}

	private void replicateThesis() throws IOException {
		List<FlakyFile> flakytests = DocumentReader
				.adjustedReadFromCSV("C:\\Users\\Socke\\Documents\\Examensarbete\\adjustedClasses.csv");
		FlakyDetector flakyDetector = new FlakyDetector();
		DocumentWriter.createDocumentWriter(new String[] { "projectName", "className", "testMethod", "flakiness-type",
				"smell-category", "category" }, "flaky_tests_test_2_inspection_2.csv"); // finished_result.csv"
		DocumentWriter.setup();
		List<List<String>> outputData = new ArrayList<>();
		for (FlakyFile file : flakytests) {
			TestFile testFile = new TestFile(file.getTestName(), file.getPath(), "");
			flakyDetector.detect(testFile);
			ArrayList<String> fields = new ArrayList<String>();

			List<List<String>> data = DocumentWriter.prepareData(testFile);

			for (AbstractSmell test : testFile.getSmellyInst()) {

				for (TestSmell smell : test.getTestSmells()) {

					if (smell.getTestMethod() == null) {
						System.out.println("FOUND:" + smell.getTestClass());
					}

					if (test.getHasSmell() && smell.getTestMethod() != null && file.getTestMethod() != null) {
						if (test.getHasSmell() && smell.isSmelly()) {

							if (smell.getTestMethod().equals(file.getTestMethod())) {

								fields.add(Util.getGitName(file.getUrl()));
								fields.add(file.getClassName());
								fields.add(smell.getTestMethod());
								fields.add(smell.getFlakinessType());
								fields.add(smell.getSmellType());
								fields.add(file.getCategory());
								outputData.add(fields);
								fields = new ArrayList<String>();
							}
						}

					}

				}
			}

		}

		System.out.println(outputData.size());
		DocumentWriter.writeToCSV(outputData);
	}
}