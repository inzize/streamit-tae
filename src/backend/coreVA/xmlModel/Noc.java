package backend.coreVA.xmlModel;

import backend.coreVA.CoreVAProcessor;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Noc {
    @XmlAttribute public Integer latency;
    @XmlAttribute public Integer comm_lib_send_wait;
    @XmlAttribute public Integer comm_lib_send_done;
    @XmlAttribute public Integer comm_lib_recv_wait;
    @XmlAttribute public Integer comm_lib_recv_done;
    @XmlAttribute public Integer fifo_depth;
    @XmlAttribute public Integer nb_virtual_channels;
    @XmlAttribute public Integer hop_latency;
    @XmlAttribute public Integer link_data_width;
    @XmlAttribute public Boolean is_mesochronous;
    @XmlAttribute public NocTopology topology;
    @XmlElementWrapper(name="nodes") public List<Node> node;

    @Override
    public String toString() {
        return "NoC";
    }

    public Noc copy() {
        Noc clone = new Noc();
        
        clone.latency = latency;
        clone.comm_lib_send_wait = comm_lib_send_wait;
        clone.comm_lib_send_done = comm_lib_send_done;
        clone.comm_lib_recv_wait = comm_lib_recv_wait;
        clone.comm_lib_recv_done = comm_lib_recv_done;
        clone.fifo_depth = fifo_depth;
        clone.nb_virtual_channels = nb_virtual_channels;
        clone.hop_latency = hop_latency;
        clone.link_data_width = link_data_width;
        clone.is_mesochronous = is_mesochronous;
        clone.topology = topology;

        if (null != node) {
            clone.node = new ArrayList<>();
            for (Node n : node) {
                clone.node.add(n.copy());
            }
        }

        return clone;
    }

    public Node getNode(CoreVAProcessor proc){
        for (Node n : node) {
            if (n.x==proc.getCol() && n.y==proc.getRow())
                return n;
        }
        return null;
    }

    public Node getNode(int x, int y){
        for (Node n : node) {
            if (n.x==x && n.y==y)
                return n;
        }
        return null;
    }

    public int countProcessors() {
        int processorCount = 0;
        for (Node n : node)
            processorCount += n.core.size();
        return processorCount;
    }

    public int getLatency(CoreVAProcessor src, CoreVAProcessor dst) {
        int mandistance = Math.abs(src.getRow()-dst.getRow())+Math.abs(src.getCol()-dst.getCol());
        return mandistance*latency;
    }

    @XmlEnum
    public enum NocTopology {

        mesh
    }
}