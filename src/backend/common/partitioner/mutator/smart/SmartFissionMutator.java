package backend.common.partitioner.mutator.smart;

import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.FissionMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.List;
import java.util.Map;

public class SmartFissionMutator extends FissionMutator{
    FilterInstance fissedFilter = null;
    public SmartFissionMutator(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, Evaluator evaluator){
        super(fissionRatio, partitionPlan);
        this.evaluator = evaluator;
    }
    
    @Override
    public boolean mutate() {
        //int index = -1;
        fissionTransformation ft;
        filter = findBusiestFilter();
        if(filter == null)
            return true;
        Integer[] original_ratio;
        if(fissionRatio.containsKey(filter))
            original_ratio = fissionRatio.get(filter);
        else{
            original_ratio = new Integer[1];
            original_ratio[0] = 1;
        }
        
        new_ratio = changeCopy(original_ratio, 1);
        return finalizeChange();
    }
    
    public boolean reduceFission(StreamNode node){
        filter = (FilterInstance)node;
        if(filter != null){
            Integer[] original_ratio;
            if(!fissionRatio.containsKey(filter))
                return false;
            original_ratio = fissionRatio.get(filter);
            new_ratio = changeCopy(original_ratio, -1);
            return finalizeChange();
        }
        return false;
    }
    
    private FilterInstance findBusiestFilter(){
        for(FilterInstance f: evaluator.getBottleneckFilters().keySet())
            if(f.isFissable())
                if(f.isFissedNode){
                    fissedFilter = f;
                    return (FilterInstance)f.previousNode;
                }
                else
                    return f;
        return null;
    }
    
    private FilterInstance findUnnecessaryFission() {
        StreamNode graph = FilterInstance.globalGraph;
        for(FilterInstance f: evaluator.getBottleneckFilters().keySet())
            if(f.isFissedNode)
                return null;
        List<FilterInstance> filters = graph.InOrder();
        for(FilterInstance f: filters)
            if(f.isFissedNode && !evaluator.getBottleneckFilters().containsKey(f))
                return (FilterInstance)f.previousNode;
        return null;
    }
}
