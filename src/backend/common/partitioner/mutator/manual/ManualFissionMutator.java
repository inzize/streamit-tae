package backend.common.partitioner.mutator.manual;

import backend.common.partitioner.mutator.FissionMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.Map;

public class ManualFissionMutator extends FissionMutator{
    
    public ManualFissionMutator(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, FilterInstance filter, int copies){
        super(fissionRatio, partitionPlan);
        this.filter = filter;
        new_ratio = filter.getRatio(copies);
    }

    @Override
    public boolean mutate() {
        target_processor_id = partitionPlan.get(filter.StreamNodeName());
        return finalizeChange();
    }
    
}
