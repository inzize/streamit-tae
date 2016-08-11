package ast.programElements;

import ast.AbstractCode;
import ast.statements.VarDecl;
import java.util.*;

public class Struct extends ProgramElement
{
    String name;
    List<VarDecl> fields;

    public Struct(String name, List<VarDecl> fields)
    {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public void GenerateInitCode()
    {
    }

    @Override
    public void LiftDeclarations()
    {       
    }    
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (VarDecl decl : fields)
            decl.Analyse(this);
    }
}
