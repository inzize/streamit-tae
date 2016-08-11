package backend.coreVA.channels;

import ast.expressions.Expression;
import ast.types.Type;
import backend.common.Chip;
import backend.common.channels.AbstractChannel;
import backend.common.channels.AbstractInputChannel;
import backend.common.estimators.CommunicationModel;
import backend.common.evaluators.ThroughputEvaluator;
import hierarchicalGraph.FilterInstance;
import java.util.AbstractMap;

public class CoreVAFileInputChannel extends AbstractChannel implements AbstractInputChannel {
    private final String receiverName, receiveBuffer;
    private final Type type;
    public int fioSharedMemOffset = 0;    
    
    public CoreVAFileInputChannel(FilterInstance src) {
        super(src,src);
        type = src.filter.type.output;
        receiverName = getIdent() + "_receiver";
        receiveBuffer = getIdent() + "_receive_buffer";
    }

    @Override
    public long getChannelMemSize() {
        return 0;
    }

    @Override
    public final String getIdent() {
        return "FileInput_to_" + src.StreamNodeName();
    }

    private String SizeofItem() {
        return "sizeof(" + type.CType() + ")";
    }

    @Override
    public String PopString() {
        return ("(*" + receiveBuffer + "++)");
    }

    @Override
    public void Peek(Expression arg) {
    }

    @Override
    public void Push(Expression arg, boolean semicolon) {
        arg.Print();
        writer.print(" = *" + receiveBuffer + "++");
        if (semicolon) {
            writer.println(";");
        }
    }

    @Override
    public void Push(String arg, boolean semicolon) {
        writer.print("" + arg + " = *" + receiveBuffer);
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
        writer.writeLine(filename_var + " = \"" + filename_val + "\";");
        int init = (int) (src.initMult*src.push);
        int size = (int) Math.max(src.netSteady(),init);
        writer.writeLine("//net_: "+src.netSteady() + " init: "+init);
        writer.writeLine("volatile " + type.CType() + "* " + receiveBuffer + " = (" + type + "*)fioAllocateSharedMem(" + fioSharedMemOffset + ", " + size + " * " + SizeofItem() + ");");
        if (init!=0) //We need init so configure fio for init
            writer.writeLine("initFioReadMultiChannel(&" + receiverName + ", 0, " + filename_var + ", (void*)" + receiveBuffer + ", " + init + " * " + SizeofItem() + ", 1);");
        else //There is no init fio will start in steady state
            writer.writeLine("initFioReadMultiChannel(&" + receiverName + ", 0, " + filename_var + ", (void*)" + receiveBuffer + ", " + src.netSteady() + " * " + SizeofItem() + ", 1);");
    }
    public void reconfigureForSteadyState() {
        String filename_val = (String) src.args[0];
        String filename_var = src.filter.params.get(0).name;
        int filterPushes = 0;
        for (FilterInstance[] fL : src.split)
            filterPushes += fL.length;
        final int maxFiterInitPushes = (int) (src.initMult*src.push);
        int actualInitWrites = Math.min(maxFiterInitPushes,filterPushes);
        if (filterPushes==1) //Special case: push everything on init
            actualInitWrites = maxFiterInitPushes; 
        writer.writeLine(receiveBuffer+"-="+actualInitWrites+";");
        writer.writeLine("initFioReadMultiChannel(&" + receiverName + ", 0, " + filename_var + ", (void*)" + receiveBuffer + ", " + src.netSteady() + " * " + SizeofItem() + ", 1);");
    }
    @Override
    public void ReadDecls() {
    }

    @Override
    public void WriteDecls() {
        writer.writeLine("MULTI_CHANNEL_SYNC " + receiverName + ";");
        fileReaderThroughputLimitDecls();
    }
    
    public void fileReaderThroughputLimitDecls() {
        if (streamit2.Compiler.fileReaderMode != streamit2.Compiler.FileReaderMode.LIMIT_NO) {
            writer.writeLine("#ifndef fileReader_min_cycles");
            writer.incIndention();
            writer.print("unsigned int fileReader_min_cycles=");
            switch (streamit2.Compiler.fileReaderMode) {
                case LIMIT_TO_FINAL_STREAMIT_THROUGHPUT://TODO: FIXME: MERGE
                    //writer.writeLine(MaxThroughputEvaluator.getInstance().getLastStats().steadyCycles+"; //Target->StreamIt Throughput: "+MaxThroughputEvaluator.getInstance().getLastStats().throughput);
                    break;
                case LIMIT_TO_TARGET_THROUGHPUT:
                    writer.writeLine(streamit2.Compiler.targetThroughput+"; //Target->User Throughput");
                default:
                    throw new UnsupportedOperationException(streamit2.Compiler.fileReaderMode + "not impl");
            }
            writer.decIndention();
            writer.writeLine("#endif");
            writer.writeLine("unsigned int last_fileReader_clock_counter=0;");
        }
    }

    @Override
    public void GenerateInitWaitWrite() {
        assert false;
    }

    /**
      * This is the FileReader init function and we do nothing here
      * See FileReaderSplitBuffer.GenerateInitDoneWriting
      * 
      */
    @Override
    public void GenerateInitWaitRead() {
    }
    
    @Override
    public void generateWaitRead() {
        writer.writeLine("volatile " + type.CType() + "* " + receiveBuffer + " = waitMultiChannelReady(&" + receiverName + ");");        
    }
    
    @Override
    public void generateDoneReading() {
        writer.writeLine("setMultiChannelReady(&" + receiverName + ");");        
    }
    
    @Override
    public void GenerateInitDoneWriting() {
        assert false;
    }

    /**
     * See GenerateInitWaitRead()
     * This should be empty
     */
    @Override
    public void GenerateInitDoneReading() {        
    }
    
    @Override
    public void GenerateSteadyWaitWrite() {
        assert false;
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 29;
    }

    @Override
    public void GenerateSteadyWaitRead() {
        if (streamit2.Compiler.fileReaderMode != streamit2.Compiler.FileReaderMode.LIMIT_NO)
            generateThroughputLimit();
        generateWaitRead();
    }
    
    public void generateThroughputLimit() {
        writer.writeLine("#ifdef COREVA");
        writer.incIndention();
        writer.writeLine("//generate FileReader throughput limit! -> "+streamit2.Compiler.fileReaderMode);
        writer.writeLine("while(*(volatile unsigned int*)COREVA_CLOCK_COUNTER_ADDR-last_fileReader_clock_counter<fileReader_min_cycles) {}");
        writer.writeLine("last_fileReader_clock_counter = *(volatile unsigned int*)COREVA_CLOCK_COUNTER_ADDR;");
        writer.decIndention();
        writer.writeLine("#endif");
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        return 0;
    }

    @Override
    public void GenerateSteadyDoneWriting() {
        assert false;
    }

    @Override
    public int getDoneWriteCost(Chip chip) {
        return 36;
    }

    @Override
    public void GenerateSteadyDoneReading() {
        generateDoneReading();
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }

    @Override
    public void destructChannel() {
        writer.writeLine("deleteMultiChannel(&" + receiverName + ");");
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
      
    @Override
    public AbstractMap.SimpleEntry<Integer,Integer> getMemUsedShared() {
        int size = (int) (src.netSteady() * type.SizeOf(src));
        return new AbstractMap.SimpleEntry<>(0,size);
    }       
}
