package com.sail.java.exam.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;
import com.sail.util.TextUtil;

public class JavaKnowledgdUnitWritingResultUtil {
    public  List<String> topicListJavaProfessionalExam = Arrays.asList("Section-10-Item1-a-enum_decl",
            "Section-10-Item2-a-nested_class", "Section-10-Item2-b-local_class",
            "Section-10-item2-c-anonymous_inner_class", "Section-10-item3-a-overriden_method_with_annotation",

            "Section-11-item1-a-generic_class", "Section-11-item2-a-instance_array_set_map_deque",
            "Section-11-item3-a-comparator_anonymous_class", "Section-11-item3-b-comparable_interface",

            "Section-12-item1-a-built_in_interface", "Section-12-item2-a-primitive_functional_interface",
            "Section-12-item3-a-binary_functional_interface", "Section-12-item4-a-unary_functional_interface",

            "Section-13-item1-a-stream_peak", "Section-13-item1-b-stream_map", "Section-13-item2-a-stream_search",
            "Section-13-item3-a-java_optional_class", "Section-13-item4-a-stream_collection",
            "Section-13-item5-a-stream_flat_map", "Section-13-item6-a-collector_class",
            "Section-13-item7-a-lambda_expression", "Section-13-item8-a-stream_foreach",

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
            "Section-21-item2-a-decimal_formatter"

    );

    public static List<String> topicListJavaAssociateExam = Arrays.asList("Section-2-Item1-a-ArrayType",
            "Section-2-Item1-a-ParameterizedType", "Section-2-Item1-a-PrimitiveType", "Section-2-Item1-a-WildcardType",
            "Section-3-Item1-a-ArithmeticOperator", "Section-3-Item1-a-RelationalOperator",
            "Section-3-Item1-a-BitwiseOperator", "Section-3-Item1-a-LogicalOperator",
            "Section-3-Item1-a-TernaryOperator", "Section-3-Item1-a-AssignmentOperator	",
            "Section-3-Item1-a-PrefixOperator	", "Section-3-Item1-a-PostfixOperator",
            "Section-3-Item3-a-if_else_condition", "Section-3-Item3-b-ternary_condition",
            "Section-3-Item4-a-switch_condition", "Section-4-Item1-a-one_dim_array", "Section-4-Item2-a-two_dim_array",
            "Section-5-Item1-a-while_loop", "Section-5-Item2-a-for_loop", "Section-5-Item3-a-do_while_loop",
            "Section-5-Item5-a-continue_statement", "Section-5-Item5-b-break_statement",
            "Section-6-Item1-a-method_with_arguments", "Section-6-Item2-a-static_class_field",
            "Section-6-Item2-b-static_block", "Section-6-Item3-a-overloaded_method_constructor",
            "Section-6-Item3-b-constructor_chaining", "Section-6-Item3-d-variable_arguments",
            "Section-6-Item4-a-access_modifiers", "Section-6-Item5-a-set_get_method",
            "Section-6-Item5-b-immutable_class", "Section-6-Item6-a-object_type_parameter_update",
            "Section-7-Item2-a-basic_polymorphism", "Section-7-Item2-b-polymorphic_parameters",
            "	Section-7-Item3-a-casting_super_sub_class", "Section-7-Item4-a-super_method_variable",
            "Section-7-Item5-a-abstract_class", "Section-7-Item5-b-interface", "Section-8-Item2-a-try_catch_block",
            "Section-8-Item2-b-super_wider_exception", "Section-8-Item4-a-method_with_throws",
            "	Section-8-Item5-a-multiple_catch", "Section-8-Item5-b-different_exception",
            "Section-9-Item1-a-manipulate_string_builder", "Section-9-Item2-a-calendar_data_usage");

