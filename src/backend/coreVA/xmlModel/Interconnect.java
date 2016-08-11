package backend.coreVA.xmlModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Interconnect {
    @XmlAttribute public int id;
    @XmlAttribute public Integer width;
    @XmlAttribute public Integer latency;
    @XmlAttribute public Integer comm_lib_send_wait;
    @XmlAttribute public Integer comm_lib_send_done;
    @XmlAttribute public Integer comm_lib_recv_wait;
    @XmlAttribute public Integer comm_lib_recv_done;
    @XmlAttribute public Integer reg_type1_master;
    @XmlAttribute public Integer reg_type0_master;
    @XmlAttribute public Integer reg_type1_slave;
    @XmlAttribute public Integer reg_type0_slave;
    @XmlAttribute public Standard standard;
    @XmlAttribute public Topology topology;

    @Override
    public String toString() {
        return "Interconnect " + id;
    }

    public Interconnect copy() {
        Interconnect clone = new Interconnect();

        clone.id = id;
        clone.width = width;
        clone.latency = latency;
        clone.comm_lib_send_wait = comm_lib_send_wait;
        clone.comm_lib_send_done = comm_lib_send_done;
        clone.comm_lib_recv_wait = comm_lib_recv_wait;
        clone.comm_lib_recv_done = comm_lib_recv_done;
        clone.reg_type1_master = reg_type1_master;
        clone.reg_type0_master = reg_type0_master;
        clone.reg_type1_slave = reg_type1_slave;
        clone.reg_type0_slave = reg_type0_slave;
        clone.standard = standard;
        clone.topology = topology;

        return clone;
    }

    public int getCommunicationCostSender() {
        return comm_lib_send_wait + comm_lib_send_done;
    }

    public int getCommunicationCostReceiver() {
        return comm_lib_recv_wait + comm_lib_recv_done;
    }

    @XmlEnum
    public enum Topology {

        sharedbus, // 0
        switchmatrix, // 1
        switchmatrix_2, // 2
        switchmatrix_3, // ...
        switchmatrix_4,
        switchmatrix_5,
        switchmatrix_6,
        switchmatrix_7,
        switchmatrix_8,
        switchmatrix_9,
        switchmatrix_10,
        switchmatrix_11,
        switchmatrix_12,
        switchmatrix_13,
        switchmatrix_14,
        switchmatrix_15,
        switchmatrix_16,
        switchmatrix_17,
        switchmatrix_18,
        switchmatrix_19,
        switchmatrix_20,
        switchmatrix_21,
        switchmatrix_22,
        switchmatrix_23,
        switchmatrix_24,
        switchmatrix_25,
        switchmatrix_26,
        switchmatrix_27,
        switchmatrix_28,
        switchmatrix_29,
        switchmatrix_30,
        switchmatrix_31
    }

    @XmlEnum
    public enum Standard {
        wishbone,
        axi
    }
}
