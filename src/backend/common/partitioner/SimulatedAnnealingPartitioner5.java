package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.estimators.Estimator;
import backend.common.*;
import backend.common.partitioner.mutator.CorrectionMutation;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.WeightMutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import streamit2.Compiler;
import streamit2.NoEstimationDataException;

public class SimulatedAnnealingPartitioner5 {
    private final static Logger logger = Logger.getLogger("compiler");
    private static final Random rand = new Random(42);
    static WeightMutation sm_outer;
    static WeightMutation sm_inner;
    static SortedMap<StreamNode, Processor> bestPartition;
    public static SortedMap<StreamNode, Processor> currentPartition;
    static SortedMap<StreamNode, Processor> previousPartition;
    static Map<String, Integer> bestPartitionPlan;
    public static Map<String, Integer> currentPartitionPlan;
    static Map<String, Integer> previousPartitionPlan;
    static Map<String, Integer> innerPartitionPlan;
    static List<Evaluator> evaluators;
    static double initialThroughput;
    static double bestThroughput;
    static double currentThroughput;
    static double previousThroughput;
    static double innerThroughput;
    static Map<StreamNode, Integer[]> bestFissionRatio;
    public static Map<StreamNode, Integer[]> currentFissionRatio;
    static Map<StreamNode, Integer[]> previousFissionRatio;
    static int bestMult;
    static int prviousMult;
    
    private static SortedMap<StreamNode, Processor> generateInitialPartition(List<FilterInstance> filters, Chip chip, Estimator estimator, SortedMap<FilterInstance, Processor> partition){
        boolean clusterOnly = false;
        SortedMap<FilterInstance, Processor> initialPartition = partition;
        while(initialPartition == null){
            logger.log(Level.INFO, "No start up partition specified, generating initial paritition with greedy partitioner...");            
            initialPartition = new GreedyPartitioner().createPartition(filters, chip, estimator, clusterOnly).getFilterMapping();
            try {
                initialPartition = validateInitialPartition(initialPartition, chip, filters, clusterOnly);
            } catch (NoEstimationDataException e) {
                e.unknownPartition = initialPartition;
                throw e;
            }
            clusterOnly =true;
        }
        try {
            Mutation.hillClimbMult(evaluators, filters, Mutation.convertPartition(initialPartition));
        } catch (NoEstimationDataException e) {
                e.unknownPartition = initialPartition;
                throw e;
            }
        initialThroughput = Mutation.getThroughput(evaluators);
        return Mutation.convertPartition(initialPartition);
    }
    
    private static void initalParameters(){
        bestPartition = new TreeMap<>();
        currentPartition = new TreeMap<>();
        previousPartition = new TreeMap<>();
        bestPartitionPlan = new LinkedHashMap<>();
        currentPartitionPlan = new LinkedHashMap<>();
        previousPartitionPlan = new LinkedHashMap<>();
        innerPartitionPlan = new LinkedHashMap<>();
        initialThroughput = Double.MAX_VALUE;
        bestThroughput = Double.MAX_VALUE;
        currentThroughput = Double.MAX_VALUE;
        previousThroughput = Double.MAX_VALUE;
        innerThroughput = Double.MAX_VALUE;
        bestFissionRatio = new LinkedHashMap<>();
        currentFissionRatio = new LinkedHashMap<>();
        previousFissionRatio = new LinkedHashMap<>();
        bestMult = 1;
        prviousMult = 1;
    }
    
    //check if the initial partition if valid
    private static SortedMap<FilterInstance, Processor> validateInitialPartition(SortedMap<FilterInstance, Processor> initialPartition, Chip chip, List<FilterInstance> filters, boolean clusterOnly){
        if(Mutation.validate(filters, evaluators, Mutation.convertPartition(initialPartition)))
            return initialPartition;
        logger.log(Level.INFO, "Found invalid initial partition. attempting to fix initial partition...");
        SortedMap<StreamNode, Processor> fixedPartition = Mutation.convertPartition(initialPartition);
        CorrectionMutation cm = new CorrectionMutation(filters, fixedPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators);
        if(!cm.correct()){
            if(clusterOnly){
                logger.log(Level.SEVERE, "Could not find a valid initial partition!");
                System.exit(-1);
            }else{
                logger.log(Level.INFO, "Moving all filters to single cluster...");
                initialPartition = null;
                return null;
            }
        }
        return Mutation.flattenPartition(fixedPartition);
    }
    
    
    
