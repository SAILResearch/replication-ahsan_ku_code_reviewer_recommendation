package com.sail.test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class LambdaExpressionTester {
	class Customer {
	    private String name;
	    private int points;
	    public Customer(String name, int points) {
	    	this.name = name;
	    	this.points = points;
	    }
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getPoints() {
			return points;
		}
		public void setPoints(int points) {
			this.points = points;
		}
	}
	public void test() {
		Customer john = new Customer("John P.", 15);
		Customer sarah = new Customer("Sarah M.", 200);
		Customer charles = new Customer("Charles B.", 150);
		Customer mary = new Customer("Mary T.", 1);
		List<Customer> customers = Arrays.asList(john, sarah, charles, mary);
		List<Customer> customersWithMoreThan100Points = customers
				  .stream()
				  .filter(c -> c.getPoints() > 100)
				  .collect(Collectors.toList());
		List<Customer> charlesWithMoreThan100Points = customers
				  .stream()
				  .filter(c -> c.getPoints() > 100 && c.getName().startsWith("Charles"))
				  .collect(Collectors.toList());
	}
	public static void main(String[] args) {
		LambdaExpressionTester ob = new LambdaExpressionTester();
		ob.test();
	}
}



