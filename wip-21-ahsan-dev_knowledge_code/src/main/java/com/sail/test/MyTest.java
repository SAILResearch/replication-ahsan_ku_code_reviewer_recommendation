package com.sail.test;

import java.util.HashSet;
import java.util.Set;

public final class MyTest {

	Set<String> stringList = new HashSet<String>();
	private int accountId;
	private int amount;

	public int getAccountId() {

		return accountId;
	}

	public void setAccountId(int accountId) {
		accountId = accountId;
		int value = 34;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {

		this.amount = amount;
	}

	public MyTest() {
		this(12);
	}

	public void initialize(Set<String> myList, String value) {
		myList.add(value);
	}

	public void Test2(A ob) {

	}

	public MyTest(int a) {
		initialize(stringList, "abc");
		try {
			a = 20;
			A ob;
			ob = (A) new B();
			Test2(new B());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// this(a,"asdf");
	}

	public MyTest(int a, String b) {

	}
}
