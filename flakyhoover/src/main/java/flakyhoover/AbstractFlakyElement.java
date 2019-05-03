package flakyhoover;

import java.util.List;
import java.util.Map;

public abstract class AbstractFlakyElement {
	
	public abstract String getMethodName();

	public abstract boolean getHasFlaky();

	public abstract Map<String, String> getData();

	public abstract int getLine();
	
	public abstract Map<String, List<MetaData>> getMetaData();
}

