package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;

public class PostFix extends Expression0
{
    Expression e;
    String op;
    
    public PostFix(Expression0 e, String op)
    {
        this.e = new Expression(e);
        this.op = op;
    }
    
    @Override
    public void Print()
    {
        e.Print();
        writer.print(op);
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        return type = e.AnalyseExpression(this);
    }
}
