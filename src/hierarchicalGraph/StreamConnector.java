package hierarchicalGraph;

import ast.GeneratesCode;
import java.io.PrintStream;

public abstract class StreamConnector extends GeneratesCode {

    public long steadyMult = 1;
    public long pop, push, peek;
    public FilterInstance[][] in;
    public FilterInstance[] out;
    public static PrintStream qut_graph;

    public abstract void Dump(int indent);

    public void clone(StreamConnector newCopy) {
        newCopy.steadyMult = this.steadyMult;
        newCopy.pop = this.pop;
        newCopy.push = this.push;
        newCopy.peek = this.peek;
        if (this.in != null) {
            newCopy.in = this.in.clone();
        }
        if (this.out != null) {
            newCopy.out = this.out.clone();
        }
    }

    public void Indent(int indent, String msg) {
        for (int i = 0; i < indent; i++) {
            qut_graph.print("  ");
        }
        qut_graph.println(msg);
    }

    public long netPop() {
        return steadyMult * pop;
    }

    public long netPush() {
        return steadyMult * push;
    }

    static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    static long gcd(long k, long m) {
        if (k <= 0 || m <= 0) {
            throw new UnsupportedOperationException("gcd zero");
        }

        while (k != m) {
            if (k > m) {
                k = k - m;
            } else {
                m = m - k;
            }
        }
        return k;
    }
}
