package testfiles;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;

import org.omg.CORBA.Request;

import com.google.common.collect.Lists;

public class TestRunWarTest {
	private static RequestHandler handler;
	private ClientRegistry _clientRegistry = new ClientRegistry();

	public void shakespeare() {
		toBeAFunction(basicProxyAuthentication(), orNotAFunction, thatIsTheQuestion);
	}

	@Test
	public void basicProxyAuthentication() throws Exception {
		final AtomicBoolean finalHostReached = new AtomicBoolean(false);
		handler = new RequestHandler() {

			@Override
			public void handle(Request request, HttpServletResponse response) {
				finalHostReached.set(true);
				response.setStatus(HTTP_OK);
			}
		};
		assertTrue(get(url).useProxy("localhost", proxyPort).proxyBasic("user", "p4ssw0rd").ok());
		assertEquals("user", proxyUser.get());
		assertEquals("p4ssw0rd", proxyPassword.get());
		assertEquals(true, finalHostReached.get());
		assertEquals(1, proxyHitCount.get());
	}

	@Test
	public void getReader() throws Exception {
		handler = new RequestHandler() {

			@Override
			public void handle(Request request, HttpServletResponse response) {
				response.setStatus(HTTP_OK);
				instance(new Method(handler));
				write("hello");
			}
		};
		HttpRequest request = get(url);
		assertTrue(request.ok());
		BufferedReader reader = new BufferedReader(request.reader());
		assertEquals("hello", reader.readLine());
		reader.close();
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

	public void serviceTopPartitionsNoArg() {

		some.something(SystemKeyspace.persistLocalMetadata());

	}

	// FROM:
	// https://raw.githubusercontent.com/apache/qpid/0.18/qpid/java/perftests/src/test/java/org/apache/qpid/disttest/controller/ClientRegistryTest.java
	public void testRegisterClient() {
		assertEquals(0, _clientRegistry.getClients().size());

		_clientRegistry.registerClient(CLIENT1_REGISTERED_NAME);
		assertEquals(1, _clientRegistry.getClients().size());

	}

	public void testRejectsDuplicateClientNames() {
		_clientRegistry.registerClient(CLIENT1_REGISTERED_NAME);
		try {
			_clientRegistry.registerClient(CLIENT1_REGISTERED_NAME);
			fail("Should have thrown an exception");
		} catch (final DistributedTestException e) {
			// pass
		}
	}

	public void testAwaitOneClientWhenClientNotRegistered() {
		int numberOfClientsAbsent = _clientRegistry.awaitClients(1, AWAIT_DELAY);
		assertEquals(1, numberOfClientsAbsent);
	}

	public void testAwaitOneClientWhenClientAlreadyRegistered() {
		_clientRegistry.registerClient(CLIENT1_REGISTERED_NAME);

		int numberOfClientsAbsent = _clientRegistry.awaitClients(1, AWAIT_DELAY);
		assertEquals(0, numberOfClientsAbsent);
	}

	public void testAwaitTwoClientWhenClientRegistersWhilstWaiting() {
		_clientRegistry.registerClient(CLIENT1_REGISTERED_NAME);
		registerClientLater(CLIENT2_REGISTERED_NAME, 50);

		int numberOfClientsAbsent = _clientRegistry.awaitClients(2, AWAIT_DELAY);
		assertEquals(0, numberOfClientsAbsent);
	}

	private void registerClientLater(final String clientName, long delayInMillis) {
		doLater(new TimerTask() {
			@Override
			public void run() {
				_clientRegistry.registerClient(clientName);
			}
		}, delayInMillis);
	}

	private void doLater(TimerTask task, long delayInMillis) {
		Timer timer = new Timer();
		timer.schedule(task, delayInMillis);
	}

}
