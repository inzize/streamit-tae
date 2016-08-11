package backend.common.evaluators;

import backend.common.channels.AbstractChannel;
import backend.common.ChannelMessage;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.buffers.FileWriterMergeBuffer;
import backend.common.estimators.CommunicationModel;
import backend.common.estimators.Estimator;
import java.util.LinkedHashMap;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import streamit2.Compiler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyse Latency of a given partition
 * 
 * @see Evaluator
 */
public class LatencyEvaluator extends Evaluator {

    private class PerfData {
        public long prev_time;
        public long prev_latency;
        public long prev_throughput;
    }

    private final static Logger logger = Logger.getLogger("compiler");
    private final Chip chip;
    private final ThroughputEvaluator throughputEvaluator;
    private final Estimator filterEstimator;
    private long latency, throughput;
    private final long minLateny = Long.MAX_VALUE;
    private final Map<FilterInstance, PerfData> lastPerf;
    private final Map<FilterInstance, Integer> lastLatency; //use above!
    private Map<FilterInstance, Processor> lastPartition;

    public LatencyEvaluator() {
        this.filterEstimator = Compiler.filterEstimator;
        lastPerf = new LinkedHashMap();
        lastLatency = new LinkedHashMap();
        chip = Compiler.chip;
        throughputEvaluator = new ThroughputEvaluator();
    }
    
    public LatencyEvaluator(Estimator filterEstimator) {
        this.filterEstimator = filterEstimator;
        lastPerf = new LinkedHashMap();
        lastLatency = new LinkedHashMap();
        chip = Compiler.chip;
        throughputEvaluator = new ThroughputEvaluator(filterEstimator);
    }

