package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.TextUtil;

public class ConcurrencyKnowledgeExtractorOCP extends TopicExtractorBaseModel {

	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaOtherElementModel> implementRunnable = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> extendsThread = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> threadInstanceCreationList = new ArrayList<JavaOtherElementModel>();
	
	Map<String, ArrayList<JavaOtherElementModel>> executorMethodCallList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	ArrayList<JavaOtherElementModel> callableInstanceCreationList = new ArrayList<JavaOtherElementModel>();
	
	Map<String, ArrayList<JavaOtherElementModel>> scheduleExecutorServiceList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	Map<String, ArrayList<JavaOtherElementModel>> synchronizeMethodCallList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	Map<String, ArrayList<JavaOtherElementModel>> parallelMethodList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	Map<String,Map<String,ArrayList<JavaOtherElementModel>>> concurrentCollectionList =
			new HashMap<String,Map<String,ArrayList<JavaOtherElementModel>>>();
	
	Map<String, ArrayList<JavaOtherElementModel>> forkJoinTaskList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> forkJoinPoolList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> recursiveTaskList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	public ConcurrencyKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	public void visitTypeDeclaration(TypeDeclaration node) {
		ITypeBinding typeBinding = node.resolveBinding();

		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		// System.out.println("Node: " + node.toString());
		if (typeBinding != null) {
			ITypeBinding interfaceBindingList[] = typeBinding.getInterfaces();
			for (ITypeBinding interfaceBinding : interfaceBindingList) {
				String qualifiedName = interfaceBinding.getQualifiedName();
				// System.out.println("Implemented: " + qualifiedName);
				if (qualifiedName.compareTo("java.lang.Runnable") == 0) {
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					implementRunnable.add(ob);
				}
			}
			ITypeBinding superClassBinding = typeBinding.getSuperclass();
			if (superClassBinding != null) {
				if (superClassBinding.getQualifiedName().compareTo("") == 0) {

				}
			}
		}
	}

	public void visitClassInstanceCreation(ClassInstanceCreation node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		ITypeBinding typeBinding = node.resolveTypeBinding();
		if (typeBinding != null) {
			String qualifiedNameClassInstanceCreation = typeBinding.getQualifiedName();
			// System.out.println("Thread instance creator: " +
			// qualifiedNameClassInstanceCreation);
			if (qualifiedNameClassInstanceCreation.compareTo("java.lang.Thread") == 0) {
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				threadInstanceCreationList.add(ob);
				// System.out.println("FIND Thread instance creator: " +
				// qualifiedNameClassInstanceCreation);
			}
		}
	}

