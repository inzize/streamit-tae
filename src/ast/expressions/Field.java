package ast.expressions;

import ast.AbstractCode;
import ast.types.Type;

public class Field extends Expression0
{
    Expression obj;
    String field;
    
    public Field(Expression0 obj, String field)
    {
        this.obj = new Expression(obj);
        this.field = field;
    }
    
    @Override
    public void Print()
    {
        obj.Print();
        writer.print('.');
        writer.print(field);
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        Type cls = obj.AnalyseExpression(this);
        
        return type = cls.Field(field);
    }
}
