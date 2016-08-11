package backend.common.comparators;

import hierarchicalGraph.FilterInstance;
import java.util.Comparator;
import java.util.Map;

public class FilterComparator implements Comparator<FilterInstance> {

    Map<FilterInstance, Integer> base;
    public FilterComparator(Map<FilterInstance, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(FilterInstance a, FilterInstance b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else {
            return -1;
        }
    }
}
