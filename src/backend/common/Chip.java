package backend.common;
 
import backend.common.channels.AbstractChannel;
import java.util.AbstractMap;

/**
 * A set of processors. 
 * The chip interface provides high level informations about the target chip structure 
 * used by the {@link Partitioner} 
 */
public interface Chip {
    /**
     * Gets all processors on the target chip
     */
    public Processor[] getProcessors(); 
    public Processor[] getFirstClusterProcessors();
    /**
     * Gets the cost in processor ticks for a communication between the two processors
     * @return key: costs for src; value: costs for dst
     */    
    public AbstractMap.SimpleEntry<Integer,Integer> getCommunciationCost(Processor src, Processor dst);
    public int getCommunicationLatency(Processor src, Processor dst);
    public double getLinkWidth(Link lnk);
    /**
     * Gets the cost in bytes of memory for a communication channel between the two processors
     */
    public boolean isValidChannel(Processor pSrc, Processor pDst, AbstractChannel channel);
    public boolean isValidBufferSize(Processor pSrc, Processor pDst, AbstractChannel channel);
    
    
    enum LinkType {

        LNK_EAST, LNK_NORTH, LNK_WEST, LNK_SOUTH, LNK_NCI, LNK_BUS
    }

    public class Link {

        public int x, y;
        public LinkType linkType;

        public Link(int x, int y, LinkType linkType) {
            this.x = x;
            this.y = y;
            this.linkType = linkType;
        }

        @Override
        public boolean equals(Object o) {
            Link l = (Link) o;
            if ((l.x == this.x) && (l.y == this.y) && (l.linkType == this.linkType)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            String s = String.valueOf(x) + "." + String.valueOf(y) + "." + String.valueOf(linkType.ordinal());
            return s.hashCode();
        }
        
        @Override
        public String toString() {
            return String.format("(%2d,%2d) %s", x, y, linkType);
        }
    }
}
