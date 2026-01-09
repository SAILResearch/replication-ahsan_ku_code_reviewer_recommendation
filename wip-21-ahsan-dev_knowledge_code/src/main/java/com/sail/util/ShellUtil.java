package com.sail.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ShellUtil {

	public static final String OUTPUT_TYPE = "OUTPUT";
	public static final String ERROR_TYPE = "ERROR";
	// git log -1 --format=%ai MY_TAG_NAME
	// git tag

	public static void getAllCommitId(String path) {
		String command[] = { "git", "log", "--pretty=oneline" };
		String commitIdString = runCommand(path, command);
		System.out.println(commitIdString.split("\n").length);
	}

	public static String runCommand(String projectPath, String command[]) {
		//System.out.println("Command: " + Arrays.asList(command).toString());
		Path directory = Paths.get(projectPath);
		if (!Files.exists(directory)) {
			throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
		}
		try {
			ProcessBuilder pb = new ProcessBuilder().command(command).directory(new File(projectPath));
			Process p = pb.start();
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), ERROR_TYPE);
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), OUTPUT_TYPE);
			outputGobbler.start();
			errorGobbler.start();
			int exit = p.waitFor();
			errorGobbler.join();
			outputGobbler.join();
			if (exit != 0) {
				System.out.println("ERROR: " + errorGobbler.getOutputResult());
				throw new AssertionError(String.format("runCommand returned %d", exit));
			}
			Thread.sleep(1000);
			return outputGobbler.getOutputResult();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String runCommand(String command[]) {
		try {
			ProcessBuilder pb = new ProcessBuilder().command(command);
			Process p = pb.start();
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), ERROR_TYPE);
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), OUTPUT_TYPE);
			outputGobbler.start();
			errorGobbler.start();
			int exit = p.waitFor();
			errorGobbler.join();
			outputGobbler.join();
			if (exit != 0) {
				System.out.println("ERROR: " + errorGobbler.getOutputResult());
				throw new AssertionError(String.format("runCommand returned %d", exit));
			}
			Thread.sleep(1000);
			return outputGobbler.getOutputResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
