package testfiles;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import junit.framework.TestCase;

public class IvyBuildListTest extends TestCase {

	/*
	 * Those tests use the ivy files A , B , C , D , E in test/buildlist The
	 * dependencies are : A -> C B has no dependency C -> B D -> A , B E has no
	 * dependency F -> G G -> F
	 */

	// CheckStyle:MagicNumber| OFF
	// The test very often use MagicNumber. Using a constant is less expressive.

	public void testSimple() {
		Project p = new Project();

		IvyBuildList buildlist = new IvyBuildList();
		buildlist.setProject(p);

		FileSet fs = new FileSet();
		fs.setDir(new File("test/buildlist"));
		fs.setIncludes("**/build.xml");
		fs.setExcludes("E2/build.xml,F/build.xml,G/build.xml");
		buildlist.addFileset(fs);
		buildlist.setOnMissingDescriptor("skip");
		buildlist.setReference("ordered.build.files");

		buildlist.execute();

		Object o = p.getReference("ordered.build.files");
		assertNotNull(o);
		assertTrue(o instanceof Path);

		Path path = (Path) o;
		String[] files = path.list();
		assertNotNull(files);

		assertEquals(5, files.length);

		assertEquals(new File("test/buildlist/B/build.xml").getAbsolutePath(), new File(files[0]).getAbsolutePath());
		assertEquals(new File("test/buildlist/C/build.xml").getAbsolutePath(), new File(files[1]).getAbsolutePath());
		assertEquals(new File("test/buildlist/A/build.xml").getAbsolutePath(), new File(files[2]).getAbsolutePath());
		assertEquals(new File("test/buildlist/D/build.xml").getAbsolutePath(), new File(files[3]).getAbsolutePath());
		assertEquals(new File("test/buildlist/E/build.xml").getAbsolutePath(), new File(files[4]).getAbsolutePath());
	}
}