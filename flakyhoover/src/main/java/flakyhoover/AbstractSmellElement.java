package flakyhoover;

import java.util.List;
import java.util.Map;

public abstract class AbstractSmellElement {

	public abstract String getMethodName();

	public abstract boolean getHasSmell();

	public abstract Map<String, String> getData();

	public abstract int getLine();

	public abstract Map<String, List<MetaData>> getMetaData();
}
