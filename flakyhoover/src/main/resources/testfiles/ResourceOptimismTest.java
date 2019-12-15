package testfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.print.attribute.standard.Compression;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Response;

public class ResourceOptimismTest extends TestCase {

	private static final File testDir = new File(System.getProperty("test.input"));
	private Path path1;
	private Path path2;

	// Source
	// https://github.com/apache/nutch/blob/branch-1.4/src/plugin/creativecommons/src/test/org/creativecommons/nutch/TestCCParseFilter.java

	// is flaky
	public void testPages() throws Exception {
		pageTest(new File(testDir, "anchor.html"), "http://foo.com/",
				"http://creativecommons.org/licenses/by-nc-sa/1.0", "a", null);
		// Tika returns <a> whereas parse-html returns <rel>
		// check later
		pageTest(new File(testDir, "rel.html"), "http://foo.com/", "http://creativecommons.org/licenses/by-nc/2.0",
				"rel", null);
		// Tika returns <a> whereas parse-html returns <rdf>
		// check later
		pageTest(new File(testDir, "rdf.html"), "http://foo.com/", "http://creativecommons.org/licenses/by-nc/1.0",
				"rdf", "text");
	}

//	Source:https:// github.com/apache/ant-ivy/blob/2.1.x/test/java/org/apache/ivy/core/retrieve/RetrieveTest.java
//  is flaky
	private void assertLink(String filename) throws IOException {
		// if the OS is known to support symlink, check that the file is a symlink,
		// otherwise just check the file exist.

		File file = new File(filename);
		assertTrue("The file " + filename + " doesn't exist", file.exists());

		String os = System.getProperty("os.name");
		if (os.equals("Linux") || os.equals("Solaris") || os.equals("FreeBSD")) {
			// these OS should support symnlink, so check that the file is actually a
			// symlink.
			// this is done be checking that the canonical path is different from the
			// absolute
			// path.
			File absFile = file.getAbsoluteFile();
			File canFile = file.getCanonicalFile();
			assertFalse("The file " + filename + " isn't a symlink", absFile.equals(canFile));
		}
	}

//	// SRC:
//	// https://github.com/apache/ant-ivy/blob/2.1.x/test/java/org/apache/ivy/core/resolve/ResolveTest.java
//	// Not flaky atm
//	public void testConfigurationMapping1() throws Exception {
//		Ivy ivy = new Ivy();
//		ConfigurationResolveReport conf = report.getConfigurationReport("default");
//
//		assertContainsArtifact("test", "a", "1.0.2", "a", "txt", "txt", conf);
//		assertDoesntContainArtifact("test", "a", "1.0.2", "a-bt", "txt", "txt", conf);
//		assertContainsArtifact("test", "b", "1.0.2", "b", "txt", "txt", conf);
//		assertDoesntContainArtifact("test", "b", "1.0.2", "b-bt", "txt", "txt", conf);
//		assertContainsArtifact("test", "c", "1.0.2", "c", "txt", "txt", conf);
//		assertDoesntContainArtifact("test", "c", "1.0.2", "c-bt", "txt", "txt", conf);
//	}

	// SRC:
	// https://github.com/apache/ant-ivy/blob/2.1.x/test/java/org/apache/ivy/core/settings/XmlSettingsParserTest.java
	// Flaky
	public void test() throws Exception {
		IvySettings settings = new IvySettings();
		XmlSettingsParser parser = new XmlSettingsParser(settings);
		parser.parse(XmlSettingsParserTest.class.getResource("ivysettings-test.xml"));
		File defaultCache;
		defaultCache = settings.getDefaultCache();
		assertNotNull(defaultCache);
	}

