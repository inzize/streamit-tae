package ast.expressions.streamIT;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.types.Type;

public class Range extends Expression0
{
    Expression e1, e2, e3;

    public Range(Expression0 e1, Expression0 e2, Expression0 e3)
    {
        throw new UnsupportedOperationException("Dynamic rates not supported yet.");
    }

    @Override
    public void Print()
    {
        // Fixme
        if (e3 != null)
            e3.Print();
        else
            e1.Print();
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent) 
    {
        this.parent = parent;
        e1.AnalyseExpression(this);
        e2.AnalyseExpression(this);
        if (e3 != null)
            e3.AnalyseExpression(this);

        throw new UnsupportedOperationException("Dynamic rates not supported yet.");
    }
}
