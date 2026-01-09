package com.sail.test;

class Ball{
	public void method1() {
		System.out.println("sdf");
	}
}
class SuperClass{
	public int value = 12;
	public void print() {
		System.out.println("This is the super class");
	}
}
class SubClass extends SuperClass{
	public void print() {
		System.out.println("Override the method " + value);
	}
}
class SubClass2 extends Ball{
	public void method1() {
		System.out.println("Hello");
	}
}
public class SuperClassSubClassTest {
	public double myNumber = 12.3;
	public String name = "sf";
	public SuperClass sOb = new SuperClass();
	
	
	public static void methodRef (SuperClass superOb) {
		System.out.println("Super class catch sub class ob");
	}
	
	public static void main(String[] args) {
		SubClass subOb = new SubClass();
		SuperClass superOb = new SuperClass();
		superOb = subOb;
		SuperClass superOb2 = new SubClass();
		superOb.print();
		methodRef(subOb);
		SubClass2 ob2 = new SubClass2();
		ob2.method1();
	}
}
