package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.estimators.Estimator;
import static ast.GeneratesCode.writer;
import ast.Program;
import backend.common.Chip;
import backend.common.PartitionLoader;
import backend.common.Processor;
import backend.common.SourceFileWriter;
import backend.common.partitioner.mutator.ManualMutation;
import backend.common.partitioner.mutator.Mutation;
import backend.coreVA.CoreVABackend;
import backend.coreVA.CoreVACodeGenerator;
import backend.coreVA.xmlModel.MpsocChip;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import partitionerGUI.GUIDrawer;
import static streamit2.Compiler.xmlPath;

public class GUIManualPartitioner {
    private final static Logger logger = Logger.getLogger("compiler");     
    public static ThroughputEvaluator evaluator;
    public static SortedMap<StreamNode,Processor> partition;
    public static Chip chip;
    public static Estimator estimator;
    public static Map<StreamNode, Integer[]> fissionRatio;
    public static Map<String, Integer> partitionPlan;
    public static List<FilterInstance> filters;
    public static ManualMutation mu;
    public static List<Evaluator> evaluators;
    private static String filename;
    private static Program program;
    private static String make_file_content = "ROOT_PATH=/home/coreva/coreva_dev/qut-stream-ubi-mpsoc\n" +
                                    "run: benchmark\n" +
                                    "include /home/coreva/coreva_dev/software/makebase_streamit.mk";
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> f, Chip c, Estimator e, List<Evaluator> es, SortedMap<FilterInstance,Processor> p, String fn, Program pro)
    {
        evaluators = es;
        evaluator = (ThroughputEvaluator)evaluators.get(evaluators.size() - 1);
        filters = f;
        filename = fn;
        program = pro;
        SortedMap<FilterInstance,Processor> flattenedPartition;
        if(partition == null)
            flattenedPartition = new GreedyPartitioner().createPartition(filters, c, e, false).getFilterMapping();
        else
            flattenedPartition = p;
        
        partition = Mutation.convertPartition(flattenedPartition);
        partitionPlan = new LinkedHashMap<>();
        fissionRatio = new LinkedHashMap<>();
        chip = c;
        estimator = e;
        mu = new ManualMutation(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
        Mutation.validate(filters, evaluators, partition);
        GUIDrawer.initialFrame((MpsocChip)chip);
        PartitionLoader.setFissionPlan(fissionRatio);
        return Mutation.flattenPartition(partition);
    }
    
    public static boolean savePartition(File location){
        PartitionLoader.setFissionPlan(fissionRatio);
        return PartitionLoader.storePartition(location.getAbsolutePath(), chip, Mutation.flattenPartition(partition));
    }
    
    public static boolean openPartition(File location){
        if(!PartitionLoader.retrivePartition(location.getAbsolutePath()))
            return false;
        if(!PartitionLoader.checkParFile(chip))
            return false;
        fissionRatio = PartitionLoader.getFissionRatio();
        partitionPlan = PartitionLoader.getPartitionPlan();
        mu.partition(fissionRatio, partitionPlan, partition, PartitionLoader.getMult());
        Mutation.validate(filters, evaluators, partition);
        drawGraph();
        return true;
    }
    
    public static double getThroughput(){
        Mutation.validate(filters, evaluators, partition);
        return evaluator.evaluate(partition);
    }
    
    public static void drawGraph(){
        GUIDrawer.setChip(chip);
        GUIDrawer.setPartition(Mutation.flattenPartition(partition));
        GUIDrawer.draw();
    }
    
    public static String generateCode(){
        CoreVABackend.partitioning = Mutation.flattenPartition(partition);
        String mfname = null;
        try{
            CoreVABackend.Compile(filename, program, FilterInstance.globalGraph.InOrder(),xmlPath);
            mfname = CoreVACodeGenerator.project_dir + File.separator + "Makefile";
            FileWriter file_writer = new FileWriter(mfname);
            writer = new SourceFileWriter(file_writer);
            writer.print(make_file_content);
            writer.close();
        }catch(Exception e){
            return mfname;
        }
        
        return CoreVACodeGenerator.project_dir.toString();
    }
    
    public static String run(String mfname){
        String toReturn = null;
        Runtime rt = Runtime.getRuntime();
        try{
            String[] cmdArray = new String[3];
            cmdArray[0] = "/bin/bash";
            cmdArray[1] = "-c";
            cmdArray[2] = "make";
            Process proc = rt.exec(cmdArray, null, new File(mfname));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null){
                if(s.startsWith("throughput estimated")){
                    toReturn = s;
                    break;
                }
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Unknown error");
        }
        return toReturn;
    }
    
    public static boolean alterPartition(FilterInstance filter, Processor target_processor){
        boolean toReturn = mu.alterPartition(filter, target_processor);
        drawGraph();
        return toReturn;
    }
    
    public static boolean alterRootMult(int newMult){
        boolean toReturn = mu.alterRootMult(newMult);
        drawGraph();
        return toReturn;
    }
    
    public static double getFilterWork(FilterInstance filter){
        return evaluator.evaluateFilter(null, Mutation.flattenPartition(partition), filter);
    }
    
    public static boolean fissFilter(FilterInstance filter, int copies){
        boolean toReturn = mu.alterFission(filter, copies);
        drawGraph();
        return toReturn;
    }
    
    public static void restore(){
        mu.restoreState();
        drawGraph();
    }
}
