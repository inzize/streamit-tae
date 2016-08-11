package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;
import ast.types.Type;

public class Ternary extends Expression0
{
    Expression cond, Then, Else;
    
    public Ternary(Expression0 cond, Expression0 Then, Expression0 Else)
    {
        this.cond = new Expression(cond);
        this.Then = new Expression(Then);
        this.Else = new Expression(Else);
    }
    
    @Override
    public void Print()
    {
        cond.Print();
        writer.print('?');
        Then.Print();
        writer.print(':');
        Else.Print();
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        cond.AnalyseExpression(this);
        Then.AnalyseExpression(this);
        return type = Else.AnalyseExpression(this);
    }
}
