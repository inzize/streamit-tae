package backend.common.estimators;

import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.Chip.Link;
import backend.common.Chip.LinkType;
import backend.common.Processor;
import backend.coreVA.CoreVABackend;
import backend.coreVA.CoreVAProcessor;
import hierarchicalGraph.FilterInstance;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Communication model estimating time to transmit data by each link.
 * A link is either a bus in a cluster, a NCI or a NoC link
 */
public class CommunicationModel {

    private Map<Link, List<AbstractChannel>> linkChannels;
//    private TreeMap<Link, Integer> linkTime;    
    private Map<Link, Integer> linkTime;
    private final Chip model;
    private final Map<FilterInstance, Processor> partition;

    public CommunicationModel(Chip model, Map<FilterInstance, Processor> partition) {
        CoreVABackend.BuildChannels(partition);
        linkChannels = new LinkedHashMap<>();
        this.model = model;
        this.partition = partition;

        // map links to involved channels
        for (FilterInstance f1 : partition.keySet()) {
            for (FilterInstance f2 : f1.outputs.keySet()) {
                AbstractChannel c = f1.outputs.get(f2);
                addLink(c);
            }
        }

        // map links to link transmission time
//        linkTime = new TreeMap<>(new LinkComparator());        
        linkTime = new LinkedHashMap<>();
        for (Link l : linkChannels.keySet()) {
            linkTime.put(l, getLinkTime(l));
        }
    }
    
    public int throughput(AbstractChannel ch)
    {
        int bottleneck = Integer.MAX_VALUE;
        for (Link link : getLinks(ch))
            if (linkTime.get(link) < bottleneck)
                    bottleneck = linkTime.get(link);
        return bottleneck;
    }

    public Map<Link, Integer> getLinkData() {
        return linkTime;
    }

    /**
     * Add channel to communication model by adding the amount of data a channel
     * transmits to all involved links
     *
     * @param ch channel to add to the communication model
     */
    private void addLink(AbstractChannel ch) {
        for (Link l : getLinks(ch)) {
            List<AbstractChannel> channels = linkChannels.containsKey(l) ? linkChannels.get(l) : new ArrayList<AbstractChannel>();
            channels.add(ch);
            linkChannels.put(l, channels);
        }
    }

    /**
     * Get amount of data transmitted over a link during one steady state
     *
     * @param lnk Link
     * @return transmitted data in bytes
     */
    public int getTransmittedData(Link lnk) {
        int data = 0;
        for (AbstractChannel ch : linkChannels.get(lnk)) {
            data += ch.getWorkload();
        }
        return data;
    }

    /**
     * Get all channels involved in a link
     *
     * @param lnk link in the mpsoc (bus, nci, noc)
     * @return list of channels
     */
    public List<AbstractChannel> getChannels(Link lnk) {
        return linkChannels.get(lnk);
    }

    /**
     * Get links involved in the channel
     *
     * @param ch channel
     * @return list of links
     */
    private List<Link> getLinks(AbstractChannel ch) {
        // assume all processors are CoreVAProcessor        
        CoreVAProcessor pSrc = (CoreVAProcessor) partition.get(ch.src);
        CoreVAProcessor pDst = (CoreVAProcessor) partition.get(ch.dst);
        List<Link> links = new ArrayList<>();

        if (pSrc == pDst) {
            // MemoryChannel, no link
        } else if (pSrc.getRow() == pDst.getRow() && pSrc.getCol() == pDst.getCol()) {
            // ClusterChannel, bus link
            links.add(new Link(pSrc.getCol(), pSrc.getRow(), LinkType.LNK_BUS));
        } else {
            // NocChannel, nci link + noc links
            links.add(new Link(pSrc.getCol(), pSrc.getRow(), LinkType.LNK_NCI));
            // NCI cannot receive and send at the same time.
            links.add(new Link(pDst.getCol(), pDst.getRow(), LinkType.LNK_NCI));
            // noc link routing: horizontal first, than vertical
            int hDist = pDst.getCol() - pSrc.getCol();
            int vDist = pDst.getRow() - pSrc.getRow();
            // horizontal links
            if (hDist >= 0) {
                for (int i = 0; i < hDist; i++) {
                    links.add(new Link(pSrc.getCol() + i, pSrc.getRow(), LinkType.LNK_WEST));
                }
            } else {
                for (int i = 0; i > hDist; i--) {
                    links.add(new Link(pSrc.getCol() + i, pSrc.getRow(), LinkType.LNK_EAST));
                }
            }
            // vertical links
            if (vDist >= 0) {
                for (int i = 0; i < vDist; i++) {
                    links.add(new Link(pDst.getCol(), pSrc.getRow() + i, LinkType.LNK_SOUTH));
                }
            } else {
                for (int i = 0; i > vDist; i--) {
                    links.add(new Link(pDst.getCol(), pSrc.getRow() + i, LinkType.LNK_NORTH));
                }
            }
        }
        return links;
    }

    /**
     * Get time link needs to transmit data
     *
     * @param l link
     * @return cycles needed to transmit data
     */
    private int getLinkTime(Link l) {
        //round up to int
        return (int) Math.ceil(getTransmittedData(l) / model.getLinkWidth(l));
    }

    public void reset() {
        linkChannels = new LinkedHashMap<>();
        linkTime = new LinkedHashMap<>();
    }
}
