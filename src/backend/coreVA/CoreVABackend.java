package backend.coreVA;

import streamit2.Compiler;
import ast.Program;
import backend.common.Chip;
import backend.common.Chip.Link;
import backend.common.PartitionLoader;
import backend.common.Partitioner;
import backend.common.Processor;
import backend.common.TimelineWriter;
import backend.common.buffers.FileReaderSplitBuffer;
import backend.common.buffers.FileWriterMergeBuffer;
import backend.common.channels.AbstractChannel;
import backend.common.channels.MemoryChannel2;
import backend.common.estimators.ASTEstimator;
import backend.common.estimators.BufferEstimator;
import backend.common.estimators.CommunicationModel;
import backend.common.estimators.Estimator;
import backend.common.estimators.MemoryEstimator;
import backend.common.estimators.PerfFileEstimator;
import backend.common.evaluators.legacy.ChannelEvaluator;
import backend.common.evaluators.legacy.CommunicationEvaluator;
import backend.common.evaluators.LatencyEvaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.evaluators.MemoryEvaluator;
import backend.common.evaluators.Evaluator;
import backend.common.partitioner.GUIManualPartitioner;
import backend.common.partitioner.GreedyPartitioner;
import backend.common.partitioner.GroupMigrationPartitioner;
import backend.common.partitioner.MITPartitioner;
import backend.common.partitioner.PresetPartitioner;
import backend.common.partitioner.SimulatedAnnealingPartitioner5;
import backend.common.partitioner.TaePartitionerILP;
import backend.common.partitioner.TaePartitionerILP_NotFission;
import backend.common.partitioner.mutator.Mutation;
import backend.common.partitioner.mutator.smart.SmartSplitTreeMutator;
import backend.coreVA.channels.CoreVAClusterChannel;
import backend.coreVA.channels.CoreVAClusterChannelRingbuffer;
import backend.coreVA.channels.CoreVAClusterChannelShared;
import backend.coreVA.channels.CoreVAFileInputChannel;
import backend.coreVA.channels.CoreVAFileOutputChannel;
import backend.coreVA.channels.CoreVANocChannel;
import backend.coreVA.channels.CoreVANocChannelShared;
import backend.coreVA.xmlModel.MpsocChip;
import backend.coreVA.xmlModel.Node;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static streamit2.Compiler.analyseLatency;
import static streamit2.Compiler.splitTrees;
import streamit2.NoEstimationDataException;

public class CoreVABackend {

    private final static Logger logger = Logger.getLogger("compiler");
    public static Estimator estimator;
    public static List<Evaluator> evaluators;
    public static SortedMap<FilterInstance, Processor> partitioning;
    public static MpsocChip chip;

