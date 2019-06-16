package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import flakyhoover.AbstractFlaky;
import flakyhoover.TestFile;

/**
 * This class is utilized to write output to a CSV file
 */
public class DocumentWriter {

	private static String filename;
	private static String[] headers;
	private static FileWriter fw;
	private static File file;
	// or BufferedWriter writer =
	// Files.newBufferedWriter(Paths.get(file.getName()));
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

	public static void writeToCSV(ArrayList<ArrayList<String>> data) throws IOException {

		try {
			for (ArrayList<String> arrayList : data) {

				csvFilePrinter.printRecord(arrayList.toArray());
				csvFilePrinter.flush();
			}

			// {file: ArrayList<ArrayList<String>>

//			csvPrinter.printRecord("1", "Sundar Pichai ♥", "CEO", "Google");
//			csvPrinter.printRecord("2", "Satya Nadella", "CEO", "Microsoft");
//			csvPrinter.printRecord("3", "Tim cook", "CEO", "Apple");
//			csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));

		} catch (Exception e) {
			System.out.println(e);
		}

	}



	public static List<List<String>>  readFromCSV(String csvFilePath, String[] headers) {

		List<List<String>> retList = new ArrayList<List<String>>();
		FileReader fReader = null;
		CSVParser csvParser = null;

		try {
			
			InputStream csvFile = new FileInputStream(csvFilePath);
//			
//			CSVParser csvParser1 = CSVFormat.DEFAULT.parse(new InputStreamReader(csvFile));
//			for (CSVRecord record : csvParser1) {  
//			    String field_1 = record.get(0);
//			   
//			    System.out.println(field_1);
//			 
//			}
			
			
			
			fReader = new FileReader(csvFilePath);
			

			CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers).withDelimiter(';');

			csvParser = new CSVParser(fReader, csvFormat);
			
			
			List<CSVRecord> rowList = csvParser.getRecords();

			
			for (int i = 1; i < rowList.size(); i++) {
				CSVRecord row = rowList.get(i);
				
				String project = row.get("project");
				String test_class = row.get("test-class");
				String test_method = row.get("test-method");
				String smell_type = "";
				String flakiness_type ="";
				try {
					smell_type = row.get("smell-type");
					flakiness_type = row.get("flakiness-type");
				}catch(Exception e) {
					
				}

				List<String> lineList = new ArrayList<String>();

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

	public static ArrayList<ArrayList<String>> prepareData(TestFile testfile) {
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		ArrayList<String> line = new ArrayList<String>();

		for (AbstractFlaky flaky : testfile.getFlakyInst()) {
//			System.out.println("get has flaky: " + flaky.getHasFlaky());
			if (flaky.getHasFlaky()) {
				ArrayList<TestSmell> smells = flaky.getTestSmells();
				if (smells != null) {

//					System.out.println("prepare success");
					for (TestSmell smell : smells) {
						line.add(smell.getProject());
						line.add(smell.getTestClass());
						line.add(smell.getTestMethod());
						line.add(smell.getSmellType());
						line.add(smell.getFlakinessType());
//					break;
						data.add(line);

						line = new ArrayList<String>();

					}
				} else {
//					System.out.println("prepare fail");
				}

			}
		}
		return data;
	}

	public static void createCSVFile(List<String> data) throws IOException {

//		CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
		CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(";");

		FileWriter fWriter = null;
		CSVPrinter csvPrinter = null;

		try {
//			System.out.println("Prepare CSVPrinter object. ");
			/* Create file writer. */
			fWriter = new FileWriter(filename);

			/* Create CSVPrinter */
//			csvPrinter = new CSVPrinter(fWriter,
//					csvFormat.withHeader("project", "test-class", "test-method", "smell-type", "flakiness-type"));
			csvPrinter = new CSVPrinter(fWriter, CSVFormat.DEFAULT.withHeader("project", "test-class", "test-method",
					"smell-type", "flakiness-type"));

//			System.out.println("Print header in file. ");
			/* First create header in csv file. */
//			csvPrinter.printRecord(headers);

//			System.out.println("Loop in the row list and print each row to csv file " + filename);
			/* Loop the user account list and print to csv file. */
//			for (int i = 0; i < data.size(); i++) {
//				String d = data.get(i);

//				List<String> rowDataList = new ArrayList<String>();
//				rowDataList.add(String.valueOf(userDto.getId()));
//				rowDataList.add(userDto.getUserName());
//				rowDataList.add(userDto.getPassword());
//				rowDataList.add(userDto.getEmail());
//				rowDataList.add(userDto.getAge());
//				rowDataList.add(userDto.getMobile());

//				csvPrinter.printRecord(1, "john73", "John", "Doe", "a");
//			}

//			System.out.println("Create file compelete successfully. ");
		} catch (Exception e) {

		}

	}

}

//String[] headers = {"project","test-class",	"test-method",	"smell-type",	"flakiness-type"};
//ArrayList<String> places = new ArrayList<>(Arrays.asList("apache-cassandra-1.1", "org.apache.cassandra.utils.IntervalTest", "testIntersects","fire-and-forget", "async-wait"));
//
//

//try {
//dw = DocumentWriter.createDocumentWriter(headers, "results.csv");
//dw.writeToCSV(places);
//} catch (IOException e) {
//// TODO Auto-generated catch block
//e.printStackTrace();
//}

//String[] headers = { "author", "title"};
//DocumentWriter.createDocumentWriter(headers, "books.csv");
//DocumentWriter.createCSVFile(AUTHOR_BOOK_MAP);
//createCSVFile(AUTHOR_BOOK_MAP, HEADERS);
