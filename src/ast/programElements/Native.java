package ast.programElements;

import ast.AbstractCode;
import java.util.*;

public class Native extends ProgramElement
{
    String id;
    List<NativeFunction> functions;

    public Native(String id, List<NativeFunction> functions)
    {
        this.id = id;
        this.functions = functions;
    }

    @Override
    public void GenerateInitCode()
    {
    }

    @Override
    public void LiftDeclarations()
    {
        throw new UnsupportedOperationException("Native::LiftDeclarations - not supported yet.");        
    }    
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        throw new UnsupportedOperationException("Native::Analyse - not supported yet.");
    }
}
