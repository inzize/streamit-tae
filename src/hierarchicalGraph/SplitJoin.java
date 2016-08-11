package hierarchicalGraph;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplitJoin extends StreamContainer {

    private final static Logger logger = Logger.getLogger("compiler");

    public Splitter splitter = null;
    public Joiner joiner = null;

    @Override
    public void SteadySchedule(StreamNode parent) {
        this.parent = parent;
        splitter.setChildren(children.size());
        joiner.setChildren(children.size());

        for (StreamNode child : children) {
            child.SteadySchedule(this);
        }

        // Match output of Splitter with input of each child
        for (int i = 0; i < children.size(); i++) {
            StreamNode next = children.get(i);
            long previousSend = splitter.netPush(i);
            long nextReceive = next.netPop();
            if (nextReceive == 0) {
                next.steadyMult *= splitter.steadyMult;
            }
            if (previousSend != nextReceive) {
                if (nextReceive == 0) {
                    logger.log(Level.WARNING, "{0}", i);
                }
                long gcd = gcd(previousSend, nextReceive);
                previousSend /= gcd;
                nextReceive /= gcd;

                splitter.steadyMult *= nextReceive;
                for (int j = 0; j < i; j++) {
                    children.get(j).steadyMult *= nextReceive;
                }
                next.steadyMult *= previousSend;
            }
        }

        // Match output of arbitrary (first) child with input of Joiner
        StreamNode previous = children.get(0);
        long previousSend = previous.netPush();
        Joiner next = this.joiner;
        long nextReceive = next.netPop(0);
        if (previousSend != nextReceive) {
            long gcd = gcd(previousSend, nextReceive);
            previousSend /= gcd;
            nextReceive /= gcd;

            splitter.steadyMult *= nextReceive;
            for (StreamNode children1 : children) {
                children1.steadyMult *= nextReceive;
            }
            next.steadyMult *= previousSend;
        }

        this.pop = splitter.netPop();
        this.push = joiner.netPush();
    }

    @Override
    protected void InOrder(List<FilterInstance> list) {
        for (StreamNode child : children) {
            child.InOrder(list);
        }
    }

    @Override
    public void Flatten() {
        for (StreamNode child : children) {
            child.Flatten();
        }

        joiner.Flatten(children);
        out = joiner.out;

        splitter.Flatten(children);
        in = splitter.in;
    }

    @Override
    public long InitSchedule(long needs_to_produce) {
        long[] child_need_to_produce = joiner.ChildrenNeed(needs_to_produce);
        long[] child_need_to_consume = new long[child_need_to_produce.length];

        for (int i = 0; i < children.size(); i++) {
            child_need_to_consume[i] = children.get(i).InitSchedule(child_need_to_produce[i]);
        }

        return splitter.InitSchedule(child_need_to_consume);
    }

    @Override
    public void Dump(int indent) {
        Indent(indent, "splitjoin");
        for (StreamNode node : children) {
            node.Dump(indent + 1);
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
        if (isFissedNode) {
            ((StreamContainer) this.parent).children.set(((StreamContainer) this.parent).children.indexOf(this), previousNode);
            previousNode.ReverseChildren();
        } else {
            splitter.reset();
            joiner.reset();
            steadyMult = 1;
            in = null;
            out = null;
            splitter.in = null;
            splitter.out = null;
            joiner.in = null;
            joiner.out = null;
            for (StreamNode child : children) {
                child.ReverseChildren();
            }
        }
    }

    @Override
    public void BackupChildren() {
        splitter.backup();
        joiner.backup();
        for (StreamNode child : children) {
            child.BackupChildren();
        }
    }

    @Override
    public StreamContainer clone() {
        SplitJoin copySplitJoin = new SplitJoin();
        super.clone(copySplitJoin);
        copySplitJoin.splitter = this.splitter.clone();
        copySplitJoin.joiner = this.joiner.clone();
        return copySplitJoin;
    }
}
