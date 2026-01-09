package reviewerrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

public class SKRankDataGeneraator {

    static List<String> studiedRecModels = Arrays.asList("KUREC","CF","RF","CHREV","ER", "CORRECT", "SOFIA", "CN");

    String skAnalysisPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/SK-RANK/";

    String resultPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";

    List<String> evaluationMetricList = Arrays.asList("Accuracy","Recall","MAP");

    List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
    
    Map<String,String> modelFileName = new HashMap<String,String>();

    public void initModelFileName(){
        modelFileName.put("CF", "CF");
        modelFileName.put("RF", "RF");
        modelFileName.put("ER", "ER");
        modelFileName.put("CHREV", "CHREV");
        modelFileName.put("KUREC", "KUREC");
        modelFileName.put("CN", "CN");
        modelFileName.put("CORRECT", "CORRECT");
        modelFileName.put("SOFIA", "SOFIA");
        


        modelFileName.put("AD_FREQ", "ADFREQ-RAND");
        modelFileName.put("AD_REC", "ADREC-RAND");
        modelFileName.put("AD_HYBRID", "ADHYBRID-RAND");

        //modelFileName.put("COMGEOM_KUREC_CHREV", "res_combined_KUREC_CHREV_geom_mean");
        //modelFileName.put("COMSUM_KUREC_CHREV", "res_combined_KUREC_CHREV_sum_values");

        //modelFileName.put("COMGEOM_KUREC_RF", "res_combined_KUREC_RF_geom_mean");
        //modelFileName.put("COMSUM_KUREC_RF", "res_combined_KUREC_RF_sum_values");

        //modelFileName.put("COMGEOM_KUREC_CF", "res_combined_KUREC_CF_geom_mean");
        //modelFileName.put("COMSUM_KUREC_CF", "res_combined_KUREC_CF_sum_values");

        //modelFileName.put("COMGEOM_KUREC_ER", "res_combined_KUREC_ER_geom_mean");
        //modelFileName.put("COMSUM_KUREC_ER", "res_combined_KUREC_ER_sum_values");
        
        
        

        

        //modelFileName.put("COMGEOM_ALL", "res_combined_all_geom_mean");
        //modelFileName.put("COMSUM_ALL", "res_combined_all_sum_values");
        //modelFileName.put("COMGEOM_RF_ER_CHREV_KUREC", "res_combined_all_exc_CF_geom_mean");
        //modelFileName.put("COMSUM_RF_ER_CHREV_KUREC", "res_combined_all_exc_CF_sum_values");
    }

    public Map<String, Map<String, Map<String, Map <Integer,Double>>>> readResultInformation(){
        //Model, Evaluation Metrics, Project, Rank, Value
        Map<String, Map<String, Map<String, Map <Integer,Double>>>> result = new HashMap<String, Map<String, Map<String, Map<Integer,Double>>>>();
        for(String modelName : modelFileName.keySet()){
            String path = resultPath + modelFileName.get(modelName) + ".csv";
            System.out.println(path);
            try{
                CsvReader reader = new CsvReader(path);
                reader.readHeaders();
                while(reader.readRecord()){
                    String projectName = reader.get("Project_Name");
                    String evalMetric = reader.get("Eval_Metric");
                    
                    if(!result.containsKey(modelName)){
                        result.put(modelName, new HashMap<String, Map<String,Map<Integer,Double>>>());
                    }
                    if(!result.get(modelName).containsKey(evalMetric)){
                        result.get(modelName).put(evalMetric, new HashMap<String, Map<Integer,Double>>());
                    }
                    if(!result.get(modelName).get(evalMetric).containsKey(projectName)){
                        result.get(modelName).get(evalMetric).put(projectName, new HashMap<Integer, Double>());
                    }
                    for(int i = 1 ; i <= 5 ; i ++){
                        double rankValue = Double.parseDouble(reader.get("Rank-" + i));
                        result.get(modelName).get(evalMetric).get(projectName).put(i, rankValue);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Finish Result Data Loading");
        return result;
    }

    public void printModelResult(String modelName,
    Map<String, Map<String, Map<String, Map <Integer,Double>>>> resultData){
        if(!resultData.containsKey(modelName)){
            System.out.println("Model is missing...");
            return;
        }
        for(String evalMetric : resultData.get(modelName).keySet()){
            for(String projectName : resultData.get(modelName).get(evalMetric).keySet()){
                System.out.print(modelName + " " + evalMetric + " " + projectName + " ");
                for(int i = 1; i <= 5 ; i ++){
                    System.out.print(resultData.get(modelName).get(evalMetric).get(projectName).get(i) + " ");
                }
                System.out.println();
            }
        }
    }

    public void generateModelResultSKDataSet(Map<String, Map<String, Map<String, Map<Integer, Double>>>> resultData) {
        
        for (String evalMetric : evaluationMetricList) {
            try {
                BufferedWriter bfWriter = new BufferedWriter(new FileWriter(skAnalysisPath  + evalMetric + "_sk_data.csv"));
                for(String modelName : modelFileName.keySet()){
                    String writeString = "";
                    writeString += modelName + " ";
                    for(String projectName : projectList){
                        for(int i = 1 ; i <= 5 ; i ++){
                            Double value = resultData.get(modelName).get(evalMetric).get(projectName).get(i);
                            writeString  += value + " ";
                        }
                    }
                    writeString = writeString.trim();
                    bfWriter.write(writeString);
                    bfWriter.write("\n");
                }
                bfWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void generateModelResultSKDataSetStartDifferent(Map<String, Map<String, Map<String, Map<Integer, Double>>>> resultData,
    String startModelName) {
    
        for (String evalMetric : evaluationMetricList) {
            try {
                BufferedWriter bfWriter = new BufferedWriter(new FileWriter(skAnalysisPath + "Recom_Data_" + evalMetric + "_" + startModelName + ".csv"));
                for(String modelName : modelFileName.keySet()){
                    String writeString = "";
                    writeString += modelName + " ";
                    for(String projectName : projectList){
                        for(int i = 1 ; i <= 5 ; i ++){
                            Double value = resultData.get(modelName).get(evalMetric).get(projectName).get(i);
                            writeString  += value + " ";
                        }
                    }
                    writeString = writeString.trim();
                    bfWriter.write(writeString);
                    bfWriter.write("\n");
                }
                bfWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void modelAnalysisWithDifferentStart(){
        
        for(String modelName : studiedRecModels){
            System.out.println("Working for the models start with: " + modelName);
            SKRankDataGeneraator ob = new SKRankDataGeneraator();
            ob.initModelFileName();
            ob.modelFileName.put("Adaptive_Model_Freq_RF_Start", "adaptiveModelRecencyResult_last_10_start_"+modelName);
            Map<String, Map<String, Map<String,  Map <Integer,Double>>>> resultData = ob.readResultInformation();
            ob.printModelResult("KUREC", resultData);
            ob.generateModelResultSKDataSetStartDifferent(resultData, modelName);
        }

    }

    public static void startAnalysis(){
        SKRankDataGeneraator ob = new SKRankDataGeneraator();
        ob.initModelFileName();
        Map<String, Map<String, Map<String, Map <Integer,Double>>>> resultData = ob.readResultInformation();
        //ob.printModelResult("KUREC", resultData);
        ob.generateModelResultSKDataSet(resultData);
    }

    public static void main(String[] args) {
        startAnalysis();
        //modelAnalysisWithDifferentStart();
        
        System.out.println("Program finishes successfuly");
    }
    
}
