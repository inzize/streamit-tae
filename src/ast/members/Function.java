package ast.members;

import ast.AbstractCode;
import ast.Decl;
import ast.expressions.Expression;
import ast.statements.VarDecl;
import ast.statements.Statement;
import ast.types.Type;
import java.util.List;

public class Function extends AbstractCode implements Member, Decl
{
    public String original_name;
    public String name;
    public Type returntype;
    public List<VarDecl> params;
    public Expression push, pop, peek;
    public Statement body;
    
    public Function(Type returntype, String name, List<VarDecl> params)
    {
        this.name = this.original_name = name;
        this.returntype = returntype;
        this.params = params;
    }

    @Override
    public void GenerateInitCode()
    {
        throw new UnsupportedOperationException("Function::GenerateInitCode - not supported yet.");
    }

    public void LiftDeclarations() 
    {
        body.LiftDeclarations();
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        returntype.Analyse(this);
        for (VarDecl decl : params)
            decl.Analyse(this);
        if (push != null)
            push.Analyse(this);
        if (pop != null)
            pop.Analyse(this);
        if (peek != null)
            peek.Analyse(this);
        body.Analyse(this);
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (VarDecl var : params)
            if (var.original_name.equals(name))
                return var;

        return parent.Resolve(name);
    }

    public void Print()
    {
        returntype.PrintType();
        writer.print(' ');
        writer.print(name);
        writer.print('(');
        boolean first = true;
        for (VarDecl decl : params)
        {
            if (first)
                first = false;
            else
                writer.print(',');
            decl.PrintParam();
        }
        writer.println(")");
        body.Print(false, null);
    }

    @Override
    public String Name() 
    {
        return name;
    }
}
