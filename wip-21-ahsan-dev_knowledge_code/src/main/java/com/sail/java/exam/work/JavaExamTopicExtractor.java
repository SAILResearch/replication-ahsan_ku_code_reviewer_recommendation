package com.sail.java.exam.work;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.java.exam.topic.associate.ArrayKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.BuiltInJavaAPIKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.DataTypeKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.HandlindExceptionKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.InheritanceKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.LoopKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.MethodEncapsulationKnowledgeExtractorOCA;
import com.sail.java.exam.topic.associate.OperatorDecisionKnowledgeExtractorOCA;
import com.sail.java.exam.topic.javaee.EnterpriseJavaBeanKnowledge;
import com.sail.java.exam.topic.javaee.JavaCDIBeanKnowledge;
import com.sail.java.exam.topic.javaee.JavaEEBatchKnowledge;
import com.sail.java.exam.topic.javaee.JavaEEConcurrentKnowledge;
import com.sail.java.exam.topic.javaee.JavaJFSKnowledge;
import com.sail.java.exam.topic.javaee.JavaPersistanceKnowledge;
import com.sail.java.exam.topic.javaee.JavaRestFulWithJAXRXKnowledge;
import com.sail.java.exam.topic.javaee.JavaWebSocketKnowledge;
import com.sail.java.exam.topic.javaee.JavaWebserviceSOAPKnowledge;
import com.sail.java.exam.topic.javaee.MessageServiceKnowledge;
import com.sail.java.exam.topic.javaee.WebApplicationWithServletKnowledge;
import com.sail.java.exam.topic.professional.AdvancedClassDesignKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.ConcurrencyKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.FunctionalInterfaceKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.GenericKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.JDBCKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.JavaDateTimeAPIKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.JavaIOFundamentalKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.JavaNIOKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.JavaStringProcessingKnowledgeExtractorOCP;
import com.sail.java.exam.topic.professional.LocalizationKnowledgeExctractorOCP;
import com.sail.java.exam.topic.professional.StreamAPIKnowledgeExtractorOCP;
import com.sail.model.JavaClassModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import SourceCodeLineCounter.SourceCodeLineCounter;


public class JavaExamTopicExtractor {

	private HashMap<String, CompilationUnit> compilationUnitList;
	public static ArrayList<String> java8PackageList = new ArrayList<String>();
	public static ArrayList<String> studiedProjectList = new ArrayList<String>();
	public static Map<String, HashSet<String>> filesUseJavaPackageList = new HashMap<String, HashSet<String>>();
	CsvWriter writerUsageJavaPackage = new CsvWriter(ConstantUtil.USAGE_JAVA_PACKAGE_FILE);
	JavaKnowledgdUnitWritingResultUtil javaKnowledgeUnitWritingResultUtilOb = new JavaKnowledgdUnitWritingResultUtil();
	public JavaExamTopicExtractor() {

	}
	

	public void settingUpJDTEnvironment(String projectPath) {

		System.out.println("Setting up the JDT Environment ... ");

		compilationUnitList = new HashMap<String, CompilationUnit>();
		long startTime = System.currentTimeMillis();

		ASTParser parser = ASTParser.newParser(AST.JLS9);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);

		final Hashtable<String, String> options = JavaCore.getOptions();
		options.put("org.eclipse.jdt.core.compiler.source", "1.8");
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		String jarPath = "/home/local/SAIL/ahsan/visual_studio_workspace/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/others/jars/javaee-api-8.0.jar";

		// sources and classpath
		String[] sources = { projectPath };
		String[] classpath = new String[] { jarPath/* JARs */ };
		String[] encodings = new String[sources.length];
		Arrays.fill(encodings, StandardCharsets.UTF_8.name());
		parser.setEnvironment(classpath, sources, encodings, true);

