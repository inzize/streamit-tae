package ast.streamNodes;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.types.Type;
import java.util.List;

public class NamedStream extends AbstractCode implements StreamNode
{
    String name;
    Type type;
    List<Expression> args;
    
    public NamedStream(String name, Type type, List<Expression> args)
    {
        this.name = name;
        this.type = type;
        this.args = args;
    }

    @Override
    public void GenerateInitCode()
    {
        if (type != null)
        {
            writer.print("new FilterInstance(\"" + name + "\", \"" + type + "\"");
            for (Expression e : args)
            {
                writer.print(',');
                e.Print();
            }
            writer.print(")");            
        }
        else
        {
            writer.print(name);
            writer.print('(');
            boolean first = true;
            if (args != null)
                for (Expression e : args)
                {
                    if (first)
                        first = false;
                    else
                        writer.print(',');
                    e.Print();
                }
            writer.print(")");
        }
    }

    @Override
    public void LiftDeclarations()
    {
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        if (args != null)
            for (Expression e : args)
                e.AnalyseExpression(this);
    }
}
