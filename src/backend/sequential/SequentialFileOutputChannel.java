package backend.sequential;

import ast.expressions.Expression;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;

public class SequentialFileOutputChannel extends AbstractChannel {

    private final String sendBuffer, sendFile;
    private final String senderName, sendStart;
    private final Type type;

    public SequentialFileOutputChannel(FilterInstance src) {
        super(src, src);
        type = src.filter.type.input;
        senderName = getIdent() + "_sender";
        sendBuffer = getIdent() + "_send_buffer";
        sendFile = getIdent() + "_file";
        sendStart = getIdent() + "_send_start";
    }

    @Override
    public long getChannelMemSize() {
        return 0;
    }

    @Override
    public String getIdent() {
        return src.StreamNodeName() + "_to_FileOutput";
    }

    private long SizeofBuffer() {
        return src.netSteady();
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
        return "";
    }

    @Override
    public void Peek(Expression arg) {
    }

    @Override
    public void Push(Expression arg, boolean semicolon) {
        writer.write(PushMethodName() + "(");
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
        writer.println("    *" + sendBuffer + "++ = arg;");
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
        String filename = (String) src.args[0]; // src.filter.params.get(0).name
        filename += "_seq";

        writer.writeLine(sendFile + " = fopen(\"" + filename + "\", \"wb\");");
        writer.writeLine(sendStart + " =  (" + type + "*)malloc(" + this.SizeofBuffer() + "*" + SizeofItem() + ");");
        writer.writeLine("if (" + sendFile + " == NULL) { fprintf(stderr, \"failed to open \\\"" + filename + "\\\"\\n\"); exit(1); }");
    }

    @Override
    public void ReadDecls() {
    }

    @Override
    public void WriteDecls() {
        writer.println("FILE *" + sendFile + ";");
        writer.println(type + " *" + sendStart + ";");
        writer.println(type + " *" + sendBuffer + ";");
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
        writer.writeLine(sendBuffer + " = " + sendStart + ";");
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        return 10; // fixme;
    }

    @Override
    public void GenerateSteadyWaitRead() {
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        return 0;
    }

    @Override
    public void GenerateSteadyDoneWriting() {
        writer.startBlock("for (int _i=0; _i<" + SizeofBuffer() + "; _i++)");
        writer.writeLine("fprintf(" + sendFile + ",\"%08X\\n\",*(unsigned int*)&" + sendStart + "[_i]);");
        writer.endBlock();
    }

    @Override
    public int getDoneWriteCost(Chip chip) {
        return (int) SizeofBuffer() * 100; // fixme
    }

    @Override
    public void GenerateSteadyDoneReading() {
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }

    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
