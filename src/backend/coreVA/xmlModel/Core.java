package backend.coreVA.xmlModel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Core {
  @XmlAttribute public int id;
  @XmlAttribute public int if_read_latency;
  @XmlAttribute public int if_fifo_depths;
  @XmlAttribute public int if_write_latency;
  @XmlAttribute public int data_mem_size;
  @XmlAttribute public Integer instr_mem_size;
  @XmlAttribute public boolean enable_simd;
  @XmlElementWrapper(name="slots")
  public List<Slot> slot;

    @Override
    public String toString() {
        return "Core " + id;
    }

    public Core copy() {
        Core clone = new Core();

        clone.id = id;
        clone.if_read_latency = if_read_latency;
        clone.if_fifo_depths = if_fifo_depths;
        clone.if_write_latency = if_write_latency;
        clone.data_mem_size = data_mem_size;
        clone.instr_mem_size = instr_mem_size;
        clone.enable_simd = enable_simd;

        if (null != slot) {
            clone.slot = new ArrayList<>();
            for (Slot s : slot) {
                clone.slot.add(s.copy());
            }
        }

        return clone;
  }
}
