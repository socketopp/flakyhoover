package flakyhoover;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import flakes.ConditionalTestLogic;
import flakes.FireAndForget;
import flakes.IndirectTesting;
import flakes.ResourceOptimism;
import flakes.SharedFixture;
import junit.framework.TestCase;
import util.TestSmell;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest extends TestCase {
	/**
	 * Rigorous Test :-)
	 */

	private static final String FIRE_AND_FORGET = "src\\main\\resources\\testfiles\\FireAndForgetTest.java";

	private static final String CONDITIONAL_TEST_LOGIC = "src\\main\\resources\\testfiles\\TestConditionalTestLogic.java";

	private static final String TEST_RUN_WAR_1 = "src\\main\\resources\\testfiles\\TestRunWarTest.java";

	// FROM:
	// https://github.com/apache/nutch/blob/branch-1.4/src/test/org/apache/nutch/crawl/TestGenerator.java
	private static final String TEST_RUN_WAR_2 = "src\\main\\resources\\testfiles\\TestGeneratorTest.java";

	// FROM:
	// https://raw.githubusercontent.com/apache/nutch/branch-1.4/src/plugin/urlfilter-suffix/src/test/org/apache/nutch/urlfilter/suffix/TestSuffixURLFilter.java
	private static final String TEST_RUN_WAR_3 = "src\\main\\resources\\testfiles\\TestSuffixURLFilter.java";

	// FROM:
	// https://github.com/apache/nutch/blob/branch-1.4/src/test/org/apache/nutch/plugin/TestPluginSystem.java
	private static final String TEST_RUN_WAR_4 = "src\\main\\resources\\testfiles\\TestPluginSystem.java";
	// FROM:

	// https://fossies.org/linux/misc/db-derby-10.15.1.3-src.tar.gz/db-derby-10.15.1.3-src/java/org.apache.derby.tests/org/apache/derbyTesting/functionTests/tests/lang/JoinTest.java
	private static final String TEST_RUN_WAR_5 = "src\\main\\resources\\testfiles\\JoinTest.java";

	private static final String TEST_RUN_WAR_6 = "src\\main\\resources\\testfiles\\TestRunWar6.java";

	private static final String TEST_RESOURCE_OPTIMISM = "src\\main\\resources\\testfiles\\ResourceOptimismTest.java";

	private static final String TEST_JAR_VISITOR = "src\\main\\resources\\testfiles\\JarVisitorTest.java";

	// https://github.com/apache/nutch/blob/branch-1.4/src/plugin/subcollection/src/test/org/apache/nutch/collection/TestSubcollection.java
	private static final String TEST_SUBCOLLECTION = "src\\main\\resources\\testfiles\\TestSubcollection.java";

	// https://github.com/apache/nutch/blob/branch-1.4/src/plugin/index-more/src/test/org/apache/nutch/indexer/more/TestMoreIndexingFilter.java
	private static final String TEST_MORE_INDEXING_FILTER = "src\\main\\resources\\testfiles\\TestMoreIndexingFilter.java";

	// https://raw.githubusercontent.com/WING-NUS/Kairos/master/src/plugin/languageidentifier/src/test/org/apache/nutch/analysis/lang/TestNGramProfile.java
	private static final String TEST_NGRAM_PROFILE = "src\\main\\resources\\testfiles\\TestNGramProfile.java";

	// https://raw.githubusercontent.com/apache/ant-ivy/2.1.x/test/java/org/apache/ivy/ant/IvyCleanCacheTest.java
	private static final String TEST_IVY_CLEAN_CACHE = "src\\main\\resources\\testfiles\\IvyCleanCacheTest.java";

	// https://github.com/apache/hbase/blob/0.94/src/test/java/org/apache/hadoop/hbase/filter/TestColumnPaginationFilter.java
	private static final String TEST_COLUMN_PAGNATION_FILTER = "src\\main\\resources\\testfiles\\TestColumnPaginationFilter.java";

	// https://github.com/apache/hbase/blob/0.94/src/test/java/org/apache/hadoop/hbase/util/TestByteBloomFilter.java
	private static final String TEST_BYTE_BLOOM_FILTER = "src\\main\\resources\\testfiles\\TestByteBloomFilter.java";

	// https://github.com/apache/cassandra/blob/cassandra-1.1/test/unit/org/apache/cassandra/utils/IntervalTest.java
	private static final String TEST_INTERVAL_TREE = "src\\main\\resources\\testfiles\\IntervalTreeTest.java";

	private static final String TEST_HTML_LANGUAGE_PARSER = "src\\main\\resources\\testfiles\\TestHTMLLanguageParser.java";

	@Test
	public void testResourceOptimism() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new ResourceOptimism());
		TestFile testFile = new TestFile("flakyhoover", TEST_RESOURCE_OPTIMISM, "");
		testFile = flakyDetector.detect(testFile);

