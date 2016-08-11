package hierarchicalGraph;

import java.util.List;

public abstract class Splitter extends StreamConnector {

    private long original_pop;
    private long original_push;
    private long original_peek;
    private long original_mult;

    public abstract int Push(int child);

    public abstract void setChildren(int count);

    public abstract void Flatten(List<StreamNode> children);

    public abstract long InitSchedule(long[] child_need_to_consume);

    @Override
    public abstract Splitter clone();
    
    public void clone(Splitter newCopy) {
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

    protected boolean ItemsStillNeeded(long[] child_need_to_consume) {
        for (int j = 0; j < child_need_to_consume.length; j++) {
            if (child_need_to_consume[j] > 0) {
                return true;
            }
        }
        return false;
    }

    final public long netPush(int child) {
        return steadyMult * Push(child);
    }

    public long netPush() {
        throw new UnsupportedOperationException("Should call netPush(i) for splitter");
    }
}
