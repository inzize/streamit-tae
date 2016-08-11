package streamit2;

import backend.common.Processor;
import hierarchicalGraph.FilterInstance;
import java.util.SortedMap;

public class NoEstimationDataException extends RuntimeException {
    public SortedMap<FilterInstance, Processor> unknownPartition;
    public NoEstimationDataException(String txt) {
        super(txt);
        streamit2.Compiler.analyseLatency = false; //Speedup!
        streamit2.Compiler.abortOnUnknownEstimation=false; //Compiling should not abort
    }
    
}
