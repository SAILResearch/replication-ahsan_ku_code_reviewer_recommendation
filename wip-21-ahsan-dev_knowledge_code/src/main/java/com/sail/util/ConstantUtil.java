package com.sail.util;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

public class ConstantUtil {

	public static String scratchDataLocBackup = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/Result";


	public static String RECOMMENDATION_RESULT_TRAINING_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Training/";
	public static String RECOMMENDATION_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";

	//public static String RECOMMENDATION_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_score_evaluation_updated_July_26/";
	//public static String RECOMMENDATION_PER_TEST_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score/";
	
	//public static String RECOMMENDATION_PER_TEST_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/";
	//public static String RECOMMENDATION_PER_TRAIN_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_training/";

	public static String RECOMMENDATION_PER_TEST_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
	public static String RECOMMENDATION_PER_TRAIN_RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Training/";


	public static String COMPARATOR_FULL_NAME = "java.util.Comparator";
	public static String xinProjectPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/java_xin_project.csv";
	public static String XIN_COMMIT_HISTORY = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/";
	
	public static List<String> majorTopicList = Arrays.asList(
			"data_type",
			"operator_decision",
			"array",
			"loop",
			"method_encapsulation",	
			"inheritance",
			"exception_handling",
			//"calender_api",
			"advanced_class_design",
			"generic_collection",
			"functional_interface",
			"stream_api",
			"date_time_api",
			"java_io",
			"java_nio",
			"concurrency",
			"data_base",
			"localization",
			"string_processing",
			
			"persistence_object", //22
			"enterprise_java_bean",
			"message_service",
			"soap_service",
			"servlet_web",
			"rest_api",
			"websocket_api",
			"jfs_web_application",
			"cdi_bean",
			"enterprise_concurrency",
			"batch_processing");
	
	List<Double> knowledgeUnitWeights = Arrays.asList(
		0.028, //"data_type",
		0.028, //"operator_decision",
		0.028, //"array",
		0.028, //"loop",
		0.028, //"method_encapsulation",	
		0.028, //"inheritance",
		0.028, //"exception_handling",
		0.028, //"advanced_class_design",
		0.028, //"generic_collection",
		0.028, //"functional_interface",
		0.028, //"stream_api",
		0.028, //"date_time_api",
		0.056, //"java_io",
		0.056, //"java_nio",
		0.056, //"concurrency",
		0.056, //"data_base",
		0.028, //"localization",
		0.028, //"string_processing",
		
		0.028, //"persistence_object",
		0.028, //"enterprise_java_bean",
		0.028, //"message_service",
		0.056, //"soap_service",
		0.056, //"servlet_web",
		0.056, //"rest_api",
		0.028, //"websocket_api",
		0.028, //"jfs_web_application",
		0.028, //"cdi_bean",
		0.028, //"enterprise_concurrency",
		0.028); //"batch_processing);

	
	public static List<String> listImplementedClassList = Arrays.asList("java.util.AbstractList",
			"java.util.AbstractSequentialList", "java.util.ArrayList", "java.util.AttributeList",
			"java.util.CopyOnWriteArrayList", "java.util.LinkedList", "java.util.RoleList",
			"java.util.RoleUnresolvedList", "java.util.Stack", "java.util.Vector");

	public static List<String> setImplementedClassList = Arrays.asList("java.util.AbstractSet",
			"java.util.ConcurrentHashMap.KeySetView", "java.util.ConcurrentSkipListSet",
			"java.util.CopyOnWriteArraySet", "java.util.HashSet", "java.util.JobStateReasons",
			"java.util.LinkedHashSet", "java.util.TreeSet");

	public static List<String> mapIMplementedClassList = Arrays.asList("java.util.AbstractMap", "java.util.Attributes",
			"java.util.AuthProvider", "java.util.ConcurrentHashMap", "java.util.ConcurrentSkipListMap",
			"java.util.EnumMap", "java.util.HashMap", "java.util.Hashtable", "java.util.IdentityHashMap",
			"java.util.LinkedHashMap", "java.util.PrinterStateReasons", "java.util.Properties", "java.util.Provider",
			"java.util.RenderingHints", "java.util.SimpleBindings", "java.util.TabularDataSupport");

	public static List<String> dequeImplementedClassList = Arrays.asList("java.util.ArrayDeque",
			"java.util.ConcurrentLinkedDeque", "java.util.LinkedBlockingDeque", "java.util.LinkedList");

