package flakyhoover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMethod extends AbstractFlakyElement {

	private String methodName;
	private int lineNr;
	private boolean hasFlaky;
	private Map<String, String> data;

	private Map<String, List<MetaData>> metaData;

	public TestMethod(String methodName, int lineNr) {
		this.methodName = methodName;
		this.lineNr = lineNr;
		data = new HashMap<>();
		metaData = new HashMap<>();
	}

	public void setHasFlaky(boolean hasFlaky) {
		this.hasFlaky = hasFlaky;
	}

	public void addDataItem(String name, String value) {
		data.put(name, value);
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public int getLine() {
		return lineNr;
	}

	@Override
	public boolean getHasFlaky() {
		return hasFlaky;
	}

	@Override
	public Map<String, String> getData() {
		return data;
	}

	public Map<String, List<MetaData>> getMetaData() {
		return metaData;
	}

	public void addMetaDataItem(String name, List<MetaData> value) {
		metaData.put(name, value);
	}
}