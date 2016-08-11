package ast.streamNodes;


import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement;
import ast.statements.Statement0;
import ast.types.streamIT.StreamType;

public class AnonPipeline extends Container implements StreamNode
{
    StreamType type;
    Statement block;
    
    public AnonPipeline(StreamType type, Statement0 block)
    {
        this.type = type;
        this.block = new Statement(block);
    }

    @Override
    public void GenerateInitCode()
    {
        block.Print(true, this);
    }

    @Override
    public void CreateNode()
    {
        writer.println("Pipeline " + variable + "=new Pipeline();");
    }
    
    @Override
    public void LiftDeclarations()
    {
        block.LiftDeclarations();
    }    

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        block.Analyse(this);
    }
}
