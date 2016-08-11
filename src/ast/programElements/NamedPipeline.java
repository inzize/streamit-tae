package ast.programElements;

import ast.AbstractCode;
import ast.Container;
import ast.statements.Statement;
import ast.statements.VarDecl;
import ast.statements.Statement0;
import ast.types.streamIT.StreamType;
import java.util.List;

public class NamedPipeline extends Container
{
    StreamType type;
    String id;
    List<VarDecl> params;
    Statement body;

    public NamedPipeline(StreamType type, String id, List<VarDecl> params, Statement0 body)
    {
        this.type = type;
        this.id = id;
        this.params = params;
        this.body = new Statement(body);
    }

    @Override
    public void LiftDeclarations()
    {       
        body.LiftDeclarations();        
    }
            
    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        
        for (VarDecl var : params)
            var.Analyse(this);
        
        body.Analyse(this);
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (VarDecl var : params)
            if (var.original_name.equals(name))
                return var;
        
        return super.Resolve(name);
    }  

    @Override
    public void GenerateInitCode()
    {
        writer.write("public static Pipeline ");
        if (type.IsVoidToVoid())
            writer.print("main(");
        else
            writer.print(id + "(");
        boolean first = true;
        for (VarDecl p : params)
        {
            if (first)
                first = false;
            else
                writer.print(',');
            p.PrintParam();
        }
        writer.println(")");
        writer.startBlock(null);

        CreateNode();
        
        body.Print(false, this);
        writer.writeLine("return " + variable + ";");
        writer.endBlock();
    }

    @Override
    public void CreateNode()
    {
        writer.writeLine("Pipeline " + variable + " = new Pipeline();");
    }
}
