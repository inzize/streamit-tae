package hierarchicalGraph;

import java.util.List;

public abstract class Joiner extends StreamConnector {

    private long original_pop;
    private long original_push;
    private long original_peek;
    private long original_mult;

    public abstract void setChildren(int count);

    public abstract long Pop(int child);

    public abstract void Flatten(List<StreamNode> children);

    public abstract long[] ChildrenNeed(long needs);

    @Override
    public abstract Joiner clone();
    
    public void clone(Joiner newCopy) {
        super.clone(newCopy);
        newCopy.original_pop = this.original_pop;
        newCopy.original_push = this.original_push;
        newCopy.original_peek = this.original_peek;
        newCopy.original_mult = this.original_mult;
    }

    public void backup() {
        original_pop = pop;
        original_push = push;
        original_peek = peek;
        original_mult = steadyMult;
    }

    public void reset() {
        pop = original_pop;
        push = original_push;
        peek = original_peek;
        steadyMult = original_mult;
    }

    @Override
    public long netPop() {
        throw new UnsupportedOperationException("Should call netPop(i) for joiner");
    }

    final public long netPop(int child) {
        return steadyMult * Pop(child);
    }

}
