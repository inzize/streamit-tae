package ast.members;

import ast.AbstractCode;
import ast.expressions.Expression;
import ast.expressions.Constant;
import ast.statements.Block;
import hierarchicalGraph.FilterInstance;

public class Work extends AbstractCode implements Member
{
    public Expression push, pop, peek;
    public Block body;
    public boolean usesPeek = false;
    
    public Work()
    {     
    }

    public Work(Integer pop, Integer push)
    {
        this.pop = new Expression(new Constant(pop.toString()));
        this.push = new Expression(new Constant(push.toString()));
    }
    
    public int MemoryUsage(FilterInstance filter)
    {
        return body.MemoryUsage(filter);
    }

    @Override
    public void GenerateInitCode()
    {
        throw new UnsupportedOperationException("Work::GenerateInitCode - not supported yet.");
    }

    public void LiftDeclarations()
    {
        if (body != null)
            body.LiftDeclarations();        
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        if (body != null)
            body.Analyse(this);
        if (push != null)
            push.AnalyseExpression(this);
        if (pop != null)
            pop.AnalyseExpression(this);
        if (peek != null)
            peek.AnalyseExpression(this);
    }
}