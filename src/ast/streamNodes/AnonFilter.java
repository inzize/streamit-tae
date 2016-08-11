package ast.streamNodes;

import ast.AbstractCode;
import ast.Decl;
import ast.Filter;
import ast.statements.VarDecl;
import ast.types.streamIT.StreamType;
import java.util.*;

public class AnonFilter extends Filter
{
    static int count = 0;

    public AnonFilter(StreamType type, boolean stateful)
    {
        super(type, stateful, "anonFilter" + (count++));
        params = new ArrayList<>();
        Filter.resolver.put(id, this);
    }

    @Override
    public void GenerateInitCode()
    {
        writer.print("new FilterInstance(\"" + id + "\", ");
        if (work != null && work.pop != null)
            work.pop.Print();
        else
            writer.print('0');
        writer.print(',');
        if (work != null && work.peek != null)
            work.peek.Print();
        else
            writer.print('0');
        writer.print(',');
        if (work != null && work.push != null)
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
        
        writer.print(")");
    }
    
    @Override
    public void CreateNode()
    {
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

        VarDecl global = parent.Resolve(name);
        params.add(new VarDecl(global.type, name));
        return Resolve(name);
    }
}
