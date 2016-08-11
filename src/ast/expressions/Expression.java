package ast.expressions;

import ast.AbstractCode;
import ast.Program;
import ast.members.Function;
import ast.types.NamedType;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Expression extends AbstractCode
{
    public Expression0 child;

    public Expression(Expression0 child)
    {
        this.child = child;
    }

    public Type gettype()
    {
        return child.type;
    }

    public void Print()
    {
        if (NamedType.Complex.equals(child.type))
            Program.useComplex = true;
        child.Print();
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        throw new UnsupportedOperationException("should call AnalyseExpression() instead");
    }

    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        return child.AnalyseExpression(this);
    }

    public Function AnalyseFunction(AbstractCode parent)
    {
        this.parent = parent;
        return child.AnalyseFunction(this);
    }

    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        this.parent = parent;
        return child.AnalyseConstant(this, filter);
    }
}
