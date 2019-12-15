package testfiles;

public class TestFlakyExample extends AletheiaIntegrationTest<SampleDomainClass> {
	private Path testDir;
	private Path logDir;
	private List<NewFile> listy;

	public class ProcEnv {
		private Path logDir11;

		public ProcedureExecutor<ProcEnv> getProcedureExecutor() {
			return procExecutor;
		}
	}

	public static class TestProcedure extends Procedure<ProcEnv> {

		private Path logDir22;

		@Override
		protected boolean holdLock(ProcEnv env) {

			return true;
		}

		@Override
		protected Procedure<ProcEnv>[] execute(ProcEnv env)
				throws ProcedureYieldException, ProcedureSuspendedException, InterruptedException {
			STEP = 1;

			setTimeout(60 * 60 * 1000);
			setState(ProcedureProtos.ProcedureState.WAITING_TIMEOUT);
			skipPersistence();
			throw new ProcedureSuspendedException();

		}

	}

	public void normalFunction() {
		Somecall();
		NewObjefsdfsct listy;

	}

}

//	private static RequestHandler handler;
//	private ClientRegistry _clientRegistry = new ClientRegistry();

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
//
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
//public static final Route AVRO_ROUTE = new Route("test_endpoint_1", "avro");
//public static final Route JSON_ROUTE = new Route("test_endpoint_2", "json");
//
//private final Predicate<SampleDomainClass> filter = new Predicate<SampleDomainClass>() {
//	@Override
//	public boolean apply(final SampleDomainClass input) {
//		return input.isDiscarded();
//	}
//};
//
//public SampleDomainClassDatumTest() {
//    super(SampleDomainClass.class);
//  }
//
//@Override
//protected SampleDomainClass domainClassRandomDatum(final boolean shouldBeSent) {
//	return new SampleDomainClass(random.nextInt(), random.nextDouble(),
//			RandomStringUtils.randomAlphanumeric(random.nextInt(20)), new Instant(), shouldBeSent);
//}

//public void test_error() throws Exception {
//	Exception error = null;
//
//	try {
//		JSONSerializer.write(new Writer() {
//
//			@Override
//			public void write(char[] cbuf, int off, int len) throws IOException {
//				throw new IOException();
//			}
//
//			@Override
//			public void flush(int a, int b) throws IOException {
//				throw new IOException();
//			}
//
//			@Override
//			public void close() throws IOException {
//				throw new IOException();
//			}
//
//		}, (Object) "abc");
//	} catch (Exception ex) {
//		error = ex;
//	}
//	Assert.assertNotNull(error);
//}
//
//public void maybe() {
//
//	handler.instance();
//}
