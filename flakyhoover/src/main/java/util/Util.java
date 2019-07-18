package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class Util {

	public Util() {

	}

	public static boolean containerContain(List<String> container, String keyword) {
		for (int i = 0; i < container.size(); i++) {
			if (keyword.contains(container.get(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidTestMethod(MethodDeclaration n) {
		boolean valid = false;

		if (!n.getAnnotationByName("Ignore").isPresent()) {
			// only analyze methods that either have a @test annotation (Junit 4) or the
			// method name starts with 'test'
			if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")
					|| n.getBody().toString().toLowerCase().contains("assert")) {
				// must be a public method
				if (n.getModifiers().contains(Modifier.publicModifier())) {
					valid = true;
				}
			}
		}

		return valid;
	}

	public static boolean isValidTestClass(ClassOrInterfaceDeclaration n) {
		return n.getNameAsString().toLowerCase().contains("test")
				|| n.getExtendedTypes().toString().contains("TestCase");

	}

	public static ArrayList<String> getAllJavaClasses() {
		ArrayList<String> javaClasses = new ArrayList<String>();

		InputStream fstream = Util.class.getResourceAsStream("/java_classes.txt");

		if (fstream != null) {

//			System.out.println("CAME HERE");

			InputStreamReader inputStreamReader = new InputStreamReader(fstream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					javaClasses.add(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Always close files.
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return javaClasses;
	}

	public static String removeTest(String test) {
		return test.toLowerCase().replace("test", "");
	}

//	public static String getTestFileName(String testFile) {
//		int lastIndex = testFile.lastIndexOf(".");
//		return testFile.substring(lastIndex + 1, testFile.length());
//	}
//
//	public static String getTestFileNameWithoutExtension(String testFile) {
//		int lastIndex = getTestFileName(testFile).lastIndexOf(".");
//		return getTestFileName(testFile).substring(0, lastIndex);
//	}
//	

	public static String getTestFileName(String testFile) {
		int lastIndex = testFile.lastIndexOf(".");

		return testFile.substring(lastIndex + 1);

//		var filename = url.substring(url.lastIndexOf('/')+1);
//		String[] stringArray = testFile.split(".");

	}

	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);

			}
		}

		return list;
	}

	public static <T> void genericPrint(Set<T> container) {
		for (T item : container) {
			System.out.println(item);
		}
	}

	public static <T> void genericPrintMap(Map<T, Set<T>> container) {
		for (Map.Entry<T, Set<T>> entry : container.entrySet()) {
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			System.out.println("KEY: " + key + " / " + value);
		}
	}

	public static boolean isStringUpperCase(String str) {

		// convert String to char array
		char[] charArray = str.toCharArray();

		for (int i = 0; i < charArray.length; i++) {

			if (charArray[i] == '_' || charArray[i] == '-' || charArray[i] == '.') {
				continue;
			}
			// if any character is not in upper case, return false
			if (!Character.isUpperCase(charArray[i]))
				return false;
		}

		return true;
	}

	public static boolean hasTimeout(MethodDeclaration n) {
		if (isValidTestMethod(n)) {
			if (n.getAnnotations().get(0).getChildNodes().size() > 1) {
				boolean timeout = n.getAnnotations().get(0).getChildNodes().get(1).getChildNodes().get(0).toString()
						.equals("timeout");
				return timeout;
			}
			return false;
		}
		return false;
	}

	public static boolean checkIntBool(String integer) {
		try {
			Integer.parseInt(integer);
			return true;
		} catch (Exception e) {
			if (integer.equals("true") || integer.equals("false")) {
				return true;
			}
			return false;
		}
	}

	public static ArrayList<String> getAllFiles(String sDir) throws IOException {

		ArrayList<String> files = new ArrayList<String>();
		Files.find(Paths.get(sDir), 999,
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java"))
				.forEach(f -> files.add(f.toString()));
		return files;
	}

	public static ArrayList<String> getAllTestFiles(String sDir) throws IOException {

		ArrayList<String> files = new ArrayList<String>();
		Files.find(Paths.get(sDir), 999,
//				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*(test|testing).*\\.java"))
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java"))
				.forEach(f -> files.add(f.toString()));
		ArrayList<String> new_files = new ArrayList<String>();

		for (String file : files) {
			if (file.matches(".*(test|testing).*\\.java")) {
				new_files.add(file);
			}
		}

		return new_files;
	}

	public static void evaluate(int positive, double tp) {

		int allTest = 19532;
		int flaky = 8829;
		int total_negatives = allTest - flaky;

		int negative = allTest - positive;
		double fp = positive - tp;
		double tn = total_negatives - fp;
		double fn = negative - tn;

		double precision = tp / (fp + tp) * 100;
//		double mm = tp / precision;

		double recall = tp / (tp + fn) * 100;

		double specificity = tn / (tn + fp) * 100;

		double accuracy = (tp + tn) / (tp + fp + tn + fn) * 100;

		System.out.printf("precision \t %.2f %% \n", precision);
		System.out.printf("recall    \t %.2f %% \n", recall);
		System.out.printf("specificity \t %.2f %% \n", specificity);
		System.out.printf("accuracy \t %.2f %% \n", accuracy);

//		System.out.println("tp: " + tp);
//		System.out.println("fp: " + fp);
//		System.out.println("tn: " + tn);
//		System.out.println("fn: " + fn);

	}
}
