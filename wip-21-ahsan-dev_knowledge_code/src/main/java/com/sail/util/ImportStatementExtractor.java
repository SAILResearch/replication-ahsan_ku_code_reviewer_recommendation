package com.sail.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

public class ImportStatementExtractor {
    
    public HashMap<String, CompilationUnit> settingUpJDTEnvironment(String projectPath) {

        HashMap<String, CompilationUnit> compilationUnitList;

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

		List<String> inProjectJavaFilePathList = FileUtil.getAllFilesWithExtension(projectPath, "java");
		String[] sourceFilesPaths = new String[inProjectJavaFilePathList.size()];
		for (int i = 0; i < inProjectJavaFilePathList.size(); i++) {
			sourceFilesPaths[i] = inProjectJavaFilePathList.get(i);
		}
		System.out.println("In Project Java Files ["+inProjectJavaFilePathList.size()+"]");
		
		parser.createASTs(sourceFilesPaths, null, new String[0], r, null);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println("Total Java files " + inProjectJavaFilePathList.size());
		System.out.println("Total time to initialize JDT Environment " + totalTime + " ms");

        return compilationUnitList;
	}

    public Map<String,String> extractImportStatement(String projectPath){
        HashMap<String, CompilationUnit> compilationUnitList = settingUpJDTEnvironment(projectPath);
        Map<String,String> perProjectImportStatement = new HashMap<String, String>();
        int total = 0;
        for(String fileName : compilationUnitList.keySet()){
            //System.out.println("F "  + fileName);
            ++total;
            //System.out.println(String.format("Working %s [%d/%d]", fileName, total, compilationUnitList.size()));
            CompilationUnit cu = compilationUnitList.get(fileName);
            ParseJavaFiles parser = new ParseJavaFiles(fileName, cu);
            parser.extractAPIUsage();
            Set<String>importStatementList = parser.getImportStatementList();
            String importString = TextUtil.convertSetToString(importStatementList, "-");
            //System.out.println(importString);
            perProjectImportStatement.put(fileName, importString);
        }
        return perProjectImportStatement;
    }

    public static void main(String[] args) {
        String gitProject = "/home/local/SAIL/ahsan/android_sdk_analyzer/repositories-jun-30-23/1fish2-BBQTimer/";
        ImportStatementExtractor ob = new ImportStatementExtractor();
        ob.extractImportStatement(gitProject);
        System.out.println("Program finishes successfully");
    }
}
