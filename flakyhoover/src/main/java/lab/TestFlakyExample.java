package lab;

import java.util.List;

public class TestFlakyExample {

	private List allAirportIds;
	private List allFlights;

//	File f1;
//	private int i = 0;
//	private Path path;
//	private String hello = "HELLO";
//	private static final int hej = 1000;
	private static RequestHandler handler;
//	private static final Config conf = new Config();
//	private SubcollectionBroken sc = new SubcollectionBroken(new Configuration());

//	Path dbDir;
//	Path segmentsDir;
//	private File f = new File("/");
//	File f1 = new File("/");
//	final static Path testdir = new Path("build/test/generator-test");
//	private SubcollectionBroken sc = new SubcollectionBroken(new Configuration());
//	
//	private static final Configuration config = new Configuration();
//	
//	 private static final String suffixes =
//			    "# this is a comment\n" +
//			    "\n" +
//			    ".gif\n" +
//			    ".jpg\n" +
//			    ".js\n";

//	  private Path generateFetchlist(int numResults, Configuration config,
//		      boolean filter) throws IOException {
//		    // generate segment
//		    Generator g = new Generator(config);
//		    Path[] generatedSegment = g.generate(dbDir, segmentsDir, -1, numResults,
//		        Long.MAX_VALUE, filter, false);
//		    if (generatedSegment==null) return null;
//		    return generatedSegment[0];
//		  }

//	public TestFlakyExample() {
//	Path currentPath = Paths.get("/");
//	 static File simpleDataFile = null;
//	public void doSomething1() {
////		File folder = new File("text2.txt");
//	}
//	   File dataFile;

//	    protected void setUp() throws IOException {
//	        dataFile = File.createTempFile("features", null, null);
//	    }

//	   private void registerClientLater(final String clientName, long delayInMillis){
//	        doLater(new TimerTask()
//	        {
//	            @Override
//	            public void run()
//	            {
//	                _clientRegistry.registerClient(clientName);
//	            }
//	        }, delayInMillis);
//	}
//	@Test
//	public void doSomething2() {
////		f.delete();
////		newMethod();
////		doSomething2();
//		
////        dataFile = File.createTempFile("features", null, null);
//
//	}
//	
//	public void test(File f, int i) { // (
////		File t = new File();
////		f.delete();
////		doSomething2(t,i);
////		test(t, i);
//
//		
//		
////		Util.writeRemoteObject(out, obj);
////		newMethod();
////		Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());
//
//	}
//	test(a, b);
//	f = new File();
//	public void test() {
//		if (conf != newConf) {
//			synchronized (conf) {
//				for (Map.Entry<String, String> entry : conf) {
//					if ((entry.getKey().matches("hcat.*")) && (newConf.get(entry.getKey()) == null)) {
//						newConf.set(entry.getKey(), entry.getValue());
//					}
//				}
//			}
//			conf = newConf;
//		}
//	}
//}

//	public void testRsReportsWrongServerName() throws Exception {
//		MiniHBaseCluster cluster = TEST_UTIL.getHBaseCluster();
//		MiniHBaseClusterRegionServer firstServer = (MiniHBaseClusterRegionServer) cluster.getRegionServer(0);
//		HRegionServer secondServer = cluster.getRegionServer(1);
//
//		HServerInfo hsi = firstServer.getServerInfo();
//		firstServer.setHServerInfo(new HServerInfo(hsi.getServerAddress(), hsi.getInfoPort(), hsi.getHostname()));
//		// Sleep while the region server pings back
//		Thread.sleep(2000);
//		assertTrue(firstServer.isOnline());
//		assertEquals(2, cluster.getLiveRegionServerThreads().size());
//
//		secondServer.getHServerInfo().setServerAddress(new HServerAddress("0.0.0.0", 60010));
//		Thread.sleep(2000);
//		assertTrue(secondServer.isOnline());
//		assertEquals(1, cluster.getLiveRegionServerThreads().size());
//	}
//
//	@Test(timeout = 180000)
//	public void notFlakytestRsReportsWrongServerName() throws Exception {
//		MiniHBaseCluster cluster = TEST_UTIL.getHBaseCluster();
//		MiniHBaseClusterRegionServer firstServer = (MiniHBaseClusterRegionServer) cluster.getRegionServer(0);
//		HRegionServer secondServer = cluster.getRegionServer(1);
//		HServerInfo hsi = firstServer.getServerInfo();
//		firstServer.setHServerInfo(new HServerInfo(hsi.getServerAddress(), hsi.getInfoPort(), hsi.getHostname()));
//
//		cluster.waitOnRegionServer(0);
//		assertEquals(2, cluster.getLiveRegionServerThreads().size());
//
//		secondServer.getHServerInfo().setServerAddress(new HServerAddress("0.0.0.0", 60010));
//		cluster.waitOnRegionServer(0);
//		assertEquals(1, cluster.getLiveRegionServerThreads().size());
//	}
//
//	@Test
//	public void testContinuousScheduling() throws Exception {
//		// set continuous scheduling enabled
//		FairScheduler fs = new FairScheduler();
//		Configuration conf = createConfiguration();
//		conf.setBoolean(FairSchedulerConfiguration.CONTINUOUS_SCHEDULING_ENABLED, true);
//		fs.reinitialize(conf, resourceManager.getRMContext());
//		Assert.assertTrue("Continuous scheduling shouldbe enabled.", fs.isContinuousSchedulingEnabled());
//
//		// Add one node
//		RMNode node1 = MockNodes.newNodeInfo(1, Resources.createResource(8 * 1024, 8), 1, "127.0.0.1");
//		NodeAddedSchedulerEvent nodeEvent1 = new NodeAddedSchedulerEvent(node1);
//		fs.handle(nodeEvent1);
//
//		// available resource
//		Assert.assertEquals(fs.getClusterCapacity().getMemory(), 8 * 1024);
//		Assert.assertEquals(fs.getClusterCapacity().getVirtualCores(), 8);
//
//		ApplicationAttemptId appAttemptId = createAppAttemptId(this.APP_ID++, this.ATTEMPT_ID++);
//		fs.addApplication(appAttemptId, "queue11", "user11");
//		List<ResourceRequest> ask = new ArrayList<ResourceRequest>();
//		ResourceRequest request = createResourceRequest(1024, 1, ResourceRequest.ANY, 1, 1, true);
//		ask.add(request);
//		fs.allocate(appAttemptId, ask, new ArrayList<ContainerId>(), null, null);
//
//		// waiting for continuous_scheduler_sleep_time
//		// at least one pass
//		Thread.sleep(fs.getConf().getContinuousSchedulingSleepMs() + 500);
//
//		// check consumption
//		Resource consumption = fs.applications.get(appAttemptId).getCurrentConsumption();
//		Assert.assertEquals(1024, consumption.getMemory());
//		Assert.assertEquals(1, consumption.getVirtualCores());
//
//	}

//	public boolean niftyTest() {
//
//		caller.instance();
//		boolean returnValue = testWriteToDB();
//		assert();
//		return returnValue;
//	}
//
//	public void testWriteToDB() throws IOException {
//		String insertQuery = "insert into ttt (id, name, ratio) values (?,?,?)";
//		pigServer.setBatchOn();
//		String dbStore = "org.apache.pig.piggybank.storage.DBStorage('" + driver + "', '" + dbUrl + "','" + user
//				+ "', '" + password + "', '" + insertQuery + "');";
//		pigServer.registerQuery("A = LOAD '" + INPUT_FILE + "' as (id:int, fruit:chararray, ratio:double);");
//		pigServer.registerQuery("STORE A INTO 'dummy' USING " + dbStore);
//		ExecJob job = pigServer.executeBatch().get(0);
//		ExecJob job = batch().get(0);
//		try {
//			while (!job.hasCompleted()) {
//				Thread.sleep(1000);
//			}
//
//		} catch (InterruptedException ie) {// ignore
//		}
//
//		assertNotSame("Failed: " + job.getException(), job.getStatus(), ExecJob.JOB_STATUS.FAILED);
//
//		Connection con = null;
//		String selectQuery = "select id, name, ratio from ttt order by name";
//		try {
//			con = DriverManager.getConnection(url, user, password);
//		} catch (SQLException sqe) {
//			throw new IOException("Unable to obtain database connection for data verification", sqe);
//		}
//		try {
//			PreparedStatement ps = con.prepareStatement(selectQuery);
//			ResultSet rs = ps.executeQuery();
//
//			int expId = 100;
//			String[] expNames = { "apple", "banana", "orange" };
//			double[] expRatios = { 1.0, 1.1, 2.0 };
//			for (int i = 0; i < 3 && rs.next(); i++) {
//				assertEquals("Id mismatch", expId, rs.getInt(1));
//				assertEquals("Name mismatch", expNames[i], rs.getString(2));
//				assertEquals("Ratio mismatch", expRatios[i], rs.getDouble(3), 0.0001);
//			}
//		} catch (SQLException sqe) {
//			throw new IOException("Unable to read data from database for verification", sqe);
//		}
//	}

//	@Test
//	public void testServiceTopPartitionsNoArg() throws Exception {
//		BlockingQueue<Map<String, Map<String, CompositeData>>> q = new ArrayBlockingQueue<>(1);
//		ColumnFamilyStore.all();
//		Executors.newCachedThreadPool().execute(() -> {
//			try {
//				q.put(StorageService.instance.samplePartitions(1000, 100, 10, Lists.newArrayList("READS", "WRITES")));
//				Thread.sleep(2000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//
//		SystemKeyspace.persistLocalMetadata();
//		Map<String, Map<String, CompositeData>> result = q.poll(11, TimeUnit.SECONDS);
//		List<CompositeData> cd = (List<CompositeData>) (Object) Lists.newArrayList(
//				((TabularDataSupport) result.get("system.local").get("WRITES").get("partitions")).values());
//		assertEquals(1, cd.size());
//	}
//
//	public void serviceTopPartitionsNoArg() {
//
//		// Should not invoke shared data?
//
//		// TODO:
//		// add whole SystemKeyspace.persistLocalMetadata() and not just SystemKeyspace
//
//		some.something(SystemKeyspace.persistLocalMetadata());
//
//	}

//	public void shakespeare() {
//		toBeAFunction(basicProxyAuthentication(), orNotAFunction, thatIsTheQuestion);
//	}
//
//	@Test
//	public void basicProxyAuthentication() throws Exception {
//		final AtomicBoolean finalHostReached = new AtomicBoolean(false);
//		handler = new RequestHandler() {
//
//			@Override
//			public void handle(Request request, HttpServletResponse response) {
//				finalHostReached.set(true);
//				response.setStatus(HTTP_OK);
//			}
//		};
//		assertTrue(get(url).useProxy("localhost", proxyPort).proxyBasic("user", "p4ssw0rd").ok());
//		assertEquals("user", proxyUser.get());
//		assertEquals("p4ssw0rd", proxyPassword.get());
//		assertEquals(true, finalHostReached.get());
//		assertEquals(1, proxyHitCount.get());
//	}
//
//	@Test
//	public void getReader() throws Exception {
//		handler = new RequestHandler() {
//
//			@Override
//			public void handle(Request request, HttpServletResponse response) {
//				response.setStatus(HTTP_OK);
//				instance(new Method(handler));
//				write("hello");
//			}
//		};
//		HttpRequest request = get(url);
//		assertTrue(request.ok());
//		BufferedReader reader = new BufferedReader(request.reader());
//		assertEquals("hello", reader.readLine());
//		reader.close();
//	}
//
//	private void createCrawlDB(ArrayList<URLCrawlDatum> list) throws IOException, Exception {
//		dbDir = new Path(testdir, "crawldb");
//		segmentsDir = new Path(testdir, "segments");
//		fs.mkdirs(dbDir);
//		fs.mkdirs(segmentsDir);
//
//		// create crawldb
//		CrawlDBTestUtil.createCrawlDb(conf, fs, dbDir, list);
//	}

//	private static String[][] cross(String[][] t1, String[][] t2) {
//		String[][] result = new String[t1.length * t2.length][];
//		for (int i = 0; i < result.length; i++) {
//			String[] r1 = t1[i / t2.length];
//			String[] r2 = t2[i % t2.length];
//			result[i] = new String[r1.length + r2.length];
//			System.arraycopy(r1, 0, result[i], 0, r1.length);
//			System.arraycopy(r2, 0, result[i], r1.length, r2.length);
//		}
//		return result;
//	}

//	public void testy() {

//		int i = Static.INT;

//		String s;
//		int i = 1000;
//		s = Static.i.instance();
//		methodcaller("param", para, "bolllll");
//	}

//	public void plany() {
//
//		Obj s;
//		int rs;
//
////		rs = s.executeQuery(PARAM, "select * from t t1 right outer join t t2 on 1=1");
//		rs = data[j][i];
//		s[a][b] = rs;
////		LOG.fd(Static.ins.msg());
//	}

//	@Test
//	public void basicProxyAuthentication() throws Exception {
//		final AtomicBoolean finalHostReached = new AtomicBoolean(false);
//		handler = new RequestHandler() {
//
//			@Override
//			public void handle(Request request, HttpServletResponse response) {
//				finalHostReached.set(true);
//				response.setStatus(HTTP_OK);
//			}
//		};
//		assertTrue(get(url).useProxy("localhost", proxyPort).proxyBasic("user", "p4ssw0rd").ok());
//		assertEquals("user", proxyUser.get());
//		assertEquals("p4ssw0rd", proxyPassword.get());
//		assertEquals(true, finalHostReached.get());
//		assertEquals(1, proxyHitCount.get());
//	}

//	public void testDisplayCurrentTime_whenever() {
//		// fixture setup
//		TimeDisplay sut = new TimeDisplay();
//		// exercise SUT
//		String result = sut.getCurrentTimeAsHtmlFragment();
//		// verify outcome
//		Calendar time = new DefaultTimeProvider().getTime();
//		StringBuffer expectedTime = new StringBuffer();
//		expectedTime.append("<span class=\"tinyBoldText\">");
//		if ((time.get(Calendar.HOUR_OF_DAY) == 0) && (time.get(Calendar.MINUTE) <= 1)) {
//			expectedTime.append("Midnight");
//		} else if ((time.get(Calendar.HOUR_OF_DAY) == 12) && (time.get(Calendar.MINUTE) == 0)) { // noon
//			expectedTime.append("Noon");
//		} else {
//			SimpleDateFormat fr = new SimpleDateFormat("h:mm a");
//			expectedTime.append(fr.format(time.getTime()));
//		}
//		expectedTime.append("</span>");
//		assertEquals(expectedTime, result);
//	}
//
//	public void verificationLogic() {
//		// verify Vancouver is in the list
//
//		int actual = null;
//		i = flightsFromCalgary.iterator();
//		while (i.hasNext()) {
//			FlightDto flightDto = (FlightDto) i.next();
//			if (flightDto.getFlightNumber().equals(expectedCalgaryToVan.getFlightNumber())) {
//				actual = flightDto;
//				assertEquals("Flight from Calgary to Vancouver", expectedCalgaryToVan, flightDto);
//				break;
//			}
//		}
//	}
//
//	public void testCombinationsOfInputValues() {
//		// Set up fixture
//		Calculator sut = new Calculator();
//		int expected; // TBD inside loops
//		for (int i = 0; i < 10; i++) {
//			for (int j = 0; j < 10; j++) {
//				// Exercise SUT
//				int actual = sut.calculate(i, j);
//				// Verify result
//				if (i == 3 & j == 4) { // special case
//					expected = 8;
//				} else {
//					expected = i + j;
//				}
//
//				assertEquals(message(i, j), expected, actual);
//			}
//		}
//	}
//
//	public void testMultipleValueSets() {
//		// Set Up Fixture
//		Calculator sut = new Calculator();
////		TestValues[] testValues = { new TestValues(1, 2, 3), new TestValues(2, 3, 5), new TestValues(3, 4, 8),
////				new TestValues(4, 5, 9) };
////		// special case!
//
//		List<String> testValues = new ArrayList<String>();
//
//		for (int i = 0; i < testValues.length; i++) {
////			TestValues values = testValues[i];
//			TestValues values = testValues.get(i);
//			// Exercise SUT
//			int actual = sut.calculate(values.a, values.b);
//			// Verify Result
//			assertEquals(message(i), values.expectedSum, actual);
//		}
//	}

//	public void nifty() {
//
//		List<String> testValues = new ArrayList<String>();
//
//		for (int i = 0; i < testValues.size(); ++i) {
//			int i = testValues.get(i);
//			assertTrue(true);
//		}
//
//	}

//	public void rift() {
//		List<String> strings = new List<String>();
//		ArrayList<String> string1 = new ArrayList<String>();
//		Set<String> string1 = new HashSet<String>();
//		Vector<String> v = new Vector<String>();// creating vector
//		TestValues[] testValues = { new TestValues(1, 2, 3), new TestValues(2, 3, 5), new TestValues(3, 4, 8),
//				new TestValues(4, 5, 9) };
//
//	}

//	public void testNullabilityInValues() throws SQLException {
//		Statement s = createStatement();
//		assertStatementError(VALUES_WITH_NULL, s,
//				"select a.* from (values (null)) a left outer join " + "(values ('a')) b on 1=1");
//		assertStatementError(VALUES_WITH_NULL, s, "select a.* from (values (null)) a");
//
//		String[][] expectedResult = { { "a" }, { "a" }, { "b" }, { "b" }, { null }, { null } };
//		JDBC.assertUnorderedResultSet(s.executeQuery("select a.* from (values ('a'),('b'),(cast(null as char(1)))) "
//				+ "a left outer join (values ('c'),('d')) b on 1=1"), expectedResult);
//	}
//	public void testGetFlightsByOrigin_NoInboundFlight_SMRTD() throws Exception {
//		// Set Up Fixture
//		BigDecimal outboundAirport = createTestAirport("1OF");
//		BigDecimal inboundAirport = null;
//		FlightDto expFlightDto = null;
//		try {
//			inboundAirport = createTestAirport("1IF");
//			expFlightDto = createTestFlight(outboundAirport, inboundAirport);
//			// Exercise System
//			List flightsAtDestination1 = facade.getFlightsByOriginAirport(inboundAirport);
//			// Verify Outcome
//			assertEquals(0, flightsAtDestination1.size());
//		} finally {
//			try {
//				facade.removeFlight(expFlightDto.getFlightNumber());
//			} finally {
//				try {
//					facade.removeAirport(inboundAirport);
//				} finally {
//					facade.removeAirport(outboundAirport);
//				}
//			}
//		}
//	}
//
//	protected void setUp() throws Exception {
//		allAirportIds = new ArrayList();
//		allFlights = new ArrayList();
//	}
//
//	protected void tearDown() throws Exception {
//		removeObjects(allAirportIds, "Airport");
//		removeObjects(allFlights, "Flight");
//	}
//
//	private BigDecimal createTestAirport(String airportName) throws FlightBookingException {
//		BigDecimal newAirportId = facade.createAirport(airportName, " Airport" + airportName, "City" + airportName);
//		allAirportIds.add(newAirportId);
//		return newAirportId;
//	}
//
//	public void removeObjects(List objectsToDelete, String type) {
//		Iterator i = objectsToDelete.iterator();
//		while (i.hasNext()) {
//			try {
//				BigDecimal id = (BigDecimal) i.next();
//				if ("Airport" == type) {
//					facade.removeAirport(id);
//				} else {
//					facade.removeFlight(id);
//				}
//			} catch (Exception e) {
//				// do nothing if the remove failed
//			}
//		}
//	}
//
//	public void testGetFlightsByOriginAirport_OneOutboundFlight() throws Exception {
//		// Fixture Setup
//		BigDecimal outboundAirport = createTestAirport("1OF");
//		BigDecimal inboundAirport = createTestAirport("1IF");
//		FlightDto expectedFlightDto = createTestFlight(outboundAirport, inboundAirport);
//		// Exercise System
//		List flightsAtOrigin = facade.getFlightsByOriginAirport(outboundAirport);
//		// Verify Outcome
//		assertOnly1FlightInDtoList("Flights at origin", expectedFlightDto, flightsAtOrigin);
//	}

//	public void testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody() {
//		// Set Up Fixture
//		Calculator sut = new Calculator();
//
//		List<String> testValues = new ArrayList<String>(Arrays.asList("setUp", "tearDown"));
//
//		for (int i = 0; i < testValues.length; i++) {
//			// Exercise SUT
//			TestValues values = testValues.get(i);
//			if(true) {
//				assertFalse(false);
//			}
//			
//
//			int actual = sut.calculate(values.a, values.b);
//			// Verify Result
//			assertEquals(message(i), values.expectedSum, actual);
//		}
//	}

//	public void testDisplayCurrentTime_whenever() {
//		// fixture setup
//		TimeDisplay sut = new TimeDisplay();
//		// exercise SUT
//		String result = sut.getCurrentTimeAsHtmlFragment();
//		// verify outcome
//		Calendar time = new DefaultTimeProvider().getTime();
//		StringBuffer expectedTime = new StringBuffer();
//		expectedTime.append("<span class=\"tinyBoldText\">");
//		if ((time.get(Calendar.HOUR_OF_DAY) == 0) && (time.get(Calendar.MINUTE) <= 1)) {
//			expectedTime.append("Midnight");
//		} else if ((time.get(Calendar.HOUR_OF_DAY) == 12) && (time.get(Calendar.MINUTE) == 0)) { // noon
//			expectedTime.append("Noon");
//		} else {
//			SimpleDateFormat fr = new SimpleDateFormat("h:mm a");
//			expectedTime.append(fr.format(time.getTime()));
//		}
//		expectedTime.append("</span>");
//		assertEquals(expectedTime, result);
//	}

