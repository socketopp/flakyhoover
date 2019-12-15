package util;

import java.util.ArrayList;
import java.util.List;

public class Collector {

	private String methodName;
	private String parentClass;
	private String baseClass;

	public String getBaseClass() {
		return baseClass;
	}

	public void setBaseClass(String baseClass) {
		this.baseClass = baseClass;
	}

	private boolean found = false;
	private List<String> seen = new ArrayList<String>();

	public List<String> getSeen() {
		return seen;
	}

	public void setSeen(List<String> seen) {
		this.seen = seen;
	}

	public void addSeen(String object) {
		seen.add(object);
	}

	public Collector(String methodName, String baseClass, String parentClass) {
		super();
		this.methodName = methodName;
		this.parentClass = parentClass;
		this.baseClass = baseClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public boolean isFound() {
		return found;
	}

	public String getParentClass() {
		return parentClass;
	}

	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}

}
