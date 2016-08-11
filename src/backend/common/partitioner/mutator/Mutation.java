package backend.common.partitioner.mutator;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.coreVA.CoreVABackend;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Mutation {
    protected static final Random rand = new Random(42);
    static StreamNode graph = FilterInstance.globalGraph;
    SortedMap<StreamNode, Processor> partition;
    List<FilterInstance> filters;
    Chip chip;
    Map<StreamNode, Integer[]> fissionRatio;
    Map<String, Integer> partitionPlan;
    List<Evaluator> evaluators;
    boolean mult_mutated;
    boolean fission_mutated;
    boolean partition_mutated;
    Map<StreamNode, Integer[]> fissionRatioBackup;
    Map<String, Integer> partitionPlanBackup;
    int multBackup;
    
    public Mutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators)
    {
        this.filters = filters;
        this.partition = partition;
        this.chip = chip;
        this.fissionRatio = fissionRatio;
        this.partitionPlan = partitionPlan;
        this.evaluators = evaluators;
        fission_mutated = false;
        partition_mutated = false;
        mult_mutated = false;
        updatePartitionPlan(partition);
    }
    
    public Mutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, int mult){
        this.filters = filters;
        this.partition = partition;
        this.chip = chip;
        this.fissionRatio = fissionRatio;
        this.partitionPlan = partitionPlan;
        partition(fissionRatio, partitionPlan, partition, mult);
    }
    
    public void setPartition(SortedMap<StreamNode, Processor> partition){
        this.partition = partition;
        updatePartitionPlan(this.partition);
    }
    
    public void backupState(){
        fission_mutated = false;
        partition_mutated = false;
        mult_mutated = false;
        multBackup = StreamNode.getRootMult();
        partitionPlanBackup = new LinkedHashMap<>();
        copyPartitionPlan(partitionPlan, partitionPlanBackup);
        fissionRatioBackup = new LinkedHashMap<>();
        copyFissionRatio(fissionRatio, fissionRatioBackup);
    }
    
    private void copyPartitionPlan(Map<String, Integer> original, Map<String, Integer> copy){
        copy.clear();
        for(String key: original.keySet())
            copy.put(key, original.get(key));
    }
    
    private void copyFissionRatio(Map<StreamNode, Integer[]> original, Map<StreamNode, Integer[]> copy){
        copy.clear();
        for(StreamNode key: original.keySet())
            copy.put(key, original.get(key).clone());
    }
    
    public void restoreState(){
        if(fission_mutated){
            copyFissionRatio(fissionRatioBackup, fissionRatio);
            copyPartitionPlan(partitionPlanBackup, partitionPlan);
            updateFission();
            StreamNode.setRootMult(multBackup);
            fission_mutated = false;
        }
        if(partition_mutated){
            copyPartitionPlan(partitionPlanBackup, partitionPlan);
            partitionByPlan(partitionPlan);
            partition_mutated = false;
        }
        if(mult_mutated){
            StreamNode.setRootMult(multBackup);
            mult_mutated = false;
        }
        validate(filters, evaluators, partition);
    }
    
    public void partition(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, SortedMap<StreamNode, Processor> partition, int mult){
        this.fissionRatio = fissionRatio;
        this.partition = partition;
        this.partitionPlan = partitionPlan;
        fissFilters(fissionRatio);
        partitionByPlan(partitionPlan);
        StreamNode.setRootMult(mult);
    }
    
    public static void fissFilters(Map<StreamNode, Integer[]> ratio) {
        StreamNode.restoreGraph();
        List<StreamNode> fissables = graph.getFissables();
        if(ratio != null && ratio.size() > 0)
            for (StreamNode f : fissables) {
                if (ratio.containsKey(f)) {
                    f.FissNode(ratio.get(f));
                }
            }
        graph.SteadySchedule(null);
        graph.InitSchedule(0);
        graph.Flatten();
        List<FilterInstance> filters = graph.InOrder();
        streamit2.Compiler.SimplifyEdges(filters);
        //streamit2.Compiler.PrimePumpSchedule(streamit2.Compiler.NormalPrimePump(filters));
    }
    
    protected final void updatePartitionPlan(SortedMap<StreamNode, Processor> partition){
        partitionPlan.clear();
        for (StreamNode filter : partition.keySet()) {
            Processor p = partition.get(filter);
            partitionPlan.put(filter.StreamNodeName(), p.getGID());
        }
    }
    
    protected final void partitionByPlan(Map<String, Integer> pp){
        partition.clear();
        List<StreamNode> filters = graph.getFissables();
        for(StreamNode filter : filters){
            String targetFilterName = filter.StreamNodeName();
            int targetProcessorID = pp.get(targetFilterName);
            partition.put(filter, chip.getProcessors()[targetProcessorID]);
        }
    }
    
    public void updatePartition(){
        updatePartitionPlan(partition);
        //evaluators.evaluateRaw(partition);
        for(Evaluator ev : evaluators) {
            ev.evaluate(partition);
        }        
    }
    
    public void updateRootMult(){
        //evaluators.evaluateRaw(partition);
    }
    
    public void updateFission(){
        long originalGlobalMult = StreamNode.getGlobalMult();
        fissFilters(fissionRatio);
        partitionByPlan(partitionPlan);
        updateMult(originalGlobalMult);
        //evaluators.evaluateRaw(partition);
    }
    
    public static double getThroughput(List<Evaluator> evaluators){
        for(Evaluator evaluator: evaluators)
            if(evaluator instanceof ThroughputEvaluator)
                return evaluator.getValue();
        return 0;
    }
    
    public static boolean validate(List<FilterInstance> filters, List<Evaluator> evaluators, SortedMap<StreamNode, Processor> partition){
        CoreVABackend.BuildChannels(flattenPartition(partition));
        for(Evaluator evaluator: evaluators){
            evaluator.evaluate( partition);
            if(!evaluator.isValid())
                return false;
            }
        return true;
    }
    
    public static Evaluator getInvalidEvaluator(List<FilterInstance> filters, List<Evaluator> evaluators, SortedMap<StreamNode, Processor> partition){
        CoreVABackend.BuildChannels(flattenPartition(partition));
        for(Evaluator evaluator: evaluators){
            evaluator.evaluate(partition);
            if(!evaluator.isValid())
                return evaluator;
        }
        return null;
    }
    
    public static SortedMap<FilterInstance, Processor> flattenPartition(SortedMap<StreamNode, Processor> partition){
        SortedMap<FilterInstance, Processor> flattened = new TreeMap<>();
        for(StreamNode node: partition.keySet())
            if(node instanceof FilterInstance)
                flattened.put((FilterInstance)node, partition.get(node));
            else{
                List<FilterInstance> filters = node.InOrder();
                for(FilterInstance filter: filters)
                    flattened.put(filter, partition.get(node));
            }
                
        return flattened;
    }
    
    public static Map<FilterInstance, Processor> flattenPartition(Map<StreamNode, Processor> partition){
        Map<FilterInstance, Processor> flattened = new LinkedHashMap<>();
        for(StreamNode node: partition.keySet())
            if(node instanceof FilterInstance)
                flattened.put((FilterInstance)node, partition.get(node));
            else{
                List<FilterInstance> filters = node.InOrder();
                for(FilterInstance filter: filters)
                    flattened.put(filter, partition.get(node));
            }
                
        return flattened;
    }
    
    public static SortedMap<StreamNode, Processor> convertPartition(SortedMap<FilterInstance, Processor> partition){
        SortedMap<StreamNode, Processor> converted = new TreeMap<>();
        for(FilterInstance node: partition.keySet())
            converted.put(node, partition.get(node));
                
        return converted;
    }
    
    public static Map<StreamNode, Processor> convertPartition(Map<FilterInstance, Processor> partition){
        Map<StreamNode, Processor> converted = new LinkedHashMap<>();
        for(FilterInstance node: partition.keySet())
            converted.put(node, partition.get(node));
                
        return converted;
    }
    
    public void finalPartition(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, SortedMap<StreamNode, Processor> partition, int mult){
        partition(fissionRatio, partitionPlan, partition, mult);
        hillClimbMult(evaluators, filters, partition);
    }
    
    public static void hillClimbMult(List<Evaluator> evaluators, List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition){
        double previousThroughput = 0;
        double newThroughput = 0;
        if(validate(filters, evaluators, partition))
            newThroughput = getThroughput(evaluators);
        while(newThroughput > previousThroughput){
            previousThroughput = newThroughput;
            if (!StreamNode.setRootMult(StreamNode.getRootMult()+1))
                return;
            if(validate(filters, evaluators, partition))
                newThroughput = getThroughput(evaluators);
            else
                newThroughput = 0;
        }
        StreamNode.setRootMult(StreamNode.getRootMult()-1);
    }
    
    private void updateMult(long originalGlobalMult){
        long currentGlobalMult = StreamNode.getGlobalMult();
        double new_root = (double) StreamNode.getRootMult() * originalGlobalMult / currentGlobalMult;
        StreamNode.setRootMult((int)Math.floor(new_root));
    }
}