	public static List<String> primitiveVersionFunctionalInterfaces = Arrays.asList("java.util.function.IntPredicate",
			"java.util.function.LongPredicate", "java.util.function.DoublePredicate", "java.util.function.IntConsumer",
			"java.util.function.LongConsumer", "java.util.function.DoubleConsumer", "java.util.function.IntFunction",
			"java.util.function.IntToDoubleFunction", "java.util.function.IntToLongFunction",
			"java.util.function.LongFunction", "java.util.function.LongToDoubleFunction",
			"java.util.function.LongToIntFunction", "java.util.function.DoubleFunction",
			"java.util.function.DoubleToIntFunction", "java.util.function.DoubleToLongFunction",
			"java.util.function.ToIntFunction", "java.util.function.ToDoubleFunction",
			"java.util.function.ToLongFunction", "java.util.function.BooleanSupplier", "java.util.function.IntSupplier",
			"java.util.function.LongSupplier", "java.util.function.DoubleSupplier",
			"java.util.function.IntUnaryOperator", "java.util.function.LongUnaryOperator",
			"java.util.function.DoubleUnaryOperator", "java.util.function.ObjIntConsumer",
			"java.util.function.ObjLongConsumer", "java.util.function.ObjDoubleConsumer",
			"java.util.function.ToIntBiFunction", "java.util.function.ToLongBiFunction",
			"java.util.function.ToDoubleBiFunction", "java.util.function.IntBinaryOperator",
			"java.util.function.LongBinaryOperator", "java.util.function.DoubleBinaryOperator");

	// findFirst, findAny, anyMatch, allMatch, noneMatch
	public static List<String> searchStreamList = Arrays.asList("java.util.stream.Stream.findFirst",
			"java.util.stream.Stream.findAny", "java.util.stream.Stream.anyMatch", "java.util.stream.Stream.allMatch",
			"java.util.stream.Stream.noneMatch");

	public static List<String> unaryOperatorFunctionalInterface = Arrays.asList("java.util.function.UnaryOperator");

	public static List<String> binaryVersionFunctionalInterfaces = Arrays.asList("java.util.function.BiConsumer",
			"java.util.function.BiFunction");

	public static String LIST_INTERFACE_FULL_NAME = "java.util.List";
	public static String MAP_INTERFACE_FULL_NAME = "java.util.Map";
	public static String SET_INTERFACE_FULL_NAME = "java.util.Set";
	public static String DEQUE_INTERFACE_FULL_NAME = "java.util.Deque";

	public static String CLASS_INSTANCE_CREATION = "Class_instance_creation";

	public static String CLASS_DECLARATION = "Class_declaration";
	public static String INTERFACE_DECLARATION = "Interface_declaration";

	public static String FUNCTIONAL_INTERFACE_PACKAGE = "java.util.function";

	public static String PRIMITIVE_FUNCTIONAL_INTERFACE = "primitive_functional_interface";
	public static String BINARY_FUNCTIONAL_INTERFACE = "binary_functional_interface";
	public static String UNARY_FUNCTIONAL_INTERFACE = "unary_functional_interface";

	public static String CUSTOM_FUNCTIONAL_INTERFACE = "custom_functionl_interface";

	public static String FUNCTION_FUNCTIONAL_INTERFACE = "function_functional_interface";
	public static String SUPPLIER_FUNCTIONAL_INTERFACE = "supplier_functional_interface";
	public static String CONSUMER_FUNCTIONAL_INTERFACE = "consumer_functional_interface";
	public static String PREDICATE_FUNCTIONAL_INTERFACE = "predicate_functional_interface";
	public static String OPTIONAL_PACKAGE_CLASS = "java.util.Optional";

	public static String PACKAGE_SPLITTER = ".";

	public static String JAVA_OLD_PACKAGE_USER_INTERACTION = "java.io.InputStream.System.in";

	public static String JAVA_NEW_CLASS_USER_INTERACTION = "java.io.Console";
	public static String JAVA_NEW_METHOD_USER_INTERACTION = "java.lang.String.readLine";

	public static List<String> byteStreamAPIList = Arrays.asList("java.io.FileInputStream", "java.io.FileOutputStream",
			"java.io.ObjectInputStream", "java.io.ObjectOutputStream");

	public static List<String> charStreamAPIList = Arrays.asList("java.io.FileReader", "java.io.FileWriter",
			"java.io.BufferedReader", "java.io.BufferedWriter");

