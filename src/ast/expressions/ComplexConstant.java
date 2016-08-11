package ast.expressions;

import ast.AbstractCode;
import ast.types.NamedType;
import ast.types.Type;

public class ComplexConstant extends Constant
{
    float imag;

    public ComplexConstant(String text)
    {
        super(null);
        this.imag = Float.parseFloat(text.substring(0, text.length()-1));
    }

    @Override
    public void Print()
    {
        writer.print("complex_imag(" + imag + "f)");
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        return type = NamedType.Complex;
    }
}
