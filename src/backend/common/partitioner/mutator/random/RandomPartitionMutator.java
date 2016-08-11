package backend.common.partitioner.mutator.random;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.partitioner.mutator.PartitionMutator;
import hierarchicalGraph.StreamNode;
import java.util.Map;

public class RandomPartitionMutator extends PartitionMutator{
    
    public RandomPartitionMutator(Map<StreamNode, Processor> partition, Chip chip){
        super(partition, chip);
    }

    @Override
    public boolean mutate() {
        StreamNode[] keys = partition.keySet().toArray(new StreamNode[partition.keySet().size()]);
        filter = keys[(int) (rand.nextDouble() * keys.length)];
        Processor[] processors = chip.getProcessors();
        if(processors.length < 2)
            return false;
        target_processor = processors[(int) (rand.nextDouble() * processors.length)];
        return finalizeChange();
    }
    
}
