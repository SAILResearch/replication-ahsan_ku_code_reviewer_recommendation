package com.sail.java.exam.dev.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.sail.github.model.KUFileModel;
import com.sail.util.ConstantUtil;
import com.sail.util.TextUtil;

public class KnowledgeUnitExtractorDevStudy {
	
	public Map<String,KUFileModel>  extractKnowledgeUnits(String filePath, String projName) {
		Map<String,KUFileModel> knowledgeUnitData = new HashMap<String, KUFileModel>();
		Map<String,KUFileModel> subKnowledgeUnitData = new HashMap<String, KUFileModel>();
		String fileExtraName = "scratch/ahsan/Java_Exam_Work/GitRepositoryTest/" + projName +"_4/";
		
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			
			while(reader.readRecord()) {
				
				String javaFileName = reader.get("File_Path");
				//System.out.println("Prev " + javaFileName);
				javaFileName = javaFileName.substring(fileExtraName.length()+ 1, javaFileName.lastIndexOf(".java"));
				if(javaFileName.contains( "/")) {
					javaFileName = javaFileName.replace("/",".").trim();
				}
				//System.out.println("After " + javaFileName);
				
				String parentClassListString = reader.get("Super_Class");
				String dependencyListString = reader.get("Invoked_Methods");
				KUFileModel kuModel = new KUFileModel();
				
				for (int i = 0; i < ConstantUtil.topicList.size(); i++) {
					String subTopic = ConstantUtil.topicList.get(i);
					String stringDataValue = reader.get(subTopic);
					double value = Double.parseDouble(stringDataValue);
					
					if (value > 0) {
						//kuModel.getKnowledgeUnitPerFile().put(columnName, value);
						String topic = subTopic.split("-")[1].trim();
						if(topic.compareTo("9") == 0) continue;
						//System.out.println("TOPIC: " + topic);
						topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topic));
						if (!kuModel.getKnowledgeUnitPerFile().containsKey(topic)) {
							kuModel.getKnowledgeUnitPerFile().put(topic, 0.0);
						}
						kuModel.getKnowledgeUnitPerFile().put( topic , 
								kuModel.getKnowledgeUnitPerFile().get(topic) + 
								value);
					}
				}
				if(kuModel.getKnowledgeUnitPerFile().size() > 0){
					kuModel.setParentClassList(TextUtil.convertStringToSet(parentClassListString,"-"));
					kuModel.setDependencyList(TextUtil.convertStringToSet(dependencyListString,"-"));
					knowledgeUnitData.put(javaFileName, kuModel);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return knowledgeUnitData;
	}

	public Map<String,KUFileModel>  extractSubKnowledgeUnits(String filePath, String projName) {
		Map<String,KUFileModel> knowledgeUnitData = new HashMap<String, KUFileModel>();
		Map<String,KUFileModel> subKnowledgeUnitData = new HashMap<String, KUFileModel>();
		String fileExtraName = "scratch/ahsan/Java_Exam_Work/GitRepositoryTest/" + projName +"_4/";
		
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			
			while(reader.readRecord()) {
				Set<String> coloumnValuePositive = new HashSet<String>();
				String javaFileName = reader.get("File_Path");
				
				//System.out.println("Prev " + javaFileName);
				javaFileName = javaFileName.substring(fileExtraName.length()+ 1, javaFileName.lastIndexOf(".java"));
				if(javaFileName.contains( "/")) {
					javaFileName = javaFileName.replace("/",".").trim();
				}
				//System.out.println("After " + javaFileName);
				
				String parentClassListString = reader.get("Super_Class");
				String dependencyListString = reader.get("Invoked_Methods");
				KUFileModel kuModel = new KUFileModel();
				KUFileModel kuModelSubTopic = new KUFileModel();
			
				for (int i = 0; i < ConstantUtil.topicList.size(); i++) {
					String subTopic = ConstantUtil.topicList.get(i);
					String stringDataValue = reader.get(subTopic);
					double value = Double.parseDouble(stringDataValue);
					if (value > 0) {
						//kuModel.getKnowledgeUnitPerFile().put(columnName, value);
						String topic = subTopic.split("-")[1].trim();
						topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topic));
						if(topic == "9") continue;
						if (!kuModel.getKnowledgeUnitPerFile().containsKey(topic)) {
							kuModel.getKnowledgeUnitPerFile().put(topic, 0.0);
						}
						kuModel.getKnowledgeUnitPerFile().put( topic , 
								kuModel.getKnowledgeUnitPerFile().get(topic) + 
								value);
						
						if (!kuModelSubTopic.getKnowledgeUnitPerFile().containsKey(subTopic)){
							kuModelSubTopic.getKnowledgeUnitPerFile().put(subTopic, 0.0);
						}

						kuModelSubTopic.getKnowledgeUnitPerFile().put(subTopic,
						kuModelSubTopic.getKnowledgeUnitPerFile().get(subTopic) + value);

					}
				}
				if(kuModel.getKnowledgeUnitPerFile().size() > 0){
					kuModel.setParentClassList(TextUtil.convertStringToSet(parentClassListString,"-"));
					kuModel.setDependencyList(TextUtil.convertStringToSet(dependencyListString,"-"));
					knowledgeUnitData.put(javaFileName, kuModel);
				}
				if (kuModelSubTopic.getKnowledgeUnitPerFile().size() > 0){
					kuModelSubTopic.setParentClassList(TextUtil.convertStringToSet(parentClassListString,"-"));
					kuModelSubTopic.setDependencyList(TextUtil.convertStringToSet(dependencyListString,"-"));
					subKnowledgeUnitData.put(javaFileName, kuModelSubTopic);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return subKnowledgeUnitData;
	}
	
	public static void main(String[] args) {
		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
		String path = "/scratch/ahsan/Java_Exam_Work/Result/Result_August_14_2021/Commit_History_Result/elastic_elasticsearch/elastic_elasticsearch-73ba30b18a8760ba7f38ff1adac0f2cb3249e0be.csv";
		Map<String,KUFileModel> knowledgeUnitData = ob.extractKnowledgeUnits(path, "jruby_jruby");
		
		int total = 0 ;
		for (String f : knowledgeUnitData.keySet()) {
			total = total + 1;
			KUFileModel kuData = knowledgeUnitData.get(f);
			for(String topic : kuData.knowledgeUnitPerFile.keySet()) {
				System.out.println(f + " " + kuData.knowledgeUnitPerFile.get(topic));
			}
			//System.out.println(TextUtil.convertSetToString(kuData.getDependencyList(), ","));
			if (total > 3) {
				break;
			}
		}
		System.out.println("Program finishes successfully");
	}
	
}
