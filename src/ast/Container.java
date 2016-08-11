package ast;

import ast.programElements.ProgramElement;

public abstract class Container extends ProgramElement {

    public static int count = 0;
    public String variable;

    public Container() {
        variable = "node" + (count++);
    }

    public abstract void CreateNode();
}