		FileASTRequestor r = new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit cu) {
				compilationUnitList.put(sourceFilePath, cu);
			}
		};

		List<String> inProjectJavaFilePathList = FileUtil.getAllFilesWithExtension(projectPath, ".java");
		String[] sourceFilesPaths = new String[inProjectJavaFilePathList.size()];
		for (int i = 0; i < inProjectJavaFilePathList.size(); i++) {
			sourceFilesPaths[i] = inProjectJavaFilePathList.get(i);
		}
		parser.createASTs(sourceFilesPaths, null, new String[0], r, null);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println("Total Java files " + inProjectJavaFilePathList.size());
		System.out.println("Total time to initialize JDT Environment " + totalTime + " ms");
	}

	public void printClassInformation(Map<String, JavaClassModel> fullClassList) {
		System.out.println("[Class Information]");
		System.out.println("Total classes: " + fullClassList.size());
		for (String className : fullClassList.keySet()) {
			System.out.println("Class Name: " + className + " Total Methods: "
					+ fullClassList.get(className).getMethodList().size() + " Fields: "
					+ fullClassList.get(className).getClassFieldList().size());
		}
	}

	public void getAllClassQualifiedName(Map<String, Map<String, JavaClassModel>> fullClassList,
			Set<String> allDefinedClassQualifiedName) {
		for (String filePath : fullClassList.keySet()) {
			for (String className : fullClassList.get(filePath).keySet()) {
				allDefinedClassQualifiedName.add(fullClassList.get(filePath).get(className).getClassName());
			}
		}
	}

	

	public void runBasicModel(Map<String, Map<String, JavaClassModel>> fullClassList,
			Set<String> allDefinedClassQualifiedName) {

		int counter = 0;
		int problemFileInitialClassInfo = 0;
		for (String javaFilePath : compilationUnitList.keySet()) {
			allDefinedClassQualifiedName.add(javaFilePath); // Added this line.
			fullClassList.put(javaFilePath, new HashMap<String, JavaClassModel>());
			++counter;
			//System.out.println("[" + counter + "] Extracting java topic for File: " + javaFilePath);
			CompilationUnit cu = compilationUnitList.get(javaFilePath);
			try {
				BasicClassInfoExtractor ob = new BasicClassInfoExtractor(cu, fullClassList.get(javaFilePath));
				ob.extractClassInfo();

			} catch (Exception e) {
				++problemFileInitialClassInfo;
				e.printStackTrace();
			}

		}
		System.out.println("Finish Basic Class info extractor: [" + counter + "]");
		getAllClassQualifiedName(fullClassList, allDefinedClassQualifiedName);
	}

	public void startExtractingJavaTopicII(String commitId, String projectPath, String projectFullName,
			Map<String, Map<String, JavaClassModel>> fullClassList, Set<String> allDefinedClassQualifiedName,
			CsvWriter writer) throws Exception {

		for (String filePath : fullClassList.keySet()) {
			/*if (!filePath.contains("UsageReport.java")){
				continue;
			}
			System.out.println("Analyzed File: " + filePath);*/

			ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList = new ArrayList<TopicExtractorBaseModel>();

			int lineOfCode = SourceCodeLineCounter.getNumberOfLines(filePath);
			CompilationUnit cu = compilationUnitList.get(filePath);
			
			// OCA Topics
			analyzedTopicExtractorList.add(new DataTypeKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new OperatorDecisionKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new ArrayKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new LoopKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new MethodEncapsulationKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new InheritanceKnowledgeExtractorOCA(filePath, cu, allDefinedClassQualifiedName));
			analyzedTopicExtractorList.add(new HandlindExceptionKnowledgeExtractorOCA(filePath, cu));
			analyzedTopicExtractorList.add(new BuiltInJavaAPIKnowledgeExtractorOCA(filePath, cu));
			
			// OCP Topics
			analyzedTopicExtractorList.add(new AdvancedClassDesignKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new GenericKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new FunctionalInterfaceKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new StreamAPIKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new JavaDateTimeAPIKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new JavaIOFundamentalKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new JavaNIOKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new ConcurrencyKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new JDBCKnowledgeExtractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new LocalizationKnowledgeExctractorOCP(filePath, cu));
			analyzedTopicExtractorList.add(new JavaStringProcessingKnowledgeExtractorOCP(filePath, cu));
			
			// Java EE Topics
			analyzedTopicExtractorList.add(new JavaPersistanceKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new EnterpriseJavaBeanKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new MessageServiceKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaWebserviceSOAPKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new WebApplicationWithServletKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaRestFulWithJAXRXKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaWebSocketKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaJFSKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaCDIBeanKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaEEConcurrentKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new JavaEEBatchKnowledge(filePath, cu));

			TopicExtractorSourceCodeVisitor topicExtractorObject = new TopicExtractorSourceCodeVisitor(filePath, 
					cu, analyzedTopicExtractorList);
			
			topicExtractorObject.parseJavaSourceCode();
			
			javaKnowledgeUnitWritingResultUtilOb.writeTopicExtractionInformationToCSVFile(projectFullName, commitId, writer,filePath, analyzedTopicExtractorList,lineOfCode);
		}
		
		
	}

	public void writeJavaPackageUsageList(CsvWriter writer, String projectName, String projectLocation, int totalFile,
			Map<String, HashSet<String>> fileUsageJavaPackageList) throws Exception {
		for (String packageName : java8PackageList) {
			writer.write(projectName);
			writer.write(projectLocation);
			writer.write(packageName);
			if (fileUsageJavaPackageList.containsKey(packageName)) {
				double percentage = 100.0 * fileUsageJavaPackageList.get(packageName).size() / (double) totalFile;
				writer.write(Integer.toString(fileUsageJavaPackageList.get(packageName).size()));
				writer.write(String.format("%.3f", percentage));
			} else {
				writer.write("0");
				writer.write("0");
			}
			writer.endRecord();
		}
	}

	public void startAnalysis() throws Exception {
		System.out.println("Start Analysis...");
		java8PackageList = FileUtil.readJavaPackageList(ConstantUtil.JAVA_8_PACKAGE_FILE);
		studiedProjectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);

		writerUsageJavaPackage.write("Project_Name");
		writerUsageJavaPackage.write("Project_Location");
		writerUsageJavaPackage.write("Java_Package_Name");
		writerUsageJavaPackage.write("Number_Of_File_Use_Package");
		writerUsageJavaPackage.write("Percentage");
		writerUsageJavaPackage.endRecord();

		final long startTime = System.currentTimeMillis();

		for (int i = 169; i < studiedProjectList.size(); i++) {
			System.out.println(studiedProjectList.get(i));
			String projectName = studiedProjectList.get(i).split("/")[1];
			String projectFullName = studiedProjectList.get(i).replace("/", "_");
			filesUseJavaPackageList.clear();
			String projectLocation = ConstantUtil.STUDIED_PROJECT_DIR + projectFullName + "/";

			Map<String, Map<String, JavaClassModel>> fullClassList = new HashMap<String, Map<String, JavaClassModel>>();
			Set<String> allDefinedClassQualifiedName = new HashSet<String>();

			settingUpJDTEnvironment(projectLocation); // Initialize the JDT environment and create compilation unit for
														// each // file
			runBasicModel(fullClassList, allDefinedClassQualifiedName);
			CsvWriter writer = new CsvWriter(ConstantUtil.OUTPUT_FILE_LOCATION + projectFullName + ".csv");
			javaKnowledgeUnitWritingResultUtilOb.writeHeaders(writer);
			startExtractingJavaTopicII("",projectLocation, projectFullName, fullClassList, allDefinedClassQualifiedName,
					writer);
			writer.close();
			writeJavaPackageUsageList(writerUsageJavaPackage, projectName, projectLocation, fullClassList.size(),
					filesUseJavaPackageList);
			
			final long endTime = System.currentTimeMillis();
			long durationSecond = (endTime - startTime) / 1000;
			System.out.println("Finish: " + (i + 1) + " "+ studiedProjectList.get(i) + " Total execution time: " + durationSecond + " seconds");
			//break;
		}
		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
		System.out.println("Total execution time: " + durationSecond + " seconds");
		writerUsageJavaPackage.close();
	}

	public void startAnalysisPaper1DefectData() throws Exception {
		System.out.println("Start Analysis...");
		java8PackageList = FileUtil.readJavaPackageList(ConstantUtil.JAVA_8_PACKAGE_FILE);
		studiedProjectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);

		writerUsageJavaPackage.write("Project_Name");
		writerUsageJavaPackage.write("Project_Location");
		writerUsageJavaPackage.write("Java_Package_Name");
		writerUsageJavaPackage.write("Number_Of_File_Use_Package");
		writerUsageJavaPackage.write("Percentage");
		writerUsageJavaPackage.endRecord();

		final long startTime = System.currentTimeMillis();

		for (int i = 169; i < studiedProjectList.size(); i++) {
			System.out.println(studiedProjectList.get(i));
			String projectName = studiedProjectList.get(i).split("/")[1];
			String projectFullName = studiedProjectList.get(i).replace("/", "_");
			filesUseJavaPackageList.clear();
			String projectLocation = ConstantUtil.STUDIED_PROJECT_DIR + projectFullName + "/";

			Map<String, Map<String, JavaClassModel>> fullClassList = new HashMap<String, Map<String, JavaClassModel>>();
			Set<String> allDefinedClassQualifiedName = new HashSet<String>();

			settingUpJDTEnvironment(projectLocation); // Initialize the JDT environment and create compilation unit for
														// each // file
			runBasicModel(fullClassList, allDefinedClassQualifiedName);
			CsvWriter writer = new CsvWriter(ConstantUtil.OUTPUT_FILE_LOCATION + projectFullName + ".csv");
			javaKnowledgeUnitWritingResultUtilOb.writeHeaders(writer);
			startExtractingJavaTopicII("",projectLocation, projectFullName, fullClassList, allDefinedClassQualifiedName,
					writer);
			writer.close();
			writeJavaPackageUsageList(writerUsageJavaPackage, projectName, projectLocation, fullClassList.size(),
					filesUseJavaPackageList);
			
			final long endTime = System.currentTimeMillis();
			long durationSecond = (endTime - startTime) / 1000;
			System.out.println("Finish: " + (i + 1) + " "+ studiedProjectList.get(i) + " Total execution time: " + durationSecond + " seconds");
			//break;
		}
		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
		System.out.println("Total execution time: " + durationSecond + " seconds");
		writerUsageJavaPackage.close();
	}
	public void startReleaseLevelChangeAnalysis(
			String commitId,
			String projectLocation, 
			String projectFullName, 
			String outputFileLocation) throws Exception{
		 
		final long startTime = System.currentTimeMillis();
		
		Map<String, Map<String, JavaClassModel>> fullClassList = new HashMap<String, Map<String, JavaClassModel>>();
		Set<String> allDefinedClassQualifiedName = new HashSet<String>();
		settingUpJDTEnvironment(projectLocation); // Initialize the JDT environment and create compilation unit for											// each // file
		runBasicModel(fullClassList, allDefinedClassQualifiedName);
		
		CsvWriter writer = new CsvWriter(outputFileLocation);
		javaKnowledgeUnitWritingResultUtilOb.writeHeaders(writer);
		startExtractingJavaTopicII(commitId, projectLocation, projectFullName, fullClassList, allDefinedClassQualifiedName,
				writer);
		writer.close();
		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
	}
	
	public void startReleaseLevelWithDependencyChangeAnalysis(
			String commitId,
			String projectLocation, 
			String projectFullName, 
			String outputFileLocation) throws Exception{
		 
		final long startTime = System.currentTimeMillis();
		
		Map<String, Map<String, JavaClassModel>> fullClassList = new HashMap<String, Map<String, JavaClassModel>>();
		Set<String> allDefinedClassQualifiedName = new HashSet<String>();
		settingUpJDTEnvironment(projectLocation); // Initialize the JDT environment and create compilation unit for											// each // file
		runBasicModel(fullClassList, allDefinedClassQualifiedName);
		
		CsvWriter writer = new CsvWriter(outputFileLocation);
		javaKnowledgeUnitWritingResultUtilOb.writeHeaders(writer);
		startExtractingJavaTopicII(commitId, projectLocation, projectFullName, fullClassList, allDefinedClassQualifiedName,
				writer);
		writer.close();
		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
	}
	
	
	public static void main(String[] args) throws Exception {
		JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
		ob.startAnalysis();
		System.out.println("Program finishes successfully");
	}
}
