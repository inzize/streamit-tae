package ast.expressions;

import ast.AbstractCode;
import ast.types.NamedType;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Constant extends Expression0
{
    Object value;
            
    public Constant(Object value)
    {
        this.value = value;
    }
    
    @Override
    public void Print()
    {   if (value instanceof Boolean && AbstractCode.GenerateC)
            writer.print((Boolean)value ? 1 : 0);
        else
        {
            writer.print(value);
            if (value instanceof java.lang.Float)
                writer.print('f');
        }
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        if (value instanceof Float)
            return type = NamedType.Float;
        else if (value instanceof Integer || value instanceof Boolean)
            return type = NamedType.Int32_t;
        else if (value instanceof String)
            return type = NamedType.String;
        else
            throw new UnsupportedOperationException("Unexpected type");
    }

    @Override
    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        return (int)value;
    }
}