//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

		String testPages = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String assertLink = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String test = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testHandleFeatures = testFile.getSmellyInst().get(0).getTestSmells().get(3).getTestMethod();
		String testPackagesExtraction = testFile.getSmellyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testNoDataEntry = testFile.getSmellyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testFailureGetNonExistentMetaBlock = testFile.getSmellyInst().get(0).getTestSmells().get(6)
				.getTestMethod();
		String testBagConstantAccess = testFile.getSmellyInst().get(0).getTestSmells().get(7).getTestMethod();
		String testBigGroupAllWithNull = testFile.getSmellyInst().get(0).getTestSmells().get(8).getTestMethod();
		String saveImage_noImageFile_ko = testFile.getSmellyInst().get(0).getTestSmells().get(9).getTestMethod();
		String relationFunction = testFile.getSmellyInst().get(0).getTestSmells().get(10).getTestMethod();
		String readContents = testFile.getSmellyInst().get(0).getTestSmells().get(11).getTestMethod();
		String classVariableExample = testFile.getSmellyInst().get(0).getTestSmells().get(12).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 13;

		assertEquals(actual, expected);

		assertEquals(testPages, "testPages");
		assertEquals(assertLink, "assertLink");
		assertEquals(test, "test");
		assertEquals(testHandleFeatures, "testHandleFeatures");
		assertEquals(testPackagesExtraction, "testPackagesExtraction");
		assertEquals(testNoDataEntry, "testNoDataEntry");
		assertEquals(testFailureGetNonExistentMetaBlock, "testFailureGetNonExistentMetaBlock");
		assertEquals(testBagConstantAccess, "testBagConstantAccess");
		assertEquals(testBigGroupAllWithNull, "testBigGroupAllWithNull");
		assertEquals(saveImage_noImageFile_ko, "saveImage_noImageFile_ko");
		assertEquals(relationFunction, "relationFunction");
		assertEquals(readContents, "readContents");
		assertEquals(classVariableExample, "classVariableExample");
	}

	@Test
	public void testResourceOptimismOnGeneratorFile() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new ResourceOptimism());

		// Both test run war and resource optimism use same file
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_2, "");
		testFile = flakyDetector.detect(testFile);

		String setUp = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String tearDown = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String delete = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testGenerateHighest = testFile.getSmellyInst().get(0).getTestSmells().get(3).getTestMethod();
		String testGenerateHostLimit = testFile.getSmellyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testGenerateDomainLimit = testFile.getSmellyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testFilter = testFile.getSmellyInst().get(0).getTestSmells().get(6).getTestMethod();
		String readContents = testFile.getSmellyInst().get(0).getTestSmells().get(7).getTestMethod();
		String generateFetchlist = testFile.getSmellyInst().get(0).getTestSmells().get(8).getTestMethod();
		String createCrawlDB = testFile.getSmellyInst().get(0).getTestSmells().get(9).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 10;

		assertEquals(actual, expected);

		assertEquals(setUp, "setUp");
		assertEquals(tearDown, "tearDown");
		assertEquals(delete, "delete");
		assertEquals(testGenerateHighest, "testGenerateHighest");
		assertEquals(testGenerateHostLimit, "testGenerateHostLimit");
		assertEquals(testGenerateDomainLimit, "testGenerateDomainLimit");
		assertEquals(testFilter, "testFilter");
		assertEquals(readContents, "readContents");
		assertEquals(generateFetchlist, "generateFetchlist");
		assertEquals(createCrawlDB, "createCrawlDB");

	}

	@Test
	public void testSharedFixture1() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_1, "");
		testFile = flakyDetector.detect(testFile);

