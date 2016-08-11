package backend.common.channels;

import ast.expressions.Expression;
import ast.types.Type;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.estimators.CommunicationModel;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;

public class MemoryChannel extends AbstractChannel
{
    private Type type;
    private String bufferName, tailName, headName;
    
    public MemoryChannel(FilterInstance src, FilterInstance dst)
    {
        super(src, dst);
        this.type = dst.filter.type.input;
        this.bufferName = getIdent() + "_buffer";
        this.headName = getIdent() + "_head";
        this.tailName = getIdent() + "_tail";
        
        int iterations = dst.primePumpCount - src.primePumpCount;
        this.bufferSize = Math.max(InitItemsPush(), 
                InitItemsPush() - InitItemsPop() + iterations * SteadyItemsPushPop() + 
                        Math.max(SteadyItemsPeek(), SteadyItemsPushPop()));
        numberOfBuffers = 1;
    }    
    
    @Override
    public long getChannelMemSize() {
        return bufferSize * type.SizeOf(dst);
    }
    
    @Override
    public long getMemUsedSrc() {
        long mem = getChannelMemSize();
        return mem + 4 + 4; // head and tail of buffer

    }

    @Override
    public long getMemUsedDst() {
        return 4 + 4; // head and tail of buffer     
    }


    public void AssertInitCount()
    {
    }

    public void AssertSteadyCount()
    {
    }   
    
    public String getIdent()
    {
        return src.StreamNodeName() + "_to_" + dst.StreamNodeName();
    }
    
    private String PopMethodName()
    {
        return getIdent() + "_memory_pop";
    }

    private String PeekMethodName()
    {
        return getIdent() + "_memory_peek";        
    }    
    
    private String PushMethodName()
    {
        return getIdent() + "_memory_push";        
    }
    
    @Override
    public String PopString()
    {
        // inlined
        return bufferName + "[" + tailName + "++ % " + bufferSize + "]"; 
            
        //writer.print(PopMethodName() + "()");
    }

    @Override
    public void Peek(Expression arg)
    {
        // inlined
        writer.print(bufferName + "[(" + tailName + " + (");
        arg.Print();
        writer.print(")) % " + bufferSize + "]");
                
        //writer.print(PeekMethodName() + "(");
        //arg.Print();
        //writer.print(")");
    }

    @Override
    public void Push(Expression arg, boolean semicolon)
    {
        // inlined
        writer.startBlock("");
        writer.writeLine(bufferName + "[" + headName + "] = "); arg.Print(); writer.println(";");
        writer.writeLine("if(++" + headName + "==" + bufferSize + ") " + headName + " = 0;");
        writer.endBlock();      
        
        //writer.print(PushMethodName() + "(");
        //arg.Print();
        //writer.print(")");
    }
    
    @Override
    public void Push(String arg, boolean semicolon)
    {
        // inlined
        writer.startBlock("");
        writer.writeLine(bufferName + "[" + headName + "] = " + arg + ";");
        writer.writeLine("if(++" + headName + "==" + bufferSize + ") " + headName + " = 0;");
        writer.endBlock();
        
        //writer.print(PushMethodName() + "(");
        //arg.Print();
        //writer.print(")");
    }    
    
    @Override
    public void PopMethod()
    {
        // inlined above instead
        /*
        type.PrintCType();
        writer.println(" " + PopMethodName() + "()");
        writer.println("{");
        writer.print("    ");
        type.PrintCType();
        writer.println(" tmp = " + bufferName + "[" + tailName + "];");
        //writer.println("    " + tailName + " = (" + tailName + " + 1) % " + bufferSize + ";");
        writer.println("    if (++" + tailName + "==" + bufferSize + ")");
        writer.println("    " + tailName + "=0;");        
        writer.println("    return tmp;");
        writer.println("}");
        writer.println();
        */
    }

    @Override
    public void PushMethod()
    {
        // inlined above instead
        /*
        writer.print("void " + PushMethodName() + "(");
        type.PrintCType();
        writer.println(" arg)");
        writer.println("{");
        writer.println("    " + bufferName + "[" + headName + "] = arg;");
        writer.println("    if (++" + headName + "==" + bufferSize + ")");
        writer.println("    " + headName + "=0;");
        //writer.println("    " + headName + " = (" + headName + " + 1) % " + bufferSize + ";");
        writer.println("}");
        writer.println();
        */
    }

    @Override
    public void PeekMethod()
    {
        type.PrintCType();
        writer.println(" " + PeekMethodName() + "(int offset)");
        writer.println("{");
        writer.println("    return " + bufferName + "[(" + tailName + " + offset) % " + bufferSize + "];");
        writer.println("}");
        writer.println();
    }

    @Override
    public void InitRead()
    {   
    }

    @Override
    public void InitWrite()
    {      
    }

    @Override
    public void ReadDecls()
    {        
        type.PrintCType();
        writer.println(" " + bufferName + "[" + bufferSize + "];");
        writer.println("int " + tailName + " = 0;");
    }

    @Override
    public void WriteDecls()
    {
        writer.println("int " + headName + " = 0;");
    }

    @Override
    public void GenerateInitWaitRead()
    {
    }

    @Override
    public void GenerateInitWaitWrite()
    {     
    }

    @Override
    public void GenerateInitDoneReading()
    {    
    }

    @Override
    public void GenerateInitDoneWriting()
    {        
    }
    
    @Override
    public void GenerateSteadyWaitRead()
    {
    }

    @Override
    public int getWaitReadCost(Chip chip) 
    {
        assert(src == dst);
        return 0;
    }     
    
    @Override
    public void GenerateSteadyWaitWrite()
    {     
    }
    
    @Override
    public int getWaitWriteCost(Chip chip) 
    {
        assert(src == dst);
        return 0;
    }    

    @Override
    public void GenerateSteadyDoneReading()
    {    
    }

    @Override
    public int getDoneReadCost(Chip chip) 
    {
        assert(src == dst);
        return 0;
    }    
    
    @Override
    public void GenerateSteadyDoneWriting()
    {        
    }    

     @Override
    public int getDoneWriteCost(Chip chip) 
    {
        assert(src == dst);
        return 0;
    }     

    @Override
    public long TransmissionTime(CommunicationModel model) 
    {
        return 0;
    }
}