	public void visitMethodInvocation(MethodInvocation node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if (methodBinding != null) {
			ITypeBinding declareClassType = methodBinding.getDeclaringClass();
			// System.out.println("Declaring class: " + declareClassType.);
			if (declareClassType != null) {
				String qualifiedName = declareClassType.getQualifiedName();
				if (qualifiedName.contains("<")) {
					qualifiedName = qualifiedName.substring(0, qualifiedName.indexOf("<"));
				}
				String fullMethodName = qualifiedName + TextUtil.DOT_SEPERATOR + node.getName();
				//System.out.println("MI: " + qualifiedName + " " + node.getName() + " " + fullMethodName);
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("method-invocation");
				ob.setName(node.getName().toString());

				for (String concurrentMethodCall : ConstantUtil.CONCURRENT_EXECUTOR_SERVICE_LIST) {
					if (qualifiedName.compareTo(concurrentMethodCall) == 0) {
						if (!executorMethodCallList.containsKey(fullMethodName)) {
							executorMethodCallList.put(fullMethodName, new ArrayList<JavaOtherElementModel>());
						}
						executorMethodCallList.get(fullMethodName).add(ob);
					}
				}
				if (qualifiedName.compareTo(ConstantUtil.CONCURRENT_SCHEDULE_EXECUTOR_SERVICE) == 0) {
					if (!scheduleExecutorServiceList.containsKey(node.getName())) {
						scheduleExecutorServiceList.put(node.getName().toString(),
								new ArrayList<JavaOtherElementModel>());

					}
					scheduleExecutorServiceList.get(node.getName().toString()).add(ob);
				}
				for (String synchronizeAPI : ConstantUtil.CONCURRENT_SYNCHRONIZE_LIST) {
					if (fullMethodName.compareTo(synchronizeAPI) == 0) {
						if (!synchronizeMethodCallList.containsKey(fullMethodName)) {
							synchronizeMethodCallList.put(fullMethodName, new ArrayList<JavaOtherElementModel>());
						}
						synchronizeMethodCallList.get(fullMethodName).add(ob);
						// System.out.println("GOT>>> " + fullMethodName);
					}
				}

				if (node.getName().toString().toLowerCase().contains("parallel")
						&& (qualifiedName.contains("Collection") || qualifiedName.contains("Stream"))) {
					if (!parallelMethodList.containsKey(node.toString())) {
						parallelMethodList.put(node.toString(), new ArrayList<JavaOtherElementModel>());
					}
					parallelMethodList.get(node.toString()).add(ob);
					// System.out.println("Parallel Method: " + node.toString());
				}

				if (qualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_TASK) == 0) {
					if (!forkJoinTaskList.containsKey(node.getName())) {
						forkJoinTaskList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					forkJoinTaskList.get(node.getName().toString()).add(ob);
				}
				if (qualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_POOL) == 0) {
					if (!forkJoinPoolList.containsKey(node.getName())) {
						forkJoinPoolList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					forkJoinPoolList.get(node.getName().toString()).add(ob);
				}
				if (qualifiedName.compareTo(ConstantUtil.CONCURRENT_RECURSIVE_TASK) == 0) {
					if (!recursiveTaskList.containsKey(node.getName())) {
						recursiveTaskList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					recursiveTaskList.get(node.getName().toString()).add(ob);
				}
				
				for(String concurrencyAPI : ConstantUtil.CONCURRENT_COLLECTION_LIST) {
					if(qualifiedName.compareTo(concurrencyAPI) == 0) {
						if(!concurrentCollectionList.containsKey(concurrencyAPI)) {
							concurrentCollectionList.put(concurrencyAPI, new HashMap<String,ArrayList<JavaOtherElementModel>>());
						}
						if(!concurrentCollectionList.get(concurrencyAPI).containsKey(node.getName().toString())){
							concurrentCollectionList.get(concurrencyAPI).put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
						}
						concurrentCollectionList.get(concurrencyAPI).get(node.getName().toString()).add(ob);
					}
				}

			}
		}
	}

	public void visitVariableDeclarationStatement(VariableDeclarationStatement node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if (node.getType() != null) {
			ITypeBinding typeBinding = node.getType().resolveBinding();
			if (typeBinding != null) {
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setName(node.toString());
				String fullyQualifiedName = typeBinding.getQualifiedName();
				if (fullyQualifiedName.contains("<")) {
					fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("<"));
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_CALLABLE) == 0) {
					ob.setElementType("callable-instance-creation");
					callableInstanceCreationList.add(ob);
					//System.out.println("Callable instance: " + fullyQualifiedName);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_TASK) == 0) {
					ob.setElementType("fork_join_task-instance-creation");
					if(!forkJoinTaskList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						forkJoinTaskList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					forkJoinTaskList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_POOL) == 0) {
					ob.setElementType("fork_join_pool-instance-creation");
					if(!forkJoinPoolList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						forkJoinPoolList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					forkJoinPoolList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_RECURSIVE_TASK) == 0) {
					ob.setElementType("recursive_task-instance-creation");
					if(!recursiveTaskList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						recursiveTaskList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					recursiveTaskList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				
				for(String concurrencyAPI : ConstantUtil.CONCURRENT_COLLECTION_LIST) {
					if(fullyQualifiedName.compareTo(concurrencyAPI) == 0) {
						ob.setElementType("concurrent_collection-instance-creation");
						if(!concurrentCollectionList.containsKey(concurrencyAPI)) {
							concurrentCollectionList.put(concurrencyAPI, new HashMap<String,ArrayList<JavaOtherElementModel>>());
						}
						if(!concurrentCollectionList.get(concurrencyAPI).containsKey(ConstantUtil.CONSTRUCTOR.toString())){
							concurrentCollectionList.get(concurrencyAPI).put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
						}
						concurrentCollectionList.get(concurrencyAPI).get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
					}
				}
			}
		}
	}

	public void visitFieldDeclaration(FieldDeclaration node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if (node.getType() != null) {
			ITypeBinding typeBinding = node.getType().resolveBinding();
			if (typeBinding != null) {
				String fullyQualifiedName = typeBinding.getQualifiedName();
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				
				ob.setName(node.toString());
				
				if (fullyQualifiedName.contains("<")) {
					fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("<"));
				}
				
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_CALLABLE) == 0) {
					ob.setElementType("callable-instance-creation");
					callableInstanceCreationList.add(ob);
					//System.out.println("Callable Field instance: " + fullyQualifiedName);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_TASK) == 0) {
					ob.setElementType("fork_join_task-instance-creation");
					if(!forkJoinTaskList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						forkJoinTaskList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					forkJoinTaskList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_FORK_JOIN_POOL) == 0) {
					ob.setElementType("fork_join_pool-instance-creation");
					if(!forkJoinPoolList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						forkJoinPoolList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					forkJoinPoolList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_RECURSIVE_TASK) == 0) {
					ob.setElementType("fork_join_recursive-instance-creation");
					if(!recursiveTaskList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						recursiveTaskList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
					}
					recursiveTaskList.get(ConstantUtil.CONSTRUCTOR).add(ob);
				}
				for(String concurrencyAPI : ConstantUtil.CONCURRENT_COLLECTION_LIST) {
					if(fullyQualifiedName.compareTo(concurrencyAPI) == 0) {
						ob.setElementType("concurrent_collection-instance-creation");
						if(!concurrentCollectionList.containsKey(concurrencyAPI)) {
							concurrentCollectionList.put(concurrencyAPI, new HashMap<String,ArrayList<JavaOtherElementModel>>());
						}
						if(!concurrentCollectionList.get(concurrencyAPI).containsKey(ConstantUtil.CONSTRUCTOR.toString())){
							concurrentCollectionList.get(concurrencyAPI).put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
						}
						concurrentCollectionList.get(concurrencyAPI).get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
					}
				}
			}
		}
	}

	public void visitMethodDeclaration(MethodDeclaration node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		// System.out.println("Method Declaration: ");
		for (int i = 0; i < node.parameters().size(); i++) {
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) node.parameters().get(i);
			if (parameter.resolveBinding() != null && parameter.getType() != null) {
				ITypeBinding typeBinding = parameter.getType().resolveBinding();
				if (typeBinding != null) {
					String fullyQualifiedName = typeBinding.getQualifiedName();
					if (fullyQualifiedName.contains("<")) {
						fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("<"));
					}
					// System.out.println("QN: " + fullyQualifiedName);
					if (fullyQualifiedName.compareTo(ConstantUtil.CONCURRENT_CALLABLE) == 0) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setElementType("callable-parameter-creation");
						ob.setName(node.toString());
						callableInstanceCreationList.add(ob);
						//System.out.println("Callable parameter: " + fullyQualifiedName);
					}
				}
			}
		}
	}

	
	
	public void printMethodInvocationOfExecutorService() {
		for (String methodName : this.executorMethodCallList.keySet()) {
			//System.out.println("Method: " + methodName);
		}
	}

	public static void main(String[] args) {
		System.out.println("Program finishes successfully");
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public ArrayList<JavaOtherElementModel> getImplementRunnable() {
		return implementRunnable;
	}

	public void setImplementRunnable(ArrayList<JavaOtherElementModel> implementRunnable) {
		this.implementRunnable = implementRunnable;
	}

	public ArrayList<JavaOtherElementModel> getExtendsThread() {
		return extendsThread;
	}

	public void setExtendsThread(ArrayList<JavaOtherElementModel> extendsThread) {
		this.extendsThread = extendsThread;
	}

	public ArrayList<JavaOtherElementModel> getThreadInstanceCreationList() {
		return threadInstanceCreationList;
	}

	public void setThreadInstanceCreationList(ArrayList<JavaOtherElementModel> threadInstanceCreationList) {
		this.threadInstanceCreationList = threadInstanceCreationList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getExecutorMethodCallList() {
		return executorMethodCallList;
	}

	public void setExecutorMethodCallList(Map<String, ArrayList<JavaOtherElementModel>> executorMethodCallList) {
		this.executorMethodCallList = executorMethodCallList;
	}

	public ArrayList<JavaOtherElementModel> getCallableInstanceCreationList() {
		return callableInstanceCreationList;
	}

	public void setCallableInstanceCreationList(ArrayList<JavaOtherElementModel> callableInstanceCreationList) {
		this.callableInstanceCreationList = callableInstanceCreationList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getScheduleExecutorServiceList() {
		return scheduleExecutorServiceList;
	}

	public void setScheduleExecutorServiceList(Map<String, ArrayList<JavaOtherElementModel>> scheduleExecutorServiceList) {
		this.scheduleExecutorServiceList = scheduleExecutorServiceList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getSynchronizeMethodCallList() {
		return synchronizeMethodCallList;
	}

	public void setSynchronizeMethodCallList(Map<String, ArrayList<JavaOtherElementModel>> synchronizeMethodCallList) {
		this.synchronizeMethodCallList = synchronizeMethodCallList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getParallelMethodList() {
		return parallelMethodList;
	}

	public void setParallelMethodList(Map<String, ArrayList<JavaOtherElementModel>> parallelMethodList) {
		this.parallelMethodList = parallelMethodList;
	}

	public Map<String, Map<String, ArrayList<JavaOtherElementModel>>> getConcurrentCollectionList() {
		return concurrentCollectionList;
	}

	public void setConcurrentCollectionList(
			Map<String, Map<String, ArrayList<JavaOtherElementModel>>> concurrentCollectionList) {
		this.concurrentCollectionList = concurrentCollectionList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getForkJoinTaskList() {
		return forkJoinTaskList;
	}

	public void setForkJoinTaskList(Map<String, ArrayList<JavaOtherElementModel>> forkJoinTaskList) {
		this.forkJoinTaskList = forkJoinTaskList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getForkJoinPoolList() {
		return forkJoinPoolList;
	}

	public void setForkJoinPoolList(Map<String, ArrayList<JavaOtherElementModel>> forkJoinPoolList) {
		this.forkJoinPoolList = forkJoinPoolList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getRecursiveTaskList() {
		return recursiveTaskList;
	}

	public void setRecursiveTaskList(Map<String, ArrayList<JavaOtherElementModel>> recursiveTaskList) {
		this.recursiveTaskList = recursiveTaskList;
	}
}
