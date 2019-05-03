package util;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("issues")
public class Issues {

	@XStreamAlias("issue")
	@XStreamAsAttribute
	private Issue issue;

	@XStreamImplicit
	private ArrayList<Issue> issues;

	public Issues() {
		this.issue = new Issue();
		issues = new ArrayList<Issue>();
	}

	public Issues getIssues() {
		return this;
	}

	public ArrayList<Issue> getIssuesList() {
		return this.issues;
	}

	public void setIssues(ArrayList<Issue> issues) {
		this.issues = issues;
	}
}
