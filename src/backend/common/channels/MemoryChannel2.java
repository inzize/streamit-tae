package backend.common.channels;

import static ast.GeneratesCode.writer;
import static streamit2.MathHelper.CEIL;
import ast.types.Type;
import backend.common.Chip;
import backend.common.SourceFileWriter;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;

public class MemoryChannel2 extends AbstractChannel {
    private final Type type;
    private final String bufferName, bufferNamePtr, tailName, headName;
    private final String bufferNamePtrHead, bufferNamePtrTail;
    private final long writeOffset, readOffset;

    public MemoryChannel2(FilterInstance src, FilterInstance dst) {
        super(src, dst);
        this.type = dst.filter.type.input;
        String ident = getIdent();
        this.bufferName = ident + "_buffer";
        this.bufferNamePtr = ident+ "_buffer_ptr";
        this.bufferNamePtrHead = ident + "_buffer_ptr_head";
        this.bufferNamePtrTail = ident + "_buffer_ptr_tail";
        this.headName = ident + "_head";
        this.tailName = ident + "_tail";
        long initItemsPush = InitItemsPush();
        long steadyItemsPushPop = SteadyItemsPushPop();
        writeOffset = initItemsPush;
        readOffset = InitItemsPop();
        int iterations = Math.abs(dst.primePumpCount - src.primePumpCount);
        // primepump offset
        long bufferSizeOffset = 0;
        if (writeOffset - readOffset != 0) {
            bufferSizeOffset = CEIL(writeOffset - readOffset, steadyItemsPushPop);
            bufferSizeOffset *= steadyItemsPushPop;
        }
        this.bufferSize = Math.max(initItemsPush + steadyItemsPushPop + bufferSizeOffset,
                bufferSizeOffset
                + iterations * steadyItemsPushPop
                + Math.max(SteadyItemsPeek(), steadyItemsPushPop));
        numberOfBuffers = 1;     
    }
    
    @Override
    public String getBufferName(FilterInstance f, boolean unique) {
        String ret = "";
        if (f.equals(src)) {
            ret = bufferNamePtrHead;
        } else if (f.equals(dst)) {
            ret = bufferNamePtrTail;
        }
        
        if (!unique) {
            ret = ret.replace(getIdent(), "");
        }
        
        return ret;
    }

    @Override
    public String getIdent() {
        return src.StreamNodeName() + "_to_" + dst.StreamNodeName();
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
        writer.writeLine("// InitRead " + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine(bufferNamePtrTail + "=" + bufferName + ";");
    }

    @Override
    public void InitWrite() {
        writer.writeLine("// InitWrite " + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine(bufferNamePtrHead + "=" + bufferName + ";");
    }

    @Override
    public void ReadDecls() {
        type.PrintCType();
        writer.println("* " + bufferNamePtrHead + " = 0;");
        type.PrintCType();
        writer.println(" " + bufferName + "[" + bufferSize + "+" + writeOffset + "];");
    }

    @Override
    public void WriteDecls() {
        type.PrintCType();
        writer.println("* " + bufferNamePtrTail + " = 0;");

    }

    @Override
    public void GenerateInitWaitRead() {
    }

    @Override
    public void GenerateInitWaitWrite() {
    }

    @Override
    public void GenerateInitDoneReading() {
        if (dst.isLastPeekingFilter) {
            writer.startBlock("for (int _k=0; _k<" + (dst.peek - dst.pop) + "; _k++)");
            writer.write("");
            dst.GeneratePop(true);
            writer.writeLine(";");
            writer.endBlock();
        }
    }

    @Override
    public void GenerateInitDoneWriting() {
    }

    @Override
    public void GenerateSteadyWaitRead() {
        writer.writeLine("if(((" + bufferNamePtrTail + "-" + bufferName + ")+" + SteadyItemsPushPop() + ")>" + bufferSize + "+" + readOffset + ")");
        writer.writeLine(SourceFileWriter.INDENT_STR + bufferNamePtrTail + "=" + bufferName + "+" + readOffset + ";");
    }

    @Override
    public void GenerateSteadyWaitWrite() {
        writer.startBlock("if(((" + bufferNamePtrHead + "-" + bufferName + ")+" + SteadyItemsPushPop() + ")>" + bufferSize + "+" + writeOffset + ")");
        if (writeOffset - readOffset != 0) {
            writer.writeLine("__buildin__memcpy(" + bufferName + "+" + readOffset + ","
                    + bufferNamePtrHead + "-" + (writeOffset - readOffset) + ","
                    + (writeOffset - readOffset) + "*sizeof(" + type.CType() + "));");
        }
        writer.writeLine(bufferNamePtrHead + "=" + bufferName + "+" + writeOffset + ";");
        writer.endBlock();
    }

    @Override
    public void GenerateSteadyDoneReading() {
    }

    @Override
    public void GenerateSteadyDoneWriting() {
    }

    //Cost functions
   
    @Override
    public long getChannelMemSize() {
        // more space needed for copy mechanism
        return (bufferSize + writeOffset) * type.SizeOf(dst);
    }

    @Override
    public long getMemUsedSrc() {
        long mem = getChannelMemSize();
        return mem + 4; // head of buffer ptr

    }

    @Override
    public long getMemUsedDst() {
        return 4; // tail of buffer ptr
    }
    
    @Override
    public int getWaitReadCost(Chip chip) {
        assert src != dst;
        return 13;
    }

    
    @Override
    public int getWaitWriteCost(Chip chip) {
        assert src != null;
        assert dst != null;
        assert src != dst;
        return 13;
    }
    
    @Override
    public int getDoneWriteCost(Chip chip) {
        assert src != dst;
        return 1;
    }
    
    @Override
    public int getDoneReadCost(Chip chip) {
        assert src != dst;
        return 1;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        return 0;
    }
}
