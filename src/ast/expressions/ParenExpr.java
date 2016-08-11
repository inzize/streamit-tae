package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class ParenExpr extends Expression0
{
    Expression child;
    
    public ParenExpr(Expression0 child)
    {
        this.child = new Expression(child);
    }
    
    @Override
    public void Print()
    {
        writer.print('(');
        child.Print();
        writer.print(')');
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        return type = child.AnalyseExpression(this);
    }

    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        return child.AnalyseConstant(parent, filter);
    }
}
