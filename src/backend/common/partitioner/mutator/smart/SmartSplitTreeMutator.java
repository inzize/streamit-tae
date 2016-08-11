package backend.common.partitioner.mutator.smart;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.estimators.Estimator;
import backend.common.evaluators.legacy.ChannelEvaluator;
import backend.common.evaluators.legacy.CommunicationEvaluator;
import static backend.common.evaluators.EvaluationStats.PartitionBottleneck.FILTER;
import static backend.common.evaluators.EvaluationStats.PartitionBottleneck.LINK;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.evaluators.MemoryEvaluator;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.GreedyPartitioner;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.Mutator;
import backend.coreVA.CoreVABackend;
import static backend.coreVA.CoreVABackend.evaluators;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.SplitTreeStreamNode;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class SmartSplitTreeMutator extends Mutator {

    StreamNode sn = StreamNode.globalGraph;
    ThroughputEvaluator mte;
    List<FilterInstance> filterList;
     SortedMap<StreamNode, Processor> part;
    
    @Deprecated
    public SmartSplitTreeMutator(List<FilterInstance> filters, Chip chip, Estimator est) {
        mte = new ThroughputEvaluator(chip);
        evaluators = new ArrayList<>();
        Evaluator memEvaluator = new MemoryEvaluator();
        Evaluator chanEvaluator = new ChannelEvaluator();
        Evaluator comEvaluator = new CommunicationEvaluator();
        evaluators.add(memEvaluator);
        evaluators.add(chanEvaluator);
        evaluators.add(comEvaluator);
        evaluators.add(mte);
        assert mte != null;
        filterList = filters;
        for (int i=0;i<2;i++) {
            SortedMap<FilterInstance, Processor> tmp = new GreedyPartitioner().createPartition(filters, chip, est, false).getFilterMapping();
            Mutation.hillClimbMult(evaluators, filters, Mutation.convertPartition(tmp));
            CoreVABackend.BuildChannels(tmp);
            mte.evaluate(tmp);
            boolean changed=false;
            assert tmp.get(mte.getBottleneckFilters().keySet().toArray()[0])==mte.getBusyProcessor();
            for (FilterInstance f : mte.getBottleneckFilters().keySet()) {
                if (f.split.size()>0 && f.split.get(0).length>2) {
                    try {
                        SplitTreeStreamNode newSplitFilter = new SplitTreeStreamNode(f);                    
                        filters.add(newSplitFilter);
                        //TODO: Rebuild Channels?
                        changed=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("(Ignored)");
                        continue;
                    }
                    break;
                }
            }
            if (!changed) return;
        }        
    }
     
    public SmartSplitTreeMutator(List<FilterInstance> fList, SortedMap<StreamNode, Processor> part) {
        mte = new ThroughputEvaluator();
        assert mte != null;
        filterList = fList;
        this.part = part;
        mte.evaluate(part);
    }
    
    @Override
    public boolean mutate() {
        switch (mte.getLastStats().bottleneck) {
            case FILTER:
                Map<FilterInstance,Integer> bottleneck = mte.getBottleneckFilters();
/*                for (FilterInstance filter : bottleneck.keySet()) {
                    assert bottleneck.get(filter)==bottleneck.values().toArray()[0];
                }*/
                int targetFilterId = 0;//rand.nextInt(bottleneck.size());
                FilterInstance targetFilter = (FilterInstance) bottleneck.keySet().toArray()[targetFilterId];
                if (targetFilter.split.size()==0)
                    return false; //Filter has no split
                SplitTreeStreamNode newSplitFilter = null;//new IdentityStreamNode(targetFilter);
                filterList.add(newSplitFilter);
                assert !newSplitFilter.equals(targetFilter);
                assert newSplitFilter.compareTo(targetFilter)!=0;
                assert !part.containsKey(newSplitFilter);
                Object tmp = part.put(newSplitFilter, mte.getIdleProcessor());
                System.out.print(tmp);
                break;
            case LINK:
                System.out.println("Link is bottleneck!?");
                break;
            default:
                throw new RuntimeException("Not Implemented!");
        }
        return true;
    }

    @Override
    public boolean finalizeChange() {
        return true;
    }
    
}
