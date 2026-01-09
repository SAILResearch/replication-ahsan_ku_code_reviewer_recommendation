package com.sail.java.exam.data.analysis;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.NumericDataAnalysisModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class ReleaseLevelCommitDataAnalyzer {
	
	// System data_type_knowledge operator_decision_Knowledge array_knowledge
	// loop_knowledge method_encapsulation_knowledge inheritance_knowledge
	// exception_knowledge stringBuilder_calender_api_knowledge
	// advanced_class_design_knowledge generic_collection_knowledge
	// functional_interface_knowledge stream_api_knowledge date_time_api_knowledge
	// io_knowledge
	// nio_knowledge concurrency_knowledge data_base_knowledge
	// localization_knowledge string_processing_knowledge

	//String gitRepoTestPath = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/GitRepositoryTest/";
	Map<String, Map<String, DescriptiveStatistics>> topicStats = new HashMap<String, Map<String, DescriptiveStatistics>>();
	Map<String, Map<String, DescriptiveStatistics>> topicCategoryStats = new HashMap<String, Map<String, DescriptiveStatistics>>();
	Map<String, ArrayList<NumericDataAnalysisModel>> fileChangeInformation = new HashMap<String, ArrayList<NumericDataAnalysisModel>>();

	List<String> topicList = Arrays.asList(

			"Section-2-Item1-a-ArrayType", "Section-2-Item1-a-ParameterizedType", "Section-2-Item1-a-PrimitiveType",
			"Section-2-Item1-a-WildcardType", "Section-3-Item1-a-ArithmeticOperator",

			"Section-3-Item1-a-RelationalOperator", "Section-3-Item1-a-BitwiseOperator",
			"Section-3-Item1-a-LogicalOperator", "Section-3-Item1-a-TernaryOperator",
			"Section-3-Item1-a-AssignmentOperator", "Section-3-Item1-a-PrefixOperator",
			"Section-3-Item1-a-PostfixOperator", "Section-3-Item3-a-if_else_condition",
			"Section-3-Item3-b-ternary_condition", "Section-3-Item4-a-switch_condition",

			"Section-4-Item1-a-one_dim_array", "Section-4-Item2-a-two_dim_array", "Section-5-Item1-a-while_loop",

			"Section-5-Item2-a-for_loop", "Section-5-Item3-a-do_while_loop", "Section-5-Item5-a-continue_statement",
			"Section-5-Item5-b-break_statement", "Section-6-Item1-a-method_with_arguments",

			"Section-6-Item2-a-static_class_field", "Section-6-Item2-b-static_block",
			"Section-6-Item3-a-overloaded_method_constructor", "Section-6-Item3-b-constructor_chaining",
			"Section-6-Item3-d-variable_arguments", "Section-6-Item4-a-access_modifiers",
			"Section-6-Item5-a-set_get_method", "Section-6-Item5-b-immutable_class",
			"Section-6-Item6-a-object_type_parameter_update", "Section-7-Item2-a-basic_polymorphism",

			"Section-7-Item2-b-polymorphic_parameters", "Section-7-Item3-a-casting_super_sub_class",
			"Section-7-Item4-a-super_method_variable", "Section-7-Item5-a-abstract_class",
			"Section-7-Item5-b-interface", "Section-8-Item2-a-try_catch_block",

			"Section-8-Item2-b-super_wider_exception", "Section-8-Item4-a-method_with_throws",
			"Section-8-Item5-a-multiple_catch", "Section-8-Item5-b-different_exception",

			"Section-9-Item1-a-manipulate_string_builder", "Section-9-Item2-a-calendar_data_usage",

			"Section-10-Item1-a-enum_decl", "Section-10-Item2-a-nested_class", "Section-10-Item2-b-local_class",
			"Section-10-item2-c-anonymous_inner_class", "Section-10-item3-a-overriden_method_with_annotation",

			"Section-11-item1-a-generic_class", "Section-11-item2-a-instance_array_set_map_deque",
			"Section-11-item3-a-comparator_anonymous_class", "Section-11-item3-b-comparable_interface",

			"Section-12-item1-a-built_in_interface", "Section-12-item2-a-primitive_functional_interface",
			"Section-12-item3-a-binary_functional_interface", "Section-12-item4-a-unary_functional_interface",

			"Section-13-item1-a-stream_peak", "Section-13-item1-b-stream_map", "Section-13-item2-a-stream_search",
			"Section-13-item3-a-java_optional_class", "Section-13-item4-a-stream_collection",
			"Section-13-item5-a-stream_flat_map", "Section-13-item6-a-collector_class",
			"Section-13-item7-a-lambda_expression", "Section-13-item8-a-stream_foreach",

			// "Section14-item1-a" , "Section14-item2-a" , "Section14-item3-a" ,
			// "Section14-item4-a" ,
			"Section-15-item1-a-local_date_time_api", "Section-15-item2-a-time_format",
			"Section-15-item3-a-time_instant", "Section-15-item3-b-time_period", "Section-15-item3-c-time_duration",

			"Section-16-item1-a-byte_file_stream", "Section-16-item1-b-char_file_stream",
			"Section-16-item1-c-binary_file_stream", "Section-16-item1-d-other_stream_api",
			"Section-16-item2-a-console_read_old_approach", "Section-16-item2-a-console_read_new_approach",
			"Section-16-item3-a-usage_of_serialization",

			"Section-17-item1-a-path_nio", "Section-17-item2-a-file_nio", "Section-17-item3-a-attribute_modify_files",

			"Section-18-item1-a-runnable", "Section-18-item1-b-callable", "Section-18-item1-c-thread_cration",
			"Section-18-item1-a-executors", "Section-18-item3-a-concurrent_collection",
			"Section-18-item4-a-fork_join_task", "Section-18-item4-b-fork_join_pool",
			"Section-18-item4-c-recursive_task", "Section-18-item5-a-parallel_stream",
			"Section-18-item6-a-schedule_executor", "Section-18-item7-a-synchronized_method_api",

			"Section-19-item1-a-sql_driver_manager", "Section-19-item1-b-sql_connection",
			"Section-19-item2-a-sql_statement", "Section-19-item3-a-sql_result_set",

			"Section-20-item1-a-usage_locale", "Section-20-item2-a-usage_properties",
			"Section-20-item3-a-usage_resource_buldne",

			"Section-21-item1-a-string_api_usage", "Section-21-item2-a-string_pattern",
			"Section-21-item2-a-string_matcher", "Section-21-item2-a-string_formatter",
			"Section-21-item2-a-decimal_formatter");

	List<String> topicCategoryList = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "15",
			"16", "17", "18", "19", "20", "21");

	public void analyze(String projectIdentificationName) throws Exception {
		String projectFullName = projectIdentificationName.replace("/", "_");
		System.out.println("Project Name: " + projectFullName);

		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		String projectRepoName = projectFullName;
		String projectRepoPath = ConstantUtil.STUDIED_PROJECT_DIR + projectRepoName + "/";

		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);

		topicStats.put(projectFullName, new HashMap<String, DescriptiveStatistics>());
		topicCategoryStats.put(projectFullName, new HashMap<String, DescriptiveStatistics>());
		fileChangeInformation.put(projectFullName, new ArrayList<NumericDataAnalysisModel>());
		for (String topic : topicList) {
			topicStats.get(projectFullName).put(topic, new DescriptiveStatistics());
		}

		for (String topicCat : topicCategoryList) {
			topicCategoryStats.get(projectFullName).put(topicCat, new DescriptiveStatistics());
		}

		for (int i = gitCommitList.size() - 1; i >= 0; i--) {

			System.out.println(projectFullName + " remaining analyze release: " + i);

			GitCommitModel gitCommit = gitCommitList.get(i);

			NumericDataAnalysisModel filePerTopicCategory = new NumericDataAnalysisModel();
			NumericDataAnalysisModel filePerTopicList = new NumericDataAnalysisModel();

			NumericDataAnalysisModel filePerTopicCategoryChanges = new NumericDataAnalysisModel();
			NumericDataAnalysisModel filePerTopicListChanges = new NumericDataAnalysisModel();

			String fileName = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" + projectRepoName
					+ "-" + gitCommit.getCommitId() + "-" + gitCommit.getCommitCommitterDate() + ".csv";

			try {
				analyzeJavaTopic(projectRepoPath, projectRepoName, fileName, gitCommit, filePerTopicCategory,
						filePerTopicList, filePerTopicCategoryChanges, filePerTopicListChanges);

				fileChangeInformation.get(projectFullName).add(filePerTopicCategory);

				for (String topic : topicList) {
					double denominator = Math.max(filePerTopicList.getFieldWithPosValueList().get(topic), 1);
					double numerator = filePerTopicListChanges.getFieldWithPosValueList().get(topic);
					double ratio = numerator / denominator;
					// System.out.println(topic + " " + ratio + " " +
					// filePerTopicListChanges.getFieldWithPosValueList().get(topic)
					// + " "+ filePerTopicList.getFieldWithPosValueList().get(topic) );
					topicStats.get(projectFullName).get(topic).addValue(ratio);
				}

				for (String topicCategory : topicCategoryList) {
					double denominator = Math.max(filePerTopicCategory.getFieldWithPosValueList().get(topicCategory),
							1);
					double numerator = filePerTopicCategoryChanges.getFieldWithPosValueList().get(topicCategory);
					double ratio = numerator / denominator;
					// System.out.println(topicCategory + " " + ratio + " " +
					// filePerTopicCategoryChanges.getFieldWithPosValueList().get(topicCategory)
					// + " "+ filePerTopicCategory.getFieldWithPosValueList().get(topicCategory) );
					topicCategoryStats.get(projectFullName).get(topicCategory).addValue(ratio);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			// break;
		}
		System.out.println("Finish analysis: " + projectFullName);
	}

	public void analyzeJavaTopic(String projectRepoPath, String projectRepoName, String fileName,
			GitCommitModel gitCommit, NumericDataAnalysisModel filePerTopicCategory,
			NumericDataAnalysisModel filePerTopicList, NumericDataAnalysisModel filePerTopicCategoryChanges,
			NumericDataAnalysisModel filePerTopicListChanges) throws Exception {

		CsvReader reader = new CsvReader(fileName);
		reader.readHeaders();

		int totalColumn = reader.getHeaderCount();
		List<String> columnList = new ArrayList<String>();

		filePerTopicList.setChangedFile(gitCommit.getChangedJavaFileList().size());
		filePerTopicCategory.setChangedFile(gitCommit.getChangedJavaFileList().size());

		for (String topic : topicList) {
			filePerTopicList.getFieldWithPosValueList().put(topic, 0.0);
			filePerTopicListChanges.getFieldWithPosValueList().put(topic, 0.0);
		}

		for (String topicCat : topicCategoryList) {
			filePerTopicCategory.getFieldWithPosValueList().put(topicCat, 0.0);
			filePerTopicCategoryChanges.getFieldWithPosValueList().put(topicCat, 0.0);
		}

		int totalRecord = 0;
		

		while (reader.readRecord()) {
			Set<String> coloumnValuePositive = new HashSet<String>();
			Set<String> coloumnValuePositiveFileChanges = new HashSet<String>();
			++totalRecord;

			String javaFilePath = reader.get("File_Path");
			javaFilePath = javaFilePath.substring((ConstantUtil.STUDIED_PROJECT_DIR + projectRepoName + "/").length());
			// System.out.println("Java file path: " + javaFilePath);

			for (int i = 0; i < topicList.size(); i++) {
				String columnName = topicList.get(i);
				String stringDataValue = reader.get(columnName);

				if (stringDataValue.trim().length() <= 0) {
					System.out.println("PROBLEM: R " + totalRecord + " C " + i + " " + columnName + " ColName: "
							+ stringDataValue + " " + fileName);
				}
				double value = Double.parseDouble(stringDataValue);

				String columnsStrip[] = columnName.split("-");
				String colomnCat = columnsStrip[1];
				if (value > 0) {
					filePerTopicList.getFieldWithPosValueList().put(columnName,
							filePerTopicList.getFieldWithPosValueList().get(columnName) + 1);
					coloumnValuePositive.add(colomnCat);
					if (gitCommit.getChangedJavaFileList().contains(javaFilePath)) {
						// System.out.println(columnName + " " + javaFilePath);
						filePerTopicListChanges.getFieldWithPosValueList().put(columnName,
								filePerTopicListChanges.getFieldWithPosValueList().get(columnName) + 1);
					}
				}
			}
			for (String colName : coloumnValuePositive) {
				filePerTopicCategory.getFieldWithPosValueList().put(colName,
						filePerTopicCategory.getFieldWithPosValueList().get(colName) + 1);
				if (gitCommit.getChangedJavaFileList().contains(javaFilePath)) {
					filePerTopicCategoryChanges.getFieldWithPosValueList().put(colName,
							filePerTopicCategoryChanges.getFieldWithPosValueList().get(colName) + 1);
				}
			}
			filePerTopicList.totalFile++;
			filePerTopicCategory.totalFile++;
		}
	}

	public void runReleaseLevelAnalyzer() throws Exception {
		ArrayList<String> studiedProjectList = new ArrayList<String>();
		studiedProjectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		for (int i = 0; i < studiedProjectList.size(); i++) {
			System.out.println(studiedProjectList.get(i));
			String projectFullName = studiedProjectList.get(i).replace("/", "_");
			analyze(projectFullName);
		}

		for (String projectName : fileChangeInformation.keySet()) {
			int totalReleases = fileChangeInformation.get(projectName).size();
			DescriptiveStatistics changedFiles = new DescriptiveStatistics();
			DescriptiveStatistics totalFiles = new DescriptiveStatistics();
			for (NumericDataAnalysisModel nn : fileChangeInformation.get(projectName)) {
				changedFiles.addValue(nn.getChangedFile());
				totalFiles.addValue(nn.getTotalFile());
			}

			System.out.println(
					projectName + " TR: " + totalReleases + " CF: " + changedFiles.getPercentile(50) + " TF: " + totalFiles.getPercentile(50));
		}
		 writerStatResult();
	}

	public void writerStatResult() throws Exception {

		CsvWriter writerTopicList = new CsvWriter(
				ConstantUtil.OUTPUT_DATA_ANALYSIS + "release_change_file_exam_topic_result.csv");
		CsvWriter writerTopicCategoryList = new CsvWriter(
				ConstantUtil.OUTPUT_DATA_ANALYSIS + "release_change_file_exam_topic_category_result.csv");

		for (String topic : topicList) {
			writerTopicList.write(topic);
		}
		for (String topicCategory : topicCategoryList) {
			writerTopicCategoryList.write(topicCategory);
		}

		writerTopicList.endRecord();
		writerTopicCategoryList.endRecord();

		for (String projectName : topicStats.keySet()) {
			writerTopicList.write(projectName);
			for (String topic : topicList) {
				writerTopicList.write(String.format("%.5f", topicStats.get(projectName).get(topic).getPercentile(50)));
			}
			writerTopicList.endRecord();
		}

		for (String projectName : topicStats.keySet()) {
			writerTopicCategoryList.write(projectName);
			for (String topicCategory : topicCategoryList) {
				writerTopicCategoryList.write(String.format("%.5f",
						topicCategoryStats.get(projectName).get(topicCategory).getPercentile(50)));
			}
			writerTopicCategoryList.endRecord();
		}

		writerTopicList.close();
		writerTopicCategoryList.close();
	}

	public void writeReleaseBasicInformation() {

	}

	public void writerStatResultPerRecord() throws Exception {

		CsvWriter writerTopicList = new CsvWriter(
				ConstantUtil.OUTPUT_FILE_LOCATION + "release_change_file_exam_topic_result_per_record.csv");
		CsvWriter writerTopicCategoryList = new CsvWriter(
				ConstantUtil.OUTPUT_FILE_LOCATION + "release_change_file_exam_topic_category_result_per_record.csv");

		for (String topic : topicList) {
			writerTopicList.write(topic);
		}
		for (String topicCategory : topicCategoryList) {
			writerTopicCategoryList.write(topicCategory);
		}

		writerTopicList.endRecord();
		writerTopicCategoryList.endRecord();

		for (String projectName : topicStats.keySet()) {
			writerTopicList.write(projectName);
			for (String topic : topicList) {
				for (double value : topicStats.get(projectName).get(topic).getValues()) {
					writerTopicList.write(String.format("%.5f", value));
				}

			}
			writerTopicList.endRecord();
		}

		for (String projectName : topicStats.keySet()) {
			writerTopicCategoryList.write(projectName);
			for (String topicCategory : topicCategoryList) {
				writerTopicCategoryList.write(String.format("%.5f",
						topicCategoryStats.get(projectName).get(topicCategory).getPercentile(50)));
			}
			writerTopicCategoryList.endRecord();
		}

		writerTopicList.close();
		writerTopicCategoryList.close();
	}

	public static void main(String[] args) throws Exception {
		ReleaseLevelCommitDataAnalyzer ob = new ReleaseLevelCommitDataAnalyzer();
		// ob.analyze("h2oai_h2o-dev");
		ob.runReleaseLevelAnalyzer();
		System.out.println("Program finishes successfully");
	}
}
