package flakyhoover;

import java.util.ArrayList;
import java.util.List;

import util.TestSmell;

public class TestFile {
	private String app, testFilePath, productionFilePath;
	private List<AbstractFlaky> flakyInst;

	public String getApp() {
		return app;
	}

	public void printResult() {
		System.out.println("\n");
//		System.out.println("PrintRes: " +  getFlakyInst().size());

		for (AbstractFlaky flaky : getFlakyInst()) {
			if (flaky.getHasFlaky()) {
				System.out.println("File: " + getTestFileName());
				System.out.println("Has flaky :" + flaky.getHasFlaky());
				System.out.println("FlakyName: " + flaky.getFlakyName());
				System.out.println("FlakyClass: " + flaky.getClass());

			}

			System.out.println("flaky.getFlakyElements().size(): " + flaky.getFlakyElements().size());
			if (flaky.getFlakyElements().size() >= 1) {
				for (AbstractFlakyElement element : flaky.getFlakyElements()) {
					System.out.println("");

					System.out.println("Method: " + element.getMethodName());
					System.out.println("Line: " + element.getLine());
					System.out.println("HasFlaky: " + element.getHasFlaky());
					System.out.println("");
					if (!element.getMetaData().isEmpty()) {
						List<MetaData> list = element.getMetaData().get("VariableCond");
						System.out.println("LIST: " + list.size());

						for (MetaData d : list) {
							if(d.getIsTestRunWar()) {
								System.out.println("ISFLAKY: " + d.getIsFlaky());
								System.out.println("");
								System.out.println("getLine1: "+ d.getLine1());
								System.out.println("getLine2: " + d.getLine2());
								System.out.println("getLineInfo1: " + d.getLineInfo1());
								System.out.println("getLineInfo2: " + d.getLineInfo2());
							}
							else if (d.getIsFlaky()) {
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

	public List<AbstractFlaky> getFlakyInst() {
		return flakyInst;
	}

	public boolean getHasProductionFile() {
		return ((productionFilePath != null && !productionFilePath.isEmpty()));
	}

	public TestFile(String app, String testFilePath, String productionFilePath) {
		this.app = app;
		this.testFilePath = testFilePath;
		this.productionFilePath = productionFilePath;
		this.flakyInst = new ArrayList<>();
	}
	

	public void addFlakyInst(AbstractFlaky flaky) {
		flakyInst.add(flaky);
	}

	public String getTagName() {
		return testFilePath.split("\\\\")[4];
	}

	public String getTestFileName() {
		int lastIndex = testFilePath.lastIndexOf("\\");
		return testFilePath.substring(lastIndex + 1, testFilePath.length());
	}

	public String getTestFileNameWithoutExtension() {
		int lastIndex = getTestFileName().lastIndexOf(".");
		return getTestFileName().substring(0, lastIndex);
	}

	public String getProductionFileNameWithoutExtension() {
		int lastIndex = getProductionFileName().lastIndexOf(".");
		if (lastIndex == -1)
			return "";
		return getProductionFileName().substring(0, lastIndex);
	}

	public String getProductionFileName() {
		int lastIndex = productionFilePath.lastIndexOf("\\");
		if (lastIndex == -1)
			return "";
		return productionFilePath.substring(lastIndex + 1, productionFilePath.length());
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