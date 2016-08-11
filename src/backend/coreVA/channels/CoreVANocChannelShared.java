package backend.coreVA.channels;

import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.AbstractMap;

public class CoreVANocChannelShared extends CoreVANocChannel {

    public CoreVANocChannelShared(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        super(src, srcProc, dst, dstProc);
    }

    @Override
    public void InitRead() {
        writer.writeLine("// init_push=" + InitItemsPush() + ", init_pop=" + InitItemsPop() + ", init_peek=" + InitItemsPeek() + ", steady_pushpop=" + SteadyItemsPushPop() + ", steady_peek=" + SteadyItemsPeek());
        writer.writeLine("initNocReceiver(&" + receiverName + ", " + srcProc.getCol() + ", " + srcProc.getRow() + ", " + srcProc.getId() + ", " + flowId + ", " + ackId + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + writePointerOffset + ", " + numberOfBuffers + ", " + numberOfBuffersToCopy + ", TCDM" + ");");
    }

    @Override
    public void InitWrite() {
        writer.writeLine("// init_push=" + InitItemsPush() + ", init_pop=" + InitItemsPop() + ", init_peek=" + InitItemsPeek() + ", steady_pushpop=" + SteadyItemsPushPop() + ", steady_peek=" + SteadyItemsPeek());
        writer.writeLine("initNocSender(&" + senderName + ", " + dstProc.getCol() + ", " + dstProc.getRow() + ", " + dstProc.getId() + ", " + flowId + ", " + ackId + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffers + ", TCDM" + ");");
    }

    @Override
    public long getMemUsedSrc() {
        // buffer is allocated at sender and receiver
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long channelStructSize = 24 + allBuffers * 16;
        return channelStructSize + 6 + 4; // channel struct + NocInfo struct + pointer      
    }

    @Override
    public long getMemUsedDst() {
        // buffer is allocated at sender and receiver
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long channelStructSize = 24 + allBuffers * 16;
        return channelStructSize + 4; // channel struct + pointer
    }

    @Override
    public AbstractMap.SimpleEntry<Integer, Integer> getMemUsedShared() {
        int size = (int) getChannelMemSize();
        return new AbstractMap.SimpleEntry<>(size, size); // allocated at sender and receiver cluster
    }
}
