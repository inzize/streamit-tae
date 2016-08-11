package ast.programElements;

import ast.AbstractCode;
import ast.Decl;
import ast.Filter;
import ast.statements.VarDecl;
import ast.types.streamIT.StreamType;
import java.util.List;


public class NamedFilter extends Filter
{
    public NamedFilter(StreamType type, boolean stateful, String id, List<VarDecl> params)
    {
        super(type, stateful, id);
        this.params = params;
        Filter.resolver.put(id, this);
    }

    @Override
    public void GenerateInitCode()
    {
        writer.write("public static FilterInstance ");
        if (type != null && type.IsVoidToVoid())
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
        for (VarDecl decl : this.decls)
        {
            writer.write("");
            decl.Print(true, this);
        }        
        CreateNode();
        writer.writeLine("return " + variable + ";");
        writer.endBlock();
    }

    @Override
    public void CreateNode()
    {
        writer.write("FilterInstance " + variable + " = new FilterInstance(");
        writer.print("\"" + id + "\", ");

        if (work.pop != null)
            work.pop.Print();
        else
            writer.print('0');
        writer.print(',');
        if (work.peek != null)
            work.peek.Print();
        else
            writer.print('0');
        writer.print(',');
        if (work.push != null)
            work.push.Print();
        else
            writer.print('0');
        writer.print(',');
        if (prework != null && prework.pop != null)
            prework.pop.Print();
        else
            writer.print('0');
        writer.print(',');
        if (prework != null && prework.peek != null)
            prework.peek.Print();
        else
            writer.print('0');
        writer.print(',');
        if (prework != null && prework.push != null)
            prework.push.Print();
        else
            writer.print('0');

        for (VarDecl p : params)
        {
            writer.print(", (Object)");
            writer.print(p.name);
        }

        writer.println(");");
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        super.Analyse(parent);

        for (VarDecl decl : params)
            decl.Analyse(this);
    }

    @Override
    public VarDecl Resolve(String name)
    {
        for (VarDecl var : decls)
            if (var.original_name.equals(name))
                return var;

        for (VarDecl var : params)
            if (var.original_name.equals(name))
                return var;
        
        return super.Resolve(name);
    }
}
