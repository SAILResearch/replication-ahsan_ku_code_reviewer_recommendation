package com.sail.test;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

class Emp implements Serializable {
	private static final long serialversionUID = 129348938L;
	transient int a;
	static int b;
	String name;
	int age;

	// Default constructor
	public Emp(String name, int age, int a, int b) {
		this.name = name;
		this.age = age;
		this.a = a;
		this.b = b;
	}

}

public class FileTester {

	public static void copy(File source, File destination) throws IOException {
		try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
		}
	}

	public void fileNIO() {

		Path path1 = Paths.get("fish.txt");

		try {
			Files.copy(Paths.get("/panda"), Paths.get("/panda-save"));
			Files.copy(Paths.get("/panda/bamboo.txt"), Paths.get("/panda-save/bamboo.txt"));

			Path path = Paths.get("/turtles/sea.txt");
			BasicFileAttributes data = Files.readAttributes(path, BasicFileAttributes.class);

			System.out.println("Is path a directory? " + data.isDirectory());
			System.out.println("Is path a regular file? " + data.isRegularFile());
			System.out.println("Is path a symbolic link? " + data.isSymbolicLink());
			System.out.println("Path not a file, directory, nor symbolic link? " + data.isOther());

			BasicFileAttributeView view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
			BasicFileAttributes data2 = view.readAttributes();
			FileTime lastModifiedTime = FileTime.fromMillis(data2.lastModifiedTime().toMillis() + 10_000);
			view.setTimes(lastModifiedTime, null, null);

		} catch (IOException e) {

		}

	}

	public static void main(String[] args) throws IOException {

		// Old way to interact with the user
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput = reader.readLine();
		System.out.println("You entered the following: " + userInput);

		// new way to ineteract with the user and recommended after java 6
		Console console = System.console();
		if (console != null) {
			String userInput2 = console.readLine();
			console.writer().println("You entered the following: " + userInput2);
		}
	}
}
