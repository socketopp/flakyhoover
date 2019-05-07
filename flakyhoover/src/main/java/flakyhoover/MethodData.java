package flakyhoover;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;

public class MethodData {

	private int line;
	private String expr;
	private String functionCallName;
	private String functionName;
	private String baseVar;
	NodeList<Expression> args;
	private List<String> extra;

	public MethodData(int line, String expr, String functionCaller, NodeList<Expression> args, String baseVar,
			String functionName) {
		this.setLine(line);
		this.setExpr(expr);
		this.setFuncCallName(functionCaller);
		this.setArgs(args);
		this.setBaseVar(baseVar);
		this.setFuncName(functionName);
	}

	public void setFuncName(String functionName) {
		this.setFunctionName(functionName);
	}

	public void setBaseVar(String baseVar) {
		this.baseVar = baseVar;
	}

	public String getBaseVar() {
		return this.baseVar;
	}

	public void setArgs(NodeList<Expression> args) {
		this.args = args;
	}

	public void setExtra(List<String> extra) {
		this.extra = extra;
	}

	public List<String> getExtra() {
		return this.extra;
	}

	public String getExpr() {
		return expr;
	}

	public void setFuncCallName(String functionName) {
		this.functionCallName = functionName;
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

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionCallName() {
		return this.functionCallName;
	}

	public void setFunctionCallName(String functionCallName) {
		this.functionCallName = functionCallName;
	}

}
