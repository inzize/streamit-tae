package backend.common.partitioner.mutator;

import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Mutator {
    private final static Logger logger = Logger.getLogger("compiler");
    
    public static final Random rand = new Random(42);
    public abstract boolean mutate();
    public abstract boolean finalizeChange();
    
    private static Map<Processor, Integer> generateProcessorDistribution(Map<Processor, Integer> rank){
        Map<Processor, Integer> toReturn = new LinkedHashMap<>();
        int totalWork = getTotalProcessorWork(rank);
        Map<Processor, Double> doublerank = new LinkedHashMap<>();
        for(Processor p: rank.keySet()){
            double ratio = (double)rank.get(p) / totalWork;
            double newvalue = Math.pow(ratio, 20);
            doublerank.put(p, newvalue);
        }
        double totaldoublerank = getTotalProcessorWorkDouble(doublerank);
        double factor = 100/totaldoublerank;
        for(Processor p: doublerank.keySet())
            toReturn.put(p, (int)(doublerank.get(p) * factor));
        
        return toReturn;
    }
    
    protected static Processor getLargeProcessor(Evaluator evaluator, String type){
        Processor proc = null;
        Map<Processor, Integer> rank = generateProcessorDistribution(evaluator.getProcessorsRank());
        int totalWork = getTotalProcessorWork(rank);
        int index = rand.nextInt(totalWork);
        int currantWork = 0;
        for(Processor p: rank.keySet()){
            currantWork += rank.get(p);
            if(currantWork >= index){
                proc = p;
                break;
            }
        }
        if(proc == null){
            logger.log(Level.SEVERE, "Could not find a large processor!");
            System.exit(-1);
        }
        return proc;
    }
    
    protected static Processor getLargestProcessor(Evaluator evaluator){
        Map<Processor, Integer> rank = evaluator.getProcessorsRank();
        if(rank != null)
            return (Processor)rank.keySet().toArray()[rank.size() - 1];
        else
            return null;
    }
    
    public static double getMaxMemUsageRate(Evaluator evaluator){
        Map<Processor, Integer> rank = evaluator.getProcessorsRank();
        Processor proc = (Processor) rank.keySet().toArray()[rank.keySet().size()-1];
        int memUsage = rank.get(proc);
        return memUsage/(((CoreVAProcessor)proc).memSize * 0.25);
    }
        
    protected static Processor getSmallProcessor(Evaluator evaluator, String type){
        Processor proc = null;
        Map<Processor, Integer> rank = generateProcessorDistribution(evaluator.getProcessorsRank());
        List<Processor> procs = new ArrayList<>(rank.keySet());
        if(rank.get(procs.get(0)) == 0)
            return procs.get(0);
        int totalWork = getTotalProcessorWork(rank);
        int index = rand.nextInt(totalWork);
        int currantWork = 0;
        for(Processor p: rank.keySet()){
            currantWork += rank.get(p);
            if(currantWork >= index){
                proc = p;
                break;
            }
        }
        if(proc == null){
            logger.log(Level.SEVERE, "Could not find an small processor!");
            System.exit(-1);
        }
        int i = procs.indexOf(proc);
        Collections.reverse(procs);
        proc = procs.get(i);
        return proc;
    }
    
    protected static int getTotalProcessorWork(Map<Processor, Integer> rank){
        int totalWork = 0;
        for(Processor p: rank.keySet())
            totalWork += rank.get(p);
        return totalWork;
    }
    
    protected static double getTotalProcessorWorkDouble(Map<Processor, Double> rank){
        double totalWork = 0;
        for(Processor p: rank.keySet())
            totalWork += rank.get(p);
        return totalWork;
    }
    
    protected static FilterInstance getLargeFilter(Evaluator evaluator, Processor proc, boolean fissableOnly){
        FilterInstance filterinstance = null;
        Map<FilterInstance, Integer> rank = evaluator.filtersOnProcessorRank(proc);
        Map<FilterInstance, Integer> new_rank = new LinkedHashMap<>();
        if(fissableOnly){
            for(FilterInstance f: rank.keySet())
                if(f.isFissable())
                    new_rank.put(f, rank.get(f));
            if(new_rank.isEmpty())
                return null;
        }else{
            new_rank = rank;
        }
        int totalWork = getTotalFilterWork(new_rank);
        int index = rand.nextInt(totalWork);
        int currantWork = 0;
        for(FilterInstance f: new_rank.keySet()){
            currantWork += new_rank.get(f);
            if(currantWork >= index){
                filterinstance = f;
                break;
            }
        }
        if(filterinstance == null){
            logger.log(Level.SEVERE, "Could not find a large filter!");
            System.exit(-1);
        }
        if(filterinstance.isFissedNode)
            return (FilterInstance)filterinstance.previousNode;
        else
            return filterinstance;
    }
    
    protected FilterInstance getSmallFilter(Evaluator evaluator, Processor proc){
        FilterInstance filterinstance = null;
        Map<FilterInstance, Integer> rank = evaluator.filtersOnProcessorRank(proc);
        if(rank.isEmpty())
            return null;
        int totalWork = getTotalFilterWork(rank);
        //Rand needs values >=1 so it gets 1 if totalWork is 0
        int index = rand.nextInt(totalWork==0?1:totalWork);
        int currantWork = 0;
        for(FilterInstance f: rank.keySet()){
            currantWork += rank.get(f);
            if(currantWork >= index){
                filterinstance = f;
                break;
            }
        }
        if(filterinstance == null){
            logger.log(Level.SEVERE, "Could not find a small filter!");
            System.exit(-1);
        }
        List<FilterInstance> filterinstances = new ArrayList<>(rank.keySet());
        int i = filterinstances.indexOf(filterinstance);
        Collections.reverse(filterinstances);
        filterinstance = filterinstances.get(i);
        return filterinstance;
    }
    
    protected FilterInstance getSmallestFilter(Evaluator evaluator, Processor proc){
        Map<FilterInstance, Integer> rank = evaluator.filtersOnProcessorRank(proc);
        if(rank != null)
            return (FilterInstance)rank.keySet().toArray()[0];
        else
            return null;
    }
    
    protected static int getTotalFilterWork(Map<FilterInstance, Integer> rank){
        int totalWork = 0;
        for(FilterInstance f: rank.keySet())
            totalWork += rank.get(f);
        return totalWork;
    }
}
