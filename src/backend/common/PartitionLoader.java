package backend.common;

import backend.coreVA.xmlModel.MpsocChip;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PartitionLoader {
    private final static Logger logger = Logger.getLogger("compiler");    
    static Map<String, Integer[]> fissionPlan = new LinkedHashMap<>();
    static List<String> filterNames = new ArrayList<>();
    static List<FilterInstance> filters = new ArrayList<>();
    static Map<String, Integer> partitionPlan = new LinkedHashMap<>(); 
    static Integer numChip = 0;
    static Integer mult = 0;
    
    public static void setFissionPlan(Map<StreamNode, Integer[]> fissionRatio){
        fissionPlan.clear();
        for(StreamNode filter: fissionRatio.keySet())
            fissionPlan.put(filter.StreamNodeName(), fissionRatio.get(filter));
    }
    
    public static Map<StreamNode, Integer[]> getFissionRatio(){
        Map<StreamNode, Integer[]> fissionRatio = new LinkedHashMap<>();
        for(String filtername : fissionPlan.keySet())
            for(FilterInstance filter: filters)
                if(filter.StreamNodeName().equals(filtername))
                    fissionRatio.put(filter, fissionPlan.get(filtername));
        return fissionRatio;
    }
    
    public static Integer getMult(){
        return mult;
    }
    
    public static Map<String, Integer> getPartitionPlan(){
        return partitionPlan;
    }
    
    public static void setFilterNames(List<FilterInstance> fs){
        filters = new ArrayList<>(fs);
        filterNames.clear();
        for(FilterInstance filter: fs)
            filterNames.add(filter.StreamNodeName());
    }
    
    public static boolean checkParFile(Chip chip){
        if(chip.getProcessors().length != numChip)
            return false;
        int size = filterNames.size();
        if(size != filters.size())
            return false;
        for(int i = 0; i < size; i++)
            if(!filterNames.get(i).equals(filters.get(i).StreamNodeName()))
                return false;
        return true;
    }
    
    public static boolean retrivePartition(String parFile){
        try{
            FileInputStream in = new FileInputStream(parFile);
            try (ObjectInputStream ois = new ObjectInputStream(in)) {
                numChip = (Integer)ois.readObject();
                filterNames = (List<String>)ois.readObject();
                fissionPlan = (Map<String, Integer[]>)ois.readObject();
                partitionPlan = (Map<String, Integer>)ois.readObject();
                mult = (Integer)ois.readObject();
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error: Failed to retrieve partition");
            return false;
        }
        return true;
    }
    
    public static boolean storePartition(String dstLocation, Chip chip, SortedMap<FilterInstance,Processor> partition){
        numChip = chip.getProcessors().length;
        String filename;
        if(dstLocation.endsWith(".par"))
            filename = dstLocation;
        else
            filename = dstLocation+File.separator+"partition.par";

        updatePartitionPlan(partition);
        
        try{
            FileOutputStream out = new FileOutputStream(filename);
            try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                oout.writeObject(numChip);
                oout.writeObject(filterNames);
                oout.writeObject(fissionPlan);
                oout.writeObject(partitionPlan);
                oout.writeObject(StreamNode.getRootMult());
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error: Failed to save partition");
            return false;
        }
        return true;
    }
    
    private static void updatePartitionPlan(SortedMap<FilterInstance,Processor> partition){
        partitionPlan.clear();
        for (FilterInstance filter : partition.keySet()) {
            Processor p = partition.get(filter);
            partitionPlan.put(filter.StreamNodeName(), p.getGID());
        }
    }
}
