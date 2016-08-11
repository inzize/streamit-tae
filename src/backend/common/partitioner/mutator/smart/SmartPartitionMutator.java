package backend.common.partitioner.mutator.smart;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.PartitionMutator;
import hierarchicalGraph.StreamNode;
import java.util.Map;

public class SmartPartitionMutator extends PartitionMutator{
    Evaluator evaluator;
    
    public SmartPartitionMutator(Map<StreamNode, Processor> partition, Chip chip, Evaluator evaluator){
        super(partition, chip);
        this.evaluator = evaluator;
    }
    
    @Override
    public boolean mutate() {
        if(evaluator.getBottleneckFilters().size() < 2)
            return true;
        filter = (StreamNode)evaluator.getBottleneckFilters().keySet().toArray()[evaluator.getBottleneckFilters().size() - 1];
        target_processor = evaluator.getIdleProcessor();
        return finalizeChange();
    }
    
}
