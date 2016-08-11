package backend.common.evaluators;

import backend.common.Processor;
import backend.common.estimators.MemoryEstimator;
import hierarchicalGraph.FilterInstance;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyse the memory usage of a given partition
 * 
 * @see Evaluator
 */
public class MemoryEvaluator extends Evaluator {
    private final static Logger logger = Logger.getLogger("compiler");    
    private MemoryEstimator memEstimator;

    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        currentPartition = new LinkedHashMap<>(partition);
        memEstimator = new MemoryEstimator(partition);
        return Double.NaN;
    }
    
    @Override
    public Map<Processor, Integer> getProcessorsRank() {
        if (memEstimator == null) {
            logger.log(Level.SEVERE, "Could not find a valid estimator!");
            System.exit(-1);
        }
        return rankProcessor(memEstimator.getMemoryList());
    }

    @Override
    public Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc) {
        if (memEstimator == null) {
            logger.log(Level.SEVERE, "Could not find a valid estimator!");
            System.exit(-1);
        }
        return rankFilter(memEstimator.getFilterMemoryListByProc(currentPartition, proc));
    }
    
    @Override
    public Map<FilterInstance, Integer> getBottleneckFilters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<FilterInstance, Integer> getIdleFilters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Processor getIdleProcessor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getIdleProcessorWork() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Processor getBusyProcessor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getBusyProcessorWork() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<FilterInstance, Integer> getFiltersRank() {
        return rankFilter(memEstimator.getFilterMemoryList());
    }

    @Override
    public boolean isValid() {
        return memEstimator.validatePartition();
    }
    
    @Override
    public double getValue() {
        return Double.NaN;
    }

}
