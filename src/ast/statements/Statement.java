package ast.statements;

import ast.*;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.*;

public class Statement extends AbstractCode
{
    public List<Statement0> children = new ArrayList<>();

    public Statement(Statement0 child)
    {
        this.children.add(child);
    }
    
    public int MemoryUsage(FilterInstance filter)
    {
        int total = 0;
        for (Statement0 stmt : children)
            total += stmt.MemoryUsage(filter);
        return total;
    }

    @Override
    public void Analyse(AbstractCode parent) 
    {
        this.parent = parent;
        for (Statement0 child : children)
            child.Analyse(this);        
    }
    
    
    public void Replace(Statement0 old, List<Statement0> newChildren)
    {
        int i = children.indexOf(old);
        children.remove(i);
        children.addAll(i, newChildren);
    }
    
    public VarDecl FindVarDecls(String name)
    {
        for (Statement0 child : children)
            if (child instanceof VarDecl)
            {
                VarDecl decl = (VarDecl) child;
                VarDecl v = decl.FindVarDecls(name);
                if (v != null)
                    return v;
            }
        return null;
    }
    
    public void Print(boolean semicolon, Container container)
    {
        for (Statement0 child : children)
            child.Print(semicolon, container);  
    }
    
    public List<VarDecl> LiftDeclarations()
    {
        List<VarDecl> list = new ArrayList<>();
        List<Statement0> clone = new ArrayList<>(children);
        for (Statement0 child : clone)
            list.addAll(child.LiftDeclarations());
        return list;
    }    
}
