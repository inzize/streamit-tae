package hierarchicalGraph;

import java.util.List;

public class FissionJoiner extends Joiner {

    Integer[] weights;

    public FissionJoiner(Integer... weights) {
        this.weights = weights;
        push = 0;
        for (Integer weight : weights) {
            push += weight;
        }
    }

    @Override
    public void setChildren(int count) {
        if (weights.length == 0) {
            weights = new Integer[count];
            for (int i = 0; i < count; i++) {
                weights[i] = 1;
            }
        } else if (weights.length == 1) {
            int weight = weights[0];
            weights = new Integer[count];
            for (int i = 0; i < count; i++) {
                weights[i] = weight;
            }
        } else if (weights.length != count) {
            throw new UnsupportedOperationException("not enough weights");
        }

        push = 0;
        for (int i = 0; i < weights.length; i++) {
            push += weights[i];
        }
    }

    @Override
    public long Pop(int child) {
        return weights[child];
    }

    @Override
    public void Flatten(List<StreamNode> children) {
        Integer[] partten = new Integer[weights.length];
        int childpush = (int) children.get(0).push;
        for (int i = 0; i < weights.length; i++) {
            partten[i] = weights[i] * childpush;
        }
        push *= childpush;

        if (push == 0) {
            out = new FilterInstance[0];
        } else {
            out = new FilterInstance[(int) push];
            int x = 0;
            for (int j = 0; j < partten.length; j++) {
                for (int k = 0; k < partten[j]; k++) {
                    FilterInstance[] child = children.get(j).out;
                    out[x++] = child[k % child.length];
                }
            }
        }
    }

    @Override
    public long[] ChildrenNeed(long needs) {
        long[] children_need = new long[weights.length];

        for (int i = 0; i < weights.length; i++) {
            children_need[i] = 0;
        }

        for (int n = 0; n < needs; n++) {
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i]; j++) {
                    children_need[i]++;
                }
            }
        }

        return children_need;
    }

    @Override
    public void Dump(int indent) {
        Indent(indent, "<RoundRobinJoiner>");
    }

    @Override
    public void reset() {
        pop = 0;
        peek = 0;
        push = 0;
    }

    @Override
    public Joiner clone() {
        FissionJoiner copyFissionJoiner = new FissionJoiner();
        super.clone(copyFissionJoiner);
        copyFissionJoiner.weights = this.weights.clone();
        return copyFissionJoiner;
    }
}
