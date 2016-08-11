package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.Chip;
import backend.common.Processor;
import hierarchicalGraph.FilterInstance;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class RandomPartitioner {
    private static final Random rand = new Random(42);
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Evaluator evaluator) {
        SortedMap<FilterInstance, Processor> partitioning = new TreeMap<>();
        Processor[] processors = chip.getProcessors();
        
        do{
            partitioning.clear();
            for (FilterInstance filter : filters)
                partitioning.put(filter, processors[rand.nextInt(processors.length)]);
        }while(evaluator.evaluate(partitioning) == 0);
        
        return partitioning;
    }
}
