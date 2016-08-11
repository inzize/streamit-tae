package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.*;

public class Assignment extends Statement0
{
    public Expression lhs, rhs;
    String op;

    public Assignment(Expression0 lhs, String op, Expression0 rhs)
    {
        this.lhs = new Expression(lhs);
        this.op = op;
        this.rhs = new Expression(rhs);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        lhs.Print();
        writer.print(op);
        rhs.Print();
        if (semicolon)
            writer.println(";");
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        return new ArrayList<>();
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        lhs.AnalyseExpression(this);
        rhs.AnalyseExpression(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter)
    {
        return 0;
    }
}
