package ast.programElements;

import ast.AbstractCode;
import ast.members.Function;
import java.util.*;

public class Helper extends ProgramElement
{
    String id;
    List<Function> functions;

    public Helper(String id, List<Function> functions)
    {
        this.id = id;
        this.functions = functions;
    }

    @Override
    public void GenerateInitCode()
    {
    }

    public void LiftDeclarations() 
    {
        for (Function f : functions)
            f.LiftDeclarations();
    }
        
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (Function f : functions)
            f.Analyse(this);
    }
}
