package com.sail.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;

@Remote
interface TimerSessionBeanRemote {
    public void createTimer(long milliseconds);
}

class TimerSessionBean {

    @Resource
    private SessionContext context;

}

@Stateless
public class TestJavaEnterpriseBean implements TimerSessionBeanRemote {

    @Resource
    private SessionContext context;

    public void createTimer(long duration) {
        context.getTimerService().createTimer(duration, "Hello World!");
    }

    @Timeout
    public void timeOutHandler(Timer timer) {
        System.out.println("timeoutHandler : " + timer.getInfo());
        timer.cancel();
    }

    @Remote
    public interface LibrarySessionBeanRemote {
        void addBook(String bookName);

        List getBooks();
    }

    @Stateless
    class LibrarySessionBean implements LibrarySessionBeanRemote {

        List<String> bookShelf;

        public LibrarySessionBean() {
            bookShelf = new ArrayList<String>();
        }

        public void addBook(String bookName) {
            bookShelf.add(bookName);
        }

        public List<String> getBooks() {
            return bookShelf;
        }
    }
}