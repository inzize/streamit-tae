package backend.common.partitioner;

import backend.common.estimators.Estimator;
import backend.common.Chip;
import backend.common.Partition;
import backend.common.Processor;
import backend.common.buffers.FileReaderSplitBuffer;
import backend.common.estimators.PerfFileEstimator;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreedyPartitioner implements Partitioner {

    private final static Logger logger = Logger.getLogger("GreedyPartitioner");

    private static class TimeComparator<T> implements Comparator<Entry<Integer, T>> {

        boolean up;

        public TimeComparator(boolean up) {
            this.up = up;
        }

        @Override
        public int compare(Entry<Integer, T> o1, Entry<Integer, T> o2) {
            if (up) {
                if (o1.getKey() > o2.getKey()) {
                    return 1;
                } else if (o1.getKey() < o2.getKey()) {
                    return -1;
                } else if (((Processor) (o1.getValue())).getGID() > ((Processor) (o2.getValue())).getGID()) {
                    return 1;
                } else {
                    return -1;
                }
                //return o1.getKey().compareTo(o2.getKey());
            } else if (o1.getKey() > o2.getKey()) {
                return -1;
            } else if (o1.getKey() < o2.getKey()) {
                return 1;
            } else if (((FilterInstance) (o1.getValue())).filter_id > ((FilterInstance) (o2.getValue())).filter_id) {
                return 1;
            } else {
                return -1;
            }
            //return o2.getKey().compareTo(o1.getKey());
        }
    }

    //TODO: Review this code -> it should be moved into one of our estimators...
    public static int getFilterCost(FilterInstance filter, Chip chip, Estimator estimator) {
        int work = estimator.estimateFilter(filter);
        if ("FileWriter".equals(filter.filter.id)) {
            work += filter.getMergeCost();
        } else {
            if (filter.join.size() > 1) {
                filter.input_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(null, filter);
                work += filter.input_buffer.getWaitReadCost(chip);
            }
            int split = filter.split.size();
            if (split == 1) {
                split = filter.split.get(0).length;
            }
            if ("FileReader".equals(filter.filter.id)) {
                filter.output_buffer = new FileReaderSplitBuffer(filter);
            } else if (split > 1) {
                filter.output_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(filter, null);
                work += filter.output_buffer.getDoneWriteCost(chip);
            }
        }
        return work;
    }

    @Override
    public Partition createPartition(List<FilterInstance> filters, Chip chip, Estimator estimator, boolean oneClusterOnly) {
        final Processor[] processors = oneClusterOnly ? chip.getFirstClusterProcessors() : chip.getProcessors(); //new Processor[]{chip.getProcessors()[0]};//
        final int numProc = processors.length;
        SortedMap<FilterInstance, Processor> filterMapping = new TreeMap<>();
        PriorityQueue<Entry<Integer, FilterInstance>> times = new PriorityQueue(filters.size(), new TimeComparator<>(false));
        PriorityQueue<Entry<Integer, Processor>> procs = new PriorityQueue(numProc, new TimeComparator<>(true));
        PriorityQueue<Entry<Integer, Processor>> procsStatefulOnly = new PriorityQueue(numProc, new TimeComparator<>(true));
        //Prepare/Fill all for filter mapping
        for (Processor p : processors) {
            procs.add(new SimpleEntry<>(0, p));
            procsStatefulOnly.add(new SimpleEntry<>(0, p));
        }
        for (FilterInstance filter : filters) {
            if (filter.push > 0 && filter.split.isEmpty()) { // map FileWriter to core 0x0x0
                filterMapping.put(filter, (chip.getProcessors())[0]);
                Entry<Integer, Processor> firstProc = procs.poll();
                procs.add(new SimpleEntry<>(firstProc.getKey() + getFilterCost(filter, chip, estimator), firstProc.getValue()));
            } else if (filter.pop==0) { // map FileReader to 0x0x<last>
                Processor lastProc = chip.getFirstClusterProcessors()[chip.getFirstClusterProcessors().length-1];
                filterMapping.put(filter, lastProc);
                // update proc entry in PriorityQueue
                int work = 0;
                for (Object obj : procs.toArray()) {
                    SimpleEntry<Integer, Processor> se = (SimpleEntry<Integer, Processor>) obj;
                    if(se.getValue().equals(lastProc)) {
                        work = se.getKey();
                        procs.remove(obj);
                    }
                }
                Entry<Integer, Processor> firstProc = new SimpleEntry<>(work, lastProc);
                procs.add(new SimpleEntry<>(firstProc.getKey() + getFilterCost(filter, chip, estimator), firstProc.getValue()));
            } 
            else { //normal filter
                times.add(new SimpleEntry<>(getFilterCost(filter, chip, estimator), filter));
            }
        }

        // measure greedy partitioning: start
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long startTime = bean.getCurrentThreadCpuTime();

        HashMap<Processor, Integer> numFiltersOnProc = new HashMap<>(); //For stats only
        //Do the filter mapping
        while (!times.isEmpty()) {
            Entry<Integer, FilterInstance> highestWorkFilter = times.poll();
            Entry<Integer, Processor> lowestWorkProcessor = procs.poll();
            filterMapping.put(highestWorkFilter.getValue(), lowestWorkProcessor.getValue());
            lowestWorkProcessor = new SimpleEntry<>(lowestWorkProcessor.getKey() + highestWorkFilter.getKey(), lowestWorkProcessor.getValue());
            procs.add(lowestWorkProcessor);
            //stats:
            if (!highestWorkFilter.getValue().isFissable() && (estimator instanceof PerfFileEstimator)) {
                lowestWorkProcessor = procsStatefulOnly.poll();
                //wtf?::filterMapping.put(highestWorkFilter.getValue(), lowestWorkProcessor.getValue());
                lowestWorkProcessor = new SimpleEntry<>(lowestWorkProcessor.getKey() + ((PerfFileEstimator) estimator).getMinFilterCyclesNormalized(highestWorkFilter.getValue()), lowestWorkProcessor.getValue());
                procsStatefulOnly.add(lowestWorkProcessor);
                if (!numFiltersOnProc.containsKey(lowestWorkProcessor.getValue())) {
                    numFiltersOnProc.put(lowestWorkProcessor.getValue(), 1);
                } else {
                    numFiltersOnProc.put(lowestWorkProcessor.getValue(), (int) numFiltersOnProc.get(lowestWorkProcessor.getValue()) + 1);
                }
            }
        }
        Partition partition = new Partition(filterMapping);
        //Print stats (works only for PerfFileEstimator!)
        if (!(estimator instanceof PerfFileEstimator)) {
            return partition;
        }
        FilterInstance highestWorkStatefulFilter = null;
        int statelessFilterCycles = 0;
        for (FilterInstance filter : filters) {
            if (filter.isFissable()) {
                statelessFilterCycles += ((PerfFileEstimator) estimator).getMinFilterCyclesNormalized(filter);
            }
        }
        int singleCoreCycles = statelessFilterCycles;
        int highestWorkStatefulProc = 0;
        for (Entry<Integer, Processor> proc : procsStatefulOnly) {
            singleCoreCycles += (int) proc.getKey();
            if (highestWorkStatefulProc < proc.getKey()) {
                highestWorkStatefulProc = (int) proc.getKey();
            }
        }

        // measure greedy partitioning: stop
        long stopTime = bean.getCurrentThreadCpuTime();
        logger.log(Level.INFO, "greedy_time: {0,number,#} [ns]", (stopTime - startTime));

        logger.log(Level.INFO, "highest proc stateful: {0}", highestWorkStatefulProc);
        int freeCpuCycles = 0;
        for (Entry<Integer, Processor> data : procsStatefulOnly) {
            logger.log(Level.INFO, "Stateful {0}: {1} cycles({2})", new Object[]{data.getValue(), data.getKey(), numFiltersOnProc.get(data.getValue())});
            freeCpuCycles += highestWorkStatefulProc - (int) data.getKey();
        }
        logger.log(Level.INFO, "Stateful free total: {0} cycles", freeCpuCycles);
        logger.log(Level.INFO, "Stateless filter total: {0} cycles", statelessFilterCycles);
        int statelessCyclesAfterMap = statelessFilterCycles - freeCpuCycles;
        if (statelessCyclesAfterMap < 0) {
            statelessCyclesAfterMap = 0;
        }
        double theoCycles = highestWorkStatefulProc + (1.0 * statelessCyclesAfterMap / numProc);
        List<FilterInstance> filtersInOrder = StreamNode.globalGraph.InOrder();

        logger.log(Level.INFO, "Mult was: {0}", StreamNode.getRootMult());
        // get netPop rate of last filter for throughput estimation
        int lastFilterPop = (int) (filtersInOrder.get(filtersInOrder.size() - 1).pop * filtersInOrder.get(filtersInOrder.size() - 1).netSteady());
        logger.log(Level.INFO, "statelessCyclesAfterMap: {0}", statelessCyclesAfterMap);
        logger.log(Level.INFO, "single core cycles: {0}", singleCoreCycles);
        logger.log(Level.INFO, "theoCycles: {0}", theoCycles);
        logger.log(Level.INFO, "lastFilterPop: {0}", lastFilterPop);
        logger.log(Level.INFO, "outputItemsPerTheoMCycles: {0}", lastFilterPop * 1000000.0 / theoCycles);
        logger.log(Level.INFO, "theo. speedup: {0}", 1.0 * singleCoreCycles / theoCycles);
        return partition; 
    }
}
