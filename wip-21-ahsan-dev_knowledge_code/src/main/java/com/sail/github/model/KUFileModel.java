package com.sail.github.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KUFileModel {

	public Map<String, Double> knowledgeUnitPerFile = new HashMap<String, Double>();
	public Set<String> parentClassList;
	public Set<String> dependencyList;
	
	public Map<String, Double> getKnowledgeUnitPerFile() {
		return knowledgeUnitPerFile;
	}
	public void setKnowledgeUnitPerFile(Map<String, Double> knowledgeUnitPerFile) {
		this.knowledgeUnitPerFile = knowledgeUnitPerFile;
	}
	public Set<String> getParentClassList() {
		return parentClassList;
	}
	public void setParentClassList(Set<String> parentClassList) {
		this.parentClassList = parentClassList;
	}
	public Set<String> getDependencyList() {
		return dependencyList;
	}
	public void setDependencyList(Set<String> dependencyList) {
		this.dependencyList = dependencyList;
	}
}