    public static void Partition(String filename, Program program, List<FilterInstance> filters, int ProcRows, int ProcCols, int ProcIds, String xmlPath) throws FileNotFoundException, IOException {
        Compiler.program = filename;
        if (xmlPath != null) {
            chip = new MpsocChip(xmlPath, false);
        } else {
            chip = new MpsocChip(ProcCols, ProcRows, ProcIds);
        }
        Compiler.chip = chip;
        long startTime = System.nanoTime();
        estimator = chooseFilterEstimator(filename, filters);
        StreamNode.estimator = estimator;
        Evaluator evaluator;
        switch (Compiler.optimizeTarget) {
            case THROUGHPUT:
                evaluator = new ThroughputEvaluator();
                break;
            case LATENCY:
                evaluator = new LatencyEvaluator();
                break;
            default:
                throw new RuntimeException("Not Impl");
        }

        //Evaluation evaluator = new LatencyEvaluator(estimator, chip);
        evaluators = new ArrayList<>();
        Evaluator memEvaluator = new MemoryEvaluator();
        Evaluator chanEvaluator = new ChannelEvaluator();
        Evaluator comEvaluator = new CommunicationEvaluator();
        evaluators.add(memEvaluator);
        evaluators.add(chanEvaluator);
        evaluators.add(comEvaluator);
        if (Compiler.targetLatency > 0) {
            logger.log(Level.INFO, "TargetLatency set: " + Compiler.targetLatency);
            evaluators.add(new LatencyEvaluator());
        }
        evaluators.add(evaluator);
        try {
            partitioning = choosePartitioner(filename, program, filters, chip);
        } catch (NoEstimationDataException e) {
            if (e.unknownPartition==null) {
                e.printStackTrace();
                System.exit(-1);
            } 
            logger.log(Level.INFO, "Run abort: No Estimation Data, generating last partition!");
            partitioning = e.unknownPartition;
            return; //Is that ok?
        }
        if (chip.getProcessors().length == 1) { //Why do we have to do this?
            new GreedyPartitioner().createPartition(filters, chip, estimator, false);
        }
        filters = StreamNode.globalGraph.InOrder();
        try {
            Mutation.hillClimbMult(evaluators, filters, Mutation.convertPartition(partitioning));
        } catch (NoEstimationDataException e) {
            }            
        startTime = System.nanoTime() - startTime;
        logger.log(Level.INFO, "part_time: {0} [ms]", new Object[]{startTime / 1000000});
        logger.log(Level.INFO, "estimated throughput: {0} [OutputItems/MCycles]", Mutation.getThroughput(evaluators));

        // log bottleneck cpu and filters
        String bottleneck_filters = "";
        evaluator.evaluate(partitioning);
        for (Entry<FilterInstance, Integer> e : evaluator.getBottleneckFilters().entrySet()) {
            String bf = e.getKey().StreamNodeName() + ":" + e.getValue();
            bf += bottleneck_filters.isEmpty() ? "" : ", ";
            bottleneck_filters = bf + bottleneck_filters;
        }
        logger.log(Level.INFO, "bottleneck: {0} ({1})", new Object[]{evaluator.getBusyProcessor().toString(), bottleneck_filters});

        // log evaluator speed
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean.isCurrentThreadCpuTimeSupported()) {
            startTime = bean.getCurrentThreadCpuTime();
            double ret = 0;
            int nRuns = 1000;
            for (int i = 0; i < nRuns; i++) {
                ret += evaluator.evaluate(partitioning);
            }
            long stopTime = bean.getCurrentThreadCpuTime();
            long duration = (stopTime - startTime) / nRuns;
            logger.log(Level.INFO, "evaluator_time Old: {0,number,#} [ns]", duration);
        }
        if (bean.isCurrentThreadCpuTimeSupported() && analyseLatency) {
            LatencyEvaluator tmp = new LatencyEvaluator();
            startTime = bean.getCurrentThreadCpuTime();
            double ret = 0;
            int nRuns = 100;
            for (int i = 0; i < nRuns; i++) {
                ret += tmp.evaluate(partitioning);
            }
            long stopTime = bean.getCurrentThreadCpuTime();
            long duration = (stopTime - startTime) / nRuns;
            logger.log(Level.INFO, "evaluator_time Latency: {0,number,#} [ns]", duration);
        }
        
        // stream graph stats
        logger.log(Level.INFO, "filter_cnt: {0}", new Object[]{filters.size()});
        logger.log(Level.INFO, "global_mult: {0}", new Object[]{StreamNode.getGlobalMult()});
        logger.log(Level.INFO, "root_mult: {0}", new Object[]{StreamNode.getRootMult()});
        
