package util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class DocumentReader {

	public static List<FlakyFile> readFromCSV(String PATH) throws IOException {

		Reader reader = new FileReader(PATH);

		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreEmptyLines(true).withDelimiter(','));
		int id = 0;
		ArrayList<FlakyFile> flakyFiles = new ArrayList<FlakyFile>();

		for (CSVRecord csvRecord : csvParser) {

			String url = csvRecord.get(0);
			String sha = csvRecord.get(1);
			String testCount = csvRecord.get(2);
			String module = csvRecord.get(3);
			String testName = csvRecord.get(4);
			String category = csvRecord.get(5);
			String version = csvRecord.get(6);

			System.out.println("Record No - " + csvRecord.getRecordNumber());
			System.out.println("---------------");

			int start = testName.lastIndexOf(".");
			int end = testName.length();
			String testMethod = testName.substring(start + 1, end);

			String classString = testName.substring(0, start);
			int cEnd = classString.length();
			int cStart = classString.lastIndexOf(".");
			String className = testName.substring(cStart + 1, cEnd);

			if (!testMethod.contains(" ")) {
				System.out.println("id" + id);
				System.out.println("url : " + url);
				System.out.println("sha : " + sha);
				System.out.println("testCount : " + testCount);
				System.out.println("Method: " + testMethod);
				System.out.println("Class: " + className);
				System.out.println("module : " + module);
				System.out.println("testName : " + testName);
				System.out.println("category : " + category);
				System.out.println("version : " + version);
				System.out.println("---------------\n\n");
				flakyFiles.add(new FlakyFile(id, url, sha, Integer.parseInt(testCount), testName, module, category,
						version, className, testMethod));
				id++;
			}

		}
		return flakyFiles;

	}

}
