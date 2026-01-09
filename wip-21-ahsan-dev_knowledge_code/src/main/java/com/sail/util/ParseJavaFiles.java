package com.sail.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;

public class ParseJavaFiles {
    public String filePath = "";
	public CompilationUnit cu = null;
    Set<String> importStatementList = new HashSet<String>();

    
    public Set<String> getImportStatementList() {
        return importStatementList;
    }

    public ParseJavaFiles(String filePath, CompilationUnit cu){
        this.filePath = filePath;
        this.cu = cu;
    }

    public void extractAPIUsage() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(ImportDeclaration node) {
                IBinding typeBinding = node.resolveBinding();
                String importStatement = node.toString().trim();
                if (importStatement.length() > 0){
                    importStatement = importStatement.substring("import".length()).trim();
                    importStatement = importStatement.substring(0, importStatement.indexOf(";")).trim();
                    importStatementList.add(importStatement);
                }
                //System.out.println(importStatement); 
                return super.visit(node);
            }
        
        });
    }

    public static void main(String[] args) {
        
    }
}
