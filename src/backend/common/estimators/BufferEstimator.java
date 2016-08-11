package backend.common.estimators;

import backend.common.buffers.SimpleMergeSplitBuffer;
import backend.common.Processor;
import backend.common.buffers.AbstractMergeSplitBuffer;
import backend.common.buffers.FileWriterMergeBuffer;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BufferEstimator {
    private final static Logger logger = Logger.getLogger("MergeBufferEstimator");
    public static BufferEstimator mbe = null;
    Properties estimations;
    private final static String MERGE_SPLIT_SEP=";";
    private final static String SPLIT_GROUP_SEP="|";
    
    public static BufferEstimator getInstance() {
        if (mbe==null)
            try {
                mbe = new BufferEstimator(streamit2.Main.getConfigDir()+"bufferCosts.csv");
            } catch (IOException ex) {
                Logger.getLogger(BufferEstimator.class.getName()).log(Level.SEVERE, null, ex);
            }
        return mbe;
    }   
    
    private BufferEstimator(String filename) throws IOException {
        estimations = new Properties();   
        if (new File(filename).exists())
            estimations.load(new FileInputStream(filename));
        else
            logger.log(Level.WARNING, "MergeBuffer estimation file not found ({0})", filename);
    }
    
    //TODO: Improve Performance!
    private String getMergeSplitKeyFromBuffer(AbstractMergeSplitBuffer mergeBuffer) {
        StringBuilder pattern = new StringBuilder();
        FilterInstance filter;
        LinkedHashMap<FilterInstance,Integer> f2key = new LinkedHashMap<>();
        List<FilterInstance> usedFilters;
        final String progname = streamit2.Compiler.program.substring(streamit2.Compiler.program.lastIndexOf(File.separator));
        long iterations=0;
        if (mergeBuffer instanceof FileWriterMergeBuffer) {
            filter = mergeBuffer.dst;
            iterations = (filter.netSteady() / filter.join.size());
            usedFilters = filter.join;
            pattern.append(mergeBuffer.getClass().getSimpleName()).append("M>"+filter.originalFilterName+">"+progname+">");
        } else if (mergeBuffer.src==null) { //Merge
            filter = mergeBuffer.dst;
            usedFilters = mergeBuffer.dst.join;
            iterations = (filter.netSteady() * filter.pop / filter.join.size());
            if (filter.input_buffer!=mergeBuffer)
                throw new RuntimeException("Assert!");
            pattern.append(mergeBuffer.getClass().getSimpleName()).append("M"+filter.originalFilterName+">"+progname+">");
        } else if (mergeBuffer.dst==null) { //Split
            filter = mergeBuffer.src;
            usedFilters = new ArrayList<>();
            iterations = (filter.netSteady() * filter.push / filter.split.size());
            int len = filter.split.size();
            for (int j = 0; j < len; j++) {
                FilterInstance[] list = filter.split.get((j + (int) filter.initMult) % len);
                usedFilters.addAll(Arrays.asList(list));
                if (j!=len-1)
                    usedFilters.add(null); //used as marker
            }
            pattern.append(mergeBuffer.getClass().getSimpleName()).append("S>"+filter.originalFilterName+">"+progname+">");
        } else {
            throw new RuntimeException("Unknown case");
        }
        
        if (usedFilters.isEmpty())
            return "";
        
        pattern.append(iterations).append(">");
        //TODO: Analyse why split pattern starts with empty slice
        while (usedFilters.size()>0) { //Special begin is null handling!
            if (usedFilters.get(0)==null) {
                pattern.append(SPLIT_GROUP_SEP);
                usedFilters.remove(0);
            } else {
                break;
            }
        }
        f2key.put(usedFilters.get(0), 0);
        for (FilterInstance f : usedFilters) {
            if (f==null) {
               if (pattern.charAt(pattern.length()-1)==',')
                   pattern.deleteCharAt(pattern.length()-1);
               pattern.append(SPLIT_GROUP_SEP);
               continue;
            }
            if (!f2key.containsKey(f)) {
                Object[] tmp = f2key.values().toArray();
                f2key.put(f,(Integer)(1+(Integer)tmp[tmp.length-1]));
            }
            pattern.append(f2key.get(f)).append(",");
        }
        return pattern.substring(0, pattern.length()-1); //remove last ','
    }
    
    public int getBufferCost(AbstractMergeSplitBuffer mergeBuffer) {
        String key = getMergeSplitKeyFromBuffer(mergeBuffer);
        if (estimations.containsKey(key))
            return Integer.parseInt((String) estimations.get(key));
        return -1;
    }
    
    public void createMergeMappingFile(String filename, SortedMap<FilterInstance, Processor> partition) throws IOException {
        Properties mapping = new Properties();
        for (FilterInstance filter : partition.keySet()) {
            String merge = "-1";
            String split = "-1";
            //Special FileWriter handling -> get rid of it! -.-
            if ("FileWriter".equals(filter.filter.id)) {
                split=getMergeSplitKeyFromBuffer(new FileWriterMergeBuffer(null,filter));
            } else {
                if (null != filter.input_buffer) 
                    merge = getMergeSplitKeyFromBuffer((AbstractMergeSplitBuffer) filter.input_buffer);
                if (null != filter.output_buffer)
                    split = getMergeSplitKeyFromBuffer((AbstractMergeSplitBuffer) filter.output_buffer);
            }
            if (null != filter.originalFilterName)
                mapping.put(filter.originalFilterName,merge+MERGE_SPLIT_SEP+split);
            else
               mapping.put(filter.previousNode.StreamNodeName()+"_"+filter.fission_id,merge+MERGE_SPLIT_SEP+split);    
        }
        (new File(filename.substring(0,filename.lastIndexOf("/")))).mkdirs();
        mapping.store(new FileOutputStream(filename), "Filtername -> merge pattern [-1 no merge]");
    }
    
}
