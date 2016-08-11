package backend.coreVA.xmlModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Memory {
    @XmlAttribute public int id;
    @XmlAttribute public int size;
    @XmlAttribute public int latency;
    @XmlAttribute public int banks;
    @XmlAttribute public Type type;
    @XmlAttribute public Topology topology;

    @Override
    public String toString() {
        return "Memory " + id;
    }

    public Memory copy() {
        Memory clone = new Memory();

        clone.id = id;
        clone.size = size;
        clone.latency = latency;
        clone.banks = banks;
        clone.type = type;
        clone.topology = topology;

        return clone;
    }

    @XmlEnum
    public enum Type {

        shared,
        tcdm
    }

    @XmlEnum
    public enum Topology {

        mesh_of_tree,
        crossbar
    }
}
