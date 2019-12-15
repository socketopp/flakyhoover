package flakyhoover;

import java.util.HashSet;
import java.util.Set;

public class IntelMethod {
	public String name;
	public boolean isSmelly;
	public Set<String> calls;

	public IntelMethod(String name, boolean isSmelly) {
		this.name = name;
		this.isSmelly = isSmelly;
		this.calls = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSmelly() {
		return isSmelly;
	}

	public void setSmelly(boolean isSmelly) {
		this.isSmelly = isSmelly;
	}

	public void addMethod(String method) {
		calls.add(method);
	}

	public Set<String> getMethods() {
		return this.calls;
	}
}