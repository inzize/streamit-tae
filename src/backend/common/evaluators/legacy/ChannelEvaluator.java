package backend.common.evaluators.legacy;

import backend.common.Processor;
import backend.common.estimators.ChannelEstimator;
import backend.common.evaluators.Evaluator;
import hierarchicalGraph.FilterInstance;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelEvaluator extends Evaluator {
    private final static Logger logger = Logger.getLogger("compiler");    
    private ChannelEstimator channelEstimator;
    private boolean recvLimit = false;
    
    @Override
    public double evaluate(Map<FilterInstance, Processor> partition) {
        currentPartition = new LinkedHashMap<>(partition);
        channelEstimator = new ChannelEstimator(partition);
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
        if (channelEstimator == null) {
            logger.log(Level.SEVERE, "Could not find a valid estimator!");
            System.exit(-1);
        }
        return rankFilter(channelEstimator.getFilterChannelListByProc(null, recvLimit));
    }

    @Override
    public Map<FilterInstance, Integer> filtersOnProcessorRank(Processor proc) {
        if (channelEstimator == null) {
            logger.log(Level.SEVERE, "Could not find a valid estimator!");
            System.exit(-1);
        }
        return rankFilter(channelEstimator.getFilterChannelListByProc(proc, recvLimit));
    }

    @Override
    public Map<Processor, Integer> getProcessorsRank() {
        if (channelEstimator == null) {
            logger.log(Level.SEVERE, "Could not find a valid estimator!");
            System.exit(-1);
        }
        Map<Processor, Integer> recvRank = rankProcessor(channelEstimator.getProcessorsNbRecvChannel());
        Map<Processor, Integer> sendRank = rankProcessor(channelEstimator.getProcessorsNbSendChannel());
        int maxRecv = recvRank.get(recvRank.keySet().toArray()[recvRank.size() - 1]);
        int maxSend = sendRank.get(sendRank.keySet().toArray()[sendRank.size() - 1]);
        recvLimit = maxRecv > maxSend;
        if(recvLimit)
            return recvRank;
        else 
            return sendRank;
    }

    @Override
    public boolean isValid() {
        return channelEstimator.isValid();
    }
}
