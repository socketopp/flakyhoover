package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class IssueWriter {

	private static String prettyPrint(Document document) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		DOMSource source = new DOMSource(document);
		StringWriter strWriter = new StringWriter();
		StreamResult result = new StreamResult(strWriter);
		transformer.transform(source, result);
		System.out.println(strWriter.getBuffer().toString());

		return strWriter.getBuffer().toString();
	}

	public static String formatXml(String xml) {

		try {

			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());

			serializer.transform(xmlSource, res);

			return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());

		} catch (Exception e) {
			return xml;
		}
	}
}

////XStream xstream = new XStream(new StaxDriver());
//XStream xstream = new XStream(new DomDriver());
////XStream xstream = new XStream();
//
//xstream.autodetectAnnotations(true);
//
//
//Issues issues = new Issues();
//
//ArrayList<Issue> issueslist = issues.getIssuesList();
//issueslist.add(new Issue());
//issueslist.add(new Issue());
//issueslist.add(new Issue());
//issues.setIssues(issueslist);

//xstream.processAnnotations(Issues.class);


// Using annotations in class Employee
//xstream.alias("issue", Issue.class);
//xstream.alias("issues", Issues.class);
//xstream.processAnnotations(Issue.class);


// Object to XML Conversion
//String xml = xstream.toXML(issues.getIssues());
// create the xml file
//transform the DOM Object to an XML File

//System.out.println(formatXml(xml));

//DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//DocumentBuilder db = dbf.newDocumentBuilder();
//InputSource is = new InputSource(new StringReader(xml));
//
//Document doc = db.parse(is);
////prettyPrint(doc);
//
//
//
//TransformerFactory transformerFactory = TransformerFactory.newInstance();
//Transformer transformer = transformerFactory.newTransformer();
//DOMSource domSource = new DOMSource(doc);
//
//StreamResult streamResult = new StreamResult(new File("newfile.xml"));


//transformer.transform(domSource, streamResult);

