package backend.common.estimators;

import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.SplitTreeStreamNode;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PerfFileEstimator extends Estimator {
    private final static Logger logger = Logger.getLogger("PerfFileEstimator");
    // the key of a filter time is filter_id
    private final LinkedHashMap<Integer,int[]> filterTimes;
    
    public PerfFileEstimator(String strFile, List<FilterInstance> filters){               
	filterTimes = new LinkedHashMap<>();
        final String configDir = streamit2.Main.getConfigDir();
        final String perfFile = configDir+"perf/"+strFile.substring(strFile.lastIndexOf(File.separator),strFile.length()-4)+".perf";
        
        try {
          readFile(perfFile, filters);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Sry file {0} doesn''t exist!", perfFile);
            System.exit(-1);
        }      
    }
    
    private int[] parseInputLine(String inputLine) {
        String[] parts = inputLine.split(",");
        int[] result = new int[parts.length];
        for (int i=0;i<parts.length;i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }
    
    private void readFile(String perfFile, List<FilterInstance> filters) throws IOException {
        Properties filterTimesFile = new Properties();   
        filterTimesFile.load(new FileInputStream(perfFile));
        for (String filterKey : filterTimesFile.stringPropertyNames()) {
            int filterId = Integer.parseInt(filterKey);
            filterTimes.put(filterId, parseInputLine(filterTimesFile.getProperty(filterKey)));
        }
        // assume filterwriter's work is always zero (only merging overhead, which will be estimated).
        if(filters.get(filters.size()-1).filter.id.startsWith("FileWriter")){
            filterTimes.put(filters.get(filters.size()-1).filter_id, new int[]{0});
        }
    }
     
    public int getMinFilterCyclesNormalized(FilterInstance filter) {
        FilterInstance original;
        if (filter.isFissedNode && filter.previousNode instanceof FilterInstance) {
            original = (FilterInstance) filter.previousNode;
        } else {
            original = filter;
        }
        int filterId = original.originalFilterId;
        int[] result = filterTimes.get(filterId);
        if (null == result) {
            logger.log(Level.WARNING, "There was no filtertime for {0}", filterId);
            return -1;
        }
        int minResult = result[0];
        for (int i=1;i<result.length;i++)
            if (minResult>result[i]/i)
                minResult = result[i]/i;
        return minResult;
    }
    
    @Override
    public int estimateFilter(FilterInstance filter) {
        FilterInstance original;
        if (filter instanceof SplitTreeStreamNode)
            return 0;
        if (filter.isFissedNode && filter.previousNode instanceof FilterInstance) {
            original = (FilterInstance) filter.previousNode;
        } else {
            original = filter;
        }

        int filterId = original.originalFilterId;
        int[] result = filterTimes.get(filterId);
        if (null == result) {
            logger.log(Level.WARNING, "There was no filtertime for {0}", filterId);
            return -1;
        } else {
            final int rootMult = StreamNode.getRootMult();
            final double approxMult = Math.max(1.0,filter.netSteady()/original.originalNetSteady);
            if (result.length >= approxMult) {
               // if (filter.filter_id==272)
               //     logger.log(Level.INFO, "FilterID: {0} netSteady: {1} rootMult: {2} orignalNetSteady: {3} orginalNetSteady {4}", new Object[] {filter.filter_id, filter.netSteady(), rootMult,original.originalNetSteady,original.netSteady()});
                return (int) (1.0*result[(int)Math.round(approxMult) - 1] / approxMult * filter.netSteady() / original.originalNetSteady);
            } else {
                //   logger.log(Level.INFO, "Used {0} estimation instead of {1}", new Object[]{result.length, rootMult});
                return (int) (1.0*result[result.length - 1] / result.length * filter.netSteady() / original.originalNetSteady);
            }
        }
    }
}
