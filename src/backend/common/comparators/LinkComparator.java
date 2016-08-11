package backend.common.comparators;

import backend.common.Chip.Link;
import java.util.Comparator;
import java.util.Map;

public class LinkComparator implements Comparator<Link> {

    Map<Link, Integer> base;
    public LinkComparator(Map<Link, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(Link a, Link b) {
        if (base.get(a) > base.get(b)) {
            return 1;
        }else {
            return -1;
        }
    }
}
