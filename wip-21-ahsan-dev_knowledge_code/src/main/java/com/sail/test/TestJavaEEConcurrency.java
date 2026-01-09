package com.sail.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ExecutorServlet")
 class ExecutorServlet extends HttpServlet {
 
    @Resource(name = "DefaultManagedExecutorService")
    ManagedExecutorService executor;
 
       protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
 
             PrintWriter writer = response.getWriter();           
 
             executor.execute(new SimpleTask());          
 
             writer.write("Task SimpleTask executed! check logs");      
 
       }
 
}

class CallableListenerTask implements Callable<Long>,ManagedTaskListener {
 
    private int id;

    public CallableListenerTask(int id) {

          this.id = id;

    }

    public Long call() {

          long summation = 0;

          for (int i = 1; i <= id; i++) {

                 summation += i;

          }

          return new Long(summation);

    }

    public void taskSubmitted(Future<?> f, ManagedExecutorService es,
                 Object obj) {

          System.out.println("Task Submitted! "+f);

    }

    public void taskDone(Future<?> f, ManagedExecutorService es, Object obj,
                 Throwable exc) {

          System.out.println("Task DONE! "+f);

    }

    public void taskStarting(Future<?> f, ManagedExecutorService es,
                 Object obj) {

          System.out.println("Task Starting! "+f);

    }

    public void taskAborted(Future<?> f, ManagedExecutorService es,
                 Object obj, Throwable exc) {

          System.out.println("Task Aborted! "+f);

    }

}

class SimpleTask implements Runnable {
    @Override
    public void run() {
          System.out.println("Thread started.");
    }
}

@WebServlet("/ScheduledExecutor")
class ScheduledExecutor extends HttpServlet {
 
       @Resource(name ="DefaultManagedScheduledExecutorService")
       ManagedScheduledExecutorService scheduledExecutor;
 
       protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
             PrintWriter writer = response.getWriter();           
 
             ScheduledFuture<?> futureResult = scheduledExecutor.schedule(new SimpleTask(),       10,TimeUnit.SECONDS);
 
             writer.write("Waiting 10 seconds before firing the task");
 
       }
 
}


public class TestJavaEEConcurrency {
    
}
