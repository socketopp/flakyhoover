package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import flakyhoover.IntelMethod;

public class ASTHelper {

	private static List<String> exceptions = new ArrayList<String>(
			Arrays.asList("Boolean", "Byte", "Short", "Character", "toString", "Integer", "Long", "Float", "Double",
					"LOG", "log", "", "Collections", "Math", "assert", "assertEquals", "assertTrue", "assertFalse",
					"assertNotNull", "assertNull", "assertSame", "assertNotSame", "assertArrayEquals", "fail"));

	public static void addArguments(MethodCallExpr n, Set<String> methodCallArray) {
		for (Expression expr : n.getArguments()) {
			String type = expr.getClass().getSimpleName();
			String value = expr.toString();
			System.out.println("value: " + value);
			if (type.equals("MethodCallExpr")) {
				String[] array = value.split("\\.");
				if (array.length > 1) {
					methodCallArray.add(getObjectBase(value));
				}
			} else if (type.equals("NameExpr")) {
				methodCallArray.add(getObjectBase(value));
			}
		}
	}

	public static String getObjectBase(String expression) {
		return expression.split("\\.")[0];
	}

	public static boolean analyzeTestRunWar(Set<String> classVariables, Map<String, Set<String>> methodCalls,
			String currentMethod) {
		boolean isFlaky = false;
		for (String variable : classVariables) {
			for (Map.Entry<String, Set<String>> method1 : methodCalls.entrySet()) {
				for (Map.Entry<String, Set<String>> method2 : methodCalls.entrySet()) {
					if (currentMethod.equals(method1.getKey()) && !method1.getKey().equals(method2.getKey())) {
						if (method1.getValue().contains(variable) && method2.getValue().contains(variable)) {
							isFlaky = true;
						}

					}
				}
			}
		}
		return isFlaky;
	}

	public static String getSimpleNameRec(ArrayList<Node> nodes) {
		String returnString = "";
		if (nodes.size() > 0) {
			Node node = nodes.get(0);
			if (node.getClass().getSimpleName().toString().equals("VariableDeclarator")) {
				String type = node.getChildNodes().get(0).getClass().getSimpleName();
				String value = node.getChildNodes().get(0).toString();
				if (type.equals("ClassOrInterfaceType") && !value.equals("String")) {
					returnString = node.getChildNodes().get(1).toString();
				}

			} else {
				nodes.remove(0);
				return getSimpleNameRec(nodes);
			}
		}
		return returnString;
	}

	public static String checkIfClassMember(MethodCallExpr n) {

		Matcher m = Pattern.compile("(?<=\\[).+?(?=\\])").matcher(n.getScope().toString());
		String base = "empty";

		if (m.find()) {

			if (!exceptions.contains(m.group())) {

				base = m.group();
				if (base.contains(".")) {
					String[] arrOfbase = base.split("\\.");
					base = arrOfbase[0];
				}
			}
		}
		return base;
	}

	public static String getParameter(Parameter n) {
		String type = "";
		String value = "";
		String classtype = "";
		if (n.getChildNodes().size() >= 3) {
			classtype = n.getChildNodes().get(1).toString();
			type = n.getChildNodes().get(1).getClass().getSimpleName();
			value = n.getChildNodes().get(2).toString();
		} else {
			classtype = n.getChildNodes().get(0).toString();
			type = n.getChildNodes().get(0).getClass().getSimpleName();
			value = n.getChildNodes().get(1).toString();
		}

		if (type.equals("ClassOrInterfaceType") && !classtype.equals("String")) {
			return value;
		} else {
			return "";
		}
	}

//	ASTHelper.removeIntersectingVars(classVisitor.varDeclExpr, classVisitor.methodCalls);
	public static void removeIntersectingVars(Map<String, Set<String>> list1, Map<String, Set<String>> list2) {
		for (Map.Entry<String, Set<String>> entry1 : list1.entrySet()) {
			String method1 = entry1.getKey();
			for (Map.Entry<String, Set<String>> entry2 : list2.entrySet()) {
				String method2 = entry2.getKey();
				if (method1.equals(method2)) {
					for (Iterator<String> iterator1 = entry1.getValue().iterator(); iterator1.hasNext();) {
						String val1 = iterator1.next();
						for (Iterator<String> iterator2 = entry2.getValue().iterator(); iterator2.hasNext();) {
							String val2 = iterator2.next();
							if (val1.equals(val2)) {
								iterator2.remove();
							}
						}
					}
				}
			}
		}
	}

