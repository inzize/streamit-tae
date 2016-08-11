package backend.common;

import hierarchicalGraph.FilterInstance;
import java.util.*;

public class Partitioner
{
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip)
    {
        SortedMap<FilterInstance, Processor> partitioning = new TreeMap<>();
        Processor[] processors = chip.getProcessors();
        int i = 0;
        for (FilterInstance filter : filters)
            partitioning.put(filter, processors[i++ % processors.length]);

        return partitioning;
    }
}
