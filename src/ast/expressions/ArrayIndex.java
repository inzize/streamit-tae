package ast.expressions;

import ast.AbstractCode;
import ast.types.Array;
import ast.types.Type;

public class ArrayIndex extends Expression0
{
    Expression array;
    Expression index;
    
    public ArrayIndex(Expression0 array, Expression0 index)
    {
        this.array = new Expression(array);
        this.index = new Expression(index);
    }
    
    public void Print()
    {
        array.Print();
        writer.print('[');
        index.Print();
        writer.print(']');
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        Type arrayType = array.AnalyseExpression(this);
        index.AnalyseExpression(this);

        return type = ((Array)arrayType).elementType;
    }
}
