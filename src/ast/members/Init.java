package ast.members;

import ast.statements.VarDecl;
import ast.statements.Statement;
import ast.statements.Block;
import ast.AbstractCode;
import ast.Container;
import static ast.GeneratesCode.writer;
import ast.statements.Statement0;
import hierarchicalGraph.FilterInstance;
import java.util.List;

public class Init extends Statement0 implements Member
{
    public Block body;
    
    public Init(Block body)
    {
        this.body = body;
    }
    
    @Override    
    public void GenerateInitCode()
    {
        writer.println();        
        writer.writeLine("static");
        writer.write("");
        body.Print(true, null);     
        writer.println();
    }    

    @Override
    public void Print(boolean semicolon, Container container)
    {
        for (VarDecl d : body.decls)
        {
            writer.write("");
            d.Print(semicolon, container);
            writer.println();        
        }
        for (Statement s : body.stmts)
        {
            writer.write("");
            s.Print(semicolon, container);
        }
    }
    
    @Override
    public List<VarDecl> LiftDeclarations()
    {    
        return body.LiftDeclarations();
    }    

    @Override
    public void Analyse(AbstractCode parent) 
    {
        this.parent = parent;
        body.Analyse(this);
    }

    @Override
    public int MemoryUsage(FilterInstance filter) 
    {
        return body.MemoryUsage(filter);
    }
}
