package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.estimators.Estimator;
import backend.common.*;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.SmartMutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import streamit2.Compiler;

public class SimulatedAnnealingPartitioner4 {
    private final static Logger logger = Logger.getLogger("compiler");
    
    private static final Random rand = new Random(42);
    static SmartMutation sm_outer;
    static SmartMutation sm_inner;
    static SortedMap<StreamNode, Processor> bestPartition;
    public static SortedMap<StreamNode, Processor> currentPartition;
    static SortedMap<StreamNode, Processor> previousPartition;
    static Map<String, Integer> bestPartitionPlan;
    public static Map<String, Integer> currentPartitionPlan;
    static Map<String, Integer> previousPartitionPlan;
    static Map<String, Integer> innerPartitionPlan;
    static ThroughputEvaluator evaluator;
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
    static List<Evaluator> evaluators = new ArrayList<>();
    
    private static SortedMap<StreamNode, Processor> generateInitialPartition(List<FilterInstance> filters, Chip chip, Estimator estimator){
        logger.log(Level.INFO, "Generating initial paritition...");
        SortedMap<FilterInstance, Processor> initialPartition = new GreedyPartitioner().createPartition(filters, chip, estimator, false).getFilterMapping();
        Mutation.hillClimbMult(evaluators, filters, Mutation.convertPartition(initialPartition));
        initialThroughput = evaluator.evaluate(initialPartition);
        return Mutation.convertPartition(initialPartition);
    }
    
    private static void initalParameters(Chip chip, Estimator estimator){
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
        
        evaluator = new ThroughputEvaluator();
        evaluators.add(evaluator);
    }
    
    private static void innerSA(List<FilterInstance> filters, Chip chip){
        double temp_inner = 1000;
        double coolingRate = 0.1;
        sm_inner = new SmartMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators);
        savePreviousState();
        while (temp_inner > 1){
            boolean noChange = true;
            while(noChange)
                noChange = !sm_inner.mutatePartition();
                
            double newThroughput = evaluator.evaluate(currentPartition);
            if (acceptanceProbability(innerThroughput, newThroughput, temp_inner) > rand.nextDouble())
                currentThroughput = newThroughput;
            else
                sm_inner.restoreState();
            
            if (currentThroughput > innerThroughput) {
                saveInnerBestState();
            }
            
            temp_inner *= 1 - coolingRate;
        }
        if(previousThroughput >= innerThroughput)
            innerPartitionPlan = new LinkedHashMap<>(previousPartitionPlan);
        else
            currentPartitionPlan = new LinkedHashMap<>(innerPartitionPlan);
        
        sm_inner.partition(currentFissionRatio, currentPartitionPlan, currentPartition, StreamNode.getRootMult());
    }
    
    private static void outterSA(List<FilterInstance> filters, Chip chip){
        double temp_outer = 100;
        double coolingRate = 0.03;
        int counter = 20;
        long startTime = System.currentTimeMillis();
        long timeLimit = startTime + Compiler.SATimeout * 1000;
        while (temp_outer > 1 && (Compiler.SATimeout < 0 || System.currentTimeMillis() < timeLimit) && counter > 0) {
            counter--;
            boolean noChange = true;
            while(noChange){
                //if(rand.nextDouble() <0.2)
                   // noChange = !sm_outer.mutateRootMult();
                //else
                    noChange = !sm_outer.mutateFission();
            }

            innerSA(filters, chip);
            double newThroughput = evaluator.evaluate(currentPartition);

            if (acceptanceProbability(bestThroughput, newThroughput, temp_outer) > rand.nextDouble())
                currentThroughput = newThroughput;
            else
                sm_outer.restoreState();

            if (currentThroughput > bestThroughput) {
                saveBestState();
                counter = 20;
                logger.log(Level.INFO, "Best throunput found: " + bestThroughput);
            }else{
                logger.log(Level.INFO, ".");
            }

            temp_outer *= 1 - coolingRate;
        }
        //debug
        logger.log(Level.INFO, "Debug: End Temp = " + temp_outer);
    }
    
    private static void cleanupUnnecessaryFission(List<FilterInstance> filters, Chip chip){
        logger.log(Level.INFO, "Cleaning up unnecessary fission...");
        sm_outer.partition(bestFissionRatio, bestPartitionPlan, bestPartition, bestMult);
        currentPartition = new TreeMap<>(bestPartition);
        currentFissionRatio = new LinkedHashMap<>(bestFissionRatio);
        currentPartitionPlan = new LinkedHashMap<>(bestPartitionPlan);
        sm_outer = new SmartMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators);
        
        Object[] fissedNodes = bestFissionRatio.keySet().toArray();
        
        for(Object node: fissedNodes) {
            while(sm_outer.SimplifyFission((StreamNode)node)){
                innerSA(filters, chip);
                double newThroughput = evaluator.evaluate(currentPartition);
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
    }
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator estimator) {
        initalParameters(chip, estimator);
        
        //generate initial partition without fission
        currentPartition = generateInitialPartition(filters, chip, estimator);
        
        sm_outer = new SmartMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators);
        bestThroughput = initialThroughput;
        currentThroughput = initialThroughput;
       
        saveBestState();
        StreamNode.setRootMult((int) Math.ceil((double) bestMult / 2));

        logger.log(Level.INFO, "Simmulated Annealing...");
        logger.log(Level.INFO, "Initial throughput (from Greedy Partitioner): " + bestThroughput);
        if(!statefulOnlyFilters(filters)){
            outterSA(filters, chip);
            cleanupUnnecessaryFission(filters, chip);
        }
        
        sm_outer.finalPartition(bestFissionRatio, bestPartitionPlan, bestPartition, bestMult);
        bestThroughput = evaluator.evaluate(bestPartition);
        printBestRatio();
        logger.log(Level.INFO, "Best global mult found: " + StreamNode.getGlobalMult());
        logger.log(Level.INFO, "Best throughput found: " + bestThroughput);
        logger.log(Level.INFO, "Predicted speedup (over initial partition): " + bestThroughput / initialThroughput + "x");
        
        return Mutation.flattenPartition(bestPartition);
    }
    
    private static void printBestRatio(){
        logger.log(Level.INFO, "Best fission ratio: ");
        if(bestFissionRatio == null)
            logger.log(Level.INFO, "null");
        else{
            for (StreamNode f : bestFissionRatio.keySet()) {
                logger.log(Level.INFO, f.StreamNodeName() + " is ");
                for (int i = 0; i < bestFissionRatio.get(f).length; i++) {
                    logger.log(Level.INFO, bestFissionRatio.get(f)[i].toString());
                    if (i != bestFissionRatio.get(f).length - 1) {
                        logger.log(Level.INFO, ":");
                    }
                }
                logger.log(Level.INFO, "  ");
            }
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
        return Math.exp((newEngery - engery) / temperature);
    }
}
