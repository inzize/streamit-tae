package backend.common.partitioner.mutator.manual;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.partitioner.mutator.PartitionMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.Map;

public class ManualPartitionMutator extends PartitionMutator{

    public ManualPartitionMutator(Map<StreamNode, Processor> partition, Chip chip, FilterInstance filter, Processor target_processor){
        super(partition, chip);
        this.filter = filter;
        this.target_processor = target_processor;
    }

    @Override
    public boolean mutate() {
        if(finalizeChange())
            return true;
        return false;
    }

}
