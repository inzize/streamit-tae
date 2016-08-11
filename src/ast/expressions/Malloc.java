package ast.expressions;

import ast.AbstractCode;
import ast.types.*;

public class Malloc extends Expression0 {

    Array type;
    
    public Malloc(Array type)
    {
	this.type = type;
    }
    
    @Override
    public void Print() {
//	(float*)malloc(sizeof(float)*3);
	writer.print("(");
	type.PrintType();
	writer.print(")malloc(sizeof(");
	type.elementType.PrintType();
	writer.print(")*");
	type.size.Print();
	writer.print(")");
    }

    @Override
    public Type AnalyseExpression(AbstractCode parent) {
	this.parent = parent;
	type.Analyse(this);
	return type;
    }
    
}