//		"serviceTopPartitionsNoArg"
//		"testServiceTopPartitionsNoArg",

		String basicProxyAuthentication = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String getReader = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testRegisterClient = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testRejectsDuplicateClientNames = testFile.getSmellyInst().get(0).getTestSmells().get(3).getTestMethod();
		String testAwaitOneClientWhenClientNotRegistered = testFile.getSmellyInst().get(0).getTestSmells().get(4)
				.getTestMethod();
		String testAwaitOneClientWhenClientAlreadyRegistered = testFile.getSmellyInst().get(0).getTestSmells().get(5)
				.getTestMethod();
		String testAwaitTwoClientWhenClientRegistersWhilstWaiting = testFile.getSmellyInst().get(0).getTestSmells()
				.get(6).getTestMethod();
		String registerClientLater = testFile.getSmellyInst().get(0).getTestSmells().get(7).getTestMethod();
		String shakespeare = testFile.getSmellyInst().get(0).getTestSmells().get(8).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 9;

		assertEquals(actual, expected);

		assertEquals(basicProxyAuthentication, "basicProxyAuthentication");
		assertEquals(getReader, "getReader");
		assertEquals(testRegisterClient, "testRegisterClient");
		assertEquals(testRejectsDuplicateClientNames, "testRejectsDuplicateClientNames");
		assertEquals(testAwaitOneClientWhenClientNotRegistered, "testAwaitOneClientWhenClientNotRegistered");
		assertEquals(testAwaitOneClientWhenClientAlreadyRegistered, "testAwaitOneClientWhenClientAlreadyRegistered");
		assertEquals(testAwaitTwoClientWhenClientRegistersWhilstWaiting,
				"testAwaitTwoClientWhenClientRegistersWhilstWaiting");
		assertEquals(registerClientLater, "registerClientLater");
		assertEquals(shakespeare, "shakespeare");

	}

	@Test
	public void testSharedFixture2() throws FileNotFoundException {

		ArrayList<String> assertions = new ArrayList<String>(Arrays.asList("setUp", "tearDown", "delete",
				"testGenerateHighest", "testGenerateHostLimit", "testGenerateDomainLimit", "testFilter", "readContents",
				"generateFetchlist", "createCrawlDB", "createURLCrawlDatum"));

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_2, "");
		testFile = flakyDetector.detect(testFile);

//		for (int i = 0; i < testFile.getFlakyInst().get(0).getTestSmells().size(); i++) {
//			System.out.println("test: " + testFile.getFlakyInst().get(0).getTestSmells().get(i).getTestMethod());
//			System.out.println("assert: " + assertions.get(i));
//		}

		assertEquals(assertions.size(), testFile.getSmellyInst().get(0).getTestSmells().size());

		for (int i = 0; i < testFile.getSmellyInst().size(); i++) {
			String ts = testFile.getSmellyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}
	}

	@Test
	public void testSharedFixture3() throws FileNotFoundException {

		ArrayList<String> assertions = new ArrayList<String>(
				Arrays.asList("setUp", "testModeAccept", "testModeReject", "testModeAcceptIgnoreCase",
						"testModeRejectIgnoreCase", "testModeAcceptAndNonPathFilter", "testModeAcceptAndPathFilter"));

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_3, "");
		testFile = flakyDetector.detect(testFile);

		assertEquals(assertions.size(), testFile.getSmellyInst().get(0).getTestSmells().size());
		for (int i = 0; i < testFile.getSmellyInst().size(); i++) {
			String ts = testFile.getSmellyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}
	}

	@Test
	public void testSharedFixture4() throws FileNotFoundException {

		ArrayList<String> assertions = new ArrayList<String>(Arrays.asList("setUp", "tearDown", "testLoadPlugins",
				"testGetExtensionAndAttributes", "testGetExtensionInstances", "testGetClassLoader", "testGetResources",
				"getPluginFolder", "createDummyPlugins", "testPluginConfiguration"));

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_4, "");
		testFile = flakyDetector.detect(testFile);

		for (int i = 0; i < testFile.getSmellyInst().size(); i++) {
			String ts = testFile.getSmellyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}

	}

	@Test
	public void testSharedFixture5() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_5, "");
		testFile = flakyDetector.detect(testFile);
