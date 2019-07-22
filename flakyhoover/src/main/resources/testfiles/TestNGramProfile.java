package testfiles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class TestNGramProfile extends TestCase {

	String tokencontent1 = "testaddtoken";
	String tokencontent2 = "anotherteststring";

	int[] counts1 = { 3, 2, 2, 1, 1, 1, 1, 1 };

	String[] chars1 = { "t", "d", "e", "a", "k", "n", "o", "s" };

	/**
	 * Test analyze method
	 */
	public void testAnalyze() {
		String tokencontent = "testmeagain";

		NGramProfile p = new NGramProfile("test", 1, 1);
		p.analyze(new StringBuilder(tokencontent));

		// test that profile size is ok, eg 8 different NGramEntries "tesmagin"
		assertEquals(8, p.getSorted().size());
	}

	/**
	 * test getSorted method
	 */
	public void testGetSorted() {
		int[] count = { 4, 3, 1 };
		String[] ngram = { "a", "b", "c" };

		String teststring = "AAaaBbbC";

		NGramProfile p = new NGramProfile("test", 1, 1);
		p.analyze(new StringBuilder(teststring));

		// test size of profile
		assertEquals(3, p.getSorted().size());

		testCounts(p.getSorted(), count);
		testContents(p.getSorted(), ngram);

	}

	public void testGetSimilarity() {
		NGramProfile a = new NGramProfile("a", 1, 1);
		NGramProfile b = new NGramProfile("b", 1, 1);

		a.analyze(new StringBuilder(tokencontent1));
		b.analyze(new StringBuilder(tokencontent2));

		// because of rounding errors might slightly return different results
		assertEquals(a.getSimilarity(b), b.getSimilarity(a), 0.0000002);

	}

	public void testExactMatch() {
		NGramProfile a = new NGramProfile("a", 1, 1);

		a.analyze(new StringBuilder(tokencontent1));

		assertEquals(a.getSimilarity(a), 0, 0);

	}

	public void testIO() {
		// Create profile and set some contents
		NGramProfile a = new NGramProfile("a", 1, 1);
		a.analyze(new StringBuilder(this.tokencontent1));

		NGramProfile b = new NGramProfile("a_from_inputstream", 1, 1);

		// save profile
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			a.save(os);
			os.close();
		} catch (Exception e) {
			fail();
		}

		// load profile
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		try {
			b.load(is);
			is.close();
		} catch (Exception e) {
			fail();
		}

		// check it
		testCounts(b.getSorted(), counts1);
		testContents(b.getSorted(), chars1);
	}

	private void testContents(List<NGramEntry> entries, String contents[]) {
		int c = 0;

		for (NGramEntry nge : entries) {
			assertEquals(contents[c], nge.getSeq().toString());
			c++;
		}
	}

	private void testCounts(List<NGramEntry> entries, int counts[]) {
		int c = 0;

		for (NGramEntry nge : entries) {
			assertEquals(counts[c], nge.getCount());
			c++;
		}
	}

}
