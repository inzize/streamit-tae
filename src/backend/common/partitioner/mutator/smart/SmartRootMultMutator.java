package backend.common.partitioner.mutator.smart;

import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.RootMultMutator;
import hierarchicalGraph.FilterInstance;

public class SmartRootMultMutator extends RootMultMutator{
    Evaluator evaluator;
    
    public SmartRootMultMutator(Evaluator evaluator){
        this.evaluator = evaluator;
    }

    @Override
    public boolean mutate() {
        boolean unfissable = true;
        for(FilterInstance filter: evaluator.getBottleneckFilters().keySet())
            if(filter.isFissable())
                unfissable = false;

        switch (unfissable ? 0 : 1){
            case 0:
                rootMult = (int)Math.ceil(rootMult * (rand.nextDouble() + 1));
                break;
            case 1:
                rootMult = (int)Math.ceil(rootMult / (rand.nextDouble() + 1));
                if(rootMult < 1)
                    rootMult = 1;
                break;
        }
        if(finalizeChange())
            return true;
        return false;
    }
}
