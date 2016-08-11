package backend.common.buffers;

import static ast.GeneratesCode.writer;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import backend.common.estimators.BufferEstimator;
import hierarchicalGraph.FilterInstance;

public class SimpleMergeSplitBuffer extends AbstractMergeSplitBuffer {
    //TODO: remove these values
    private static final int loopOverhead = 9;
    private static final int mergingOverhead = 3;
    private static final int assignOverhead = 1;
    private static final int tempOverhead = 2;

    public SimpleMergeSplitBuffer(FilterInstance src, FilterInstance dst) {
        super(src, dst);
    }
       
    @Override
    public String getBufferName(FilterInstance f, boolean unique) {
        String ret = "";
        
            if (src != null && src.equals(f)) {
            ret = bufferNamePtrHead;
        } else if (dst != null && dst.equals(f)) {
            ret = bufferNamePtrTail;
        } else if(src != null) {
            ret = bufferNamePtrTail;
        } else if(dst != null) {
            ret = bufferNamePtrHead;
        }
        
        if(!unique) {
            ret = ret.replace(getIdent(), "");
        }
        
        return ret;
    }
    
    @Override
    public void GenerateInitWaitRead() {
        writer.writeLine("/* InitWaitRead : " + this.getClass().getSimpleName() + " */");  
        writer.writeLine(type.CType() + "* " + bufferNamePtrHead + "=" + bufferName + ";");
        long iterations = (int) Math.ceil((double) dst.initMult / dst.join.size());
        writer.writeLine("// generated MERGE");
        writer.startBlock("for (unsigned int _i=0; _i<" + iterations + ";_i++)");
        for (FilterInstance f : dst.join) {
            writer.write("");
            Push(dst.inputs.get(f).PopString(true), true, true);
        }
        writer.endBlock();
        writer.writeLine(type.CType() + "* " + bufferNamePtrTail + "=" + bufferName + ";");        
    }

    @Override
    public void GenerateInitWaitWrite() {
//        assert BufferType.SPLIT == bufferType;     
//        writer.writeLine("/* InitWaitWrite : " + this.getClass().getSimpleName() + " */");        
        writer.writeLine(type.CType() + "* " + bufferNamePtrHead + "=" + bufferName + ";"); 
    }

    @Override
    public void GenerateInitDoneReading() {
//        writer.writeLine("/* InitDoneReading : " + this.getClass().getSimpleName() + " */");                        
    }

    /**
     * cklarhor: Questions: is counter==buffersize?
     */
    @Override
    public void GenerateInitDoneWriting() {
//        assert BufferType.SPLIT == bufferType;
//        writer.writeLine("/* InitDoneWriting : " + this.getClass().getSimpleName() + " */");                
        writer.writeLine(type.CType() + "* " + bufferNamePtrTail + "=" + bufferName + ";");
        int iterations = (int) Math.ceil((double) src.initMult / src.split.size());
        if (iterations > 0) {
            writer.startBlock("for (unsigned int _i=0; _i<" + iterations + ";_i++)");
        }
        String var = "_tmp";
        // declare _tmp variable only when needed
        for (FilterInstance[] list : src.split) {
            if (list.length > 1) {
                writer.write("");
                src.filter.type.output.PrintCType();
                writer.println(" " + var + ";");
                break;
            }
        }
        long counter = src.initMult * src.push;
        for (FilterInstance[] list : src.split) {
            if (counter-- == 0) { //cklarhor: ???
                break;
            }
            if (list.length > 1) {
                writer.write(var + " = ");
                writer.write(PopString(true));
                writer.writeLine(";");
                for (FilterInstance f : list) {
                    writer.write("");
                    src.outputs.get(f).Push(var, true, true);
                }
            } else {
                writer.write("");
                src.outputs.get(list[0]).Push(PopString(true), true, true);
            }
        }
        if (iterations > 0) {
            writer.endBlock();
        }
    }

    /**
     * Generate merge pattern!
     */
    @Override
    public void GenerateSteadyWaitRead() {
//        assert BufferType.MERGE == bufferType;
        writer.writeLine(type.CType() + "* " + bufferNamePtrHead + "=" + bufferName + ";");
        long iterations = (dst.netSteady() * dst.pop / dst.join.size());
        writer.writeLine("// SimpleMergeSplitBuffer -> Merge");
        writer.startBlock("for (unsigned int _i=0;_i<" + iterations + ";_i++)");
        for (FilterInstance f : dst.join) {
            writer.write("");
            Push(dst.inputs.get(f).PopString(true), true, true);
        }
        writer.endBlock();
        writer.writeLine(type.CType() + "* " + bufferNamePtrTail + "=" + bufferName + ";");
    }

    
    @Override
    public void GenerateSteadyWaitWrite() {
//        assert BufferType.SPLIT == bufferType;
        writer.writeLine(type.CType() + "* " + bufferNamePtrHead + "=" + bufferName + ";");
    }

