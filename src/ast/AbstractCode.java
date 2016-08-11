package ast;

import ast.members.Function;
import ast.statements.VarDecl;

public abstract class AbstractCode extends GeneratesCode {

    public AbstractCode parent;

    public abstract void Analyse(AbstractCode parent);

    public VarDecl Resolve(String name) {
        if (parent != null) {
            return parent.Resolve(name);
        } else {
            throw new UnsupportedOperationException("unresolved identifier " + name);
        }
    }

    public Function ResolveFunction(String name) {
        if (parent != null) {
            return parent.ResolveFunction(name);
        } else {
            throw new UnsupportedOperationException("unresolved function " + name);
        }
    }
}
