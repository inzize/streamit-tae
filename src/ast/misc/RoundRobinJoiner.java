package ast.misc;

import ast.AbstractCode;
import ast.expressions.Expression;
import java.util.*;

public class RoundRobinJoiner extends AbstractCode
{
    List<Expression> list;

    public RoundRobinJoiner(List<Expression> list)
    {
        this.list = list;
    }

    public void Print()
    {
        writer.print("new RoundRobinJoiner(");
        boolean first = true;
        for (Expression e : list)
        {
            if (first)
                first = false;
            else
                writer.print(',');
            e.Print();
        }
        writer.print(')');
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (Expression e : list)
            e.AnalyseExpression(this);
    }
}
