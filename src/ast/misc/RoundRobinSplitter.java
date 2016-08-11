package ast.misc;

import ast.AbstractCode;
import ast.expressions.Expression;
import java.util.List;


public class RoundRobinSplitter extends Splitter
{
    List<Expression> list;

    public RoundRobinSplitter(List<Expression> list)
    {
        this.list = list;
    }

    @Override
    public void Print()
    {
        writer.print("new RoundRobinSplitter(");
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
