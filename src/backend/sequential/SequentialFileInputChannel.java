package backend.sequential;

import ast.expressions.Expression;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.channels.AbstractInputChannel;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;

public class SequentialFileInputChannel extends AbstractChannel implements AbstractInputChannel {

    private final String receiveFile;
    private final String receiveName;
    private final String receiveBuffer;
    private final String receiveStart;
    private final Type type;
    private long currSizeOfBuffer;

    public SequentialFileInputChannel(FilterInstance src) {
        super(src,src);
        type = src.filter.type.output;
        receiveName = getIdent() + "_sender";
        receiveBuffer = getIdent() + "_send_buffer";
        receiveFile = getIdent() + "_file";
        receiveStart = getIdent() + "_send_start";
        if (src.initMult!=0)
            currSizeOfBuffer = getSizeOfInitBuffer();
        else
            currSizeOfBuffer = getSizeOfSteadyBuffer();
    }

    @Override
    public long getChannelMemSize() {
        return 0;
    }

    @Override
    public String getIdent() {
        return "FileInput_to_" + src.StreamNodeName();
    }

    private long getSizeOfSteadyBuffer() {
        return src.netSteady();
    }
    
    private long getSizeOfInitBuffer() {
        return src.initMult*src.push;
    }

    private long getMaxBufferSize() {
        return Math.max(getSizeOfInitBuffer(), getSizeOfSteadyBuffer());
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
        return getIdent() + "_push";
    }

    @Override
    public String PopString() {
        return ("(*" + receiveStart + "++)");
    }

    @Override
    public void Peek(Expression arg) {
    }

    @Override
    public void Push(Expression arg, boolean semicolon) {
        writer.print(PushMethodName() + "(");
        arg.Print();
        writer.print(")");
        if (semicolon) {
            writer.println(";");
        }
    }

    @Override
    public void Push(String arg, boolean semicolon) {
        writer.print(PushMethodName() + "(" + arg + ")");
        if (semicolon) {
            writer.println(";");
        }
    }

    @Override
    public void PopMethod() {
    }

    @Override
    public void PushMethod() {
        writer.println("void " + PushMethodName() + "(" + type.CType() + " arg)");
        writer.println("{");
        writer.println("    *" + receiveBuffer + "++ = arg;");
        writer.println("}");
        writer.println();
    }

    @Override
    public void PeekMethod() {
    }

    @Override
    public void InitRead() {
    }

    @Override
    public void InitWrite() {
        String filename = "input.stream";
        writer.writeLine(receiveFile + " = fopen(\"" + filename + "\", \"r\");");
        writer.writeLine("if (" + receiveFile + " == NULL) { fprintf(stderr, \"failed to open \\\"" + filename + "\\\"\\n\"); exit(1); }");
    }

    @Override
    public void ReadDecls() {
    }

    @Override
    public void WriteDecls() {
        writer.println("FILE *" + receiveFile + ";");
        writer.println(type + " *" + receiveStart + ";");
        writer.println(type + " " + receiveBuffer + "[" + getMaxBufferSize() + "];");
    }

    @Override
    public void GenerateInitWaitWrite() {
        assert false;
    }

    @Override
    public void GenerateInitWaitRead() {
        
    }

    @Override
    public void GenerateInitDoneWriting() {
        assert false;
    }

    @Override
    public void GenerateInitDoneReading() {
    }

    @Override
    public void GenerateSteadyWaitWrite() {
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 10; // fixme;
    }

    @Override
    public void GenerateSteadyWaitRead() {
        writer.writeLine("/* SteadyWaitWrite */");
        writer.writeLine(receiveStart + " = " + receiveBuffer + ";");
        writer.startBlock("for(int _i=0;_i<" + getSizeOfSteadyBuffer() + ";_i++)");
        writer.writeLine("// read file continously");
        writer.startBlock("if(fscanf(" + receiveFile + ",\"%X\",(uint32_t*)" + receiveStart + "++) == EOF)");
        writer.writeLine("rewind("+receiveFile+");");
        writer.writeLine("fscanf(" + receiveFile + ",\"%X\",(uint32_t*)" + receiveStart + "-1);");
        writer.endBlock();
        writer.endBlock();
        writer.writeLine(receiveStart + " = " + receiveBuffer + ";");
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        return 0;
    }

    @Override
    public void GenerateSteadyDoneWriting() {
  

    }

    @Override
    public int getDoneWriteCost(Chip chip) {
        return (int) getSizeOfSteadyBuffer() * 100; // fixme
    }

    @Override
    public void GenerateSteadyDoneReading() {
        writer.writeLine("/* SteadyDoneWriting */");
        writer.writeLine(receiveStart + " = " + receiveBuffer + ";");
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generateWaitRead() {
        writer.writeLine(receiveStart + " = " + receiveBuffer + ";");
        writer.startBlock("for(int _i=0;_i<" + currSizeOfBuffer+ ";_i++)");
        writer.writeLine("fscanf(" + receiveFile + ",\"%X\",(unsigned int *)" + receiveStart + "++);");
        writer.endBlock();
        writer.writeLine(receiveStart + " = " + receiveBuffer + ";");        
    }

    @Override
    public void generateDoneReading() {
        
    }

    @Override
    public void reconfigureForSteadyState() {
        currSizeOfBuffer = getSizeOfSteadyBuffer();
    }

}
