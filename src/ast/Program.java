package ast;

import ast.members.Function;
import ast.statements.VarDecl;
import ast.programElements.ProgramElement;
import ast.programElements.Static;
import ast.types.NamedType;
import backend.common.SourceFileWriter;
import hierarchicalGraph.FilterInstance;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program extends AbstractCode {
    public static VarDecl pi = new VarDecl(null, "foo", null);
    public List<ProgramElement> elements = new ArrayList<>();
    public List<Static> statics = new ArrayList<>();
    public static boolean useMath = false;
    public static boolean useRound = false;
    public static boolean useMax = false;
    public static boolean useComplex = false;
    public static boolean useStdio = false;

    public Program() {
    }

    public void GenerateInitCode(Writer w) {
        Program.GenerateC(false);  // generate Java code
        AbstractCode.writer = new SourceFileWriter(w);

        writer.writeLine("package streamit2;\n");
        writer.writeLine("import hierarchicalGraph.*;\n");
        writer.startBlock("public class Init");
        for (Static e : statics) {
            e.GenerateInitCode();
        }

        for (ProgramElement e : elements) {
            e.GenerateInitCode();
        }

        writer.endBlock();
        AbstractCode.writer.close();
    }

    @Override
    public void Analyse(AbstractCode parent) {
        this.parent = parent;

        for (Static e : statics) {
            e.Analyse(this);
        }

        for (ProgramElement e : elements) {
            e.Analyse(this);
        }
    }

    public void LiftDeclarations() {
        for (Static e : statics) {
            e.LiftDeclarations();
        }

        for (ProgramElement e : elements) {
            e.LiftDeclarations();
        }
    }

    public static void GenerateC(boolean generateC) {
        GeneratesCode.GenerateC = generateC;

        for (Function math : mathFunctions) {
            if (generateC) {
                math.name = math.original_name;
            } else {
                math.name = "(float)Math." + math.original_name;
            }
        }

        if (generateC) {
            pi.name = Float.toString((float) Math.PI);
        } else {
            pi.name = "(float)Math.PI";
        }
    }

    public static Function[] stdioFunctions = new Function[]{
        new Function(null, "println", null),
        new Function(null, "print", null)};

    public static Function[] mathFunctions = new Function[]{
        new Function(NamedType.Float, "sqrt", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "floor", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "ceil", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "cos", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "sin", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "atan2", Arrays.asList(new VarDecl(NamedType.Float, "x"), new VarDecl(NamedType.Float, "y"))),
        new Function(NamedType.Float, "exp", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "atan", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "round", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "abs", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "log", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "max", Arrays.asList(new VarDecl(NamedType.Float, "x"), new VarDecl(NamedType.Float, "y"))),
        new Function(NamedType.Int32_t, "round", Arrays.asList(new VarDecl(NamedType.Float, "x"))),
        new Function(NamedType.Float, "pow", Arrays.asList(new VarDecl(NamedType.Float, "x"), new VarDecl(NamedType.Float, "y")))};

    @Override
    public VarDecl Resolve(String name) {
        for (Static e : statics) {
            for (VarDecl var : ((Static) e).decls) {
                if (var.original_name.equals(name)) {
                    return var;
                }
            }
        }

        return super.Resolve(name);
    }

    public static void Reset() {
        useMath = false;
        useRound = false;
        useMax = false;
        useComplex = false;
        useStdio = false;
        FilterInstance.special = 0;
        FilterInstance.next_filter_id = 0;
    }

    @Override
    public Function ResolveFunction(String name) {
        for (Function math : mathFunctions) {
            if (math.original_name.equals(name)) {
                useMath = true;
                if ("round".equals(math.name)) {
                    useRound = true;
                }
                if ("max".contains(math.name)) {
                    useMax = true;
                }

                return math;
            }
        }
        for (Function other : stdioFunctions) {
            if (other.original_name.equals(name)) {
                useStdio = true;
                return other;
            }
        }
        return super.ResolveFunction(name);
    }
}
