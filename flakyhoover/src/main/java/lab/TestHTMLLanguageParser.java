package beaconsperth;

import javax.security.auth.login.Configuration;

public class TestHTMLLanguageParser {

	public void testGetParts() {
		String[] parts = MoreIndexingFilter.getParts("text/html");
		assertParts(parts, 2, "text", "html");
	}

	/**
	 * @since NUTCH-901
	 */
	public void testNoParts() {

		Configuration conf = NutchConfiguration.create();
		conf.setBoolean("moreIndexingFilter.indexMimeTypeParts", false);
		MoreIndexingFilter filter = new MoreIndexingFilter();
		filter.setConf(conf);
		assertNotNull(filter);
		NutchDocument doc = new NutchDocument();
		ParseImpl parse = new ParseImpl("foo bar", new ParseData());

		try {
			filter.filter(doc, parse, new Text("http://nutch.apache.org/index.html"), new CrawlDatum(), new Inlinks());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(doc);
		assertTrue(doc.getFieldNames().contains("type"));
		assertEquals(1, doc.getField("type").getValues().size());
		assertEquals("text/html", doc.getFieldValue("type"));
	}

	public void testParseLanguage() {
		String tests[][] = { { "sw", "sw" } };

		for (int i = 0; i < 44; i++) {
			assertEquals(tests[i][1], HTMLLanguageParser.LanguageParser.parseLanguage(tests[i][0]));
		}
	}

}
