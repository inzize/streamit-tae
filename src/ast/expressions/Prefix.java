package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;

public class Prefix extends Expression0
{
    String op;
    Expression e;
    
    public Prefix(String op, Expression0 e)
    {
        this.op = op;
        this.e = new Expression(e);
    }
    
    @Override
    public void Print()
    {
        writer.print(op);
        e.Print();
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent) 
    {
        this.parent = parent;
        return type = e.AnalyseExpression(this);
    }
}
