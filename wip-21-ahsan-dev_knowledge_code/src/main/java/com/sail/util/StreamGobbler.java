package com.sail.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	private final InputStream is;
	private final String type;
	String outputResult = "";
	
	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}
	
    public String getOutputResult() {
		return outputResult;
	}
    
	@Override
    public void run() {

    	try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
			String line;
			int lineNumber = 0 ;
			while ((line = br.readLine()) != null) {
				//System.out.println(type + "> " + line + " Line: " + (++lineNumber));
				if(this.type.compareTo("OUTPUT") == 0) {
					outputResult += line + "\n";
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
    }

}
