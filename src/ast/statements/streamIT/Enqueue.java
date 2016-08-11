package ast.statements.streamIT;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

// Used in feedback loop

public class Enqueue extends Statement0
{
    Expression value;

    public Enqueue(Expression0 value)
    {
        this.value = new Expression(value);
    }
    
    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }    

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print(container.variable + ".enqueued.add(");
        value.Print();
        writer.print(");");
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
        value.Analyse(this);
    }
}
