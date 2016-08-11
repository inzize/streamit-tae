package streamit2;

import backend.common.FileManager;
import backend.common.partitioner.PartitionerTrace;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import streamit2.Compiler.FileReaderMode;
import streamit2.Compiler.OptimizeTargets;
import static streamit2.Compiler.OptimizeTargets.LATENCY;

public class Main {

    private static final Logger logger = Logger.getLogger("compiler");
    private static String[] strArgs;
    private static final Pattern NumProcs = Pattern.compile("-(\\d+)[*xX](\\d+)[*xX](\\d+)");
    private static final Pattern XMLFile = Pattern.compile("-xml=(.*)");
    private static final Pattern Partitioner = Pattern.compile("-part=(.*)");
    private static final Pattern Multiplier = Pattern.compile("-mult=(\\d*)");
    private static final Pattern SATimeout = Pattern.compile("-satimeout=(\\d*)");
    private static final Pattern loadPartition = Pattern.compile("-loadpart=(.*)");
    private static final Pattern resaCount = Pattern.compile("-resa=(\\d*)");
    private static final Pattern forcesplitter = Pattern.compile("-forcesplitter=(\\d*)");
    private static final Pattern forcejoiner = Pattern.compile("-forcejoiner=(\\d*)");
    private static final Pattern maxMult = Pattern.compile("-maxmult=(\\d*)");
    private static final Pattern createTimeline = Pattern.compile("--create-timeline=(.*)");
    private static final Pattern outputDir = Pattern.compile("--output-dir=(.*)");
    private static final Pattern maxMem = Pattern.compile("--max-mem=(.*)");
    private static final Pattern memoryLayout = Pattern.compile("--memlayout=(.*)");
    private static final Pattern optimize = Pattern.compile("--optimize=(.*)");
    private static final Pattern targetLatency = Pattern.compile("--target-latency=(.*)");
    private static final Pattern targetThroughput = Pattern.compile("--target-throughput=(.*)");
    private static final Pattern fileReaderMode = Pattern.compile("--fileReader-mode=(.*)");
    
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        // setup root logger
        Logger system = Logger.getLogger("");
        Handler[] handlers = system.getHandlers();
        for (Handler h : handlers) {
            h.setFormatter(new LogFormatter());
            h.setLevel(Level.INFO);
        }
        system.setLevel(Level.INFO);