	public static List<String> binaryStreamAPIList = Arrays.asList("java.io.PrintStream", "java.io.PrintWriter");

	public static String JAVA_NIO_PATH = "java.nio.file.Paths";
	public static String JAVA_NIO_FILE = "java.nio.file.Files";

	///Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/Root
	public static String ROOT = "/scratch/ahsan/Java_Exam_Work";
	//public static String ROOT = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/Root";

	public static String OUTPUT_FILE_LOCATION = ROOT + "/Result/Result_June_30/";
	public static String OUTPUT_DATA_ANALYSIS = ROOT + "/Result/Result_June_30/DataAnalysisResult/";

	public static String JAVA_8_PACKAGE_FILE = ROOT + "/Data/java8_pacakge_list.csv";
	
	///scratch/ahsan/Java_Exam_Work/Data/missing_files.csv
	//public static String STUDIED_PROJECT_FILE = ROOT + "/Data/Studied_Project_List_200.csv";
	
	public static String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/data/studied_project_dev_knowledge.csv";
	//public static String STUDIED_PROJECT_FILE = ROOT + "/Data/Studied_Project_List_Final_Temp.csv";
	//public static String STUDIED_PROJECT_COMMITS_FILE = ROOT + "/Data/proj_branch_commit_meta.csv";
	//public static String STUDIED_PROJECT_COMMITS_FILE = ROOT + "/Data/studied_project_dev_knowledge_feasibility.csv";
	public static String STUDIED_PROJECT_COMMITS_FILE = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/data/studied_project_dev_knowledge.csv";

	//public static String STUDIED_PROJECT_FILE = ROOT + "/Data/missing_files.csv";
	public static String STUDIED_PROJECT_DIR = ROOT + "/GitRepositoryTemp/GitReposistories/";

	public static String USAGE_JAVA_PACKAGE_FILE = ROOT
			+ "/Result/Result_June_30/usage_pacakge/java_package_usage_list.csv";
	
	//public static String COMMIT_HISTORY_DIR = ROOT + "/Data/commit_merge_data/";
	//public static String COMMIT_HISTORY_RESULT_LOC = ROOT + "/Result/Result_Nov_18_2021/Commit_History_Result";
	
	public static String COMMIT_HISTORY_DIR = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/commit_merge_data/";
	public static String COMMIT_HISTORY_RESULT_LOC = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/merged_knowledge_units_project_history/";
	//public static String COMMIT_HISTORY_RESULT_LOC = "/home/local/SAIL/ahsan/merged_knowledge_units_project_history/";
	

	public static List<String> CONCURRENT_EXECUTOR_SERVICE_LIST = Arrays.asList("java.util.concurrent.Executor",
			"java.util.concurrent.Executors", "java.util.concurrent.ExecutorService",
			"java.util.concurrent.ScheduledThreadPoolExecutor", "java.util.concurrent.ThreadPoolExecutor");

	public static String CONCURRENT_SCHEDULE_EXECUTOR_SERVICE = "java.util.concurrent.ScheduledExecutorService";
	public static String CONCURRENT_CALLABLE = "java.util.concurrent.Callable";

	public static List<String> CONCURRENT_SYNCHRONIZE_LIST = Arrays.asList(
			"java.util.Collections.synchronizedCollection", "java.util.Collections.synchronizedList",
			"java.util.Collections.synchronizedMap", "java.util.Collections.synchronizedNavigableMap",
			"java.util.Collections.synchronizedNavigableSet", "java.util.Collections.synchronizedSet",
			"java.util.Collections.synchronizedSortedMap", "java.util.Collections.synchronizedSortedSet");

	public static List<String> CONCURRENT_COLLECTION_LIST = Arrays.asList("java.util.concurrent.ConcurrentHashMap",
			"java.util.concurrent.ConcurrentLinkedDeque", "java.util.concurrent.ConcurrentLinkedQueue",
			"java.util.concurrent.ConcurrentSkipListMap", "java.util.concurrent.ConcurrentSkipListSet",
			"java.util.concurrent.CopyOnWriteArrayList", "java.util.concurrent.CopyOnWriteArraySet",
			"java.util.concurrent.LinkedBlockingDeque", "java.util.concurrent.LinkedBlockingQueue",
			"java.util.concurrent.BlockingDeque", "java.util.concurrent.BlockingQueue");

