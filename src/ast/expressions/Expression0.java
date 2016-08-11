package ast.expressions;

import ast.AbstractCode;
import ast.members.Function;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public abstract class Expression0 extends AbstractCode
{
    public Type type;
    
    public abstract void Print();
    public abstract Type AnalyseExpression(AbstractCode parent);

    @Override
    public final void Analyse(AbstractCode parent)
    {
        throw new UnsupportedOperationException("should call AnalyseExpression() instead");
    }

    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        throw new UnsupportedOperationException("Unsupported constant type " + getClass());
    }

    public Function AnalyseFunction(AbstractCode parent)
    {
        // Only the Identifier subclass supports this method.
        throw new UnsupportedOperationException("Cannot resolve function of type" + getClass());
    }


}
