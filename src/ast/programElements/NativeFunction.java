package ast.programElements;

import ast.members.Member;
import ast.statements.VarDecl;
import ast.types.Type;
import java.util.List;

public class NativeFunction implements Member
{
    Type type;
    String id;
    List<VarDecl> params;

    public NativeFunction(Type type, String id, List<VarDecl> params)
    {
        this.type = type;
        this.id = id;
        this.params = params;
    }

    @Override
    public void GenerateInitCode()
    {
        throw new UnsupportedOperationException("NativeFunction::GenerateInitCode - not supported yet.");
    }
}
