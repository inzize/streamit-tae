package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.estimators.Estimator;
import backend.common.*;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.RandomMutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import streamit2.Compiler;

public class SimulatedAnnealingPartitioner2 {
    private final static Logger logger = Logger.getLogger("compiler");
    
    private static final Random rand = new Random(42);
    static RandomMutation rm;
    static SortedMap<StreamNode, Processor> bestPartition = new TreeMap<>();
    static SortedMap<StreamNode, Processor> currentPartition = new TreeMap<>();
    static Map<String, Integer> bestPartitionPlan = new LinkedHashMap<>();
    static Map<String, Integer> currentPartitionPlan = new LinkedHashMap<>();
    static ThroughputEvaluator evaluator;
    static double initialThroughput = Double.MAX_VALUE;
    static double bestThroughput = Double.MAX_VALUE;
    static double currentThroughput = Double.MAX_VALUE;
    static Map<StreamNode, Integer[]> bestFissionRatio = new LinkedHashMap<>();
    static Map<StreamNode, Integer[]> currentFissionRatio = new LinkedHashMap<>();
    static int bestMult = 1;
    
    private static SortedMap<StreamNode, Processor> generateInitialPartition(List<FilterInstance> filters, Chip chip, Estimator estimator){
        SortedMap<FilterInstance, Processor> initialPartition = new GreedyPartitioner().createPartition(filters, chip, estimator, false).getFilterMapping();
        initialThroughput = evaluator.evaluate(initialPartition);
        logger.log(Level.INFO, "Throughput for the initial partition (from Greedy Partitioner): " + initialThroughput);
        return Mutation.convertPartition(initialPartition);
    }
    
    private static void initalParameters(Chip chip, Estimator estimator){
        evaluator = new ThroughputEvaluator();
    }
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator estimator) {
        double temp_outer = 1000;
        double coolingRate = 0.03;
        
        initalParameters(chip, estimator);
        
        //generate initial partition without fission
        currentPartition = generateInitialPartition(filters, chip, estimator);
        List<Evaluator> evaluators = new ArrayList<>();
        evaluators.add(evaluator);
        rm = new RandomMutation(filters, currentPartition, chip, currentFissionRatio, currentPartitionPlan, evaluators);
        bestThroughput = initialThroughput;
        currentThroughput = initialThroughput;
        
        long startTime = System.currentTimeMillis();
        long timeLimit = startTime + Compiler.SATimeout * 1000;
        saveBestState();

        while (temp_outer > 1 && (Compiler.SATimeout < 0 || System.currentTimeMillis() < timeLimit)) {
            rm.mutate();
            double newThroughput = evaluator.evaluate(currentPartition);
            logger.log(Level.INFO, temp_outer + " ");
            logger.log(Level.INFO, "{0}", newThroughput);
            if (acceptanceProbability(bestThroughput, newThroughput, temp_outer) > rand.nextDouble())
                currentThroughput = newThroughput;
            else
                rm.restoreState();

            if (currentThroughput > bestThroughput) {
                saveBestState();
            }

            temp_outer *= 1 - coolingRate;
        }
        
        rm.partition(bestFissionRatio, bestPartitionPlan, bestPartition, bestMult);

        bestThroughput = evaluator.evaluate(bestPartition);
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

        logger.log(Level.INFO, "Best rootMult found: " + bestMult);
        logger.log(Level.INFO, "Best global mult found: " + StreamNode.getGlobalMult());
        logger.log(Level.INFO, "Predicted speedup: " + bestThroughput / initialThroughput + "x");
        
        return Mutation.flattenPartition(bestPartition);
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