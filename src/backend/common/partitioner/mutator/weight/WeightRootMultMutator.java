package backend.common.partitioner.mutator.weight;

import backend.common.evaluators.Evaluator;
import backend.common.partitioner.mutator.RootMultMutator;

public class WeightRootMultMutator extends RootMultMutator{
    Evaluator evaluator;
    double weight;
    
    public WeightRootMultMutator(Evaluator evaluator, double weight){
        this.evaluator = evaluator; 
        this.weight = weight;
    }

    @Override
    public boolean mutate() {
        boolean increase = false;
        if(rand.nextDouble() > getMaxMemUsageRate(evaluator))
            increase = true;
            

        switch (increase ? 0 : 1){
            case 0:
                rootMult++;
                break;
            case 1:
                rootMult--;
                if(rootMult < 1)
                    rootMult = 1;
                break;
        }
        return finalizeChange();
    }
}
