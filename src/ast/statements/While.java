package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class While extends Statement0
{
    Expression cond;
    Statement body;

    public While(Expression0 cond, Statement0 body)
    {
        this.cond = new Expression(cond);
        this.body = new Statement(body);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("while");
        writer.print('(');
        cond.Print();
        writer.print(')');
        writer.println();
        body.Print(true, container);

    }

    @Override
    public List<VarDecl> LiftDeclarations()
    {
        body.LiftDeclarations();
        return new ArrayList<>(); 
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        cond.AnalyseExpression(this);
        body.Analyse(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return body.MemoryUsage(filter);
    }
}
