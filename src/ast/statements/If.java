package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.Container;
import ast.AbstractCode;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class If extends Statement0
{
    Expression cond;
    Statement Then, Else;

    public If(Expression0 cond, Statement0 Then, Statement0 Else)
    {
        this.cond = new Expression(cond);
        this.Then = new Statement(Then);
        if (Else != null)
            this.Else = new Statement(Else);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("if (");
        cond.Print();
        writer.println(")");
        writer.write(writer.INDENT_STR);
        Then.Print(true, container);

        if (Else != null)
        {
            writer.println();
            writer.writeLine("else");
            writer.write(writer.INDENT_STR);
            Else.Print(true, container);
        }
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        Then.LiftDeclarations();
        if (Else != null)
            Else.LiftDeclarations();
        return new ArrayList<>();
    }
    

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        cond.AnalyseExpression(this);
        Then.Analyse(this);
        if (Else != null)
            Else.Analyse(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return Then.MemoryUsage(filter) + (Else != null ? Else.MemoryUsage(filter) : 0);
    }
}
