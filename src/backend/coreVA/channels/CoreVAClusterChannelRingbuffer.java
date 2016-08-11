package backend.coreVA.channels;

import static ast.GeneratesCode.writer;
import ast.expressions.Expression;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;

/**
 * Ringbuffer as communication channel between two filters on two
 * different cores in one cluster
 */
public class CoreVAClusterChannelRingbuffer extends AbstractChannel {
    private String senderName;
    private String receiverName;
    private Type type;
    private long numberOfBuffers;
    private long numberOfElements;
    CoreVAProcessor srcProc, dstProc;

    private long MAX(long a, long b) {
        return a > b ? a : b;
    }

    private long CEIL(long a, long b) {
        return (a + b - 1) / b;
    }

    private long getNumberOfBuffers() {
        long readPointerOffset = InitItemsPop() % SteadyItemsPushPop();
        long writePointerOffset = (InitItemsPush() % SteadyItemsPushPop()) > 0 ? SteadyItemsPushPop() - (InitItemsPush() % SteadyItemsPushPop()) : 0;

        // number of buffers in Init, write and read phase
        long nbInitBuffers = CEIL(InitItemsPush(), SteadyItemsPushPop());
        long nbStdyWriteBuffers = CEIL(SteadyItemsPushPop() + writePointerOffset, SteadyItemsPushPop());
        long nbStdyReadBuffers = CEIL(MAX(SteadyItemsPeek(), SteadyItemsPushPop()) + readPointerOffset, SteadyItemsPushPop());

        return MAX(nbInitBuffers, nbStdyWriteBuffers) + nbStdyReadBuffers + 1;
    }

    public CoreVAClusterChannelRingbuffer(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        super(src, dst);
        this.srcProc = srcProc;
        this.dstProc = dstProc;
        this.type = dst.filter.type.input;
        senderName = getIdent() + "_sender";
        receiverName = getIdent() + "_receiver";
        numberOfBuffers = getNumberOfBuffers() + Math.abs(dst.primePumpCount - src.primePumpCount);
        // make number of elements a power of 2
        numberOfElements = numberOfBuffers * SteadyItemsPushPop();
        if(Long.highestOneBit(numberOfElements) != numberOfElements)
            numberOfElements = Long.highestOneBit(numberOfElements) << 1;
    }

    public void AssertInitCount() {
    }

    public void AssertSteadyCount() {
    }

    @Override
    public long getChannelMemSize() {
        return numberOfBuffers * type.SizeOf(dst);
    }
    
    private String SizeofItem() {
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
    public String PopString() {
        return (PopMethodName() + "()");
    }

    @Override
    public void Peek(Expression arg) {
        writer.print(PeekMethodName() + "(");
        arg.Print();
        writer.print(")");
    }

    @Override
    public void Push(Expression arg, boolean semicolon) {
        writer.print(PushMethodName() + "(");
        arg.Print();
        writer.print(")");
        if (semicolon)
            writer.println(";");
    }

    @Override
    public void Push(String arg, boolean semicolon) {
        writer.print(PushMethodName() + "(" + arg + ")");
        if (semicolon)
            writer.println(";");
    }    
    
   @Override
    public void PopMethod() {
        writer.println(type.CType() + " " + PopMethodName() + "()");
        writer.println("{");
        writer.println("    " + type.CType() + " data;");
        writer.println("    popRingbuffer2(&" + receiverName + ", &data);");
        writer.println("    return data;");
        writer.println("}");
        writer.println();
    }

    @Override
    public void PushMethod() {
        writer.println("void " + PushMethodName() + "(" + type.CType() + " arg)");
        writer.println("{");
        writer.println("    pushRingbuffer2(&" + senderName + ", &arg);");
        writer.println("}");
        writer.println();
    }

    @Override
    public void PeekMethod() {
        writer.println(type.CType() + " " + PeekMethodName() + "(int offset)");
        writer.println("{");
        writer.println("    " + type.CType() + " data;");
        writer.println("    peekRingbuffer2(&" + receiverName + ", &data, offset);");
        writer.println("    return data;");
        writer.println("}");
        writer.println();
    }

    @Override
    public void InitRead() {
        writer.writeLine("ringbufferInitClusterReceiver(&" + receiverName + ", " + 0 + ", " + srcProc.getGID()
                + ", (" + numberOfElements + "), " + SizeofItem() + ");");
    }

    @Override
    public void InitWrite() {
        writer.writeLine("ringbufferInitClusterSender(&" + senderName + ", " + 0 + ", " + dstProc.getGID()
                + ", (" + numberOfElements + "), " + SizeofItem() + ");");
    }

    @Override
    public void ReadDecls() {
        writer.println("RINGBUFFER_SYNC " + receiverName + ";");
    }

    @Override
    public void WriteDecls() {
        writer.println("RINGBUFFER_SYNC " + senderName + ";");
    }

    @Override
    public void GenerateInitWaitWrite() {
    }

    @Override
    public void GenerateInitWaitRead() {
    }

    @Override
    public void GenerateInitDoneWriting() {
    }

    @Override
    public void GenerateInitDoneReading() {
    }

    @Override
    public void GenerateSteadyWaitWrite() 
    {
    }

    @Override
    public int getWaitWriteCost(Chip chip) 
    {
        return 0;
    } 
    
    @Override
    public void GenerateSteadyWaitRead() 
    {
    }    
    
    @Override
    public int getWaitReadCost(Chip chip) 
    {
        return 0;
    } 
    
    @Override
    public void GenerateSteadyDoneWriting() 
    {
    }

    @Override
    public int getDoneWriteCost(Chip chip) 
    {
        return 0;
    }    
    
    @Override
    public void GenerateSteadyDoneReading() 
    {
    }
    
    @Override
    public int getDoneReadCost(Chip chip) 
    {
        return 0;
    }      

    @Override
    public long TransmissionTime(CommunicationModel model) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}