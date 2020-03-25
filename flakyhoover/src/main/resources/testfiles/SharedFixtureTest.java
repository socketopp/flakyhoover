import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * 
 * @author Socke
 *
 */
public class SharedFixtureTest extends TestCase {

//	SomeClass tSc;
	
	File f = new File();
//	Path path2 = new Path("S#¤#¤#");
	Shared tObject = sahred();
	
//	@Test
//	public void method1() {

//		SomeClassB tScb = new SomeClassB(1, "20", reference, STATIC.reference);
//		tSc.oneMethod(d0, new Object(referecingArandomVar.newCall(d, d2)));
		

		
//		instance.useFile(file);	
//		File f = new File();

//		String absolutePath = f.getAbsolutePath();

		

//		f.getAbsolutePath();
//		
//		tObject.getInstance().callingMethod(new Creathing(caller(f.getAbsoluteFile())));
		
//		f.getAbsolutePath();
//		cs.testing(new File(method.call(path2)));
//		method(new File());
//		f.canExecute();
//		f.exists();
		
//		long fsize = fs.getFileStatus(path).getLen();
//		fs.getFileStatus(path, appendable, true, options.keyLength);
//		timeWrite(path, appendable, true, options.keyLength, ref.call());

//	}
	
	@Test
	private ArrayList<URLCrawlDatum> readContents(Path fetchlist) throws IOException {
		// verify results
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, fetchlist, conf);

		ArrayList<URLCrawlDatum> l = new ArrayList<URLCrawlDatum>();

		READ: do {
			Text key = new Text();
			CrawlDatum value = new CrawlDatum();
			if (!reader.next(key, value)) {
				break READ;
			}
			l.add(new URLCrawlDatum(key, value));
		} while (true);

		reader.close();
		return l;
		
	}
	

	


}