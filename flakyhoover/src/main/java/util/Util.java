package util;

import java.io.BufferedReader;
import java.io.File;
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

	/*
	 * Default constructor
	 */
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

	public static boolean isAnnotatedTest(MethodDeclaration n) {
		boolean valid = false;

		if (!n.getAnnotationByName("Ignore").isPresent()) {

			if (n.getAnnotationByName("Test").isPresent()) {
				// must be a public method
				if (n.getModifiers().contains(Modifier.publicModifier())) {
					valid = true;
				}
			}
		}
		return valid;
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
		ArrayList<String> javaClasses = new ArrayList<>();

		InputStream fstream = Util.class.getResourceAsStream("/java_classes.txt");

		if (fstream != null) {

			InputStreamReader inputStreamReader = new InputStreamReader(fstream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					javaClasses.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Always close files.
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return javaClasses;
	}

	public static String removeTest(String test) {
		return test.toLowerCase().replace("test", "");
	}


	public static String getTestFileName(String testFile) {
		int lastIndex = testFile.lastIndexOf(".");
		return testFile.substring(lastIndex + 1);

	}

	public static String getGitName(String name) {
		int lastIndex = name.lastIndexOf("/");
		return name.substring(lastIndex + 1);

	}

	public static String getClassName(String file) {
		int first = file.lastIndexOf("\\");
		int dot = file.substring(first + 1).lastIndexOf(".");
		return file.substring(first + 1).substring(0, dot);
	}

	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);

			}
		}

		return list;
	}

	public static <T> void genericPrintMap(Map<T, Set<T>> container) {
		for (Map.Entry<T, Set<T>> entry : container.entrySet()) {
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			System.out.println("KEY: " + key + " / " + value);
		}
	}

	public static boolean checkIfCloned(File gitPath, String project) {
		boolean value = false;
		for (int i = 0; i < gitPath.list().length; i++) {
			if (gitPath.list()[i].equals(project)) {
				value = true;
			}
		}
		return value;
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

	public static List<String> getFlakyFiles(String sDir, String testFile) throws IOException {

		// TODO Use endsWith instead of matches.
//		add this instead .endsWith(".*\\" + testFile+".java"))
		ArrayList<String> files = new ArrayList<>();
		Files.find(Paths.get(sDir), 999,
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java")).forEach(f -> {
					if (Util.getClassName(f.toString()).equals(testFile)) {
						files.add(f.toString());
					}
				});

		return files;

	}

	public static List<String> getFlakyFiles2(String sDir, String testFile) throws IOException {

		// TODO Use endsWith instead of matches.
//		add this instead .endsWith(".*\\" + testFile+".java"))
		ArrayList<String> files = new ArrayList<>();
		Files.find(Paths.get(sDir), 999,
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().endsWith(".*\\" + testFile + ".java"))
				.forEach(f -> {
					if (Util.getClassName(f.toString()).equals(testFile)) {
						files.add(f.toString());
					}
				});

		return files;

	}

	public static String getFlakyFile(String sDir, String testFile) throws IOException {

		ArrayList<String> files = new ArrayList<>();

		Files.find(Paths.get(sDir), 999,
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java"))
				.forEach(f -> files.add(f.toString()));

		String output = null;
		for (String file : files) {

			if ((Util.getClassName(file).equals(testFile))) {
				output = file;
			}
		}
		return output;
	}

	public static ArrayList<String> getAllFiles(String sDir) throws IOException {

		ArrayList<String> files = new ArrayList<>();
		Files.find(Paths.get(sDir), 999,
				(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.java"))
				.forEach(f -> files.add(f.toString()));
		return files;
	}

	public static ArrayList<String> getAllTestFiles(String sDir) throws IOException {

		ArrayList<String> files = new ArrayList<>();
		Files.find(Paths.get(sDir), Integer.MAX_VALUE, (p, bfa) -> bfa.isRegularFile()).forEach(f -> {
			if (f.toString().matches(".*Test.java")) {
				files.add(f.toString());
			}
		});

		return files;
	}
}
