package ast.expressions.streamIT;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.Filter;
import ast.members.Work;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Peek extends Expression0
{
    Expression e;
    public static FilterInstance filter;

    public Peek(Expression0 e)
    {
        this.e = new Expression(e);
    }

    @Override
    public void Print()
    {
        filter.GeneratePeek(e);
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent) 
    {
        this.parent = parent;
        
        AbstractCode p = parent;
        while (p != null && !(p instanceof Work))
            p = p.parent;
        if (p != null)
            ((Work)p).usesPeek = true;
        
        e.AnalyseExpression(this);
        while (!(parent instanceof Filter))
            parent = parent.parent;
        return ((Filter)parent).type.input;
    }
}
