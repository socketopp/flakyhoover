package lab;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;
import javax.security.auth.login.Configuration;

import com.google.common.collect.Lists;

public class FireAndForgetTest {

	public void testRsReportsWrongServerName() throws Exception {
		MiniHBaseCluster cluster = TEST_UTIL.getHBaseCluster();
		MiniHBaseClusterRegionServer firstServer = (MiniHBaseClusterRegionServer) cluster.getRegionServer(0);
		HRegionServer secondServer = cluster.getRegionServer(1);

		HServerInfo hsi = firstServer.getServerInfo();
		firstServer.setHServerInfo(new HServerInfo(hsi.getServerAddress(), hsi.getInfoPort(), hsi.getHostname()));
		// Sleep while the region server pings back
		Thread.sleep(2000);
		assertTrue(firstServer.isOnline());
		assertEquals(2, cluster.getLiveRegionServerThreads().size());

		secondServer.getHServerInfo().setServerAddress(new HServerAddress("0.0.0.0", 60010));
		Thread.sleep(2000);
		assertTrue(secondServer.isOnline());
		assertEquals(1, cluster.getLiveRegionServerThreads().size());
	}

	public void niftyTest() {

		boolean returnValue = new Object(functionCall(testWriteToDB()));
		assertTrue(true);
	}

