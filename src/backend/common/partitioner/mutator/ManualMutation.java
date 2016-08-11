package backend.common.partitioner.mutator;

import backend.common.partitioner.mutator.manual.ManualFissionMutator;
import backend.common.partitioner.mutator.manual.ManualPartitionMutator;
import backend.common.partitioner.mutator.manual.ManualRootMultMutator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ManualMutation extends Mutation{
    
    public ManualMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators){
        super(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
    }
    
    public ManualMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, int mult){
        super(filters, partition, chip, fissionRatio, partitionPlan, mult);
    }
    
    public boolean alterRootMult(int newMult){
        Mutator m = new ManualRootMultMutator(newMult);
        backupState();
        if(m.mutate()){
            updateRootMult();
            mult_mutated = true;
            return Mutation.validate(filters, evaluators, partition);
        }
        return false;
    }
    
    public boolean alterPartition(FilterInstance filter, Processor target_processor){
        Mutator m = new ManualPartitionMutator(partition, chip, filter, target_processor);
        backupState();
        if(m.mutate()){
            updatePartition();
            partition_mutated = true;
            return Mutation.validate(filters, evaluators, partition);
        }
        return false;
    }
    
    public boolean alterFission(FilterInstance filter, int copies){
        Mutator m = new ManualFissionMutator(fissionRatio, partitionPlan, filter, copies);
        backupState();
        if(m.mutate()){
            updateFission();
            fission_mutated = true;
            return Mutation.validate(filters, evaluators, partition);
        }
        return false;
    }
}
