package util;

import java.util.ArrayList;
import java.util.List;

import flakyhoover.AbstractSmell;

public class FlakyFile {

	public FlakyFile(int id, String url, String sha, int testCount, String module, String testName, String category,
			String version, String className, String testMethod) {
		super();
		this.className = className;
		this.testMethod = testMethod;
		this.id = id;
		this.url = url;
		this.sha = sha;
		this.testCount = testCount;
		this.module = module;
		this.testName = testName;
		this.category = category;
		this.version = version;
		this.setSmellyInstances(new ArrayList<AbstractSmell>());
	}

	public FlakyFile(int id, String url, String sha, String testName, String className, String testMethod,
			String category, String module, String path) {
		super();
		this.className = className;
		this.testMethod = testMethod;
		this.id = id;
		this.url = url;
		this.sha = sha;
		this.module = module;
		this.testName = testName;
		this.category = category;
		this.path = path;
		this.setSmellyInstances(new ArrayList<AbstractSmell>());
	}

	private List<AbstractSmell> smellyInstances;
	private int id;
	private String className;
	private String testMethod;
	private String url;
	private String sha;
	private int testCount;
	private String module;
	private String testName;
	private String category;
	private String version;
	private String path;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSha() {
		return sha;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public int getTestCount() {
		return testCount;
	}

	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public List<AbstractSmell> getSmellyInstances() {
		return smellyInstances;
	}

	public void setSmellyInstances(List<AbstractSmell> smellyInstances) {
		this.smellyInstances = smellyInstances;
	}

	public void addSmellyInstances(AbstractSmell flaky) {
		smellyInstances.add(flaky);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
