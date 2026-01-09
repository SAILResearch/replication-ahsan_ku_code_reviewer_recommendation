package com.sail.java.exam.data.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.model.NumericDataAnalysisModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class DataAnalyzer {

	List<String> projectNameList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
	
	List<String> topicList = new ArrayList<String>();
	List<String> topicCategoryList = new ArrayList<String>();
	
	public void analyze() throws Exception{
		CsvWriter writerTopic = new CsvWriter(ConstantUtil.OUTPUT_DATA_ANALYSIS + "examTopicAnalysis.csv");
		CsvWriter writerTopicCategory = new CsvWriter(ConstantUtil.OUTPUT_DATA_ANALYSIS + "examTopicCategoryAnalysis.csv");
		
		writerTopic.write("File_Name");
		writerTopicCategory.write("File_Name");
		for(int i = 0 ; i < projectNameList.size() ; i ++ ) {
			String projectFullName = projectNameList.get(i).replace("/", "_");
			System.out.println("Project Name: " + projectFullName);
			topicList.clear();
			topicCategoryList.clear();
			NumericDataAnalysisModel filePerTopicCategory = new  NumericDataAnalysisModel();
			NumericDataAnalysisModel filePerTopicList = new  NumericDataAnalysisModel();
		    
			analyzeJavaTopic(ConstantUtil.OUTPUT_FILE_LOCATION + projectFullName +".csv",filePerTopicCategory,filePerTopicList,topicList, topicCategoryList);
			System.out.println("Finish analysis: " + projectFullName);
			
			if(i == 0) {
				for(String topic : topicCategoryList) {
					writerTopic.write(topic);
				}
				for(String topic : topicList) {
					writerTopic.write(topic);
				}
				writerTopic.write("Total_File");
				writerTopic.endRecord();
				writerTopicCategory.endRecord();
			}
			writeData(projectNameList.get(i), writerTopicCategory, filePerTopicCategory,topicCategoryList);
			writeData(projectNameList.get(i), writerTopic, filePerTopicList,topicList);
		}
		writerTopic.close();
		writerTopicCategory.close();
	}
	
	public void writeData(String fileName, CsvWriter writer, NumericDataAnalysisModel result, List<String> column ) throws Exception{
		
		writer.write(fileName);
		for(int i = 0 ; i < column.size() ; i ++ ) {
			double percentage = 100.0 * result.getFieldWithPosValueList().get(column.get(i))/Math.max(result.getTotalFile(),1);
			writer.write(Double.toString(percentage));
		}
		writer.write(Double.toString(result.getTotalFile()));
		writer.endRecord();
		
	}
	
	public void analyzeJavaTopic(String fileName, NumericDataAnalysisModel filePerTopicCategory,
		NumericDataAnalysisModel filePerTopicList, 
		List<String> topicList,
		List<String> topicCategoryList) throws Exception{
		
		CsvReader reader = new CsvReader(fileName);
		reader.readHeaders();
		int totalColumn = reader.getHeaderCount();
		List<String> columnList = new ArrayList<String>();
		
		for(int i = 2 ; i < totalColumn ; i ++) {
			String columnName = reader.getHeader(i);
			String columnsStrip[] = columnName.split("-");
			String colomnCat = columnsStrip[1];
			columnList.add(columnName);
			
			filePerTopicCategory.getFieldWithPosValueList().put(colomnCat, 0.0);
			filePerTopicList.getFieldWithPosValueList().put(columnName, 0.0);
			
			if(!topicList.contains(columnName)) {
				topicList.add(columnName);
			}
			if(!topicCategoryList.contains(colomnCat)) {
				topicCategoryList.add(colomnCat);
			}
		}
		int totalRecord = 0 ;
		while(reader.readRecord()) {
			Set<String> coloumnValuePositive = new HashSet<String>();
			++totalRecord;
			for(int i = 0 ; i < columnList.size() ; i ++) {
				String columnName = columnList.get(i);
				String stringDataValue = reader.get(columnName);
				
				if(stringDataValue.trim().length() <= 0 ) {
					System.out.println("PROBLEM: R " + totalRecord +" C "+ i + " ColName: "+ stringDataValue + " " + fileName);
				}
				double value = Double.parseDouble(stringDataValue);
				
				String columnsStrip[] = columnName.split("-");
				String colomnCat = columnsStrip[1];
				if(value > 0) {
					filePerTopicList.getFieldWithPosValueList().put(columnName, filePerTopicList.getFieldWithPosValueList().get(columnName) + 1);
					coloumnValuePositive.add(colomnCat);
				}
			}
			for(String colName : coloumnValuePositive) {
				filePerTopicCategory.getFieldWithPosValueList().put(colName, filePerTopicCategory.getFieldWithPosValueList().get(colName) + 1);
			}
			filePerTopicList.totalFile++;
			filePerTopicCategory.totalFile++;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		DataAnalyzer ob = new DataAnalyzer();
		ob.analyze();
		System.out.println("Program finishes successfully");
	}
}
