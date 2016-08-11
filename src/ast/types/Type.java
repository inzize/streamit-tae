package ast.types;

import ast.AbstractCode;
import ast.expressions.Expression0;
import hierarchicalGraph.FilterInstance;

public abstract class Type extends AbstractCode
{
    public void PrintType()
    {
        if (AbstractCode.GenerateC)
            PrintCType();
        else
            PrintJavaType();
    }
    public abstract void PrintJavaType();
    public abstract void PrintCType();
    public abstract String CType();
    public abstract void Allocate();
    public abstract boolean IsVoid();
    public abstract Type base();
    public abstract String Format();
    public abstract Type Field(String fieldname);
    public abstract int SizeOf(FilterInstance filter);
    public abstract Expression0 SomeValue();
}
