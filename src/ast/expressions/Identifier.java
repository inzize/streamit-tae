package ast.expressions;

import ast.AbstractCode;
import ast.Decl;
import ast.members.Function;
import ast.statements.VarDecl;
import ast.Program;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;

public class Identifier extends Expression0
{
    String name;
    public Decl declaration;

    public Identifier(String name)
    {
        this.name = name;
    }

    @Override
    public void Print()
    {
        if (declaration != null) 
            if (GenerateC)
                writer.print("("+declaration.Name()+")"); //Don't trust this line
            else
                writer.print(declaration.Name()); //() doesn't work for function calls like sin() -> (sin)() <- 
        else
            writer.print(name);
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent)
    {
        this.parent = parent;

        if ("pi".equals(name))
            declaration = Program.pi;
        else
            declaration = Resolve(name);
        
        // just for debugging
        //if (declaration == null)
        //    declaration = Resolve(name);

        return type = ((VarDecl)declaration).type;
    }

    @Override
    public Function AnalyseFunction(AbstractCode parent)
    {
        this.parent = parent;
        declaration = parent.ResolveFunction(name);
        return (Function)declaration;
    }
    
    @Override
    public int AnalyseConstant(AbstractCode parent, FilterInstance filter)
    {
        AnalyseExpression(parent);
        Expression value = ((VarDecl)declaration).value;
        if (value != null)
            return value.AnalyseConstant(parent, filter);
        else
        {
            for (int i=0; i< filter.filter.params.size(); i++)
                if (filter.filter.params.get(i) == declaration)
                    return (int)filter.args[i];
            
            throw new UnsupportedOperationException("Can't resolve array size" + declaration.Name());
        }
    }
}
