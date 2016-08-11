package backend.common.buffers;

import static java.lang.Math.max;
import static ast.GeneratesCode.writer;
import ast.expressions.Expression;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;
import java.util.logging.Logger;

enum BufferType {MERGE, SPLIT};

public abstract class AbstractMergeSplitBuffer extends AbstractChannel {
    final static Logger logger = Logger.getLogger("SimpleMergeSplitBuffer");
    final BufferType bufferType;
    final Type type;
    final String bufferName;
    final String bufferNamePtrHead, bufferNamePtrTail;    
    long arraySize = 0;

    public AbstractMergeSplitBuffer(FilterInstance src, FilterInstance dst) {
        super(src, dst);
        if (src == null && dst != null) {
            bufferType = BufferType.MERGE;
            this.bufferSize = dst.netSteady() * dst.pop;
            this.type = dst.filter.type.input;
            arraySize = bufferSize;
        } else if (src != null && dst==null) {
            bufferType = BufferType.SPLIT;
            this.type = src.filter.type.output;
            this.bufferSize = src.netSteady() * src.push;
            arraySize = max(bufferSize, src.initMult);
        } else {
            throw new RuntimeException("Should not happen!");
        }
        String thisIdent = this.getIdent();
        bufferName = getBufferTypeIdent() + thisIdent + "_buffer";
        bufferNamePtrHead = getBufferTypeIdent() + thisIdent + "_buffer_ptr_head";
        bufferNamePtrTail = getBufferTypeIdent() + thisIdent + "_buffer_ptr_tail";
    }

    public BufferType getBufferType() {
        return bufferType;
    }
    
    private String getBufferTypeIdent() {
        switch (bufferType) {
            case MERGE:
                return "merge_";
            case SPLIT:
                return "split_";
            default:
                throw new RuntimeException("Not impl");
        }
    }
   
    @Override
    public final String getIdent() {
        switch (bufferType) {
            case MERGE:
                return dst.StreamNodeName();
            case SPLIT:
                return src.StreamNodeName();
            default:
                throw new RuntimeException("Not impl");
        }
    }

    @Override
    public void PopMethod() {
    }

    @Override
    public void PushMethod() {
    }

    @Override
    public void PeekMethod() {
    }

    @Override
    public void InitRead() {
//        writer.writeLine("// InitRead " + getBufferTypeIdent() + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine(type.CType() + "* " + bufferNamePtrTail + "=" + bufferName + ";");
    }

    @Override
    public void InitWrite() {
//        writer.writeLine("// InitWrite " + getBufferTypeIdent() + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine(type.CType() + "* " + bufferNamePtrHead + "=" + bufferName + ";");
    }

    @Override
    public void ReadDecls() {
        type.PrintCType();
        writer.println(" " + bufferName + "[" + arraySize + "];");
    }

    @Override
    public void WriteDecls() {
    }
    
    @Override
    public void GenerateSteadyDoneReading() {
        assert false; //you have to add code to CommunicationEvaluator if you want to use this function
    }

    //Cost functions
    @Override
    public long getChannelMemSize() {
        switch (bufferType) {
            case MERGE:
                return bufferSize * type.SizeOf(dst);
            case SPLIT:
                return bufferSize * type.SizeOf(src);
            default:
                throw new RuntimeException("Not impl");
        }
    }

    @Override
    public long getMemUsedSrc() {
        int memoryUsed = 0;
        memoryUsed += getChannelMemSize();
        memoryUsed += 4;
        memoryUsed += 4;
        return memoryUsed;
    }

    @Override
    public long getMemUsedDst() {
        int memoryUsed = 0;
        memoryUsed += getChannelMemSize();
        memoryUsed += 4;
        memoryUsed += 4;
        return memoryUsed;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }  
}
