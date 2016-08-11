package backend.common.estimators;

import static ast.GeneratesCode.writer;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.Chip.Link;
import backend.common.buffers.FileWriterMergeBuffer;
import static backend.coreVA.CoreVABackend.chip;
import hierarchicalGraph.FilterInstance;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommunicationEstimator {
    private final Chip model;
    private Map<FilterInstance, Integer> filterCom;
    private Map<FilterInstance, List<FilterInstance>> destinationFilters;
    private final Map<FilterInstance, Processor> partition;
    private CommunicationModel comModel;

    public CommunicationEstimator(Map<FilterInstance, Processor> partition) {
        this.partition = new LinkedHashMap<>(partition);
        this.model = chip;
        comModel = new CommunicationModel(model, partition);
    }

    public boolean isValid() {
        for (FilterInstance filter : partition.keySet()) {
            for (AbstractChannel chan : filter.inputs.values()) {
                // ask model if all channel requirements are met 
               if (!chip.isValidBufferSize(partition.get(chan.src),partition.get(chan.dst), chan)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void estimatePartition() {
        destinationFilters = new LinkedHashMap<>();
        filterCom = new LinkedHashMap<>();
        comModel = new CommunicationModel(model, partition);
        for (FilterInstance filter : partition.keySet()) {
            updateFilterTime(filter, partition);
        }
    }

    public Map<Link, Integer> getLinkData(){
        return comModel.getLinkData();
    }

    public int getFilterCost(FilterInstance filter) {
        return filterCom.get(filter);
    }
    
    public Map<FilterInstance, Integer> getFilterCom() {
        return filterCom;
    }

    public int GetWaitCost(PrintStream writer, FilterInstance filter) {
        int total_cost = 0;
        //---Wait read---
        //Channel
        for (AbstractChannel input : filter.inputs.values()) {
            FilterInstance srcFilter = input.src;
            Processor src = partition.get(srcFilter);
            Processor dst = partition.get(filter);
            int cost = input.getWaitReadCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        wait read " + srcFilter + ": " + cost + " on " + input.getClass().getSimpleName());
            }
        }
        //FileReader
        if (filter.FileReaderChannel != null) {
            Processor src = partition.get(filter);
            int cost = filter.FileReaderChannel.getWaitWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        wait read file: " + cost + " on " + filter.FileReaderChannel.getClass().getSimpleName());
            }
        } 
        //Merge if not handled by FileWriter
        if ((null!=filter.input_buffer) && !("FileWriter".equals(filter.filter.id))) {
            int cost = filter.input_buffer.getWaitReadCost(model);
            if (writer != null) {
                writer.println("        wait read " + filter + ": " + cost + " on " + filter.input_buffer.getClass().getSimpleName());
            }
            total_cost += cost;
        }
        //---WAIT WRITES---
        //Channels
        for (AbstractChannel output : filter.outputs.values()) {
            FilterInstance dstFilter = output.dst;
            Processor src = partition.get(filter);
            Processor dst = partition.get(dstFilter);
            int cost = output.getWaitWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        wait write " + dstFilter + ": " + cost + " on " + output.getClass().getSimpleName());
            }
        }
        //FileWriter w merge
        if (filter.FileWriterChannel != null) {
            Processor src = partition.get(filter);
            int cost = filter.FileWriterChannel.getWaitWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        wait write file: " + cost + " on " + filter.FileWriterChannel.getClass().getSimpleName());
            }
            //TODO: Combine with new Buffersystem!!!
            //This handles merges too!!! OptimizeFileWriter costs
            FileWriterMergeBuffer buf = new FileWriterMergeBuffer(null,filter);
            cost = buf.getWaitWriteCost(model);
            if (writer != null) {
                writer.println("        wait write " + filter + ": " + cost + " on " + buf.getClass().getSimpleName() );
            }
                total_cost += cost; 
        }
        //Split prepare
        if (filter.output_buffer!=null ) { //|| (streamit2.Compiler.merge && filter.outputs.size() > 1)
            int cost = 4; //TODO: check cost
            if (writer != null) {
                writer.println("        wait write " + filter + ": " + cost + " on "+ filter.output_buffer.getClass().getSimpleName());
            }
            total_cost += cost;
        }
        return total_cost;
    }

    public int GetDoneCost(PrintStream writer, FilterInstance filter) {
        int total_cost = 0;
        //---DONE READS---
        //Channels
        for (AbstractChannel input : filter.inputs.values()) {
            FilterInstance srcFilter = input.src;
            Processor src = partition.get(srcFilter);
            Processor dst = partition.get(filter);
            int cost = input.getDoneReadCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        done read " + srcFilter + ": " + cost + " on " + input.getClass().getSimpleName());
            }
        }
        //---DONE WRITES---
         // SPLIT (implicit FileReaderSplit handling)
        if (null != filter.output_buffer) {
            Processor src = partition.get(filter);
            int cost = filter.output_buffer.getDoneWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        done write split: " + cost + " on " + filter.output_buffer.getClass().getSimpleName());
            }
        }
        //File Reader
        if (filter.FileReaderChannel != null) {
            Processor src = partition.get(filter);
            int cost = filter.FileReaderChannel.getDoneWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        done write file: " + cost + " on " + filter.FileReaderChannel.getClass().getSimpleName());
            }
        }
        //Split (FileWriter?)
        if ((filter.outputs.size() > 1) && !("FileReader".equals(filter.filter.id))) {
            int cost = filter.output_buffer.getDoneWriteCost(model);
            if (writer != null) {
                writer.println("        done write " + filter + ": " + cost + " on " + filter.output_buffer.getClass().getSimpleName());
            }
            total_cost += cost;
        }
        // Channels
        for (AbstractChannel output : filter.outputs.values()) {
            FilterInstance dstFilter = output.dst;
            Processor src = partition.get(filter);
            Processor dst = partition.get(dstFilter);
            int cost = output.getDoneWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        done write " + dstFilter + ": " + cost + " on " + output.getClass().getSimpleName());
            }
        }
        //File Writer
        if (filter.FileWriterChannel != null) {
            Processor src = partition.get(filter);
            int cost = filter.FileWriterChannel.getDoneWriteCost(model);
            total_cost += cost;
            if (writer != null) {
                writer.println("        done write file: " + cost + " on " + filter.FileWriterChannel.getClass().getSimpleName());
            }
        }
        return total_cost;
    }

    private void updateFilterTime(FilterInstance filter, Map<FilterInstance, Processor> partition) {
        Processor srcProcessor = partition.get(filter);
        //FilterInstance[] dstFilters = filter.parent.out;
        List<FilterInstance[]> dstFilters = filter.split;
        int maxLatency = 0;
        for (FilterInstance[] dstFilterArray : dstFilters) {
            for (FilterInstance dstFilter : dstFilterArray) {
                if (!paired(filter, dstFilter)) {
                    Processor dstProcessor = partition.get(dstFilter);
                    int latency = model.getCommunicationLatency(srcProcessor, dstProcessor);
                    AbstractMap.SimpleEntry<Integer, Integer> costs = model.getCommunciationCost(srcProcessor, dstProcessor);
                    if(filterCom.containsKey(filter)) 
                        filterCom.put(filter, filterCom.get(filter) + costs.getKey());
                    else 
                        filterCom.put(filter, costs.getKey());
                        
                    if(filterCom.containsKey(dstFilter))
                        filterCom.put(dstFilter, filterCom.get(dstFilter) + costs.getValue());
                    else
                        filterCom.put(dstFilter, costs.getKey());
                        
                    if (maxLatency < latency) {
                        maxLatency = latency;
                    }
                }
            }
        }
        if(filterCom.containsKey(filter)) 
            filterCom.put(filter, filterCom.get(filter) + maxLatency);
        else 
            filterCom.put(filter, maxLatency);
    }

    private boolean paired(FilterInstance src, FilterInstance dst) {
        boolean toReturn = false;
        if (destinationFilters.containsKey(src)) {
            List<FilterInstance> filters = destinationFilters.get(src);
            if (filters.contains(dst)) {
                return true;
            } else {
                filters.add(dst);
            }
        } else {
            List<FilterInstance> filters = new ArrayList<>();
            filters.add(dst);
            destinationFilters.put(src, filters);
        }
        return toReturn;
    }
}
