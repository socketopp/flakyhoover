package flakyhoover;

public class VariableData {

	private int line;
	private String expr;
	private String variable;
	private String functionName;

	public VariableData(int line, String expr, String variable, String funcName) {
		this.setLine(line);
		this.setExpr(expr);
		this.setVar(variable);
		this.setFuncName(funcName);
	}

	public void setVar(String variable) {
		this.variable = variable;
	}

	public String getVar() {
		return variable;
	}

	public String getfunctionName() {
		return functionName;
	}

	public String getExpr() {
		return expr;
	}

	public void setFuncName(String functionName) {
		this.functionName = functionName;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}
