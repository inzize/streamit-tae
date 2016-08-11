package ast.statements;

import ast.Container;
import ast.AbstractCode;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Break extends Statement0
{
    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("break;");
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
    }

    @Override
    public int MemoryUsage(FilterInstance filter) {
        return 0;
    }
}
