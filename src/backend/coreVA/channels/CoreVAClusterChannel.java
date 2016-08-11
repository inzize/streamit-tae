package backend.coreVA.channels;

import static ast.GeneratesCode.writer;
import static streamit2.MathHelper.CEIL;
import static java.lang.Math.max;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;

public class CoreVAClusterChannel extends AbstractChannel {

    CoreVAProcessor srcProc, dstProc;
    protected String receiveBuffer;
    protected String sendBuffer;
    protected String senderName;
    protected String receiverName;
    protected Type type;
    protected long numberOfBuffersToCopy;
    protected long writePointerOffset;
    protected long readPointerOffset;
    protected long nextBuffers;
    protected long initNextBuffers;
    protected long initPushBuffers;

    public CoreVAClusterChannel(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        super(src, dst);
        this.srcProc = srcProc;
        this.dstProc = dstProc;
        this.type = dst.filter.type.input;
        String ident = getIdent();
        senderName = ident + "_sender";
        sendBuffer = ident + "_send_buffer";
        receiverName = ident + "_receiver";
        receiveBuffer = ident + "_receive_buffer";
        numberOfBuffers = getNumberOfBuffers() + Math.abs(dst.primePumpCount - src.primePumpCount);
        srcProc.chip.channels[srcProc.getCol()][srcProc.getRow()][srcProc.getId()][dstProc.getId()]++;
        long steadyItemsPushPop = SteadyItemsPushPop();
        long initItemsPush = InitItemsPush();
        bufferSize = steadyItemsPushPop;
        readPointerOffset = InitItemsPop() % steadyItemsPushPop;
        writePointerOffset = (InitItemsPush() % steadyItemsPushPop > 0) ? steadyItemsPushPop - (initItemsPush % steadyItemsPushPop) : 0;
        nextBuffers = CEIL(max(steadyItemsPushPop, SteadyItemsPeek()), steadyItemsPushPop);
        initNextBuffers = CEIL(InitItemsPeek(), steadyItemsPushPop);
        initPushBuffers = CEIL(initItemsPush, steadyItemsPushPop);
        numberOfBuffersToCopy = getNumberOfBuffersToCopy();
    }

    @Override
    public String getBufferName(FilterInstance f, boolean unique) {
        String ret = "";
        if (f.equals(src)) {
            ret = sendBuffer;
        } else if (f.equals(dst)) {
            ret = receiveBuffer;
        } 
        if(!unique) {
            ret = ret.replace(getIdent(), "");
        }
        return ret;
    }
    
    private long getNumberOfBuffers() {
        // number of buffers in Init, write and read phase
        final long steadyItemsPushPop = SteadyItemsPushPop();
        final long nbInitBuffers = CEIL(InitItemsPush(), steadyItemsPushPop);
        final long nbStdyWriteBuffers = CEIL(steadyItemsPushPop + writePointerOffset, steadyItemsPushPop);
        final long nbStdyReadBuffers = CEIL(max(SteadyItemsPeek(), steadyItemsPushPop) + readPointerOffset, steadyItemsPushPop);
        return Math.max(nbInitBuffers, nbStdyWriteBuffers) + nbStdyReadBuffers;
    }

    private long getNumberOfBuffersToCopy() {
        long peekOffset = 0;
        final long steadyItemsPushPop = SteadyItemsPushPop();
        // peek>buffer? copying of complete extra buffer required
        if (SteadyItemsPeek() > steadyItemsPushPop) {
            peekOffset = SteadyItemsPeek() - steadyItemsPushPop;
        }
        final long nbOfBuffersToCopy = CEIL((writePointerOffset + readPointerOffset + peekOffset), steadyItemsPushPop);
        return nbOfBuffersToCopy;
    }

    private String BufferSize() {
        return SizeofItem() + " * " + bufferSize;
    }

    protected String SizeofItem() {
        return "sizeof(" + type.CType() + ")";
    }

    @Override
    public String getIdent() {
        return src.StreamNodeName() + "_to_" + dst.StreamNodeName();
    }

    private String PopMethodName() {
        return getIdent() + "_coreva_pop";
    }

