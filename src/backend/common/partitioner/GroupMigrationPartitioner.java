package backend.common.partitioner;

import backend.common.evaluators.Evaluator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.common.estimators.Estimator;
import backend.common.Chip;
import backend.common.Processor;
import hierarchicalGraph.FilterInstance;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GroupMigrationPartitioner {
  private static Processor[] processors;
  private static Evaluator evaluator;
  
 
  private static int gmRound(List<FilterInstance> filters, Map<FilterInstance, Processor> currentMap, int currentValue){
      final int numFilters    = currentMap.size();
      FilterInstance[] f = currentMap.keySet().toArray(new FilterInstance[currentMap.size()]);
      BitSet moved = new BitSet(numFilters);
      int bestMovingFilter;
      Processor      bestProcessorToUse = null;
     
      for (int i=0;i<numFilters;i++) {
          bestMovingFilter=-1;
          for (int t=0;t<numFilters;t++) {
              if (moved.get(t)) continue;
              Processor pOld = currentMap.get(f[t]);
              for (Processor p : processors) {
                  if (p == pOld) continue;
                  currentMap.put(f[t], p);
                  int cost = (int)evaluator.evaluate(currentMap);
                  if (cost>currentValue) continue;
                  bestMovingFilter = t;
                  bestProcessorToUse = p;
                  currentValue = cost;
              }
              currentMap.put(f[t], pOld);
          }
          if (bestMovingFilter<0) {
            return currentValue;
          }
          moved.set(bestMovingFilter);
          currentMap.put(f[bestMovingFilter],bestProcessorToUse);
      }
      
      return currentValue;
  }
    
  public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator estimator){
      evaluator = new ThroughputEvaluator();
      int bestValue;
      Map<FilterInstance,Processor> bestMap = new LinkedHashMap<>();
      processors = chip.getProcessors();
      for (FilterInstance f : filters) {
          bestMap.put(f, chip.getProcessors()[0]);
      }
      bestValue  = (int)evaluator.evaluate(bestMap);
      Map<FilterInstance,Processor> currentMap;
      
      while (true) {
          currentMap = new LinkedHashMap<>(bestMap);
          int currentValue = gmRound(filters, currentMap, bestValue);
          if (currentValue<bestValue) {
              bestValue = currentValue;
              bestMap = currentMap;
          } else {
              break;
          }
      }
      return new TreeMap<>(bestMap);
  }
}
