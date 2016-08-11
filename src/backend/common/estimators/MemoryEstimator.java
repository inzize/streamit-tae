package backend.common.estimators;

import backend.common.channels.AbstractChannel;
import backend.common.Processor;
import backend.coreVA.CoreVABackend;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryEstimator {

    private final static Logger logger = Logger.getLogger("compiler");
    Map<Processor, Integer> localMemory;
    Map<FilterInstance, Integer> filterMemory;
    Map<Integer, Integer> sharedMemory;       //TODO: multiple shared memories
    Map<Processor, Integer> sharedMemoryProc; //TODO: multiple shared memories

    public MemoryEstimator(Map<FilterInstance, Processor> partition) {
        localMemory = new LinkedHashMap<>();
        filterMemory = new LinkedHashMap<>();
        sharedMemory = new LinkedHashMap<>();
        sharedMemoryProc = new LinkedHashMap<>();
        
        // statically reserved memory
        for(Processor proc : CoreVABackend.chip.getProcessors()) {
            int mem_local = 0;
            
            // additional data mem (libs, ...), only if filters on this proc (TODO: accurate estimation of this part)
            if(partition.containsValue(proc)) {
                mem_local += 3700;
            }
            
            long nocChannels = CoreVABackend.chip.getMaxChannelRecv((CoreVAProcessor) proc)
                    + CoreVABackend.chip.getMaxChannelSend((CoreVAProcessor) proc);
            long mpsocClusterMaxCpus = CoreVABackend.chip.getMaxDepth();
            long mpsocChannelWidth = CoreVABackend.chip.getClusterChannelWidth((CoreVAProcessor) proc);
            // local memory
            mem_local += mpsocClusterMaxCpus * mpsocChannelWidth * 4;  // cluster
            mem_local += 6 * 4; // stdout sync struct
            mem_local += 16;    // e.g. crt0-2slot.o
            mem_local += 8;     // cpu_?_?_?.o

            // statically reserved shared memory per node (cluster)
            // topology is only known for CoreVA-MPSoC!
            int mem_shared = 0;
            mem_shared += nocChannels * 4; // reserved memory for noc channels
            if ((proc instanceof CoreVAProcessor)
                    // shared mem available in XML config
                    && (CoreVABackend.chip.sharedMemAvail((CoreVAProcessor) proc))
                    // shared mem allowed by compiler parameter
                    && (!streamit2.Compiler.memoryLayout.equals("local"))) {
                // overwrite value for all core in a cluster
                sharedMemory.put(((CoreVAProcessor) proc).getClusterId(), mem_shared);
            } else {
                // no shared mem available -> use local
                mem_local += mem_shared;
            }
            localMemory.put(proc, mem_local);
        }
        
        
        for (FilterInstance filter : partition.keySet()) {
            final Processor targetProc = partition.get(filter);
            int currentMemoryUsed = 0;
            if (localMemory.containsKey(targetProc)) {
                currentMemoryUsed = localMemory.get(targetProc);
            }
            // filter memory
            int filterMem = getMemoryUsage(filter);
            currentMemoryUsed += filterMem;
            filterMemory.put(filter, filterMem);
            localMemory.put(targetProc, currentMemoryUsed);
            // shared memory
            // amount of shared memory required by each processor and node/cluster
            for (AbstractChannel chan : filter.inputs.values()) {
                Processor sourceProc = partition.get(chan.src);
                AbstractMap.SimpleEntry<Integer, Integer> smem = chan.getMemUsedShared();

                // target proc
                Integer mem = sharedMemoryProc.get(targetProc);
                sharedMemoryProc.put(targetProc, mem == null ? smem.getValue() : mem + smem.getValue());
                // source proc (>0 only for NocChannel)
                mem = sharedMemoryProc.get(sourceProc);
                sharedMemoryProc.put(sourceProc, mem == null ? smem.getKey() : mem + smem.getKey());

                // topology is only known for CoreVAProcessor
                if (targetProc instanceof CoreVAProcessor) {
                    CoreVAProcessor cpTarget = (CoreVAProcessor) targetProc;
                    CoreVAProcessor cpSource = (CoreVAProcessor) sourceProc;
                    mem = sharedMemory.get(cpTarget.getClusterId());
                    sharedMemory.put(cpTarget.getClusterId(), mem == null ? smem.getValue() : mem + smem.getValue());
                    mem = sharedMemory.get(cpSource.getClusterId());
                    sharedMemory.put(cpSource.getClusterId(), mem == null ? smem.getKey() : mem + smem.getKey());
                }
            }
        }
        // ranking processors by memory usage.
        for (Processor proc : CoreVABackend.chip.getProcessors()) {
            if (!localMemory.containsKey(proc)) {
                localMemory.put(proc, 0);
            }
        }
    }


    private int getMemoryUsage(FilterInstance filter) {
        int memUsed = 0;
        //filter memory
        memUsed += filter.MemoryUsage();
        
        //channel memory:
        // TODO FIXME! split into filter and channel memory functions
        // TODO: Please change memoryLayout to enum 
        if (streamit2.Compiler.memoryLayout.equals("local")) {
            for (AbstractChannel chan : filter.inputs.values()) {
                memUsed += chan.getMemUsedDst();
            }
            for (AbstractChannel chan : filter.outputs.values()) {
                memUsed += chan.getMemUsedSrc();
            }
        }
        if (filter.FileWriterChannel != null) {
            memUsed += filter.FileWriterChannel.getMemUsedSrc();
        }
        //merger
        if (filter.input_buffer != null) {
            memUsed += filter.input_buffer.getMemUsedDst();
        }
        if (filter.output_buffer != null) {
            memUsed += filter.output_buffer.getMemUsedSrc();
        }
        return memUsed;
    }


    public Map<FilterInstance, Integer> getFilterMemoryListByProc(Map<FilterInstance, Processor> partition, Processor proc) {
        Map<FilterInstance, Integer> filterMem = new LinkedHashMap<>();
        for (FilterInstance filter : partition.keySet()) {
            if (partition.get(filter) == proc) {
                filterMem.put(filter, getMemoryUsage(filter));
            }
        }
        return filterMem;
    }

    public Map<FilterInstance, Integer> getFilterMemoryList() {
        return filterMemory;
    }

    public boolean validatePartition() {
        final double memUsage = streamit2.Compiler.maxMem;
        for (Processor p : localMemory.keySet()) {
            final int memUsed = localMemory.get(p);
            if (memUsed > ((CoreVAProcessor) p).memSize * memUsage) {
                logger.log(Level.FINE, "Memory limit exceeded on {0}: {1} > {2}", new Object[]{((CoreVAProcessor) p).toString(), memUsed, ((CoreVAProcessor) p).memSize * memUsage});
                return false;
            }
        }
        // shared memory
        for (int clusterId : sharedMemory.keySet()) {
            if (CoreVABackend.chip.sharedMemCluster[clusterId].length <= 0) {
                continue; // no shared mem in this cluster/node
            } else {
                int memUsed = sharedMemory.get(clusterId);
                int memAvail = CoreVABackend.chip.sharedMemCluster[clusterId][0]; // TODO: multiple shared memories per cluster
                if (memUsed > memAvail * memUsage) {
                    logger.log(Level.FINE, "Shared memory limit exceeded on cluster {0}: {1} > {2} ", new Object[]{clusterId, memUsed, memAvail});
                    return false;
                }
            }
        }
        return true;
    }
    
    public Map<Integer, Integer> getSharedMemoryUsage() {
        return sharedMemory;
    }
    
    public int getEstimationShared(Processor p) {
        if (!sharedMemoryProc.containsKey(p)) {
            return 0;
        }
        return sharedMemoryProc.get(p);
    }

    public Map<Processor, Integer> getMemoryList() {
        return localMemory;
    }

    public int getEstimationLocal(Processor p) {
        if (!localMemory.containsKey(p)) {
            return 0;
        }
        return localMemory.get(p);
    }
}
