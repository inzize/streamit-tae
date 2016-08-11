package backend.common;

import backend.common.channels.AbstractChannel;
import ast.expressions.streamIT.Peek;
import ast.expressions.streamIT.Pop;
import ast.GeneratesCode;
import static ast.GeneratesCode.writer;
import ast.members.Function;
import ast.members.Init;
import ast.Program;
import ast.expressions.ArrayIndex;
import ast.expressions.Expression;
import ast.expressions.Identifier;
import ast.programElements.Static;
import ast.statements.Block;
import ast.statements.Statement;
import ast.statements.Statement0;
import ast.statements.streamIT.Push;
import hierarchicalGraph.FilterInstance;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import ast.statements.VarDecl;
import backend.common.buffers.FileReaderSplitBuffer;
import backend.common.buffers.FileWriterMergeBuffer;
import backend.coreVA.CoreVACodeGenerator;
import hierarchicalGraph.StreamNode;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator extends GeneratesCode {
    private final static int FUNCTION_LOWEST_MARKER_ID = 10;
    /* this list is sorted by filter id */
    protected List<FilterInstance> local_filters;
    private final Program program;
    private final String prefix;
    private final String mainName;
    protected String filename;

    public CodeGenerator(Processor proc, String filename, Program program, List<FilterInstance> local_filters) throws FileNotFoundException {
        if (proc != null) {
            prefix = "_" + proc.toString();
            mainName = proc.toString();
        } else {
            prefix = "";
            mainName = "main";
        }
        this.program = program;
        this.local_filters = local_filters;
        this.filename = filename;
    }

    public void Generate() throws IOException {
        // Write to a string first, so that we can see which header resources we actually need
        StringWriter string_writer = new StringWriter();
        writer = new SourceFileWriter(string_writer);
        GenerateCode();
        writer.close();
        // copy string to file
        FileWriter file_writer = new FileWriter(filename);
        writer = new SourceFileWriter(file_writer);
        generateHeader();
        writer.print(string_writer.toString());
        writer.close();
    }

    protected void GenerateCode() {
        GenerateTrace();
        GenerateFunctionPrototypes();
        GenerateStatics();
        GenerateChannelDeclarations();
        GenerateWorkFunctions();
        GenerateSteadyFunctions();
        generateInitChannels();
        GenerateInitFilters();
        GeneratePrimePump();
        GenerateMain();
    }

    private void GenerateFunctionPrototypes() {
//        writer.println();
//        for (FilterInstance filter : local_filters) {
//            //writer.writeLine("static void " + filter.StreamNodeName() + "();");
//            writer.writeLine("static void " + filter.StreamNodeName() + "_init();");
//            writer.writeLine("static void " + filter.StreamNodeName() + "_steady();");
//        }
//        writer.println();

        // TODO - TESTING
        for (FilterInstance filter : local_filters) {
            GenerateInitParameters(filter); // TODO: skip unused params
            for (VarDecl decl : filter.filter.decls) {
                decl.Rename(filter.StreamNodeName());
                decl.Print(true, filter.filter);
                decl.Rename("");                
            }
        }
    }
    
    protected void generateHeader() {
        writer.println("#include <string.h>");
        if (Program.useStdio) {
            writer.println("#include <stdio.h>");
        }
        writer.println("#ifdef __GNUC__\n    #include <stdint.h>\n    #include <stdlib.h>\n    #include <stdio.h>\n   	#define __buildin__memcpy memcpy\n#endif");
        if (Program.useMath) {
            writer.println("#include <math.h>");
            writer.println("#include \"streamit-complex-lib.h\"");
        }
        if (Program.useRound || Program.useMax) {
            writer.println("#include \"streamit-math-lib.h\"");
        }
        writer.println("#ifndef STEADY_ITERATIONS\n    #define STEADY_ITERATIONS 100\n#endif");

        if (!(this instanceof CoreVACodeGenerator)) {
            writer.println("#ifndef COREVA_HWACC_MISC_TRACE\n    #define COREVA_HWACC_MISC_TRACE(id) \n#endif");
        }
    }

    protected void GenerateTrace() {
        if (streamit2.Compiler.trace) {
            writer.println();
            writer.println("static FILE* trace;");
        }
    }

    protected void GenerateStatics() {
        for (Static s : program.statics) {
            for (VarDecl decl : s.decls) {
                decl.Rename(prefix);
                decl.Print(true, null);
            }
        }
        writer.println();
        writer.startBlock("void InitStatics" + prefix + "()");
        if (streamit2.Compiler.trace) {
            writer.writeLine("trace = fopen(\"QUTtrace" + prefix + ".txt\", \"w\");");
        }
        for (Static s : program.statics) {
            if (s.init != null) {
                writer.writeLine("{");
                s.init.Print(true, null);
                writer.writeLine("}");
            }
        }
        writer.endBlock();
    }

    private void GenerateChannelDeclarations() {
        for (FilterInstance filter : local_filters) {
            for (AbstractChannel input : filter.inputs.values()) {
                input.ReadDecls();
            }
            for (AbstractChannel output : filter.outputs.values()) {
                output.WriteDecls();
            }
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.WriteDecls();
            }
            if (filter.FileWriterChannel != null) {
                filter.FileWriterChannel.WriteDecls();
            }
            if (filter.input_buffer != null) {
                filter.input_buffer.WriteDecls();
                filter.input_buffer.ReadDecls();
            }

            if (filter.output_buffer != null) {
                filter.output_buffer.WriteDecls();
                filter.output_buffer.ReadDecls();
            }
        }
        writer.println();
    }

    private void GenerateMain() {
        writer.startBlock("int " + mainName + "()");
        writer.writeLine("InitStatics" + prefix + "();");
        writer.writeLine("InitChannels" + prefix + "();");
        writer.writeLine("InitFilters" + prefix + "();");
        writer.writeLine("PrimePump" + prefix + "();");
        GenerateSteadyLoop();
        generateDestructChannelsBody();
        writer.writeLine("\treturn 0;");        
        writer.endBlock();
    }

    private void generateInitChannels() {
        writer.startBlock("static void InitChannels" + prefix + "()");
        generateInitChannelsBody();
        writer.endBlock();
    }

    protected void generateInitChannelsBody() {
        for (FilterInstance filter : local_filters) {
            for (AbstractChannel input : filter.inputs.values()) {
                input.InitRead();
            }
        }
        for (FilterInstance filter : local_filters) {
            for (AbstractChannel output : filter.outputs.values()) {
                output.InitWrite();
            }
            if (filter.FileWriterChannel != null) {
                filter.FileWriterChannel.InitWrite();
            }
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.InitWrite();
            }
        }
    }

    private void GenerateInitFilters() {
        writer.startBlock("static void InitFilters" + prefix + "()");
        int seq = 10;
        for (FilterInstance filter : local_filters) {
            writer.writeLine(filter.StreamNodeName() + "_init();");
            if ((filter.filter.prework != null && filter.fission_id < 2) || filter.initMult > 0) {
                if (filter.FileWriterChannel != null) {
                    filter.FileWriterChannel.GenerateInitWaitWrite();
                }
                if (filter.filter.prework != null && filter.fission_id < 2) {
                    writer.writeLine("COREVA_HWACC_MISC_TRACE(" + seq + ");");
                    writer.writeLine(filter.StreamNodeName() + "_prework" + filter.PreworkFunctionCall());
                }
                if (filter.FileWriterChannel != null) {
                    filter.FileWriterChannel.GenerateInitWaitWrite();
                }
            }
            seq++;
        }
        writer.endBlock();
    }

    private void GeneratePrimePump() {
        writer.startBlock("static void PrimePump" + prefix + "()");
        writer.startBlock("for (int iteration=0; iteration<" + (FilterInstance.primePumpMax + 1) + "; iteration++)");
        for (int iteration = 0; iteration <= FilterInstance.primePumpMax; iteration++) {
            List<FilterInstance> filters = new ArrayList<>();
            for (FilterInstance filter : local_filters) {
                if (FilterInstance.primePumpMax - filter.primePumpCount == iteration) {
                    filters.add(filter);
                }
            }
            if (filters.size() > 0) {
                writer.startBlock("if (iteration >= " + iteration + ")");
                if (filters.size() > 1) {
                    for (FilterInstance filter : filters) {
                        GenerateSteady(filter);
                    }
                } else {
                    GenerateSteady(filters.get(0));
                }
                writer.endBlock();
            }
        }
        writer.endBlock();
        writer.endBlock();
    }

    protected void GenerateSteadyLoop() {
        writer.startBlock("for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop");
        GenerateSteadyLoopBody();
        writer.endBlock();
    }

    protected void GenerateSteadyLoopBody() {        
        int accFilterMarkerID = FUNCTION_LOWEST_MARKER_ID;
        for (FilterInstance filter : local_filters) {
            writer.writeLine("COREVA_HWACC_MISC_TRACE(" + (accFilterMarkerID++) + ");");
            GenerateSteady(filter);
        }
    }

    protected void GenerateSteady(FilterInstance filter) {
        writer.writeLine(filter.StreamNodeName() + "_steady();");
    }

    private void GenerateSteadyFunctions() {
        for (FilterInstance filter : local_filters) {
            GenerateSteadyFunction(filter);
        }
    }

    private void GenerateSteadyFunction(FilterInstance filter) {
        writer.startBlock("static void " + filter.StreamNodeName() + "_steady()");
        GenerateSteadyFunctionBody(filter);
        writer.endBlock();
    }

    protected void generateDestructChannelsBody() {
        // FileWriter destructor
        for (FilterInstance filter : local_filters) {
            if (filter.FileWriterChannel != null) {
                filter.FileWriterChannel.destructChannel();
            }
        }
    }

    protected void GenerateSteadyFunctionBody(FilterInstance filter) {
        // add unique prefix to parameters and var decl
        for (VarDecl decl : filter.filter.decls) {
            decl.Rename(filter.StreamNodeName());
        }
        for (int i = 0; i < filter.args.length; i++) {
            filter.filter.params.get(i).Rename(filter.StreamNodeName());
        }   
        
        //List<FilterInstance> filters = StreamNode.globalGraph.InOrder();
        //Check assumptions
        assert (filter.inputs.size()>1) == (filter.input_buffer!=null);
        assert ("FileWriter".equals(filter.filter.id)) == (filter.FileWriterChannel != null);
        assert ("FileReader".equals(filter.filter.id)) == (filter.FileReaderChannel != null);
        assert ("FileReader".equals(filter.filter.id)) == (filter.output_buffer instanceof FileReaderSplitBuffer);
        if (filter.isFirstFilterInGraph()) {
            writer.writeLine("COREVA_HWACC_MISC_TRACE(8);");
        }
        writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");   
        //ALWAYS KEEP THIS IN SYNC TO [Communication|Latency]Evaluator!!!!
        //---WAIT READS---
        //Channels
        for (AbstractChannel input : filter.inputs.values()) {
            input.GenerateSteadyWaitRead();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
        }
        //FileReader
        if (filter.FileReaderChannel != null) {
            filter.FileReaderChannel.GenerateSteadyWaitRead();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
        } 
        //Merge if not handled by FileWriter 
        if ((filter.input_buffer!=null) && (filter.FileWriterChannel == null)) {
            filter.input_buffer.GenerateSteadyWaitRead();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
        }
        //---WAIT WRITES---
        //Channels
        for (AbstractChannel output : filter.outputs.values()) {
            output.GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");
        }
        //FileWriter w merge
        if (filter.FileWriterChannel != null) {
            filter.FileWriterChannel.GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite FileWriter");
            //This handles merges too!!!
            //Is it possible to move it to wait read?
            //FIXME: I'm a hack -> old OptimizeFileWriter
            (new FileWriterMergeBuffer(null,filter)).GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");            
        }
        //Split prepare
        if (filter.output_buffer != null) {
            filter.output_buffer.GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split");
        }
        //---Work starts--- 
        if (filter.isFissedPeekingFilter) { //NOT REVIEWED YET
            writer.writeLine("unsigned int _i=0;");
            if (filter.netSteady() / filter.steadyMult != 1) {
                writer.writeLine("// " + filter.netSteady() + "  " + filter.steadyMult);
                writer.writeLine("for (_i=0; _i<" + filter.netSteady() + "; _i++)");
            }
            writer.startBlock(null);
            if (filter.steadyMult != 1) {
                writer.startBlock("for (unsigned int _j=0; _j<" + filter.steadyMult + "; _j++, _i++)");
            }
            writer.writeLine(filter.filter.id + filter.WorkFunctionCall());
            if (filter.steadyMult != 1) {
                writer.endBlock();
                writer.writeLine("_i--;"); // _i was counted one time to often
            }
            GeneratePopNItems(filter);
            writer.endBlock();
        } else {
            filter.generateCode();
        }
        writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
        //---DONE READS---
        //Channels
        for (AbstractChannel input : filter.inputs.values()) {
            writer.write("");
            input.GenerateSteadyDoneReading();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from " + input.src);
        }
        //---DONE WRITES---
        // SPLIT (implicit FileReaderSplit handling)
        if (null != filter.output_buffer) {
            writer.write("");
            filter.output_buffer.GenerateSteadyDoneWriting();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7);  // "+filter.output_buffer.getClass().getSimpleName()+" split");
        }
        //File Reader (count as write, has too run after split because it shares a buffer) 
        if (filter.FileReaderChannel != null) {
            filter.FileReaderChannel.GenerateSteadyDoneReading();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7);  // SteadyDoneRead FileReader");
        }
        // Channels
        for (AbstractChannel output : filter.outputs.values()) {
            output.GenerateSteadyDoneWriting();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to " + output.dst);
        }
        //File Writer
        if (filter.FileWriterChannel != null) {
            filter.FileWriterChannel.GenerateSteadyDoneWriting();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite FileWriter");
            // special trace mark for measuring delay purpose. 
            writer.writeLine("COREVA_HWACC_MISC_TRACE(9);");
        }
        //NO OUTPUT CHANNEL -> MARKER
        if (filter.outputs.isEmpty()) {
            //writer.writeLine("COREVA_HWACC_MISC_TRACE(9);");
        }
    }

    private void GenerateWorkFunctions() {
        Map<String, FilterInstance> unique_filters = new HashMap<>();
        for (FilterInstance filter : local_filters) {
            if (!unique_filters.containsKey(filter.filter.id)) {
                unique_filters.put(filter.filter.id, filter);
            }
        }

        writer.writeLine("// WORK");
        for (FilterInstance filter : unique_filters.values()) {
                    GenerateWorkFunction(filter);
        }

        writer.writeLine("// PREWORK + INIT");
        for (FilterInstance filter : local_filters) {
//            GenerateWorkFunction(filter);
            GeneratePreworkFunction(filter);
            GenerateInitFunction(filter);
        }
    }

    private void GenerateInitParameters(FilterInstance filter) {
        for (int i = 0; i < filter.args.length; i++) {
            VarDecl param = filter.filter.params.get(i);
            param.Rename(filter.StreamNodeName());
            param.PrintParam();
            writer.println(";");
        }

        if (filter.args.length > 0 && filter.filter.init == null) {
            filter.filter.init = new Init(new Block(new ArrayList<Statement>()));
        }
    }

    private void GenerateInitializeParameters(FilterInstance filter) {
        List<Statement0> stmts = new ArrayList<>();
        List<VarDecl> var_decls = new ArrayList<>();
        for (int i = 0; i < filter.args.length; i++) {
            VarDecl param = filter.filter.params.get(i);
            Object value = filter.args[i];

            if (value.getClass().isArray()) {
                Identifier j = new Identifier(param.name);
                j.declaration = param;
                VarDecl.allocate_array(j, new ast.types.Array(value), stmts, var_decls);
                //VarDecl.allocate_array(j, (ast.types.Array) param.type, stmts, var_decls);
            }
        }
        for (VarDecl decl : var_decls) {
            writer.write("");
            decl.Print(true, null);
            writer.println();
        }
        for (Statement0 stmt : stmts) {
            writer.write("");
            stmt.Print(true, null);
            writer.println();
        }

        for (int i = 0; i < filter.args.length; i++) {
            VarDecl param = filter.filter.params.get(i);
            Object value = filter.args[i];

            writer.write("");
            if (value.getClass().isArray()) {
                ArrayInit(param.name, value);
            } else {
                param.PrintName();
                writer.print(" = ");
                GenerateObjectValue(value);
                writer.println(";");
            }
        }
    }

    private void ArrayInit(String param, Object value) {
        for (int i = 0; i < Array.getLength(value); i++) {
            String var = param + "[" + i + "]";
            Object element = Array.get(value, i);
            if (element.getClass().isArray()) {
                ArrayInit(var, element);
            } else {
                writer.print(var + " = ");
                GenerateObjectValue(element);
                writer.print("; ");
            }
        }
        writer.println();
    }

    private void GenerateObjectValue(Object value) {
        if (value instanceof Boolean) {
            writer.print((Boolean) value ? "1" : "0");
        } else if (value instanceof String) {
            writer.print("\"" + value + "\"");
        } else if (value instanceof Float) {
            writer.print(value + "f");
        } else {
            writer.print(value);
        }
    }

    private void TraceWork(FilterInstance filter) {
        if (streamit2.Compiler.trace) {
            writer.println("fprintf(trace, \"" + filter.filter_id + ".work();\\n\");");
        }
    }

    private void TracePreWork(FilterInstance filter) {
        if (streamit2.Compiler.trace) {
            writer.println("fprintf(trace, \"" + filter.filter_id + ".prework();\\n\");");
        }
    }

    private void TracePop(FilterInstance filter) {
        if (streamit2.Compiler.trace) {
            writer.println("fprintf(trace, \"" + filter.filter_id + ".pop(%08x)\\n\", val);");
        }
    }

    private void TracePush(FilterInstance filter) {
        if (streamit2.Compiler.trace) {
            writer.println("fprintf(trace, \"" + filter.filter_id + ".push(%08x)\\n\", p);");
        }
    }

    private void GeneratePreworkFunction(FilterInstance filter) {
        if (filter.filter.prework != null && filter.fission_id < 2) {
//            writer.startBlock("static void " + filter.WorkFunctionName() + "_prework()");
            writer.startBlock("static void " + filter.StreamNodeName() + "_prework" + filter.PreworkFunctionDecl());

            filter.filter.prework.body.PrintDecls(null);

            if (filter.isFirstFilterInGraph()) {
                writer.writeLine("COREVA_HWACC_MISC_TRACE(8);");
            }

            writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");

            TracePreWork(filter);
            //---WAIT READS---
            //Channels
            if (Math.max(filter.pre_pop, filter.pre_peek) > 0) {
                for (AbstractChannel input : filter.inputs.values()) {
                    input.GenerateInitWaitRead();
                    writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
                }
            }
                        
            //FileReader
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.GenerateSteadyWaitRead();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
            }
            //Merge if not handled by FileWriter 
            if ((filter.input_buffer != null) && (filter.FileWriterChannel == null)) {
                filter.input_buffer.GenerateSteadyWaitRead();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
            }

            //---WAIT WRITE---
            //Channels
            if (filter.pre_push > 0) {
                for (AbstractChannel output : filter.outputs.values()) {
                    output.GenerateInitWaitWrite();
                    writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");
                }
            }
            //FileWriter w merge
            if (filter.FileWriterChannel != null) {
                assert false;
                filter.FileWriterChannel.GenerateSteadyWaitWrite();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite FileWriter");
                //This handles merges too!!!
                //Is it possible to move it to wait read?
                //FIXME: I'm a hack -> old OptimizeFileWriter
                (new FileWriterMergeBuffer(null, filter)).GenerateSteadyWaitWrite();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");
            }
            //Split prepare
            if (filter.output_buffer != null) {
                filter.output_buffer.GenerateSteadyWaitWrite();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split");
            }
            //---WORK---
            filter.filter.prework.body.PrintStatements(null);
            writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
            //---DONE READ---
            //Channels
            if (Math.max(filter.pre_pop, filter.pre_peek) > 0) {
                for (AbstractChannel input : filter.inputs.values()) {
                    writer.write("");
                    input.GenerateInitDoneReading();
                    writer.writeLine("COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from " + input.src.filter.id);
                }
            }
            //---DONE WRITE---
            // SPLIT (implicit FileReaderSplit handling)
            if (null != filter.output_buffer) {
                writer.write("");
                filter.output_buffer.GenerateSteadyDoneWriting();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(7);  // " + filter.output_buffer.getClass().getSimpleName() + " split");
            }
            //File Reader (count as write, has to run after split because it shares a buffer) 
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.GenerateSteadyDoneReading();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(7);  // SteadyDoneRead FileReader");
            }
            // Channels
            if (filter.pre_push > 0) {
                for (AbstractChannel output : filter.outputs.values()) {
                    output.GenerateInitDoneWriting();
                    writer.writeLine("COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to " + output.dst.filter.id);
                }
            }
            //File Writer
            if (filter.FileWriterChannel != null) {
                filter.FileWriterChannel.GenerateSteadyDoneWriting();
                writer.writeLine("COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite FileWriter");
                // special trace mark for measuring delay purpose. 
                writer.println("COREVA_HWACC_MISC_TRACE(9);");
            }
            //NO OUTPUT CHANNEL -> MARKER
            if (filter.outputs.isEmpty()) {
                //writer.writeLine("COREVA_HWACC_MISC_TRACE(9);");
            }
            writer.endBlock();
        }
    }

    private void GenerateInitFunction(FilterInstance filter) {

        // add unique prefix to parameters and var decl
        for (VarDecl decl : filter.filter.decls) {
            decl.Rename(filter.StreamNodeName());
        }
        for (int i = 0; i < filter.args.length; i++) {
            filter.filter.params.get(i).Rename(filter.StreamNodeName());
        }   
         
        writer.startBlock("static void " + filter.WorkFunctionName() + "_init()");
        boolean isFileReader = false;
        if ((filter.filter.work == null) && (filter.filter.id.equals("FileReader"))) {
            isFileReader = true;
        }

        if (filter.filter.init != null) {
            filter.filter.init.body.PrintDecls(null);
            GenerateInitializeParameters(filter);
            filter.filter.init.body.PrintStatements(null);
        }

        // WAIT
        if (filter.initMult > 0) {
            for (AbstractChannel input : filter.inputs.values()) {
                input.GenerateInitWaitRead();
            }
            for (AbstractChannel output : filter.outputs.values()) {
                output.GenerateInitWaitWrite();
            }
            
            //FileReader
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.GenerateInitWaitRead();
            }
            
            //SPLIT
            if (filter.output_buffer != null) {
                filter.output_buffer.GenerateInitWaitWrite();
            }

            //MERGE (if not handled by FileWriter)
            if ((filter.input_buffer != null) && (filter.FileWriterChannel == null)) {
                filter.input_buffer.GenerateInitWaitRead();
            }
 
            if (!isFileReader) {                
                if (filter.isFissedPeekingFilter) {
                    //writer.writeLine("// initMult = " + filter.initMult + ", netSteady = " + filter.netSteady());
                    writer.startBlock("for (unsigned int _i=0; _i<" + filter.initMult + "; _i++)");
                    writer.writeLine(filter.filter.id + filter.WorkFunctionCall());
                    writer.endBlock();
                } else {
                    if (filter.initMult > 1) {
                        writer.writeLine("for (unsigned int _i=0; _i<" + filter.initMult + "; _i++)");
                    }
                    writer.startBlock(null);
                    writer.writeLine(filter.filter.id + filter.WorkFunctionCall());
                    writer.endBlock();
                }
            } 
        }
        //SPLIT
        if (null!=filter.output_buffer) {
            filter.output_buffer.GenerateInitDoneWriting();
        }

        // do init pop due to filter fission
        if (filter.isLastPeekingFilter) {
            // init wait
            if (filter.initMult == 0) {
                for (AbstractChannel input : filter.inputs.values()) {
                    input.GenerateInitWaitRead();
                }
            }
            //Do not call GeneratePopNItems, it's more efficient to just mark the buffer as read
            //GeneratePopNItems(filter);
            // init done
            if (filter.initMult == 0) {
                for (AbstractChannel input : filter.inputs.values()) {
                    input.GenerateInitDoneReading();
                }
            }
        }

        // DONE
        if (filter.initMult > 0) {
            for (AbstractChannel input : filter.inputs.values()) {
                input.GenerateInitDoneReading();
            }
            for (AbstractChannel output : filter.outputs.values()) {
                output.GenerateInitDoneWriting();
            }
            //File Reader (count as write, has too run after split because it share a buffer) 
            if (filter.FileReaderChannel != null) {
                filter.FileReaderChannel.GenerateInitDoneReading();
            }
        }
        writer.endBlock();
    }
   
    private void GenerateWorkFunction(FilterInstance filter) {           
        // remove prefix from parameters
        for (int i = 0; i < filter.args.length; i++) {
            VarDecl param = filter.filter.params.get(i);
            param.Rename("");
        }            
        
//        writer.writeLine("// filter_id: " + filter.filter.id);
//        writer.writeLine("// param_decl: " + filter.getParamDecl());
//        writer.writeLine("// param_val: " + filter.getParamValues());
        
        String channels = "";
        for(AbstractChannel c : filter.inputs.values()) {
            channels += (channels.isEmpty() ? "" : ", ");
            channels += c.getIdent();
        }
//        writer.writeLine("// chan_in: " + channels);
        channels = "";
        for(AbstractChannel c : filter.outputs.values()) {
            channels += (channels.isEmpty() ? "" : ", ");
            channels += c.getIdent();
        }
//        writer.writeLine("// chan_out: " + channels);
        
//        GenerateInitParameters(filter);
  
        boolean isFileReader = false;

        if (filter.filter.work == null) // special built in filter (Identity, FileReader or FileWriter)
        {
            switch (filter.filter.id) {
                case "FileReader": {
                    writer.writeLine("FILE *" + filter.StreamNodeName() + "_file;");
                    isFileReader = true;
                    break;
                }
            }
        }

//        for (VarDecl decl : filter.filter.decls) {
//            decl.Rename(filter.StreamNodeName());
//            decl.Print(true, filter.filter);
//        }
//        if (!filter.filter.decls.isEmpty()) //TODO: CHECK
//        {
//            writer.println();
//        }

        for (AbstractChannel input : filter.inputs.values()) {
            if (filter.pop > 0 || filter.pre_pop > 0) {
                input.PopMethod();
            }
            if ((filter.filter.work != null && filter.filter.work.usesPeek) || (filter.filter.prework != null && filter.filter.prework.usesPeek)) {
                input.PeekMethod();
            }
        }
        for (AbstractChannel output : filter.outputs.values()) {
            if (filter.push > 0 || filter.pre_push > 0) {
                output.PushMethod();
            }
        }
        if (filter.FileWriterChannel != null) {
            filter.FileWriterChannel.PushMethod();
        }

        Pop.filter = filter;
        Peek.filter = filter;
        Push.filter = filter;

        for (Function helper : filter.filter.functions) {
            helper.name = filter.StreamNodeName() + "_" + helper.original_name;
            helper.Print();
            writer.println();
        }

        if (filter.filter.work != null) {
            writer.writeLine("static void " + filter.filter.id + filter.WorkFunctionDecl());
            //filter.filter.work.body.Print(true, null);
            writer.startBlock(null);
            filter.filter.work.body.PrintDecls(null);
            TraceWork(filter);
            filter.filter.work.body.PrintStatements(null);
            // search for local malloc statements to free memory
            for (Statement stmt : filter.filter.work.body.stmts) {
                for (Statement0 stmt0 : stmt.children) {
                    if (stmt0 instanceof ast.statements.Assignment) {
                        if (((ast.statements.Assignment) stmt0).rhs.child instanceof ast.expressions.Malloc) {
                            ast.expressions.Expression exp = ((ast.statements.Assignment) stmt0).lhs;
                            writer.writeLine("free(" + ((Identifier) exp.child).declaration.Name() + ");");
                        }
                    }
                }
            }
            writer.endBlock();
        } else {
            switch (filter.filter.id) {
                case "Identity":
//                    writer.startBlock("static void " + filter.WorkFunctionName() + "()");
                    writer.startBlock("static void " + filter.filter.id + filter.WorkFunctionDecl());
                    filter.GeneratePush(new Expression(new Pop()), true);
                    writer.endBlock();                    
                    break;
                case "FileWriter":
                case "FileReader":
//                    filter.GeneratePop(true);
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected built in filter");
            }
        }
    }

    private List<FilterInstance> RemoveDuplicates(List<FilterInstance> all) {
        List<FilterInstance> set = new ArrayList<>();
        for (FilterInstance f : all) {
            if (!set.contains(f)) {
                set.add(f);
            }
        }
        return set;
    }

    private List<FilterInstance[]> RemoveSetDuplicates(List<FilterInstance[]> all) {
        List<FilterInstance[]> set = new ArrayList<>();
        for (FilterInstance[] f : all) {
            boolean contains = false;
            for (FilterInstance[] s : set) {
                if (FilterInstance.equals(f, s)) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                set.add(f);
            }
        }
        return set;
    }

    // Generate pop function for peeking filter to skip N items
    protected void GeneratePopNItems(FilterInstance filter) {
        Pop.filter = filter;
        writer.startBlock("for (int _k=0; _k<" + (filter.peek - filter.pop) + "; _k++)");
        writer.write("");
        filter.GeneratePop(true);
        writer.writeLine(";");
        writer.endBlock();
    }
}
