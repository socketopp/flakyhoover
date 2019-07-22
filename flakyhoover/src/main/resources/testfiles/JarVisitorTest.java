package testfiles;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class JarVisitorTest extends PackagingTestCase {

	// SRC:
	// https://github.com/hibernate/hibernate-orm/blob/4.0/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/JarVisitorTest.java
	// Flaky
	@Test
	@TestForIssue(jiraKey = "HHH-6806")
	public void testJarVisitorFactoryNested() throws Exception {

		// setting URL to accept vfs based protocol
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if ("vfszip".equals(protocol) || "vfsfile".equals(protocol))
					return new URLStreamHandler() {
						protected URLConnection openConnection(URL u) throws IOException {
							assertEquals(ExplodedJarVisitor.class.getName(), jarVisitor.getClass().getName());

							return null;
						}
					};
				return null;
			}
		});
	}

	// SRC:
	// https://github.com/hibernate/hibernate-orm/blob/4.0/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/JarVisitorTest.java
	// Flaky
	@Test
	@TestForIssue(jiraKey = "HHH-6806")
	public void testJarVisitorFactory() throws Exception {

		// setting URL to accept vfs based protocol
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if ("vfszip".equals(protocol) || "vfsfile".equals(protocol))
					return new URLStreamHandler() {
						protected URLConnection openConnection(URL u) throws IOException {
							return null;
						}
					};
				return null;
			}
		});
		URL jarUrl = new URL("file:./target/packages/defaultpar.par");
		JarVisitor jarVisitor = JarVisitorFactory.getVisitor(jarUrl, getFilters(), null);
		assertEquals(FileZippedJarVisitor.class.getName(), jarVisitor.getClass().getName());

		jarUrl = new URL("file:./target/packages/explodedpar");
		jarVisitor = JarVisitorFactory.getVisitor(jarUrl, getFilters(), null);
		assertEquals(ExplodedJarVisitor.class.getName(), jarVisitor.getClass().getName());

		jarUrl = new URL("vfszip:./target/packages/defaultpar.par");
		jarVisitor = JarVisitorFactory.getVisitor(jarUrl, getFilters(), null);
		assertEquals(FileZippedJarVisitor.class.getName(), jarVisitor.getClass().getName());

		jarUrl = new URL("vfsfile:./target/packages/explodedpar");
		jarVisitor = JarVisitorFactory.getVisitor(jarUrl, getFilters(), null);
		assertEquals(ExplodedJarVisitor.class.getName(), jarVisitor.getClass().getName());

	}

	// NOT FLAKY
	@Test
	public void testJarVisitor() throws Exception {

		URL jarUrl = new URL(" file :./ target / packages / defaultpar . par ");
		JarVisitor.setupFilters();
		JarVisitor jarVisitor = JarVisitorFactory.getVisitor(jarUrl, JarVisitor.getFilters(), null);
		assertEquals(JarVisitor.class.getName(), jarVisitor.getClass().getName());
	}

	// NOT FLAKY
	@Test
	public void testExplodedJarVisitor() throws Exception {

		URL jarUrl = new URL(" file :./ target / packages / explodedpar ");
		ExplodedJarVisitor.setupFilters();
		ExplodedJarVisitor jarVisitor = JarVisitorFactory.getVisitor(jarUrl, ExplodedJarVisitor.getFilters(), null);
		assertEquals(ExplodedJarVisitor.class.getName(), jarVisitor.getClass().getName());
	}

	// NOT FLAKY
	@Test
	public void testFileZippedJarVisitor() throws Exception {

		URL jarUrl = new URL(" vfszip :./ target / packages / defaultpar . par ");
		FileZippedJarVisitor.setupFilters();
		FileZippedJarVisitor jarVisitor = JarVisitorFactory.getVisitor(jarUrl, JarVisitor.getFilters(), null);
		assertEquals(FileZippedJarVisitor.class.getName(), jarVisitor.getClass().getName());
	}

}
