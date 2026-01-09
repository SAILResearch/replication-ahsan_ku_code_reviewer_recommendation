package com.sail.evaluatingevaluator.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProcessRunner {

	public void run(String commands[]) throws IOException {
		BufferedReader br = null;
		String line = null;
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commands);
		
		InputStream inStream = process.getInputStream();
		OutputStream outStream = process.getOutputStream();
		InputStream errorStream = process.getErrorStream();
		
		System.out.println("printing input streams");
		br = new BufferedReader(new InputStreamReader(inStream));
		while((line=br.readLine())!=null) {
			System.out.println("Line: "+line);
		}
		
		System.out.println("Printing output streams");
		br = new BufferedReader(new InputStreamReader(errorStream));
		while((line=br.readLine())!=null) {
			System.out.println("Line: "+line);
		}	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String commandList[] = {"cmd","/C","git version"};
		ProcessRunner runner = new ProcessRunner();
		try {
			runner.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() throws IOException, InterruptedException{
		ProcessBuilder pb = new ProcessBuilder("cmd","/C","git show 472aeb1861db7175e56e2594354d0ff0a25772d:FrameworkInfoCollector/FrameworkInfoLoader/src/frameworkinfoloader/Activator.java");
		pb.directory(new File("D:\\Muhammad\\version_evaluation\\FrameworkInfoCollector"));
		Process process = pb.start();
		int errCode = process.waitFor();
		System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
		System.out.println("Echo Outut:\n" + output(process.getInputStream()));  
		System.out.println("Echo Error Outut:\n" + output(process.getErrorStream()));  
	}
	private String output(InputStream inputStream) throws IOException {

		StringBuilder sb = new StringBuilder();
	    BufferedReader br = null;
		try {
		    br = new BufferedReader(new InputStreamReader(inputStream));
		    String line = null;
		    while ((line = br.readLine()) != null) {
		         sb.append(line + System.getProperty("line.separator"));
		    }

		} finally {
		    br.close();
		}
		return sb.toString();
	}
}
