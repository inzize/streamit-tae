package backend.common.partitioner.mutator.manual;

import backend.common.partitioner.mutator.RootMultMutator;

public class ManualRootMultMutator extends RootMultMutator{
    
    public ManualRootMultMutator(int mult){
        this.mult = mult;
    }

    @Override
    public boolean mutate() {
        rootMult = mult;
        if(finalizeChange())
            return true;
        return false;
    }
}
