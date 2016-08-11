package backend.common.partitioner.mutator;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class CorrectionMutation extends Mutation{
    
    public CorrectionMutation(List<FilterInstance> filters, SortedMap<StreamNode, Processor> partition, Chip chip, Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, List<Evaluator> evaluators){
        super(filters, partition, chip, fissionRatio, partitionPlan, evaluators);
    }
    
    public boolean mutatePartition(Evaluator evaluator){
        backupState();
        Mutator m = new CorrectionPartitionMutator(partition, chip, evaluator);
        List<Evaluator> evas = new ArrayList<>();
        evas.add(evaluator);
        for(int i = 0; i < 100; i++){
            if(m.mutate()){
                updatePartition();
                partition_mutated = true;
                if(validate(filters, evas, partition))
                    return true;
            }
        }
        return false;
    }
    
    public boolean correct(){
        for(int i = 0; i < 10; i++){
            mutatePartition(Mutation.getInvalidEvaluator(filters, evaluators, partition));
            if(validate(filters, evaluators, partition))
                return true;
        }
        return false;
    }
    
//    public boolean mutateFission(){
//        backupState();
//        Mutator m = new WeightFissionMutator2(fissionRatio, partitionPlan, evaluators, weight);
//        if(m.mutate()){
//            updateFission();
//            fission_mutated = true;
//            if(validate(evaluators, partition))
//                return true;
//            else
//                restoreState();
//        }
//        return false;
//    }
}
