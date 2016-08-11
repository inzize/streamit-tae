package backend.common.estimators;

import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.Pipeline;
import hierarchicalGraph.SplitJoin;
import hierarchicalGraph.StreamNode;

public abstract class Estimator {
    public abstract int estimateFilter(FilterInstance filter);
    
    public int estimateStreamNode(StreamNode node){
        int totalwork = 0;
        if(node instanceof FilterInstance)
            return estimateFilter((FilterInstance)node);
        else if(node instanceof SplitJoin){
            SplitJoin sj = ((SplitJoin)node);
            for(StreamNode sn : sj.children)
                totalwork += estimateStreamNode(sn);
        }else if(node instanceof Pipeline){
            Pipeline pl = ((Pipeline)node);
            for(StreamNode sn : pl.children)
                totalwork += estimateStreamNode(sn);
        }
            
        return totalwork;
    }
}
