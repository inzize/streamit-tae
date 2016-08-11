package backend.common.estimators;

import backend.common.channels.AbstractChannel;
import backend.common.Processor;
import static backend.coreVA.CoreVABackend.chip;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ChannelEstimator {
    private Map<Processor, Integer> processors;
    private final Map<FilterInstance, Processor> partition;

    public ChannelEstimator(Map<FilterInstance, Processor> partition) {
        this.partition = new LinkedHashMap<>(partition);
    }
    
    public boolean isValid() {
        for (FilterInstance filter : partition.keySet()) {
            for (AbstractChannel chan : filter.inputs.values()) {
                // ask model if all channel requirements are met 
               if (!chip.isValidChannel(partition.get(chan.src),partition.get(chan.dst), chan)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Map<FilterInstance, Integer> getFilterChannelListByProc(Processor proc, boolean recvLimit){
        Map<FilterInstance, Integer> filterChannel = new LinkedHashMap<>();
        for (FilterInstance filter : partition.keySet()) {
            if (partition.get(filter) == proc || proc == null) {
                Object[] channels;
                if(recvLimit)
                    channels = filter.inputs.values().toArray();
                else
                    channels = filter.outputs.values().toArray();
                int nbChannels = 0;
                for(Object ob : channels){
                    AbstractChannel ch = (AbstractChannel)ob;
                    nbChannels += ch.numberOfBuffers;
                }
                filterChannel.put(filter, nbChannels);
            }
        }
        return filterChannel;
    }
        
    public Map<Processor, Integer> getProcessorsNbRecvChannel(){
        processors = new LinkedHashMap<>();
        for(Processor p: chip.getProcessors()){
            Map<FilterInstance, Integer> filterList = getFilterChannelListByProc(p, true);
            int totalChannels = 0;
            for(Integer n: filterList.values())
                totalChannels += n;
            processors.put(p, totalChannels);
        }
        return processors;
    }
    
    public Map<Processor, Integer> getProcessorsNbSendChannel(){
        processors = new LinkedHashMap<>();
        for(Processor p: chip.getProcessors()){
            Map<FilterInstance, Integer> filterList = getFilterChannelListByProc(p, false);
            int totalChannels = 0;
            for(Integer n: filterList.values())
                totalChannels += n;
            processors.put(p, totalChannels);
        }
        return processors;
    }
}
