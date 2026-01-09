package com.sail.domain.categorization;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.csvreader.CsvWriter;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class LDAInputGenerator {

	private HashMap<String, CompilationUnit> compilationUnitList;
	public static ArrayList<String> studiedProjectList = new ArrayList<String>();

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

		// sources and classpath
		String[] sources = { projectPath };
		String[] classpath = new String[] { /* JARs */ };
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

	public void generateLDAInput() throws Exception {
		studiedProjectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		final long startTime = System.currentTimeMillis();

		Map<String, String> projectClassNamesString = new HashMap<String, String>();

		for (int i = 0; i < studiedProjectList.size(); i++) {
			System.out.println(studiedProjectList.get(i));
			String projectName = studiedProjectList.get(i).split("/")[1];
			String projectFullName = studiedProjectList.get(i).replace("/", "_");
			String projectLocation = ConstantUtil.STUDIED_PROJECT_DIR + projectFullName + "/";

			try {
				settingUpJDTEnvironment(projectLocation);
				List<String> classNamesList = new ArrayList<String>();

				for (String javaFilePath : compilationUnitList.keySet()) {
					CompilationUnit cu = compilationUnitList.get(javaFilePath);
					if (cu == null)
						continue;
					cu.accept(new ASTVisitor() {
						@Override
						public boolean visit(TypeDeclaration node) {
							// System.out.println("Node Name: " + node.getName());
							classNamesList.add(node.getName().toString());
							return super.visit(node);
						}
					});
				}
				System.out.println("Project " + projectFullName + " Total Classes: " + classNamesList.size());
				String ss = "";
				for (int j = 0; j < classNamesList.size(); j++) {
					// System.out.println(j + " " + classNamesList.get(j));
					ss += classNamesList.get(j) + " ";
				}
				ss = ss.trim();
				projectClassNamesString.put(projectFullName, ss);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String outputFile = ConstantUtil.ROOT + "/Result/project_class_names_lda_corpus.csv";
		CsvWriter writer = new CsvWriter(outputFile);
		writer.write("Project_Name");
		writer.write("Class_Names");
		writer.endRecord();
		for (String projectName : projectClassNamesString.keySet()) {
			String classNameString = projectClassNamesString.get(projectName);
			writer.write(projectName);
			writer.write(classNameString);
			writer.endRecord();
		}
		writer.close();
	}

	public static void main(String arg[]) throws Exception {
		LDAInputGenerator ob = new LDAInputGenerator();
		ob.generateLDAInput();
		System.out.println("Finish Successfully");
	}

}