    @Override
    public void GenerateSteadyDoneReading() {
        assert false; //you have to add code to CommunicationEvaluator if you want to use this function
    }

    /**
     * Generate split pattern
     */
    @Override
    public void GenerateSteadyDoneWriting() {
//        assert BufferType.SPLIT == bufferType;
        writer.writeLine("// SimpleMergeSplitBuffer -> Split");
        writer.writeLine(type.CType() + "* " + bufferNamePtrTail + "=" + bufferName + ";");
        long iterations = (src.netSteady() * src.push / src.split.size());
        if (iterations > 0) {
            writer.startBlock("for (unsigned int _i=0;_i<" + iterations + ";_i++)");
        }
        int len = src.split.size();
        String var = "_tmp";
        if (len > 0) {
            writer.write("");
            src.filter.type.output.PrintCType();
            writer.writeLine(" " + var + ";");
        }
        // wrap merging pattern around if init already merged
        for (int j = 0; j < len; j++) {
            FilterInstance[] list = src.split.get((j + (int) src.initMult) % len);
            if (list.length > 1) {
                writer.write(var + " = ");
                writer.write(PopString(true));
                writer.println(";");
                for (FilterInstance f : list) {
                    writer.write("");
                    src.outputs.get(f).Push(var, true, true);
                }
            } else {
                writer.write("");
                src.outputs.get(list[0]).Push(PopString(true), true, true);
            }
        }
        if (iterations > 0) {
            writer.endBlock();
        }
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        int extra_overhead = 0;
        if (super.dst.join.size() > 1) {
            BufferEstimator mbe = BufferEstimator.getInstance();
            int cachedResult = mbe.getBufferCost(this);
            if (cachedResult != -1) {
                return cachedResult;
            }
            long maxLines = 16;
            long iterations = (super.dst.netSteady() * super.dst.pop / super.dst.join.size());
            long numLines = super.dst.join.size();
            long unrolledIterations = maxLines / numLines;
            unrolledIterations = (unrolledIterations == 0) ? 1 : unrolledIterations;
            long loopIterations = iterations / unrolledIterations;
            long remainingIterations = iterations - (unrolledIterations * loopIterations);

            if (numLines < maxLines) {
                extra_overhead += loopIterations * loopOverhead;
                extra_overhead += unrolledIterations * loopIterations * super.dst.join.size() * mergingOverhead;
                extra_overhead += remainingIterations * super.dst.join.size() * mergingOverhead;
            } else {
                int round = (int) (super.dst.pop * super.dst.netSteady() / super.dst.join.size()); //* StreamNode.getRootMult()
                extra_overhead += round * loopOverhead;
                extra_overhead += round * super.dst.join.size() * mergingOverhead;
            }
        }
        return extra_overhead;
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 1;
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }

    //Split cost
    @Override
    public int getDoneWriteCost(Chip chip) {
//        assert BufferType.SPLIT == bufferType;
        int extra_overhead = 0;
        int len = super.src.split.size();
        int body_cost = 0;
        BufferEstimator mbe = BufferEstimator.getInstance();
        int cachedResult = mbe.getBufferCost(this);
        if (cachedResult != -1) {
            return cachedResult;
        }
        for (int j = 0; j < len; j++) {
            FilterInstance[] list = super.src.split.get((j + (int) super.src.initMult) % len);
            if (list.length > 1) {
                body_cost += tempOverhead;
                body_cost += assignOverhead * list.length;
            } else {
                body_cost += mergingOverhead;
            }
        }
        int split = super.src.split.size();
        if (split == 1) {
            split = super.src.split.get(0).length;
        }
        if (split > 1) {
            long maxLines = 16;
            long iterations = (super.src.netSteady() * super.src.push / super.src.split.size());
            long numLines = super.src.split.size();
            long unrolledIterations = maxLines / numLines;
            unrolledIterations = (unrolledIterations == 0) ? 1 : unrolledIterations;
            long loopIterations = iterations / unrolledIterations;
            long remainingIterations = iterations - (unrolledIterations * loopIterations);

            if (numLines < maxLines) {
                extra_overhead += loopIterations * loopOverhead;
                extra_overhead += body_cost * unrolledIterations * loopIterations;
                extra_overhead += body_cost * remainingIterations;
            } else {
                int round = (int) (super.src.push * super.src.netSteady() / super.src.split.size());
                extra_overhead += round * loopOverhead;
                extra_overhead += (int) (round * body_cost * 1.75);
            }
        }
        return extra_overhead;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
