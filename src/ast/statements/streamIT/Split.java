package ast.statements.streamIT;

import ast.AbstractCode;
import ast.Container;
import ast.misc.Splitter;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Split extends Statement0
{
    Splitter schedule;

    public Split(Splitter schedule)
    {
        this.schedule = schedule;
    }
    
    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }    

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print(container.variable + ".splitter=");
        schedule.Print();
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
        schedule.Analyse(this);
    }
}
