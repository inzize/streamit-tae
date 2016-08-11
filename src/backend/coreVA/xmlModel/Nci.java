package backend.coreVA.xmlModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Nci {
    @XmlAttribute public Integer max_buffersize;
    @XmlAttribute public Integer max_channel_send;
    @XmlAttribute public Integer max_channel_recv;
    @XmlAttribute public Integer comm_lib_send_wait;
    @XmlAttribute public Integer comm_lib_send_done;
    @XmlAttribute public Integer comm_lib_recv_wait;
    @XmlAttribute public Integer comm_lib_recv_done;
    @XmlAttribute public Integer bus_noc_latency;
    @XmlAttribute public Integer noc_bus_latency;
    @XmlAttribute public Integer send_ctrl_fifo_depth;
    @XmlAttribute public Integer noc_recv_fifo;

    @Override
    public String toString() {
        return "NCI";
    }

    public Nci copy() {
        Nci clone = new Nci();

        clone.max_buffersize = max_buffersize;
        clone.max_channel_send = max_channel_send;
        clone.max_channel_recv = max_channel_recv;
        clone.comm_lib_send_wait = comm_lib_send_wait;
        clone.comm_lib_send_done = comm_lib_send_done;
        clone.comm_lib_recv_wait = comm_lib_recv_wait;
        clone.comm_lib_recv_done = comm_lib_recv_done;
        clone.bus_noc_latency = bus_noc_latency;
        clone.noc_bus_latency = noc_bus_latency;
        clone.send_ctrl_fifo_depth = send_ctrl_fifo_depth;
        clone.noc_recv_fifo = noc_recv_fifo;

        return clone;
    }
}
