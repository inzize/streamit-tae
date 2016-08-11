package backend.coreVA.channels;

import static streamit2.MathHelper.CEIL;
import static java.lang.Math.max;
import static ast.GeneratesCode.writer;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;

public class CoreVANocChannel extends AbstractChannel {

    private final String receiveBuffer, sendBuffer;
    private final long readPointerOffset;
    private final long nextBuffers;
    private final long initNextBuffers, initPushBuffers;
    protected final int flowId, ackId;
    protected final CoreVAProcessor srcProc, dstProc;
    protected String senderName, receiverName;
    protected Type type;
    protected long numberOfBuffersToCopy;
    protected long writePointerOffset;

    public CoreVANocChannel(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        super(src, dst);
        this.srcProc = srcProc;
        this.dstProc = dstProc;
        this.type = dst.filter.type.input;
        senderName = getIdent() + "_sender";
        sendBuffer = getIdent() + "_send_buffer";
        receiverName = getIdent() + "_receiver";
        receiveBuffer = getIdent() + "_receive_buffer";
        numberOfBuffers = getNumberOfBuffers() + Math.abs(dst.primePumpCount - src.primePumpCount);
        flowId = srcProc.chip.next_flow_id[dstProc.getCol()][dstProc.getRow()];
        ackId = srcProc.chip.next_ack_id[srcProc.getCol()][srcProc.getRow()];
        srcProc.chip.next_flow_id[dstProc.getCol()][dstProc.getRow()] += numberOfBuffers;
        srcProc.chip.next_ack_id[srcProc.getCol()][srcProc.getRow()] += numberOfBuffers;
        srcProc.chip.channels[srcProc.getCol()][srcProc.getRow()][srcProc.getId()][dstProc.getId()]++;
        // number of items (no bytes) according to MemoryChannel, todo: extra space (factor 2 here)?
        bufferSize = SteadyItemsPushPop();
        readPointerOffset = InitItemsPop() % SteadyItemsPushPop();
        writePointerOffset = (InitItemsPush() % SteadyItemsPushPop()) > 0 ? SteadyItemsPushPop() - (InitItemsPush() % SteadyItemsPushPop()) : 0;
        nextBuffers = CEIL(max(SteadyItemsPushPop(), SteadyItemsPeek()), SteadyItemsPushPop());
        initNextBuffers = CEIL(InitItemsPeek(), SteadyItemsPushPop());
        initPushBuffers = CEIL(InitItemsPush(), SteadyItemsPushPop());
        numberOfBuffersToCopy = getNumberOfBuffersToCopy();
    }

