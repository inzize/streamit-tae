package hierarchicalGraph;

import java.util.*;

public class Pipeline extends StreamContainer {

    private StreamNode FirstChild() {
        return children.get(0);
    }

    private StreamNode LastChild() {
        return children.get(children.size() - 1);
    }

    @Override
    public void SteadySchedule(StreamNode parent) {
        this.parent = parent;
        for (StreamNode child : children) {
            child.SteadySchedule(this);
        }

        for (int i = 1; i < children.size(); i++) {
            StreamNode previous = children.get(i - 1);
            StreamNode next = children.get(i);
            long previousSend = previous.netPush();
            long nextReceive = next.netPop();
            if (previousSend != nextReceive) {
                long gcd = gcd(previousSend, nextReceive);
                previousSend /= gcd;
                nextReceive /= gcd;

                for (int j = 0; j < i; j++) {
                    children.get(j).steadyMult *= nextReceive;
                }
                next.steadyMult *= previousSend;
            }
        }
        this.pop = FirstChild().netPop();
        this.push = LastChild().netPush();
    }

    @Override
    protected void InOrder(List<FilterInstance> list) {
        for (StreamNode child : children) {
            child.InOrder(list);
        }
    }

    private static void connect(FilterInstance[] from, FilterInstance[][] to) {
        long mult = lcm(from.length, to.length);

        int src_pos = 0, dst_pos = 0;
        for (int i = 0; i < mult; i++) {
            FilterInstance src = from[src_pos];
            src_pos = (src_pos + 1) % from.length;

            FilterInstance[] dst = to[dst_pos];
            dst_pos = (dst_pos + 1) % to.length;

            src.split.add(dst);

            for (FilterInstance dest : dst) {
                dest.join.add(src);
            }
        }
    }

    @Override
    public void Flatten() {
        for (StreamNode child : children) {
            child.Flatten();
        }

        for (int i = 1; i < children.size(); i++) {
            StreamNode prev = children.get(i - 1);
            StreamNode next = children.get(i);
            connect(prev.out, next.in);
        }

        in = FirstChild().in;
        out = LastChild().out;
    }

    @Override
    public long InitSchedule(long needed) {
        for (int i = children.size() - 1; i >= 0; i--) {
            StreamNode child = children.get(i);
            needed = child.InitSchedule(needed);
        }
        return needed;
    }

    @Override
    public void Dump(int indent) {
        Indent(indent, "pipeline");
        for (StreamNode child : children) {
            child.Dump(indent + 1);
        }
    }

    @Override
    public void SetParent(StreamNode parent) {
        this.parent = parent;
        for (StreamNode child : children) {
            child.SetParent(this);
        }
    }

    @Override
    public void ReverseChildren() {
        steadyMult = 1;
        in = null;
        out = null;
        pop = 0;
        peek = 0;
        push = 0;
        for (StreamNode child : children) {
            child.ReverseChildren();
        }
    }

    @Override
    public void BackupChildren() {
        for (StreamNode child : children) {
            child.BackupChildren();
        }
    }

    @Override
    public StreamContainer clone() {
        Pipeline copyPipeline = new Pipeline();
        copyPipeline.isFusedContainer = this.isFusedContainer;
        if (isFusedContainer) {
            copyPipeline.fused_container_id = StreamContainer.next_fused_container_id++;
        }
        super.clone(copyPipeline);
        return copyPipeline;
    }
}
