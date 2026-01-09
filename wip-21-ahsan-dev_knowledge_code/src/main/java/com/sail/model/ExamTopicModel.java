package com.sail.model;

public class ExamTopicModel extends BasicModel{
	
	public String elementName;
	public String stringText;

	public ExamTopicModel(int startLine, String elementName, String stringText) {
		this.startLine = startLine;
		this.elementName = elementName;
		this.stringText = stringText;
	}
	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getStringText() {
		return stringText;
	}

	public void setStringText(String stringText) {
		this.stringText = stringText;
	}

	public void summaryPrint() {
		System.out.println("Element [" + this.elementName + "] " + "Start Line [" + this.startLine + "]");
	}
}
