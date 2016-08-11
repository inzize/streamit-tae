package ast.types;

import ast.AbstractCode;
import ast.expressions.*;
import static ast.GeneratesCode.writer;
import hierarchicalGraph.FilterInstance;

public class Array extends Type {

    public Type elementType;
    public Expression size;

    public Array(Type elementType, Expression0 size) {
	this.elementType = elementType;
	this.size = new Expression(size);
    }

    public Array(Object value) {
	this.size = new Expression(new Constant(java.lang.reflect.Array.getLength(value)));
	Object element = java.lang.reflect.Array.get(value, 0);
	if(value.getClass().getComponentType().isArray() )
	    this.elementType = new Array(element);
	else
	    this.elementType = new NamedType(element);
    }

	    
    @Override
    public boolean equals(Object other) {
	if (!(other instanceof Array)) {
	    return false;
	}
	return ((Array) other).elementType.equals(elementType);
    }

    @Override
    public void PrintJavaType() {
	// Use to Generate Java Code (only?)
	assert (!AbstractCode.GenerateC);
	elementType.PrintJavaType();
	writer.print('[');
	writer.print(']');
    }

    @Override
    public void Allocate() {
	// Use to Generate Java Code (only?)
	assert (!AbstractCode.GenerateC);
	//elementType.Allocate();
	writer.print('[');
	size.Print();
	writer.print(']');
	elementType.Allocate();
    }

    @Override
    public void PrintCType() {
	// Used to Generate C Code (only?)
	assert (AbstractCode.GenerateC);
	elementType.PrintCType();
	writer.print('*');
    }

    @Override
    public String CType() {
	assert (AbstractCode.GenerateC);
	return elementType.CType() + "*";
    }    
    
    @Override
    public boolean IsVoid() {
	return false;
    }

    @Override
    public void Analyse(AbstractCode parent) {
	this.parent = parent;
	elementType.Analyse(this);
	size.AnalyseExpression(parent);
    }

    @Override
    public Type base() {
	return elementType.base();
    }

    @Override
    public String Format() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Type Field(String fieldname) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    // only used for debugging - can be safely removed.
    public String toString() {
	return elementType.toString() + "[]";
    }

    @Override
    public int SizeOf(FilterInstance filter) {
        return elementType.SizeOf(filter) * size.AnalyseConstant(parent, filter);
    }

    @Override
    public Expression0 SomeValue() 
    {
        throw new UnsupportedOperationException("Not supported yet.");    
    }
}