//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}
		assertTrue(testFile.getSmellyInst().get(0).getTestSmells().isEmpty());
	}

	@Test
	public void testSharedFixture6() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_6, "");
		testFile = flakyDetector.detect(testFile);
//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

		assertTrue(testFile.getSmellyInst().get(0).getTestSmells().isEmpty());
	}

	@Test
	public void testFireAndForget() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new FireAndForget());
		TestFile testFile = new TestFile("flakyhoover", FIRE_AND_FORGET, "");
		testFile = flakyDetector.detect(testFile);

		String testRsReportsWrongServerName = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testWriteToDB = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testServiceTopPartitionsNoArg = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testContinuousScheduling = testFile.getSmellyInst().get(0).getTestSmells().get(3).getTestMethod();
		String niftyTest = testFile.getSmellyInst().get(0).getTestSmells().get(4).getTestMethod();

//		String asyncIssue = testFile.getFlakyInst().get(0).getTestSmells().get(5).getTestMethod();

		for (TestSmell ts : testFile.getSmellyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 5;
		assertEquals(actual, expected);

		assertEquals(testRsReportsWrongServerName, "testRsReportsWrongServerName");
		assertEquals(testWriteToDB, "testWriteToDB");
		assertEquals(testContinuousScheduling, "testContinuousScheduling");
		assertEquals(testServiceTopPartitionsNoArg, "testServiceTopPartitionsNoArg");
		assertEquals(niftyTest, "niftyTest");

	}

	@Test
	public void testConditionalTestLogic() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new ConditionalTestLogic());
		TestFile testFile = new TestFile("flakyhoover", CONDITIONAL_TEST_LOGIC, "");
		testFile = flakyDetector.detect(testFile);

		String testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody = testFile.getSmellyInst().get(0)
				.getTestSmells().get(0).getTestMethod();

		String conditionalVerificationLogic = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testDisplayCurrentTime_whenever = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testGetFlightsByOrigin_NoInboundFlight_SMRTD = testFile.getSmellyInst().get(0).getTestSmells().get(3)
				.getTestMethod();
		String testCombinationsOfInputValues = testFile.getSmellyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testMultipleValueSets = testFile.getSmellyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testSpinner = testFile.getSmellyInst().get(0).getTestSmells().get(6).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 7;

		assertEquals(actual, expected);

