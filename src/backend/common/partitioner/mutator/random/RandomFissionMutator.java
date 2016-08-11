package backend.common.partitioner.mutator.random;

import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.FissionMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.SplitJoin;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RandomFissionMutator extends FissionMutator{
    public RandomFissionMutator(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, Evaluator evaluator){
        super(fissionRatio, partitionPlan);
        this.evaluator = evaluator;
    }

    @Override
    public boolean mutate() {
        filter = pickRandomFilter();
        if(filter == null)
            return false;
        Integer[] original_ratio;
        if(fissionRatio.containsKey(filter))
            original_ratio = fissionRatio.get(filter);
        else{
            original_ratio = new Integer[1];
            original_ratio[0] = 1;
        }
        
        while(new_ratio == null){
            fissionTransformation ft = original_ratio.length < 2 ? fissionTransformation.COPY_INC : fissionTransformation.values()[(int)(rand.nextDouble() * 2)];
            switch(ft){
                case COPY_INC:
                    new_ratio = changeCopy(original_ratio, 1);
                    break;
                case COPY_DEC:
                    new_ratio = changeCopy(original_ratio, -1);
                    break;
//                case RATE_INC:
//                    new_ratio = changeRate(original_ratio, (int)(rand.nextDouble() * original_ratio.length), 1);
//                    break;
//                case RATE_DEC:
//                    new_ratio = changeRate(original_ratio, (int)(rand.nextDouble() * original_ratio.length), -1);
//                    break;
            }
        }
        if(finalizeChange())
            return true;
        return false;
    }
    
    private FilterInstance pickRandomFilter(){
        StreamNode graph = StreamNode.globalGraph;
        List<FilterInstance> filters = graph.InOrder();
        List<FilterInstance> fissableFilters = new ArrayList<>();
        for(FilterInstance f: filters)
            if(f.isFissable()){
                if(f.isFissedNode){
                    if(!fissableFilters.contains((FilterInstance)((SplitJoin)(f.parent)).previousNode))
                        fissableFilters.add((FilterInstance)((SplitJoin)(f.parent)).previousNode);
                }else
                    fissableFilters.add(f);
            }
        if(fissableFilters.isEmpty())
            return null;
        return fissableFilters.get((int) (rand.nextDouble() * fissableFilters.size()));
    }
}
