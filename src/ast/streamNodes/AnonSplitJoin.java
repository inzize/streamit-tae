package ast.streamNodes;

import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement;
import ast.statements.Statement0;
import ast.types.streamIT.StreamType;

public class AnonSplitJoin extends Container implements StreamNode
{
    StreamType type;
    Statement block;
    
    public AnonSplitJoin(StreamType type, Statement0 block)
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
        writer.println("SplitJoin " + variable + "=new SplitJoin();");
    }

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
