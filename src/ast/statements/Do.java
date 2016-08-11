package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.Container;
import ast.AbstractCode;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Do extends Statement0
{
    Statement body;
    Expression cond;

    public Do(Statement0 body, Expression0 cond)
    {
        this.body = new Statement(body);
        this.cond = new Expression(cond);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("do");
        writer.println();
        body.Print(true, container);
        writer.print("while");
        writer.print('(');
        cond.Print();
        writer.print(");");
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