        strArgs = args;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                Option(arg);
            } else {
                Process(arg);
            }
        }
    }
    
    public static String getConfigDir() {
        final String gitDir = System.getenv("COREVA_STREAMIT_PERF");
        final String confDir = System.getenv("COREVA_BUILD_DIR_SUFFIX");
        if (gitDir==null) {
            throw new RuntimeException("COREVA_STREAMIT_PERF is not set!");
        }
        if (confDir==null) {
            throw new RuntimeException("COREVA_BUILD_DIR_SUFFIX is not set!");
        }
        return gitDir+"/config"+confDir+"/";
    }
    
    private static void Option(String arg) {
        Matcher NumProcsMatcher = NumProcs.matcher(arg);
        Matcher XmlFileMatcher = XMLFile.matcher(arg);
        Matcher MultMatcher = Multiplier.matcher(arg);
        Matcher PartitionerMatcher = Partitioner.matcher(arg);
        Matcher SATimeoutMatcher = SATimeout.matcher(arg);
        Matcher loadPartitionMatcher = loadPartition.matcher(arg);
        Matcher resaMatcher = resaCount.matcher(arg);
        Matcher forcesplitterMatcher = forcesplitter.matcher(arg);
        Matcher forcejoinerMatcher = forcejoiner.matcher(arg);
        Matcher maxMultMatcher = maxMult.matcher(arg);
        Matcher createTimelineMatcher = createTimeline.matcher(arg);
        Matcher outputDirMatcher = outputDir.matcher(arg);
        Matcher maxMemMatcher = maxMem.matcher(arg);
        Matcher memoryLayoutMatcher = memoryLayout.matcher(arg);
        Matcher optimizeMatcher = optimize.matcher(arg);
        Matcher targetLatencyMatcher = targetLatency.matcher(arg);
        Matcher targetThroughputMatcher = targetThroughput.matcher(arg);
        Matcher fileReaderModeMatcher = fileReaderMode.matcher(arg);
        
        if ("-?".equals(arg) || "-h".equals(arg) || "-help".equals(arg) || "\\help".equals(arg) || "\\?".equals(arg)) {
            logger.log(Level.INFO, "Usage: streamit2 [-c] [-RxCxI] file.str|directory");
            logger.log(Level.INFO, "-RxCxI: Generate parallel code for processor array with R rows and C columns and I cores per cluster");
            logger.log(Level.INFO, "-xml=[file]: Use MPSoC xml model");
            logger.log(Level.INFO, "-parttime: Print needed time for partitioning");
            logger.log(Level.INFO, "-mult=n");
            logger.log(Level.INFO, "--iterMult");
            logger.log(Level.INFO, "-maxmult=n: maximum multiplicity allow for simulated annealing algorithm");
            logger.log(Level.INFO, "-part=[partitioner:args]: Use special partitioner");
            logger.log(Level.INFO, "-parttrace: Create tracefile of partitioning process");
            logger.log(Level.INFO, "-satimeout=t: SA time limit in second");
            logger.log(Level.INFO, "-savepart: save partition configuration in a par file");
            logger.log(Level.INFO, "-loadpar=[file]: Use partition configuration file (*.par)");
            logger.log(Level.INFO, "--ringbuffer: use ringbuffers for communication inside a cluster");
            logger.log(Level.INFO, "--memlayout: memory layout in cpu cluster (local, hybrid, shared)");
            logger.log(Level.INFO, "-v: be verbose");
            System.exit(0);
        }
        if ("--analyse-latency".equals(arg)) {
            Compiler.analyseLatency = true;
        } else if ("--abort-on-unknown-estimation".equals(arg)) {
            Compiler.abortOnUnknownEstimation = true;
        } else if (targetThroughputMatcher.matches()) {
            Compiler.targetThroughput = Integer.parseInt(targetThroughputMatcher.group(0));
        } else if (targetLatencyMatcher.matches()) {
            Compiler.analyseLatency = true;
            Compiler.targetLatency = Integer.parseInt(targetLatencyMatcher.group(0));
        } else if (optimizeMatcher.matches()) {
            Compiler.optimizeTarget = OptimizeTargets.valueOf(optimizeMatcher.group(1));
            if (LATENCY == Compiler.optimizeTarget) {
                Compiler.analyseLatency = true;
            }
        } else if (fileReaderModeMatcher.matches()) {
            Compiler.fileReaderMode = FileReaderMode.valueOf(fileReaderModeMatcher.group(1));
        } else if ("--create-merge-split-file".equals(arg)) {
            Compiler.mergeSplitMappingFile = true;
        } else if (createTimelineMatcher.matches()) {
            Compiler.createTimeline = createTimelineMatcher.group(1);
        } else if ("--iterMult".equals(arg)) {
            Compiler.iterateOverMult = true;
        } else if ("-parttrace".equals(arg)) {
            Compiler.partitionTrace = true;
        } else if (forcesplitterMatcher.matches()) {
            int n = Integer.parseInt(forcesplitterMatcher.group(1));
            if (n < 2) {
                n = 1;
            }
            Compiler.forcesplitter = n;
        } else if (forcejoinerMatcher.matches()) {
            int n = Integer.parseInt(forcejoinerMatcher.group(1));
            if (n < 2) {
                n = 1;
            }
            Compiler.forcejoiner = n;
        } else if (resaMatcher.matches()) {
            int n = Integer.parseInt(resaMatcher.group(1));
            if (n < 2) {
                n = 1;
            }
            Compiler.resa = n;
        } else if ("-v".equals(arg)) {
            Logger system = Logger.getLogger("");
            Handler[] handlers = system.getHandlers();
            for (Handler h : handlers) {
                h.setLevel(Level.ALL);
                h.setFormatter(new LogFormatter());
            }
            system.setLevel(Level.ALL);
        } else if (MultMatcher.matches()) {
            logger.log(Level.WARNING, "Force mult to {0}", Integer.parseInt(MultMatcher.group(1)));
            StreamNode.fixRootMult(Integer.parseInt(MultMatcher.group(1)));
        } else if (maxMultMatcher.matches()) {
            int _maxMult = Integer.parseInt(maxMultMatcher.group(1));
            Compiler.maxMult = (_maxMult > 0) ? _maxMult : Compiler.maxMult;
        } else if (SATimeoutMatcher.matches()) {
            int _SATimeout = Integer.parseInt(SATimeoutMatcher.group(1));
            Compiler.SATimeout = (_SATimeout > 0) ? _SATimeout : Compiler.SATimeout;
        } else if (loadPartitionMatcher.matches()) {
            Compiler.loadPartition = loadPartitionMatcher.group(1);
        } else if (NumProcsMatcher.matches()) {
            Compiler.ProcRows = Integer.parseInt(NumProcsMatcher.group(1));
            Compiler.ProcCols = Integer.parseInt(NumProcsMatcher.group(2));
            Compiler.ProcIds = Integer.parseInt(NumProcsMatcher.group(3));
        } else if ("-trace".equals(arg)) {
            Compiler.trace = true;
        } else if ("--ringbuffer".equals(arg)) {
            Compiler.useRingbuffer = true;
        } else if (XmlFileMatcher.matches()) {
            Compiler.xmlPath = XmlFileMatcher.group(1);
            Compiler.xmlPath = getConfigDir() + Compiler.xmlPath;
            if (!(new File(Compiler.xmlPath)).isFile()) {
                logger.log(Level.SEVERE, "XML file \"" + Compiler.xmlPath + "\" does not exist");
                System.exit(-1);
            }
        } else if (PartitionerMatcher.matches()) {
            Compiler.partitioner = PartitionerMatcher.group(1);
            if (!Compiler.partitioner.startsWith("sa") && Compiler.SATimeout > 0) {
                logger.log(Level.INFO, "Timout parameter is only available for sa partitioner");
            }
        } else if (outputDirMatcher.matches()) {
            Compiler.outputDir = outputDirMatcher.group(1);
        } else if (maxMemMatcher.matches()) {
            Compiler.maxMem = Double.parseDouble(maxMemMatcher.group(1));
        } else if (memoryLayoutMatcher.matches()) {
            Compiler.memoryLayout = memoryLayoutMatcher.group(1);
        } else {
            System.err.println("unknown option " + arg);
        }
    }

    private static void Process(String name) throws Exception {
        File input = new File(name);
        if (input.isDirectory()) {
            for (File file : input.listFiles()) {
                Process(file.getAbsolutePath());
            }
        } else if (name.endsWith(".str")) {
            Compile(input.getAbsolutePath());
        }
    }

    private static void Compile(String filename) throws Exception {
        logger.log(Level.INFO, "Current Config {0}",getConfigDir());
        // setup logfile next to generated code
        FileHandler logHandler = new FileHandler(FileManager.CreateProjectDir(FileManager.CreateSrcDir(filename), filename).toString() + File.separator + "streamit.log", false);
        logHandler.setFormatter(new LogFormatter());
        // setup root logger
        Logger system = Logger.getLogger("");
        system.addHandler(logHandler);
        Logger logger = Logger.getLogger("compiler");
        logger.setLevel(Level.ALL);

        logger.log(Level.INFO, filename);
        logger.log(Level.INFO, "Arguments: {0}", Arrays.toString(strArgs));

        // setup partitioner trace file next to generated code
        if (Compiler.partitionTrace) {
            String partitionerTraceFile = FileManager.CreateProjectDir(FileManager.CreateSrcDir(filename), filename).toString() + File.separator + "ptrace_";
            if (Compiler.partitioner != null) {
                partitionerTraceFile += Compiler.partitioner.replaceAll("[^A-Za-z0-9]", "");
            }
            partitionerTraceFile += ".csv";
            Compiler.pTrace = new PartitionerTrace(partitionerTraceFile);
        } else {
            Compiler.pTrace = new PartitionerTrace("");
        }

        boolean continue_after_exception = false;

        if (continue_after_exception) {
            try {
                Compiler.Compile(filename);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.toString());
            }
        } else {
            Compiler.Compile(filename);
        }
        // close partitioning trace file
        if (Compiler.partitionTrace) {
            Compiler.pTrace.finish();
        }
    }
}
