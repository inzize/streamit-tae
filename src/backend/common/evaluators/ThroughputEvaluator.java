package backend.common.evaluators;

import streamit2.Compiler;
import backend.common.comparators.ProcessorComparator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.estimators.CommunicationEstimator;
import backend.common.estimators.Estimator;
import static backend.common.evaluators.EvaluationStats.PartitionBottleneck.FILTER;
import static backend.common.evaluators.EvaluationStats.PartitionBottleneck.LINK;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Analyses the throughput of a given partition<br><br>
 * 
 * It is assumed that the throughput is either limited by a communication or computational bottleneck.
 * Furthermore it is assumed that the computational performance is limited by the processor with highest computational time and the communication performance is limited by the highest transmission time.<br>
 * 
 * The accuracy of the final computation and transmission time estimations depend on the runtime configured estimators.<br>
 * Computation time per processor is assumed to be the sum of all filter computation times (estimated filter by filter via {@link backend.common.estimators.ASTEstimator} or {@link backend.common.estimators.PerfFileEstimator}) + sum of communication software overhead (estimated via {@link backend.common.estimators.ChannelEstimator}).<br>
 * Transmission times per link are estimated via {@link backend.common.estimators.CommunicationEstimator} and {@link backend.common.estimators.CommunicationModel}. [This will be merged in the future] <br><br>
 * 
 * Currently the bottleneck reason is only returned via lastStats but ranking, bottleneckFilters, etc are reported depending on the estimated bottleneck.
 * 
 * @see Evaluator
 */
public class ThroughputEvaluator extends Evaluator{
    private static final Logger logger = Logger.getLogger("compiler");
    private static final double targetMinThroughput = 0;
    final Chip chip;
    final Estimator filterEstimator;
    private CommunicationEstimator comEstimator;
    private Map<Processor, Integer> procWorks;
    // filters on bottleneck processor
    private Map<FilterInstance, Integer> bottleneckFilters;
    private Map<FilterInstance, Integer> idleFilters;
    private Map<FilterInstance, Integer> filtersRank;
    private Processor idleProc;
    private Integer idleWork;
    private Processor busyProc;
    private Integer busyWork;
    private double currentThroughput = 0;
    private EvaluationStats lastStats; 

    /**
     * Create ThrougputEvaluator with runtime selected Chip and FilterEstimator implementation<br><br>
     * 
     * You normally want to use this constructor
     */
    public ThroughputEvaluator() {
        this.filterEstimator = Compiler.filterEstimator;
        this.chip = Compiler.chip;
    }
    
    /**
     * Create ThrougputEvaluator with runtime selected FilterEstimator implementation and use the given chip<br><br>
     * 
     * Use case: You want to change the target model to see if it improves the throughput 
     */
    public ThroughputEvaluator(Chip chip) {
        this.filterEstimator = Compiler.filterEstimator;
        this.chip = chip;
    }
    
