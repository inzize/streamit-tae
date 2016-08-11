package ast.statements.streamIT;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Push extends Statement0
{
    Expression node;
    public static FilterInstance filter;

    public Push(Expression0 node)
    {
        this.node = new Expression(node);
    }

    public void Print(boolean semicolon, Container container)
    {
        filter.GeneratePush(node, semicolon);     
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
        node.AnalyseExpression(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }
}
