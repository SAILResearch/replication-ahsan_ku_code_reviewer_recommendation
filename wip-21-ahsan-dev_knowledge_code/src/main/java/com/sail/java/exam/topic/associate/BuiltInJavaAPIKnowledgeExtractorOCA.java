package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.java.exam.work.OverrideDetector;
import com.sail.model.JavaClassModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;

public class BuiltInJavaAPIKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;
	

	List<String> dateAPIList = Arrays.asList("java.time.LocalDateTime", "java.time.LocalDate", "java.time.LocalTime",
			"java.time.format.DateTimeFormatter", "java.time.Period");

	ArrayList<JavaOtherElementModel> manipulatingDateAPI = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> manipulatingStringAPI = new ArrayList<JavaOtherElementModel>();

	public BuiltInJavaAPIKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	public ArrayList<JavaOtherElementModel> getManipulatingDateAPI() {
		return manipulatingDateAPI;
	}

	public void setManipulatingDateAPI(ArrayList<JavaOtherElementModel> manipulatingDateAPI) {
		this.manipulatingDateAPI = manipulatingDateAPI;
	}

	public ArrayList<JavaOtherElementModel> getManipulatingStringAPI() {
		return manipulatingStringAPI;
	}

	public void setManipulatingStringAPI(ArrayList<JavaOtherElementModel> manipulatingStringAPI) {
		this.manipulatingStringAPI = manipulatingStringAPI;
	}

	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub
		if (node.getExpression() != null) {
			int startLine = cu.getLineNumber(node.getStartPosition());
			int nodeLength = node.getLength();
			int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

			String callerName = node.getExpression().toString();
			ITypeBinding typeBinding = node.resolveTypeBinding();

			if (typeBinding != null) {
				if (dateAPIList.contains(typeBinding.getQualifiedName().toString())) {
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName("ManipulatingDateAPI");
					manipulatingDateAPI.add(ob);

				}
				if (typeBinding.getQualifiedName().toString().compareTo("java.lang.StringBuilder") == 0) {
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName("ManipulatingStringAPI");
					manipulatingStringAPI.add(ob);
				}
			}

			// System.out.println("Invoke: " + typeBinding.getQualifiedName() +" " +
			// callerName);

		}
		
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodInvocation node) {
				
				return super.visit(node);
			}

		});
		//System.out.println("===========================");
		//System.out.println("Manipulating Date API : " + manipulatingDateAPI.size());
		//System.out.println("Manipulating StringBuilderAPI " + manipulatingStringAPI.size());
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
