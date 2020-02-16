package testfiles;

import java.io.BufferedReader;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;

import org.checkerframework.checker.units.qual.s;
import org.omg.CORBA.Request;

import com.google.common.collect.Lists;

public class TestNestedFunction {
	
	private StatusCode HTTP_OK = 0;

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

	class B {
		
	}
	
	@Test
	public void getReader(final pString a, Object b) throws Exception {
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
	public void nextFunc(String param) {
		call(new OBJ(HTTP_OK));
	}
	
	@Test
	public void recursive(String param) {
		nextFunc("HTTP_OK");
	}



//	private void registerClientLater(final String clientName, long delayInMillis) {
//		doLater(new TimerTask() {
//			@Override
//			public void run() {
//				_clientRegistry.registerClient(clientName);
//			}
//		}, delayInMillis);
//	}



}