    /**
     * Create ThroughputEvaluator with special FilterEstimator implementation, use runtime selected Chip<br><br>
     * 
     * Use case: Compare accuracy of different Estimator implementations
     */
    public ThroughputEvaluator(Estimator filterEstimator) {
        this.chip = Compiler.chip;
        this.filterEstimator = filterEstimator;
    }

     
    @Override
    public Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc){
        Map<FilterInstance, Integer> filterWorks = new LinkedHashMap<>();
        for (FilterInstance filter : currentPartition.keySet())
            if(currentPartition.get(filter) == proc){
                int time = evaluateFilter(null, currentPartition, filter);
                filterWorks.put(filter, time);
            }
        if(filterWorks.isEmpty())
            return new LinkedHashMap<>();
        return sortFilters(filterWorks);
    }
    
    public int evaluateFilter(PrintStream writer, Map<FilterInstance, Processor> partition, FilterInstance filter){
        int cost = 0;
        if (writer != null)
            writer.println("    " + filter);
        cost += comEstimator.GetWaitCost(writer, filter);
        int work = filterEstimator.estimateFilter(filter);
        cost += work;
        if (writer != null)
            writer.println("        work: " + work);
        cost += comEstimator.GetDoneCost(writer, filter);
        return cost;
    }
    
    public double getEstimate(PrintStream writer, Map<FilterInstance, Processor> partition) {
        return getEstimateStats(writer,partition).throughput;
    }
    
    public EvaluationStats getLastStats() {
        return lastStats;
    }
    
    public EvaluationStats getEstimateStats(PrintStream writer, Map<FilterInstance, Processor> partition) {
        EvaluationStats stats = new EvaluationStats();
        currentThroughput = 0;
        currentPartition = new LinkedHashMap<>(partition);
        comEstimator = new CommunicationEstimator(partition);
        List<Processor> procs = new ArrayList(new HashSet(partition.values()));
        Collections.sort(procs);
        int bottleneck = 0;
        Map<Processor, Integer> costs = new LinkedHashMap<>();
        Map<FilterInstance, Integer> filterWorks = new LinkedHashMap<>();
        Processor busiestProc = procs.get(0);
        for (Processor proc : procs)
        {
            int cost = 0;
            if (writer != null)
                writer.println(proc);
            for (Map.Entry<FilterInstance, Processor> entry : partition.entrySet())
                if (entry.getValue() == proc)
                {
                    FilterInstance filter = entry.getKey();
                    int work = evaluateFilter(writer, partition, filter);
                    filterWorks.put(filter, work);
                    cost += work;
                }
            if (writer != null)
                writer.println("    total: " + cost);
            if (cost > bottleneck){
                bottleneck = cost;
                busiestProc = proc;
            }
            
            costs.put(proc, cost);
        }
        
        List<FilterInstance> filters = StreamNode.globalGraph.InOrder();
        // get netPop rate of last filter for throughput estimation
        int lastFilterPop = (int)(filters.get(filters.size()-1).pop * filters.get(filters.size()-1).netSteady());
        
        stats.linkRank = rankLink(comEstimator.getLinkData());
        int linkBottleneck = 0;
        if(stats.linkRank.size() > 0)
            linkBottleneck = (int)stats.linkRank.get(stats.linkRank.keySet().toArray()[stats.linkRank.size() - 1]);
        boolean linkIsBottleneck = linkBottleneck > bottleneck;
        
        if (writer != null)
            writer.println("throughput: " + bottleneck + " cycles/iteration (multiplicity " + StreamNode.getRootMult()+", outputItems: "+ lastFilterPop+", filters: " + filters.size()+") ..Debug: link bottlenect = " + linkBottleneck);
        
        if(linkIsBottleneck) {
            bottleneck = linkBottleneck;
            stats.bottleneck = LINK;
        } else {
            stats.bottleneck = FILTER;
        }
        busyProc = busiestProc;
        busyWork = bottleneck;
        bottleneckFilters = filtersOnProcessorRank(busiestProc);
        procWorks = sortProcessorByProcessorWork(costs);
        idleFilters = filtersOnProcessorRank(idleProc);
        filtersRank = sortFilters(filterWorks);
        
        // get netPop rate of last filter for throughput estimation
        double throughput = (double) lastFilterPop/bottleneck;
        throughput *= 1000000;
       
        currentThroughput = throughput;
        stats.throughput = throughput;
        lastStats = stats;
        return stats;
    }
    
    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        if (streamit2.Compiler.partitionTrace) {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long startTime = bean.getCurrentThreadCpuTime();
            double ret = getEstimate(null, partition);
            long stopTime = bean.getCurrentThreadCpuTime();
            Compiler.pTrace.append(stopTime - startTime, ret);
            return ret;
        } else {
            return getEstimate(null, partition);
        }
    }
    
    @Override
    public Processor getIdleProcessor(){
        return idleProc;
    }
    
    @Override
    public Integer getIdleProcessorWork(){
        return idleWork;
    }
    
    @Override
    public Processor getBusyProcessor(){
        return busyProc;
    }
    
    @Override
    public Integer getBusyProcessorWork(){
        return busyWork;
    }
    
    public Map<Processor, Integer> sortProcessorByProcessorWork(Map<Processor, Integer> costs){
        for(Processor proc: chip.getProcessors())
            if(!costs.containsKey(proc))
                costs.put(proc, 0);
        ProcessorComparator vc =  new ProcessorComparator(costs);
        Map<Processor, Integer> SortedProcessorWorks = new TreeMap<>(vc);
        SortedProcessorWorks.putAll(costs);
        Map<Processor, Integer> toReturn = new LinkedHashMap<>();
        toReturn.putAll(SortedProcessorWorks);
        idleProc = (Processor)toReturn.keySet().toArray()[0];
        idleWork = toReturn.get(idleProc);
        return toReturn;
    }
    
    @Override
    public Map<FilterInstance, Integer> getBottleneckFilters(){
        return bottleneckFilters;
    }
    
    @Override
    public Map<FilterInstance, Integer> getIdleFilters(){
        return idleFilters;
    }
        
    @Override
    public Map<FilterInstance, Integer> getFiltersRank(){
        return filtersRank;
    }        

    @Override
    public Map<Processor, Integer> getProcessorsRank() {
        return procWorks;
    }

    @Override
    public boolean isValid() {
        return currentThroughput > targetMinThroughput;
    }

    @Override
    public double getValue() {
        return currentThroughput;
    }
}
