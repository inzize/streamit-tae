package backend.common.partitioner.mutator.random;

import backend.common.partitioner.mutator.RootMultMutator;

public class RandomRootMultMutator extends RootMultMutator{
    
    public RandomRootMultMutator(){
        
    }

    @Override
    public boolean mutate() {
        while(!finalizeChange()){
            switch ((int)(rand.nextDouble() * 2)){
                case 0:
                    rootMult = (int)Math.ceil(rootMult * (rand.nextDouble() + 1));
                    break;
                case 1:
                    rootMult = (int)Math.ceil(rootMult / (rand.nextDouble() + 1));
                    if(rootMult < 1)
                        rootMult = 1;
                    break;
            }
        }
        return true;
    }
}