    private String PeekMethodName() {
        return getIdent() + "_coreva_peek";
    }

    private String PushMethodName() {
        return getIdent() + "_coreva_push";
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
        writer.writeLine("initReceiver(&" + receiverName + ", " + 0 + ", " + srcProc.getId() + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + writePointerOffset + ", " + numberOfBuffers + ", " + numberOfBuffersToCopy + ", DATA_MEM" + ");");
    }

    @Override
    public void InitWrite() {
        writer.writeLine("// InitWrite " + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine("initSender(&" + senderName + ", " + 0 + ", " + dstProc.getId() + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffers + ", DATA_MEM" + ");");
    }

    @Override
    public void ReadDecls() {
        writer.writeLine("MULTI_CHANNEL_SYNC " + receiverName + ";");
    }

    @Override
    public void WriteDecls() {
        writer.writeLine("MULTI_CHANNEL_SYNC " + senderName + ";");
    }

    @Override
    public void GenerateInitWaitWrite() {
        writer.writeLine(type.CType() + "* " + sendBuffer + " = (" + type.CType() + "*)initWaitWrite(&" + senderName + ", " + initPushBuffers + ");");
    }

    @Override
    public void GenerateInitWaitRead() {
        writer.writeLine(type.CType() + "* " + receiveBuffer + " = (" + type.CType() + "*)initWaitRead(&" + receiverName + ", " + initNextBuffers + ");");
    }

    @Override
    public void GenerateInitDoneWriting() {
        writer.writeLine("initDoneWrite(&" + senderName + ", " + initPushBuffers + ", " + SizeofItem() + ", " + writePointerOffset + ");");
    }

    @Override
    public void GenerateInitDoneReading() {
        writer.writeLine("initDoneRead(&" + receiverName + ", " + InitItemsPop() + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + readPointerOffset + ");");
    }

    @Override
    public void GenerateSteadyWaitWrite() {
        writer.writeLine(type.CType() + "* " + sendBuffer + " = (" + type.CType() + "*)steadyClusterWaitWrite(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyWaitRead() {
        writer.writeLine(type.CType() + "* " + receiveBuffer + " = (" + type.CType() + "*)steadyClusterWaitRead(&" + receiverName + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffersToCopy + ", " + readPointerOffset + ", " + writePointerOffset + ", " + nextBuffers + ");");
    }

    @Override
    public void GenerateSteadyDoneWriting() {
        writer.writeLine("steadyClusterDoneWrite(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyDoneReading() {
        writer.writeLine("steadyClusterDoneRead(&" + receiverName + ");");
    }

    //Cost functions
    @Override
    public int getWaitWriteCost(Chip chip) {
//         return 37; //old
//         return 23; //1slot
        return 20; //2slot
//         return 19; //4slot

    }

    @Override
    public int getDoneReadCost(Chip chip) {
//         return 45; //old
//         return 28; //1slot
        return 24; //2slot
//         return 22; //4slot
    }

    @Override
    public int getDoneWriteCost(Chip chip) {
//         return 44; //old
//         return 26; //1slot
        return 21; //2slot
//         return 21; //4slot
    }

    @Override
    public int getWaitReadCost(Chip chip) {
//         return 76; //old
//         return 57; //1slot
        return 50; //2slot
//         return 47; //4slot
    }

    @Override
    public long getMemUsedSrc() {
        // buffer is allocated at receiver=dst
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long channelStructSize = 24 + allBuffers * 16;
        channelStructSize += allBuffers * 4; // accessReady int        
        return channelStructSize + 4; // channel struct + pointer
    }

    @Override
    public long getMemUsedDst() {
        // buffer is allocated at receiver=dst
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long mem = getChannelMemSize();
        long channelStructSize = 24 + allBuffers * 16;
        channelStructSize += allBuffers * 4; // accessReady int
        return mem + channelStructSize + 4; // channel struct + pointer
    }

    @Override
    public long getChannelMemSize() {
        return (numberOfBuffers + numberOfBuffersToCopy) * bufferSize * type.SizeOf(dst);
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        return 5;
    }
}
