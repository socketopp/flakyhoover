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

	private static final String FIRE_AND_FORGET = "src\\main\\java\\lab\\FireAndForgetTest.java";

	private static final String CONDITIONAL_TEST_LOGIC = "src\\main\\java\\lab\\TestConditionalTestLogic.java";

	private static final String TEST_RUN_WAR_1 = "src\\main\\java\\lab\\TestRunWarTest.java";

	// FROM:
	// https://github.com/apache/nutch/blob/branch-1.4/src/test/org/apache/nutch/crawl/TestGenerator.java
	private static final String TEST_RUN_WAR_2 = "src\\main\\java\\lab\\TestGeneratorTest.java";

	// FROM:
	// https://raw.githubusercontent.com/apache/nutch/branch-1.4/src/plugin/urlfilter-suffix/src/test/org/apache/nutch/urlfilter/suffix/TestSuffixURLFilter.java
	private static final String TEST_RUN_WAR_3 = "src\\main\\java\\lab\\TestSuffixURLFilter.java";

	// FROM:
	// https://github.com/apache/nutch/blob/branch-1.4/src/test/org/apache/nutch/plugin/TestPluginSystem.java
	private static final String TEST_RUN_WAR_4 = "src\\main\\java\\lab\\TestPluginSystem.java";
	// FROM:

	// https://fossies.org/linux/misc/db-derby-10.15.1.3-src.tar.gz/db-derby-10.15.1.3-src/java/org.apache.derby.tests/org/apache/derbyTesting/functionTests/tests/lang/JoinTest.java
	private static final String TEST_RUN_WAR_5 = "src\\main\\java\\lab\\JoinTest.java";

	@Test
	public void testSharedFixture() throws FileNotFoundException {

		FlakyDetector flakyDetector = new FlakyDetector(true, new SharedFixture());
		TestFile testFile = new TestFile("flakyhoover", TEST_RUN_WAR_1, "");
		testFile = flakyDetector.detect(testFile);

		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

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

		// for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
//			System.out.println("TS: " + ts.getTestMethod());
//		}

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
		String niftyTest = testFile.getFlakyInst().get(0).getTestSmells().get(4).getTestMethod();

		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

		assertEquals(testRsReportsWrongServerName, "testRsReportsWrongServerName");
		assertEquals(testContinuousScheduling, "testContinuousScheduling");
		assertEquals(testWriteToDB, "testWriteToDB");
		assertEquals(testServiceTopPartitionsNoArg, "testServiceTopPartitionsNoArg");
		assertEquals(niftyTest, "niftyTest");

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

		for (TestSmell ts : testFile.getFlakyInst().get(0).getTestSmells()) {
			System.out.println("TS: " + ts.getTestMethod());
		}

		assertEquals(testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody,
				"testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody");
		assertEquals(testDisplayCurrentTime_whenever, "testDisplayCurrentTime_whenever");
		assertEquals(conditionalVerificationLogic, "conditionalVerificationLogic");
		assertEquals(testGetFlightsByOrigin_NoInboundFlight_SMRTD, "testGetFlightsByOrigin_NoInboundFlight_SMRTD");
		assertEquals(testCombinationsOfInputValues, "testCombinationsOfInputValues");
		assertEquals(testMultipleValueSets, "testMultipleValueSets");

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
