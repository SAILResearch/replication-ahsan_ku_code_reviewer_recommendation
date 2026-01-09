package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.StreamModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class StreamAPIKnowledgeExtractorOCP extends TopicExtractorBaseModel {
	public String filePath = "";
	public CompilationUnit cu = null;

	public String javaStreamOtherAPIs = "java.util.stream";
	
	Map<String, ArrayList<StreamModel>> allUsedStreamMethodlList = new HashMap<String, ArrayList<StreamModel>>();
	ArrayList<StreamModel> filterCollectionWithLambda = new ArrayList<StreamModel>();
	Map<String,ArrayList<JavaOtherElementModel>> optionalClassUsageList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	ArrayList<JavaOtherElementModel> sortingCollectionList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> mapStreamList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> peekStreamList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> flatMapStreamList = new ArrayList<JavaOtherElementModel>();

	Set<String> usageOfOtherStreamAPI = new HashSet<String>();

	public StreamAPIKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	
	
	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub


		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		IMethodBinding mBinding = node.resolveMethodBinding();
		if (mBinding != null) {
			ITypeBinding declareClassType = mBinding.getDeclaringClass();
			if (declareClassType != null) {
				String packageName = declareClassType.getQualifiedName();
				if (packageName.contains(javaStreamOtherAPIs)){
					usageOfOtherStreamAPI.add(packageName);
				}
				if (packageName.contains("<")) {
					packageName = packageName.substring(0, packageName.indexOf("<"));
				}
				String packageNameWithMethod = packageName + ConstantUtil.PACKAGE_SPLITTER
						+ node.getName().toString();
				//System.out.println("Method Invokation: " + packageNameWithMethod);

				if (packageName.contains("java.util.stream.Stream")) {
					StreamModel model = new StreamModel();
					model.setStartLine(startLine);
					model.setEndLine(endLine);
					model.setFullyQualifiedPackageName(packageName);
					model.setInvokedMethodName(node.getName().toString());
					if (!allUsedStreamMethodlList.containsKey(packageNameWithMethod)) {
						allUsedStreamMethodlList.put(packageNameWithMethod, new ArrayList<StreamModel>());
					}
					allUsedStreamMethodlList.get(packageNameWithMethod).add(model);

					if (node.getName().toString().compareTo("filter") == 0) {
						for (int i = 0; i < node.arguments().size(); i++) {
							if (node.arguments().get(i) instanceof LambdaExpression) {
								LambdaExpression lambda = (LambdaExpression) node.arguments().get(i);
								for (int j = 0; j < lambda.parameters().size(); j++) {
									if (lambda.parameters().get(i) instanceof VariableDeclaration) {
										VariableDeclaration var = (VariableDeclaration) lambda.parameters()
												.get(i);
										if (var != null) {
											// There is a variable declaration which suggest it should be a collection
											StreamModel ob = new StreamModel();
											ob.setStartLine(startLine);
											ob.setEndLine(endLine);
											filterCollectionWithLambda.add(ob);
										}
									}
								}
							}
						}
					}else if (node.getName().toString().compareTo("sorted") == 0) {
						String fullayQualifiedName = packageName + ConstantUtil.PACKAGE_SPLITTER + node.getName().toString();
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullayQualifiedName);
						ob.setElementType("SortedMethod");
						sortingCollectionList.add(ob);
					}
					else if (node.getName().toString().compareTo("map") == 0) {
						String fullayQualifiedName = packageName + ConstantUtil.PACKAGE_SPLITTER + node.getName().toString();
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullayQualifiedName);
						ob.setElementType("mapMethod");
						mapStreamList.add(ob);
					}
					else if (node.getName().toString().compareTo("peek") == 0) {
						String fullayQualifiedName = packageName + ConstantUtil.PACKAGE_SPLITTER + node.getName().toString();
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullayQualifiedName);
						ob.setElementType("peekMethod");
						peekStreamList.add(ob);
					}
					else if (node.getName().toString().compareTo("flatMap") == 0) {
						String fullayQualifiedName = packageName + ConstantUtil.PACKAGE_SPLITTER + node.getName().toString();
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullayQualifiedName);
						ob.setElementType("flatMapMethod");
						flatMapStreamList.add(ob);
					}

				}
				else if (packageName.contains(ConstantUtil.OPTIONAL_PACKAGE_CLASS)) {
					if(node.resolveTypeBinding() != null) {
						String fullayQualifiedName = packageName + ConstantUtil.PACKAGE_SPLITTER + node.getName().toString();
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setElementType("OptionalClass");
						ob.setName(fullayQualifiedName);
						
						if(!optionalClassUsageList.containsKey(fullayQualifiedName)) {
							optionalClassUsageList.put(fullayQualifiedName, new ArrayList<JavaOtherElementModel>());
						}
						optionalClassUsageList.get(fullayQualifiedName).add(ob);
						
					}
				}
				
				
			}
		}
	}



	public void extractTopic() throws Exception {
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
	}
	public ArrayList<StreamModel> getFilterCollectionWithLambda() {
		return filterCollectionWithLambda;
	}
	public void setFilterCollectionWithLambda(ArrayList<StreamModel> filterCollectionWithLambda) {
		this.filterCollectionWithLambda = filterCollectionWithLambda;
	}
	public Map<String, ArrayList<JavaOtherElementModel>> getOptionalClassUsageList() {
		return optionalClassUsageList;
	}
	public void setOptionalClassUsageList(Map<String, ArrayList<JavaOtherElementModel>> optionalClassUsageList) {
		this.optionalClassUsageList = optionalClassUsageList;
	}
	public ArrayList<JavaOtherElementModel> getSortingCollectionList() {
		return sortingCollectionList;
	}
	public void setSortingCollectionList(ArrayList<JavaOtherElementModel> sortingCollectionList) {
		this.sortingCollectionList = sortingCollectionList;
	}
	public ArrayList<JavaOtherElementModel> getMapStreamList() {
		return mapStreamList;
	}
	public void setMapStreamList(ArrayList<JavaOtherElementModel> mapStreamList) {
		this.mapStreamList = mapStreamList;
	}
	public ArrayList<JavaOtherElementModel> getPeekStreamList() {
		return peekStreamList;
	}
	public void setPeekStreamList(ArrayList<JavaOtherElementModel> peekStreamList) {
		this.peekStreamList = peekStreamList;
	}
	public ArrayList<JavaOtherElementModel> getFlatMapStreamList() {
		return flatMapStreamList;
	}
	public void setFlatMapStreamList(ArrayList<JavaOtherElementModel> flatMapStreamList) {
		this.flatMapStreamList = flatMapStreamList;
	}
	
	public Map<String,ArrayList<StreamModel>> getSearchStreamAPIUsage() {
		Map<String,ArrayList<StreamModel>> streamList = new HashMap<String,ArrayList<StreamModel>>();
		for(String name : allUsedStreamMethodlList.keySet()) {
			//System.out.println(name);
			if(ConstantUtil.searchStreamList.contains(name)) {
				streamList.put(name,allUsedStreamMethodlList.get(name));
			}
		}
		return streamList;
	}
	
	public ArrayList<StreamModel> getUsageOfForEach(){
		for(String name : allUsedStreamMethodlList.keySet()) {
			if(name.contains("java.util.stream.Stream.forEach")) {
				return allUsedStreamMethodlList.get(name);
			}
		}
		return new ArrayList<StreamModel>();
	}
	
	public ArrayList<StreamModel> getUsageOfCollector(){
		for(String name : allUsedStreamMethodlList.keySet()) {
			if(name.contains("java.util.stream.Stream.collect")) {
				return allUsedStreamMethodlList.get(name);
			}
		}
		return new ArrayList<StreamModel>();
	}

	public Set<String> getUsageOfOtherStreamAPI() {
		return usageOfOtherStreamAPI;
	}
	
}