    private static void innerSA(List<FilterInstance> filters, Chip chip){
        savePreviousState();
        currentThroughput = Mutation.getThroughput(evaluators);
        double temp_inner = currentThroughput;
        double coolingRate = 0.03;
        sm_inner = new WeightMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators, temp_inner);
        //evaluator.estimateChannels();
        while (temp_inner > 1){
            boolean noChange = !sm_inner.innerMutate(temp_inner);
            //if(noChange)
            //    break;
                
            double newThroughput = Mutation.getThroughput(evaluators);
            
            if(evaluators.get(evaluators.size() - 1).getBottleneckFilters().size() < 2){
                innerThroughput = newThroughput;
                break;
            }
            
            if (acceptanceProbability(currentThroughput, newThroughput, temp_inner) > rand.nextDouble())
                currentThroughput = newThroughput;
            else
                sm_inner.restoreState();
            
            if (currentThroughput > innerThroughput) {
                saveInnerBestState();
            }
            
            temp_inner *= 1 - coolingRate;
        }
        currentThroughput = previousThroughput;
        //evaluator.estimateChannels();
        //if(previousThroughput > innerThroughput)
         //   innerPartitionPlan = new LinkedHashMap<>(previousPartitionPlan);
        //else
         //   currentPartitionPlan = new LinkedHashMap<>(innerPartitionPlan);
        
