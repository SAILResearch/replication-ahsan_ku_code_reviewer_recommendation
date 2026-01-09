package com.sail.test;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class JGraphTesting {

    public void testingJagraph(){
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("a");
        g.addVertex("b");
        g.addVertex("c");
        g.addVertex("d");
        g.addVertex("e");
        g.addVertex("f");

        g.addEdge("a", "b");
        g.addEdge("b", "c");
        g.addEdge("c", "b");
        g.addEdge("d", "e");

        ConnectivityInspector ci = new ConnectivityInspector(g);

        List<Set<String>> connectedVertex = ci.connectedSets();

        g.setEdgeWeight("a", "b", 1.5);
        //DefaultWeightedEdge e = (DefaultWeightedEdge) g.getEdge("a", "b");
        double w = g.getEdgeWeight(g.getEdge("a", "b"));
        System.out.println("weight: " + w);

        for(Set <String> conVerList : connectedVertex){
            System.out.println("Group");
            for (String v : conVerList){
                System.out.print(v + " ");
            }
            System.out.println();
        }


        DateTime date1 = new DateTime("2023-07-01");
        DateTime date2 = new DateTime("2023-07-30");

        System.out.println(Days.daysBetween(date1, date2).getDays());

        System.out.println(Graphs.neighborListOf(g, "a"));

    }
    public static void main(String[] args) {
        JGraphTesting ob = new JGraphTesting();
        ob.testingJagraph();
        System.out.println("Program finishes successfully");
    }
}
