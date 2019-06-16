package flakyhoover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import util.DocumentWriter;
import util.Util;

public class Main {

//	private static final String FLAKY_FILE_PATH = "src\\main\\java\\lab\\HFileArchiveTestingUtil.java";
//	private static final String FLAKY_FILE_PATH = "src\\main\\java\\lab\\JoinTest.java";
//	private static final String FLAKY_FILE_PATH = "src\\main\\java\\lab\\ClientRegistryTest.java";
//	private static final String FLAKY_FILE_PATH = "src\\main\\java\\lab\\TestFlakyExample.java";
	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestFlakyExample.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestGenerator.java";
//	private static final String TEST_CON_PATH = "src\\main\\java\\lab\\TestContent.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestHTMLLanguageParser.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestSubcollection.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestByteBloomFilter.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\HFileArchiveTestingUtil.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\TestSuffixURLFilter.java";
//	private static final String TEST_INDIRECT = "src\\main\\java\\lab\\IvyBuildListTest.java";

	private static FlakyDetector flakyDetector;
	private static DocumentWriter dw;
	private static final String[] smell_headers = { "project", "test-class", "test-method", "smell-type",
			"flakiness-type" };
	private static final String[] flaky_headers = { "project", "test-class", "test-method" };

//	private static final List<String> projects = new ArrayList<String>(
//			Arrays.asList( "cassandra", "derby", "hbase\\src\\main\\java\\org\\apache\\hadoop\\hbase", "ant-ivy","hive", "karaf","lucene-solr", "hibernate-orm","qpid", "jfreechart_v1.0.x","elasticsearch","pig","nutch"
//					, "wicket-1.4.x", "struts"));

	private static final List<String> projects = new ArrayList<String>(Arrays.asList(
			"hbase\\src\\test\\java\\org\\apache\\hadoop\\hbase", // Actual 699
			"hbase\\src\\main\\java\\org\\apache\\hadoop\\hbase", // Found 1287

			"pig\\src\\org\\apache\\pig", // Actual 922
			"pig\\test\\org\\apache\\pig", // Found 934

			"nutch\\src",

			"derby\\java\\testing", "derby\\java\\client", "derby\\java\\engine\\org\\apache\\derby\\impl",
			"derby\\java\\engine\\org\\apache\\derby\\jdbc",

			"elasticsearch\\src\\test", // Actual 2,265
			"elasticsearch\\src\\main\\java\\org\\elasticsearch", // Found 2,483

			"ant-ivy\\test\\java\\org\\apache\\ivy", "ant-ivy\\src\\java\\org\\apache\\ivy\\core",
			"ant-ivy\\src\\java\\org\\apache\\ivy\\plugins",

			"cassandra\\src\\java\\org\\apache\\cassandra", // Actual 586
			"cassandra\\test\\unit\\org\\apache\\cassandra", // Found 702

			"karaf\\admin", // 39!
			"karaf\\deployer", // 14!
			"karaf\\features", // 55!
			"karaf\\jaas", // 72!
			"karaf\\management", // 33!
			"karaf\\shell", // 232!
			"karaf\\tooling", // 63!
			"karaf\\util", // 11!
			"karaf\\webconsole", // 10!

			"hive",

			"qpid\\qpid\\java\\client", "qpid\\qpid\\java\\perftests", "qpid\\qpid\\java\\common",
			"qpid\\qpid\\java\\systests", "qpid\\qpid\\java\\broker\\src\\main\\java\\org\\apache\\qpid\\server",
			"qpid\\qpid\\java\\broker\\src\\test\\java\\org\\apache\\qpid\\server",

			"lucene-solr\\lucene\\core", "lucene-solr\\lucene\\backwards", "lucene-solr\\solr",

			"hibernate-orm\\hibernate-entitymanager\\src\\main\\java\\org",
			"hibernate-orm\\hibernate-entitymanager\\src\\test\\java\\org\\hibernate"

	));

//			
//	

//	https://stackoverflow.com/questions/8123058/passing-on-command-line-arguments-to-runnable-jar/8123262

//	TO-DO 
//	For each method, add line start (first occurence, and line end for the last occurence if something is smelly/flaky)
	// If check indexing an list, always check the length before:pp otherwise u get
	// an indexerror, not fun.

