package backend.common;

import backend.common.estimators.CommunicationEstimator;
import backend.common.estimators.PerfFileEstimator;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class TimelineWriter {
    public static void writeTimeline(Map<FilterInstance, Processor> partition, String filename, Chip chip, String dstPath) throws FileNotFoundException  {
        PerfFileEstimator      filterTimeEstimator = new PerfFileEstimator(filename, new ArrayList<>(partition.keySet()));
        CommunicationEstimator comEstimator = new CommunicationEstimator(partition);
        comEstimator.estimatePartition();       
        for (Processor proc: partition.values()) {
            File traceFile = new File(dstPath+File.separator+"str_"+proc.toString()+".csv");
            traceFile.getParentFile().mkdirs();
            PrintWriter output = new PrintWriter(traceFile);
            long procCounter   = 0;
            output.println(procCounter + ";0");            
            for (FilterInstance filter : partition.keySet()) {
                if (partition.get(filter) == proc) {
                    procCounter+=filterTimeEstimator.estimateFilter(filter);
                    output.print(procCounter);
                    output.print(";1");
                    // filter name
                    //output.println(filter.toString());
                    procCounter+=comEstimator.getFilterCost(filter); //TODO: handle 0 com time  + don't use latency
                    output.print(procCounter);
                    output.println(";0");
                }                
            }
            output.close();
        }
    }
}
