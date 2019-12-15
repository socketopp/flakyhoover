package flakyhoover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMethod extends AbstractSmellElement {

	private String methodName;
	private int lineNr;
	private boolean hasSmell;
	private Map<String, String> data;

	private Map<String, List<MetaData>> metaData;

	public TestMethod(String methodName, int lineNr) {
		this.methodName = methodName;
		this.lineNr = lineNr;
		data = new HashMap<>();
		metaData = new HashMap<>();
	}

	public void setHasSmell(boolean hasSmell) {
		this.hasSmell = hasSmell;
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
	public boolean getHasSmell() {
		return hasSmell;
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