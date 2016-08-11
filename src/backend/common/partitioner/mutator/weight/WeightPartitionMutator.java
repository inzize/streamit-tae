package backend.common.partitioner.mutator.weight;

import backend.common.partitioner.mutator.random.RandomPartitionMutator;
import backend.common.comparators.ProcessorComparator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.PartitionMutator;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;;
import java.util.TreeMap;import java.util.TreeMap;

public class WeightPartitionMutator extends PartitionMutator{
    Evaluator evaluator;
    double weight;
    
    public WeightPartitionMutator(Map<StreamNode, Processor> partition, Chip chip, Evaluator evaluator, double weight){
        super(partition, chip);
        this.evaluator = evaluator;
        this.weight = weight;
    }
    
    @Override
    public boolean mutate() {
        boolean random = false;
        if(rand.nextDouble() < 0.0 || weight > rand.nextDouble())
            random = true;
        
        if(random){
            PartitionMutator rpm = new RandomPartitionMutator(partition, chip);
            return rpm.mutate();
        }
        
        Map<FilterInstance, Integer> filterWorks = evaluator.getBottleneckFilters();
        if(filterWorks.size() < 2){
            if(!memoryReductionMovement())
                return memoryReductionMovement2();
            return true;
        }
        int gap = evaluator.getBusyProcessorWork() - evaluator.getIdleProcessorWork();
        FilterInstance fi = (FilterInstance)filterWorks.keySet().toArray()[rand.nextInt(filterWorks.size())];
        if(filterWorks.get(fi) < gap){
            filter = fi;
            List<Processor> idleProcessors = new ArrayList<>(evaluator.getProcessorsRank().keySet());
            int firstWork = evaluator.getProcessorsRank().get(idleProcessors.get(0));
            for(Processor proc : evaluator.getProcessorsRank().keySet()){
                if(evaluator.getProcessorsRank().get(proc) != firstWork)
                    idleProcessors.remove(proc);
            }
            if(idleProcessors.size() == 1){
                target_processor = evaluator.getIdleProcessor();
                return finalizeChange();
            }

            List<Processor> suitableProcessors = new ArrayList<>(findSuitableProcessors(fi).keySet());
            for(Processor proc: idleProcessors){
                if(suitableProcessors.contains(proc)){
                    target_processor = proc;
                    return finalizeChange();
                }
            }
            target_processor = evaluator.getIdleProcessor();
            return finalizeChange();
        }
        
        if(!evaluator.getIdleFilters().isEmpty()){
            List<FilterInstance> filters = new ArrayList<>(evaluator.getIdleFilters().keySet());
            Collections.reverse(filters);
            for(FilterInstance f: filters){
                List<Processor> processors = new ArrayList<>(evaluator.getProcessorsRank().keySet());
                Collections.reverse(processors);
                for(Processor proc: processors)
                    if(evaluator.getIdleFilters().get(f) + evaluator.getProcessorsRank().get(proc) < evaluator.getBusyProcessorWork()){
                        filter = f;
                        target_processor = proc;
                        return finalizeChange();
                    }
            }
        }
        
        if(!memoryReductionMovement())
            return memoryReductionMovement2();
        return true;
    }
    
    private boolean memoryReductionMovement(){
        List<StreamNode> filters = new ArrayList<>(partition.keySet());
        int counter = 20;
        while(counter-- > 0){
            FilterInstance filter1 = (FilterInstance)filters.get(rand.nextInt(filters.size()));
            FilterInstance filter2 = (FilterInstance)filters.get(rand.nextInt(filters.size()));
            if(filter1 == filter2)
                continue;
            Map<Processor,Integer> availableProcessors1 = findSuitableProcessors(filter1);
            Map<Processor,Integer> availableProcessors2 = findSuitableProcessors(filter2);
            Processor processor1 = partition.get(filter1);
            Processor processor2 = partition.get(filter2);
            List<Processor> processors1 = new ArrayList<>(availableProcessors1.keySet());
            List<Processor> processors2 = new ArrayList<>(availableProcessors2.keySet());
            if(processors1.contains(processor1) && processors2.contains(processor2)){
                boolean result = true;
                filter = filter1;
                target_processor = processor2;
                result = result & finalizeChange();
                filter = filter2;
                target_processor = processor1;
                result = result & finalizeChange();
                return result;
            }
        }
        return false;
    }
    
        private boolean memoryReductionMovement2(){
        List<StreamNode> filters = new ArrayList<>(partition.keySet());
        int counter = 20;
        while(counter-- > 0){
            FilterInstance filter1 = (FilterInstance)filters.get(rand.nextInt(filters.size()));
            Map<Processor,Integer> availableProcessors1 = findSuitableProcessors(filter1);
            List<Processor> processors1 = new ArrayList<>(availableProcessors1.keySet());
            Processor processor1 = partition.get(filter1);
            if(processors1.get(processors1.size() - 1) != processor1){
                filter = filter1;
                target_processor = processor1;
                return finalizeChange();
            }
        }
        return false;
    }
    
    private Map<Processor,Integer> findSuitableProcessors(FilterInstance filter){
        Map<Processor,Integer> suitableProcessors = new LinkedHashMap<>();
        for(FilterInstance[] list : filter.split)
            for(FilterInstance f: list)
                if(!suitableProcessors.containsKey(partition.get(f)))
                    suitableProcessors.put(partition.get(f), 1);
                else{
                    int value = suitableProcessors.get(partition.get(f));
                    suitableProcessors.put(partition.get(f), value++);
                }
        for(FilterInstance f: filter.join)
            if(!suitableProcessors.containsKey(partition.get(f)))
                suitableProcessors.put(partition.get(f), 1);
            else{
                int value = suitableProcessors.get(partition.get(f));
                suitableProcessors.put(partition.get(f), value++);
        }
        List<Processor> procs = new ArrayList<>(suitableProcessors.keySet());
        for(Processor proc: procs)
            for(Processor cluster: chip.getProcessors()){
                if(!suitableProcessors.containsKey(cluster)){
                    CoreVAProcessor corevacluster = (CoreVAProcessor)cluster;
                    CoreVAProcessor corevaproc = (CoreVAProcessor)proc;
                    if(corevacluster.getCol() == corevaproc.getCol() && corevaproc.getRow() == corevacluster.getRow())
                        suitableProcessors.put(cluster, 0);
                }
            }
        ProcessorComparator vc =  new ProcessorComparator(suitableProcessors);
        Map<Processor, Integer> sortedProcessors = new TreeMap<>(vc);
        sortedProcessors.putAll(suitableProcessors);
        return sortedProcessors;
    }
}
