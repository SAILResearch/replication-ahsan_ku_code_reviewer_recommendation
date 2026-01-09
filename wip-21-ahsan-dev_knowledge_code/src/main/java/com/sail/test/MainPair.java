package com.sail.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

interface Pair<K, V> {
	public K getKey();

	public V getValue();
}
class OrderedPair<K, V> implements Pair<K, V> {

	private K key;
	private V value;

	public OrderedPair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
}

class Employee {
	public Date hireDate;
	public int number;
	public Date getHireDate() {
		return this.hireDate;
	}
	public int getNumber() {
		return this.number;
	}
}

class Simpson implements Comparable<Simpson> {
    String name;

    Simpson(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Simpson simpson) {
        return this.name.compareTo(simpson.name);
    }
}

public class MainPair {
	static final Comparator<Employee> SENIORITY_ORDER = new Comparator<Employee>() {
		public int compare(Employee e1, Employee e2) {
			int dateCmp = e2.getHireDate().compareTo(e1.getHireDate());
			if (dateCmp != 0)
				return dateCmp;
			return (e1.getNumber() < e2.getNumber() ? -1 : (e1.getNumber() == e2.getNumber() ? 0 : 1));
		}
	};

	public void test() {
		OrderedPair<String, Integer> p1 = new OrderedPair<>("Even", 8);
		List<String> list = new ArrayList<String>();
		list.add("abc");
	}
}