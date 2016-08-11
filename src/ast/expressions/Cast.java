package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Cast extends Expression0
{
    Type cast_type;
    Expression expr;
    
    public Cast(Type cast_type, Expression0 expr)
    {
        this.cast_type = cast_type;
        this.expr = new Expression(expr);
    }
    
    @Override
    public void Print()
    {
        writer.print('(');
        cast_type.PrintType();
        writer.print(')');
        expr.Print();
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        cast_type.Analyse(this);
        expr.AnalyseExpression(this);
        return type = cast_type;
    }

    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        return expr.AnalyseConstant(parent, filter);
    }
}
