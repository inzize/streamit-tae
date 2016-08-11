package backend.common.evaluators;

import java.util.Map;

public class EvaluationStats {
    public enum PartitionBottleneck {
        LINK, FILTER;
    }
    
    public PartitionBottleneck bottleneck;
    public double throughput;
    public Map linkRank;
}
