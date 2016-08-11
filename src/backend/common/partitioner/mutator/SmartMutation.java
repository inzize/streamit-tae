package backend.common.partitioner.mutator;

import backend.common.partitioner.mutator.random.RandomFissionMutator;
import backend.common.partitioner.mutator.random.RandomRootMultMutator;
import backend.common.partitioner.mutator.random.RandomPartitionMutator;
import backend.common.partitioner.mutator.smart.SmartFissionMutator;
import backend.common.partitioner.mutator.smart.SmartRootMultMutator;
import backend.common.partitioner.mutator.smart.SmartPartitionMutator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class SmartMutation extends Mutation{
    double rootMultChance = 0.05;
    double partitionChance = 0.9;
    double fissionChance = 0.05;
    
    public SmartMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators){
        super(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
    }
    
    public boolean SimplifyFission(StreamNode node){
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        SmartFissionMutator m = new SmartFissionMutator(fissionRatio, partitionPlan, evaluator);
        backupState();
        if(m.reduceFission(node)){
            updateFission();
            fission_mutated = true;
            if(validate(filters, evaluators, partition))
                return true;
            else
                restoreState();
        }
        return false;
    }
    
    public void mutate(){
        boolean noChange = true;
        while(noChange){
            double random = rand.nextDouble();
            if(random < rootMultChance)
                noChange = !mutateRootMult();
            else if(random < partitionChance + rootMultChance)
                noChange = !mutatePartition();
            else
                noChange = !mutateFission();
        }
    }
    
    public boolean mutateRootMult(){
        backupState();
        Mutator m;
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        if(rand.nextDouble() < 0.3)
            m = new SmartRootMultMutator(evaluator);
        else
            m = new RandomRootMultMutator();
        if(m.mutate()){
            updateRootMult();
            mult_mutated = true;
            if(validate(filters, evaluators, partition))
                return true;
            else
                restoreState();
        }
        return false;
    }
    
    public boolean mutatePartition(){
        backupState();
        Mutator m;
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        if(rand.nextDouble() < 0.5)
            m = new SmartPartitionMutator(partition, chip, evaluator);
        else
            m = new RandomPartitionMutator(partition, chip);
        if(m.mutate()){
            updatePartition();
            partition_mutated = true;
            if(validate(filters, evaluators, partition))
                return true;
            else
                restoreState();
        }
        return false;
    }
    
    public boolean mutateFission(){
        backupState();
        Mutator m;
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        if(rand.nextDouble() < 0.5)
            m = new SmartFissionMutator(fissionRatio, partitionPlan, evaluator);
        else
            m = new RandomFissionMutator(fissionRatio, partitionPlan, evaluator);
        
        if(m.mutate()){
            updateFission();
            fission_mutated = true;
            if(validate(filters, evaluators, partition))
                return true;
            else
                restoreState();
        }
        return false;
    }
}
