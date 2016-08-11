package ast.programElements;

import ast.statements.Statement;
import ast.statements.Block;
import ast.AbstractCode;
import ast.members.Init;
import ast.statements.Statement0;
import ast.statements.VarDecl;
import java.util.*;

public class Static extends ProgramElement
{
    public List<VarDecl> decls = new ArrayList<>();
    public Init init = null;

    @Override
    public void GenerateInitCode()
    {
        for (VarDecl decl : decls)
            decl.GenerateInitCode();
        if (init != null)
            init.GenerateInitCode();
        writer.println();
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (VarDecl var : decls)
            if (var.original_name.equals(name))
                return var;

        return super.Resolve(name);
    }

    @Override
    public void LiftDeclarations() 
    {
        List<Statement0> inits = new ArrayList<>();
        
	List<VarDecl> temp_var_decls = new ArrayList<>();
        for (VarDecl v : decls)
            inits.addAll(v.ReplaceDeclarations(temp_var_decls));
        
	decls.addAll(temp_var_decls);

        if (init != null)
        {
            List<VarDecl> decls = init.LiftDeclarations();
            inits.addAll(decls);        
        }
        
        if (inits.size() > 0)
        {
            if (init == null)
                init = new Init(new Block(new ArrayList<Statement>()));
            for (int i=0; i<inits.size(); i++)
                init.body.stmts.add(i, new Statement(inits.get(i)));
        }           
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (VarDecl decl : decls)
            decl.Analyse(this);
        if (init != null)
            init.Analyse(this);
    }
}