    public static List<String> topicListJavaEEExam = Arrays.asList(
           
            "Section-22-item1-b-jpa_entity_orm_mapping", 
            "Section-22-item2-a-database_operation",
            "Section-22-item2-b-transaction_operation", 
            "Section-22-item3-a-jpql_statement",
            "Section-22-item4-a-jpql_other_api",

            "Section-23-item1-a-ejb_lifecycle", 
            "Section-23-item1-b-ejb_session", 
            "Section-23-item2-a-ejb_transaction",
            "Section-23-item2-b-ejb_timers", 
            "Section-23-item3-a-ejb_other_apis",

            "Section-24-item1-a-consumer_message", 
            "Section-24-item1-b-producer_message",
            "Section-24-item2-a-messenger_message", 
            "Section-24-item3-a-other_message",

            "Section-25-item1-a-create_web_service", 
            "Section-25-item2-a-mar_unmarshall_ob",

            "Section-26-item1-a-create_servlet", 
            "Section-26-item2-a-servlet_header",
            "Section-26-item2-b-servlet_cookie", 
            "Section-26-item3-a-servlet_lifecycle",
            "Section-26-item3-a-servlet_other_apis",

            "Section-27-item1-a-rest_service", 
            "Section-27-item2-a-rest_client",

            "Section-28-item1-a-websocket_creation", 
            "Section-28-item2-a-websocket_encoder",
            "Section-28-item2-b-websocket_decoder",

            "Section-29-item1-a-jsf_lifecycle", 
            "Section-29-item2-a-jsf_model", 
            "Section-29-item2-b-jsf_render",
            "Section-29-item2-c-jsf_validator", 
            "Section-29-item3-a-jsf_other_apis",

            "Section-30-item1-a-cdi_qualifier", 
            "Section-30-item2-a-cdi_producer", 
            "Section-30-item3-a-cdi_dispose",
            "Section-30-item4-a-cdi_stereotype", 
            "Section-30-item5-a-cdi_other_apis",

            "Section-31-item1-a-ee_concur_manage_exec", 
            "Section-31-item1-a-ee_concur_other_apis",

            "Section-32-item1-a-ee_java_batch"

    );

