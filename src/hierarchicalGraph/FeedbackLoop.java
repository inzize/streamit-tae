package hierarchicalGraph;

import java.util.*;

public class FeedbackLoop extends StreamContainer {

    public StreamNode body = null; // Fixme: defaults to identify if not specified
    public StreamNode loop = null; // Fixme: defaults to identify if not specified
    public List<Object> enqueued = new ArrayList<>();
    public Splitter splitter;
    public Joiner joiner;

    @Override
    public void SteadySchedule(StreamNode parent) {
        this.parent = parent;
        joiner.setChildren(2);
        splitter.setChildren(2);

        body.SteadySchedule(this);
        loop.SteadySchedule(this);

        // Match output of Joiner with input of each body
        long previousSend = joiner.netPush();
        long nextReceive = body.netPop();
        if (previousSend != nextReceive) {
            long gcd = gcd(previousSend, nextReceive);
            previousSend /= gcd;
            nextReceive /= gcd;

            joiner.steadyMult *= nextReceive;
            body.steadyMult *= previousSend;
        }

        // Match output of body with input of Splitter
        previousSend = body.netPush();
        nextReceive = splitter.netPop();
        if (previousSend != nextReceive) {
            long gcd = gcd(previousSend, nextReceive);
            previousSend /= gcd;
            nextReceive /= gcd;

            body.steadyMult *= nextReceive;
            splitter.steadyMult *= previousSend;
        }

        // Match output of Splitter with input of Loop
        previousSend = splitter.netPush(1);
        nextReceive = loop.netPop();
        if (previousSend != nextReceive) {
            long gcd = gcd(previousSend, nextReceive);
            previousSend /= gcd;
            nextReceive /= gcd;

            splitter.steadyMult *= nextReceive;
            loop.steadyMult *= previousSend;
        }

        //Fixme: check output of Loop matches input of joiner.
        this.pop = joiner.netPop(0);
        this.push = splitter.netPush(0);
    }

    @Override
    protected void InOrder(List<FilterInstance> list) {
        body.InOrder(list);
        loop.InOrder(list);
    }

    @Override
    public void Flatten() {
        // Fixme
        throw new UnsupportedOperationException("Feedback loops not supported yet.");
    }

    @Override
    public long InitSchedule(long needed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void Dump(int indent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void SetParent(StreamNode parent) {

    }

    @Override
    public void ReverseChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void BackupChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StreamContainer clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
