package backend.common.partitioner.mutator;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.StreamNode;
import java.util.Map;;

public class CorrectionPartitionMutator extends PartitionMutator{
    Evaluator evaluator;
    
    public CorrectionPartitionMutator(Map<StreamNode, Processor> partition, Chip chip, Evaluator evaluator){
        super(partition, chip);
        this.evaluator = evaluator;
    }
    
    @Override
    public boolean mutate() {
        Processor proc = getLargestProcessor(evaluator);
        filter = getSmallestFilter(evaluator, proc);
        target_processor = getEnoughProcessor(evaluator, filter);
        if(target_processor == null)
            return false;
        return finalizeChange();
    }
}
