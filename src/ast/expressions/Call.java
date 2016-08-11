package ast.expressions;

import ast.AbstractCode;
import ast.members.Function;
import ast.types.Type;
import java.util.*;



public class Call extends Expression0
{
    Expression function;
    Function function_type;
    List<Expression> args;
    
    public Call(Expression0 function, List<Expression> args)
    {
        this.function = new Expression(function);
        this.args = args;
    }
    
    @Override
    public void Print()
    {
        if ("print".equals(function_type.name) || "println".contains(function_type.name))
        {
            writer.print("printf(\"");
            for (Expression e : args)
                writer.print(e.AnalyseExpression(this).Format());
            if ("println".equals(function_type.name))
                writer.print("\\n");
            writer.print("\", ");
        }
        else
        {
            function.Print();
            writer.print('(');
        }

            boolean first = true;
            int i = 0;
            for (Expression e : args)
            {
                if (!first)
                    writer.print(',');
                else
                    first = false;
                if (function_type.params != null)
                {
                    Type parameter_type = function_type.params.get(i).type;
                    if (!parameter_type.equals(e.gettype()))
                    {
                        writer.print("(" + parameter_type + ")(");
                        e.Print();
                        writer.print(")");
                    }
                    else
                        e.Print();
                }
                else
                    e.Print();
                
                i++;
            }
            writer.print(')');
        }

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;
        for (Expression e : args)
            e.AnalyseExpression(this);
        function_type = function.AnalyseFunction(this);
        return type = function_type.returntype;
    }
}
