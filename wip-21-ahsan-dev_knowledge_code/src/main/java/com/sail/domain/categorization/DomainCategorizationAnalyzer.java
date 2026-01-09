package com.sail.domain.categorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class DomainCategorizationAnalyzer {

	List<String> domainList = Arrays.asList("Visualization", "Networking", "Web Application Framework",
			"Data Management", "Docker manager", "IOT Framework", "Security", "Code Management", "Machine Learning",
			"Analytics", "Utility", "Game", "UI", "Distributed Framework", "Education",
			"Video player", "Performance", "Communication", "Testing", "Other");

	List<String> clusterList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14");

	public String domainCatResult = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/RScripts/Results/Cluster_with_project/Manual_Project_Domain/manual_system_domain_categorization_result.csv";
	public String domainOutputCluster = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/RScripts/Results/Cluster_with_project/Manual_Project_Domain/output_domain_cluster.csv";
	public String clusterOutputDomain = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/RScripts/Results/Cluster_with_project/Manual_Project_Domain/output_cluster_domain.csv";

	Map<String, Set<String>> systemInDomain = new HashMap<String, Set<String>>();
	Map<String, Set<String>> systemInCluster = new HashMap<String, Set<String>>();

	// Domain,cluster,system
	Map<String, Map<String, Set<String>>> domainWiseSystem = new HashMap<String, Map<String, Set<String>>>();
	// Cluster,domain,system
	Map<String, Map<String, Set<String>>> clusterWiseSystem = new HashMap<String, Map<String, Set<String>>>();

	Set<String> clusterSet = new HashSet<String>();
	Set<String> domainSet = new HashSet<String>();

	public void analyzeSystemDomain() throws Exception {

		Collections.sort(domainList);

		for (int i = 0; i < clusterList.size(); i++) {
			String clusterName = clusterList.get(i);
			systemInCluster.put(clusterName, new HashSet<String>());
			if (!clusterWiseSystem.containsKey(clusterName)) {
				clusterWiseSystem.put(clusterName, new HashMap<String, Set<String>>());
			}
			for (int j = 0; j < domainList.size(); j++) {
				String domainName = domainList.get(j);
				clusterWiseSystem.get(clusterName).put(domainName, new HashSet<String>());
			}
		}

		for (int j = 0; j < domainList.size(); j++) {
			String domainName = domainList.get(j);
			systemInDomain.put(domainName, new HashSet<String>());
			if (!domainWiseSystem.containsKey(domainName)) {
				domainWiseSystem.put(domainName, new HashMap<String, Set<String>>());
			}
			for (int i = 0; i < clusterList.size(); i++) {
				String clusterName = clusterList.get(i);
				domainWiseSystem.get(domainName).put(clusterName, new HashSet<String>());
			}
		}

		CsvReader reader = new CsvReader(domainCatResult);
		reader.readHeaders();

		while (reader.readRecord()) {
			String system = reader.get("System");
			String clusterLabel = reader.get("cl_kmeans").trim();
			String domainLabel = reader.get("Domain").trim();

			domainSet.add(domainLabel);
			clusterSet.add(clusterLabel);

			
			systemInDomain.get(domainLabel).add(system);
			systemInCluster.get(clusterLabel).add(system);

			
			domainWiseSystem.get(domainLabel).get(clusterLabel).add(system);
			clusterWiseSystem.get(clusterLabel).get(domainLabel).add(system);
		}

		// Write to csv file domain-cluster-result
		CsvWriter writer = new CsvWriter(domainOutputCluster);
		
		System.out.println("Total domain: " + domainList.size() + " " + domainList);
		System.out.println("Total cluster: " + clusterList.size() + "  " + clusterList);
		writer.write("Domain");
		writer.write("No_Systems");

		for (int j = 0; j < clusterList.size(); j++) {
			writer.write(clusterList.get(j));
		}
		writer.endRecord();

		for (int i = 0; i < domainList.size(); i++) {
			String domainName = domainList.get(i);
			double totalSystem = systemInDomain.get(domainName).size();
			writer.write(domainName);
			writer.write(String.format("%.0f", totalSystem));
			for (int j = 0; j < clusterList.size(); j++) {
				String clusterName = clusterList.get(j);
				System.out.println("D: " + domainName + " C: " + clusterName + " "
						+ domainWiseSystem.get(domainName).get(clusterName));
				double value = domainWiseSystem.get(domainName).get(clusterName).size();
				double ratio = value / totalSystem;
				writer.write(String.format("%.5f", ratio));
			}
			writer.endRecord();
		}
		writer.close();

		CsvWriter writer2 = new CsvWriter(clusterOutputDomain);
		writer2.write("Cluster");
		writer2.write("No_Systems");

		for (int j = 0; j < domainList.size(); j++) {
			writer2.write(domainList.get(j));
		}
		writer2.endRecord();
		for (int i = 0; i < clusterList.size(); i++) {
			String clusterName = clusterList.get(i);
			double totalSystem = systemInCluster.get(clusterName).size();
			writer2.write(clusterName);
			writer2.write(String.format("%.0f", totalSystem));
			for (int j = 0; j < domainList.size(); j++) {
				String domainName = domainList.get(j);
				double value = clusterWiseSystem.get(clusterName).get(domainName).size();
				double ratio = value / totalSystem;
				writer2.write(String.format("%.5f", ratio));
			}
			writer2.endRecord();
		}
		writer2.close();
	}

	public static void main(String[] args) throws Exception {
		DomainCategorizationAnalyzer ob = new DomainCategorizationAnalyzer();
		ob.analyzeSystemDomain();
		System.out.println("Program finishes successfully");
	}
}
