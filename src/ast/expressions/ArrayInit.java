package ast.expressions;

import ast.AbstractCode;
import ast.types.Array;
import ast.types.Type;
import java.util.*;

public class ArrayInit extends Expression0
{
    public List<Expression> elements;
    
    public ArrayInit(List<Expression> elements)
    {   
        this.elements = elements;
    }
    
    @Override
    public void Print()
    {
        writer.print('{');
        boolean first = true;
        for (Expression e : elements)
        {
            if (!first)
                writer.print(',');
            else
                first = false;
            e.Print();
        }
        writer.print('}');
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        for (Expression e : elements)
            e.AnalyseExpression(this);
        return type = new Array(elements.get(0).AnalyseExpression(this), new Constant(elements.size()));
    }
}
