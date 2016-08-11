package backend.common;

public abstract class Processor implements Comparable<Processor> {
    public abstract long getStaticEnergyConsumption();
   
    public long previousFilterFinish;
    public int previousFilter;
    
    private final int globalId;
    private int flowId = 1;

    public Processor(int globalId) {
        this.globalId = globalId;
    }

    @Override
    public String toString() {
        return "cpu_" + globalId;
    }

    public int get_next_flow_id(int numberOfBuffers) {
        int next = flowId;
        flowId += numberOfBuffers;
        return next;
    }

    /**
     * Gets the unique processor id used to address a processor.
     */
    public int getGID() {
        return globalId;
    }

    @Override
    public int compareTo(Processor t1) {
        return toString().compareTo(t1.toString());
    }
}
