package util;

public class TestSmell {

	private String project;
	private String testClass;
	private String testMethod;
	private String smellType;
	private String flakinessType;

	public TestSmell(String project, String testClass, String testMethod, String smellType, String flakinessType) {
		super();
		this.project = project;
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.smellType = smellType;
		this.flakinessType = flakinessType;
	}

	public TestSmell() {

	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public String getSmellType() {
		return smellType;
	}

	public void setSmellType(String smellType) {
		this.smellType = smellType;
	}

	public String getFlakinessType() {
		return flakinessType;
	}

	public void setFlakinessType(String flakinessType) {
		this.flakinessType = flakinessType;
	}

}
