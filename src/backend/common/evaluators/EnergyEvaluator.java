package backend.common.evaluators;

import backend.common.Chip;
import backend.common.Processor;
import hierarchicalGraph.FilterInstance;
import java.util.Map;
import streamit2.Compiler;

public class EnergyEvaluator extends Evaluator{
    public enum EvaluateStructure {CPU, CHANNEL, MEMORY};
    public enum CycleEvaluationStrategy {NO_CYCLES, ON_OFF, LINEAR, LINEAR_PER_TYPE};
    
    private final Chip chip;
    
    public EnergyEvaluator() {
        this.chip = Compiler.chip;
    }
    
    
    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        long staticCPUConsumption = 0;
        for (Processor p : chip.getProcessors())
            staticCPUConsumption += p.getStaticEnergyConsumption();
        return staticCPUConsumption;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<FilterInstance, Integer> getFiltersRank() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
