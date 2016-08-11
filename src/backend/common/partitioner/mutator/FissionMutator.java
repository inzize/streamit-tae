package backend.common.partitioner.mutator;

import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.SplitJoin;
import hierarchicalGraph.StreamNode;
import java.util.Map;

public abstract class FissionMutator extends Mutator{
    public enum fissionTransformation { COPY_INC, COPY_DEC };
    protected FilterInstance filter;
    protected FilterInstance fissedFilter = null;
    protected Integer[] new_ratio;
    protected Integer target_processor_id = -1;
    protected Map<StreamNode, Integer[]> fissionRatio;
    protected Map<String, Integer> partitionPlan;
    protected Evaluator evaluator;
    
    public FissionMutator(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan){
        this.fissionRatio = fissionRatio;
        this.partitionPlan = partitionPlan;
    }
    
    protected Integer[] changeCopy(Integer[] original_ratio, int change){
        if(change == 0)
            return original_ratio;
        return filter.getRatio(original_ratio.length + change);
    }
    
    protected Integer[] changeRate(Integer[] original_ratio, int index, int change){
        if(change == 0)
            return original_ratio;
        Integer[] ratio;
        int original_rate = original_ratio[index];
        int new_rate = original_rate + change;
        if(new_rate < 1 || original_ratio.length < 2)
            return null;
        ratio = new Integer[original_ratio.length];
        for(int i = 0; i < original_ratio.length; i++)
            ratio[i] = i == index ? new_rate : original_ratio[i];
        return ratio;
    }
    
    @Override
    public boolean finalizeChange() {
        if(filter.isFissedNode)
            filter = (FilterInstance)((SplitJoin)(filter.parent)).previousNode;
        if(!filter.isFissable())
            return false;
        if(fissionRatio.containsKey(filter)){
            Integer[] original_ratio = fissionRatio.get(filter);
            if(checkArray(original_ratio, new_ratio))
                return false;
            allocateFilters(original_ratio, new_ratio);
            return true;
        }else{
            if(new_ratio == null || new_ratio.length < 2)
                return false;
            Integer[] original_ratio = new Integer[1];
            allocateFilters(original_ratio, new_ratio);
            return true;
        }

    }

    private boolean checkArray(Integer[] ratio1, Integer[] ratio2){
        if(ratio1.length != ratio2.length)
            return false;
        for(int i = 0; i < ratio1.length; i++)
            if(ratio1[i] != ratio2[i])
                return false;
        return true;
    }
    
    private void allocateFilters(Integer[] original_ratio, Integer[] new_ratio){
        int original_length = original_ratio.length;
        int new_length = new_ratio.length;
        
        if(original_length < new_length)
            if(target_processor_id > -1){
                if(original_length < 2)
                    partitionPlan.put(filter.StreamNodeName() + "_1", target_processor_id);
                for(int i = original_length + 1; i <= new_length; i++)
                    partitionPlan.put(filter.originalFilterName + "_" + i, target_processor_id);
            }else{
                target_processor_id = evaluator.getIdleProcessor().getGID();
                if(original_length < 2){
                    int original_processor_id = partitionPlan.remove(filter.StreamNodeName());
                    partitionPlan.put(filter.StreamNodeName() + "_1", original_processor_id);
                }
                for(int i = original_length + 1; i <= new_length; i++){
                    int p_id = target_processor_id = evaluator.getIdleProcessor().getGID();
                    partitionPlan.put(filter.originalFilterName + "_" + i, p_id);
                }
            }
        else
            removeFiltersFromPartitionPlan(original_length, new_length);
        
        if(new_length < 2){
            fissionRatio.remove(filter);
            target_processor_id = partitionPlan.get(filter.StreamNodeName() + "_1");
            partitionPlan.remove(filter.StreamNodeName() + "_1");
            partitionPlan.put(filter.StreamNodeName(), target_processor_id);
        }else
            fissionRatio.put(filter, new_ratio);
    }
    
    private void removeFiltersFromPartitionPlan(int original_length, int new_length){
        for(int i = original_length; i > new_length; i--){
            int toRemove;
            if(fissedFilter == null)
                toRemove = (int)(rand.nextDouble() * i) + 1;
            else
                toRemove = fissedFilter.fission_id;
            partitionPlan.remove(filter.originalFilterName + "_" + toRemove);
            for(int j = toRemove+1; j < i+1; j++){
                String original_name = filter.originalFilterName + "_" + j;
                String new_name = filter.originalFilterName + "_" + (j - 1);
                int proc_number = partitionPlan.get(original_name);
                partitionPlan.remove(original_name);
                partitionPlan.put(new_name, proc_number);
            }
        }
    }
}
