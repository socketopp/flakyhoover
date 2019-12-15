package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SuperClassFinder {

	public SuperClassFinder() {

	}

	public static class MethodClassCollector extends VoidVisitorAdapter<Collector> {
		private ArrayList<String> jClasses = Util.getAllJavaClasses();

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Collector collector) {

			for (MethodDeclaration method : n.getMethods()) {
				// If we found the method we're looking for and we have a parentClass, then we
				// are done.
				if (collector.getMethodName().equals(method.getNameAsString())) {
					collector.setFound(true);
					collector.setParentClass(n.getNameAsString());
					if (n.isStatic()) {
						collector.setParentClass(collector.getBaseClass());
					}
				}
			}

			if (n.getExtendedTypes().size() > 0 && !collector.isFound()) {
				String className = n.getExtendedTypes().get(0).getNameAsString();
				collector.addSeen(n.getNameAsString());
				if (!jClasses.contains(className)) {
					collector.setParentClass(className);
				}
			}
			super.visit(n, collector);

		}

//		@Override
//		public void visit(ClassOrInterfaceDeclaration n, Collector collector) {
//			
//			if (n.getExtendedTypes().size() > 0) {
//				for (ClassOrInterfaceType type : n.getExtendedTypes()) {
//					String className = type.getNameAsString();
//					// For each class: check if it is not included in java libraries
//					if (!jClasses.contains(className)) {
//						// If not, then we can assume it's a class in the production code
//						collector.setParentClass(className);
//					}
//				}
//			}
//			
//			for (MethodDeclaration method : n.getMethods()) {
//				// If we found the method we're looking for and we have a parentClass, then we
//				// are done.
//				if (collector.getMethodName().equals(method.getNameAsString()) && collector.getParentClass() != null) {
//					collector.setFound(true);
//				}
//			}
//			
//			super.visit(n, collector);
//			
//		}

		public String findSuperClass(String derivedClass, String method, String projectName, String base)
				throws IOException {

			String path = Util.getFlakyFile("D:\\Hårddisk D\\uppsats\\repos\\aletheia", derivedClass);

			FileInputStream testFileInputStream = new FileInputStream(path);
			CompilationUnit cu = StaticJavaParser.parse(testFileInputStream);
			VoidVisitor<Collector> methodNameCollector = new MethodClassCollector();
			Collector collector = new Collector(method, base, null);
			methodNameCollector.visit(cu, collector);
			while (!collector.isFound()) {
				String parent = collector.getParentClass();
				String classPath = Util.getFlakyFile("D:\\Hårddisk D\\uppsats\\repos\\aletheia", parent);
				testFileInputStream = new FileInputStream(classPath);
				cu = StaticJavaParser.parse(testFileInputStream);
				methodNameCollector = new MethodClassCollector();
				methodNameCollector.visit(cu, collector);
				// }
			}

			return collector.getParentClass();

		}

	}
}