    public void writeHeaders(CsvWriter writer) {
        try {
            writer.write("Project_Name");
            writer.write("Commit_Id");
            writer.write("File_Path");
            writer.write("File_Name");
            writer.write("Line_Of_Code");
            for (int i = 0; i < topicListJavaAssociateExam.size(); i++) {
                writer.write(topicListJavaAssociateExam.get(i));
            }
            for (int i = 0; i < topicListJavaProfessionalExam.size(); i++) {
                writer.write(topicListJavaProfessionalExam.get(i));
            }
            for (int i = 0; i < topicListJavaEEExam.size(); i++) {
                writer.write(topicListJavaEEExam.get(i));
            }
            writer.write("Super_Class");
            writer.write("Invoked_Methods");
            writer.endRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeTopicExtractionInformationToCSVFile(String projectFullName, String commitId,
            CsvWriter writer, String javaFilePath, ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList,
            int lineOfCode)
            throws Exception {

        writer.write(projectFullName);
        writer.write(commitId);
        writer.write(javaFilePath);
        writer.write(javaFilePath.substring(javaFilePath.lastIndexOf("/") + 1));
        writer.write(Integer.toString(lineOfCode));
        Set<String> superClassList = new HashSet<String>();
        Set<String> invokedMethodList = new HashSet<String>();

        //System.out.println("Total Topic Module: " + analyzedTopicExtractorList.size());

        for (TopicExtractorBaseModel topic : analyzedTopicExtractorList) {

            if (topic instanceof DataTypeKnowledgeExtractorOCA) {
                // System.out.println("DataTypeKnowledgeExtractorOCA");
                DataTypeKnowledgeExtractorOCA ob = (DataTypeKnowledgeExtractorOCA) topic;
                for (int i = 0; i < JDTUtil.selectedTypeList.size(); i++) {
                    String varType = JDTUtil.selectedTypeList.get(i);
                    if (ob.getVariableDeclList().containsKey(varType)) {
                        writer.write(Integer.toString(ob.getVariableDeclList().get(varType).size()));
                    } else {
                        writer.write("0");
                    }
                }
            } else if (topic instanceof OperatorDecisionKnowledgeExtractorOCA) {
                // System.out.println("OperatorDecisionKnowledgeExtractorOCA");
                OperatorDecisionKnowledgeExtractorOCA ob = (OperatorDecisionKnowledgeExtractorOCA) topic;
                for (int i = 0; i < JDTUtil.operatorList.size(); i++) {
                    String operName = JDTUtil.operatorList.get(i);

                    if (ob.getOperatorList().containsKey(operName)) {
                        writer.write(Integer.toString(ob.getOperatorList().get(operName).size()));
                    } else {
                        writer.write("0");
                    }
                }
                writer.write(Integer.toString(ob.getIfElseStatement().size()));
                writer.write(Integer.toString(ob.getTernaryStatement().size()));
                writer.write(Integer.toString(ob.getSwitchStatement().size()));
            } else if (topic instanceof ArrayKnowledgeExtractorOCA) {
                // System.out.println("ArrayKnowledgeExtractorOCA");
                ArrayKnowledgeExtractorOCA ob = (ArrayKnowledgeExtractorOCA) topic;
                writer.write(Integer.toString(ob.getOneDimArrayList().size()));
                writer.write(Integer.toString(ob.getTwoDimArrayList().size()));
            } else if (topic instanceof LoopKnowledgeExtractorOCA) {
                // System.out.println("LoopKnowledgeExtractorOCA");
                LoopKnowledgeExtractorOCA ob = (LoopKnowledgeExtractorOCA) topic;
                writer.write(Integer.toString(ob.getWhileList().size()));
                writer.write(Integer.toString(ob.getForList().size()));
                writer.write(Integer.toString(ob.getDoWhileList().size()));
                writer.write(Integer.toString(ob.getContinueList().size()));
                writer.write(Integer.toString(ob.getBreakList().size()));
            } else if (topic instanceof MethodEncapsulationKnowledgeExtractorOCA) {
                //System.out.println("MethodEncapsulationKnowledgeExtractorOCA");
                MethodEncapsulationKnowledgeExtractorOCA ob = (MethodEncapsulationKnowledgeExtractorOCA) topic;
                ob.checkImmutableClass();
                ob.getInfoFromMethod();
                writer.write(Integer.toString(ob.getMethodList().size()));
                writer.write(Integer.toString(ob.getStaticVariableDeclaration().size()));
                writer.write(Integer.toString(ob.getStaticBlocks().size()));
                writer.write(Integer.toString(ob.getOverloadedMethod().size()));
                writer.write(Integer.toString(ob.getThisConstructorInvoke().size()));
                writer.write(Integer.toString(ob.getVarLengthArughmentMethodList().size()));
                
                
                //writer.write(ob.isDifferentAccessModifierIsUsedInMethod() ? "1" : "0");
                //Update for paper 1
                writer.write(Integer.toString(ob.accessModifierFullList.size()));
                writer.write(Integer.toString(ob.getClassPrivateVariableInitializeOtherMethods().size()));
                writer.write(Integer.toString(ob.getClassPrivateVariableInitiaalizeInConstructor().size()));
                writer.write(Integer.toString(ob.getObjectParameterModifiedInsideMethodList().size()));
            } else if (topic instanceof InheritanceKnowledgeExtractorOCA) {
                // System.out.println("InheritanceKnowledgeExtractorOCA");
                InheritanceKnowledgeExtractorOCA ob = (InheritanceKnowledgeExtractorOCA) topic;
                writer.write(Integer.toString(ob.getSuperClassReferSubClass().size()));
                writer.write(Integer.toString(ob.getParameterizedPolymorphism().size()));
                // writer.write("0"); // did not finish it
                writer.write(Integer.toString(ob.getSuperClassReferSubClassWithCasting().size()));
                writer.write(Integer.toString(ob.getSuperAccess().size()));
                
                writer.write(Integer.toString(ob.abstractClassCount.size()));
                writer.write(Integer.toString(ob.interfaceCount.size()));
                //writer.write(ob.isAbstractClass() ? "1" : "0");
                //writer.write(ob.isInterface() ? "1" : "0");
                invokedMethodList = ob.invokedMethodList;
                superClassList = ob.superClassList;
            } else if (topic instanceof HandlindExceptionKnowledgeExtractorOCA) {
                // System.out.println("HandlindExceptionKnowledgeExtractorOCA");
                HandlindExceptionKnowledgeExtractorOCA ob = (HandlindExceptionKnowledgeExtractorOCA) topic;
                writer.write(Integer.toString(ob.getTryCatchBlockList().size()));
                writer.write(Integer.toString(ob.getWiderExceptionType().size()));
                writer.write(Integer.toString(ob.getMethodThrowException().size()));
                writer.write(Integer.toString(ob.getMultipleMethodThrowException().size()));
                writer.write(Integer.toString(ob.getOtherExceptionCatch().size()));
            } else if (topic instanceof BuiltInJavaAPIKnowledgeExtractorOCA) {
                // System.out.println("BuiltInJavaAPIKnowledgeExtractorOCA");
                BuiltInJavaAPIKnowledgeExtractorOCA ob = (BuiltInJavaAPIKnowledgeExtractorOCA) topic;
                writer.write(Integer.toString(ob.getManipulatingStringAPI().size()));
                writer.write(Integer.toString(ob.getManipulatingDateAPI().size()));
            } else if (topic instanceof AdvancedClassDesignKnowledgeExtractorOCP) {
                // System.out.println("AdvancedClassDesignKnowledgeExtractorOCP");
                AdvancedClassDesignKnowledgeExtractorOCP ob = (AdvancedClassDesignKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getEnumDecList().size()));
                writer.write(Integer.toString(ob.getInnerClassList().size()));
                writer.write(Integer.toString(ob.getLocalClassList().size()));
                writer.write(Integer.toString(ob.getAnonymousClassList().size()));
                writer.write(Integer.toString(ob.getOverridedMethodList().size()));
            } else if (topic instanceof GenericKnowledgeExtractorOCP) {
                // System.out.println("GenericKnowledgeExtractorOCP");
                GenericKnowledgeExtractorOCP ob = (GenericKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getDeclaredGenericClassList().size()));
                writer.write(Integer.toString(ob.getCreationFourGenInterface().size()));
                writer.write(Integer.toString(ob.getCreationOfComparator().size()));
                writer.write(Integer.toString(ob.getCompareToMethodCall().size()));
            } else if (topic instanceof FunctionalInterfaceKnowledgeExtractorOCP) {
                // System.out.println("FunctionalInterfaceKnowledgeExtractorOCP");
                FunctionalInterfaceKnowledgeExtractorOCP ob = (FunctionalInterfaceKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getBuiltInInterface().size()));
                writer.write(Integer.toString(ob.getPrimitiveFunctionalInterface().size()));
                writer.write(Integer.toString(ob.getBinaryFunctionalInterface().size()));
                writer.write(Integer.toString(ob.getUnaryFunctionalInterface().size()));
            } else if (topic instanceof StreamAPIKnowledgeExtractorOCP) {
                // System.out.println("StreamAPIKnowledgeExtractorOCP");
                StreamAPIKnowledgeExtractorOCP ob = (StreamAPIKnowledgeExtractorOCP) topic;
                
                writer.write(Integer.toString(Math.max(ob.getPeekStreamList().size(), ob.getUsageOfOtherStreamAPI().size())));
                writer.write(Integer.toString(ob.getMapStreamList().size()));
                writer.write(Integer.toString(ob.getSearchStreamAPIUsage().size()));
                writer.write(Integer.toString(ob.getOptionalClassUsageList().size()));
                writer.write(Integer.toString(ob.getSortingCollectionList().size()));
                writer.write(Integer.toString(ob.getFlatMapStreamList().size()));
                writer.write(Integer.toString(ob.getUsageOfCollector().size()));
                writer.write(Integer.toString(ob.getFilterCollectionWithLambda().size()));
                writer.write(Integer.toString(ob.getUsageOfForEach().size()));
            } else if (topic instanceof JavaDateTimeAPIKnowledgeExtractorOCP) {
                JavaDateTimeAPIKnowledgeExtractorOCP ob = (JavaDateTimeAPIKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getJavaLocalDateList().size()));
                writer.write(Integer.toString(ob.getJavaDateTimeFormatterList().size()));
                writer.write(Integer.toString(ob.getJavaTimeInstantList().size()));
                writer.write(Integer.toString(ob.getJavaTimePeriodList().size()));
                writer.write(Integer.toString(ob.getJavaTimeDurationList().size()));
            } else if (topic instanceof JavaIOFundamentalKnowledgeExtractorOCP) {
                // System.out.println("JavaIOFundamentalKnowledgeExtractorOCP");
                JavaIOFundamentalKnowledgeExtractorOCP ob = (JavaIOFundamentalKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getByteStreamFileList().size()));
                writer.write(Integer.toString(ob.getCharStreamFileList().size()));
                writer.write(Integer.toString(ob.getBinaryStreamFileList().size()));
                writer.write(Integer.toString(ob.getOtherIOFileList().size()));
                writer.write(Integer.toString(ob.getOldWayUserInteractionProcess().size()));
                writer.write(Integer.toString(ob.getNewWayUserInteractionProcess().size()));
                writer.write(Integer.toString(ob.getUseSeialization().size()));
            } else if (topic instanceof JavaNIOKnowledgeExtractorOCP) {
                // System.out.println("JavaNIOKnowledgeExtractorOCP");
                JavaNIOKnowledgeExtractorOCP ob = (JavaNIOKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getMethodCallPathList().size()));
                writer.write(Integer.toString(ob.getMethodCallFileList().size()));
                writer.write(Integer.toString(ob.getFileBasicAttributeList().size()));
            } else if (topic instanceof ConcurrencyKnowledgeExtractorOCP) {
                ConcurrencyKnowledgeExtractorOCP ob = (ConcurrencyKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getImplementRunnable().size()));
                writer.write(Integer.toString(ob.getCallableInstanceCreationList().size()));
                writer.write(Integer.toString(ob.getThreadInstanceCreationList().size()));
                writer.write(Integer.toString(ob.getExecutorMethodCallList().size()));
                writer.write(Integer.toString(ob.getConcurrentCollectionList().size()));
                writer.write(Integer.toString(ob.getForkJoinTaskList().size()));
                writer.write(Integer.toString(ob.getForkJoinPoolList().size()));
                writer.write(Integer.toString(ob.getRecursiveTaskList().size()));
                writer.write(Integer.toString(ob.getParallelMethodList().size()));
                writer.write(Integer.toString(ob.getScheduleExecutorServiceList().size()));
                writer.write(Integer.toString(ob.getSynchronizeMethodCallList().size()));
            } else if (topic instanceof JDBCKnowledgeExtractorOCP) {
                JDBCKnowledgeExtractorOCP ob = (JDBCKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getDriverManagerList().size()));
                writer.write(Integer.toString(ob.getSqlConnectionList().size()));
                writer.write(Integer.toString(ob.getSqlStatementList().size()));
                writer.write(Integer.toString(ob.getSqlResultSetList().size()));
            } else if (topic instanceof LocalizationKnowledgeExctractorOCP) {
                LocalizationKnowledgeExctractorOCP ob = (LocalizationKnowledgeExctractorOCP) topic;
                writer.write(Integer.toString(ob.getJavaLocaleList().size()));
                writer.write(Integer.toString(ob.getJavaPropertyAccessList().size()));
                writer.write(Integer.toString(ob.getJavaResourceBuilderList().size()));
            } else if (topic instanceof JavaStringProcessingKnowledgeExtractorOCP) {
                JavaStringProcessingKnowledgeExtractorOCP ob = (JavaStringProcessingKnowledgeExtractorOCP) topic;
                writer.write(Integer.toString(ob.getJavaStringAPIUsage().size()));
                writer.write(Integer.toString(ob.getJavaStringPattern().size()));
                writer.write(Integer.toString(ob.getJavaStringMatcher().size()));
                writer.write(Integer.toString(ob.getJavaStringFormatter().size()));
                writer.write(Integer.toString(ob.getJavaDecimalFormatter().size()));
            } else if (topic instanceof JavaPersistanceKnowledge) {
                JavaPersistanceKnowledge ob = (JavaPersistanceKnowledge) topic;
                
                writer.write(Integer.toString(ob.getUsageOfPersistenceEntityORM().size()));
                writer.write(Integer.toString(ob.getUsageOfPersistenceTransaction().size()));
                writer.write(Integer.toString(ob.getUsageOfPersistenceDatabase().size()));
                writer.write(Integer.toString(ob.getUsageOfJQLStatement().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherJavaPersistence().size()));
            } else if (topic instanceof EnterpriseJavaBeanKnowledge) {
                EnterpriseJavaBeanKnowledge ob = (EnterpriseJavaBeanKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfJavaBeanLifeCycle().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaBeanSession().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaBeanTransaction().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaBeanTimerList().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherJavaBean().size()));
            } else if (topic instanceof MessageServiceKnowledge) {
                MessageServiceKnowledge ob = (MessageServiceKnowledge) topic;
                writer.write(Integer.toString(ob.getUsedProducerAPIList().size()));
                writer.write(Integer.toString(ob.getUsedConsumerAPIList().size()));
                writer.write(Integer.toString(ob.getUsedMessageAPIList().size()));
                writer.write(Integer.toString(ob.getOtherUsedJMSAPIList().size()));
            } else if (topic instanceof JavaWebserviceSOAPKnowledge) {
                JavaWebserviceSOAPKnowledge ob = (JavaWebserviceSOAPKnowledge) topic;
                writer.write(Integer.toString(ob.getCreationOfSOAPList().size()));
                writer.write(Integer.toString(ob.getUsageOfMarshallUnmarshallList().size()));
            } else if (topic instanceof WebApplicationWithServletKnowledge) {
                WebApplicationWithServletKnowledge ob = (WebApplicationWithServletKnowledge) topic;
                //System.out.println("Talking from the Servlet Knowledge Module");
                //ob.printInfo();
                writer.write(Integer.toString(ob.getUsageOfJavaServletHTTP().size()));
                writer.write(Integer.toString(ob.getUsageOfHeaderAPI().size()));
                writer.write(Integer.toString(ob.getUsageOfCookie().size()));
                writer.write(Integer.toString(ob.getUsageServletLifeCycle().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherServletAPI().size()));
            } else if (topic instanceof JavaRestFulWithJAXRXKnowledge) {
                JavaRestFulWithJAXRXKnowledge ob = (JavaRestFulWithJAXRXKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfJavaRXService().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaRXClient().size()));
            } else if (topic instanceof JavaWebSocketKnowledge) {
                JavaWebSocketKnowledge ob = (JavaWebSocketKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageJavaWebSocket().size()));
                writer.write(Integer.toString(ob.getUsageOfWebsocketMessageEncoder().size()));
                writer.write(Integer.toString(ob.getUsageOfWebsocketMessageDecoder().size()));
            } else if (topic instanceof JavaJFSKnowledge) {
                JavaJFSKnowledge ob = (JavaJFSKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfJavaFaceLifecycle().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaFaceModel().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaFaceRender().size()));
                writer.write(Integer.toString(ob.getUsageOfJavaFaceValidator().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherJavaFaceAPI().size()));
            } else if (topic instanceof JavaCDIBeanKnowledge) {
                JavaCDIBeanKnowledge ob = (JavaCDIBeanKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfCDIQualifier().size()));
                writer.write(Integer.toString(ob.getUsageOfCDIProduce().size()));
                writer.write(Integer.toString(ob.getUsageOfCDIDispose().size()));
                writer.write(Integer.toString(ob.getUsageOfCDISterotype().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherInjectAPI().size()));
            } else if (topic instanceof JavaEEConcurrentKnowledge) {
                JavaEEConcurrentKnowledge ob = (JavaEEConcurrentKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfJavaManageExecutor().size()));
                writer.write(Integer.toString(ob.getUsageOfOtherJavaEEConcurrent().size()));
            } else if (topic instanceof JavaEEBatchKnowledge) {
                JavaEEBatchKnowledge ob = (JavaEEBatchKnowledge) topic;
                writer.write(Integer.toString(ob.getUsageOfJavaBatchAPI().size()));
            }
        }

        writer.write(TextUtil.convertSetToString(superClassList, "-"));
        writer.write(TextUtil.convertSetToString(invokedMethodList, "-"));

        writer.endRecord();
    }

}
