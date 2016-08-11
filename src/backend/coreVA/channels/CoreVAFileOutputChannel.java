package backend.coreVA.channels;

import ast.expressions.Expression;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;
import java.util.AbstractMap;

public class CoreVAFileOutputChannel extends AbstractChannel {

    private final String senderName, sendBuffer;
    private final Type type;
    
    public int fioSharedMemOffset = 0;

    public CoreVAFileOutputChannel(FilterInstance src) {
        super(src, src);
        type = src.filter.type.input;
        senderName = getIdent() + "_sender";
        sendBuffer = getIdent() + "_send_buffer";
        this.bufferSize = src.netSteady() * type.SizeOf(src);
    }

    @Override
    public String getIdent() {
        return src.StreamNodeName() + "_to_FileOutput";
    }

    private String SizeofItem() {
        return "sizeof(" + type.CType() + ")";
    }

    private String PopMethodName() {
        return null;
    }

    private String PeekMethodName() {
        return null;
    }

    private String PushMethodName() {
        return getIdent() + "_coreva_push";
    }

    @Override
    public String PopString() {
        return "";
    }

    @Override
    public void Peek(Expression arg) {
    }

    @Override
    public void Push(Expression arg, boolean semicolon) {
        writer.print("    *" + sendBuffer + "++ = (");
        arg.Print();
        writer.print(")");
        if (semicolon) {
            writer.println(";");
        }

    }

    @Override
    public void Push(String arg, boolean semicolon) {
        writer.print("    *" + sendBuffer + "++ = (" + arg + ")");
        if (semicolon) {
            writer.println(";");
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
    }

    @Override
    public void InitWrite() {
        String filename_val = (String) src.args[0];
        String filename_var = src.filter.params.get(0).name;
        writer.writeLine("#ifdef COREVA");
        writer.writeLine(filename_var + " = \"" + filename_val + "_cva" + "\";");
        writer.writeLine("#else");
        writer.writeLine(filename_var + " = \"" + filename_val + "_pth" + "\";");
        writer.writeLine("#endif");
        writer.writeLine("volatile " + type.CType() + "* " + sendBuffer + " = (" + type + "*)fioAllocateSharedMem(" + fioSharedMemOffset + ", " + src.netSteady() + " * " + SizeofItem() + ");");
        writer.writeLine("initFioWriteMultiChannel(&" + senderName + ", 1, INTEGER, " + filename_var + ", (void*)" + sendBuffer + ", " + src.netSteady() + " * " + SizeofItem() + ", 1);");
    }

    @Override
    public void ReadDecls() {
    }

    @Override
    public void WriteDecls() {
        writer.writeLine("MULTI_CHANNEL_SYNC " + senderName + ";");
    }

    @Override
    public void GenerateInitWaitWrite() {
        writer.writeLine("/* TODO: InitWaitWrite here */");
    }

    @Override
    public void GenerateInitWaitRead() {
    }

    @Override
    public void GenerateInitDoneWriting() {
        writer.writeLine("/* TODO: InitDoneWriting here */");
    }

    @Override
    public void GenerateInitDoneReading() {
    }

    @Override
    public void GenerateSteadyWaitWrite() {
        writer.writeLine("volatile " + type.CType() + "* " + sendBuffer + " = waitMultiChannelReady(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyWaitRead() {
    }

    @Override
    public void GenerateSteadyDoneWriting() {
        writer.writeLine("setMultiChannelReady(&" + senderName + ");");
    }

    @Override
    public void GenerateSteadyDoneReading() {
    }

    @Override
    public void destructChannel() {
        writer.writeLine("deleteMultiChannel(&" + senderName + ");");
    }

    //Cost functions
    
    @Override
    public int getDoneWriteCost(Chip chip) {
        return 34;
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        return 0;
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 29;
    }

    @Override
    public long getChannelMemSize() {
        return 144 + 4; // filename("output.stream_cva") + *send_buffer
    }

    @Override
    public long getMemUsedSrc() {
        int mem = 0;
        //mem += bufferSize; // allocated in seperate memory
        mem += 24; // MULTI_CHANNEL_SYNC
        mem += 16; //  -> CHANNEL_SYNC * num_out_buff=1
        mem += 4;  //  -> additional_info_struct: sizeof(FIO_ADDITIONAL_INFO) = 4
        mem += 4;  //  -> CHANNEL_TYPE
        mem += getChannelMemSize();
        return mem;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public AbstractMap.SimpleEntry<Integer,Integer> getMemUsedShared() {
        int size = (int) (src.netSteady() * type.SizeOf(src));
        return new AbstractMap.SimpleEntry<>(0,size);
    }    
}
