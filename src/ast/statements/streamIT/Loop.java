package ast.statements.streamIT;

import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import ast.streamNodes.StreamNode;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Loop extends Statement0
{
    StreamNode node;

    public Loop(StreamNode node)
    {
        this.node = node;
    }
    
    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }    

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print(container.variable + ".loop=");
        node.GenerateInitCode();
        writer.print(";");
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        // node.LiftDeclarations();
        return new ArrayList<>();  
    }
    

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        node.Analyse(this);
    }
}
