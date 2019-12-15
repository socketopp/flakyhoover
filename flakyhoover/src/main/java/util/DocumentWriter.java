package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import flakyhoover.AbstractSmell;
import flakyhoover.TestFile;

/**
 * This class is utilized to write output to a CSV file
 */

@SuppressWarnings("unused")
public class DocumentWriter {

	private static String filename;
	private static String[] headers;
	private static FileWriter fw;
	private static PrintWriter pw;
	private static File file;

	private static CSVPrinter csvFilePrinter;

	private DocumentWriter(String[] headers, String filename) throws IOException {
		DocumentWriter.filename = filename;
		DocumentWriter.headers = headers;

	}
//	https://stackoverflow.com/questions/39716028/java-write-new-row-to-csv-file

	public static DocumentWriter createDocumentWriter(String[] headers, String filename) throws IOException {
		return new DocumentWriter(headers, filename);

	}

	public static String[] getHeaders() {
		return headers;
	}

	public static void setup() throws IOException {
		fw = new FileWriter(filename, true);
		csvFilePrinter = new CSVPrinter(fw, CSVFormat.DEFAULT.withHeader(getHeaders()));

//		file = new File(filename);
//		if(!file.exists()) {
//			fw = new FileWriter(filename, true);
//			csvFilePrinter = new CSVPrinter(fw, CSVFormat.DEFAULT.withHeader(headers));
//		}
	}

//    https://www.dev2qa.com/read-write-csv-file-with-apache-commons-csv/

	public static void writeToCSV(List<List<String>> data) throws IOException {

		try {
			for (List<String> arrayList : data) {
				csvFilePrinter.printRecord(arrayList.toArray());
				csvFilePrinter.flush();
			}

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public static List<List<String>> readFromCSV(String csvFilePath, String[] headers) {

		List<List<String>> retList = new ArrayList<>();
		FileReader fReader = null;
		CSVParser csvParser = null;

		try {

			InputStream csvFile = new FileInputStream(csvFilePath);

			fReader = new FileReader(csvFilePath);

			CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers).withDelimiter(';');

			csvParser = new CSVParser(fReader, csvFormat);

			if (csvFile != null) {
				csvFile.close();
			}

			List<CSVRecord> rowList = csvParser.getRecords();

			for (int i = 1; i < rowList.size(); i++) {
				CSVRecord row = rowList.get(i);

				String project = row.get("project");
				String test_class = row.get("test-class");
				String test_method = row.get("test-method");
				String smell_type = "";
				String flakiness_type = "";
				try {
					smell_type = row.get("smell-type");
					flakiness_type = row.get("flakiness-type");
				} catch (Exception e) {

				}

				List<String> lineList = new ArrayList<>();

				lineList.add(project);
				lineList.add(test_class);
				lineList.add(test_method);
				lineList.add(smell_type);
				lineList.add(flakiness_type);

				retList.add(lineList);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fReader != null) {
					fReader.close();
				}

				if (csvParser != null) {
					csvParser.close();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return retList;

	}

	public static List<List<String>> prepareData(TestFile testfile) {

		List<List<String>> data = new ArrayList<>();

		List<String> line = new ArrayList<>();

		for (AbstractSmell flaky : testfile.getSmellyInst()) {

			ArrayList<TestSmell> smells = flaky.getTestSmells();
			if (smells != null) {

				for (TestSmell smell : smells) {
					if (smell.isSmelly()) {

						line.add(smell.getProject());
						line.add(smell.getTestClass());
						line.add(smell.getTestMethod());
						line.add(smell.getSmellType());
						line.add(smell.getFlakinessType());
						data.add(line);
					}

					line = new ArrayList<>();

				}

			}
		}
		return data;

	}

}
