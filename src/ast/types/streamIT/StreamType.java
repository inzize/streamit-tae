package ast.types.streamIT;

import ast.types.Type;

public class StreamType
{
    public Type input, output;
    
    public StreamType(Type input, Type output)
    {
        this.input = input;
        this.output = output;
    }
    
    public boolean IsVoidToVoid()
    {
        return input.IsVoid() && output.IsVoid();
    }
}
