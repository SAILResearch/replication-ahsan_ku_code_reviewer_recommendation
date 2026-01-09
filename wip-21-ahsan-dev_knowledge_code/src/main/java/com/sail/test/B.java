package com.sail.test;

public class B extends A{

	public void myMethodA(){
		System.out.println("Overrided Method");
	}
	public void myMethodB() {
		int ab [] = new int[23];
		ab[2] = 0;
		int multiArray[][] = new int [10][5];
		multiArray[0][0] = 20;
		System.out.println("B methods");
	}
}
