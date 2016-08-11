package ast.expressions.streamIT;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.types.NamedType;
import ast.types.Type;

public class Float2 extends Expression0
{
    Expression f1, f2;

    public Float2(Expression0 f1, Expression0 f2)
    {
        this.f1 = new Expression(f1);
        this.f2 = new Expression(f2);
    }

    @Override
    public void Print()
    {
        // Fixme
        throw new UnsupportedOperationException();
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent) 
    {
        this.parent = parent;
        f1.AnalyseExpression(this);
        f2.AnalyseExpression(this);
        return new NamedType("float2");
    }
}
