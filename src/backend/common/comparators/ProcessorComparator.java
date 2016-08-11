package backend.common.comparators;

import backend.common.Processor;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class ProcessorComparator implements Comparator<Processor> {

    Map<Processor, Integer> base;
    public ProcessorComparator(Map<Processor, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(Processor a, Processor b) {
        if (base.get(a) > base.get(b)) {
            return 1;
        } else if (Objects.equals(base.get(a), base.get(b))) {
            if(a.getGID() >= b.getGID())
                return 1;
            else
                return -1;
        }else {
            return -1;
        }
    }
}
