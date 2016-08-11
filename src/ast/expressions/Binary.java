package ast.expressions;

import ast.AbstractCode;
import ast.types.NamedType;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Binary extends Expression0
{
    Expression e1, e2;
    String op;
    
    public Binary(Expression0 e1, String op, Expression0 e2)
    {
        this.e1 = new Expression(e1);
        this.op = op;
        this.e2 = new Expression(e2);
    }
    
    public void Print()
    {
        if (NamedType.Complex.equals(e1.gettype()) || NamedType.Complex.equals(e2.gettype()))
        {
            switch (op)
            {
                case "+":
                    writer.print("complex_add(");
                    break;
                case "-":
                    writer.print("complex_sub(");
                    break;
                case "*":
                    writer.print("complex_mul(");
                    break;
                case "/":
                    writer.print("complex_div(");
                    break;
                default:
            }
            if (!NamedType.Complex.equals(e1.gettype()))
            {
                writer.print("complex_real(");
                e1.Print();
                writer.print(")");
            }
            else
                e1.Print();

            writer.print(",");

            if (!NamedType.Complex.equals(e2.gettype()))
            {
                writer.print("complex_real(");
                e2.Print();
                writer.print(")");
            }
            else
                e2.Print();

            writer.print(")");
        }
        else
        {
            e1.Print();
            writer.print(op);
            e2.Print();
        }
    }     

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        e1.AnalyseExpression(this);
        return type = e2.AnalyseExpression(this);
    }

    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        switch (op)
        {
            case "*": return e1.AnalyseConstant(parent, filter) * e2.AnalyseConstant(parent, filter);
            case "/": return e1.AnalyseConstant(parent, filter) / e2.AnalyseConstant(parent, filter);
            case "+": return e1.AnalyseConstant(parent, filter) + e2.AnalyseConstant(parent, filter);
            case "-": return e1.AnalyseConstant(parent, filter) - e2.AnalyseConstant(parent, filter);
            default: throw new UnsupportedOperationException("unexpected operator");
        }
    }
}