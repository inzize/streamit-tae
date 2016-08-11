package backend.common.partitioner.mutator;

import backend.common.partitioner.mutator.random.RandomFissionMutator;
import backend.common.partitioner.mutator.random.RandomRootMultMutator;
import backend.common.partitioner.mutator.random.RandomPartitionMutator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class RandomMutation extends Mutation{
    double rootMultChance = 0.05;
    double partitionChance = 0.9;
    double fissionChance = 0.05;
    
    public RandomMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators){
        super(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
    }
    
    public void mutate(){
        boolean noChange = true;
        backupState();
        while(noChange){
            double random = rand.nextDouble();
            if(random < rootMultChance)
                noChange = !alterRootMult();
            else if(random < partitionChance + rootMultChance)
                noChange = !alterPartition();
            else
                noChange = !alterFission();
        }
    }
    
    public boolean alterRootMult(){
        backupState();
        Mutator m = new RandomRootMultMutator();
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
    
    public boolean alterPartition(){
        backupState();
        Mutator m = new RandomPartitionMutator(partition, chip);
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
    
    public boolean alterFission(){
        backupState();
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        Mutator m = new RandomFissionMutator(fissionRatio, partitionPlan, evaluator);
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
