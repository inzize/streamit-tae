package backend.common.partitioner.mutator.weight;

import backend.common.channels.AbstractChannel;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.FissionMutator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.SplitJoin;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WeightFissionMutator2 extends FissionMutator{
    double weight;
    
    public WeightFissionMutator2(Map<StreamNode, Integer[]> fissionRatio, Map<String, Integer> partitionPlan, Evaluator evaluator, double weight){
        super(fissionRatio, partitionPlan);
        this.evaluator = evaluator;
        this.weight = weight;
    }
    
    @Override
    public boolean mutate() {
        boolean noPossibleMove = true;
        for(FilterInstance f: evaluator.getBottleneckFilters().keySet())
            if(f.isFissedNode || f.isFissable()){
                noPossibleMove = false;
                break;
            }

        if(noPossibleMove)
            return true;
        
        
        fissionTransformation ft;
        //get a busy processor
        Processor proc = getLargeProcessor(evaluator, "work");
        // fiss a bottleneck filter
        filter = getLargeFilter(evaluator, proc, true);
        //match fission amount to reduce merging/splitting
        if(weight > rand.nextDouble()){
            if(weight < rand.nextDouble() / 2)
                return reduceJoinerCommunication();
            else
                return reduceSplitterCommunication();
        }
        // clean up an unnecessary fission
        if(filter == null || weight > rand.nextDouble()){
            filter = findUnnecessaryFission();
            if(filter == null)
                return true;
            ft = fissionTransformation.COPY_DEC;
        }else
            ft = fissionTransformation.COPY_INC;
        Integer[] original_ratio;
        if(fissionRatio.containsKey(filter))
            original_ratio = fissionRatio.get(filter);
        else{
            original_ratio = new Integer[1];
            original_ratio[0] = 1;
        }

        switch(ft){
            case COPY_INC:
                new_ratio = changeCopy(original_ratio, 1);
                break;
            case COPY_DEC:
                if (original_ratio.length == 1) {
                    break;
                }
                new_ratio = changeCopy(original_ratio, -1);
                break;
        }
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
        List<FilterInstance> filters;
        if(weight < rand.nextDouble()){
            filters = new ArrayList<>(evaluator.getBottleneckFilters().keySet());
            for(FilterInstance f: filters)
                if(f.isFissable())
                    if(f.isFissedNode)
                        return (FilterInstance)f.previousNode;
                    else
                        return f;
        }else{
            Map<FilterInstance, Integer> filterRanks = evaluator.getFiltersRank();
            filters = new ArrayList<>(filterRanks.keySet());
            for(FilterInstance f: filters)
                if(f.isFissable())
                    if(f.isFissedNode)
                        return (FilterInstance)f.previousNode;
                    else
                        return f;
            
        }
        return null;
    }
    
    private FilterInstance findUnnecessaryFission() {
        List<FilterInstance> filters;
        if(weight < rand.nextDouble()){
            Map<FilterInstance, Integer> filterRanks = evaluator.getFiltersRank();
            filters = new ArrayList<>(filterRanks.keySet());
            Collections.reverse(filters);
            for(FilterInstance f: filters)
                if(f.isFissedNode)
                    return (FilterInstance)f.previousNode;
        }else{
            filters = new ArrayList<>(evaluator.getBottleneckFilters().keySet());
            for(int i = 0; i < filters.size(); i++)
                for(int j = i+1; j < filters.size(); j++)
                    if(filters.get(i).isFissedNode && filters.get(j).isFissedNode)
                        if(filters.get(i).previousNode == filters.get(j).previousNode){
                            fissedFilter = filters.get(i);
                            return (FilterInstance)(filters.get(i).previousNode);
                        }
        }
        return null;
    }
    
    private boolean reduceJoinerCommunication(){
        List<FilterInstance> filters = StreamNode.globalGraph.InOrder();
        Map<FilterInstance, Integer[]> candidates = new LinkedHashMap<>();
        for(FilterInstance dst: filters){
            if(dst.isFissable() && (dst.pop >= dst.peek) && dst.join.size() > 1){
                Map<FilterInstance, AbstractChannel> incoming_edges = dst.inputs;
                double total_pushes = 0;
                Integer[] ratio = new Integer[incoming_edges.size()];
                ArrayList<FilterInstance> srces = new ArrayList<>(incoming_edges.keySet());
                for(int i = 0; i < srces.size(); i++){
                    FilterInstance src = srces.get(i);
                    int push = (int)incoming_edges.get(src).SteadyItemsPushPop();
                    total_pushes += push/(double)src.netSteady();
                    ratio[i] = push;
                }
                double total_pops;
                if(dst.isFissedNode){
                    int num = ((SplitJoin)(dst.parent)).children.size();
                    total_pops = (double)dst.pop / num;
                }else
                    total_pops = (double)dst.pop;
                if(total_pushes <= total_pops)
                    continue;
                ratio = simplifyRatio(ratio);
                if(dst.isFissedNode)
                    candidates.put((FilterInstance)dst.previousNode, ratio);
                else
                    candidates.put(dst, ratio);
            }
        }
        if(candidates.isEmpty())
            return false;
        List<FilterInstance> all = new ArrayList<>(candidates.keySet());
        filter = all.get(rand.nextInt(all.size()));
        new_ratio = candidates.get(filter);
        return finalizeChange();
    }
    
    private boolean reduceSplitterCommunication(){
        List<FilterInstance> filters = StreamNode.globalGraph.InOrder();
        Map<FilterInstance, Integer[]> candidates = new LinkedHashMap<>();
        outter: for(FilterInstance src: filters){
            if(src.isFissable() && src.split.size() > 1){
                Map<FilterInstance, AbstractChannel> outgoing_edges = src.outputs;
                double total_pops = 0;
                Integer[] ratio = new Integer[outgoing_edges.size()];
                ArrayList<FilterInstance> dsts = new ArrayList<>(outgoing_edges.keySet());
                for(int i = 0; i < dsts.size(); i++){
                    FilterInstance dst = dsts.get(i);
                    if(dst.peek > dst.pop)
                        continue outter;
                    int pop = (int)outgoing_edges.get(dst).SteadyItemsPushPop();
                    total_pops += pop/(double)dst.netSteady();
                    ratio[i] = pop;
                }
                double total_pushes;
                if(src.isFissedNode){
                    int num = ((SplitJoin)(src.parent)).children.size();
                    total_pushes = (double)src.push / num;
                }else
                    total_pushes = (double)src.push;
                if(total_pops <= total_pushes)
                    continue;
                ratio = simplifyRatio(ratio);
                if(src.isFissedNode)
                    candidates.put((FilterInstance)src.previousNode, ratio);
                else
                    candidates.put(src, ratio);
            }
        }
        if(candidates.isEmpty())
            return false;
        List<FilterInstance> all = new ArrayList<>(candidates.keySet());
        filter = all.get(rand.nextInt(all.size()));
        new_ratio = candidates.get(filter);
        return finalizeChange();
    }
    
    private Integer[] simplifyRatio(Integer[] ratio){
        Integer gcd = gcd(ratio[0], ratio[1]);
        if(ratio.length > 2)
            for(int y=2; y < ratio.length; y++)
                gcd = gcd(gcd, ratio[y]);
        Integer[] simple_ratio = new Integer[ratio.length];
        for(int i = 0; i < ratio.length; i++)
            simple_ratio[i] = ratio[i] / gcd;
        return simple_ratio;
    }
    
    private Integer gcd(Integer k, Integer m)
    {
        if (k <= 0 || m <= 0)
            throw new UnsupportedOperationException("gcd zero");

        while (!Objects.equals(k, m))
            if (k > m)
                k = k - m;
            else
                m = m - k;
        return k;
    }
}