	public static String CONCURRENT_FORK_JOIN_TASK = "java.util.concurrent.ForkJoinTask";
	public static String CONCURRENT_FORK_JOIN_POOL = "java.util.concurrent.ForkJoinPool";
	public static String CONCURRENT_RECURSIVE_TASK = "java.util.concurrent.RecursiveTask";

	public static String CONSTRUCTOR = "Constructor";

	public static String JAVA_LOCALE = "java.util.Locale";
	public static String JAVA_RESOURCE_BUILDER = "java.util.ResourceBundle";
	public static String JAVA_DATE_TIME_FORMATTER = "java.time.format.DateTimeFormatter";
	public static List<String> DATE_TIME_API = Arrays.asList("java.time.LocalDate","java.time.LocalTime",
			"java.time.LocalDateTime");

	public static String DRIVER_MANAGER 	= "java.sql.DriverManager";
	public static String SQL_CONNECTION 	= "java.sql.Connection";
	public static String SQL_RESULTSET 		= "java.sql.ResultSet";
	public static String SQL_STATEMENT 		= "java.sql.Statement";
	
	public static String PROPERTY_API 		= "java.util.Properties";
	
	public static String JAVA_TIME_INSTANT 	= "java.time.Instant";
	public static String JAVA_TIME_PERIOD 	= "java.time.Period";
	public static String JAVA_TIME_DURATION = "java.time.Duration";
	
	public static String JAVA_PATTERN_CLASS = "java.util.regex.Pattern";
	public static String JAVA_MATCHER_CLASS = "java.util.regex.Matcher";
	
	public static String STRING_CLASS = "java.lang.String";
	public static String DECIMAL_FORMATTER = "java.text.DecimalFormat";
	
	public static DateTime analysisStartDate = DateUtil.formatterWithHyphen.parseDateTime("2015-01-01");
	
	public static List<String> topicListJavaProfessionalExam = Arrays.asList(
			"Section-10-Item1-a-enum_decl", "Section-10-Item2-a-nested_class",
			"Section-10-Item2-b-local_class", "Section-10-item2-c-anonymous_inner_class",
			"Section-10-item3-a-overriden_method_with_annotation", 
			
			"Section-11-item1-a-generic_class",
			"Section-11-item2-a-instance_array_set_map_deque", "Section-11-item3-a-comparator_anonymous_class",
			"Section-11-item3-b-comparable_interface", 
			
			"Section-12-item1-a-built_in_interface",
			"Section-12-item2-a-primitive_functional_interface", "Section-12-item3-a-binary_functional_interface",
			"Section-12-item4-a-unary_functional_interface", 
			
			"Section-13-item1-a-stream_peak",
			"Section-13-item1-b-stream_map", "Section-13-item2-a-stream_search",
			"Section-13-item3-a-java_optional_class", "Section-13-item4-a-stream_collection",
			"Section-13-item5-a-stream_flat_map", "Section-13-item6-a-collector_class",
			"Section-13-item7-a-lambda_expression", "Section-13-item8-a-stream_foreach",
			
			// "Section14-item1-a" , "Section14-item2-a" , "Section14-item3-a" ,
			// "Section14-item4-a" ,
			"Section-15-item1-a-local_date_time_api" , "Section-15-item2-a-time_format" , 
			"Section-15-item3-a-time_instant","Section-15-item3-b-time_period",
			"Section-15-item3-c-time_duration",
			
			"Section-16-item1-a-byte_file_stream", "Section-16-item1-b-char_file_stream",
			"Section-16-item1-c-binary_file_stream", "Section-16-item1-d-other_stream_api",
			"Section-16-item2-a-console_read_old_approach", "Section-16-item2-a-console_read_new_approach",
			"Section-16-item3-a-usage_of_serialization", 
			
			"Section-17-item1-a-path_nio", "Section-17-item2-a-file_nio",
			"Section-17-item3-a-attribute_modify_files",
			
			"Section-18-item1-a-runnable",
			"Section-18-item1-b-callable",
			"Section-18-item1-c-thread_cration",
			"Section-18-item1-a-executors",
			"Section-18-item3-a-concurrent_collection",
			"Section-18-item4-a-fork_join_task",
			"Section-18-item4-b-fork_join_pool",
			"Section-18-item4-c-recursive_task",
			"Section-18-item5-a-parallel_stream",
			"Section-18-item6-a-schedule_executor",
			"Section-18-item7-a-synchronized_method_api",
			
			"Section-19-item1-a-sql_driver_manager",
			"Section-19-item1-b-sql_connection",
			"Section-19-item2-a-sql_statement",
			"Section-19-item3-a-sql_result_set",
			
			"Section-20-item1-a-usage_locale",
			"Section-20-item2-a-usage_properties",
			"Section-20-item3-a-usage_resource_buldne",
			
			
			"Section-21-item1-a-string_api_usage",
			"Section-21-item2-a-string_pattern",
			"Section-21-item2-a-string_matcher",
			"Section-21-item2-a-string_formatter",
			"Section-21-item2-a-decimal_formatter"
			
	// , "Section17-item4-a"
	);