	@Test(timeout = 180000)
	public void notFlakytestRsReportsWrongServerName() throws Exception {
		MiniHBaseCluster cluster = TEST_UTIL.getHBaseCluster();
		MiniHBaseClusterRegionServer firstServer = (MiniHBaseClusterRegionServer) cluster.getRegionServer(0);
		HRegionServer secondServer = cluster.getRegionServer(1);
		HServerInfo hsi = firstServer.getServerInfo();
		firstServer.setHServerInfo(new HServerInfo(hsi.getServerAddress(), hsi.getInfoPort(), hsi.getHostname()));

		cluster.waitOnRegionServer(0);
		assertEquals(2, cluster.getLiveRegionServerThreads().size());

		secondServer.getHServerInfo().setServerAddress(new HServerAddress("0.0.0.0", 60010));
		cluster.waitOnRegionServer(0);
		assertEquals(1, cluster.getLiveRegionServerThreads().size());
	}

//	testWriteToDB taken From:
//	https://github.com/apache/pig/blob/branch-0.8/contrib/piggybank/java/src/test/java/org/apache/pig/piggybank/test/storage/TestDBStorage.java
	public void testWriteToDB() throws IOException {
		String insertQuery = "insert into ttt (id, name, ratio) values (?,?,?)";
		pigServer.setBatchOn();
		String dbStore = "org.apache.pig.piggybank.storage.DBStorage('" + driver + "', '" + dbUrl + "','" + user
				+ "', '" + password + "', '" + insertQuery + "');";
		pigServer.registerQuery("A = LOAD '" + INPUT_FILE + "' as (id:int, fruit:chararray, ratio:double);");
		pigServer.registerQuery("STORE A INTO 'dummy' USING " + dbStore);
		ExecJob job = pigServer.executeBatch().get(0);
		try {
			while (!job.hasCompleted())
				Thread.sleep(1000);
		} catch (InterruptedException ie) {// ignore
		}

		assertNotSame("Failed: " + job.getException(), job.getStatus(), ExecJob.JOB_STATUS.FAILED);

		Connection con = null;
		String selectQuery = "select id, name, ratio from ttt order by name";
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException sqe) {
			throw new IOException("Unable to obtain database connection for data verification", sqe);
		}
		try {
			PreparedStatement ps = con.prepareStatement(selectQuery);
			ResultSet rs = ps.executeQuery();

			int expId = 100;
			String[] expNames = { "apple", "banana", "orange" };
			double[] expRatios = { 1.0, 1.1, 2.0 };
			for (int i = 0; i < 3 && rs.next(); i++) {
				assertEquals("Id mismatch", expId, rs.getInt(1));
				assertEquals("Name mismatch", expNames[i], rs.getString(2));
				assertEquals("Ratio mismatch", expRatios[i], rs.getDouble(3), 0.0001);
			}
		} catch (SQLException sqe) {
			throw new IOException("Unable to read data from database for verification", sqe);
		}
	}

	@Test
	public void testServiceTopPartitionsNoArg() throws Exception {
		BlockingQueue<Map<String, Map<String, CompositeData>>> q = new ArrayBlockingQueue<>(1);
		ColumnFamilyStore.all();
		Executors.newCachedThreadPool().execute(() -> {
			try {
				q.put(StorageService.instance.samplePartitions(1000, 100, 10, Lists.newArrayList("READS", "WRITES")));
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		SystemKeyspace.persistLocalMetadata();
		Map<String, Map<String, CompositeData>> result = q.poll(11, TimeUnit.SECONDS);
		List<CompositeData> cd = (List<CompositeData>) (Object) Lists.newArrayList(
				((TabularDataSupport) result.get("system.local").get("WRITES").get("partitions")).values());
		assertEquals(1, cd.size());
	}

	@Test
	public void testContinuousScheduling() throws Exception {
		// set continuous scheduling enabled
		FairScheduler fs = new FairScheduler();
		Configuration conf = createConfiguration();
		conf.setBoolean(FairSchedulerConfiguration.CONTINUOUS_SCHEDULING_ENABLED, true);
		fs.reinitialize(conf, resourceManager.getRMContext());
		Assert.assertTrue("Continuous scheduling shouldbe enabled.", fs.isContinuousSchedulingEnabled());

		// Add one node
		RMNode node1 = MockNodes.newNodeInfo(1, Resources.createResource(8 * 1024, 8), 1, "127.0.0.1");
		NodeAddedSchedulerEvent nodeEvent1 = new NodeAddedSchedulerEvent(node1);
		fs.handle(nodeEvent1);

		// available resource
		Assert.assertEquals(fs.getClusterCapacity().getMemory(), 8 * 1024);
		Assert.assertEquals(fs.getClusterCapacity().getVirtualCores(), 8);

		ApplicationAttemptId appAttemptId = createAppAttemptId(this.APP_ID++, this.ATTEMPT_ID++);
		fs.addApplication(appAttemptId, "queue11", "user11");
		List<ResourceRequest> ask = new ArrayList<ResourceRequest>();
		ResourceRequest request = createResourceRequest(1024, 1, ResourceRequest.ANY, 1, 1, true);
		ask.add(request);
		fs.allocate(appAttemptId, ask, new ArrayList<ContainerId>(), null, null);

		// waiting for continuous_scheduler_sleep_time
		// at least one pass
		Thread.sleep(fs.getConf().getContinuousSchedulingSleepMs() + 500);

		// check consumption
		Resource consumption = fs.applications.get(appAttemptId).getCurrentConsumption();
		Assert.assertEquals(1024, consumption.getMemory());
		Assert.assertEquals(1, consumption.getVirtualCores());
	}

	// FLAKY pattern
	// SRC: https://martinfowler.com/articles/nonDeterminism.html
	public void asyncIssue() {
		int pollingInterval = 3000;
		makeAsyncCall();
		startTime = Time.now;
		while (!responseReceived()) {
			if (Time.now - startTime > waitLimit) {
				throw new TestTimeoutException();
			}
			Thread.sleep(pollingInterval);
		}
		int value = readResponse();
		assertTrue(1, vluae);
	}

	public void asyncIssueWithoutSleep() {
		int pollingInterval = 3000;
		makeAsyncCall();
		startTime = Time.now;
		while (!responseReceived()) {
			if (Time.now - startTime > waitLimit) {
				throw new TestTimeoutException();
			}
		}
		int value = readResponse();
		assertTrue(1, vluae);
	}

//	TODO implement a mechanism that detects the while loop in noFlakytestContinuousScheduling.
//	noFlakytestContinuousScheduling is a fix from testContinuousScheduling. Only difference is the while loop.

//	  @Test (timeout = 5000)
//	  public void noFlakytestContinuousScheduling() throws Exception {
//	    // set continuous scheduling enabled
//	    FairScheduler fs = new FairScheduler();
//	    Configuration conf = createConfiguration();
//	    conf.setBoolean(FairSchedulerConfiguration.CONTINUOUS_SCHEDULING_ENABLED,
//	            true);
//	    fs.reinitialize(conf, resourceManager.getRMContext());
//	    Assert.assertTrue("Continuous scheduling should be enabled.",
//	            fs.isContinuousSchedulingEnabled());
//
//	    // Add one node
//	    RMNode node1 =
//	            MockNodes.newNodeInfo(1, Resources.createResource(8 * 1024, 8), 1,
//	                    "127.0.0.1");
//	    NodeAddedSchedulerEvent nodeEvent1 = new NodeAddedSchedulerEvent(node1);
//	    fs.handle(nodeEvent1);
//
//	    // available resource
//	    Assert.assertEquals(fs.getClusterCapacity().getMemory(), 8 * 1024);
//	    Assert.assertEquals(fs.getClusterCapacity().getVirtualCores(), 8);
//
//	    // send application request
//	    ApplicationAttemptId appAttemptId =
//	            createAppAttemptId(this.APP_ID++, this.ATTEMPT_ID++);
//	    fs.addApplication(appAttemptId, "queue11", "user11");
//	    List<ResourceRequest> ask = new ArrayList<ResourceRequest>();
//	    ResourceRequest request =
//	            createResourceRequest(1024, 1, ResourceRequest.ANY, 1, 1, true);
//	    ask.add(request);
//	    fs.allocate(appAttemptId, ask, new ArrayList<ContainerId>(), null, null);
//
//	    // waiting for continuous_scheduler_sleep_time
//	    // at least one pass
//	    Thread.sleep(fs.getConf().getContinuousSchedulingSleepMs() + 500);
//
//	    FSSchedulerApp app = fs.applications.get(appAttemptId);
//	    // Wait until app gets resources.
//	    while (app.getCurrentConsumption().equals(Resources.none())) { }
//	    
//	    // check consumption
//	    Assert.assertEquals(1024, app.getCurrentConsumption().getMemory());
//	    Assert.assertEquals(1, app.getCurrentConsumption().getVirtualCores());
//	  }

}
