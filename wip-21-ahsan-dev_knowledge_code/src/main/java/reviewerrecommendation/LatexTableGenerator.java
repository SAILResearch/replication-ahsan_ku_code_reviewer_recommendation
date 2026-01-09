package reviewerrecommendation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvWriter;
import com.sail.util.RecommendationUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class LatexTableGenerator {

    String BASELINE_SYSTEM = "Baseline";
    String KU_SYSTEM = "KU";
    String COMBINED_SYSTRM = "Combined";

    List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
    
    Map<String,String> projectNameList = new HashMap<String,String>();
    Map<String,String> modelFileName = new HashMap<String,String>();
    Map<String,String> modelTypeName = new HashMap<String,String>();


    public String RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
    //public String RESULT_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/evaluation_result/";
    
    List<String> evaluationMetricList = Arrays.asList("Accuracy","MAP");

    List<String> modelList = Arrays.asList("CF","CHREV","CN", "CORRECT","ER", "RF", "SOFIA","KUREC","AD_FREQ","AD_REC","AD_HYBRID");

    public void initModelFileName(){
        modelFileName.put("CF", "CF"); modelTypeName.put("CF", BASELINE_SYSTEM);
        modelFileName.put("RF", "RF"); modelTypeName.put("RF", BASELINE_SYSTEM);
        modelFileName.put("ER", "ER"); modelTypeName.put("ER", BASELINE_SYSTEM);
        modelFileName.put("CHREV", "CHREV"); modelTypeName.put("CHREV", BASELINE_SYSTEM);
        modelFileName.put("KUREC", "KUREC"); modelTypeName.put("KUREC", KU_SYSTEM);
        modelFileName.put("CN", "CN"); modelTypeName.put("CN", BASELINE_SYSTEM);
        modelFileName.put("SOFIA", "SOFIA"); modelTypeName.put("SOFIA", BASELINE_SYSTEM);
        modelFileName.put("CORRECT", "CORRECT"); modelTypeName.put("CORRECT", BASELINE_SYSTEM);
        modelFileName.put("AD_FREQ","ADFREQ-RAND"); modelTypeName.put("AD_FREQ", COMBINED_SYSTRM);
        modelFileName.put("AD_REC","ADREC-RAND"); modelTypeName.put("AD_REC", COMBINED_SYSTRM);
        modelFileName.put("AD_HYBRID", "ADHYBRID-RAND"); modelTypeName.put("AD_HYBRID", COMBINED_SYSTRM);
    
    
        projectNameList.put("apache_activemq", "Apache ActiveMQ");
        projectNameList.put("apache_groovy", "Apache Groovy");
        projectNameList.put("apache_lucene", "Apache Lucene");
        projectNameList.put("apache_hbase", "Apache Hbase");
        projectNameList.put("apache_hive", "Apache Hive");
        projectNameList.put("apache_storm", "Apache Storm");
        projectNameList.put("apache_wicket", "Apache Wicket");
        projectNameList.put("elastic_elasticsearch", "Elastic Search");

    }

    public void writeLatexTableGeneratorCSV(Map<String, Map<String, Map<String, Map <Integer,Double>>>> result,
    String evaluationMetricName){

        String path = String.format("%sTables/table_%s.csv",RESULT_PATH, evaluationMetricName);
        CsvWriter writer = new CsvWriter(path);
        try{
            writer.write("Project");
            writer.write("Type of Recommender");
            writer.write("Recommender Name");
            for(int k = 1 ; k <= 5 ; k ++){
                writer.write("k=" + k);
            }
            writer.write("Min Top-K Accuracy");
            writer.endRecord();

            Map<String,Map<Integer,DescriptiveStatistics>> statModelValues = new HashMap<String, Map<Integer, DescriptiveStatistics>>();
            Map<String,DescriptiveStatistics> statMean = new HashMap<String, DescriptiveStatistics>();
            
            for(String project : projectList){
                for(String model : modelList){
                    //System.out.println(project + " " + model + " " + evaluationMetricName);
                    Map<Integer,Double> rankResult = result.get(model).get(evaluationMetricName).get(project);
                    writer.write(projectNameList.get(project));
                    writer.write(modelTypeName.get(model));
                    writer.write(model);
                    DescriptiveStatistics stat = new DescriptiveStatistics();
                    
                    if(!statModelValues.containsKey(model)){
                        statModelValues.put(model, new HashMap<Integer, DescriptiveStatistics>());
                    }
                    for(int k = 1 ; k <= 5 ; k ++){
                        double value = rankResult.get(k);
                        writer.write(String.format("%.2f", value));
                        stat.addValue(value);
                        if(!statModelValues.get(model).containsKey(k)){
                            statModelValues.get(model).put(k, new DescriptiveStatistics());
                        }
                        statModelValues.get(model).get(k).addValue(value);
                    }
                    writer.write(String.format("%.2f", stat.getMin()));
                    writer.endRecord();
                    if(!statMean.containsKey(model)){
                        statMean.put(model, new DescriptiveStatistics());
                    }
                    statMean.get(model).addValue(stat.getMin());
                }
            }
            for(String model : modelList){
                writer.write("Median");
                writer.write(modelTypeName.get(model));
                writer.write(model);
                DescriptiveStatistics stat = new DescriptiveStatistics();
                for(int k = 1 ; k <= 5 ; k ++){
                    double value = statModelValues.get(model).get(k).getPercentile(50);
                    writer.write(String.format("%.2f", value));
                    stat.addValue(value);
                }
                writer.write(String.format("%.2f", statMean.get(model).getPercentile(50)));
                writer.endRecord();
            }

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printValues(Map<String, Map<String, Map<String, Map <Integer,Double>>>> result){
        System.out.println("Print value:");
        for(String model : result.keySet()){
            for(String evalMetric : result.get(model).keySet()){
                for(String projectName : result.get(model).get(evalMetric).keySet()){
                    System.out.println(model + " " + evalMetric + " " + projectName);
                }
            }
        }
    }

    public void generateLatexTable(){
        initModelFileName();
        //Model, Evaluation Metrics, Project, Rank, Value
        Map<String, Map<String, Map<String, Map <Integer,Double>>>> result = RecommendationUtil.readResultInformation(modelFileName, RESULT_PATH);
        //printValues(result);
        writeLatexTableGeneratorCSV(result,evaluationMetricList.get(0));
        writeLatexTableGeneratorCSV(result,evaluationMetricList.get(1));
    }

    public static void main(String[] args) {
        LatexTableGenerator ob = new LatexTableGenerator();
        ob.generateLatexTable();
        System.out.println("Program finishes successfully");
    }
}
