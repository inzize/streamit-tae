package backend.common.partitioner;

import backend.common.Chip;
import backend.common.Processor;
import hierarchicalGraph.FilterInstance;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/* doesn't work for 1x1x1 */ 
public class PermPartitioner {
  private final static Logger logger = Logger.getLogger("compiler");
  
  public static long getMaxPermutations(List<FilterInstance> filters, Chip chip) {
      final int numProc = chip.getProcessors().length;
      String n = "";
      for (int i=0;i<filters.size();i++){
          n+=Integer.toString(numProc-1, numProc);
      }
      try{
        Long.valueOf(n, numProc);
      } catch (NumberFormatException e) {
          logger.log(Level.WARNING, "(There are too many permutations with "+filters.size()+" filters)");
          return Long.MAX_VALUE;
      }
      return Long.valueOf(n, numProc)+1;
  }
  
  public static SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, int permID){
      final int numProc = chip.getProcessors().length;
      if (numProc==1) {
          logger.log(Level.INFO, "PermPartitioner won't do anything for 1x1x1");
          SortedMap<FilterInstance,Processor> map = new TreeMap<>();
          for (FilterInstance filter : filters)
              map.put(filter, chip.getProcessors()[0]);
          return map;
      }
      final long maxPerm = getMaxPermutations(filters, chip);
      if (permID>=maxPerm) {
          logger.log(Level.SEVERE, "Your permutation is out of range max is "+maxPerm);
          System.exit(-1);
      }
      SortedMap<FilterInstance,Processor> map = new TreeMap<>();
      String perm = Integer.toString(permID, numProc);
      while(perm.length()<filters.size())
          perm="0"+perm;
      
      for(int i=0;i<perm.length();i++) { //put filters
          int dst = Integer.valueOf(perm.charAt(i)+"", numProc);
          map.put(filters.get(i), chip.getProcessors()[dst]);
      }
      return map;
    }
}
