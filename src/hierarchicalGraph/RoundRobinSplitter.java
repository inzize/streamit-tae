package hierarchicalGraph;

import java.util.List;

public class RoundRobinSplitter extends Splitter {

    Integer[] weights;

    public RoundRobinSplitter(Integer... weights) {
        this.weights = weights;
        pop = 0;
        for (int i = 0; i < weights.length; i++) {
            pop += weights[i];
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

        pop = 0;
        for (Integer weight : weights) {
            pop += weight;
        }
    }

    @Override
    public int Push(int child) {
        return weights[child];
    }

    @Override
    public void Flatten(List<StreamNode> children) {
        if (pop == 0) {
            in = new FilterInstance[0][];
        } else {
            long mult = 1;
            int[] pos = new int[children.size()];
            for (int i = 0; i < children.size(); i++) {
                pos[i] = 0;
                if (weights[i] > 0) {
                    mult = lcm(mult * weights[i], children.get(i).in.length) / weights[i];
                }
            }

            in = new FilterInstance[(int) (mult * pop)][];
            int x = 0;
            for (int i = 0; i < mult; i++) {
                for (int j = 0; j < weights.length; j++) {
                    for (int k = 0; k < weights[j]; k++) {
                        FilterInstance[][] child = children.get(j).in;
                        in[x++] = child[pos[j]];
                        pos[j] = (pos[j] + 1) % child.length;
                    }
                }
            }
        }
    }

    @Override
    public long InitSchedule(long[] child_need_to_consume) {
        int needs_to_consume = 0;
        while (ItemsStillNeeded(child_need_to_consume)) {
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i]; j++) {
                    if (ItemsStillNeeded(child_need_to_consume)) {
                        needs_to_consume++;
                        child_need_to_consume[i]--;
                    } else {
                        return needs_to_consume;
                    }
                }
            }
        }

        return needs_to_consume;
    }

    @Override
    public void Dump(int indent) {
        Indent(indent, "<RoundRobinSplitter>");
    }

    @Override
    public Splitter clone() {
        RoundRobinSplitter copyRoundRobinSplitter = new RoundRobinSplitter();
        super.clone(copyRoundRobinSplitter);
        copyRoundRobinSplitter.weights = this.weights.clone();
        return copyRoundRobinSplitter;
    }
}
