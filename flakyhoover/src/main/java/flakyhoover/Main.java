package flakyhoover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import util.DocumentWriter;
import util.Util;

public class Main {

//	private static final String FLAKY_FILE_PATH = "src\\main\\java\\lab\\TestFlakyExample.java";
//	private static final String TEST_GEN_PATH = "src\\main\\java\\lab\\TestGenerator.java";
//	private static final String TEST_CON_PATH = "src\\main\\java\\lab\\TestContent.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestHTMLLanguageParser.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestSubcollection.java";
	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestSuffixURLFilter.java";
	private static FlakyDetector flakyDetector;
	private static DocumentWriter dw;
	private static final String[] headers = { "project", "test-class", "test-method", "smell-type", "flakiness-type" };
//	https://stackoverflow.com/questions/8123058/passing-on-command-line-arguments-to-runnable-jar/8123262

//	TO-DO 
//	For each method, add line start (first occurence, and line end for the last occurence if something is smelly/flaky)

	public static void main(String[] args)
			throws IOException, ParserConfigurationException, TransformerException, SAXException {
//		String pathToProject = null;
//		String projectName = null;
//		try {
//			pathToProject = args[0];
//			projectName = args[1];
//		} catch (Exception e) {
//			System.out.println("Missing parameters: " + e);
//		}

//		ArrayList<TestFile> testFiles = new ArrayList<TestFile>();
//		ArrayList<String> projectFiles = Util.getAllFiles(pathToProject);
//		for (String path : projectFiles) {
//			testFiles.add(new TestFile(projectName, path, ""));
//
//		}

//		File f  = new File("new.txt");
//		if(!f.exists()) {
//			FileWriter fw = new FileWriter(f);
//
//		}
//		File filePath = new File(f.getAbsolutePath());
//		System.out.println("RES: " + filePath.exists());
//
//		if(filePath.exists()) {
//			System.out.println("RES: " + filePath.delete());
//
//		}
//		

//		TestFile testfile = new TestFile("beaconsperth", TEST_INDIRECT, "");
//
//		flakyDetector = new FlakyDetector();
//
//		testfile = flakyDetector.detect(testfile);
//		testfile.printResult();

		DocumentWriter.createDocumentWriter(headers, "projectName" + ".csv");
		DocumentWriter.setup();
		
////		ArrayList<String> data = new ArrayList<>(Arrays.asList("apache-cassandra-1.1", "org.apache.cassandra.utils.IntervalTest", "testIntersects","fire-and-forget", "async-wait"));
//
////
	
//
//		flakyDetector = new FlakyDetector();
//		for (TestFile testfile : testFiles) {
//			testfile = flakyDetector.detect(testfile);
//			ArrayList<String> data = DocumentWriter.prepareData(testfile);
//			if(!data.isEmpty()) {
//				DocumentWriter.writeToCSV(data);
//			}
//		}

		// Hämta alla filer för ett projekt
		// Konvertera till en lista med TestFiles
		// För varje test file kör en specifik flake

//		För varje smell:
//		För varje fil f:
//			list_smells = detect(file, smell)
//			insert list_smells to csv File.class 

//		TestFile testfile = new TestFile("beaconsperth", TEST_INDIRECT, "");
//		TestFile testfile = new TestFile("beaconsperth", TEST_GEN_PATH, "");
//		TestFile testfile = new TestFile("beaconsperth", TEST_CON_PATH, "");

//
//		for (AbstractFlaky flaky : testfile.getFlakyInst()) {
//			if (flaky.getFlakyName().contains("Resource Optimism")) {
//				ArrayList<TestSmell> smells = flaky.getTestSmells();
//				for (TestSmell smell : smells) {
//					System.out.println(smell.getFlakinessType());
//					System.out.println(smell.getProject());
//					System.out.println(smell.getSmellType());
//					System.out.println(smell.getTestClass());
//					System.out.println(smell.getTestMethod());
//				}
//			}
//		}

//		ArrayList<TestSmell> smells = testfile.getTestSmell("Resource Optimism");

//		testfile = flakyDetector.detectOneSmell(testfile, "Resource Optimism");

//		System.out.println("status: " + smells.isEmpty());
//		
//		for (TestSmell s : smells) {
//			System.out.println(s.getFlakinessType());
//			System.out.println(s.getProject());
//			System.out.println(s.getSmellType());
//			System.out.println(s.getTestClass());
//			System.out.println(s.getTestMethod());
//		}

//		

//		{"project", "test-class",	"test-method",	"smell-type",	"flakiness-type"};

	}

}
