package ast.statements;

import ast.Container;
import java.util.*;
import ast.AbstractCode;
import ast.statements.Statement0;
import ast.statements.Statement;
import static ast.GeneratesCode.writer;
import hierarchicalGraph.FilterInstance;

public class Block extends Statement0
{
    public List<VarDecl> decls = new ArrayList<>();
    public List<Statement> stmts;

    public Block(List<Statement> stmts)
    {
        this.stmts = stmts;
    }
    
    public int MemoryUsage(FilterInstance filter)
    {
        int total = 0;
        for (VarDecl decl : decls)
            total += decl.MemoryUsage(filter); 
        for (Statement stmt : stmts)
            total += stmt.MemoryUsage(filter);
        return total;
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
        writer.startBlock(null);
        PrintDecls(container);
        PrintStatements(container);
        writer.endBlock();
    }
    
    public void PrintDecls(Container container)
    {
        for (VarDecl d : decls)
        {
            writer.write(""); //write indent
            d.Print(true, container);
        }
        if (!decls.isEmpty())
            writer.println();
    }
    
    public void PrintStatements(Container container)
    {
        for (Statement s : stmts)
        {
            writer.write(""); //write indent
            s.Print(true, container);
        }
    }

    @Override
    public List<VarDecl> LiftDeclarations()
    {
        for (Statement s : stmts)
            for (VarDecl v : s.LiftDeclarations())
            {
                v.Rename("");
                decls.add(v);
            }
        return new ArrayList<>();
    }
    
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        for (Statement s : stmts)
            s.Analyse(this);
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (VarDecl d : decls)
        {
            VarDecl v = d.FindVarDecls(name);
            if (v != null)
                return v;            
        }
        
        for (Statement s : stmts)
        {
            VarDecl v = s.FindVarDecls(name);
            if (v != null)
                return v;
        }

        return super.Resolve(name);
    }
}
