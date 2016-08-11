package backend.common.partitioner;

import backend.common.Chip;
import backend.common.Partition;
import backend.common.estimators.Estimator;
import hierarchicalGraph.FilterInstance;
import java.util.List;

public interface Partitioner {
    public Partition createPartition(List<FilterInstance> filters, Chip chip, Estimator estimator, boolean oneClusterOnly);
}
