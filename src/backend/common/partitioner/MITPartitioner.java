package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.estimators.Estimator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.RandomMutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.Pipeline;
import hierarchicalGraph.SplitJoin;
import hierarchicalGraph.StreamContainer;
import hierarchicalGraph.StreamNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import streamit2.Compiler;

public class MITPartitioner {
    private final static Logger logger = Logger.getLogger("compiler");    
    private static final Random rand = new Random(42);
    // simualted annealing constants
    public static int MINTEMPITERATIONS = 200;
    public static int MAXTEMPITERATIONS = 200;
    public static int ANNEALITERATIONS = 10000;
    static Estimator estimator;
    static Evaluator evaluator;
    static Map<StreamNode, Integer[]> fissionRatio = new LinkedHashMap<>();
    static SortedMap<StreamNode, Processor> init_partition;
    static SortedMap<StreamNode, Processor> partition;
    static SortedMap<StreamNode, Processor> best_partition;
    static double currentThroughput = Double.MAX_VALUE;
    static double bestThroughput = Double.MAX_VALUE;
    static double TFACTR = 0.9;
        
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator e, List<Evaluator> evaluators) {
        evaluator = evaluators.get(evaluators.size() - 1);
        estimator = e;
        RandomMutation rm = null;
        double work = 0;
        int cores = chip.getProcessors().length;
        while(work == 0){
            StreamNode.restoreGraph();
            StreamNode.FuseAllStatelessNodes();
            List<StreamNode> fissables = StreamNode.globalGraph.getFissables();
            StreamNode.globalGraph.SteadySchedule(null);
            StreamNode.globalGraph.InitSchedule(0);
            StreamNode.globalGraph.Flatten();
            smarterDuplicate(fissables, cores);
            Mutation.fissFilters(fissionRatio);
            int numProcessors = chip.getProcessors().length;
            partition = new TreeMap<>();
            fissables = StreamNode.globalGraph.getFissables();
            int x = 0;
            for(StreamNode fissable: fissables)
                partition.put(fissable, chip.getProcessors()[x++%numProcessors]);

            init_partition = new TreeMap<>(partition);
            rm = new RandomMutation(filters, partition, chip, fissionRatio, new LinkedHashMap<String, Integer>(), evaluators);
            if(Mutation.validate(filters, evaluators, init_partition))
                work = Mutation.getThroughput(evaluators);
            else
                work = 0;
            cores--;
        }     
        
        simAnnealAssign(filters, 5, 300, rm);
                
        if(fissionRatio == null)
            logger.log(Level.INFO, "");
        else
            for (StreamNode f : fissionRatio.keySet()) {
                logger.log(Level.INFO, f.StreamNodeName() + " is ");
                for (int i = 0; i < fissionRatio.get(f).length; i++) {
                    logger.log(Level.INFO, fissionRatio.get(f)[i].toString());
                    if (i != fissionRatio.get(f).length - 1) {
                        logger.log(Level.INFO, ":");
                    }
                }
                logger.log(Level.INFO, "  ");
            }

        
        return Mutation.flattenPartition(best_partition);
    }
    
    public static void simAnnealAssign(List<FilterInstance> filters, int iterations, int nover, RandomMutation rm) {
        logger.log(Level.INFO, "Simulated Annealing Assignment");
        best_partition = new TreeMap<>(partition);
        int nsucc = 0, j = 0;
        // number of paths tried at an iteration

        int configuration = 0;

        currentThroughput = evaluator.evaluate(partition);
        logger.log(Level.INFO, "Initial Throughput: " + currentThroughput);
        double lastInitialCost = currentThroughput;

        bestThroughput = currentThroughput;

        if (currentThroughput == 0.0) {
            return;
        }

        // The first iteration is really just to get a
        // good initial layout. Some random layouts really kill the
        // algorithm
        for (int two = 0; two < iterations; two++) {
            if (two > 0 && lastInitialCost == currentThroughput) {
                logger.log(Level.INFO, "Not using last layout.");
                partition = new TreeMap<>(init_partition);
                currentThroughput = evaluator.evaluate(partition);
            }
            lastInitialCost = currentThroughput;
            init_partition = new TreeMap<>(partition);

            logger.log(Level.INFO, "\nRunning Annealing Step (" + currentThroughput
                             + ", " + bestThroughput + ")");
            double t = annealMaxTemp(filters, rm);
            double tFinal = annealMinTemp(filters, rm);
            partition = new TreeMap<>(init_partition);
            rm.setPartition(partition);
            while (true) {
                int k = 0;
                nsucc = 0;
 
                // trace partitioning process                
                Compiler.pTrace.append(bestThroughput, currentThroughput, t, two);
                        
                for (k = 0; k < nover; k++) {
                    double newThroughput = getNewThroughput(filters, rm);
                    // true if config change was accepted
                    boolean accepted = acceptanceProbability(currentThroughput, newThroughput, t) > rand.nextDouble();

                    if (accepted) {
                        nsucc++;
                        configuration++;
                    }else
                        rm.restoreState();

                    if (configuration % 500 == 0)
                        logger.log(Level.INFO, ".");

                    // keep the layout with the minimum cost
                    // or if this layout has the same minimun cost
                    // call keepNewEqualMin to decide if we should keep it
                    if (currentThroughput > bestThroughput) {
                        bestThroughput = currentThroughput;
                        // save the layout with the minimum cost
                        best_partition = new TreeMap<>(partition);
                    }


                    // this will be the final layout
                    if (currentThroughput == 0.0)
                        break;
                }

                t *= TFACTR;

                if (nsucc == 0)
                    break;
                if (currentThroughput == 0.0)
                    break;
                if (t <= tFinal)
                    break;
                j++;
            }
            if (currentThroughput == 0)
                break;
        }

        currentThroughput = evaluator.evaluate(partition);
        logger.log(Level.INFO, "\nFinal Throughput: " + currentThroughput + " Max Throughput : "
                           + bestThroughput + " in " + j + " iterations.");
        if (bestThroughput < currentThroughput) {
            best_partition = new TreeMap<>(partition);
            currentThroughput = bestThroughput;
        }

    }
    
    private static double getNewThroughput(List<FilterInstance> filters, RandomMutation rm){
        currentThroughput = evaluator.evaluate(partition);
        rm.alterPartition();
        return evaluator.evaluate(partition);
    }
    
    private static double annealMaxTemp(List<FilterInstance> filters, RandomMutation rm){
        double T = 1.0;
        int total = 0, accepted = 0;

        for (int i = 0; i < MAXTEMPITERATIONS; i++){
            T = 2.0 * T;
            total = 0;
            accepted = 0;
            for (int j = 0; j < 100; j++) {
                double newThroughput = getNewThroughput(filters, rm);
                if (acceptanceProbability(currentThroughput, newThroughput, T) > rand.nextDouble())
                    accepted++;
                else
                    rm.restoreState();
                total++;
            }
            if (((double) accepted) / ((double) total) > .9)
                break;
        }

        return T;
    }

    private static double annealMinTemp(List<FilterInstance> filters, RandomMutation rm){
        double T = 1.0;
        int total = 0, accepted = 0;

        for (int i = 0; i < MINTEMPITERATIONS; i++) {
            T = 0.5 * T;
            total = 0;
            accepted = 0;
            for (int j = 0; j < 100; j++) {
                double newThroughput = getNewThroughput(filters, rm);
                if (acceptanceProbability(currentThroughput, newThroughput, T) > rand.nextDouble())
                    accepted++;
                else
                    rm.restoreState();
                total++;
            }
            if (((double) accepted) / ((double) total) > .1)
                break;
        }
        return T;
    }
    
    public static double acceptanceProbability(double engery, double newEngery, double temperature) {
        if (newEngery > engery) {
            return 1.0;
        }
        return Math.exp((newEngery - engery) / temperature);
    }
    
    public static void smarterDuplicate(List<StreamNode> fissables, int numOfProcessors) {
        LinkedHashMap<StreamNode, Double> averageWork = 
            new LinkedHashMap<>(); 
        findAverageWork(FilterInstance.globalGraph, averageWork);
        
        long totalWork = 0;
        for (int i = 0; i < fissables.size(); i++) {
            StreamNode filter = fissables.get(i);
            int filterWork = estimator.estimateStreamNode(filter);
            logger.log(Level.INFO, "Sorted Work " + i + ": " + filter + " work "
                    + filterWork + ", is fissable: " + filter.isFissable());
            totalWork += filterWork;
        }
        
        for (int i = fissables.size() - 1; i >= 0; i--) {
            StreamNode filter = fissables.get(i);
            logger.log(Level.INFO, filter.StreamNodeName() + " work: " + estimator.estimateStreamNode(filter));
            if (!filter.isFissable()) {
                logger.log(Level.INFO, " not fissible");
                continue;
            }
            long commRate = filter.netSteady() * (filter.netPop() + filter.netPush());
            int filterWork = estimator.estimateStreamNode(filter);
            if (filterWork / commRate <= 10) {
                logger.log(Level.INFO, " Comp/Comp rate too low!");
                continue;
            }
          
            int cousins = getNumCousins(filter);
            if (cousins == 1) {
                logger.log(Level.INFO, " No cousins: dup " + numOfProcessors);
                Integer[] ratio = new Integer[numOfProcessors];
                for(int n = 0; n < numOfProcessors; n++)
                    ratio[n] = 1;
                fissionRatio.put(filter, ratio);
            }
            else {
                // esimate work fraction of this filter vs. cousins
                double workFraction = estimateWorkFraction(filter, averageWork);
                logger.log(Level.INFO, " Filter " + filter + " has " + cousins + " cousins and does " + workFraction + " of the work.");
                if (cousins < numOfProcessors) {
                    int reps = (int)Math.floor(workFraction * ((double)numOfProcessors));
                    reps = Math.min(numOfProcessors - cousins + 1, reps);
                   
                    logger.log(Level.INFO, " Calling dup with: " + reps);
                    if (reps > 1){
                        Integer[] ratio = new Integer[reps];
                        for(int n = 0; n < reps; n++)
                            ratio[n] = 1;
                        fissionRatio.put(filter, ratio);
                    }
                }
                
            }
        }
    }
    
    /**
    * Returns number of parallel streams that are at same nesting
    * depth as <filter> relative to the top-level splitjoin. Assumes
    * that the splitjoin widths on the path from <filter> to the
    * top-level splitjoin are symmetric across other siblings.
    */
    private static int getNumCousins(StreamNode node) {
        int cousins = 1;
        StreamContainer container = (StreamContainer)node.parent;
        while (container != null) {
            if (container instanceof SplitJoin) {
                cousins *= container.children.size();
            }
            container = (StreamContainer)container.parent;
        }
        return cousins;
    }
    
    /**
    * Estimates the percentage of work performed by 'filter' in
    * relation to all of its cousins. Works by ascending through
    * stream graph hierarchy. Whenever a splitjoin is encountered,
    * the average work of filters on each branch is used to guide the
    * division of work between children. Such fractions are
    * accumulated multiplicatively until reaching the top-level
    * splitjoin.
    */
    private static double estimateWorkFraction(StreamNode filter, LinkedHashMap<StreamNode, Double> averageWork) {
        double fraction = 1.0;
        StreamNode parent = filter.parent;
        StreamNode child = filter;
        while (parent != null) {
            if (parent instanceof SplitJoin) {
                // work done by other streams
                double otherWork = 0.0;
                // work done by stream containing <filter>
                double myWork = 0.0;
                for (int i=0; i< ((SplitJoin)parent).children.size(); i++) {
                    double work = averageWork.get(((SplitJoin)parent).children.get(i));
                    if (((SplitJoin)parent).children.get(i)==child) {
                        myWork = work;
                    } else {
                        otherWork += work;
                    }
                }
                // accumulate the fraction of work that we do as part of this splitjoin
                fraction *= myWork / (myWork + otherWork);
            }
            child = parent;
            parent = parent.parent;
        }
        return fraction;
    }
    
    /**
     * Mutates 'averageWork' into a mapping from filters to the
     * average amount of work for all filters deeply nested within
     * 'str'.
     */
    private static void findAverageWork(StreamNode graph, LinkedHashMap<StreamNode, Double> averageWork) {
        // the total amount of fissable work per container
        LinkedHashMap<StreamNode, Integer> sum = new LinkedHashMap<>();
        // the number of fissable filters per container
        LinkedHashMap<StreamNode, Integer> count = new LinkedHashMap<>();

        findWork(graph, count, sum, averageWork);
    }

    /**
     * Counts the number of filters in each container and stores
     * result in 'count'.  Also accumulates 'sum' of work in
     * containers, as well as 'average' of work across all filters in
     * container.
     */
    private static void findWork(StreamNode graph, 
                          LinkedHashMap<StreamNode, Integer> count,
                          LinkedHashMap<StreamNode, Integer> sum,
                          LinkedHashMap<StreamNode, Double> average) {
        if (graph instanceof FilterInstance) {
            FilterInstance filter = (FilterInstance)graph;
            count.put(filter, 1);
            sum.put(filter, estimator.estimateFilter(filter));
            average.put(filter, (double)estimator.estimateFilter(filter));
        } else if (graph instanceof Pipeline) {
            Pipeline cont = (Pipeline)graph;
            int mysum = 0;
            int mycount = 0;
            // visit children to accumulate sum, count
            for (int i=0; i<cont.children.size(); i++) {
                StreamNode child = cont.children.get(i);
                findWork(child, count, sum, average);
                mysum += sum.get(child);
                mycount += count.get(child);
            }
            // calculate average
            double myaverage = ((double)mysum) / ((double)mycount);
            // store results
            sum.put(cont, mysum);
            count.put(cont, mycount);
            average.put(cont, myaverage);
        } else if (graph instanceof SplitJoin) {
            SplitJoin cont = (SplitJoin)graph;
            int mysum = 0;
            int mycount = 0;
            // visit children to accumulate sum, count
            for (int i=0; i<cont.children.size(); i++) {
                StreamNode child = cont.children.get(i);
                findWork(child, count, sum, average);
                mysum += sum.get(child);
                mycount += count.get(child);
            }
            // calculate average
            double myaverage = ((double)mysum) / ((double)mycount);
            // store results
            sum.put(cont, mysum);
            count.put(cont, mycount);
            average.put(cont, myaverage);
        }
    }
}
