package ast.expressions.streamIT;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.types.NamedType;
import ast.types.Type;

public class Float3 extends Expression0
{
    Expression f1, f2, f3;

    public Float3(Expression0 f1, Expression0 f2, Expression0 f3)
    {
        this.f1 = new Expression(f1);
        this.f2 = new Expression(f2);
        this.f3 = new Expression(f3);
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
        f3.AnalyseExpression(this);
        return new NamedType("float3");
    }
}
