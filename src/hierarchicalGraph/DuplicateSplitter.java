package hierarchicalGraph;

import java.util.List;

public class DuplicateSplitter extends Splitter {

    public DuplicateSplitter() {
        pop = 1;
    }

    @Override
    public void setChildren(int count) {
    }

    @Override
    public int Push(int child) {
        return 1;
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

    @Override
    public void Flatten(List<StreamNode> children) {
        long mult = 1;
        int[] pos = new int[children.size()];
        for (int i = 0; i < children.size(); i++) {
            pos[i] = 0;
            mult = lcm(mult, children.get(i).in.length);
        }

        in = new FilterInstance[(int) mult][];
        for (int i = 0; i < mult; i++) {
            in[i] = new FilterInstance[0];
            for (int j = 0; j < children.size(); j++) {
                StreamNode child = children.get(j);
                in[i] = append(in[i], child.in[pos[j]]);
                pos[j] = (pos[j] + 1) % child.in.length;
            }
        }
    }

    @Override
    public long InitSchedule(long[] child_need_to_consume) {
        int needs_to_consume = 0;

        while (ItemsStillNeeded(child_need_to_consume)) {
            needs_to_consume++;
            for (int j = 0; j < child_need_to_consume.length; j++) {
                child_need_to_consume[j]--;
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
        DuplicateSplitter copyDuplicateSplitter = new DuplicateSplitter();
        super.clone(copyDuplicateSplitter);
        return copyDuplicateSplitter;
    }
}