	// SRC:
	// https://github.com/apache/karaf/blob/karaf-2.3.x/admin/core/src/test/java/org/apache/karaf/admin/internal/AdminServiceImplTest.java
	// Flaky
	public void testHandleFeatures() throws Exception {
		AdminServiceImpl as = new AdminServiceImpl();

		File f = File.createTempFile(getName(), ".test");
		try {
			Properties p = new Properties();
			p.put("featuresBoot", "abc,def ");
			p.put("featuresRepositories", "somescheme://xyz");
			OutputStream os = new FileOutputStream(f);
			try {
				p.store(os, "Test comment");
			} finally {
				os.close();
			}

			InstanceSettings s = new InstanceSettings(8122, 1122, 44444, null, null, null, Arrays.asList("test"));
			as.handleFeatures(f, s);

			Properties p2 = new Properties();
			InputStream is = new FileInputStream(f);
			try {
				p2.load(is);
			} finally {
				is.close();
			}
			assertEquals(2, p2.size());
			assertEquals("abc,def,test", p2.get("featuresBoot"));
			assertEquals("somescheme://xyz", p2.get("featuresRepositories"));
		} finally {
			f.delete();
		}
	}

	// SRC:
	// https://github.com/apache/karaf/blob/karaf-2.3.x/deployer/blueprint/src/test/java/org/apache/karaf/deployer/blueprint/BlueprintDeploymentListenerTest.java
	// FLAKY
	public void testPackagesExtraction() throws Exception {
		BlueprintDeploymentListener l = new BlueprintDeploymentListener();
		File f = new File(getClass().getClassLoader().getResource("test.xml").toURI());
		Set<String> pkgs = BlueprintTransformer.analyze(new DOMSource(BlueprintTransformer.parse(f.toURL())));
		assertNotNull(pkgs);
		assertEquals(1, pkgs.size());
		Iterator<String> it = pkgs.iterator();
		assertEquals("org.apache.aries.blueprint.sample", it.next());
	}

	// SRC:
	// https://github.com/apache/pig/blob/branch-0.8/contrib/zebra/src/test/org/apache/hadoop/zebra/tfile/TestTFileByteArrays.java
	// FLAKY
	public void testNoDataEntry() throws IOException {
		if (skip)
			return;
		closeOutput();

		Reader reader = new Reader(fs.open(path1), fs.getFileStatus(path1).getLen(), conf);
		Assert.assertTrue(reader.isSorted());
		Scanner scanner = reader.createScanner();
		Assert.assertTrue(scanner.atEnd());
		scanner.close();
		reader.close();
	}
	// SRC:
	// https://github.com/apache/pig/blob/branch-0.8/contrib/zebra/src/test/org/apache/hadoop/zebra/tfile/TestTFileByteArrays.java

	// FLAKY
	public void testFailureGetNonExistentMetaBlock() throws IOException {
		if (skip)
			return;
		writer.append("keyX".getBytes(), "valueX".getBytes());

		// create a new metablock
		DataOutputStream outMeta = writer.prepareMetaBlock("testX", Compression.Algorithm.GZ.getName());
		outMeta.write(123);
		outMeta.write("foo".getBytes());
		outMeta.close();
		closeOutput();

		Reader reader = new Reader(fs.open(path), fs.getFileStatus(path2).getLen(), conf);
		DataInputStream mb = reader.getMetaBlock("testX");
		Assert.assertNotNull(mb);
		mb.close();
		try {
			DataInputStream mbBad = reader.getMetaBlock("testY");
			Assert.assertNull(mbBad);
			Assert.fail("Error on handling non-existent metablocks.");
		} catch (Exception e) {
			// noop, expecting exceptions
		}
		reader.close();
	}

