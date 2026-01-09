package com.sail.evaluatingevaluator.diff;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class GitDiffChangeDescriptor {

	private List<Integer> addedLines; //in the new file
	private List<Integer> deletedLines; //in the old file

	public GitDiffChangeDescriptor(String output) {
		this.addedLines = new ArrayList<Integer>();
		this.deletedLines = new ArrayList<Integer>();
		try {
			this.parse(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parse(String output) throws IOException {
		
		this.addedLines.clear();
		this.deletedLines.clear();
		
		//step-1:convert the output into list of lines
		List<String> lineList = IOUtils.readLines(new StringReader(output));
		
		//step-2: now parse individual lines
		for(int i=0;i<lineList.size();i++) {
			if(lineList.get(i).length()>0 && lineList.get(i).matches("\\s+")==false) {
				if(lineList.get(i).startsWith("---")) {
					String oldFile = lineList.get(i).split("\\s+")[1].substring(1);i++;
					if(lineList.get(i).startsWith("+++")) {
						String newFile = lineList.get(i).split("\\s+")[1].substring(1);i++;
						for(;i<lineList.size();i++) {
							if(lineList.get(i).length()>0 && lineList.get(i).matches("\\s+")==false) {
								String splits[] = lineList.get(i).split("\\s+");
								if(splits[0].equals("@@") && splits[3].equals("@@")) {
									int linesRead = this.readHunk(i, lineList, newFile, this.addedLines,this.deletedLines);
									i = i+ linesRead-1;
								}
							}
						}
					}
				}
			}
		}	
	}
	
	private int readHunk(int curLineNumber, List<String> lineList, String fileName,List<Integer> addedLines, List<Integer> deletedLines) {
		
		int linesRead = 0;
		int offset=0;
		//System.out.println("Cur Line: "+curLineNumber + lineList.get(curLineNumber));
		
		String splits[]=lineList.get(curLineNumber).split("\\s+");
		int hunkStartLine = Integer.parseInt(splits[2].split(",")[0].substring(1));
		curLineNumber++;
		
		while(curLineNumber<lineList.size() && lineList.get(curLineNumber).startsWith("diff --git")==false && lineList.get(curLineNumber).startsWith("@@")==false) {
			//System.out.println("Cur Line: "+curLineNumber + lineList.get(curLineNumber));
			
			if(lineList.get(curLineNumber).startsWith("+")) {
				addedLines.add(hunkStartLine+offset);
				//System.out.println("Added line: "+hunkStartLine+offset);
			}
			else if(lineList.get(curLineNumber).startsWith("-")) {
				deletedLines.add(hunkStartLine+offset);
				offset--;
			}
			
			offset++;
			linesRead++;
			curLineNumber++;
		}
		return linesRead;
	}
	
	public List<Integer> getAddedLines() {
		return addedLines;
	}

	public void setAddedLines(List<Integer> addedLines) {
		this.addedLines = addedLines;
	}

	public List<Integer> getDeletedLines() {
		return deletedLines;
	}

	public void setDeletedLines(List<Integer> deletedLines) {
		this.deletedLines = deletedLines;
	}

	public void print() {
		System.out.println("Added Lines: "+this.addedLines);
		System.out.println("Deleted Lines: "+this.deletedLines);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
