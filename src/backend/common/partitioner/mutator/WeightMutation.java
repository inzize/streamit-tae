package backend.common.partitioner.mutator;

import backend.common.partitioner.mutator.weight.WeightRootMultMutator;
import backend.common.partitioner.mutator.weight.WeightFissionMutator2;
import backend.common.partitioner.mutator.weight.WeightPartitionMutator2;
import backend.common.partitioner.mutator.smart.SmartFissionMutator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.smart.SmartSplitTreeMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Basic SA Mutation Class
 * 
 * InnerSA:
 *  -Map Filters to Hardware
 * 
 * OuterSA:
 *  -Fission
 *  -RootMult
 * 
 * SimplifyFission
 */
public class WeightMutation extends Mutation{
    private final double inital_temp;
    
    public WeightMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators, double inital_temp){
        super(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
        this.inital_temp = inital_temp;
    }
    
    private double calculateWeight(double temp){
        return Math.exp(-3*temp/inital_temp);
    }
    
    private boolean mutateRootMult(double temp){
        double weight = calculateWeight(temp);
        backupState();
        Evaluator evaluator = evaluators.get(0);
        Mutator m = new WeightRootMultMutator(evaluator, weight);
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
    
    private boolean mutateFission(double temp){
        double weight = calculateWeight(temp);
        backupState();
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        Mutator m = new WeightFissionMutator2(fissionRatio, partitionPlan, evaluator, weight);
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
    
    public boolean innerMutate(double temp){
        double weight = calculateWeight(temp);
        backupState();
        Evaluator evaluator = evaluators.get(evaluators.size() - 1);
        Mutator m = new WeightPartitionMutator2(partition, chip, evaluator, weight);
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
    
    public boolean mutateSplitTree() {
        SmartSplitTreeMutator m = new SmartSplitTreeMutator(this.filters,this.partition); //This call will change this.filters!
        boolean change = m.mutate();
        if (change) {
            setPartition(this.partition);
            partition_mutated = true;
            validate(filters, evaluators, partition);
        }
        return change;
        
    }
    
    public boolean outerMutate(double temp){
        boolean noChange = true;
        //give up after 10 times
        int counter = 10;
        while(noChange){
            double rnd = rand.nextDouble();
            if(rnd < 0.1)
                noChange = !mutateRootMult(temp);
            else if(streamit2.Compiler.splitTrees && rnd < 0.3)
                noChange = !mutateSplitTree();
            else
                noChange = !mutateFission(temp);
            counter--;
            if(counter < 0)
                return false;
        }
        return true;
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
}
