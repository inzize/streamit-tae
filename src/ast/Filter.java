package ast;

import ast.members.Function;
import ast.members.Init;
import ast.members.Member;
import ast.members.Work;
import ast.statements.VarDecl;
import ast.statements.Block;
import ast.statements.Statement;
import ast.statements.Statement0;
import ast.streamNodes.StreamNode;
import ast.types.streamIT.StreamType;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Filter extends Container implements StreamNode {

    public static LinkedHashMap<String, Filter> resolver = new LinkedHashMap<>();

    public List<VarDecl> params;
    public StreamType type;
    public boolean stateful;
    public List<Member> body = new ArrayList<>();
    public List<Function> functions = new ArrayList<>();
    public List<VarDecl> decls = new ArrayList<>();
    public String id;
    public Work work;
    public Work prework;
    public Init init;
    public int instances = 0;

    public Filter(StreamType type, boolean stateful, String id) {
        this.type = type;
        this.id = id;
        this.stateful = stateful;
    }

    public int MemoryUsage(FilterInstance filter) {
        int total = 0;
        for (VarDecl decl : decls) {
            total += decl.type.SizeOf(filter);
        }
        if (prework != null) // Fixme: not all active at the same time.
        {
            total += prework.MemoryUsage(filter);
        }
        if (init != null) {
            total += init.MemoryUsage(filter);
        }
        if (work != null) {
            total += work.MemoryUsage(filter);
        }
        return total;
    }

    @Override
    public void LiftDeclarations() {
        List<Statement0> inits = new ArrayList<>();

        List<VarDecl> temp_var_decls = new ArrayList<>();
        for (VarDecl v : decls) {
            inits.addAll(v.ReplaceDeclarations(temp_var_decls));
        }

        decls.addAll(temp_var_decls);

        if (inits.size() > 0) {
            if (init == null) {
                init = new Init(new Block(new ArrayList<Statement>()));
            }
            List<Statement> temp = new ArrayList<>();
            for (Statement0 s : inits) {
                temp.add(new Statement(s));
            }
            init.body.stmts.addAll(0, temp);
        }
        for (Function f : functions) {
            f.LiftDeclarations();
        }
        if (init != null) {
            init.LiftDeclarations();
        }
        if (work != null) {
            work.LiftDeclarations();
        }
        if (prework != null) {
            prework.LiftDeclarations();
        }
    }

    @Override
    public void Analyse(AbstractCode parent) {
        this.parent = parent;

        for (VarDecl decl : decls) {
            decl.Analyse(this);
        }
        for (Function f : functions) {
            f.Analyse(this);
        }
        if (init != null) {
            init.Analyse(this);
        }
        if (work != null) {
            work.Analyse(this);
        }
        if (prework != null) {
            prework.Analyse(this);
        }
    }

    @Override
    public VarDecl Resolve(String name) {
        for (VarDecl var : decls) {
            if (var.original_name.equals(name)) {
                return var;
            }
        }
        return super.Resolve(name);
    }

    @Override
    public Function ResolveFunction(String name) {
        for (Function f : functions) {
            if (f.name.equals(name)) {
                return f;
            }
        }
        return parent.ResolveFunction(name);
    }
}
