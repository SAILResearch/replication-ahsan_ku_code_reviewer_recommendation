package com.sail.test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sail.java.exam.topic.associate.InheritanceKnowledgeExtractorOCA;
import com.sail.java.exam.topic.javaee.WebApplicationWithServletKnowledge;
import com.sail.java.exam.topic.professional.JavaDateTimeAPIKnowledgeExtractorOCP;
import com.sail.java.exam.work.BasicClassInfoExtractor;
import com.sail.java.exam.work.TopicExtractorSourceCodeVisitor;
import com.sail.model.JavaClassModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.FileUtil;
import com.sail.util.TextUtil;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MyJDTTester {

	public String javaProjectDirectory = "/home/local/SAIL/ahsan/git/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/src/main/java/com/sail/test/";

	public void variableLengthArgument(int... str) {

	}

	// public String
	// javaProjectDirectory="C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\Java_Exam_Work\\SampleGitRepos\\elasticsearch-master\\client\\sniffer\\src\\";
	// use ASTParse to parse string
	public static void parse(String filePath, String str) throws Exception {
		ASTParser parser = ASTParser.newParser(AST.JLS9);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);

		final Hashtable<String, String> options = JavaCore.getOptions();
		options.put("org.eclipse.jdt.core.compiler.source", "1.8");
		parser.setCompilerOptions(options);

		// sources and classpath
		String[] sources = new String[] { /* source folders */ };
		String[] classpath = new String[] { /* JARs */ };
		String[] encodings = new String[sources.length];
		Arrays.fill(encodings, StandardCharsets.UTF_8.name());
		parser.setEnvironment(classpath, sources, encodings, true);
		parser.setUnitName("code");

		parser.setSource(str.toCharArray());

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(TypeDeclaration node) {

				System.out.println("Node Name: " + node.getName());

				return super.visit(node);
			}
		});

		ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList = new ArrayList<TopicExtractorBaseModel>();
		// analyzedTopicExtractorList.add(new ConcurrencyKnowledgeExtractorOCP(filePath,
		// cu));
		analyzedTopicExtractorList.add(new JavaDateTimeAPIKnowledgeExtractorOCP(filePath, cu));

		TopicExtractorSourceCodeVisitor topicExtractorObject = new TopicExtractorSourceCodeVisitor(filePath, cu,
				analyzedTopicExtractorList);

		topicExtractorObject.parseJavaSourceCode();
		JavaDateTimeAPIKnowledgeExtractorOCP conObject = (JavaDateTimeAPIKnowledgeExtractorOCP) analyzedTopicExtractorList
				.get(0);

		System.out.println(conObject.getJavaLocalDateList().size());

		// conObject.printMethodInvocationOfExecutorService();

		// TopicExtractorSection2 extractSection2 = new TopicExtractorSection2(filePath,
		// cu);
		// TopicExtractorSection6 extractSection6 = new TopicExtractorSection6(filePath,
		// cu);
		// extractSection6.extractTopic();
		// extractSection6.findOverLoadedMethods();
		// extractSection6.printFieldInfo();

		// BasicClassInfoExtractor ob = new BasicClassInfoExtractor(cu);
		// ob.extractClassInfo();

		// OCP EXAM
		// TopicExtractorSection2 ob = new TopicExtractorSection2(filePath, cu);
		// ob.extractTopic();

		// JavaExamTopicExtractor.java8PackageList =
		// FileUtil.readJavaPackageList(ConstantUtil.JAVA_8_PACKAGE_FILE);

		// TopicExtractorProfessionalTest ob = new
		// TopicExtractorProfessionalTest(filePath, cu);
		// ob.extractTopic();

		/*
		 * System.out.println("Built-interface: " + ob.getBuiltInInterface().size());
		 * System.out.println("PrimInterface: " +
		 * ob.getPrimitiveFunctionalInterface().size());
		 * System.out.println("BinaryInterface: " +
		 * ob.getBinaryFunctionalInterface().size());
		 * System.out.println("UnaryInterface: " +
		 * ob.getUnaryFunctionalInterface().size());
		 */
		// ob.getCompareToMethodCall();
		// ob.getCreationFourGenInterface();
		// ob.getSearchStreamAPIUsage();
	}

	public void testJDT2(String filePath) throws Exception {
		parse(filePath, FileUtil.readFileToString(filePath));
	}

	private HashMap<String, CompilationUnit> compilationUnitList;

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
			fullClassList.put(javaFilePath, new HashMap<String, JavaClassModel>());
			++counter;
			// System.out.println("[" + counter + "] Extracting java topic for File: " +
			// javaFilePath);
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
		String[] classpath = new String[] { "/home/local/SAIL/ahsan/visual_studio_workspace/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/others/jars/javaee-api-8.0.jar",
	"/home/local/SAIL/ahsan/visual_studio_workspace/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/others/jars/javaee-api-7.0.jar" /* JARs */ };
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

	public void testJavaEETopic() throws Exception {
		//String myProjectPath = "/home/local/SAIL/ahsan/visual_studio_workspace/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/src/main/java/com/sail/test/";
		String myProjectPath = "/scratch/ahsan/Java_Exam_Work/GitReposistories/apache_stratos/dependencies/org.wso2.carbon.ui/src/main/java/org/wso2/carbon/ui/transports/";
		settingUpJDTEnvironment(myProjectPath);
		List<String> inProjectJavaFilePathList = FileUtil.getAllFilesWithExtension(myProjectPath, ".java");

		for (String filePath : inProjectJavaFilePathList) {
			
			if (!(filePath.contains("FileDownloadServlet"))) {
				continue;
			}

			System.out.println(filePath);
			ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList = new ArrayList<TopicExtractorBaseModel>();
			CompilationUnit cu = compilationUnitList.get(filePath);
			//analyzedTopicExtractorList.add(new MessageServiceKnowledge(filePath, cu));
			analyzedTopicExtractorList.add(new WebApplicationWithServletKnowledge(filePath, cu));
			// analyzedTopicExtractorList.add(new JavaWebserviceSOAPKnowledge(filePath, cu));
			//analyzedTopicExtractorList.add(new JavaRestFulWithJAXRXKnowledge(filePath, cu));
			//analyzedTopicExtractorList.add(new EnterpriseJavaBeanKnowledge(filePath, cu));
			//analyzedTopicExtractorList.add(new JavaCDIBeanKnowledge(filePath, cu));
			//analyzedTopicExtractorList.add(new JavaEEConcurrentKnowledge(filePath, cu));
			
			TopicExtractorSourceCodeVisitor topicExtractorObject = new TopicExtractorSourceCodeVisitor(filePath, cu,
					analyzedTopicExtractorList);

			topicExtractorObject.parseJavaSourceCode();
			WebApplicationWithServletKnowledge ob = (WebApplicationWithServletKnowledge) analyzedTopicExtractorList.get(0);
			//JavaWebserviceSOAPKnowledge ob = (JavaWebserviceSOAPKnowledge) analyzedTopicExtractorList.get(0);
			//JavaRestFulWithJAXRXKnowledge ob = (JavaRestFulWithJAXRXKnowledge) analyzedTopicExtractorList.get(0);
			//EnterpriseJavaBeanKnowledge ob = (EnterpriseJavaBeanKnowledge) analyzedTopicExtractorList.get(0);
			
			//JavaCDIBeanKnowledge ob = (JavaCDIBeanKnowledge) analyzedTopicExtractorList.get(0);
			//JavaEEConcurrentKnowledge ob = (JavaEEConcurrentKnowledge) analyzedTopicExtractorList.get(0);
				
			//System.out.println("Tranaction: " + ob.checkIfTransactionIsUsed());
			ob.printInfo();
		}
	}

	public void testJDT() throws Exception {
		// List<String> javaFiles =
		// FileUtil.getAllFilesWithExtension(javaProjectDirectory, ".java");
		Map<String, Map<String, JavaClassModel>> fullClassList = new HashMap<String, Map<String, JavaClassModel>>();
		Set<String> allDefinedClassQualifiedName = new HashSet<String>();

		settingUpJDTEnvironment(javaProjectDirectory); // Initialize the JDT environment and create compilation unit for
														// each // file
		runBasicModel(fullClassList, allDefinedClassQualifiedName);

		for (String s : allDefinedClassQualifiedName) {
			System.out.println("DC: " + s);
		}

		for (String filePath : fullClassList.keySet()) {
			System.out.println("F: " + filePath);
			ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList = new ArrayList<TopicExtractorBaseModel>();
			CompilationUnit cu = compilationUnitList.get(filePath);
			analyzedTopicExtractorList
					.add(new InheritanceKnowledgeExtractorOCA(filePath, cu, allDefinedClassQualifiedName));
			TopicExtractorSourceCodeVisitor topicExtractorObject = new TopicExtractorSourceCodeVisitor(filePath, cu,
					analyzedTopicExtractorList);

			topicExtractorObject.parseJavaSourceCode();
			InheritanceKnowledgeExtractorOCA ob = (InheritanceKnowledgeExtractorOCA) analyzedTopicExtractorList.get(0);

			String st = TextUtil.convertSetToString(ob.invokedMethodList, "-");
			System.out.println("String: " + st);
		}

	}

	public static void main(String[] args) throws Exception {
		MyJDTTester ob = new MyJDTTester();
		ob.testJavaEETopic();
		//ob.testJDT();
		// ob.testJDT2("C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\EclipseTypeBinder\\src\\com\\sail\\etb\\typebinder\\EclipseTypeBinder.java");
		// ob.testJDT2("C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\EclipseTypeBinder\\src\\com\\sail\\etb\\typebinder\\TypeFinderVisitor.java");
		// ob.testJDT2(
		// "C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\Java_Exam_Work\\SampleGitRepos\\a.java");
		// ob.testJDT2("C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\Java_Exam_Work\\SampleGitRepos\\elasticsearch-master\\client\\sniffer\\src\\main\\java\\org\\elasticsearch\\client\\sniff\\ElasticsearchNodesSniffer.java");
		// ob.testJDT2("C:\\Users\\parvez\\Documents\\Ahasanuzzaman\\Research_Queens_PHD\\Java_Exam_Work\\SampleGitRepos\\a.java");

		// OCP EXAM

		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/MainPair.java");
		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/FunctionalInterfaceExample.java");

		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/FileTester.java");

		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/AbstractClassTest.java");
		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/FileTester.java");

		// ob.testJDT2("/Users/ahsan/OneDrive/Ahsan-Mac-Data/Queens_PHD/Sail_Lab_Research/Java_Exam_Project/GitReposistories/elasticsearch-master/server/src/main/java/org/elasticsearch/discovery/DiscoveryModule.java");

		// ob.testJDT2("/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/GitReposistories/h2o-dev/h2o-algos/src/test/java/hex/deeplearning/DeepLearningMissingTest.java");
		// ob.testJDT2("/home/local/SAIL/ahsan/git/wip-21-ahsan-dev_knowledge_code/wip-21-ahsan-dev_knowledge_code/src/main/java/com/sail/test/SuperClassSubClassTest.java");

		// ob.testJDT2("/Users/ahsan/git/wip-20-ahsan-java_exam_work/src/main/java/com/sail/test/FunctionalInterfaceExample.java");

		System.out.println("Program finishes successfully");
	}
}
