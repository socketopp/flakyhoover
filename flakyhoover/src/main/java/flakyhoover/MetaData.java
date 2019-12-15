package flakyhoover;

import java.util.List;

public class MetaData {

	private int line;
	private String expr;
	private boolean isSmelly;
	private String lineInfo;
	private List<String> extra;
	private int line1;
	private int line2;
	private String lineInfo1;
	private String lineInfo2;
	private boolean isTestRunWar;

	public MetaData(int line, String expr, String lineInfo, boolean isSmelly) {
		this.setLine(line);
		this.setExpr(expr);
		this.setSmelly(isSmelly);
		this.setLineInfo(lineInfo);
	}

	public MetaData(int line1, int line2, String lineInfo1, String lineInfo2, boolean isSmelly) {
		this.setLine1(line1);
		this.setLine2(line2);
		this.setLineInfo1(lineInfo1);
		this.setLineInfo2(lineInfo2);
		this.isSmelly = isSmelly;
	}

	public void setIsTestRunWar(boolean value) {
		this.isTestRunWar = value;
	}

	public boolean getIsTestRunWar() {
		return isTestRunWar;
	}

	public void setExtra(List<String> extra) {
		this.extra = extra;
	}

	public List<String> getExtra() {
		return this.extra;
	}

	public boolean getIsSmelly() {
		return isSmelly;
	}

	public void setSmelly(boolean isSmelly) {
		this.isSmelly = isSmelly;
	}

	public String getExpr() {
		return expr;
	}

	public void setLineInfo(String lineInfo) {
		this.lineInfo = lineInfo;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getLineInfo() {
		return lineInfo;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getLineInfo1() {
		return lineInfo1;
	}

	public void setLineInfo1(String lineInfo1) {
		this.lineInfo1 = lineInfo1;
	}

	public String getLineInfo2() {
		return lineInfo2;
	}

	public void setLineInfo2(String lineInfo2) {
		this.lineInfo2 = lineInfo2;
	}

	public int getLine1() {
		return line1;
	}

	public void setLine1(int line1) {
		this.line1 = line1;
	}

	public int getLine2() {
		return line2;
	}

	public void setLine2(int line2) {
		this.line2 = line2;
	}
}
