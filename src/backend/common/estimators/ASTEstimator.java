package backend.common.estimators;

import ast.statements.Statement;
import ast.statements.StatementCost;
import hierarchicalGraph.FilterInstance;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ASTEstimator extends Estimator {
    // the key of a filter time is filter_id
    private final Map<Integer,Integer> filterTimes;
    
    public ASTEstimator() {
        filterTimes = new LinkedHashMap<>();
    }
    
    private int estimate(FilterInstance filter) {
        if (filter.filter.work == null)
            return 0;
        List<Statement> workStatements = filter.filter.work.body.stmts;
        int filterTime = 0;
        for(Statement statement : workStatements)
           filterTime  += StatementCost.getCost(statement);
        return filterTime;
    }
    
    @Override
    public int estimateFilter(FilterInstance filter) {
        int filterId;
        if(filter.isFissedNode)
            filterId = ((FilterInstance)((hierarchicalGraph.SplitJoin)(filter.parent)).previousNode).originalFilterId;
        else
            filterId = filter.originalFilterId;
        if (!filterTimes.containsKey(filterId))
            filterTimes.put(filterId, estimate(filter));
        // cycles of work function multiplied with steady state multiplicity
        // TODO: add wait/set overhead
        return (int)(filterTimes.get(filterId) * filter.netSteady()) /* + wait/set overhead */;
    }
    
}
