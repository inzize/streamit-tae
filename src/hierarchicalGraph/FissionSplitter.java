package hierarchicalGraph;

import java.util.List;

public class FissionSplitter extends Splitter {

    Integer[] weights;

    public FissionSplitter(Integer... weights) {
        this.weights = weights;
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
    public void setChildren(int count) {
        if (weights.length != count) {
            throw new UnsupportedOperationException("not enough weights");
        }

        pop = 0;
        for (Integer weight : weights) {
            pop += weight;
        }
    }

    private static FilterInstance[] append(FilterInstance[] a, FilterInstance[] b) {
        FilterInstance[] list = new FilterInstance[a.length + b.length];
        int i;
        for (i = 0; i < a.length; i++) {
            list[i] = a[i];
        }
        System.arraycopy(b, 0, list, i, b.length);
        return list;
    }

    private long getPeekMinusPop(StreamNode node) {
        long pk = node.peek;
        long pp = node.pop;
        return Math.max(0, pk - pp);
    }

    @Override
    public void Flatten(List<StreamNode> children) {
        Integer[] partten = new Integer[weights.length];
        int childpop = (int) children.get(0).pop;
        for (int i = 0; i < weights.length; i++) {
            partten[i] = weights[i] * childpop;
        }
        pop *= childpop;

        if (pop == 0) {
            in = new FilterInstance[0][];
        } else {
            in = new FilterInstance[(int) pop][];
            int x = 0;
            long y = getPeekMinusPop(children.get(0));
            long z = y;
            for (int j = 0; j < partten.length; j++) {
                for (int k = 0; k < partten[j]; k++) {
                    FilterInstance[][] child = children.get(j).in;
                    in[x] = child[k % child.length];
                    if (z > 0) {
                        in[x] = append(in[x], children.get((j - 1 + partten.length) % partten.length).in[0]);
                        z--;
                    }
                    x++;
                }
                z = y;
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
        Indent(indent, "<PeekingSplitter>");
    }

    @Override
    public Splitter clone() {
        FissionSplitter copyFissionSplitter = new FissionSplitter();
        super.clone(copyFissionSplitter);
        copyFissionSplitter.weights = this.weights.clone();
        return copyFissionSplitter;
    }
}
