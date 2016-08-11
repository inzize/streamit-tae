package backend.common;

import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import java.util.Collections;
import java.util.SortedMap;

public class Partition {
    private final SortedMap<FilterInstance, Processor> mapping;
    private Evaluator memoryEvaluator = null;
    
    public Partition(SortedMap<FilterInstance, Processor> mapping) {
        this.mapping = Collections.unmodifiableSortedMap(mapping);
    }
    
    public SortedMap<FilterInstance,Processor> getFilterMapping() {
        return mapping;
    }
    
    public Evaluator getInvalidEvaluator() {
        if (null==memoryEvaluator)
            memoryEvaluator = Evaluator.getEvaluator(Evaluator.EvaluatorType.MEMORY);
        return  !memoryEvaluator.isValid()?memoryEvaluator:null;
    }
    
    /*** Statistics ***/
    
}
