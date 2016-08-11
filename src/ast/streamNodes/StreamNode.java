package ast.streamNodes;

import ast.AbstractCode;

public interface StreamNode
{
    void GenerateInitCode();
    void Analyse(AbstractCode parent);
    void LiftDeclarations();
}
