package backend.coreVA.xmlModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.FIELD)
public class Slot {
    @XmlAttribute public int id;
    @XmlAttribute public boolean div;
    @XmlAttribute public boolean mul;
    @XmlAttribute public boolean ld_st;

    @Override
    public String toString() {
        return "Slot " + id;
    }

    public Slot copy() {
        Slot clone = new Slot();

        clone.id = id;
        clone.div = div;
        clone.mul = mul;
        clone.ld_st = ld_st;

        return clone;
    }
}
