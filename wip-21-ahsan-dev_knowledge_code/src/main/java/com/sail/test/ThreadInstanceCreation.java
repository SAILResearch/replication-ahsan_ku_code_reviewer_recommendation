package com.sail.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ThreadInstanceCreation {
	private static int counter = 0;

	public static void callableTest(Callable<Integer> callableObj) {
		final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		callableObj = () -> {
			int result = integers.stream().mapToInt(i -> i.intValue()).sum();
			return result;
		};
	}

	public static void main(String[] args) {
		new Thread(() -> {
			for (int i = 0; i < 500; i++)
				ThreadInstanceCreation.counter++;
		}).start();
		while (ThreadInstanceCreation.counter < 100) {
			System.out.println("Not reached yet");
		}
		System.out.println("Reached!");

		ExecutorService service = null;
		try {
			service = Executors.newSingleThreadExecutor();
			System.out.println("begin");
			service.execute(() -> System.out.println("Printing zoo inventory"));
			service.execute(() -> {
				for (int i = 0; i < 3; i++)
					System.out.println("Printing record: " + i);
			});
			service.execute(() -> System.out.println("Printing zoo inventory"));
			System.out.println("end");
		} finally {
			if (service != null)
				service.shutdown();
		}
		final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Callable<Integer> callableObj = () -> {
			int result = integers.stream().mapToInt(i -> i.intValue()).sum();
			return result;
		};
		
		List<Integer> list = Collections.synchronizedList( new ArrayList<>(Arrays.asList(4,3,52)));
		synchronized(list) { for(int data: list)
		System.out.print(data+" "); }
		Stream<Integer> parallelStream2 = Arrays.asList(1,2,3,4,5,6).parallelStream();
		Stream<Integer> stream = Arrays.asList(1,2,3,4,5,6).stream();
		Stream<Integer> parallelStream = stream.parallel();
		
		Arrays.asList(1,2,3,4,5,6)
		.parallelStream()
		.forEach(s -> System.out.print(s+" "));
	}

	
}
