package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import flakyhoover.Main;

public class Util {
	
	public Util() {
		
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
	

	public static ArrayList<String> getAllJavaClasses()  {
		ArrayList<String> javaClasses = new ArrayList<String>();

		InputStream fstream = Util.class.getResourceAsStream("/java_classes.txt"); 

		if(fstream != null) {

			System.out.println("CAME HERE");
			

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
		return test.replace("Test", "");
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
}
