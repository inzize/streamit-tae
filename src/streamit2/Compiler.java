package streamit2;

import ast.Program;
import backend.common.Chip;
import backend.common.PartitionLoader;
import backend.common.estimators.Estimator;
import backend.common.evaluators.LatencyEvaluator;
import backend.common.partitioner.PartitionerTrace;
import static backend.coreVA.CoreVABackend.partitioning;
import hierarchicalGraph.FilterInstance;
import static hierarchicalGraph.StreamConnector.qut_graph;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaRunner.Runner;
import parser.Parser;

public class Compiler {
    public enum OptimizeTargets {
        LATENCY, THROUGHPUT;
    }
    public enum FileReaderMode {
        LIMIT_NO, LIMIT_TO_TARGET_THROUGHPUT, LIMIT_TO_FINAL_STREAMIT_THROUGHPUT;
    }
    private final static Logger logger = Logger.getLogger("compiler");
    public static int ProcRows = 1;
    public static int ProcCols = 1;
    public static int ProcIds = 1;
    public static double maxMem = 0.25;
    public static String memoryLayout = "local";
    public static String xmlPath;
    public static boolean trace;
    public static boolean iterateOverMult;
    public static String partitioner;
    public static boolean filterProfiler = false;
    public static int forcesplitter = 1;
    public static int forcejoiner = 1;
    public static int resa = 2;
    public static boolean abortOnUnknownEstimation = false;
    public static boolean useRingbuffer = false;
    public static int SATimeout = 1000000; // Integer.MAX_VALUE is too large
    public static int maxMult = Integer.MAX_VALUE;
    public static String createTimeline;
    public static String outputDir = new String();
    public static boolean savePartition = true;
    public static String loadPartition = null;
    public static boolean partitionTrace = false;
    public static PartitionerTrace pTrace;
    public static boolean analyseLatency = false;
    public static boolean mergeSplitMappingFile = false;
    public static boolean splitTrees = false;
    public static OptimizeTargets optimizeTarget = OptimizeTargets.THROUGHPUT;
    public static int targetLatency = -1;
    public static int targetThroughput = -1;
    public static FileReaderMode fileReaderMode = FileReaderMode.LIMIT_NO;
    public static String program="";
    public static Estimator filterEstimator = null;
    public static Chip chip;
    

    private static void DumpGraph(StreamNode graph, String filename) throws Exception {
	qut_graph = new PrintStream(new File(filename.substring(0, filename.lastIndexOf("."))+".graph"));
        graph.Dump(0);
    }

    public static void Compile(String filename) throws Exception {
        Program.Reset();
        // Parsing
        Parser parser = new Parser(filename);
        Program program = parser.Parse();
        // Semantic Analysis
        program.Analyse(null);
        // Generate Hierarchical Stream Graph
        StreamNode graph = BuildStreamGraph(program);
        DumpGraph(graph, filename);
        //FilterInstance.reverseGraph();
        program.LiftDeclarations();
        // Semantic Analysis
        program.Analyse(null);
        StreamNode.backupGraph(graph);
        graph.SetParent(null);
        // Determine Init and Steady state multiplicities
        graph.SteadySchedule(null);
        graph.InitSchedule(0);
        StreamNode.saveGraphNetSteady();
        graph.Flatten();
        List<FilterInstance> filters = graph.InOrder();
        PartitionLoader.setFilterNames(filters);
        SimplifyEdges(filters);
        PrimePumpSchedule(NormalPrimePump(filters));
        //don't move primepumpschedule below partition because partition creates channels, channels calculate the required buffer size on creation time which also depends on primepump count which could result in a different perfect partition?
        // Generate code
        if (ProcRows > 1 || ProcCols > 1 || ProcIds > 1 || xmlPath != null) {
            try {
                backend.coreVA.CoreVABackend.Partition(filename, program, filters, ProcRows, ProcCols, ProcIds, xmlPath);
            } catch (RuntimeException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.log(Level.SEVERE, "Partitioning failed. {0}", new Object[]{sw.toString()});
                System.exit(-1);                
            }
            filters = graph.InOrder();
            if (analyseLatency) {
                LatencyEvaluator evaluator2 = new LatencyEvaluator();
                evaluator2.getEstimate(null, partitioning);
            }
            //logger.log(Level.INFO, "With normal prime pump:");
            //PrimePumpSchedule(NormalPrimePump(filters));            
            //logger.log(Level.INFO, "Without prime pump:");
            //PrimePumpSchedule(NoPrimePump(filters));            
            //evaluator2.getEstimate(null, filters, partitioning);               
            if (iterateOverMult) {
                StreamNode.fixRootMult(1);
                filename = filename.substring(0, filename.lastIndexOf(".")) + "_1.str";
                while (StreamNode.getRootMult() <= maxMult) {
                    logger.warning(filename);
                    try {
                        backend.coreVA.CoreVABackend.Partition(filename, program, filters, ProcRows, ProcCols, ProcIds, xmlPath);
                        filters = graph.InOrder();
                        backend.coreVA.CoreVABackend.Compile(filename, program, filters, xmlPath);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        return;
                    }
                    StreamNode.fixRootMult(StreamNode.getRootMult() + 1);
                    filename = filename.substring(0, filename.lastIndexOf("_")) + "_" + StreamNode.getRootMult() + ".str";
                }
                return; //If you want to remove this set filename to maxMult
            } else {
                backend.coreVA.CoreVABackend.Compile(filename, program, filters, xmlPath);
            }
        }

        int globalMult = (int) StreamNode.getGlobalMult();
        //GenerateChannelProfiler channel_profiler = new GenerateChannelProfiler();
        //channel_profiler.Generate(filename);
        StreamNode.restoreGraph();
        graph.SetParent(null);
        // Determine Init and Steady state multiplicities
        graph.SteadySchedule(null);
        graph.InitSchedule(0);
        graph.Flatten();
        filters = graph.InOrder();
        SimplifyEdges(filters);
        //PrimePumpSchedule(NormalPrimePump(filters));
        // set rootMult to global multiplicity of parallel code
        Compiler.maxMult = globalMult;        
        StreamNode.setRootMult(globalMult);
        //Generate sequential code
        backend.sequential.SequentialBackend.Compile(filename, program, filters);
    }

