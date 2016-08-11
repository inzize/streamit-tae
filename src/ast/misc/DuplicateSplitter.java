package ast.misc;

import ast.AbstractCode;


public class DuplicateSplitter extends Splitter
{
    public DuplicateSplitter()
    {    
    }

    @Override
    public void Print()
    {
        writer.print("new DuplicateSplitter()");
    }

    @Override
    public void Analyse(AbstractCode parent)
    {
        this.parent = parent;
    }
}
