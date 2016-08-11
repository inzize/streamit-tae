package ast.statements.streamIT;

import ast.AbstractCode;
import ast.Container;
import ast.misc.RoundRobinJoiner;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Join extends Statement0
{
    RoundRobinJoiner joiner;

    public Join(RoundRobinJoiner joiner)
    {
        this.joiner = joiner;
    }
    
    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }    

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print(container.variable + ".joiner=");
        joiner.Print();
        writer.print(";");
    }

    @Override
    public List<VarDecl> LiftDeclarations()
    {
        return new ArrayList<>(); 
    }
    
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        joiner.Analyse(this);
    }
}