    private int Latency(AbstractChannel c, Map<FilterInstance, Processor> partition) {
        return chip.getCommunicationLatency(partition.get(c.src), partition.get(c.dst));
    }

    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        try {
            throughputEvaluator.evaluate(partition);
            return getEstimate(null,partition);
        } catch (FileNotFoundException ex) {
            return 0;
        }
    }

    private String EstimateFile(Path folder, Processor p) {
        return folder + "/" + p.toString() + ".csv";
    }

    private Map<String, BufferedWriter> fileMap = new LinkedHashMap();
    
    private boolean EstimateFilter(FilterInstance f, Path folder, Map<FilterInstance, Processor> partition, CommunicationModel commModel, boolean steady, boolean afterPrimePump) throws FileNotFoundException {
        Processor p = partition.get(f);
        lastPartition = partition;
        BufferedWriter writer = null;
        if (folder != null) {
            String s = EstimateFile(folder, p);
            if (!fileMap.containsKey(s)) {
           //     logger.info("new File: "+s);
                try {
                    File file = new File(s);
                    if(!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
} 
                fileMap.put(s,new BufferedWriter(new FileWriter(s)));
                } catch (Exception e) {
                    logger.log(Level.WARNING,"Exception",e);
                    System.exit(-1);
                }
                    
            }
            writer = fileMap.get(s);
            
        }
        try {
            long time = p.previousFilterFinish + 4;

            if (writer != null && afterPrimePump) {
                writer.write(time + ";" + (p.previousFilter++));
                writer.newLine();
            } else {
                p.previousFilter++;
            }


            time += 2;

            long origin = time;

            //time+=16; //normal leave costs?

            if (f.isFirstFilterInGraph()) {
                //time+=9;
            }

            if (f.isFirstFilterInGraph() && writer != null) {
                writer.write(time + ";8");
                writer.newLine();
            }

            time+=1; //drop 2 costs
            if (writer != null) {
                writer.write(time + ";2;" + f.originalFilterName);
                writer.newLine();
            }

            //---WAIT READS---
            //Channels
            if (!f.isFirstFilterInGraph()) {
                origin = Long.MAX_VALUE;

                for (Map.Entry<FilterInstance, AbstractChannel> i : f.inputs.entrySet()) {
                    FilterInstance input = i.getKey();
                    AbstractChannel channel = i.getValue();

                    time += channel.getWaitReadCost(chip); // constant overhead even if message has already arrived

                    long need = steady ? channel.SteadyItemsPeek() : channel.InitItemsPeek();
                    while (channel.itemsAvailable < need && !channel.sentMessages.isEmpty()) {
                        ChannelMessage message = channel.sentMessages.remove();
                        channel.itemsAvailable += message.size;
                        if (message.origin < origin) {
                            origin = message.origin;
                        }

                        if (message.destTime > time) {
                            if (writer != null) {
                                writer.write(time + ";0");
                                writer.newLine();
                            }
                            time = message.destTime;
                            if (writer != null) {
                                writer.write(time + ";1");
                                writer.newLine();
                            }
                        }
                    }

                    if (writer != null) {
                        writer.write(time + ";3");
                        writer.newLine();
                    }
                }
            }
            //FileReader not impl
            //Merge if not handled by FileWriter 
            if ((f.input_buffer != null) && (f.FileWriterChannel == null)) {
                time += f.input_buffer.getWaitReadCost(chip);
                if (writer != null) {
                    writer.write(time + ";3");
                    writer.newLine();
                }
            }
            //---WAIT WRITES---
            //Channels
            for (Map.Entry<FilterInstance, AbstractChannel> o : f.outputs.entrySet()) {
                FilterInstance output = o.getKey();
                AbstractChannel channel = o.getValue();

                time += channel.getWaitWriteCost(chip); // constant overhead even if message has already arrive

                long need = steady ? channel.SteadyItemsPushPop() : channel.InitItemsPush();
                while (channel.slotsAvailable < need) {
                    ChannelMessage message = channel.ackMessages.remove();
                    channel.slotsAvailable += message.size;
                    if (message.destTime > time) {
                        if (writer != null) {
                            writer.write(time + ";0");
                            writer.newLine();
                        }
                        time = message.destTime;
                        if (writer != null) {
                            writer.write(time + ";1");
                            writer.newLine();
                        }
                    }
                }
                if (writer != null) {
                    writer.write(time + ";4");
                    writer.newLine();
                }
            }
            //FileWriter w merge
            if (f.FileWriterChannel != null) {
                time += f.FileWriterChannel.getWaitWriteCost(chip);
                if (writer != null) {
                    writer.write(time + ";4");
                    writer.newLine();
                }
                //This handles merges too!!!
                //Is it possible to move it to wait read?
                //FIXME: I'm a hack -> old OptimizeFileWriter
                time += (new FileWriterMergeBuffer(null, f)).getWaitWriteCost(chip);
                if (writer != null) {
                    writer.write(time + ";4");
                    writer.newLine();
                }
            }
            //Split prepare
            if (f.output_buffer != null) {
                time += f.output_buffer.getWaitWriteCost(chip);
                if (writer != null) {
                    writer.write(time + ";4");
                    writer.newLine();
                }
            }
            //---Work starts---
            // Multiply by Multiplicity?
            time += filterEstimator.estimateFilter(f); // time for the work function
            if (writer != null) {
                writer.write(time + ";5");
                writer.newLine();
            }

            //---DONE READS---
            //Channels        
            for (Map.Entry<FilterInstance, AbstractChannel> i : f.inputs.entrySet()) {
                FilterInstance input = i.getKey();
                AbstractChannel channel = i.getValue();

                time += channel.getDoneReadCost(chip); // constant overhead
                if (writer != null) {
                    writer.write(time + ";6");
                    writer.newLine();
                }

                long read = steady ? channel.SteadyItemsPushPop() : channel.InitItemsPop();
                channel.itemsAvailable -= read;
                channel.ackMessages.add(new ChannelMessage(read, 0, time + Latency(channel, partition)));
            }
            //---DONE WRITES---
            // SPLIT
            if (f.output_buffer != null) {
                time += f.output_buffer.getDoneWriteCost(chip);
                if (writer != null) {
                    writer.write(time + ";7");
                    writer.newLine();
                }
            }
            // Channels
            for (Map.Entry<FilterInstance, AbstractChannel> o : f.outputs.entrySet()) {
                FilterInstance output = o.getKey();
                AbstractChannel channel = o.getValue();

                time += channel.getDoneWriteCost(chip); // constant overhead
                if (writer != null) {
                    writer.write(time + ";7");
                    writer.newLine();
                }

                long arrivalTime = time + Latency(channel, partition) + channel.TransmissionTime(commModel);

                long written = steady ? channel.SteadyItemsPushPop() : channel.InitItemsPush();
                channel.slotsAvailable -= written;
                channel.sentMessages.add(new ChannelMessage(written, origin, arrivalTime));
            }
            //File Writer
            if (f.FileWriterChannel != null) {
                time += f.FileWriterChannel.getDoneWriteCost(chip);
                if (writer != null) {
                    writer.write(time + ";7");
                    writer.newLine();
                }
            }
            lastLatency.put(f, (int)time);
            //File Reader not impl
            //NO OUTPUT CHANNEL -> MARKER
            if (f.FileWriterChannel != null) {
            //if (f.outputs.isEmpty()) {
                PerfData perf;
                if (lastPerf.containsKey(f)) {
                    perf = lastPerf.get(f);
                } else {
                    perf = new PerfData();
                }
                throughput = time - perf.prev_time;
                latency = time - origin;
                if (writer != null) {
                    writer.write(time + ";9");
                    writer.newLine();
                }
                if (Math.abs(throughput - perf.prev_throughput) < 2 && Math.abs(latency - perf.prev_latency) < 2) {
                    return true;
                }

                perf.prev_time = time;
                perf.prev_latency = latency;
                perf.prev_throughput = throughput;
                lastPerf.put(f, perf);
                if (writer != null) {
                //    writer.println(time + ";9");
                    // writer.println(time + ";latency " + latency);
                }
            }

            p.previousFilterFinish = time;

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.WARNING,e.getMessage());
            logger.log(Level.SEVERE,"Error",e);
            System.exit(-1);
            return false;
        }
    }

    public double getEstimate(Path folder, Map<FilterInstance, Processor> partition) throws FileNotFoundException {
        currentPartition = partition;
        evaluate(partition);
        CommunicationModel commModel = new CommunicationModel(chip, partition);
        List<FilterInstance> filters = Arrays.asList(partition.keySet().toArray(new FilterInstance[1]));
        for (FilterInstance f : filters) {
            boolean found = false;
            for (Processor p : chip.getProcessors()) {
                if (partition.get(f)==p) {
                    found = true;
                    break;
                }
            }
            assert found;
        }
        if (filters.size()!=partition.keySet().size()) {
            logger.log(Level.SEVERE, "Filters: {0} part_filters: {1}", new Object[]{filters.size(), partition.keySet().size()});
            assert filters.size()==partition.keySet().size();
        }
        
          
        // init all processors ...
        for (Processor p : chip.getProcessors()) {
            p.previousFilterFinish = 0;
            p.previousFilter = 10;
        }

        // init all channels ...
        for (FilterInstance f : filters) {
            for (AbstractChannel channel : f.inputs.values()) {
                channel.itemsAvailable = 0;
                channel.slotsAvailable = channel.getBufferSize() * channel.numberOfBuffers;
                channel.sentMessages.clear();
                channel.ackMessages.clear();
            }
        }
        
        for (Processor p : chip.getProcessors()) { //create log (init phase doesn't create markers
            File log = new File(EstimateFile(folder, p));
            if (log.exists()) {
                log.delete();
            }
        }

        latency = throughput = 0;
        lastPerf.clear();
        boolean firstRound = true;
        // init phase ...
        for (FilterInstance f : filters) {
            if ((f.filter.prework != null && f.fission_id < 2) || f.initMult > 0) {
                EstimateFilter(f, null, partition, commModel, false, false);
   //             logger.info("With init phase!!!!!");
            }
        }



        List<FilterInstance> tmp = new ArrayList<>();
        for (int i = 0; i <= FilterInstance.primePumpMax; i++) { //iteration?
            for (FilterInstance f : filters) {
                if (i == FilterInstance.primePumpMax - f.primePumpCount) {
                    tmp.add(f);
                }
            }
        }
        
        // prime pump ...
        for (int iteration = 0; iteration <= FilterInstance.primePumpMax; iteration++) {
            for (Processor p : chip.getProcessors()) {
                p.previousFilter = 10;
            }

            for (FilterInstance f : tmp) {
                if (iteration >= FilterInstance.primePumpMax - f.primePumpCount) {
         //           logger.log(Level.WARNING, iteration + "->"+f.originalFilterName+"("+f.primePumpCount+","+FilterInstance.primePumpMax+")");
                    EstimateFilter(f, folder, partition, commModel, true, false);
                }
            }

        }
        // steady state ...
        boolean converged = false;
        for (int iteration = 0; !converged; iteration++) {
            if (iteration>1000) {
                logger.log(Level.SEVERE, "Latency simulation did not converge after "+iteration+" steady state iterations!!! Aborting");
                converged = true;
            }
            for (Processor p : chip.getProcessors()) {
                p.previousFilter = 10;
            }

            for (FilterInstance f : filters) {
                if (EstimateFilter(f, folder, partition, commModel, true, true)) {
                    converged = true;
                }
            }
        }
        //only print latency for last filter without outputs
        if (folder!=null)
            logger.log(Level.INFO, "Throughput " + throughput + ", Latency " + latency);

        
        for (BufferedWriter writer : fileMap.values()) {
            try {
                writer.close();
            } catch (Exception e) {
                logger.log(Level.WARNING,"Err",e);
                System.exit(-1);
            }
        }
        
//        if (latency<minLatency)
//            minLatency = latency;
        return throughput;
    }

    @Override
    public Map<FilterInstance, Integer> getBottleneckFilters() {
        Map<FilterInstance, Integer> tmp = new HashMap<>();
        FilterInstance highest = (FilterInstance) lastLatency.keySet().toArray()[0];
        for (FilterInstance f : lastLatency.keySet()) {
            if (lastLatency.get(highest)<lastLatency.get(f))
                highest = f;
        }
        tmp.put(highest,(int)(long)lastLatency.get(highest));
        return tmp;
    }

    @Override
    public Map<FilterInstance, Integer> getIdleFilters() {
        return new HashMap<>();
    }

    @Override
    public Processor getIdleProcessor() {
        Map<FilterInstance, Integer> tmp = new HashMap<>();
        FilterInstance lowest = (FilterInstance) lastLatency.keySet().toArray()[0];
        for (FilterInstance f : lastLatency.keySet()) {
            if (lastLatency.get(lowest)>lastLatency.get(f))
                lowest = f;
        }
        return lastPartition.get(lowest);
    }

    @Override
    public Integer getIdleProcessorWork() {
        return throughputEvaluator.getIdleProcessorWork();
    }

    @Override
    public Processor getBusyProcessor() {
         return throughputEvaluator.getBusyProcessor();
    }

    @Override
    public Integer getBusyProcessorWork() {
        return throughputEvaluator.getBusyProcessorWork();
    }

    @Override
    public double getValue() {
        return latency;
    }

    @Override
    public Map<FilterInstance, Integer> getFiltersRank() {
        return sortFilters(lastLatency);
    }

     
    @Override
    public Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc){
        return throughputEvaluator.filtersOnProcessorRank(proc);
    }
    
    @Override
    public Map<Processor, Integer> getProcessorsRank() {
        return throughputEvaluator.getProcessorsRank();
    }

    @Override
    public boolean isValid() {
            if (streamit2.Compiler.targetLatency<0)
            return true;
        return latency < streamit2.Compiler.targetLatency;
    }

}
