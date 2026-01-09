package com.sail.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowRequestHeaders extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String title = "Servlet Example: Showing Request Headers";
      Enumeration headerNames = request.getHeaderNames();
      while(headerNames.hasMoreElements()) {
        String headerName = (String)headerNames.nextElement();
        out.println("<TR><TD>" + headerName);
        out.println("    <TD>" + request.getHeader(headerName));
      }
      out.println("</TABLE>\n</BODY></HTML>");
    }
  
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
        throws ServletException, IOException {
      doGet(request, response);
    }
}