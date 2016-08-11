package hierarchicalGraph;

import java.util.List;

public class RoundRobinJoiner extends Joiner {

    Integer[] weights;

    public RoundRobinJoiner(Integer... weights) {
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
        for (Integer weight : weights) {
            push += weight;
        }
    }

    @Override
    public long Pop(int child) {
        return weights[child];
    }

    @Override
    public void Flatten(List<StreamNode> children) {
        if (push == 0) {
            out = new FilterInstance[0];
        } else {
            long mult = 1;
            int[] pos = new int[children.size()];
            for (int i = 0; i < children.size(); i++) {
                pos[i] = 0;
                if (weights[i] > 0) {
                    mult = lcm(mult * weights[i], children.get(i).out.length) / weights[i];
                }
            }

            out = new FilterInstance[(int) (mult * push)];
            int x = 0;
            for (int i = 0; i < mult; i++) {
                for (int j = 0; j < weights.length; j++) {
                    for (int k = 0; k < weights[j]; k++) {
                        FilterInstance[] child = children.get(j).out;
                        out[x++] = child[pos[j]];
                        pos[j] = (pos[j] + 1) % child.length;
                    }
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
    public Joiner clone() {
        RoundRobinJoiner copyRoundRobinJoiner = new RoundRobinJoiner();
        super.clone(copyRoundRobinJoiner);
        copyRoundRobinJoiner.weights = this.weights.clone();
        return copyRoundRobinJoiner;
    }
}
