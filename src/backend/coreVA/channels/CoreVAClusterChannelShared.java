package backend.coreVA.channels;

import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.AbstractMap;

public class CoreVAClusterChannelShared extends CoreVAClusterChannel {

    public CoreVAClusterChannelShared(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        super(src, srcProc, dst, dstProc);
    }
    
    @Override
    public void InitRead()
    {
        writer.writeLine("// InitRead " + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine("initReceiver(&" + receiverName + ", " + 0 + ", " + srcProc.getId() + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + writePointerOffset + ", " + numberOfBuffers + ", " + numberOfBuffersToCopy + ", TCDM" +");");
    }
    
    @Override
    public void InitWrite()
    {
        writer.writeLine("// InitWrite " + this.getIdent() + " (" + InitItemsPush() + " " + InitItemsPop() + " " + InitItemsPeek() + " " + SteadyItemsPushPop() + " " + SteadyItemsPeek() + ")");
        writer.writeLine("initSender(&" + senderName + ", " + 0 + ", " + dstProc.getId() + ", " + SteadyItemsPushPop() + ", " + SizeofItem() + ", " + numberOfBuffers + ", TCDM" + ");");         
    }    

    @Override
    public long getMemUsedSrc() {
        // buffer is allocated at receiver=dst
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long channelStructSize = 24 + allBuffers * 16;
        return channelStructSize + 4; // channel struct + pointer
    }

    @Override
    public long getMemUsedDst() {
        // buffer is allocated at receiver=dst
        long allBuffers = getChannelMemSize() / (bufferSize * type.SizeOf(dst));
        long channelStructSize = 24 + allBuffers * 16;
        return channelStructSize + 4; // channel struct + pointer
    }
    
    @Override
    public AbstractMap.SimpleEntry<Integer,Integer> getMemUsedShared() {
        int size = (int) getChannelMemSize();
        return new AbstractMap.SimpleEntry<>(0,size); // allocated once, same cluster for sender and receiver
    }
}