	private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

		boolean isTestClass = false;

		@Override
		public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {

//			if (!isTestClass) {
//				isTestClass = Util.isValidTestClass(n);
//			}

//			super.visit(n, collector);

			isTestClass = Util.isValidTestClass(n);
			super.visit(n, collector);

			isTestClass = false;

		}

		@Override
		public void visit(MethodDeclaration md, List<String> collector) {
			if (isTestClass) {
//				if (Util.isValidTestMethod(md)) {
				collector.add(md.getNameAsString());
//				}
			}
			super.visit(md, collector);

		}

	}

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

//		FileInputStream testFileInputStream = new FileInputStream("C:\\Users\\Socke\\Documents\\Examensarbete\\test_repos\\karaf\\archetypes\\blueprint\\src\\main\\resources\\archetype-resources\\src\\main\\java\\MyService.java");
//		CompilationUnit testFileCompilationUnit = StaticJavaParser.parse(testFileInputStream);

////		ArrayList<TestFile> testTestFiles = new ArrayList<TestFile>();
////		
//		Map<TestFile, ArrayList<String>> class_method = new HashMap<TestFile, ArrayList<String>>();
//
		ArrayList<TestFile> testFiles = new ArrayList<TestFile>();

//		for (String project : projects) {
//			ArrayList<String> allFiles = Util.getAllFiles(project);
//			System.out.println("allFiles: " + allFiles.size());
////
//			for (String path : allFiles) {
////
////			TestFile file = new TestFile(project, path, "");
//				TestFile file = new TestFile(project, path, "");
//				testFiles.add(file);
////				class_method.put(file, new ArrayList<>());
////				if (path.toLowerCase().contains("test")) {
////
//			}
//		}
////
////		8332 test
//
////		6987 //test//
//
//		// I found test 13913 test classes
//
//		System.out.println("testFiles size: " + testFiles.size());
//
//		List<String> methodNames = new ArrayList<>();
//		for (Map.Entry<TestFile, ArrayList<String>> entry : class_method.entrySet()) {
//			String file = entry.getKey().getTestFilePath();
//			ArrayList<String> methodNames3 = entry.getValue();
//
//			FileInputStream testFileInputStream = new FileInputStream(file);
//			CompilationUnit cu = StaticJavaParser.parse(testFileInputStream);
//			VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
//			methodNameCollector.visit(cu, methodNames3);
//		}
////
//		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
//		ArrayList<String> line = new ArrayList<String>();
//
//		for (Map.Entry<TestFile, ArrayList<String>> entry : class_method.entrySet()) {
//			String class_n = (String) entry.getKey().getProductionFileNameWithoutExtensiondot();
//			ArrayList<String> methodNames2 = entry.getValue();
//
//			for (String method : methodNames2) {
//				line.add("project");
//				line.add(class_n);
//				line.add(method);
//				data.add(line);
//				line = new ArrayList<String>();
//			}
//		}
//
//		System.out.println("SIZE OF testMethods: " + methodNames.size());
//		DocumentWriter.createDocumentWriter(smell_headers, "withoutdotjavaresults1339" + ".csv");
//		DocumentWriter.setup();
//		DocumentWriter.writeToCSV(data);

//		if(isTestClass) getAllFiles: 27265 methods, 13913 allclasses 
//		if(isTestClass) getAllFiles if(path.toLowerCase().contains("test")) : 27253 methods, 4175 testclasses
//		if(isTestClass) && isValidTestMethod getAllFiles if(path.toLowerCase().contains("test")) :  14952 test methods from  4175 test classes