	public static List<String> topicListJavaAssociateExam = Arrays.asList(
			"Section-2-Item1-a-ArrayType"
			,"Section-2-Item1-a-ParameterizedType"
			,"Section-2-Item1-a-PrimitiveType"
			,"Section-2-Item1-a-WildcardType"
			,"Section-3-Item1-a-ArithmeticOperator"
			,"Section-3-Item1-a-RelationalOperator"
			,"Section-3-Item1-a-BitwiseOperator"
			,"Section-3-Item1-a-LogicalOperator"
			, "Section-3-Item1-a-TernaryOperator"
			, "Section-3-Item1-a-AssignmentOperator	"
			, "Section-3-Item1-a-PrefixOperator	"
			, "Section-3-Item1-a-PostfixOperator"
			, "Section-3-Item3-a-if_else_condition"
			, "Section-3-Item3-b-ternary_condition"
			, "Section-3-Item4-a-switch_condition"
			, "Section-4-Item1-a-one_dim_array"
			, "Section-4-Item2-a-two_dim_array"
			, "Section-5-Item1-a-while_loop"
			, "Section-5-Item2-a-for_loop"
			, "Section-5-Item3-a-do_while_loop"
			, "Section-5-Item5-a-continue_statement"
			, "Section-5-Item5-b-break_statement"
			, "Section-6-Item1-a-method_with_arguments"
			, "Section-6-Item2-a-static_class_field"
			, "Section-6-Item2-b-static_block"
			, "Section-6-Item3-a-overloaded_method_constructor"
			, "Section-6-Item3-b-constructor_chaining"
			, "Section-6-Item3-d-variable_arguments"
			, "Section-6-Item4-a-access_modifiers"
			, "Section-6-Item5-a-set_get_method"
			, "Section-6-Item5-b-immutable_class"
			, "Section-6-Item6-a-object_type_parameter_update"
			, "Section-7-Item2-a-basic_polymorphism"
			, "Section-7-Item2-b-polymorphic_parameters"
			, "	Section-7-Item3-a-casting_super_sub_class"
			, "Section-7-Item4-a-super_method_variable"
			, "Section-7-Item5-a-abstract_class"
			, "Section-7-Item5-b-interface"
			, "Section-8-Item2-a-try_catch_block"
			, "Section-8-Item2-b-super_wider_exception"
			, "Section-8-Item4-a-method_with_throws"
			, "	Section-8-Item5-a-multiple_catch"
			, "Section-8-Item5-b-different_exception"
			, "Section-9-Item1-a-manipulate_string_builder"
			, "Section-9-Item2-a-calendar_data_usage"
		);
	
	public static List<String> topicList = Arrays.asList(

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
			"Section-21-item2-a-decimal_formatter",

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

            "Section-32-item1-a-ee_java_batch");



	
	public static List<String> topicCategoryList = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "10", "11", "12", "13", "15",
			"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29","30", "31", "32");

			public static String REVIEW_BASELINE = "Baseline-Review-Experience";
			public static String REVIEW_KNOWLEDGE_UNIT = "KnowledgeUnit-Review-Expertise";
			public static String REVIEW_REVIEW_EXP = "Basline-Review-Experience";
			public static String REVIEW_COMMIT_EXP = "Basline-Commit-Experience";
			public static String REVIEW_CHREV = "Basline-CHREV";
			public static String REVIEW_RAND = "Basline-Review-Random";
			public static String REVIEW_EXPLORER = "Baseline-Review-Explorer";
			public static String REVIEW_COMMENT_NETWORK = "Baseline-Comment-Network";
			


	public static List<String> studiedProject = Arrays.asList(
			"apache_lucene","apache_wicket","apache_activemq",
		"jruby_jruby","caskdata_cdap","apache_hbase",
		"apache_hive","apache_storm","apache_stratos","apache_groovy");
		

		}
