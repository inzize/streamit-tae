package backend.common.partitioner.mutator;

import hierarchicalGraph.StreamNode;

public abstract class RootMultMutator extends Mutator{
    protected int rootMult = StreamNode.getRootMult();
    protected int mult = 0;
    
    @Override
    public boolean finalizeChange(){
        if(rootMult == StreamNode.getRootMult())
            return false;
        else{
            StreamNode.setRootMult(rootMult);
            return true;
        }
    }
}
