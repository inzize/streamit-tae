package ast.statements;

import ast.expressions.Expression;
import ast.expressions.Expression0;
import ast.AbstractCode;
import ast.Container;
import ast.Decl;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class For extends Statement0
{
    List<Statement> init;
    Statement incr, body;
    Expression cond;

    public For(List<Statement0> init, Expression0 cond, Statement0 incr, Statement0 body)
    {
        this.init = new ArrayList<>();
        for (Statement0 s : init)
            this.init.add(new Statement(s));
        
        this.cond = new Expression(cond);
        this.incr = new Statement(incr);
        this.body = new Statement(body);
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.print("for (");
        for (Statement s : init)
            s.Print(false, container);
        writer.print("; ");
        cond.Print();
        writer.print("; ");
        incr.Print(false, container);
        writer.println(")");
        writer.write(writer.INDENT_STR);
        body.Print(true, container);
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {
        List<VarDecl> list = new ArrayList<>();
        for (Statement s : init)
            list.addAll(s.LiftDeclarations());
        list.addAll(body.LiftDeclarations());
        return list; 
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (Statement s : init)
            s.Analyse(this);
        cond.AnalyseExpression(this);
        incr.Analyse(this);
        body.Analyse(this);
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (Statement s : init)
        {
            VarDecl v = s.FindVarDecls(name);
            if (v != null)
                return v;
        }
        return super.Resolve(name);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        int total = 0;
        for (Statement s : init)
            total += s.MemoryUsage(filter);        
        total += body.MemoryUsage(filter);
        return total;
    }
}
