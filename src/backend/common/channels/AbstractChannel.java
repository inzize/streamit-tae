package backend.common.channels;

import hierarchicalGraph.FilterInstance;
import ast.GeneratesCode;
import ast.expressions.Expression;
import backend.common.ChannelMessage;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import java.util.AbstractMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractChannel extends GeneratesCode {

    private final static Logger logger = Logger.getLogger("compiler");

    // Used by Latency Evaluator ...
    public int itemsAvailable;
    public long slotsAvailable;
    public java.util.Queue<ChannelMessage> sentMessages = new java.util.concurrent.LinkedBlockingQueue<>();
    public java.util.Queue<ChannelMessage> ackMessages = new java.util.concurrent.LinkedBlockingQueue<>();

    public FilterInstance src, dst;
    protected long bufferSize;
    public long numberOfBuffers;

    public AbstractChannel(FilterInstance src, FilterInstance dst) {
        this.src = src;
        this.dst = dst;
    }

    //public abstract String PopString();
    public String PopString() {
        assert !getBufferName(dst,false).isEmpty();
        
        return "(*(*" + getBufferName(dst, false) + ")++)";
    }

    public String PopString(boolean unique) {
        assert !getBufferName(dst, unique).isEmpty();
        if (unique) {
            return "(*" + getBufferName(dst, unique) + "++)";
        } else {
            return "(*(*" + getBufferName(dst, unique) + ")++)";
        }
    }
    
    //I think this should be abstract, but I did not create this function
    //I leave a runtimeexception here in case someone doesn't implement it
    public String getBufferName(FilterInstance f, boolean unique) {
        throw new RuntimeException("impl this !?!");
        //return "";
    }
    
    public void Pop(boolean unique) {
       writer.print(PopString(unique));
    }
            
    
    public void Pop() {
        String tmp = PopString();
        writer.print(tmp);
    }

    public void Peek(Expression arg) {
        writer.print("(*(*" + getBufferName(dst, false) + "+");
        arg.Print();
        writer.print("))");
    }
   
    public void Push(Expression arg, boolean semicolon) {
        writer.write("*(*" + getBufferName(src, false) + ")++ = (");
        arg.Print();
        writer.write(")");
        if (semicolon) {
            writer.writeLine(";");
        }
    }    

    public void Push(String arg, boolean semicolon) {
        writer.write("*(*" + getBufferName(src, false) + ")++ = (" + arg + ")");
        if (semicolon) {
            writer.writeLine(";");
        }
    }
    
    
    public void Push(String arg, boolean semicolon, boolean unique) {
        writer.write("*" + getBufferName(src, unique) + "++ = (" + arg + ")");
        if (semicolon) {
            writer.writeLine(";");
        }
    }

    public abstract void PopMethod();

    public abstract void PushMethod();

    public abstract void PeekMethod();

    public abstract void InitRead();

    public abstract void InitWrite();

    public abstract void ReadDecls();

    public abstract void WriteDecls();

    public abstract void GenerateInitWaitRead();

    public abstract void GenerateInitWaitWrite();

    public abstract void GenerateSteadyWaitRead();

    public abstract void GenerateSteadyWaitWrite();

    public abstract void GenerateInitDoneReading();

    public abstract void GenerateInitDoneWriting();

    public abstract void GenerateSteadyDoneReading();

    public abstract void GenerateSteadyDoneWriting();

    public abstract String getIdent();

    public abstract int getDoneReadCost(Chip chip);

    public abstract int getDoneWriteCost(Chip chip);

    public abstract int getWaitReadCost(Chip chip);

    public abstract int getWaitWriteCost(Chip chip);

    public abstract long TransmissionTime(CommunicationModel model);
    
    private long steadyItemsPushPopCache = Long.MAX_VALUE;
    
    public long SteadyItemsPushPop() {
        if (steadyItemsPushPopCache!=Long.MAX_VALUE)
            return steadyItemsPushPopCache;
        int len1 = dst.join.size();
        int dst_pops = 0;
        for (int i = 0; i < len1; i++) {
            if (dst.join.get(i % len1) == src) {
                dst_pops++;
            }
        }
        int channel_pops = (int) (dst.netSteady() * dst.pop * dst_pops / len1);
        int len2 = src.split.size();
        int src_pushes = 0;
        for (int i = 0; i < len2; i++) {
            for (FilterInstance d : src.split.get(i % len2)) {
                if (d == dst) {
                    src_pushes++;
                }
            }
        }
        int channel_pushes = (int) (src.netSteady() * src.push * src_pushes / len2);

        //check if dst filter is fissed peeking filter
        if (dst.isFissedNode && dst.isFissedPeekingFilter) {
            if (steadyItemsPushPopCache==Long.MAX_VALUE)
                steadyItemsPushPopCache = Math.max(channel_pops, channel_pushes);
            return Math.max(channel_pops, channel_pushes);
        }

        if (channel_pops == channel_pushes) {
            if (steadyItemsPushPopCache==Long.MAX_VALUE)
                steadyItemsPushPopCache = channel_pushes;
            return channel_pushes;
        } else {
            logger.log(Level.SEVERE, "src {0} push {1} steadyMult {2}", new Object[]{src, src.push, src.netSteady()});
            for (FilterInstance[] list : src.split) {
                logger.log(Level.SEVERE, "{");
                for (FilterInstance d : list) {
                    logger.log(Level.SEVERE, "{0},", d);
                }
                logger.log(Level.SEVERE, "} ");
            }

            logger.log(Level.SEVERE, "dst {0} pop {1} steadyMult {2}", new Object[]{dst, dst.pop, dst.netSteady()});
            for (FilterInstance s : dst.join) {
                logger.log(Level.SEVERE, "'{'{0}'}' ", s);
            }

            throw new RuntimeException("mismatch");
        }
    }

    public long SteadyItemsPeek() {
        return dst.peek - dst.pop + SteadyItemsPushPop();
    }

    public long InitItemsPop() {
        int channel_pops = 0;
        int len = dst.join.size();
        for (int i = 0; i < dst.pre_pop + dst.pop * dst.initMult; i++) {
            if (dst.join.get(i % len) == src) {
                channel_pops++;
            }
        }
        if (dst.isLastPeekingFilter) {
            channel_pops += dst.peek - dst.pop;
        }
        return channel_pops;
    }

    public long InitItemsPeek() {
        int channel_pop_ptr = 0;
        int dst_pop_ptr = 0;
        int max = 0;
        int len = dst.join.size();

        int channel_offset = 0;
        for (int peek = 0; peek < dst.pre_peek; peek++) {
            if (dst.join.get((dst_pop_ptr + peek) % len) == src) {
                channel_offset++;
            }
        }

        max = Math.max(max, channel_pop_ptr + channel_offset - 1);

        for (int k = 0; k < dst.pre_pop; k++) {
            dst_pop_ptr++;
            if (dst.join.get((dst_pop_ptr + k) % len) == src) {
                channel_pop_ptr++;
            }
        }

        for (int j = 0; j < dst.initMult; j++) {
            channel_offset = 0;
            for (int peek = 0; peek < dst.peek; peek++) {
                if (dst.join.get((dst_pop_ptr + peek) % len) == src) {
                    channel_offset++;
                }
            }

            max = Math.max(max, channel_pop_ptr + channel_offset - 1);

            for (int k = 0; k < dst.pop; k++) {
                dst_pop_ptr++;
                if (dst.join.get((dst_pop_ptr + k) % len) == src) {
                    channel_pop_ptr++;
                }
            }
        }
        return max;
    }

    public long InitItemsPush() {
        int channel_pushes = 0;
        int len = src.split.size();
        for (int i = 0; i < src.pre_push + (src.push * src.initMult); i++) {
            for (FilterInstance d : src.split.get(i % len)) {
                if (d == dst) {
                    channel_pushes++;
                }
            }
        }
        return channel_pushes;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    abstract public long getChannelMemSize();

    /**
     * Return memory required on source side of channel
     *
     * @return memory in bytes
     */
    public long getMemUsedSrc() {
        return 0;
    }

    /**
     * Return memory required on destination side of channel
     *
     * @return memory in bytes
     */
    public long getMemUsedDst() {
        return 0;
    }

    /**
     * Return memory required in shared memory
     *
     * @return memory in bytes at sender and receiver
     */
    public AbstractMap.SimpleEntry<Integer, Integer> getMemUsedShared() {
        return new AbstractMap.SimpleEntry<>(0, 0);
    }

    public void destructChannel() {
    }

    /**
     * Return workload of the channel for one steady state
     *
     * @return workload in bytes
     */
    public long getWorkload() {
        return src.filter.type.output.SizeOf(null) * getBufferSize();
    }
}
