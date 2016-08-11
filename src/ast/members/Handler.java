package ast.members;

import ast.statements.VarDecl;
import ast.statements.Statement;
import ast.statements.Statement0;
import java.util.List;

public class Handler implements Member
{
    String id;
    List<VarDecl> params;
    Statement body;
    
    public Handler(String id, List<VarDecl> params, Statement0 body)
    {
        this.id = id;
        this.params = params;
        this.body = new Statement(body);
    }

    @Override
    public void GenerateInitCode()
    {
        throw new UnsupportedOperationException("Handler::GenerateInitCode - not supported yet.");
    }
}
