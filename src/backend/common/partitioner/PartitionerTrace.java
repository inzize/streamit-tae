package backend.common.partitioner;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create tracefile of partitioning process.
 * Takes list of double values and writes them with timestamp to csv file.
 */
public class PartitionerTrace {

    FileWriter writer;
    long startTime = 0;
    List<String> log;
    String fileName;

    public PartitionerTrace(String file) {
        log = new ArrayList<>();
        fileName = file;
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            // opening file failed
        }
    }

    /**
     * Finish log and write to file
     */
    public void finish() {

        try {
            for (String s : log) {
                writer.append(s);
            }
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            Logger.getLogger(PartitionerTrace.class.getName()).log(Level.WARNING, "Writing partitioner trace file failed! ("+fileName+")", ex);
        }
    }

    /**
     * Add timestamp and list of double values to log.
     * @param time timestamp
     * @param numbers list of double values
     */
    int cnt = 0;
    public void append(double... numbers) {
        if (writer == null) {
            return;
        }
        String s = new String();
        s += ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()/1e9;
        for (double num : numbers) {
            s += "," + String.valueOf(num);
        }
        s += "\n";
        log.add(s);
    }
}
