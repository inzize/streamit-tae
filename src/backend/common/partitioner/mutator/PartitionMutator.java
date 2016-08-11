package backend.common.partitioner.mutator;

import backend.common.Chip;
import backend.common.Processor;
import backend.common.evaluators.Evaluator;
import static backend.common.partitioner.mutator.Mutator.rand;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PartitionMutator extends Mutator{
    private final static Logger logger = Logger.getLogger("compiler");
    protected StreamNode filter = null;
    protected Processor target_processor = null;
    protected Map<StreamNode, Processor> partition;
    protected Chip chip;
    
    public PartitionMutator(Map<StreamNode, Processor> partition, Chip chip){
        this.partition = partition;
        this.chip = chip;
    }

    @Override
    public boolean finalizeChange() {
        if(target_processor == null)
            return false;
        if(filter.StreamNodeName().startsWith("FileWriter"))
            return false;
        if (filter.StreamNodeName().startsWith("FileReader"))
            return false;
        partition.put(filter, target_processor);
        return true;
    }
    
    private Map<Processor, Integer> removeUnsuitableProcessor(Map<Processor, Integer> rank, StreamNode f, Evaluator evaluator){
        int filterWork = evaluator.getFiltersRank().get((FilterInstance)f);
        Processor currentProcessor = partition.get(f);
        int currentProcessorWork = rank.get(currentProcessor);
        int maxSuitableWork = currentProcessorWork - filterWork;
        Map<Processor, Integer> suitableProcessors = new LinkedHashMap<>();
        for(Processor p: rank.keySet()){
            int work = rank.get(p);
            if(work <= maxSuitableWork)
                suitableProcessors.put(p, work);
        }
        return suitableProcessors;
    }
    
    //get a suitable target processor, that would not be a new bottleneck.
    protected Processor getSuitableProcessor(Evaluator evaluator, StreamNode f){
        Processor proc = null;
        Map<Processor, Integer> rank = removeUnsuitableProcessor(evaluator.getProcessorsRank(), f, evaluator);
        if(rank.isEmpty())
            return null;
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
            logger.log(Level.SEVERE, "Could not find an idle processor!");
            System.exit(-1);
        }
        int i = procs.indexOf(proc);
        Collections.reverse(procs);
        proc = procs.get(i);
        return proc;
    }
    
    protected Processor getEnoughProcessor(Evaluator evaluator, StreamNode f){
        Map<Processor, Integer> rank = removeUnsuitableProcessor(evaluator.getProcessorsRank(), f, evaluator);
        if(rank.isEmpty())
            return null;
        return (Processor)rank.keySet().toArray()[0];
    }
}