//		fffff
//
//		flakyDetector = new FlakyDetector();
//		TestFile testfile = new TestFile("beaconsperth", TEST_INDIRECT, "");
////		TestFile testfile = new TestFile("beaconsperth", FLAKY_FILE_PATH, "");
//		testfile = flakyDetector.detect(testfile);
//
//		ArrayList<ArrayList<String>> data = DocumentWriter.prepareData(testfile);
//		for (ArrayList<String> arrayList : data) {
//
//			System.out.println("res: " + arrayList.toArray());
//			for (String s : arrayList) {
//				System.out.println("res: " + s);
//
//			}
//			System.out.println("");
//			System.out.println("");
//		}

		// Master
//		testfile.printResult();
//

//		DocumentWriter.createDocumentWriter(smell_headers, "flakyResults_2019-06-13" + ".csv");
//		DocumentWriter.setup();
//
//		for (TestFile testfile : testFiles) {
//
//			testfile = flakyDetector.detect(testfile);
//			ArrayList<ArrayList<String>> data = DocumentWriter.prepareData(testfile);
//			if (!data.isEmpty() || !data.equals("")) {
////				System.out.println("data success");
//				DocumentWriter.writeToCSV(data);
//			} else {
////				System.out.println("data fail");
//
//			}
//		}

//		evaluate();

	}

	public static void evaluate() {

//		List<List<String>> smells = DocumentWriter.readFromCSV("C:\\Users\\Socke\\Documents\\Github and gitlab repos\\flakiness-inducing-test-smells.csv",smell_headers);
		List<List<String>> smells = DocumentWriter.readFromCSV(
				"C:\\Users\\Socke\\Documents\\Examensarbete\\flaky csv\\flakiness-inducing-test-smells-removed-duplicates.csv",
				smell_headers);

//		List<List<String>> smells = DocumentWriter.readFromCSV("C:\\Users\\Socke\\Documents\\Examensarbete\\test_repos\\flakyResults.csv",
//				smell_headers);

//		List<List<String>> flaky_tests = DocumentWriter.readFromCSV("C:\\Users\\Socke\\Documents\\Github and gitlab repos\\list-of-flaky-tests.csv", flaky_headers);
		List<List<String>> flaky_tests = DocumentWriter.readFromCSV(
				"C:\\Users\\Socke\\Documents\\Examensarbete\\flaky csv\\list-of-flaky-tests-removed-duplicates.csv",
				flaky_headers);

//		precision 	 57.20 % 
//		recall    	 72.05 % 
//		specificity  55.54 % 
//		accuracy 	 63.00 % 

		List<String> smells_list = new ArrayList<String>();
		List<String> flakes_List = new ArrayList<String>();

		for (List<String> flake : flaky_tests) {

			String test_class_path = flake.get(1);
			String test_class_flake_name = Util.getTestFileName(flake.get(1));
			String test_method_flake = flake.get(2);
			String class_method_flake = test_class_path + "." + test_method_flake;
			flakes_List.add(class_method_flake);

		}

		for (List<String> smell : smells) {

			String test_class_path = smell.get(1);
			String test_class_name = Util.getTestFileName(smell.get(1));
			String test_method = smell.get(2);
			String class_method_smell = test_class_path + "." + test_method;
			smells_list.add(class_method_smell);
		}
		System.out.println(smells_list.size());
		System.out.println(flakes_List.size());

		List<String> intersection = Util.intersection(smells_list, flakes_List);
		System.out.println(intersection.size());

		int positive = 11120;
		int tp = intersection.size();

		Util.evaluate(positive, tp);
	}

}

