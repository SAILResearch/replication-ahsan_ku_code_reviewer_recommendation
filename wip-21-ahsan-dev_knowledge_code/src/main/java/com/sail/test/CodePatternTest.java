package com.sail.test;

public class CodePatternTest {

	boolean isOpen = true ;
	
	public void test() {
		int num1 = 3;
		int num2 = 5;
		int result = 0;
		if (num1 < num2)
		     result = 1;
		result = (num1>num2) ?
		            (num1+num2):(num1-num2);
	}
}
