package ast.statements.streamIT;

import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import ast.streamNodes.StreamNode;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Body extends Statement0
{
    StreamNode node;

    public Body(StreamNode node)
    {
        this.node = node;
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print(container.variable + ".body=");
        node.GenerateInitCode();
        writer.print(";");
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        //node.LiftDeclarations();
        return new ArrayList<>(); 
    }
    

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        node.Analyse(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }
}
