package ast.expressions.streamIT;

import ast.AbstractCode;
import ast.expressions.Expression0;
import ast.types.Type;
import ast.Filter;
import hierarchicalGraph.FilterInstance;

public class Pop extends Expression0
{
    public static FilterInstance filter;

    @Override
    public void Print()
    {
        filter.GeneratePop(false);        
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;        
        while (!(parent instanceof Filter))
            parent = parent.parent;
        return ((Filter)parent).type.input;

    }
}
