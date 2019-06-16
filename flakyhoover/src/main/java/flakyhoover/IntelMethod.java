package flakyhoover;

import java.util.HashSet;
import java.util.Set;

public class IntelMethod {
	public String name;
	public boolean isFlaky;
	public Set<String> calls;

	public IntelMethod(String name, boolean isFlaky) {
		this.name = name;
		this.isFlaky = isFlaky;
		this.calls = new HashSet<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFlaky() {
		return isFlaky;
	}

	public void setFlaky(boolean isFlaky) {
		this.isFlaky = isFlaky;
	}

	public void addMethod(String method) {
		calls.add(method);
	}
	
	public Set<String> getMethods(){
		return this.calls;
	}
}