package ast.statements.streamIT;

import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import ast.streamNodes.NamedStream;
import ast.streamNodes.StreamNode;
import ast.streamNodes.AnonFilter;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;


public class Add extends Statement0
{
    StreamNode node;

    public Add(StreamNode node)
    {
        this.node = node;
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        if (node instanceof AnonFilter)
        {
            writer.print(container.variable + ".children.add(");
            node.GenerateInitCode();
            writer.print(");");
        }
        else if (node instanceof NamedStream)
        {
            writer.print(container.variable + ".children.add(");
            node.GenerateInitCode();
            writer.print(");");
        }
        else
        {
            writer.println("{");

            Container anon = (Container)node;
            anon.CreateNode();

            writer.println(container.variable + ".children.add(" + anon.variable + ");");

            node.GenerateInitCode();
            writer.println();

            writer.print('}');
        }
    }

     @Override
    public List<VarDecl> LiftDeclarations()
    {
        node.LiftDeclarations();
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
