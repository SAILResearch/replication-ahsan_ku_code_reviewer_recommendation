package com.sail.evaluatingevaluator.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtility {
	public static String output(InputStream inputStream) throws IOException {

		StringBuilder sb = new StringBuilder();
	    BufferedReader br = null;
		try {
		    br = new BufferedReader(new InputStreamReader(inputStream));
		    String line = null;
		    while ((line = br.readLine()) != null) {
		         sb.append(line + System.getProperty("line.separator"));
		        System.out.println(line);
		    }
		   System.out.flush();

		} finally {
		    br.close();
		}
		return sb.toString();
	}
}
