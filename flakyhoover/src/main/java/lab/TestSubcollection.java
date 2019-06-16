package beaconsperth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.apache.nutch.util.NutchConfiguration;

import junit.framework.TestCase;

public class TestSubcollection extends TestCase {

	/**
	 * Test filtering logic
	 * 
	 * @throws Exception
	 */
	public void testFilter() throws Exception {
		Subcollection sc = new Subcollection(NutchConfiguration.create());
		sc.setWhiteList("www.nutch.org\nwww.apache.org");
		sc.setBlackList("jpg\nwww.apache.org/zecret/");

		// matches whitelist
		assertEquals("http://www.apache.org/index.html", sc.filter("http://www.apache.org/index.html"));

		// matches blacklist
		assertEquals(null, sc.filter("http://www.apache.org/zecret/index.html"));
		assertEquals(null, sc.filter("http://www.apache.org/img/image.jpg"));

		// no match
		assertEquals(null, sc.filter("http://www.google.com/"));
	}

	public void testInput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<!-- just a comment -->");
		xml.append("<subcollections>");
		xml.append("<subcollection>");
		xml.append("<name>nutch collection</name>");
		xml.append("<id>nutch</id>");
		xml.append("<whitelist>");
		xml.append("http://lucene.apache.org/nutch/\n");
		xml.append("http://wiki.apache.org/nutch/\n");
		xml.append("</whitelist>");
		xml.append("<blacklist>");
		xml.append("http://www.xxx.yyy\n");
		xml.append("</blacklist>");
		xml.append("</subcollection>");
		xml.append("</subcollections>");

		InputStream is = new ByteArrayInputStream(xml.toString().getBytes());

		CollectionManager cm = new CollectionManager();
		cm.parse(is);

		Collection c = cm.getAll();

		// test that size matches
		assertEquals(1, c.size());

		Subcollection collection = (Subcollection) c.toArray()[0];

		// test collection id
		assertEquals("nutch", collection.getId());

		// test collection name
		assertEquals("nutch collection", collection.getName());

		// test whitelist
		assertEquals(2, collection.whiteList.size());

		String wlUrl = (String) collection.whiteList.get(0);
		assertEquals("http://lucene.apache.org/nutch/", wlUrl);

		wlUrl = (String) collection.whiteList.get(1);
		assertEquals("http://wiki.apache.org/nutch/", wlUrl);

		// matches whitelist
		assertEquals("http://lucene.apache.org/nutch/", collection.filter("http://lucene.apache.org/nutch/"));

		// test blacklist
		assertEquals(1, collection.blackList.size());

		String blUrl = (String) collection.blackList.get(0);
		assertEquals("http://www.xxx.yyy", blUrl);

		// no match
		assertEquals(null, collection.filter("http://www.google.com/"));
	}
}

//import java.io.IOException;
//import java.util.Collections;
//
//import javax.security.auth.login.Configuration;
//
//import junit.framework.TestCase;
//
//public class TestSubcollection extends TestCase {
////	private Subcollection sc = new Subcollection(new Configuration());
//	private SubcollectionBroken sc2 = new SubcollectionBroken(new Configuration());
//	public static Configuration pubconfig = NutchConfiguration.create();
//
//	public void testFilter() throws Exception {
//
////    HbaseConfiguration config = NutchConfiguration.create();
//
////	 Subcollection sc = new Subcollection();
//
////    Subcollection sc = new Subcollection(new Configuration());
//
////
////    sc.setWhiteList("www.nutch.org\nwww.apache.org");
////    sc.setBlackList("jpg\nwww.apache.org/zecret/");
////
////    //matches whitelist
////    assertEquals("http://www.apache.org/index.html", sc.filter("http://www.apache.org/index.html"));
////
////    //matches blacklist
////    assertEquals(null, sc.filter("http://www.apache.org/zecret/index.html"));
////    assertEquals(null, sc.filter("http://www.apache.org/img/image.jpg"));
//
//		// no match
////    assertEquals(null, sc.filter("http://www.google.com/"));
//	}
//
////  public void resourceError1() {
////	  File newFIle = File("/");
////	  
////  }
////  public void resourceError2() {
////	  File newFIle = File("/");
////	  
////  }
////  
////  public void IndirectResourceError() {
////	  HBFile newFIle = HBFileFile("/");
////	  
////  }
////  
////  
////  
////  
//
//	@Test
//	public void testScanEmptyToBBA() throws IOException, InterruptedException, ClassNotFoundException {
//		testScan(null, "bba", "baz");
//	}
//
//	public void testScan(String a, String c, String b) {
//		SubcollectionBroken sc = new SubcollectionBroken(new Configuration());
//		HbaseConfiguration config = NutchConfiguration.create();
//
//		RandomClass arg0;
//		Collections.addAll(arg0, arg1);
//		int r = Integer.parseInt("2");
//		assertEquals("nutch collection", collection.getName());
//
//	}
//
////  public void testRunWar() {
//////	  Conntection db = connect();
////	  Subcollection sc = new Subcollection(new ConfigurationNew());
////  }
////  
//
////	public void testInput() {
////		StringBuffer xml = new StringBuffer();
////		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
////		xml.append("<!-- just a comment -->");
////		xml.append("<subcollections>");
////		xml.append("<subcollection>");
////		xml.append("<name>nutch collection</name>");
////		xml.append("<id>nutch</id>");
////		xml.append("<whitelist>");
////		xml.append("http://lucene.apache.org/nutch/\n");
////		xml.append("http://wiki.apache.org/nutch/\n");
////		xml.append("</whitelist>");
////		xml.append("<blacklist>");
////		xml.append("http://www.xxx.yyy\n");
////		xml.append("</blacklist>");
////		xml.append("</subcollection>");
////		xml.append("</subcollections>");
////
////		InputStream is = new ByteArrayInputStream(xml.toString().getBytes());
////
////		CollectionManager cm = new CollectionManager();
////		cm.parse(is);
////
////		Collection c = cm.getAll();
////
////		// test that size matches
////		assertEquals(1, c.size());
////
////		Subcollection collection = (Subcollection) c.toArray()[0];
////
////		// test collection id
////		assertEquals("nutch", collection.getId());
////
////		// test collection name
////		assertEquals("nutch collection", collection.getName());
////
////		// test whitelist
////		assertEquals(2, collection.whiteList.size());
////
////		String wlUrl = (String) collection.whiteList.get(0);
////		assertEquals("http://lucene.apache.org/nutch/", wlUrl);
////
////		wlUrl = (String) collection.whiteList.get(1);
////		assertEquals("http://wiki.apache.org/nutch/", wlUrl);
////
////		// matches whitelist
////		assertEquals("http://lucene.apache.org/nutch/", collection.filter("http://lucene.apache.org/nutch/"));
////
////		// test blacklist
////		assertEquals(1, collection.blackList.size());
////
////		String blUrl = (String) collection.blackList.get(0);
////		assertEquals("http://www.xxx.yyy", blUrl);
////
////		// no match
////		assertEquals(null, collection.filter("http://www.google.com/"));
////	}
//}
