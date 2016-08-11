package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.Container;
import ast.AbstractCode;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.List;

public class Return extends Statement0
{
    Expression value;

    public Return(Expression0 value)
    {
        this.value = new Expression(value);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("return ");
        value.Print();
        writer.println(";");
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
        value.AnalyseExpression(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return 0;
    }
}
