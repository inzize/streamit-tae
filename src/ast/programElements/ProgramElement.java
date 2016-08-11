package ast.programElements;

import ast.AbstractCode;

public abstract class ProgramElement extends AbstractCode
{
    public abstract void GenerateInitCode();
    public abstract void LiftDeclarations();
}
