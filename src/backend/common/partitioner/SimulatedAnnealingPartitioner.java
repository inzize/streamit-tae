package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.*;
import hierarchicalGraph.FilterInstance;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulatedAnnealingPartitioner
{  
    private final static Logger logger = Logger.getLogger("compiler");
    
    private interface Strategy {
        public boolean nextRound(boolean foundBetter);
        public int getNumTries();
    }
    
    private static class NumTriesStrategy implements Strategy {
        private       int round=0;
        private final int tries=100000;
        @Override
        public boolean nextRound(boolean foundBetter) {
            return (round++<tries);
        }

        @Override
        public int getNumTries() {
            return round;
        }
    }
    
    private static class NumTimeStrategy implements Strategy {
        private int round     = 0;
        private long startTime = 0;
        private final int maxTime = 30; //[s] 

        @Override
        public boolean nextRound(boolean foundBetter) {
            if (startTime==0)
                startTime = System.currentTimeMillis();
            round++;
            return (System.currentTimeMillis()-startTime)/1000<maxTime;
        }

        @Override
        public int getNumTries() {
          return round;
        }
    }
    
    
    public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Evaluator evaluator)
    {
        int notAccepted = 0;
        int accepted    = 0;
                
        Map<FilterInstance, Processor> bestPartition = null;
        long bestEvaluate=Long.MAX_VALUE;
        
        Strategy s = new NumTimeStrategy();
        
        while (s.nextRound(false)) {
            Map<FilterInstance, Processor> partitioning = RandomPartitioner.partition(filters, chip, evaluator);
            long rating = (long)evaluator.evaluate(partitioning);
            if (bestEvaluate>rating) {
                // add partition validation
                bestEvaluate = rating;
                bestPartition = partitioning;
                accepted++;
            }
        }
        
        logger.log(Level.INFO, "Best partition: "+bestEvaluate+" [cycles] ("+s.getNumTries()+" tries, "+accepted+" better & accepted, "+notAccepted+" better & not accepted)");
        return new TreeMap(bestPartition);
    }
    
    
}
