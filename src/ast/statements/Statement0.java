package ast.statements;

import ast.*;
import hierarchicalGraph.FilterInstance;
import java.util.*;

public abstract class Statement0 extends AbstractCode
{
    public abstract void Print(boolean semicolon, Container container);
    public abstract List<VarDecl> LiftDeclarations();
    public abstract int MemoryUsage(FilterInstance filter);
}
