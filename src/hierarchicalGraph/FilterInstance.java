package hierarchicalGraph;

import ast.Filter;
import static ast.GeneratesCode.writer;
import ast.expressions.Expression;
import ast.members.Init;
import ast.statements.VarDecl;
import ast.programElements.NamedFilter;
import ast.statements.Block;
import ast.statements.Statement;
import ast.types.Array;
import ast.types.NamedType;
import ast.types.streamIT.StreamType;
import ast.types.Type;
import backend.common.channels.AbstractChannel;
import backend.common.SourceFileWriter;
import backend.common.buffers.SimpleMergeSplitBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilterInstance extends StreamNode {

    private static Logger logger = Logger.getLogger("compiler");
    static Type StringType, UIntType, VolUIntType;
    public static int next_filter_id = 0;
    public int filter_id;
    public Filter filter;
    public int instance_number;
    public long initMult;
    public int primePumpCount = Integer.MAX_VALUE;
    public static int primePumpMax;
    public AbstractChannel FileReaderChannel, FileWriterChannel, input_buffer, output_buffer;
    public List<FilterInstance[]> split = new ArrayList<>();
    public List<FilterInstance> join = new ArrayList<>();
    public Object[] args;
    public Map<FilterInstance, AbstractChannel> inputs;
    public Map<FilterInstance, AbstractChannel> outputs;
    public int pre_pop = 0, pre_peek = 0, pre_push = 0;
    public static int special = 0;
    public boolean isFissedPeekingFilter = false;
    public boolean isLastPeekingFilter = false;
    public int originalFilterInstances;
    public int originalFilterId = -1;
    public String originalFilterName = null;
    public int fission_id = 0;
    private Map<VarDecl, Object> parMap = new HashMap<>();

    static {
        StringType = new NamedType("char*");
        UIntType = new NamedType("unsigned int");
        VolUIntType = new NamedType("volatile unsigned int");
    }

    public int MemoryUsage() {
        return filter.MemoryUsage(this);
    }

    protected FilterInstance() {
        super();
    }
    
    public FilterInstance(String filter_name, String type, Object... args) {
        switch (filter_name) {
            case "FileReader":
                List<VarDecl> vars = new ArrayList<>();
                vars.add(new VarDecl(StringType, "filename"));
                vars.add(new VarDecl(UIntType, "interval"));
                vars.add(new VarDecl(UIntType, "next_block"));
                vars.add(new VarDecl(VolUIntType, "tmp"));
                filter = new NamedFilter(new StreamType(null, new NamedType(type)), true, filter_name, vars);
                filter.init = new Init(new Block(new ArrayList<Statement>()));
                this.instance_number = special++;
                List<Object> args_list = new ArrayList<>();
                if (args.length > 0) {
                    if (!(args[0].getClass().equals(String.class))) {
                        logger.log(Level.SEVERE, "FileReader: first parameter has to be a filename (String)");
                        throw new RuntimeException("FileReader: first parameter has to be a filename (String)");
                    }
                    args_list.add(args[0]);
                } else {
                    args_list.add("input.stream");
                }
                args_list.add((args.length > 1) ? args[1] : 0);
                args_list.add(0);
                args_list.add(0);
                this.args = args_list.toArray();
                this.pop = 0;
                this.push = 1;
                this.peek = 0;
                break;
            case "FileWriter":
                filter = new NamedFilter(new StreamType(new NamedType(type), new NamedType(type)), true, filter_name, Arrays.asList(new VarDecl(StringType, "filename")));
                filter.init = new Init(new Block(new ArrayList<Statement>()));
                this.instance_number = special++;
                this.args = (args.length > 0) ? args : new Object[]{"output.stream"};
                this.pop = 1;
                this.push = 1;
                this.peek = 1;
                //this.split.add(new FilterInstance[] { this });
                break;
            case "Identity":
                filter = new NamedFilter(new StreamType(new NamedType(type), new NamedType(type)), true, filter_name, new ArrayList<VarDecl>());
                this.instance_number = special++;
                this.args = args;
                this.pop = 1;
                this.push = 1;
                this.peek = 1;
                break;
            default:
                throw new RuntimeException("Not Implemented");
        }
    }

    public FilterInstance(String filter_name, int pop, int peek, int push, int pre_pop, int pre_peek, int pre_push, Object... args) {
        filter = Filter.resolver.get(filter_name);
        this.instance_number = ++filter.instances;
        this.args = args;
        this.pop = pop;
        this.push = push;
        this.peek = Math.max(peek, pop);
        this.pre_pop = pre_pop;
        this.pre_peek = Math.max(pre_peek, pre_pop);
        this.pre_push = pre_push;
        
        // filter parameters
        for (int i = 0; i < args.length; i++) {
            VarDecl param = filter.params.get(i);
            Object value = args[i];
            parMap.put(param, value);
        }
        for(VarDecl v : filter.decls) {
            parMap.put(v, v.value);
        }
    }
    
    private String generateObjectValueString(Map.Entry<VarDecl, Object> e) {
        Object value = e.getValue();
        if (value instanceof Boolean) {
            return ((Boolean) value ? "1" : "0");
        } else if (value instanceof String) {
            return ("\"" + value + "\"");
        } else if (value instanceof Float) {
            return (value + "f");
        } 
        
//        } else if ((value != null && e.getKey().type instanceof NamedType)
//                || value instanceof Expression        
        else if (e.getKey().type instanceof Array) {
            VarDecl v = e.getKey();
            v.Rename(this.StreamNodeName());
            return v.name;
        } else if (value instanceof Expression) {
            VarDecl v = e.getKey();
            v.Rename(this.StreamNodeName());
            return "&"+v.name;
        } 
        else if (value == null && e.getKey().type instanceof NamedType) {
            return "&" + e.getKey().name;
        } 
        
        else if (value == null) {
            return "VALUE IS NULL";
        } else {
            return value.toString();
        }
    }
        
    public String getParamValues() {
        String ret = "";
        for(Map.Entry<VarDecl, Object> e : parMap.entrySet()) {
            ret += (ret.isEmpty() ? "" : ", ");
            ret += generateObjectValueString(e);
        }
        return ret;
    }

    public String getParamDecl() {
        String ret = "";
        for (Map.Entry<VarDecl, Object> e : parMap.entrySet()) {
            ret += (ret.isEmpty() ? "" : ", ");

            if (e.getKey().type instanceof Array) {
                ret += ((Array) e.getKey().type).elementType.CType() + "* restrict ";
                ret += e.getKey().name;
            } else {
                ret += e.getKey().type.CType() + " ";

                if (e.getValue() instanceof Expression) {
                    ret += "*_";
                }

                if (e.getValue() == null && e.getKey().type instanceof NamedType) {
                    e.getKey().Rename("*");
                }

                ret += e.getKey().name;

                if (e.getValue() instanceof Expression) {
                    e.getKey().Rename("*");
                }
            }
        }
        return ret;
    }
        
    public String WorkFunctionDecl() {
        String ret = "";
        ret += "(";
        String params = getParamDecl();
        
        if (input_buffer != null) {
            params += (params.isEmpty() ? "" : ", ");
            params += this.filter.type.input.CType() + "** restrict " + input_buffer.getBufferName(this, false);
        } else {
            for (AbstractChannel c : inputs.values()) {
                params += (params.isEmpty() ? "" : ", ");
                params += this.filter.type.input.CType() + "** restrict " + c.getBufferName(this, false);
            }
        }
        if (output_buffer != null && (!filter.id.equals("FileReader"))) {
            params += (params.isEmpty() ? "" : ", ");            
            params += this.filter.type.output.CType() + "** restrict " + output_buffer.getBufferName(this, false);
        } else {
            for (AbstractChannel c : outputs.values()) {
                params += (params.isEmpty() ? "" : ", ");
                params += this.filter.type.output.CType() + "** restrict " + c.getBufferName(this, false);
            }
        }
              
        ret += params.isEmpty() ? "" : params;
        ret += ")";
        return ret;
    }
    
    public String PreworkFunctionDecl() {
        String ret = "";
        ret += "(";
        String params = getParamDecl(); 
        ret += params.isEmpty() ? "" : params;
        ret += ")";
        return ret;
    }
    
    public String WorkFunctionCall() {
        String ret = "";
        ret += "(";
        String params = this.getParamValues();

        if (input_buffer != null) {
            params += (params.isEmpty() ? "" : ", ");            
            params += "&" + input_buffer.getBufferName(this, true);
        } else {
            for (AbstractChannel c : inputs.values()) {
                params += (params.isEmpty() ? "" : ", ");
                params += "&" + c.getBufferName(this, true);
            }
        }
        if (output_buffer != null) {
            params += (params.isEmpty() ? "" : ", ");            
            params += "&" + output_buffer.getBufferName(this, true);
        } else {
            for (AbstractChannel c : outputs.values()) {
                params += (params.isEmpty() ? "" : ", ");
                params += "&" + c.getBufferName(this, true);
            }
        }

        ret += (params.isEmpty() ? "" : params);
        ret += ");";
        return ret;
    }
    
    public String PreworkFunctionCall() {
        String ret = "";
        ret += "(";
        String params = this.getParamValues();
        ret += (params.isEmpty() ? "" : params);
        ret += ");";
        return ret;        
    }

    public String PopMethodName() {
        return StreamNodeName() + "_joiner";
    }

    public String PeekMethodName() {
        return StreamNodeName() + "_peek";
    }

    public String PushMethodName() {
        return StreamNodeName() + "_splitter";
    }

    private static boolean multipleVoidErrorLogged=false;
    
    public boolean isFirstFilterInGraph() {
        if (inputs.isEmpty() && !this.isStateless()) {
            if (this.filter_id != 0 && !multipleVoidErrorLogged) {
                logger.info("Graph may contain multiple void->!void filters!");
                multipleVoidErrorLogged=true;
                return false;
            }
            return true;
        }
        return false;
    }

    public void GeneratePop(boolean unique) {
        if (input_buffer != null) {
            input_buffer.Pop(unique);
        } else if (join.size() > 1 || streamit2.Compiler.forcejoiner > 1) {
            writer.print(PopMethodName() + "()");
        } else if (this.filter.id.equals("FileReader")) {
            if (output_buffer != null) {
                output_buffer.Push(FileReaderChannel.PopString(), true);
            } else {
                FilterInstance input = split.get(0)[0];
                AbstractChannel channel = outputs.get(input);
                //channel.Pop();
                channel.Push(FileReaderChannel.PopString(), true);
            }
        } else {
            FilterInstance input = join.get(0);
            AbstractChannel channel = inputs.get(input);
//            channel.Pop();
            writer.write(channel.PopString(unique));
        }
    }

    public void GeneratePeek(Expression e) {
        if (input_buffer != null) {
            input_buffer.Peek(e);
        } else if (join.size() > 1 || streamit2.Compiler.forcejoiner > 1) {
            writer.print(PeekMethodName() + "(");
            e.Print();
            writer.print(")");
        } else {
            FilterInstance input = join.get(0);
            AbstractChannel channel = inputs.get(input);
            channel.Peek(e);
        }
    }

    public void GeneratePush(Expression e, boolean semicolon) {
        if (output_buffer != null) {
            output_buffer.Push(e, semicolon);
        } else if (join.isEmpty() && this.filter.id.equals("FileReader")) // FileReader
        {
            FileReaderChannel.Push(e, semicolon);
        } else if (split.isEmpty()) // FileWriter
        {
            FileWriterChannel.Push(e, semicolon);
        } else if (split.size() > 1 || streamit2.Compiler.forcesplitter > 1) {
            writer.print(PushMethodName() + "(");
            e.Print();
            writer.print(")");
            if (semicolon) {
                writer.println(";");
            }
        } else {
            FilterInstance[] duplicates = split.get(0);
            if (duplicates.length > 1) {
                writer.startBlock("");
                writer.writeLine(filter.type.output.CType() + " tmp = ");
                e.Print();
                writer.println(";");
                for (FilterInstance output : duplicates) {
                    outputs.get(output).Push("tmp", true);
                    writer.println();
                }
                writer.endBlock();
            } else {
                outputs.get(duplicates[0]).Push(e, semicolon);
            }
        }
    }

    @Override
    public String StreamNodeName() {
        if (originalFilterName != null) {
            return originalFilterName;
        } else {
            if (this.fission_id > 0) {
                return ((FilterInstance) ((SplitJoin) (this.parent)).previousNode).StreamNodeName() + "_" + fission_id;
            } else {
                return filter.id + "_" + instance_number;
            }
        }

    }
 
    public int getMergeCost() {
        final int loopOverhead = 9;
        final int mergingOverhead = 3;
        //only for FilterWriter;
        if (!"FileWriter".equals(this.filter.id)) {
            return 0;
        }
        int cost = 0;
        long maxLines = 16;
        long iterations = (this.netSteady() * this.pop / this.join.size());
        long numLines = this.join.size();
        long unrolledIterations = maxLines / numLines;
        unrolledIterations = (unrolledIterations == 0) ? 1 : unrolledIterations;
        long loopIterations = iterations / unrolledIterations;
        long remainingIterations = iterations - (unrolledIterations * loopIterations);

        if (numLines < maxLines) {
            cost += loopIterations * loopOverhead;
            cost += unrolledIterations * loopIterations * this.join.size() * mergingOverhead;
            cost += remainingIterations * this.join.size() * mergingOverhead;
        } else {
            int round = (int) (this.pop * this.steadyMult / this.join.size());
            cost += StreamNode.getRootMult() * round * loopOverhead;
            cost += StreamNode.getRootMult() * round * this.join.size() * mergingOverhead;
        }
        return cost;
    }

    public String WorkFunctionName() {
        return StreamNodeName();
    }
    
    @Override
    public void SetParent(StreamNode parent) {
        this.parent = parent;
    }

    @Override
    public void SteadySchedule(StreamNode parent) {
        this.parent = parent;
    }

    @Override
    protected void InOrder(List<FilterInstance> list) {
        list.add(this);
    }

    @Override
    protected void getFissables(List<StreamNode> list) {
        list.add(this);
    }

    @Override
    public void Flatten() {
        in = new FilterInstance[][]{new FilterInstance[]{this}};
        out = new FilterInstance[]{this};
    }

    @Override
    public String toString() {
        return StreamNodeName();
    }

    @Override
    public long InitSchedule(long needs_to_produce) {
        long needs_to_consume = 0;

        if (filter.prework != null) {
            needs_to_produce = Math.max(0, needs_to_produce - pre_push);
            needs_to_consume = Math.max(pre_pop, pre_peek);
        }

        if (push > 0) {
            initMult = div_ceiling(needs_to_produce, push);
        } else {
            initMult = 0;
        }

        needs_to_consume += initMult * pop;

        if (peek > pop) {
            needs_to_consume += (peek - pop);
        }

        return needs_to_consume;
    }

    @Override
    public void Dump(int indent) {
        filter_id = next_filter_id++;
        Indent(indent, "filter " + filter_id + " " + filter.id);
    }

    public static boolean equals(FilterInstance[] a, FilterInstance[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (FilterInstance x : a) {
            boolean contained = false;
            for (FilterInstance y : b) {
                if (x == y) {
                    contained = true;
                }
            }
            if (!contained) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFissable() {
        if (this.filter.stateful || pop == 0 || push == 0 || StreamNodeName().contains("FileWriter")) {
            return false;
        }
        long commRate = this.netSteady() * (this.netPop() + this.netPush());
        int filterWork = estimator.estimateStreamNode(this);
        return true;
    }

    @Override
    public FilterInstance clone() {
        String copyFilterName = filter.id;
        FilterInstance copyFilterInstance = new FilterInstance(copyFilterName, (int) pop, (int) peek, (int) push, (int) pre_pop, (int) pre_peek, (int) pre_push);
        super.clone(copyFilterInstance);
        copyFilterInstance.filter_id = next_filter_id++;
        copyFilterInstance.initMult = initMult;
        copyFilterInstance.primePumpCount = primePumpCount;
        copyFilterInstance.args = args;
        copyFilterInstance.isFissedNode = true;
        copyFilterInstance.parMap = parMap;
        return copyFilterInstance;
    }

    @Override
    public List<StreamNode> InitFissedNodes(Integer[] ratio) {
        List<StreamNode> fissedNodes = new ArrayList<>();
        for (int i = 0; i < ratio.length; i++) {
            FilterInstance fissedFilterInstance = this.clone();
            fissedNodes.add(fissedFilterInstance);
            fissedFilterInstance.fission_id = i + 1;
            if (peek > pop) {
                fissedFilterInstance.isFissedPeekingFilter = true;
                if (i == ratio.length - 1) {
                    fissedFilterInstance.isLastPeekingFilter = true;
                }
            }
        }
        return fissedNodes;
    }

    @Override
    public void BackupChildren() {
        originalFilterInstances = filter.instances;
        originalFilterId = filter_id;
        originalFilterName = this.StreamNodeName();
    }

    @Override
    public void ReverseChildren() {
        split = new ArrayList<>();
        join = new ArrayList<>();
        in = null;
        out = null;
        initMult = 0;
        steadyMult = 1;
        filter.instances = originalFilterInstances;
        filter_id = originalFilterId;
    }

    @Override
    public int compareTo(Object t) {
        if (!(t instanceof FilterInstance)) {
            return super.compareTo(t);
        }
        final FilterInstance other = (FilterInstance) t;
        return Integer.compare(filter_id, other.filter_id);
    }

    @Override
    public boolean isStateless() {
        return !this.filter.stateful && pop != 0 && push != 0 && !StreamNodeName().contains("FileWriter") && !isFissedNode && this.filter.prework == null;
    }

    @Override
    public boolean isPeeking() {
        if (peek > pop) {
            return true;
        }
        if (pre_peek > pre_pop) {
            return true;
        }
        return false;
    }
    
    public void generateCode() {
        if ("FileReader".equals(filter.id))
            return;
        if ("FileWriter".equals(filter.id))
            return;
        if (netSteady() != 1) {
            writer.writeLine("for (int _i=0; _i<" + netSteady() + "; _i++)");
            writer.writeLine(SourceFileWriter.INDENT_STR + this.filter.id + this.WorkFunctionCall());
        } else {
            writer.writeLine(this.filter.id + this.WorkFunctionCall());
        }
    }
}
