package flakyhoover;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		String testPages = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String assertLink = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String test = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testHandleFeatures = testFile.getFlakyInst().get(0).getTestSmells().get(3).getTestMethod();
		String testPackagesExtraction = testFile.getFlakyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testNoDataEntry = testFile.getFlakyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testFailureGetNonExistentMetaBlock = testFile.getFlakyInst().get(0).getTestSmells().get(6)
				.getTestMethod();
		String testBagConstantAccess = testFile.getFlakyInst().get(0).getTestSmells().get(7).getTestMethod();
		String testBigGroupAllWithNull = testFile.getFlakyInst().get(0).getTestSmells().get(8).getTestMethod();
		String saveImage_noImageFile_ko = testFile.getFlakyInst().get(0).getTestSmells().get(9).getTestMethod();
		String relationFunction = testFile.getFlakyInst().get(0).getTestSmells().get(10).getTestMethod();
		String readContents = testFile.getFlakyInst().get(0).getTestSmells().get(11).getTestMethod();
		String classVariableExample = testFile.getFlakyInst().get(0).getTestSmells().get(12).getTestMethod();

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

		String setUp = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String tearDown = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String delete = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testGenerateHighest = testFile.getFlakyInst().get(0).getTestSmells().get(3).getTestMethod();
		String testGenerateHostLimit = testFile.getFlakyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testGenerateDomainLimit = testFile.getFlakyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testFilter = testFile.getFlakyInst().get(0).getTestSmells().get(6).getTestMethod();
		String readContents = testFile.getFlakyInst().get(0).getTestSmells().get(7).getTestMethod();
		String generateFetchlist = testFile.getFlakyInst().get(0).getTestSmells().get(8).getTestMethod();
		String createCrawlDB = testFile.getFlakyInst().get(0).getTestSmells().get(9).getTestMethod();

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
	public void testSharedFixture() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_1, "");
		testFile = flakyDetector.detect(testFile);

//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

		List<String> assertions = new ArrayList<String>(Arrays.asList("basicProxyAuthentication", "getReader",
				"testServiceTopPartitionsNoArg", "serviceTopPartitionsNoArg", "testRegisterClient",
				"testRejectsDuplicateClientNames", "testAwaitOneClientWhenClientNotRegistered",
				"testAwaitOneClientWhenClientAlreadyRegistered", "testAwaitTwoClientWhenClientRegistersWhilstWaiting",
				"registerClientLater", "doLater", "shakespeare"));

		for (int i = 0; i < testFile.getFlakyInst().size(); i++) {
			String ts = testFile.getFlakyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}

		assertions = new ArrayList<String>(
				Arrays.asList("setUp", "tearDown", "delete", "testGenerateHighest", "testGenerateHostLimit",
						"testGenerateDomainLimit", "testFilter", "readContents", "generateFetchlist", "createCrawlDB"));

		flakyDetector = new FlakyDetector(true, new SharedFixture());
		testFile = new TestFile("flakyhoover", TEST_RUN_WAR_2, "");
		testFile = flakyDetector.detect(testFile);

		for (int i = 0; i < testFile.getFlakyInst().size(); i++) {
			String ts = testFile.getFlakyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}

		assertions = new ArrayList<String>(
				Arrays.asList("setUp", "testModeAccept", "testModeReject", "testModeAcceptIgnoreCase",
						"testModeRejectIgnoreCase", "testModeAcceptAndNonPathFilter", "testModeAcceptAndPathFilter"));

		flakyDetector = new FlakyDetector(true, new SharedFixture());
		testFile = new TestFile("flakyhoover", TEST_RUN_WAR_3, "");
		testFile = flakyDetector.detect(testFile);

		for (int i = 0; i < testFile.getFlakyInst().size(); i++) {
			String ts = testFile.getFlakyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}

		assertions = new ArrayList<String>(Arrays.asList("setUp", "tearDown", "testLoadPlugins",
				"testGetExtensionAndAttributes", "testGetExtensionInstances", "testGetClassLoader", "testGetResources",
				"getPluginFolder", "createDummyPlugins", "testPluginConfiguration"));

		flakyDetector = new FlakyDetector(true, new SharedFixture());
		testFile = new TestFile("flakyhoover", TEST_RUN_WAR_4, "");
		testFile = flakyDetector.detect(testFile);

		for (int i = 0; i < testFile.getFlakyInst().size(); i++) {
			String ts = testFile.getFlakyInst().get(0).getTestSmells().get(i).getTestMethod();
			assertEquals(ts, assertions.get(i));
		}

		flakyDetector = new FlakyDetector(true, new SharedFixture());
		testFile = new TestFile("flakyhoover", TEST_RUN_WAR_5, "");
		testFile = flakyDetector.detect(testFile);
		assertTrue(testFile.getFlakyInst().get(0).getTestSmells().isEmpty());

	}

	@Test
	public void testFireAndForget() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new FireAndForget());
		TestFile testFile = new TestFile("flakyhoover", FIRE_AND_FORGET, "");
		testFile = flakyDetector.detect(testFile);

		String testRsReportsWrongServerName = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testWriteToDB = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testServiceTopPartitionsNoArg = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testContinuousScheduling = testFile.getFlakyInst().get(0).getTestSmells().get(3).getTestMethod();
		String asyncIssue = testFile.getFlakyInst().get(0).getTestSmells().get(4).getTestMethod();
		String niftyTest = testFile.getFlakyInst().get(0).getTestSmells().get(5).getTestMethod();

