package backend.coreVA.xmlModel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {
  @XmlAttribute public int x;
  @XmlAttribute public int y;
  @XmlElement public Nci nci;
  @XmlElementWrapper(name="interconnects")
  public List<Interconnect> interconnect;
  @XmlElementWrapper(name="cores")
  public List<Core> core;
  @XmlElementWrapper(name="memories")
  public List<Memory> memory;

    @Override
    public String toString() {
        return "Node " + x + " " + y;
    }

    public Node copy() {
        Node clone = new Node();

        clone.x = x;
        clone.y = y;

        if (null != nci) {
            clone.nci = nci.copy();
        }

        if (null != interconnect) {
            clone.interconnect = new ArrayList<>();
            for (Interconnect i : interconnect) {
                clone.interconnect.add(i.copy());
            }
        }

        if (null != core) {
            clone.core = new ArrayList<>();
            for (Core c : core) {
                clone.core.add(c.copy());
            }
        }

        if (null != memory) {
            clone.memory = new ArrayList<>();
            for (Memory m : memory) {
                clone.memory.add(m.copy());
            }
        }

        return clone;
    }
}