	// SRC:
	// https://github.com/apache/pig/blob/branch-0.8/test/org/apache/pig/test/TestDataBagAccess.java
	// FLAKY
	@Test
	public void testBagConstantAccess() throws IOException, ExecException {
		File input = Util.createInputFile("tmp", "", new String[] { "sampledata\tnot_used" });
		pigServer.registerQuery(
				"a = load '" + Util.generateURI(Util.encodeEscape(input.toString()), pigServer.getPigContext()) + "';");
		pigServer.registerQuery(
				"b = foreach a generate {(16, 4.0e-2, 'hello')} as mybag:{t:(i: int, d: double, c: chararray)};");
		pigServer.registerQuery("c = foreach b generate mybag.i, mybag.d, mybag.c;");
		Iterator<Tuple> it = pigServer.openIterator("c");
		Tuple t = it.next();
		Object[] results = new Object[] { new Integer(16), new Double(4.0e-2), "hello" };
		Class[] resultClasses = new Class[] { Integer.class, Double.class, String.class };
		assertEquals(results.length, t.size());
		for (int i = 0; i < results.length; i++) {
			DataBag bag = (DataBag) t.get(i);
			assertEquals(results[i], bag.iterator().next().get(0));
			assertEquals(resultClasses[i], bag.iterator().next().get(0).getClass());
		}
	}

	// SRC:
	// https://github.com/apache/pig/blob/branch-0.8/test/org/apache/pig/test/TestLocal.java
	// FLAKY
	@Test
	public void testBigGroupAllWithNull() throws Throwable {

		int LOOP_COUNT = 4 * 1024;
		File tmpFile = File.createTempFile(this.getName(), ".txt");
		PrintStream ps = new PrintStream(new FileOutputStream(tmpFile));
		long nonNullCnt = 0;
		for (int i = 0; i < LOOP_COUNT; i++) {
			if (i % 10 == 0) {
				ps.println("");
			} else {
				ps.println(i);
				nonNullCnt++;
			}
		}
		ps.close();

		assertEquals(new Double(nonNullCnt), bigGroupAll(tmpFile));

		tmpFile.delete();

	}

	// SRC:
	// https://testsmells.github.io/pages/testsmellexamples.html#ResourceOptimism
	// https://github.com/Freeyourgadget/Gadgetbridge/blob/d9283d0f2265fbe36759d01f9ae52cd9d3605585/app/src/test/java/nodomain/freeyourgadget/gadgetbridge/test/LoggingTest.java
	// FLAKY
	@Test
	public void saveImage_noImageFile_ko() throws IOException {
		File outputFile = File.createTempFile("prefix", "png", new File("/tmp"));
		ProductImage image = new ProductImage("01010101010101", ProductImageField.FRONT, outputFile);
		Response response = serviceWrite.saveImage(image.getCode(), image.getField(), image.getImguploadFront(),
				image.getImguploadIngredients(), image.getImguploadNutrition()).execute();
		assertTrue(response.isSuccess());
		assertThatJson(response.body()).node("status").isEqualTo("status not ok");
	}

	// Flaky
	public void relationFunction() {
		readContents(new Path());
	}

	// SRC:
	// https://github.com/apache/nutch/blob/branch-1.4/src/test/org/apache/nutch/crawl/TestGenerator.java
	// FLAKY
	private ArrayList<URLCrawlDatum> readContents(Path fetchlist) throws IOException {
		// verify results
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, fetchlist, conf);

		ArrayList<URLCrawlDatum> l = new ArrayList<URLCrawlDatum>();

		READ: do {
			Text key = new Text();
			CrawlDatum value = new CrawlDatum();
			if (!reader.next(key, value)) {
				break READ;
			}
			l.add(new URLCrawlDatum(key, value));
		} while (true);

		reader.close();
		return l;
	}

	// Not flaky
	@Test
	public void filesExists() {
		Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());

		if (Files.exists(regionDir, options)) {
			dataFile = File.createTempFile("features", null, null, regionDir);
		}
	}

	// Not flaky
	@Test
	public void isReadable() {
		Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());

		if (Files.isReadable(regionDir, options)) {
			dataFile = File.createTempFile("features", null, null, regionDir);
		}
	}

	// Not flaky
	@Test
	public void isExecutable() {
		Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());

		if (Files.isExecutable(regionDir, options)) {
			dataFile = File.createTempFile("features", null, null, regionDir);
			File file = new File(regionDir);
		}
	}

	public void classVariableExample() {

		someObject.getInstance().callingMethod(new Creathing(caller(testDir)));

	}

}
