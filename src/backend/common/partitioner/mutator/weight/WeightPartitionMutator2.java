package backend.common.partitioner.mutator.weight;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.PartitionMutator;
import hierarchicalGraph.StreamNode;
import java.util.Map;;

public class WeightPartitionMutator2 extends PartitionMutator{
    Evaluator evaluator;
    double weight;
    
    public WeightPartitionMutator2(Map<StreamNode, Processor> partition, Chip chip, Evaluator evaluator, double weight){
        super(partition, chip);
        this.evaluator = evaluator;
        this.weight = weight;
    }
    
    @Override
    public boolean mutate() {
        String type;
        if(rand.nextDouble() < 1)
            type = "work";
        else
            type = "memory";
        
        //give up after 10 times
        int counter = 10;
        while(target_processor == null){
            Processor proc = getLargeProcessor(evaluator, type);
            filter = getSmallFilter(evaluator, proc);
            if(filter == null)
                continue;
            target_processor = getSuitableProcessor(evaluator, filter);
            counter--;
            if(counter < 0)
                return false;
        }
        return finalizeChange();
    }
}
