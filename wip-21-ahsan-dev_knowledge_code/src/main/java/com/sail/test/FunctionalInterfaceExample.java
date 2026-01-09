package com.sail.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public boolean hasOverHundredPoints() {
	    return this.points > 100;
	}
}
public class FunctionalInterfaceExample {
	
	public enum Level {
	    HIGH  (3),  //calls constructor with value 3
	    MEDIUM(2),  //calls constructor with value 2
	    LOW   (1)   //calls constructor with value 1
	    ; // semicolon needed when fields / methods follow


	    private final int levelCode;

	    Level(int levelCode) {
	        this.levelCode = levelCode;
	    }
	    
	    public int getLevelCode() {
	        return this.levelCode;
	    }
	    public void showInfo() {
	    	
	    }

		@Override
		public String toString() {
			System.out.println("This is the overridden method");
			return super.toString();
		}
	    
	}
	private String greeting = "Hi";
	
	protected class Inner {
		public int repeat = 3;
		public void go() {
		for (int i = 0; i < repeat; i++)
		System.out.println(greeting); }}
		
	public void calculate() {
		final int width = 20; class Inner2 {
		public void multiply() { System.out.println("INNER2");
		} }
		Inner2 inner2 = new Inner2();
		inner2.multiply(); }
	
	public void exampleFunctionalInterface() {
		Function<String, Integer> f1 = String::length; 
		Function<String, Integer> f2 = x -> x.length();
		
		List<Integer> list = Arrays.asList(3, 5, 7, 9, 11); 
		Optional<Integer> answer = list.stream().findFirst();
		if (answer.isPresent()) { 
            System.out.println(answer.get()); 
        } 
        else { 
            System.out.println("no value"); 
        } 
		
		boolean answerMatch = list.stream().anyMatch(n -> (n * (n + 1)) / 4 == 5);
		
		
		Customer john = new Customer("John P.", 15);
		Customer sarah = new Customer("Sarah M.", 200);
		Customer charles = new Customer("Charles B.", 150);
		Customer mary = new Customer("Mary T.", 1);
		List<Customer> customers = Arrays.asList(john, sarah, charles, mary);
		List<Customer> customersWithMoreThan100Points = customers
				  .stream()
				  .filter(c -> c.getPoints() > 100)
				  .collect(Collectors.toList());
		
		List<Customer> customersWithMoreThan100Points2 = customers
				  .stream()
				  .filter(Customer::hasOverHundredPoints)
				  .collect(Collectors.toList());
		
		
		String[] words = new String[10];   
        Optional<String> checkNull =  
                      Optional.ofNullable(words[5]);
        
        
        //Sorting
        Stream<String> s = Stream.of("brown bear-", "grizzly-"); 
        s.sorted(Comparator.reverseOrder())
        .forEach(System.out::print); 
	}
}
