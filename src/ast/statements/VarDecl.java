package ast.statements;

import ast.expressions.ArrayInit;
import ast.expressions.Expression;
import ast.expressions.Constant;
import ast.expressions.Expression0;
import ast.expressions.Identifier;
import ast.expressions.ArrayIndex;
import ast.AbstractCode;
import ast.Container;
import ast.Decl;
import ast.statements.Statement0;
import static ast.GeneratesCode.writer;
import ast.expressions.Binary;
import ast.expressions.Field;
import ast.expressions.Malloc;
import ast.expressions.PostFix;
import ast.members.Member;
import ast.types.Array;
import ast.types.NamedType;
import ast.types.Type;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VarDecl extends Statement0 implements Member, Decl
{
    public String original_name;
    public String name;
    public Type type;
    public Expression value;
    public static List<String> identifiers = new ArrayList<>();

    public VarDecl(Type type, String name)
    {
	this.name = this.original_name = name;
	this.type = type;
	this.value = null;
    }

    public VarDecl(Type type, String name, Expression0 value)
    {
	this.name = this.original_name = name;
	this.type = type;
	if (value != null) {
	    this.value = new Expression(value);
	}
    }
    
    public int MemoryUsage(FilterInstance filter)
    {
        return type.SizeOf(filter);
    }

    public void PrintParam()
    {
	type.PrintType();
	writer.print(' ');
	PrintName();
    }

    public void Rename(String prefix)
    {
	if (!"".equals(prefix)) {
	    prefix = prefix + "_";
	}
        
        // TODO Remove this hack!
        if(!name.equals("i"))
            identifiers.remove(name);

	name = prefix + original_name;

	int i = 2;
	while (identifiers.contains(name)) {
	    name = prefix + original_name + "_" + String.format("%02d", i);
	    i++;
	}
	identifiers.add(name);
    }

    @Override
    public void GenerateInitCode()
    {
	writer.print("static ");
	Print(true, null);
	writer.println();
    }

    @Override
    public void Print(boolean semicolon, Container container)
    {
	type.PrintType();
	writer.print(' ');
	PrintName();
	PrintValue();
	if (semicolon) {
	    writer.println(";");
	}
    }

    @Override
    public List<VarDecl> LiftDeclarations()
    {
	List<VarDecl> var_decls = new ArrayList<>();
	List<Statement0> stmts = ReplaceDeclarations(var_decls);
	Statement parentStmt = (Statement) (this.parent);
	parentStmt.Replace(this, stmts);
	var_decls.add(this);
	return var_decls;
    }

    public VarDecl FindVarDecls(String name)
    {
	if (original_name.equals(name)) {
	    return this;
	} else {
	    return null;
	}
    }

    public void PrintName()
    {
	writer.print(name);
    }

    public void PrintValue()
    {
	if (value == null) {
	    if (type instanceof Array) {
		if (!AbstractCode.GenerateC) {
		    writer.print(" = new ");
		    // print base type
		    type.base().PrintJavaType();
		    type.Allocate();
		} else {
		    writer.print(" = {0}");
		}
	    }
	} else {
	    writer.print(" = ");
	    value.Print();
	}
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
	if (!identifiers.contains(name)) {
	    identifiers.add(name);
	}

	this.parent = parent;

	type.Analyse(this);

	if (value != null) {
	    value.AnalyseExpression(this);
	}
    }

    private void ArrayAssignment(List<Statement0> stmts, Expression0 lhs, ArrayInit rhs)
    {
	for (int i = 0; i < rhs.elements.size(); i++) {
	    Expression0 left = new ArrayIndex(lhs, new Constant(i));
	    Expression element = rhs.elements.get(i);
	    if (element.child instanceof ArrayInit) {
		ArrayAssignment(stmts, left, (ArrayInit) element.child);
	    } else {
		stmts.add(new Assignment(left, "=", element.child));
	    }
	}
    }

    public List<Statement0> ReplaceDeclarations(List<VarDecl> var_decls)
    {
	List<Statement0> stmts = new ArrayList<>();
	if (type instanceof ast.types.Array) {
	    ast.types.Array array_type = (ast.types.Array) type;
	    Identifier id = new Identifier(name);
	    id.declaration = this;
	    allocate_array(id, array_type, stmts, var_decls);
	}
	if (value != null) {
	    Identifier id = new Identifier(name);
	    id.declaration = this;
	    if (value.child instanceof ArrayInit) {
		ArrayAssignment(stmts, id, (ArrayInit) value.child);
	    } else {
		Assignment assign = new Assignment(id, "=", value.child);
		stmts.add(assign);
	    }
	    value = null;
	} else {
	    Identifier id = new Identifier(name);
	    id.declaration = this;
	    if (NamedType.Complex.equals(type)) {
		Assignment assign_r = new Assignment(new Field(id, "real"), "=", new Constant(0));
		stmts.add(assign_r);
		Assignment assign_i = new Assignment(new Field(id, "imag"), "=", new Constant(0));
		stmts.add(assign_i);
	    } else if (!(type instanceof ast.types.Array)) {
		Assignment assign = new Assignment(id, "=", new Constant(0));
		stmts.add(assign);
	    }
	}
	return stmts;
    }

    static public void allocate_array(Expression0 lhs, ast.types.Array array_type, List<Statement0> stmts, List<VarDecl> var_decls)
    {
	Malloc malloc_expr = new Malloc(array_type);
	Assignment assign = new Assignment(lhs, "=", malloc_expr);
	stmts.add(assign);

	// more dimensional array
	if (array_type.elementType instanceof ast.types.Array) {
	    VarDecl temp_index = new VarDecl(ast.types.NamedType.Int32_t, "i");
	    var_decls.add(temp_index);
	    temp_index.Rename("");
	    Identifier i = new Identifier("i");
	    i.declaration = temp_index;
	    Statement0 init = new Assignment(i, "=", new Constant(0));

	    List<Statement0> loop_stmts = new ArrayList<>();

	    ArrayIndex array_01 = new ArrayIndex(lhs, i);
	    allocate_array(array_01, (ast.types.Array) array_type.elementType, loop_stmts, var_decls);

	    List<Statement> loop_stmts2 = new ArrayList<>();
	    for (Statement0 s : loop_stmts) {
		loop_stmts2.add(new Statement(s));
	    }
	    Block loop_body = new Block(loop_stmts2);

	    ast.statements.For for_loop = new ast.statements.For(
		    Arrays.asList(init),
		    new Binary(i, "<", array_type.size.child),
		    new ExpressionStmt(new PostFix(i, "++")),
		    loop_body
		    );
	    stmts.add(for_loop);
	}
    }

    @Override
    public String toString()
    {
	return type + " " + name;
    }

    @Override
    public String Name()
    {
	return name;
    }
}
