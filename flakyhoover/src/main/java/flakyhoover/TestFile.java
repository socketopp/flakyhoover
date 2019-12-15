package flakyhoover;

import java.util.ArrayList;
import java.util.List;

public class TestFile {
	private String app, testFilePath, productionFilePath, testMethod;
	private List<AbstractSmell> smellyInst;

	public TestFile(String app, String testFilePath, String productionFilePath) {
		this.app = app;
		this.testFilePath = testFilePath;
		this.productionFilePath = productionFilePath;
		this.smellyInst = new ArrayList<>();
	}

	public String getApp() {
		return app;
	}

	public void printResult() {
		System.out.println("\n");

		for (AbstractSmell smell : getSmellyInst()) {
			if (smell.getHasSmell()) {
				System.out.println("File: " + getTestFileName());
				System.out.println("Has smell :" + smell.getHasSmell());
				System.out.println("FlakyName: " + smell.getSmellName());
				System.out.println("FlakyClass: " + smell.getClass());

			}

			System.out.println("smell.getFlakyElements().size(): " + smell.getSmellyElements().size());
			if (smell.getSmellyElements().size() >= 1) {
				for (AbstractSmellElement element : smell.getSmellyElements()) {
					System.out.println("");

					System.out.println("Method: " + element.getMethodName());
					System.out.println("Line: " + element.getLine());
					System.out.println("HasFlaky: " + element.getHasSmell());
					System.out.println("");
					if (!element.getMetaData().isEmpty()) {
						List<MetaData> list = element.getMetaData().get("VariableCond");
						System.out.println("LIST: " + list.size());

						for (MetaData d : list) {
							if (d.getIsTestRunWar()) {
								System.out.println("ISFLAKY: " + d.getIsSmelly());
								System.out.println("");
								System.out.println("getLine1: " + d.getLine1());
								System.out.println("getLine2: " + d.getLine2());
								System.out.println("getLineInfo1: " + d.getLineInfo1());
								System.out.println("getLineInfo2: " + d.getLineInfo2());
							} else if (d.getIsSmelly()) {
//								System.out.println("");
//								System.out.println(d.getExpr());
//								System.out.println(d.getLine());
//								System.out.println(d.getClass());
//								System.out.println(d.getLineInfo());
//								List<String> extras = d.getExtra();
//								if(extras != null) {
//									for(String extra : d.getExtra()) {
//										System.out.println(extra);
//									}
//								}
							}
						}
					}

				}
			}
			System.out.println("\n");

		}
	}

	public String getProductionFilePath() {
		return productionFilePath;
	}

	public String getTestFilePathWithDot() {
		return testFilePath.replaceAll("\\\\", ".");
	}

	public String getTestFilePath() {
		return testFilePath;
	}

	public String getTestMethodName() {
		return this.testMethod;
	}

	public List<AbstractSmell> getSmellyInst() {
		return smellyInst;
	}

	public boolean getHasProductionFile() {
		return ((productionFilePath != null && !productionFilePath.isEmpty()));
	}

	public void addSmellyInst(AbstractSmell smell) {
		smellyInst.add(smell);
	}

	public String getTagName() {
		return testFilePath.split("\\\\")[4];
	}

	public String getTestFileName() {
		int lastIndex = testFilePath.lastIndexOf("\\");
		return testFilePath.substring(lastIndex + 1, testFilePath.length());
	}

	public static String getTestFileName(String testFilePath) {
		int lastIndex = testFilePath.lastIndexOf("\\");
		return getProductionFileNameWithoutExtension(testFilePath.substring(lastIndex + 1, testFilePath.length()));
	}

	public String getTestFileNameWithoutExtension() {
		int lastIndex = getTestFileName().lastIndexOf(".");
		return getTestFileName().substring(0, lastIndex);
	}

	public static String getProductionFileNameWithoutExtension(String path) {
		int lastIndex = path.lastIndexOf(".");
		if (lastIndex == -1)
			return "";
		return path.substring(0, lastIndex);
	}

	public String getProductionFileNameWithoutExtension() {
		int lastIndex = getProductionFileName().lastIndexOf(".");
		if (lastIndex == -1)
			return "";
		return getProductionFileName().substring(0, lastIndex);
	}

	public String getProductionFileNameWithoutExtensiondot() {
		int lastIndex = getProductionFileNameFromOrg().lastIndexOf(".");
		if (lastIndex == -1)
			return "";
//		return getProductionFileName().substring(0, lastIndex);
		return getProductionFileNameFromOrg().substring(0, lastIndex);
	}

	public String getProductionFileName() {
		int lastIndex = productionFilePath.lastIndexOf("\\");
		if (lastIndex == -1)
			return "";
		return productionFilePath.substring(lastIndex + 1, productionFilePath.length());
	}

	public String getProductionFileNameFromOrg() {
		int last = getTestFilePathWithDot().length();
		int from = getTestFilePathWithDot().indexOf("org");
		return getTestFilePathWithDot().substring(from, last);
	}

	public String getRelativeTestFilePath() {
		String[] splitString = testFilePath.split("\\\\");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			stringBuilder.append(splitString[i] + "\\");
		}
		return testFilePath.substring(stringBuilder.toString().length()).replace("\\", "/");
	}

//	 public String getRelativeProductionFilePath() {
//	 if (!StringUtils.isEmpty(productionFilePath)) {
//	 String[] splitString = productionFilePath.split("\\\\");
//	 StringBuilder stringBuilder = new StringBuilder();
//	 for (int i = 0; i < 5; i++) {
//	 stringBuilder.append(splitString[i] + "\\");
//	 }
//	 return
//	 productionFilePath.substring(stringBuilder.toString().length()).replace("\\",
//	 "/");
//	 } else {
//	 return "";
//
//	 }
//	 }

}