        //sm_inner.partition(currentFissionRatio, currentPartitionPlan, currentPartition, StreamNode.rootMult);
    }
    
    private static void outterSA(List<FilterInstance> filters, Chip chip){
        double coolingRate = 0.03;
        long startTime = System.currentTimeMillis();
        long timeLimit = startTime + Compiler.SATimeout * 1000;
        while(Compiler.resa-- > 0){
            double temp_outer = bestThroughput;
            sm_outer = new WeightMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators, temp_outer);
            StreamNode.setRootMult((int) Math.ceil((double) bestMult / 2));
            int max_count = 20;
            int counter = max_count;
            while (temp_outer > 1 && (Compiler.SATimeout < 0 || System.currentTimeMillis() < timeLimit)) {
                counter--;
                
                if(counter < 0){
                    logger.log(Level.INFO, "Return to best solution");
                    cleanupUnnecessaryFission(filters, chip);
                    counter = max_count;
                }
                boolean noChange = !sm_outer.outerMutate(temp_outer);
                
                //if(noChange)
                  //  break;

                innerSA(filters, chip);
                double newThroughput = Mutation.getThroughput(evaluators);
                
                if (acceptanceProbability(currentThroughput, newThroughput, temp_outer) > rand.nextDouble())
                    currentThroughput = newThroughput;
                else
                    sm_outer.restoreState();

                if (currentThroughput > bestThroughput) {
                    saveBestState();
                    counter = max_count;
                    logger.log(Level.INFO, "Best throughput found: " + bestThroughput+" (mult is "+StreamNode.getRootMult()+")");
                }else{
                    logger.log(Level.INFO, ".");
                }

                temp_outer *= 1 - coolingRate;
            }
            //debug
            logger.log(Level.INFO, "Debug: End Temp = " + temp_outer);
            cleanupUnnecessaryFission(filters, chip);
        }
    }
    
    private static void cleanupUnnecessaryFission(List<FilterInstance> filters, Chip chip){
        logger.log(Level.INFO, "Cleaning up unnecessary fission...");
        sm_outer.partition(bestFissionRatio, bestPartitionPlan, bestPartition, bestMult);
        currentPartition = new TreeMap<>(bestPartition);
        currentFissionRatio = new LinkedHashMap<>(bestFissionRatio);
        currentPartitionPlan = new LinkedHashMap<>(bestPartitionPlan);
        sm_outer = new WeightMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators, bestThroughput);
        
        Object[] fissedNodes = bestFissionRatio.keySet().toArray();
        Mutation.validate(filters, evaluators, currentPartition);
        for(Object node: fissedNodes) {
            while(sm_outer.SimplifyFission((StreamNode)node)){
                innerSA(filters, chip);
                Mutation.validate(filters, evaluators, currentPartition);
                double newThroughput = Mutation.getThroughput(evaluators);
                if (newThroughput >= bestThroughput) {
                    currentThroughput = newThroughput;
                    saveBestState();
                    logger.log(Level.INFO, "Fission simplified: " + ((StreamNode)node).StreamNodeName());
                }else{
                    sm_outer.restoreState();
                    break;
                }
            }
        }
        bestPartition = new TreeMap<>(currentPartition);
        bestFissionRatio = new LinkedHashMap<>(currentFissionRatio);
        bestPartitionPlan = new LinkedHashMap<>(currentPartitionPlan);
    }
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator estimator, List<Evaluator> es, SortedMap<FilterInstance, Processor> initialPartition) {
        evaluators = es;
        initalParameters();
        
        //generate initial partition without fission
        currentPartition = generateInitialPartition(filters, chip, estimator, initialPartition);
        
        bestPartition = new TreeMap<>(currentPartition);
        bestThroughput = initialThroughput;
        currentThroughput = initialThroughput;
        sm_outer = new WeightMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators, 1);
        saveBestState();
        StreamNode.setRootMult((int) Math.ceil((double) bestMult / 2));
        logger.log(Level.INFO, "rootMult start is"+StreamNode.getRootMult());
        logger.log(Level.INFO, "Simmulated Annealing...");
        logger.log(Level.INFO, "Initial throughput (from Greedy Partitioner): " + bestThroughput);
        try {
            outterSA(filters, chip);
        } catch (NoEstimationDataException e) {
            e.unknownPartition = Mutation.flattenPartition(currentPartition);
            throw e;
        }
        
        sm_outer.finalPartition(bestFissionRatio, bestPartitionPlan, bestPartition, bestMult);
        bestThroughput = Mutation.getThroughput(evaluators);
        //debug
        Evaluator eva = evaluators.get(evaluators.size() - 1);
        logger.log(Level.INFO, "{0}", eva.getProcessorsRank());
        logger.log(Level.INFO, "Simulated Annealing completed.");
        printBestRatio();
        logger.log(Level.INFO, "Best global mult found: {0}", new Object[]{StreamNode.getGlobalMult()});
        logger.log(Level.INFO, "Best throughput found: {0}", new Object[]{bestThroughput});
        logger.log(Level.INFO, "Predicted speedup (over initial partition): {0}", new Object[]{(bestThroughput / initialThroughput) + "x"});
        
        return Mutation.flattenPartition(bestPartition);
    }
    
    private static void printBestRatio(){
        logger.log(Level.INFO, "Best fission ratio: ");
        if(bestFissionRatio == null)
            logger.log(Level.INFO, "null");
        else{
            String fission_ratio_string = "";
            for (StreamNode f : bestFissionRatio.keySet()) {
                fission_ratio_string += "\n\t\t" + f.StreamNodeName() + " is ";
                for (int i = 0; i < bestFissionRatio.get(f).length; i++) {
                    fission_ratio_string += bestFissionRatio.get(f)[i];
                    if (i != bestFissionRatio.get(f).length - 1) {
                        fission_ratio_string += ":";
                    }
                }
            }
            logger.log(Level.INFO, "Best fission ratio: {0}", new Object[]{fission_ratio_string});
            PartitionLoader.setFissionPlan(bestFissionRatio);
        }
    }
    
    private static boolean statefulOnlyFilters(List<FilterInstance> filters){
        for(FilterInstance filter: filters)
            if(filter.isFissable())
                return false;
        return true;
    }
    
    private static void savePreviousState(){
        previousPartitionPlan = new LinkedHashMap<>(currentPartitionPlan);
        previousThroughput = currentThroughput;
        saveInnerBestState();
    }
    
    private static void saveInnerBestState(){
        innerPartitionPlan = new LinkedHashMap<>(currentPartitionPlan);
        innerThroughput = currentThroughput;
    }
    
    private static void saveBestState(){
        bestPartitionPlan = new LinkedHashMap<>(currentPartitionPlan);
        bestFissionRatio = new LinkedHashMap<>(currentFissionRatio);
        bestThroughput = currentThroughput;
        bestMult = StreamNode.getRootMult();
    }

    public static double acceptanceProbability(double engery, double newEngery, double temperature) {
        if (newEngery > engery) {
            return 1.0;
        }
        double test = Math.exp((newEngery - engery) / temperature);
        return test;
    }
}