//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

		assertEquals(testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody,
				"testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody");
		assertEquals(testDisplayCurrentTime_whenever, "testDisplayCurrentTime_whenever");
		assertEquals(conditionalVerificationLogic, "conditionalVerificationLogic");
		assertEquals(testGetFlightsByOrigin_NoInboundFlight_SMRTD, "testGetFlightsByOrigin_NoInboundFlight_SMRTD");
		assertEquals(testCombinationsOfInputValues, "testCombinationsOfInputValues");
		assertEquals(testMultipleValueSets, "testMultipleValueSets");
		assertEquals(testSpinner, "testSpinner");

	}

	@Test
	public void testIndirectTestingJarVisitor() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_JAR_VISITOR, "");
		testFile = flakyDetector.detect(testFile);

		String testJarVisitorFactoryNested = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testJarVisitorFactory = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String relationTest = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 3;

		for (TestSmell ts : testFile.getSmellyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

		assertEquals(actual, expected);

		assertEquals(testJarVisitorFactoryNested, "testJarVisitorFactoryNested");

		assertEquals(testJarVisitorFactory, "testJarVisitorFactory");
		assertEquals(relationTest, "relationTest");

	}

	@Test
	public void testIndirectTestingSubCollection() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_SUBCOLLECTION, "");
		testFile = flakyDetector.detect(testFile);

		String testFilter = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testInput = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 2;

		assertEquals(actual, expected);

		assertEquals(testFilter, "testFilter");
		assertEquals(testInput, "testInput");

	}

	@Test
	public void testIndirectTestingTestMoreIndexingFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_MORE_INDEXING_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		String testContentType = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testNoParts = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String assertContentType = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 3;

		assertEquals(actual, expected);

		assertEquals(testContentType, "testContentType");
		assertEquals(testNoParts, "testNoParts");
		assertEquals(assertContentType, "assertContentType");

	}

	@Test
	public void testIndirectTestingTestNGramProfile() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_NGRAM_PROFILE, "");
		testFile = flakyDetector.detect(testFile);

		String testContents = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testCounts = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testGetSorted = testFile.getSmellyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testIO = testFile.getSmellyInst().get(0).getTestSmells().get(3).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 4;

		assertEquals(actual, expected);

		assertEquals(testContents, "testContents");
		assertEquals(testCounts, "testCounts");
		assertEquals(testGetSorted, "testGetSorted");
		assertEquals(testIO, "testIO");

	}

	@Test
	public void testIndirectTestingTestIvyCleanCacheTest() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_IVY_CLEAN_CACHE, "");
		testFile = flakyDetector.detect(testFile);

		String setUp = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 1;

		assertEquals(actual, expected);

		assertEquals(setUp, "setUp");
	}

	@Test
	public void testIndirectTestingTestColumnPaginationFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_COLUMN_PAGNATION_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		ArrayList<TestSmell> smells = testFile.getSmellyInst().get(0).getTestSmells();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 0;
		assertEquals(actual, expected);

		assertTrue(smells.isEmpty());
	}

	@Test
	public void testIndirectTestingTestSuffixURLFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_3, "");
		testFile = flakyDetector.detect(testFile);

		ArrayList<TestSmell> smells = testFile.getSmellyInst().get(0).getTestSmells();
		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 0;
		assertEquals(actual, expected);

		assertTrue(smells.isEmpty());
	}

	@Test
	public void testIndirectTestingTestByteBloomFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_BYTE_BLOOM_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		String testBloomFold = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testBloomPerf = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();

		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 2;
		assertEquals(actual, expected);

		assertEquals(testBloomFold, "testBloomFold");
		assertEquals(testBloomPerf, "testBloomPerf");
	}

	@Test
	public void testIndirectTestingTestIntervalTree() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_INTERVAL_TREE, "");
		testFile = flakyDetector.detect(testFile);

		String testSearch = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String randomTestForIndirectTesting = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 2;
		assertEquals(actual, expected);

		assertEquals(testSearch, "testSearch");
		assertEquals(randomTestForIndirectTesting, "randomTestForIndirectTesting");
	}

	@Test
	public void testIndirectTestingTestHTMLLanguageParser() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_HTML_LANGUAGE_PARSER, "");
		testFile = flakyDetector.detect(testFile);

		String testMetaHTMLParsing = testFile.getSmellyInst().get(0).getTestSmells().get(0).getTestMethod();
		String getContent = testFile.getSmellyInst().get(0).getTestSmells().get(1).getTestMethod();
		int actual = testFile.getSmellyInst().get(0).getTestSmells().size();
		int expected = 2;
		assertEquals(actual, expected);

		assertEquals(testMetaHTMLParsing, "testMetaHTMLParsing");
		assertEquals(getContent, "getContent");
	}

//	@Test
//	public void testFindAllReferences() throws FileNotFoundException {
//
//		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
//		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR, "");
//		testFile = flakyDetector.detect(testFile);
//		
//
//	}

//	@Test
//	public void shouldAnswerWithTrue() {
//		assertTrue(1 == 2);
//	}

}