	public void testMultipleValueSets() {
		// Set Up Fixture
		Calculator sut = new Calculator();
		TestValues[] testValues = { new TestValues(1, 2, 3), new TestValues(2, 3, 5), new TestValues(3, 4, 8), // special
																												// case!
				new TestValues(4, 5, 9) };
		for (int i = 0; i < testValues.length; i++) {
			TestValues values = testValues[i];
			if (values == "FlakyBehaviour") {
				assertFalse(values == "Cause error");
			} else {
				assertEquals(values.equals("If no conditions in for loop, then ur good"));
			}
			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Outcome
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

}

//	SubcollectionBroken sc = new SubcollectionBroken(new Configuration());
//
//	public void testInstallFeatureWithDependantFeaturesAndRangeWithoutPreinstall() throws Exception {
//
//		String name = getJarUrl(Bundle.class);
//
//		File tmp = File.createTempFile("smx", ".feature");
//		PrintWriter pw = new PrintWriter(new FileWriter(tmp));
//		pw.println("<features xmlns=\"http://karaf.apache.org/xmlns/features/v1.0.0\">");
//		pw.println("  <feature name=\"f1\" version=\"0.1\">");
//		pw.println("    <feature version=\"[0.1,0.3)\">f2</feature>");
//		pw.println("  </feature>");
//		pw.println("  <feature name=\"f2\" version=\"0.1\">");
//		pw.println("    <bundle>" + name + "</bundle>");
//		pw.println("  </feature>");
//		pw.println("  <feature name=\"f2\" version=\"0.2\">");
//		pw.println("    <bundle>" + name + "</bundle>");
//		pw.println("  </feature>");
//		pw.println("</features>");
//		pw.close();
//		
//
//		URI uri = tmp.toURI();
//
//		BundleContext bundleContext = prepareBundleContextForInstallUninstall();
//
//		FeaturesServiceImpl svc = new FeaturesServiceImpl();
//		svc.setBundleContext(bundleContext);
//		svc.addRepository(uri);
//
//		svc.installFeature("f1", "0.1");
//
//		// Uninstall repository
//		svc.uninstallFeature("f1", "0.1");
//		svc.uninstallFeature("f2", "0.2");
//	}
//	public void testDefaultCacheDir() {
//		// test with an URL
//		configure.setUrl(getClass().getResource("ivysettings-defaultCacheDir.xml"));
//		configure.setSettingsId("test");
//		configure.execute();
//
//		assertEquals(new File("mycache").getAbsolutePath(), project.getProperty("ivy.cache.dir.test"));
//
//		// test with a File
//		project = new Project();
//		configure = new IvyConfigure();
//		configure.setProject(project);
//		configure.setFile(new File("test/java/org/apache/ivy/ant/ivysettings-defaultCacheDir.xml"));
//		configure.setSettingsId("test2");
//		configure.execute();
//
//		assertEquals(new File("mycache").getAbsolutePath(), project.getProperty("ivy.cache.dir.test2"));
//
//		// test if no defaultCacheDir is specified
//		project = new Project();
//		configure = new IvyConfigure();
//		configure.setProject(project);
//		configure.setFile(new File("test/java/org/apache/ivy/ant/ivysettings-noDefaultCacheDir.xml"));
//		configure.setSettingsId("test3");
//		configure.execute();
//
//		assertNotNull(project.getProperty("ivy.cache.dir.test3"));
//	}

//	public void method() {
//		File f = new File(path);
////		path.getFileSystem();
//
//	}

//	public void method1(String s1, File f1337) {
//
//		if (true) {
//			assertTrue(1, 1);
//
//		} else if (1 == 1) {
//			assertTrue(1, 1);
//
//		} else {
//			assertTrue(1, 1);
//		}
//
//		{
//			caller();
//		}
//	}
//	public void shakespeare() {
//		caller(basicProxyAuthentication());
//	}
//
//	@Test
//	public void basicProxyAuthentication() throws Exception {
//		final AtomicBoolean finalHostReached = new AtomicBoolean(false);
//		handler = new RequestHandler() {
//
//			@Override
//			public void handle(Request request, HttpServletResponse response) {
//				finalHostReached.set(true);
//				response.setStatus(HTTP_OK);
//			}
//		};
//		assertTrue(get(url).useProxy("localhost", proxyPort).proxyBasic("user", "p4ssw0rd").ok());
//		assertEquals("user", proxyUser.get());
//		assertEquals("p4ssw0rd", proxyPassword.get());
//		assertEquals(true, finalHostReached.get());
//		assertEquals(1, proxyHitCount.get());
//	}
//
//	@Test
//	public void getReader() throws Exception {
//		handler = new RequestHandler() {
//
//			@Override
//			public void handle(Request request, HttpServletResponse response) {
//				response.setStatus(HTTP_OK);
//				instance(new Method(handler));
//				write("hello");
//			}
//		};
//		HttpRequest request = get(url);
//		assertTrue(request.ok());
//		BufferedReader reader = new BufferedReader(request.reader());
//		assertEquals("hello", reader.readLine());
//		reader.close();
//	}

//	newMethod2("s");
//	FileInputFormat.setInputPaths(job, new File(path));

//	obj.getInstance(myparam).function(new Obj(functioncall(param, 1, transform(path))));
//	path..getFileSystem();
//	friend(path);

//	FileInputFormat.setInputPaths().getIvory();

//	@Test
//	public void testServiceTopPartitionsNoArg() throws Exception {
//
//		BlockingQueue<Map<String, Map<String, CompositeData>>> q = new ArrayBlockingQueue<>(1);
//		ColumnFamilyStore.all();
//		Executors.newCachedThreadPool().execute(() -> {
//			try {
//				q.put(StorageService.instance.samplePartitions(1000, 100, 10, Lists.newArrayList("READS", "WRITES")));
//				Thread.sleep(2000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//
//		SystemKeyspace.persistLocalMetadata();
//		Map<String, Map<String, CompositeData>> result = q.poll(11, TimeUnit.SECONDS);
//		List<CompositeData> cd = (List<CompositeData>) (Object) Lists.newArrayList(
//				((TabularDataSupport) result.get("system.local").get("WRITES").get("partitions")).values());
//		assertEquals(1, cd.size());
//	}

//	f.delete();
//	service.setStorageLocation(new File("target/instances/" + System.currentTimeMillis()));
//	FileInputFormat.setInputPaths(job, new Path(baseDir.getAbsolutePath()));
//	_cache = new File("build/cache");

//	f.getinstanceof().close();
//	public void newMethod2(String i) { // , File file, Path path

//		request.execute(new AsyncCompletionHandler<Object>() {
//			@Override
//			public Object onCompleted(Response response) throws Exception {
//				return response;
//			}
//		});
//
//	}

//	sc.getInstance().somethingELse(param).callSomething();
//	    Path regionDir = region.getRegionDir();
//		Files.isExecutable(path);
//		
//		File f1 = new File("/");

//		Path path = region.getRegionDir();
//	    List<String> storeFiles = getRegionStoreFiles(fs, regionDir);
//		if (Files.exists(path)) {
//		long fsize = fs.getFileStatus(path).getLen();
//		}

//		f1.exists();

//		if (f1.exists()) {
//		f1.delete();
//		}

//		Path path = new Path(options.rootDir, "SeqFile.Performance");

//		path.equals("asa");
//		f1.exists();
//		f1.delete();
//
//		if (!Files.isWritable(path)) {
////	    	
//			timeWrite(path, appendable, options.keyLength, options.valueLength, options.fileSize);
//		}

//		ResolveReport report = ivy.resolve(LatestConflictManagerTest.class.getResource("ivy-388.xml"), getResolveOptions());
//		boolean b = report.getConfigurationReport(conf).getDependency(node.getResolvedId()) != null;
//		methodcall();

//		Integer.parseInt("3");

// File f1 = new File("/");
//		Path regionDir = Path();
//		Files.exists(regionDir.getFileName());
//		 FSDataInputStream fin = fs.open(uTfile);
//		 f1.open();

//		Path regionDir = region.getRegionDir();
//		List<String> storeFiles = getRegionStoreFiles(fs, regionDir);

//	public void newMethod() {
//		print();
//		Util.writeRemoteObject(out, obj);
//        JDBC.assertNullability(rs,
//                new boolean[]{true, true, true, false, false, true});
////		f.exists();
//        Path regionDir = HRegion.getRegionDir(region.getTableDir().getParent(), region.getRegionInfo());
//		Path regionDir = HRegion.getRegionDir(somerandomshit(), region.getRegionInfo());
//		f1.delete();

//	}
//	

//	public void testy() {
//		newMethod();
//	}

//}

//	
//	  @Test(expected = OrphanHLogAfterSplitException.class)
//	  public void testSplitFailsIfNewHLogGetsCreatedAfterSplitStarted()
//	  throws IOException {
//	    AtomicBoolean stop = new AtomicBoolean(false);
//
//	    assertFalse("Previous test should clean up table dir",
//	      fs.exists(new Path("/hbase/t1")));
//
//	    generateHLogs(-1);
//
//	    CountDownLatch latch = new CountDownLatch(1);
//	    try {
//	      (new ZombieNewLogWriterRegionServer(latch, stop)).start();
//	      HLogSplitter logSplitter = HLogSplitter.createLogSplitter(conf, hbaseDir, hlogDir, oldLogDir,
//	        fs);
//	      logSplitter.splitLog(latch);
//	    } finally {
//	      stop.set(true);
//	    }
//	}

//    private void createCache() {
//        _cache = new File("build/cache");
//        _cache.mkdirs();
//    }

//public void testIvy388() throws Exception {
//List deps = report.getDependencies();
//ResolveReport report = ivy.resolve(LatestConflictManagerTest.class.getResource("ivy-388.xml"), getResolveOptions());
//
//Iterator dependencies = deps.iterator();
//String[] confs = report.getConfigurations();
//while (dependencies.hasNext()) {
//  IvyNode node = (IvyNode) dependencies.next();
//  for (int i = 0; i < confs.length; i++) {
//      String conf = confs[i];
//      if (!node.isEvicted(conf)) {
//
//
//          boolean flag1 = report.getConfigurationReport(conf).getDependency(node.getResolvedId()) != null;
//          boolean flag2 = report.getConfigurationReport(conf).getModuleRevisionIds().contains(node.getResolvedId());
//          assertEquals("Inconsistent data for node " + node + " in conf " + conf, flag1, flag2);
//      }
//      try {
//    	  int is = 0;
//      }catch(Exception e) {
//
//	      }
//	  }
//	}
//}

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

// Files.isReadable(currentPath);
// Files.isExecutable(currentPath);
//
//		String singlePartitionedFile = simpleDataFile.getAbsolutePath();
//		FileInputFormat.setInputPaths(job, new Path(baseDir.getAbsolutePath()));
//_cache = new File("build/cache");

//		dataFile.exists();

// Files.isReadable(folder.toPath());
//		 expect(bundleContext.getDataFile(EasyMock.<String>anyObject())).andReturn(dataFile).anyTimes();

//        AdminServiceImpl service = new AdminServiceImpl();
//        service.setStorageLocation(new File("target/instances/" + System.currentTimeMillis() ));
//FileInputFormat.setInputPaths(job, new Path(baseDir.getAbsolutePath()));
//_cache = new File("build/cache");

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