    private static StreamNode BuildStreamGraph(Program program) throws ClassNotFoundException, Exception {
        StringWriter string_writer = new StringWriter();
        program.GenerateInitCode(string_writer);
        String generated_src_code = string_writer.toString();
        Class init;
        Method main = null;
        try {
            // Compile and Load the dynamically generated class
            init = new Runner().Load("streamit2.Init", generated_src_code);
            // Use reflection to find the main method
            main = init.getMethod("main", new Class[0]);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "building streamit graph failed (" + e.getMessage() + ")", e);
            logger.log(Level.INFO, "generated src code:\n{0}", generated_src_code);
            System.exit(-1);
        }
        // Invoke the main method to generate the stream graph
        return (StreamNode) main.invoke(null, new Object[0]);
    }

    public static void SimplifyEdges(List<FilterInstance> filters) {
        for (FilterInstance filter : filters) {
            if (filter.join.size() > 0) {
                FilterInstance first = filter.join.get(0);
                boolean same = true;
                for (FilterInstance src : filter.join) {
                    if (src != first) {
                        same = false;
                    }
                }
                if (same) //filter.join = Arrays.asList(first);
                {
                    filter.join = new ArrayList<>(Arrays.asList(first));
                }
            }
            if (filter.split.size() > 0) {
                FilterInstance[] first = filter.split.get(0);
                boolean same = true;
                for (FilterInstance[] src : filter.split) {
                    if (!FilterInstance.equals(src, first)) {
                        same = false;
                    }
                }

                if (same) {
                    filter.split = new ArrayList<>();
                    filter.split.add(first);
                }
            }
        }
    }

    private static boolean canFire(FilterInstance filter, List<FilterInstance> previous) {
        for (FilterInstance p : filter.join) {
            if (!previous.contains(p)) {
                return false;
            }
        }
        return true;
    }

    private static void Propagate(int successorCount, FilterInstance filter, Map<FilterInstance, Integer> prime) {
        filter.primePumpCount = Math.max(filter.primePumpCount, successorCount + prime.get(filter));
        for (FilterInstance predecessor : new HashSet<>(filter.join)) {
            Propagate(filter.primePumpCount, predecessor, prime);
        }
    }

    /**
     * find last filter without outgoing edges prefer filter with higher id if
     * multiple filters
     *
     * @param all_filters
     * @return filter without outgoing edges and highes id
     */
    public static FilterInstance Last(Collection<FilterInstance> all_filters) {
        FilterInstance last = null;
        int last_id = -1;
        for (FilterInstance filter : all_filters) {
            if ((filter.split.isEmpty()) && (filter.filter_id > last_id)) {
                last = filter;
                last_id = filter.filter_id;
            }
        }
        return last;
    }

    public static Map<FilterInstance, Integer> NormalPrimePump(List<FilterInstance> all_filters) {
        Map<FilterInstance, Integer> prime = new java.util.HashMap<>();
        FilterInstance last = Last(all_filters);
        for (FilterInstance filter : all_filters) {
            if (filter == last) {
                prime.put(filter, 0);
            } else {
                prime.put(filter, 1);
            }
        }
        return prime;
    }

    private static Map<FilterInstance, Integer> NoPrimePump(List<FilterInstance> all_filters) {
        Map<FilterInstance, Integer> prime = new java.util.HashMap<>();
        for (FilterInstance filter : all_filters) {
            prime.put(filter, 0);
        }
        return prime;
    }

    public static void PrimePumpSchedule(Map<FilterInstance, Integer> prime) {
        for (FilterInstance filter : prime.keySet()) {
            filter.primePumpCount = 0;
        }
        Propagate(0, Last(prime.keySet()), prime);
        FilterInstance.primePumpMax = 0;
        for (FilterInstance filter : prime.keySet()) {
            if (filter.primePumpCount > FilterInstance.primePumpMax) {
                FilterInstance.primePumpMax = filter.primePumpCount;
            }
        }
    }
}