//Evaluate
//List<List<String>> smells = DocumentWriter.readFromCSV(
//		"C:\\Users\\Socke\\Documents\\Github and gitlab repos\\flakiness-inducing-test-smells.csv",
//		smell_headers);
//List<List<String>> flaky_tests = DocumentWriter.readFromCSV(
//		"C:\\Users\\Socke\\Documents\\Github and gitlab repos\\list-of-flaky-tests.csv", flaky_headers);
//
//
//int i = 0;
//
//List<String> smells_list = new ArrayList<String>();
//List<String> flakes_List = new ArrayList<String>();
//
//
//for (List<String> flake : flaky_tests) {
//	
//
//	String test_class_path = flake.get(1);
//	String test_class_flake_name = Util.getTestFileName(flake.get(1));
//	String test_method_flake = flake.get(2);
////	String class_method_flake = test_class_flake_name + "." + test_method_flake;
//	String class_method_flake = test_class_path + "." + test_method_flake;
//	flakes_List.add(class_method_flake);
//	
//}
//
//for (List<String> smell : smells) {
//
//	String test_class_path = smell.get(1);
//	String test_class_name = Util.getTestFileName(smell.get(1));
//	String test_method = smell.get(2);
////	String class_method_smell = test_class_name + "." + test_method;
//	String class_method_smell = test_class_path + "." + test_method;
//	smells_list.add(class_method_smell);
//}
//System.out.println(smells_list.size());
//System.out.println(flakes_List.size());
//
//List<String> intersection = Util.intersection(smells_list, flakes_List);
//System.out.println(intersection.size());

//
//int positive = 11120;
//int tp = intersection.size();
//
//Util.evaluate(positive, tp);
////Evaluate

//ArrayList<ArrayList<String>> data = DocumentWriter.prepareData(testfile);

//flakyDetector = new FlakyDetector();
//for (TestFile testfile : testFiles) {
//	testfile = flakyDetector.detect(testfile);
//	ArrayList<String> data = DocumentWriter.prepareData(testfile);
//	if(!data.isEmpty()) {
//		DocumentWriter.writeToCSV(data);
//	}
//}

//File f  = new File("new.txt");
//if(!f.exists()) {
//	FileWriter fw = new FileWriter(f);
//
//}
//File filePath = new File(f.getAbsolutePath());
//System.out.println("RES: " + filePath.exists());
//
//if(filePath.exists()) {
//	System.out.println("RES: " + filePath.delete());
//
//}
////ArrayList<String> data = new ArrayList<>(Arrays.asList("apache-cassandra-1.1", "org.apache.cassandra.utils.IntervalTest", "testIntersects","fire-and-forget", "async-wait"));

// Hämta alla filer för ett projekt
// Konvertera till en lista med TestFiles
// För varje test file kör en specifik flake

//För varje smell:
//För varje fil f:
//	list_smells = detect(file, smell)
//	insert list_smells to csv File.class 

//TestFile testfile = new TestFile("beaconsperth", TEST_INDIRECT, "");
//TestFile testfile = new TestFile("beaconsperth", TEST_GEN_PATH, "");
//TestFile testfile = new TestFile("beaconsperth", TEST_CON_PATH, "");

//
//for (AbstractFlaky flaky : testfile.getFlakyInst()) {
//	if (flaky.getFlakyName().contains("Resource Optimism")) {
//		ArrayList<TestSmell> smells = flaky.getTestSmells();
//		for (TestSmell smell : smells) {
//			System.out.println(smell.getFlakinessType());
//			System.out.println(smell.getProject());
//			System.out.println(smell.getSmellType());
//			System.out.println(smell.getTestClass());
//			System.out.println(smell.getTestMethod());
//		}
//	}
//}

//ArrayList<TestSmell> smells = testfile.getTestSmell("Resource Optimism");

//testfile = flakyDetector.detectOneSmell(testfile, "Resource Optimism");

//System.out.println("status: " + smells.isEmpty());
//
//for (TestSmell s : smells) {
//	System.out.println(s.getFlakinessType());
//	System.out.println(s.getProject());
//	System.out.println(s.getSmellType());
//	System.out.println(s.getTestClass());
//	System.out.println(s.getTestMethod());
//}

//

//{"project", "test-class",	"test-method",	"smell-type",	"flakiness-type"};

// Bugg hittar saker i TestByteBloomFilter när jag kör .jar men inte i eclipse
// lol. Beror på Jclasses duuh (FIXAT)
// fixa test run war.... om två metoder i samma klass kör samma metod då ere
// knas men borde inte vara så om inte metoderna är klassade som test run
// war:oooo
