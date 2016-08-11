package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class ExpressionStmt extends Statement0
{
    Expression e;

    public ExpressionStmt(Expression0 e)
    {
        this.e = new Expression(e);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        e.Print();
        if (semicolon)
            writer.println(";");
    }
    
    public int MemoryUsage(FilterInstance filter)
    {
        return 0;
    }
    
    /**
     *
     * @return
     */
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        return new ArrayList<>(); 
    }
    

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        e.AnalyseExpression(this);
    }
}