    @Override
    public String getBufferName(FilterInstance f, boolean unique) {
        String ret = "";
//        ret += (unique ? getIdent() : "");       
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
        long nbInitBuffers = CEIL(InitItemsPush(), SteadyItemsPushPop());
        long nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() + writePointerOffset, SteadyItemsPushPop());
        long nbStdyReadBuffers = CEIL(max(SteadyItemsPeek(), SteadyItemsPushPop()) + readPointerOffset, SteadyItemsPushPop());
        return max(nbInitBuffers, nbStdyWriteBuffers) + nbStdyReadBuffers;
    }

    private long getNumberOfBuffersToCopy() {
        long peekOffset = 0;
        // peek>buffer? copying of complete extra buffer required
        if (SteadyItemsPeek() > SteadyItemsPushPop()) {
            peekOffset = SteadyItemsPeek() - SteadyItemsPushPop();
        }
        long nbOfBuffersToCopy = CEIL((writePointerOffset + readPointerOffset + peekOffset), SteadyItemsPushPop());
        return nbOfBuffersToCopy;
    }

    @Override
    public long getChannelMemSize() {
        return (numberOfBuffers + numberOfBuffersToCopy) * bufferSize * type.SizeOf(dst) * 2; // TODO eliminate factor 2 in streamit api!
    }

    @Override
    public long getMemUsedSrc() {
        // buffer is allocated at sender and receiver
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long mem = getChannelMemSize() + 8;
        long channelStructSize = 24 + allBuffers * (16 + 8);
        return mem + channelStructSize + 6 + 4; // channel struct + NocInfo struct + pointer      
    }

    @Override
    public long getMemUsedDst() {
        // buffer is allocated at sender and receiver
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long mem = getChannelMemSize() + 8;
        long channelStructSize = 24 + allBuffers * 16;
        return mem + channelStructSize + 4; // channel struct + pointer
    }

    private String BufferSize() {
        return SizeofItem() + " * " + bufferSize;
    }

    public long getBuffersizeBytes() {
        return getBufferSize() * type.SizeOf(dst);
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
        writer.writeLine("// init_push=" + InitItemsPush() + ", init_pop=" + InitItemsPop() + ", init_peek=" + InitItemsPeek() + ", steady_pushpop=" + SteadyItemsPushPop() + ", steady_peek=" + SteadyItemsPeek());
        writer.writeLine("initNocReceiver(&" + receiverName + ", " + srcProc.getCol() + ", " + srcProc.getRow() + ", " + srcProc.getId() + ", " + flowId + ", " + ackId + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + writePointerOffset + ", " + numberOfBuffers + ", " + numberOfBuffersToCopy + ", DATA_MEM" + ");");
    }

    @Override
    public void InitWrite() {
        writer.writeLine("// init_push=" + InitItemsPush() + ", init_pop=" + InitItemsPop() + ", init_peek=" + InitItemsPeek() + ", steady_pushpop=" + SteadyItemsPushPop() + ", steady_peek=" + SteadyItemsPeek());
        writer.writeLine("initNocSender(&" + senderName + ", " + dstProc.getCol() + ", " + dstProc.getRow() + ", " + dstProc.getId() + ", " + flowId + ", " + ackId + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffers + ", DATA_MEM" + ");");
    }

    @Override
    public void ReadDecls() {
        //copy of nbOfBuffers:
        long nbInitBuffers = CEIL(InitItemsPush(), SteadyItemsPushPop());
        long nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() + writePointerOffset, SteadyItemsPushPop());
        long nbStdyReadBuffers = CEIL(max(SteadyItemsPeek(), SteadyItemsPushPop()) + readPointerOffset, SteadyItemsPushPop());
        //end
        writer.writeLine("//BufferInfo:\n//nbOfBuffers: "+getNumberOfBuffers()+" = max(nbInitBuffers["+nbInitBuffers+"], nbStdyWriteBuffers["+nbStdyWriteBuffers+"]) + nbStdyReadBuffers["+nbStdyReadBuffers+"] + Math.abs(dst.primePumpCount - src.primePumpCount) ["+Math.abs(dst.primePumpCount - src.primePumpCount)+"]");
        writer.writeLine("//nbInitBuffers = CEIL(InitItemsPush() ["+ InitItemsPush() +"], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +"])");
        writer.writeLine("//nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() ["+ SteadyItemsPushPop() + "] + writePointerOffset ["+ writePointerOffset + "], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +"])");
        writer.writeLine("//nbStdyReadBuffers = CEIL(max(SteadyItemsPeek() ["+ SteadyItemsPeek() + "], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +") + readPointerOffset ["+ readPointerOffset +"], SteadyItemsPushPop() ["+SteadyItemsPushPop()+"])");
        writer.writeLine("MULTI_CHANNEL_SYNC " + receiverName + ";");
    }

    @Override
    public void WriteDecls() {
        //copy of nbOfBuffers:
        long nbInitBuffers = CEIL(InitItemsPush(), SteadyItemsPushPop());
        long nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() + writePointerOffset, SteadyItemsPushPop());
        long nbStdyReadBuffers = CEIL(max(SteadyItemsPeek(), SteadyItemsPushPop()) + readPointerOffset, SteadyItemsPushPop());
        //end
        writer.writeLine("//BufferInfo:\n//nbOfBuffers: "+getNumberOfBuffers()+" = max(nbInitBuffers["+nbInitBuffers+"], nbStdyWriteBuffers["+nbStdyWriteBuffers+"]) + nbStdyReadBuffers ["+nbStdyReadBuffers+"] + Math.abs(dst.primePumpCount - src.primePumpCount) ["+Math.abs(dst.primePumpCount - src.primePumpCount)+"]");
        writer.writeLine("//nbInitBuffers = CEIL(InitItemsPush() ["+ InitItemsPush() +"], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +"])");
        writer.writeLine("//nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() ["+ SteadyItemsPushPop() + "] + writePointerOffset ["+ writePointerOffset + "], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +"])");
        writer.writeLine("//nbStdyReadBuffers = CEIL(max(SteadyItemsPeek() ["+ SteadyItemsPeek() + "], SteadyItemsPushPop() ["+ SteadyItemsPushPop() +") + readPointerOffset ["+ readPointerOffset +"], SteadyItemsPushPop() ["+SteadyItemsPushPop()+"])");
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
        writer.writeLine(type.CType() + "* " + sendBuffer + " = (" + type.CType() + "*)steadyNocWaitWrite(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyWaitRead() {
        writer.writeLine(type.CType() + "* " + receiveBuffer + " = (" + type.CType() + "*)steadyNocWaitRead(&" + receiverName + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffersToCopy + ", " + readPointerOffset + ", " + writePointerOffset + ", " + nextBuffers + ");");
    }

    @Override
    public void GenerateSteadyDoneWriting() {
        writer.writeLine("steadyNocDoneWrite(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyDoneReading() {
        writer.writeLine("steadyNocDoneRead(&" + receiverName + ");");
    }

    //Cost functions
    @Override
    public int getWaitReadCost(Chip chip) {
        return 80;
    }

    @Override
    public int getDoneWriteCost(Chip chip) {
        return 63;
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 36;
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 40;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
       // return 2000;streamit2.Compiler.program.contains("AutoCor") ||
        long tmp = CEIL(bufferSize, model.throughput(this))*100;
        if (streamit2.Compiler.program.contains("Counting") || streamit2.Compiler.program.contains("MergeSort22")|| streamit2.Compiler.program.contains("MatrixMultInt")) {
            tmp+=4000;
        }
        return tmp;
    }
}
