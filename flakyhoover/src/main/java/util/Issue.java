package util;

public class Issue {
	private String fileName = "File 1";
	private String packageName = "Package 1";
	private int lineStart = 1;
	private int lineEnd = 2;
	private int columnStart = 1;
	private int columnEnd = 2;
	private String fingerprint = "Fingerprint 2";
	private String message = "Message 1";
	private String description = "Description 1";
	private String additionalProperties ="Property 21, Property 22";
	private String reference = "reference1";
	private String origin ="ORIGIN1";
	private String moduleName = "modulename1";
	private String severity = "servery 1";
	private String type = "type 1";
	private String category = "category 1";
	private String lineRanges;

	public Issue() {
		
	}

	public String getLineRanges() {
		return lineRanges;
	}

}
