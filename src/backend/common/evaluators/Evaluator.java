package backend.common.evaluators;

import backend.common.comparators.FilterComparator;
import backend.common.comparators.LinkComparator;
import backend.common.comparators.ProcessorComparator;
import backend.common.Chip.Link;
import backend.common.Processor;
import backend.common.partitioner.mutator.Mutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Analyse a given partition based on a specific evaluation parameter 
 * (e.g. {@link ThroughputEvaluator throughput}, {@link LatencyEvaluator latency}, {@link MemoryEvaluator memory}, energy [not implemented]) <br>
 * 
 * It provides (depending on the specific subclass/evaluation parameter):<br>
 * <ul>
 * <li>A ranking of filters ({@link #getFiltersRank}) and processors ({@link #getProcessorsRank})</li>
 * <li>A list of worst ({@link #getBottleneckFilters}) and best ({@link #getIdleFilters}) Filters</li>
 * <li>A value to represent the partitions performance ({@link #getValue})</li>
 * </ul>
 * It's furthermore able to validate the partition ({@link #isValid}) against any target model limitations based on the current evaluation paramter.
 * The MemoryEvaluators isValid function will therefore return false if the partitions memory usage exceeds the targets available memory.
 */
public abstract class Evaluator {
    protected Map<FilterInstance, Processor> currentPartition;
    public abstract double evaluate(Map<FilterInstance, Processor> partition);
    public abstract Map<FilterInstance, Integer> getBottleneckFilters();
    public abstract Map<FilterInstance, Integer> getIdleFilters();
    public abstract Processor getIdleProcessor();
    public abstract Integer getIdleProcessorWork();
    public abstract Processor getBusyProcessor();
    public abstract Integer getBusyProcessorWork();
    public abstract double getValue();
    //Rank: all filters, filters on a given processor or all processors
    public abstract Map<FilterInstance, Integer> getFiltersRank();
    public abstract Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc);
    public abstract Map<Processor, Integer> getProcessorsRank();
    public abstract boolean isValid();
    
    public double evaluate(SortedMap<StreamNode, Processor> partition){
        return evaluate(Mutation.flattenPartition(partition));
    }
    
    public enum EvaluatorType {MEMORY, THROUGHPUT, LATENCY, ENERGY};
    
    public static Evaluator getEvaluator(EvaluatorType type) {
        switch (type) {
            case MEMORY:
                return new MemoryEvaluator();
            case THROUGHPUT:
                return new ThroughputEvaluator();
            case LATENCY:
                return new LatencyEvaluator();
            default:
                throw new RuntimeException("not impl");
        }
    }
    
    //Helper functions
    
    protected Map<FilterInstance, Integer> rankFilter(Map<FilterInstance, Integer> filters){
        FilterComparator vc = new FilterComparator(filters);
        Map<FilterInstance, Integer> SortedFilter = new TreeMap<>(vc);
        SortedFilter.putAll(filters);
        Map<FilterInstance, Integer> toReturn = new LinkedHashMap<>();
        toReturn.putAll(SortedFilter);
        return toReturn;
    }
    
    protected Map<Processor, Integer> rankProcessor(Map<Processor, Integer> processors){
        ProcessorComparator vc = new ProcessorComparator(processors);
        Map<Processor, Integer> SortedProcessor = new TreeMap<>(vc);
        SortedProcessor.putAll(processors);
        Map<Processor, Integer> toReturn = new LinkedHashMap<>();
        toReturn.putAll(SortedProcessor);
        return toReturn;
    }
    
    protected Map<FilterInstance, Integer> sortFilters(Map<FilterInstance, Integer> filterWorks) {
        FilterComparator vc =  new FilterComparator(filterWorks);
        Map<FilterInstance, Integer> SortedFilterWorks = new TreeMap<>(vc);
        SortedFilterWorks.putAll(filterWorks);
        Map<FilterInstance, Integer> toReturn = new LinkedHashMap<>();
        toReturn.putAll(SortedFilterWorks);
        return toReturn;
    }
    
    protected Map<Link, Integer> rankLink(Map<Link, Integer> links){
        LinkComparator vc = new LinkComparator(links);
        Map<Link, Integer> SortedLink = new TreeMap<>(vc);
        SortedLink.putAll(links);
        Map<Link, Integer> toReturn = new LinkedHashMap<>();
        toReturn.putAll(SortedLink);
        return toReturn;
    }
}
