package backend.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * Writes files, take care of indention levels
 *
 */
public class SourceFileWriter {
    public static final String INDENT_STR = "    ";
    private final static Logger logger = Logger.getLogger("SourceFileWriter");
    private final PrintWriter writer;
    private int indentLvl = 0;
    private boolean alreadyWriteIndention = false;
    public SourceFileWriter(String filename) throws IOException {
        writer = new PrintWriter(filename);
    }
    
    public SourceFileWriter(Writer out) {
        writer = new PrintWriter(out);
    }

    public void close() {
        writer.close();
    }
    
    private void printIndention() {
        if (alreadyWriteIndention) {
            //logger.info("You already printed the indention for this line");
            return;
        }
        for (int i=0; i<indentLvl; i++)
            writer.print("    ");
        alreadyWriteIndention=true;
    }
    
    /**
     * Write a complete line (with '\n'), prefix it with the right indention
     */
    public void writeLine(String line) {
        printIndention();
        writer.println(line);
        alreadyWriteIndention=false;
    }
    
    /**
     * Write l (without appending '\n'), prefix it with the right indention
     */
    public void write(String l) {
        printIndention();
        writer.print(l);
    }
    
    @Deprecated
    /**
     * Increases the indention level
     */
    public void incIndention() {
        indentLvl++;
    }
    
    public void decIndention() {
        indentLvl--;
        assert indentLvl>=0;
    }
    
    /**
     * Start a new block, write decl with indention, write '{' with indention and increase indention level
     * writes no decl if null
     * @param decl 
     */
    public void startBlock(String decl) {
        if (decl!=null)
            writeLine(decl);
        writeLine("{");
        indentLvl++;
    }
    
    
    /**
     *  Decrease indention level and write } with indention
     */
    public void endBlock() {
        indentLvl--;
        if (indentLvl<0) {
            logger.warning("You called endBlock too often -> resetting indentLvl!");
            //indentLvl = 0;
            throw new RuntimeException("-");
        }
        writeLine("}");
        if (indentLvl==0)
            writer.println("");
    }
    
    //Functions below are only used for old code try to not use them
    
    public void print(String txt) {
        writer.print(txt);
    }

    public void print(Object o) {
        writer.print(o);
    }
    
    public void print(char c) {
        writer.print(c);
    }
    
    
    public void println() {
        writer.println();
        alreadyWriteIndention = false;
    }
    
    public void println(String t) {
        writer.println(t);
        alreadyWriteIndention = false;
    }

}
