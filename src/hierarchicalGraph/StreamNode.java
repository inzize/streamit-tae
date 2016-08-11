package hierarchicalGraph;

import backend.common.estimators.Estimator;
import streamit2.Compiler;
import java.util.*;

public abstract class StreamNode extends StreamConnector implements Comparable {

    public static int next_streamnode_id = 0;
    public int streamnode_id = next_streamnode_id++;
    public static Estimator estimator;

    public StreamNode parent;

    public abstract void SteadySchedule(StreamNode parent);

    public abstract long InitSchedule(long needed);

    public abstract void Flatten();

    public abstract void SetParent(StreamNode parent);

    public abstract void ReverseChildren();

    public abstract void BackupChildren();

    public abstract boolean isFissable();

    public abstract boolean isStateless();

    public abstract boolean isPeeking();

    /**
     * @return final filter name including fission suffix
     */
    public abstract String StreamNodeName();

    public abstract List<StreamNode> InitFissedNodes(Integer[] ratio);

    public static StreamNode globalGraph;
    private static int rootMult = 1;
    public static int originalNextFilterId;
    public long originalNetSteady = 1;

    private static final double communicationThreshold = 0.9;

    public boolean isFissedNode = false;
    public StreamNode previousNode;
    private static boolean fixRootMult = false;

    public static void fixRootMult(int mult) {
        fixRootMult = true;
        rootMult = mult;
    }

    public static boolean setRootMult(int mult) {
        if (fixRootMult) {
            return false;
        }
        int tmpMult = rootMult;
        rootMult = mult;
        if ((getRootMult() > Compiler.maxMult) || mult < 1) {
            rootMult = tmpMult;
            return false;
        }
        return true;
    }

    public static int getRootMult() {
        return rootMult;
    }

    public static long getGlobalMult() {
        FilterInstance output_filter;
        if (globalGraph instanceof StreamContainer) {
            StreamContainer sc = (StreamContainer) globalGraph;
            output_filter = (FilterInstance) sc.children.get(sc.children.size() - 1);
        } else {
            output_filter = (FilterInstance) globalGraph;
        }
        assert Compiler.Last(globalGraph.InOrder()) == output_filter;
        long globalMult = output_filter.netSteady() / output_filter.originalNetSteady;
        return globalMult;
    }

    public void clone(StreamNode newCopy) {
        super.clone(newCopy);
        newCopy.isFissedNode = true;
        newCopy.parent = this.parent;
        newCopy.previousNode = this;
    }

    public String DumpStats() {
        return " pop=" + pop + ",push=" + push + ",peek=" + peek + ",netSteady=" + netSteady();
    }

    public List<FilterInstance> InOrder() {
        List<FilterInstance> list = new ArrayList<>();
        InOrder(list);
        return list;
    }

    protected void InOrder(List<FilterInstance> list) {
    }

    public List<StreamNode> getFissables() {
        List<StreamNode> list = new ArrayList<>();
        getFissables(list);
        return list;
    }

    protected void getFissables(List<StreamNode> list) {
    }

    /*netSteady -> #workfunction calls per steady state (includes rootMult, steadyMult and fission)*/
    final public long netSteady() {
        if (parent != null) {
            return steadyMult * parent.netSteady();
        } else {
            return steadyMult * rootMult;
        }
    }

    public static long div_ceiling(long a, long b) {
        return (a + b - 1) / b;
    }

    public static void FuseAllStatelessNodes() {
        if (globalGraph instanceof StreamContainer) {
            ((StreamContainer) globalGraph).fuseAllStateless();
        }
    }

    public void FissNode(Integer[] ratio) {
        FissNode(ratio.length);
    }

    public void FissNodeStrict(Integer[] ratio) {
        if (!isFissable()) {
            return;
        }
        SplitJoin newNode = new SplitJoin();
        Integer[] copyRatio = ratio.clone();
        newNode.splitter = CreateSplitter(copyRatio);
        newNode.joiner = CreateJoiner(copyRatio);
        newNode.children = InitFissedNodes(copyRatio);
        newNode.previousNode = this;
        newNode.isFissedNode = true;
        StreamContainer par = (StreamContainer) this.parent;
        par.children.set(par.children.indexOf(this), newNode);

        globalGraph.SetParent(null);

        List<FilterInstance> filters = globalGraph.InOrder();
        reNumberFilters(filters);
    }

    public void FissNode(int copies) {
        Integer[] ratio = getRatio(copies);
        FissNodeStrict(ratio);
    }

    private static void reNumberFilters(List<FilterInstance> filters) {
        for (int i = 0; i < filters.size(); i++) {
            filters.get(i).filter_id = i;
        }
    }

    private Splitter CreateSplitter(Integer[] ratio) {
        int totalItems = 0;
        for (Integer r : ratio) {
            totalItems += r;
        }
        if (peek > pop) {
            double communicationRate = (double) (peek - pop) * ratio.length / (totalItems * pop);
            if (communicationRate > communicationThreshold) {
                int Mult = (int) Math.ceil((peek - pop) * ratio.length / (communicationThreshold * totalItems * pop));
                for (int i = 0; i < ratio.length; i++) {
                    ratio[i] *= Mult;
                }
            }
        }
        Splitter splitter = new FissionSplitter(ratio);
        return splitter;
    }

    private Joiner CreateJoiner(Integer[] ratio) {
        Joiner joiner = new FissionJoiner(ratio);
        return joiner;
    }

    public static void backupGraph(StreamNode graph) {
        originalNextFilterId = FilterInstance.next_filter_id;
        globalGraph = graph;
        globalGraph.BackupChildren();
    }

    public static void saveGraphNetSteady() {
        globalGraph.saveNetSteady();
    }

    public void saveNetSteady() {
        originalNetSteady = this.netSteady();
    }

    public Integer[] getRatio(int copies) {
        if (isFissedNode) {
            originalNetSteady = ((SplitJoin) (this.parent)).previousNode.originalNetSteady;
        }
        Integer[] ratio = new Integer[copies];
        if (originalNetSteady % copies == 0) {
            for (int i = 0; i < copies; i++) {
                ratio[i] = 1;
            }
        } else {
            double mult = Math.ceil((double) copies / originalNetSteady);
            long ns = originalNetSteady * (int) mult;
            int base = (int) (ns / copies);
            for (int i = 0; i < copies; i++) {
                ratio[i] = base;
            }
            for (int i = 0; i < ns % copies; i++) {
                ratio[i] += 1;
            }
        }
        return ratio;
    }

    public static void restoreGraph() {
        FilterInstance.next_filter_id = originalNextFilterId;
        globalGraph.ReverseChildren();
    }

    @Override
    public int compareTo(Object t) {
        if (!(t instanceof StreamNode)) {
            throw new RuntimeException("Not Comparable");
        }
        final StreamNode other = (StreamNode) t;
        return Integer.compare(streamnode_id, other.streamnode_id);
    }
}