        // throw exception if partition is invalid
        if (!Mutation.validate(filters, evaluators, Mutation.convertPartition(partitioning))) {
            printMemStat(new MemoryEstimator(partitioning));
            Evaluator eval = Mutation.getInvalidEvaluator(filters, evaluators, Mutation.convertPartition(partitioning));
            throw new RuntimeException("Could not find a valid partition! (" + eval.toString() + ")");
        }        
    }

    public static void printMemStat(MemoryEstimator mem) {
        // memory usage per cpu
        String mem_usage = "";
        for (Processor pProc : chip.getProcessors()) {
            CoreVAProcessor proc = (CoreVAProcessor) pProc;
            mem_usage += "\n\t\t\t" + proc.toString() + ": " + mem.getEstimationLocal(pProc) + " byte (" + (mem.getEstimationLocal(pProc) * 100.0f) / proc.memSize + "%)";
        }
        logger.log(Level.INFO, "mem_usage: {0}", new Object[]{mem_usage});

        // shared memory usage per cluster
        String sharedMemUsage = "";
        Map<Integer, Integer> sharedMem = mem.getSharedMemoryUsage();
        for (int clusterId : sharedMem.keySet()) {
            if (CoreVABackend.chip.sharedMemCluster[clusterId].length > 0) {
                sharedMemUsage += "\n\t\t\t" + clusterId + ": " + sharedMem.get(clusterId) + " byte (" + (sharedMem.get(clusterId) * 100.0f) / CoreVABackend.chip.sharedMemCluster[clusterId][0] + "%)"; //TODO: multiple shared memories per cluster
            }
        }
        if (!sharedMemUsage.isEmpty()) {
            logger.log(Level.INFO, "shared mem usage per cluster (layout: {0}): {1}", new Object[]{Compiler.memoryLayout, sharedMemUsage});
        }
    }

    public static void printCommStat(Map<FilterInstance, Processor> partition) {
        CommunicationModel cm = new CommunicationModel(chip, partition);
        String linkStat = "";
        double tp = Mutation.getThroughput(evaluators);
        ThroughputEvaluator eva = new ThroughputEvaluator();
        eva.evaluate(partition);
        int steadyState = eva.getBusyProcessorWork();

        logger.log(Level.INFO, "steady state: {0} cycle", new Object[]{steadyState});
        logger.log(Level.INFO, "output_rate: {0} bit/cycle", new Object[]{String.format("%.4f", tp * 4 * 8 / 1000000)});

        // link communication
        for (Link lnk : cm.getLinkData().keySet()) {
            linkStat += String.format("\n\t\t\t%-20s: %5d byte, %.4f bit/cycle", lnk.toString(), cm.getTransmittedData(lnk), (float) (cm.getTransmittedData(lnk) * 8) / steadyState);
        }
        if (!linkStat.isEmpty()) {
            logger.log(Level.INFO, "link_communication:" + linkStat);
        }

        // filter communication
        String filtStat = "";
        for (FilterInstance f : partition.keySet()) {
            int in = 0;
            for (AbstractChannel c : f.inputs.values()) {
                in += c.SteadyItemsPeek() * c.dst.filter.type.input.SizeOf(f);
            }
            int out = 0;
            for (AbstractChannel c : f.outputs.values()) {
                out += c.SteadyItemsPushPop() * c.src.filter.type.output.SizeOf(f);
            }

            filtStat += String.format("\n\t\t\t%-20s: in: %5d byte, out: %5d byte", f.toString(), in, out);
        }
        logger.log(Level.INFO, "filter_communication:" + filtStat);
    }
    
    private static void genDottyProgGraph(Path folder, String prog, List<FilterInstance> filters) {
        PrintStream g;
        try {
            g = new PrintStream(new File(folder + "/" + prog +".dot"));
        } catch (FileNotFoundException ex) {
            return;
        }
        g.append("digraph prof {\n");
        g.append("ratio=fill; margin=0; nodesep=0.25; ranksep=0.5;\n");
        g.append("node [style=filled; fillcolor=\"#EEEEEE\"; shape=box; fontname=\"arial\"];\n");
        g.append("edge [fontname=\"arial\"; fontsize=9;];\n");
        for(FilterInstance f : partitioning.keySet()) {
            String dst = f.StreamNodeName();
            int in = 0;
            for (AbstractChannel c : f.inputs.values()) {
                in += c.SteadyItemsPeek();
            }
            int out = 0;
            for (AbstractChannel c : f.outputs.values()) {
                out += c.SteadyItemsPushPop();
            }
            Evaluator evaluator = new ThroughputEvaluator();
            evaluator.evaluate(partitioning);
            Map<FilterInstance, Integer> eval = evaluator.getFiltersRank();
            
            g.append(dst + "[");
            if(f.isFissedNode) g.append("color=\"#1111EE\";");
            // node label           
            g.append("label=<<FONT POINT-SIZE=\"12\">" + dst +"</FONT><BR/>"
                    + "<FONT POINT-SIZE=\"9\">work: " + eval.get(f) + "</FONT><BR/>"
                    + "<FONT POINT-SIZE=\"9\">in: " + in + " out: " + out + "</FONT><BR/>"
                    + "<FONT POINT-SIZE=\"9\">init mult: "+ f.initMult + "</FONT><BR/>"
                    + "<FONT POINT-SIZE=\"9\">peek: " + f.peek + " pop: " + f.pop + " push: "+ f.push +" nsteady: "+ f.netSteady()+"</FONT>>;");
            g.append("];\n");
            for(AbstractChannel c : f.inputs.values()) {
                String src = c.src.StreamNodeName();
                g.append(src + " -> " + dst);
                // edge label
                //g.append(" [xlabel=" + c.SteadyItemsPushPop() + "]");
                g.append(";\n");
            }        
        }
        g.append("}");  
    }

    public static void Compile(String filename, Program program, List<FilterInstance> filters, String xmlPath) throws FileNotFoundException, IOException {
        BuildChannels(partitioning);
        //This needs to be done before Generate is called (in case you use a partitioner that doesn't analyse throughput!)
        ThroughputEvaluator evaluator = new ThroughputEvaluator();
        evaluator.getEstimate(null,partitioning);
        Path folder = CoreVACodeGenerator.Generate(filename, chip, program, partitioning);
        String progName = (new File(filename)).getName();
        progName = progName.substring(0, progName.lastIndexOf("."));
        
        // dotty app graph        
        genDottyProgGraph(folder, progName, filters);
        
        MemoryEstimator mem = new MemoryEstimator(partitioning);
        if (xmlPath != null) {
            printMemStat(mem);
        }
        if (streamit2.Compiler.savePartition) {
            PartitionLoader.storePartition(folder.toAbsolutePath().toString(), chip, partitioning);
        }

        // generated memory layout for linker script generation
        generateMemoryLayout(folder, mem, streamit2.Compiler.memoryLayout);

        if (streamit2.Compiler.analyseLatency) {

            LatencyEvaluator evaluator2 = new LatencyEvaluator();
            evaluator2.getEstimate(folder, partitioning);
        }
        evaluator.getEstimate(new PrintStream(folder + "/PerfEstimate.txt"), partitioning);
        if ((streamit2.Compiler.createTimeline != null) && (streamit2.Compiler.partitioner.contains(":sim"))) {
            TimelineWriter.writeTimeline(partitioning, filename, chip, streamit2.Compiler.createTimeline);
        }
        BuildChannels(partitioning);
        if (streamit2.Compiler.mergeSplitMappingFile) {
            BufferEstimator mbe = BufferEstimator.getInstance();
            mbe.createMergeMappingFile(folder + "/merge_split_mapping.csv", partitioning);
        }

        // log stats for communication on links (bus, nci, noc) and filters
        printCommStat(partitioning);
    }

    public static void BuildChannels(Map<FilterInstance, Processor> partitioning) {
        // reset internal data structures of model + used memory to 0 on each core
        chip.reset();
        int res = 0;
        for (FilterInstance filter : partitioning.keySet()) {
            filter.inputs = new LinkedHashMap<>();
            filter.outputs = new LinkedHashMap<>();
        }
        for (FilterInstance src : partitioning.keySet()) {
            if (src.push > 0 && src.split.isEmpty()) {
                src.FileWriterChannel = new CoreVAFileOutputChannel(src);
                ((CoreVAFileOutputChannel) src.FileWriterChannel).fioSharedMemOffset = chip.fioSharedMem;
                chip.fioSharedMem += src.FileWriterChannel.getMemUsedShared().getValue();
            }
            if (src.push > 0 && src.join.isEmpty() && src.filter.id.equals("FileReader")) {
                src.FileReaderChannel = new CoreVAFileInputChannel(src);
                ((CoreVAFileInputChannel) src.FileReaderChannel).fioSharedMemOffset = chip.fioSharedMem;
                chip.fioSharedMem += src.FileReaderChannel.getMemUsedShared().getValue();                
            }
            for (FilterInstance[] list : src.split) {
                for (FilterInstance dst : list) {
                    if (!src.outputs.containsKey(dst)) {
                        AbstractChannel channel = SelectChannel(src, (CoreVAProcessor) partitioning.get(src), dst, (CoreVAProcessor) partitioning.get(dst));
                        src.outputs.put(dst, channel);
                        dst.inputs.put(src, channel);
                    }
                }
            }
            if (src.inputs.size() > 1) {
                if ("FileWriter".equals(src.filter.id)) {
                    src.input_buffer = new FileWriterMergeBuffer(null, src);
                } else {
                    src.input_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(null, src);
                }
            } else {
                src.input_buffer = null;
            }
            if ("FileReader".equals(src.filter.id)) {
                src.output_buffer = new FileReaderSplitBuffer(src);
            } else if (src.outputs.size() > 1) {
                src.output_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(src, null);
            } else {
                src.output_buffer = null;
            }
        }
    }

    private static AbstractChannel SelectChannel(FilterInstance src, CoreVAProcessor srcProc, FilterInstance dst, CoreVAProcessor dstProc) {
        if (srcProc == dstProc) {
            return new MemoryChannel2(src, dst);
        } else if (srcProc.getRow() == dstProc.getRow() && srcProc.getCol() == dstProc.getCol()) {
            if (streamit2.Compiler.useRingbuffer) {
                return new CoreVAClusterChannelRingbuffer(src, srcProc, dst, dstProc);
            } else {
                if ((chip.sharedMemCluster[srcProc.getClusterId()].length > 0)
                        && (streamit2.Compiler.memoryLayout.equals("shared") || streamit2.Compiler.memoryLayout.equals("hybrid"))) {
                    return new CoreVAClusterChannelShared(src, srcProc, dst, dstProc);
                }
                return new CoreVAClusterChannel(src, srcProc, dst, dstProc);
            }
        } else {
            if ((chip.sharedMemCluster[srcProc.getClusterId()].length > 0)
                    && (chip.sharedMemCluster[dstProc.getClusterId()].length > 0)
                    && (streamit2.Compiler.memoryLayout.equals("shared") || streamit2.Compiler.memoryLayout.equals("hybrid"))) {
                return new CoreVANocChannelShared(src, srcProc, dst, dstProc);
            }
            return new CoreVANocChannel(src, srcProc, dst, dstProc);
        }
    }

    public static Estimator chooseFilterEstimator(String srcFile, List<FilterInstance> filters) {
        if (Compiler.partitioner.contains(":sim")) {
            Compiler.filterEstimator = new PerfFileEstimator(srcFile, filters);
        } else if (Compiler.partitioner.contains(":ast")) {
            Compiler.filterEstimator = new ASTEstimator();
        } else {
            logger.log(Level.SEVERE, "unknown estimator");
            System.exit(-1);
        }
        return Compiler.filterEstimator;
    }

    public static SortedMap<FilterInstance, Processor> choosePartitioner(String srcFile, Program program, List<FilterInstance> filters, Chip chip) {
        if (chip.getProcessors().length == 1) {
            return Partitioner.partition(filters, chip);
        }
        if (streamit2.Compiler.loadPartition != null) {
            return PresetPartitioner.partition(filters, streamit2.Compiler.loadPartition, chip);
        }
        if (streamit2.Compiler.partitioner.contains("sa:")) {
            String[] splits = streamit2.Compiler.partitioner.split("sa:");
            if (splits[0].isEmpty()) {
                return SimulatedAnnealingPartitioner5.partition(filters, chip, estimator, evaluators, null);
            }
            streamit2.Compiler.partitioner = streamit2.Compiler.partitioner.replace("sa:", "");
            SortedMap<FilterInstance, Processor> initialPartition = choosePartitioner(srcFile, program, filters, chip);
            return SimulatedAnnealingPartitioner5.partition(filters, chip, estimator, evaluators, initialPartition);
        }
        if (streamit2.Compiler.partitioner.contains("greedy:")) {
            if (splitTrees) {
                SmartSplitTreeMutator sptm = new SmartSplitTreeMutator(filters, chip, estimator);
            }
            return new GreedyPartitioner().createPartition(filters, chip, estimator, false).getFilterMapping();
        }
        if (streamit2.Compiler.partitioner.contains("gui:")) {
            return GUIManualPartitioner.partition(filters, chip, estimator, evaluators, null, srcFile, program);
        }
        if (streamit2.Compiler.partitioner.contains("mit:")) {
            return MITPartitioner.partition(filters, chip, estimator, evaluators);
        }
        if (streamit2.Compiler.partitioner.contains("gm:")) {
            return GroupMigrationPartitioner.partition(filters, chip, estimator);
        }

        //Add Tae Patitioner 
        if (streamit2.Compiler.partitioner.contains("taeILP:")) { 
            return new TaePartitionerILP().partition(filters, chip, estimator, evaluators); 
        }
        if (streamit2.Compiler.partitioner.contains("taeILP_NotFission:")) { 
            return new TaePartitionerILP_NotFission().partition(filters, chip, estimator, evaluators); 
        }
        
        logger.log(Level.SEVERE, "unknown partitioner");
        System.exit(-1);
        return null;
    }

    public static void generateMemoryLayout(Path folder, MemoryEstimator mem, String memLayout) throws IOException {
        long instrMemOrigin = 0;
        long instrMemLength = 0x00100000;
        long localMemOrigin = 0x00100000;
        long localMemLength = 0x00100000;
        long sharedMemOrigin = 0x60000000;
        long sharedMemLength = 0;
        long localNocSendChannels = 256;
        long localNocRecvChannels = 256;
        long sharedNocSendChannels = 0;
        long sharedNocRecvChannels = 0;
        long clusterMaxCpus = 128;
        long clusterChannelWidth = 3;
               
        Map<Integer, Long> privateMem = new LinkedHashMap<>();
        Map<Integer, Long> privateMemTotal = new LinkedHashMap<>();
        Map<Integer, Long> sharedMem = new LinkedHashMap<>();
        Map<Integer, Long> sharedMemTotal = new LinkedHashMap<>();

        switch (memLayout) {
            case "shared":
                // total private memory
                for (Processor p : chip.getProcessors()) {
                    CoreVAProcessor cp = (CoreVAProcessor) p;
                    long usedMem = (privateMemTotal.containsKey(cp.getClusterId())) ? privateMemTotal.get(cp.getClusterId()) : 0;
                    long newMem = localMemLength; //mem.getEstimationLocal(cp);
                    privateMemTotal.put(cp.getClusterId(), usedMem + newMem);
                    long usedSharedMem = (sharedMemTotal.containsKey(cp.getClusterId())) ? sharedMemTotal.get(cp.getClusterId()) : 0;
                    sharedMemTotal.put(cp.getClusterId(), usedSharedMem + 0x100000); //TODO: TCDM size = 1MB/CPU
                }
                // private mem starts in shared mem
                localMemOrigin = sharedMemOrigin;
                break;
            case "hybrid": // both: shared+hybrid
                // total shared (tcdm) memory                
                for (Processor p : chip.getProcessors()) {
                    CoreVAProcessor cp = (CoreVAProcessor) p;
                    long usedSharedMem = (sharedMemTotal.containsKey(cp.getClusterId())) ? sharedMemTotal.get(cp.getClusterId()) : 0;
                    sharedMemTotal.put(cp.getClusterId(), usedSharedMem + 0x100000); //TODO: TCDM size = 1MB/CPU
                }
                break;
            default:
                break;
        }

        for (Processor p : chip.getProcessors()) {
            CoreVAProcessor cp = (CoreVAProcessor) p;
            String lnkFilename = folder.toString() + File.separator + "coreva" + File.separator + "cores" + File.separator + cp.toString() + ".sc";

            clusterMaxCpus = chip.getMaxDepth();
            clusterChannelWidth = chip.getClusterChannelWidth(cp);
            localNocSendChannels = chip.getMaxChannelSend(cp);
            localNocRecvChannels = chip.getMaxChannelRecv(cp);
            
            switch (memLayout) {
                case "shared":
                    // private + shared mem in shared mem
                    localMemOrigin = (privateMem.containsKey(cp.getClusterId())) ? privateMem.get(cp.getClusterId()) : localMemOrigin + privateMemTotal.get(cp.getClusterId());
                    //privateMemLength = mem.getEstimationLocal(cp);
                    privateMem.put(cp.getClusterId(), localMemOrigin + localMemLength);
                    // NoC Channels are located in shared mem
                    localNocSendChannels = 0;
                    localNocRecvChannels = 0;
                    sharedNocSendChannels = chip.getMaxChannelSend(cp);
                    sharedNocRecvChannels = chip.getMaxChannelRecv(cp);
                    // all private mem is in shared mem, shared mem starts afterwards
                    sharedMemOrigin = (sharedMem.containsKey(cp.getClusterId())) ? sharedMem.get(cp.getClusterId()) : sharedMemOrigin;
                    //sharedMemLength = mem.getEstimationShared(cp);
                    sharedMemLength = (sharedMemTotal.containsKey(cp.getClusterId())) ? sharedMemTotal.get(cp.getClusterId()) : sharedMemLength;
                    sharedMem.put(cp.getClusterId(), sharedMemOrigin /*+ sharedMemLength*/); //TODO? independet segements
                    break;
                case "hybrid":
                    // private mem in local mem, shared mem in tcdm
                    //privateMemLength = mem.getEstimationLocal(cp);
                    //sharedMemLength = mem.getEstimationShared(cp);
                    // NoC Channels are located in shared mem
                    localNocSendChannels = 0;
                    localNocRecvChannels = 0;
                    sharedNocSendChannels = chip.getMaxChannelSend(cp);
                    sharedNocRecvChannels = chip.getMaxChannelRecv(cp);                    
                    sharedMemLength = (sharedMemTotal.containsKey(cp.getClusterId())) ? sharedMemTotal.get(cp.getClusterId()) : sharedMemLength;
                    break;
                case "local":
                default:
                    // private mem in local mem, no shared mem
                    break;
            }

            try (FileWriter file_writer = new FileWriter(lnkFilename)) {
                PrintWriter writer = new PrintWriter(file_writer);
                writer.println("/* generated by compiler */");
                writer.println("/* CLUSTER CONFIGURATION */");
                writer.println("__mpsoc_cluster_max_cpus = " + clusterMaxCpus + ";");
                writer.println(".cluster_channel_width   = " + clusterChannelWidth + ";");
                writer.println("/* NOC CHANNEL CONFIGURATION */");
                writer.println("__local_noc_send_channels  = " + localNocSendChannels + ";");
                writer.println("__local_noc_recv_channels  = " + localNocRecvChannels + ";");
                writer.println("__shared_noc_send_channels = " + sharedNocSendChannels + ";");
                writer.println("__shared_noc_recv_channels = " + sharedNocRecvChannels + ";");
                writer.println("/* MEMORY CONFIGURATION */");
                writer.println("__coreva_instr_mem_origin  = 0x" + Long.toHexString(instrMemOrigin) + ";");
                writer.println("__coreva_instr_mem_length  = 0x" + Long.toHexString(instrMemLength) + ";");
                writer.println("__coreva_local_mem_origin  = 0x" + Long.toHexString(localMemOrigin) + ";");
                writer.println("__coreva_local_mem_length  = 0x" + Long.toHexString(localMemLength) + ";");
                writer.println("__coreva_shared_mem_origin = 0x" + Long.toHexString(sharedMemOrigin) + ";");
                writer.println("__coreva_shared_mem_length = 0x" + Long.toHexString(sharedMemLength) + ";");
            }
        }
    }
}
