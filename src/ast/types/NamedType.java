package ast.types;

import ast.AbstractCode;
import static ast.GeneratesCode.writer;
import ast.expressions.Constant;
import ast.expressions.Expression;
import ast.expressions.Expression0;
import hierarchicalGraph.FilterInstance;

public class NamedType extends Type
{
    public static final NamedType Float = new NamedType("float");
    public static final NamedType Int8_t = new NamedType("int8_t");
    public static final NamedType UInt8_t = new NamedType("uint8_t");
    public static final NamedType Int16_t = new NamedType("int16_t");
    public static final NamedType UInt16_t = new NamedType("uint16_t");
    public static final NamedType Int32_t = new NamedType("int32_t");
    public static final NamedType UInt32_t = new NamedType("uint32_t");
    public static final NamedType Int64_t = new NamedType("int64_t");
    public static final NamedType UInt64_t = new NamedType("uint64_t");
    public static final NamedType String = new NamedType("char*");
    public static final NamedType Complex = new NamedType("Complex");
    public static final NamedType Void = new NamedType("void");


    String name;

    public NamedType(String name)
    {
        this.name = name;
    }
    
    public NamedType(Object value) {
	if (value instanceof Float) {
	    this.name = "float";
	} else if (value instanceof Integer) {
	    this.name = "int";
	} else if (value instanceof Long) {
	    this.name = "long";
	} else if (value instanceof String) {
	    this.name = "char*";
	} else if (value instanceof streamit2.Complex) {
	    this.name = "Complex";
	} 
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof NamedType))
            return false;
        return ((NamedType)other).name.equals(name);
    }

    @Override
    public void PrintJavaType()
    {
        // java code: only int type
        if(name.matches("[u]*int[0-9]+_t"))
            writer.print("int");
        else
            writer.print(name);
    }

    @Override
    public void PrintCType()
    {
        switch (name)
        {
            case "boolean":              
            case "bit":
                writer.print("uint8_t");
                break;
            case "complex":
                writer.print("Complex");
                break;
            default:
                writer.print(name);
        }
    }    
    
    public String CType()
    {
        switch (name)
        {
            case "boolean":
            case "bit":
                return "uint8_t";    
            case "complex":
                return "Complex";    
            default:
                return name;
        }
    }     
    
    @Override
    public void Allocate()
    {
        // PrintJavaType();
    }

    @Override
    public boolean IsVoid()
    {
       return "void".equals(name);
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
        // Fixme: resolve named type ???
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public Type base()
    {
        return this;
    }
    
    @Override
    public String Format()
    {
        switch (name)
        {
            case "bit":
                return "%hhi";
            case "int8_t":
                return "%hhu";
            case "uint8_t":
                return "%u";
            case "int16_t": 
                return "%hi";
            case "uint16_t": 
                return "%hu";
            case "int32_t":
                return "%d";
            case "uint32_t":
                return "%u";
            case "int64_t": 
                return "%lli";
            case "uint64_t": 
                return "%llu";                
            case "float":
                return "%08x";
            case "char*":
                return "%s";
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public Type Field(String fieldname)
    {
        switch (name)
        {
            case "Complex":
                if ("real".equals(fieldname) || "imag".equals(fieldname))
                    return new NamedType("float");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int SizeOf(FilterInstance filter) 
    {
        switch (name)
        {
            case "void":
                return 0;
            case "boolean":        
            case "bit":
                return 1; // translated to uint8_t
            case "int8_t":
                return 1;
            case "uint8_t":
                return 1;
            case "int16_t":
                return 2;
            case "uint16_t":
                return 2;
            case "int32_t":
                return 4;
            case "uint32_t":
                return 4;
            case "int64_t":
                return 8;
            case "uint64_t":
                return 8;
           case "float":
                return 4;                
            case "complex":
                return 2 * NamedType.Float.SizeOf(filter);
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public Expression0 SomeValue() 
    {
        switch (name)
        {
            case "bit":
                return new Constant(0); 
            case "boolean":
                return new Constant(true);
            case "short": case "unsigned short":
                return new Constant(21); 
            case "int": case "unsigned int":
                return new Constant(42); 
            case "long": case "unsigned long": 
                return new Constant(128);
            case "float":
                return new Constant(3.14);
            default:
                throw new UnsupportedOperationException(name + " not supported yet.");
        }
    }
}
