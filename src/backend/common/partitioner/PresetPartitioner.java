package backend.common.partitioner;

import backend.common.Chip;
import backend.common.PartitionLoader;
import backend.common.Processor;
import backend.common.partitioner.mutator.ManualMutation;
import backend.common.partitioner.mutator.Mutation;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PresetPartitioner {
    private final static Logger logger = Logger.getLogger("compiler");    
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, String parFile, Chip chip){
        File file = new File(parFile);
        if(!PartitionLoader.retrivePartition(file.getAbsolutePath())){
            logger.log(Level.SEVERE, "Error: Could not load this partition file.");
            System.exit(-1);
        }
        
        if(!PartitionLoader.checkParFile(chip)){
            logger.log(Level.SEVERE, "Error: This partition file is not for this source file!");
            System.exit(-1);
        }
        
        Map<StreamNode, Integer[]> fissionRatio = PartitionLoader.getFissionRatio();
        Map<String, Integer> partitionPlan = PartitionLoader.getPartitionPlan();
        SortedMap<StreamNode, Processor> partition = new TreeMap<>();
        int mult = PartitionLoader.getMult();
        
        new ManualMutation(filters, partition, chip, fissionRatio, partitionPlan, mult);

        return Mutation.flattenPartition(partition);
    }
    

}
