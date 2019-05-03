package lab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFlakyExample {
	
//	File f = new File("/");
//	f.exists();
//  f.delete();
    
	public TestFlakyExample() {}
//	Path currentPath = Paths.get("/");
//	 static File simpleDataFile = null;
//	public void doSomething1() {
////		File folder = new File("text2.txt");
//	
//	}
//	   File dataFile;

//	    protected void setUp() throws IOException {
//	        dataFile = File.createTempFile("features", null, null);
//	    }
	
	@Test
	public void doSomething2() {
		
//	    Path regionDir = region.getRegionDir();
//	    List<String> storeFiles = getRegionStoreFiles(fs, regionDir);
		
//		 Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());
//		 assertFalse(fs.exists(regionDir));
		
//	       File input = Util.createInputFile("tmp", "", 
//	                new String[] {"pigtester\t10\t1.2", "pigtester\t15\t1.2", 
//	                "pigtester2\t10\t1.2",
//	"pigtester3\t10\t1.2", "pigtester3\t20\t1.2", "pigtester3\t30\t1.2"});
		
//		
//	        pigServer.registerQuery("a = load '" 
//	                + Util.generateURI(input.toString(), pigContext) + "' using PigStorage() " 
//	+ "as (name:chararray, age:int, gpa:double);");
	       
//		path.exists();

//		Files.exists(path);
//	    long fsize = fs.getFileStatus(path).getLen();
		
//	    Path path = new Path(options.rootDir, "SeqFile.Performance");
//	    if(!Files.isWritable(path)) {
//	    	
//		    timeWrite(path, appendable, options.keyLength, options.valueLength,
//		    		options.fileSize);
//	    }



//	    File folder = new File("text2.txt");
//	    folder.exists();
//	    folder.getAbsoluteFile();

//		Path p2 = Paths.get("/foo","bar","gus");
//		Files.exists(p2);
		
//		Files.exists(currentPath);

//		currentPath.getParent();
//		Files.isWritable(currentPath);


//		 File tmp = File.createTempFile("smx", ".feature");

		//Files.isReadable(currentPath);
		//Files.isExecutable(currentPath);
		//
//		String singlePartitionedFile = simpleDataFile.getAbsolutePath();
//		FileInputFormat.setInputPaths(job, new Path(baseDir.getAbsolutePath()));
		
//		dataFile.exists();
		
		//Files.isReadable(folder.toPath());
//		 expect(bundleContext.getDataFile(EasyMock.<String>anyObject())).andReturn(dataFile).anyTimes();
		
//        AdminServiceImpl service = new AdminServiceImpl();
//        service.setStorageLocation(new File("target/instances/" + System.currentTimeMillis() ));

	}

}

//
//
//FileStore fz = Files.getFileStore(currentPath);
//



//assertTrue(1==1);
//
//
//	File file2;
//	final static File testfle = new File("build/test/generator-test");

//	final static Path testdir = Paths.get("build/test/generator-test", "NN.txt");
//
//
//	Path p2 = Paths.get("/foo","bar","gus");
//    Path path3 = FileSystems.getDefault().getPath("logs", "access.log");
//    Path p3 = Paths.get("D:\\dir\\file.txt");
//    Path path1 = p2.getParent();
////	private ReversePolishNotation reversePolishNotation = new ReversePolishNotation();
////	private ReversePolishNotation reversePolishNotation2 = new ReversePolishNotation();
////	private ReversePolishNotation reversePolishNotation3 = new ReversePolishNotation();
//
//
////	public void doSomething() {
//////
////////		final int NUM_RESULTS = 2;
////	    Path testdir = new Paths().get("/");
//////	    Path path = Paths.get("C:/home/joe/foo");
////	    File file = new File("text.txt");
//////
//////        String string = "getPluginFolder();";
//////        Properties properties = new Properties();
//////
//////
//////	    file2 = new File("text2.txt");
//////
////	}