//		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

		assertEquals(testRsReportsWrongServerName, "testRsReportsWrongServerName");
		assertEquals(testContinuousScheduling, "testContinuousScheduling");
		assertEquals(testWriteToDB, "testWriteToDB");
		assertEquals(testServiceTopPartitionsNoArg, "testServiceTopPartitionsNoArg");
		assertEquals(niftyTest, "niftyTest");
		assertEquals(asyncIssue, "asyncIssue");

	}

	@Test
	public void testConditionalTestLogic() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new ConditionalTestLogic());
		TestFile testFile = new TestFile("flakyhoover", CONDITIONAL_TEST_LOGIC, "");
		testFile = flakyDetector.detect(testFile);

		String testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody = testFile.getFlakyInst().get(0)
				.getTestSmells().get(0).getTestMethod();

		String conditionalVerificationLogic = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testDisplayCurrentTime_whenever = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testGetFlightsByOrigin_NoInboundFlight_SMRTD = testFile.getFlakyInst().get(0).getTestSmells().get(3)
				.getTestMethod();
		String testCombinationsOfInputValues = testFile.getFlakyInst().get(0).getTestSmells().get(4).getTestMethod();
		String testMultipleValueSets = testFile.getFlakyInst().get(0).getTestSmells().get(5).getTestMethod();
		String testSpinner = testFile.getFlakyInst().get(0).getTestSmells().get(6).getTestMethod();

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

		String testJarVisitorFactoryNested = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testJarVisitorFactory = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();

		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

		assertEquals(testJarVisitorFactory, "testJarVisitorFactory");
		assertEquals(testJarVisitorFactoryNested, "testJarVisitorFactoryNested");

	}

	@Test
	public void testIndirectTestingSubCollection() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_SUBCOLLECTION, "");
		testFile = flakyDetector.detect(testFile);

		String testFilter = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testInput = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();

		assertEquals(testFilter, "testFilter");
		assertEquals(testInput, "testInput");

	}

	@Test
	public void testIndirectTestingTestMoreIndexingFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_MORE_INDEXING_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		String testContentType = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testNoParts = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String assertContentType = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();

		assertEquals(testContentType, "testContentType");
		assertEquals(testNoParts, "testNoParts");
		assertEquals(assertContentType, "assertContentType");

	}

	@Test
	public void testIndirectTestingTestNGramProfile() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_NGRAM_PROFILE, "");
		testFile = flakyDetector.detect(testFile);

		String testContents = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testCounts = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testGetSorted = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();
		String testIO = testFile.getFlakyInst().get(0).getTestSmells().get(3).getTestMethod();

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

		String setUp = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();

		assertEquals(setUp, "setUp");
	}

	@Test
	public void testIndirectTestingTestColumnPaginationFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_COLUMN_PAGNATION_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		ArrayList<TestSmell> smells = testFile.getFlakyInst().get(0).getTestSmells();
		assertTrue(smells.isEmpty());
	}

	@Test
	public void testIndirectTestingTestSuffixURLFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_3, "");
		testFile = flakyDetector.detect(testFile);

		ArrayList<TestSmell> smells = testFile.getFlakyInst().get(0).getTestSmells();
		assertTrue(smells.isEmpty());
	}

	@Test
	public void testIndirectTestingTestByteBloomFilter() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_BYTE_BLOOM_FILTER, "");
		testFile = flakyDetector.detect(testFile);

		String testBloomFold = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String testBloomPerf = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();

		assertEquals(testBloomFold, "testBloomFold");
		assertEquals(testBloomPerf, "testBloomPerf");
	}

	@Test
	public void testIndirectTestingTestIntervalTree() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_INTERVAL_TREE, "");
		testFile = flakyDetector.detect(testFile);

		String testSearch = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String randomTestForIndirectTesting = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();

		assertEquals(testSearch, "testSearch");
		assertEquals(randomTestForIndirectTesting, "randomTestForIndirectTesting");
	}

	@Test
	public void testIndirectTestingTestHTMLLanguageParser() throws FileNotFoundException {
		FlakyDetector flakyDetector = new FlakyDetector(true, new IndirectTesting());
		TestFile testFile = new TestFile("flakyhoover", TEST_HTML_LANGUAGE_PARSER, "");
		testFile = flakyDetector.detect(testFile);

		String testMetaHTMLParsing = testFile.getFlakyInst().get(0).getTestSmells().get(0).getTestMethod();
		String getContent = testFile.getFlakyInst().get(0).getTestSmells().get(1).getTestMethod();
		String testLanguageIndentifier = testFile.getFlakyInst().get(0).getTestSmells().get(2).getTestMethod();

		assertEquals(testMetaHTMLParsing, "testMetaHTMLParsing");
		assertEquals(getContent, "getContent");
		assertEquals(testLanguageIndentifier, "testLanguageIndentifier");
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