	public static void setMethodStatusFlaky(MethodDeclaration currentMethod, Set<IntelMethod> allMethodsData,
			boolean value) {
		for (IntelMethod m : allMethodsData) {
			if (m.getName().equals(currentMethod.getNameAsString())) {
				m.setFlaky(value);
				break;
			}
		}
	}

	public static boolean checkIfFlaky(String currentMethod, Set<IntelMethod> allMethodsData) {
		boolean flaky = false;
		for (IntelMethod m : allMethodsData) {
			if (m.getName().equals(currentMethod)) {
				if (m.isFlaky()) {
					flaky = m.isFlaky();
					break;
				}

			}
		}
		return flaky;
	}

	public static IntelMethod getMethod(String currentMethod, Set<IntelMethod> allMethodsData) {
		IntelMethod new_intel = null;
		for (IntelMethod intel : allMethodsData) {
			if (intel.getName().equals(currentMethod)) {
				new_intel = intel;
				break;
			}
		}
		return new_intel;
	}

	// TO-DO
	// Don't use get.child.nodes.get(0) (no good to index, may cause errors).
	// Use if(n.getScope().get() instanceof MethodCallExpr) instead

	public static void addToClassVariabels(FieldDeclaration n, Set<String> classVariabels) {

		for (VariableDeclarator variableDeclarator : n.getVariables()) {

			String name = variableDeclarator.getNameAsString();
			String type = variableDeclarator.getType().getClass().getSimpleName();
			String class_type = variableDeclarator.getTypeAsString();

			if (type.equals("ClassOrInterfaceType") && !class_type.equals("String")) {
				classVariabels.add(name);
			}
		}
	}

	public static void addToVariabelsDeclarations(VariableDeclarationExpr n, Set<String> variabelsDeclarations) {
		for (VariableDeclarator variableDeclarator : n.getVariables()) {

			String name = variableDeclarator.getNameAsString();
			String type = variableDeclarator.getType().getClass().getSimpleName();
			String class_type = variableDeclarator.getTypeAsString();

			if (type.equals("ClassOrInterfaceType") && !class_type.equals("String")) {
				variabelsDeclarations.add(name);
			}
		}
	}

	public static void addParams(MethodDeclaration n, Set<String> methodVariables) {
		if (n.getParameters().size() > 0) {
			for (Parameter param : n.getParameters()) {
				if (param.getTypeAsString().equals("File") || param.getTypeAsString().equals("Path")) {
					methodVariables.add(param.getNameAsString());
				}
			}
		}
	}

	public static void addIndirectParams(MethodDeclaration n, Set<String> indirectClasses, ArrayList<String> jClasses,
			String className) {
		if (n.getParameters().size() > 0) {
			for (Parameter param : n.getParameters()) {
				String name = param.getNameAsString();
				String type = param.getTypeAsString();
				if (!jClasses.contains(type) && !name.equals(className)) {
					indirectClasses.add(name);
				}
			}
		}
	}

	public static void addParameterToVariabelsDeclarations(MethodDeclaration n, Set<String> variabelsDeclarations) {

		if (n.getParameters().size() > 0) {
			for (Parameter param : n.getParameters()) {

				String parameter = getParameter(param);
				if (!parameter.equals("")) {
					variabelsDeclarations.add(parameter);
				}

				String name = param.getNameAsString();
				String type = param.getType().getClass().getSimpleName();
				String class_type = param.getTypeAsString();
				if (type.equals("ClassOrInterfaceType") && !class_type.equals("String")) {
					variabelsDeclarations.add(name);
				}
			}
		}

	}

}

//Old solution to get all object declaration except primitives and strings 
//String variable = ASTHelper.getSimpleNameRec(new ArrayList<Node>(n.getChildNodes()));
//if (!variable.equals("")) {
//	varDeclExprArray.add(variable);
//}

// Old solution to get all class objects except primitives and strings 
//String simpleName = ASTHelper.getSimpleNameRec(new ArrayList<Node>(n.getChildNodes()));
//if (!simpleName.equals("")) {
//	classVariables.add(simpleName);
//}

//public String tranverseVariableDeclarator(ArrayList<Node> nodes) {
//String returnString = "";
//
//if (nodes.size() > 0) {
//	Node node = nodes.get(0);
//
//	if (node.getClass().getSimpleName().toString().equals("VariableDeclarator")) {
//		// (node.getChildNodes().get(1).toString()
//		varDeclExprList.add(new VariableData(node.getBegin().get().line, node.toString(),
//				node.getChildNodes().get(1).toString(), currentMethod));
//	} else {
//		nodes.remove(0);
//		tranverseVariableDeclarator(nodes, varDeclExprList, currentMethod);
//	}
//}
//}