//
//	public void func1() {
//		Double d = reversePolishNotation.calc("a");
//		func3();
//	 assertEquals(type, metadata.get("Work-Type"));
//
//	}
//
////	public void func3() {
////
////		doSomething();
////		doSomething1();
////
////
////		reversePolishNotation3.calc2("");
////	}
//
//
////	public void func3(String n) {
////
////	}
////
////	public void func3(int n) {
////		func3("sss");
////
////	}
//
//
//}
//
//
//
////    public void testArithExpressions() throws IOException, ExecException {
////        String inputFileName = "testArithExpressions-input.txt";
////        Util.createInputFile(cluster, inputFileName,
////                new String[] {"10\t11.0"});
////        pigServer.registerQuery("a = load '" + inputFileName + "' as (x:int, y:double);");
////        pigServer.registerQuery("b = foreach a generate x + null, x * null, x / null, x - null, null % x, " +
////        		"y + null, y * null, y / null, y - null;");
////        Iterator<Tuple> it = pigServer.openIterator("b");
////        Tuple t = it.next();
////        for (int i = 0; i < 9; i++) {
////            assertEquals(null, t.get(i));
////        }
////        Util.deleteFile(cluster, inputFileName);
////}
//
//
//
//
////	private static int a=15*15;
////	public int i;
////	private ReversePolishNotation reversePolishNotation = new ReversePolishNotation();
////	private ReversePolishNotation reversePolishNotation2 = new ReversePolishNotation();
////	private ReversePolishNotation reversePolishNotation3 = new ReversePolishNotation();
////
////	int f;
////	public boolean b = true;
////	int g = 0;
////	private String new_str = "WWW";
////	public String returnString() {
////		doSomething();
////		f += 1;
////		return "a";
////
////	}
////
////
//// [1,2,3]
////	[a,b,c]
////	1a, 1b, 1c,
////	2a,2b,2c
////	3a,3b,3c
////
////
////	public void findAllThings() {
//////		reversePolishNotation.calc2(new_str);
//////
////		reversePolishNotation.waitForResult();
////
//////		doSomething();
////
//////		String f = reversePolishNotation2.mem;
////
////	}
////
////	public void func1() {
////		findAllThings();
////		doSomething();
////	}
////	public void func2() {
////		findAllThings();
////	}
//
////	public boolean func3() {
//////		String s = returnString();
//////		f++;
////		reversePolishNotation.calc2(new_str);
////
////		return true;
////	}
//
//
//
//}
//
//
////public void findAllThing2s() {
//
////i++;
////g = 10;
////g+=1;
//////	reversePolishNotation.calc2(new_str);
////
////	reversePolishNotation.waitForResult();
//////	doSomething();
//////	new_str = returnString();
////	boolean bb = true;
////
//////	b = !b;
////
//////	String t = returnString();
//////	String f = reversePolishNotation3.mem;
//////	g = 10;
//////	!bb;
////
////	int i = -1;
////	b = !true;
////	g +=1;
////
////	++i;
////	i++;
////	i--;
////	--i;
////	i = ~i;
////	g = -g;
////	g = +g;
////}
//
//
//
////public boolean methodBoolCall(boolean boolInParam, int integerInt) {
////
////	int c=15*15;
////	ReversePolishNotation reversePolishNotation2 = new ReversePolishNotation();
////
////	int g;
////	boolean val = true;
////
////	return val;
////}
////
////public boolean methodBoolCall2(double doooble, Util util) {
////
////	int e = 3;
////	f = 2;
////	doooble++;
////	boolean b = methodBoolCall(true, 5);
////	reversePolishNotation.getClass();
////	return true;
////}
//
//
////	public FlakyExample(int _f) {
////		this.f = _f;
////	}
//
////	public  void main(String[] args) throws InterruptedException {
////	// TODO Auto-generated method stub
//////		i = 7;
////		boolean bool = true;
//////		reversePolishNotation.getClass();
////		boolean bool2 = false;
////		int greaterThanLolz4 = 1;
////		int h = 2;
//////
//
//
//
//
////		while((greaterThanLolz4 != h) || ((1==i) && (greaterThanLolz4>2))) {
////
////
////			greaterThanLolz4 = 1;
////			h = 2;
////			break;
////		}
//
////		i = 1;
////		break;
////		for(int i = 0; true; i++) {
////
////		}
////		while(bool) {
////			bool = false;
////		}
////
////		reversePolishNotation.getResource("s");
////		reversePolishNotation.waitForResult();
//
//
//
////		while(bool || bool && bool || (bool2 && bool) == bool2) {
////			bool=false;
////
////			break;
////		}
//
//
//
////		threadProblem();
//
//
////	}
//
//
////	public void testIvy388() throws Exception {
////	List deps = report.getDependencies();
////    ResolveReport report = ivy.resolve(LatestConflictManagerTest.class.getResource("ivy-388.xml"), getResolveOptions());
////
////	Iterator dependencies = deps.iterator();
////	String[] confs = report.getConfigurations();
////	while (dependencies.hasNext()) {
////	  IvyNode node = (IvyNode) dependencies.next();
////	  for (int i = 0; i < confs.length; i++) {
////	      String conf = confs[i];
////	      if (!node.isEvicted(conf)) {
////
////
////	          boolean flag1 = report.getConfigurationReport(conf).getDependency(node.getResolvedId()) != null;
////	          boolean flag2 = report.getConfigurationReport(conf).getModuleRevisionIds().contains(node.getResolvedId());
////	          assertEquals("Inconsistent data for node " + node + " in conf " + conf, flag1, flag2);
////	      }
////	      try {
////	    	  int is = 0;
////	      }catch(Exception e) {
////
////		      }
////		  }
////		}
////	}
//
////}
//
//
////
////do {
////	System.out.println();
////
////} while(true);
////}
////}
//
//
//
//
////public void flaky_cond() {
////File file = new File("newName.java");
////int i = 10;
////boolean cond = 4>2;
////while(cond) { //Report
////	break;
////}
////while(true) {
////	break;
////}
////while(i>2) { // report
////	break;
////}
////
////while ((5>0)==(2<i)) { // report
////	break;
////}
//
//
//
////while (methodBoolCall()) { // report
////	break;
////}
////
////
////int variable = 5;
////
////}
//
//
////@Test(timeout=5000)
////@Test
////public void threadProblem() throws InterruptedException {
////
////while(true) {
////	System.out.println("TIMEOUT??");
////	System.out.println();
////
////	break;
////}
////Thread.sleep(400);
////
////}
//
//
//
//
//
//
//
////Thread.sleep(499);
//
//
////public void ioIssue() throws IOException {
////
////Socket s = new Socket("localhost", 8000);
////DataInputStream dis = new DataInputStream(s.getInputStream());
////
////byte[] data = new byte[100];
////
////int read = dis.read(data);
////
////int len = dis.readInt();
////
////IOUtils.readFully(dis, data, 0, len);
////
////
////
////}
//
//
//
//
//
//
//
//
////Model model = new MavenXpp3Reader().read( new FileReader( pom ) );
//
////// MavenXpp3Reader leaves the file open, so we need to close it ourselves.
////FileReader reader = new FileReader( "www.txt" );
//////Model model = new MavenXpp3Reader().read( reader );
////
////reader.close();
////
//// FileReader fileReader =  new FileReader("TT.txt");
//// BufferedReader bufferedReader =
////            new BufferedReader(fileReader);
//
//
//
//
//
//
////public void flaky_resource_optimism_method() {
////
////	File file = new File("newName.java");
////	if(file.exists()) {
////
////	}
////
////}
//
//
//
//
//
//
//
