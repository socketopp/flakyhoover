package testfiles;

import java.io.IOException;

import nonapi.io.github.classgraph.json.JSONSerializer;

public class TestRunWar6 {

	public void test_error() throws Exception {
		Exception error = null;
		try {
			JSONSerializer.write(new Writer() {

				@Override
				public void write(char[] cbuf, int off, int len) throws IOException {
					throw new IOException();
				}

				@Override
				public void flush() throws IOException {
					throw new IOException();
				}

				@Override
				public void close() throws IOException {
					throw new IOException();
				}

			}, (Object) "abc");
		} catch (Exception ex) {
			error = ex;
		}
		Assert.assertNotNull(error);
	}
}
