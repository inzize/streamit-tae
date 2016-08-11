package backend.common.evaluators.legacy;

import backend.common.Processor;
import backend.common.estimators.CommunicationEstimator;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommunicationEvaluator extends Evaluator {
    private CommunicationEstimator comEstimator;

    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        currentPartition = new LinkedHashMap<>(partition);
        comEstimator = new CommunicationEstimator(partition);
        return Double.NaN;
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
    public double getValue() {
        return Double.NaN;
    }

    @Override
    public Map<FilterInstance, Integer> getFiltersRank() {
        return rankFilter(comEstimator.getFilterCom());
    }

    @Override
    public Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Processor, Integer> getProcessorsRank() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid() {
        return comEstimator.isValid();
    }
    
}
