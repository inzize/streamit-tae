package hierarchicalGraph;

import java.util.ArrayList;
import java.util.List;

public class SplitTreeStreamNode extends FilterInstance{
    
    public SplitTreeStreamNode(FilterInstance targetFilter) throws Exception {
       super();
       this.args=targetFilter.args;
       /* Save checks:
            -Check if we are in a Pipeline or in a SplitJoin
       */
       if (!(targetFilter.parent instanceof Pipeline) && !(targetFilter.parent instanceof SplitJoin)) {
           throw new Exception("Not Implemented for"+targetFilter.parent.parent.getClass().getSimpleName());
       }
       boolean isInPipeline = (targetFilter.parent instanceof Pipeline);
       if (!isInPipeline) {
            if (!(targetFilter.parent.parent instanceof Pipeline)) {
                throw new Exception("Not Implemented for"+targetFilter.parent.parent.getClass().getSimpleName());
            }
       }
       //END OF SAVE CHECKS!
       filter_id = super.next_filter_id++; 
       //-------Needed for output buffer calc-----
       pop  = targetFilter.push;
       push = targetFilter.push;
       peek = targetFilter.push;
       steadyMult = targetFilter.steadyMult;
       filter = targetFilter.filter;
       //---Everything for RoundRobinJoiner
       targetFilter.out = new FilterInstance[]{this}; 
       out = new FilterInstance[]{this}; //Who has invented that?
       in = new FilterInstance[][]{new FilterInstance[]{targetFilter}};
       //-----Change StreamNode Graph:-----
       /*IDEA (isInPipeline=true):
            -Find split filter
            -Assumption: the actual splitter is one behind target Filter!
            -put new dummy filter between target and splitter
            -repair all edges!
       */
       parent = targetFilter.parent;
       if (isInPipeline) {
        Pipeline pipe = (Pipeline) parent;
        assert pipe.children.contains(targetFilter);
        int index = pipe.children.indexOf(targetFilter);
        pipe.children.add(index+1,this);
        /*SplitJoin splitJoin = (SplitJoin) pipe.children.get(index+2);
        assert splitJoin.splitter.out.length==1;
        assert splitJoin.splitter.out[0]==targetFilter;
        splitJoin.splitter.out = new FilterInstance[]{this};*/
       } else {
            if (1==1)
                throw new Exception("Deactivated -> safety (breaks sometimes)");
            SplitJoin splitJoin = (SplitJoin) parent;
            Pipeline pipe = (Pipeline) splitJoin.parent;
             int indexOfSplit = pipe.children.indexOf(parent);
             pipe.children.add(indexOfSplit, targetFilter);
             targetFilter.parent = pipe;
             assert splitJoin.children.contains(targetFilter);
             int indexOfOldSplitFilter = splitJoin.children.indexOf(targetFilter);
             splitJoin.children.remove(indexOfOldSplitFilter);
             splitJoin.children.add(this);
       }
       //-----Change current Split/Join-----
       /*
        idea: 
            -target filter output to our new filter
            -move split outputs to new filter
            -change all split filter inputs to our filter
            -set our input to target filter
       */
       split = targetFilter.split;
       for (FilterInstance[] fl : split)
           for (FilterInstance f : fl) {
               if (f.join.contains(targetFilter)) {
                   while (f.join.contains(targetFilter)) {
                       int id = f.join.indexOf(targetFilter);
                       f.join.remove(id);
                       f.join.add(id, this);
                   }
               }
           }
       join = new ArrayList();
       join.add(targetFilter);
       targetFilter.split = new ArrayList<>();
       targetFilter.split.add(new FilterInstance[]{this});
       //----Do we need to recalc netsteady?----
       System.out.println("New SplitTreeStreamNode: "+filter_id);
       //StreamNode.globalGraph.Flatten();
    }
    
    /*@Override
    public void SteadySchedule(StreamNode parent) {
    
    }*/

    /*@Override
    public long InitSchedule(long needed) {
        return 0;
    }*/

    @Override
    public void Flatten() {
        in = new FilterInstance[][]{new FilterInstance[]{this}};
        out = new FilterInstance[]{this};
    }


    @Override
    protected void InOrder(List<FilterInstance> list) {
        list.add(this);
    }
   

    @Override
    public boolean isFissable() {
       return false;
    }

    @Override
    public boolean isStateless() {
        return true;
    }

    @Override
    public boolean isPeeking() {
        return false;
    }

    @Override
    public String StreamNodeName() {
        return "SplitDummy"+filter_id;
    }

    @Override
    public List<StreamNode> InitFissedNodes(Integer[] ratio) {
        return null;
    }

    @Override
    public void Dump(int indent) {
        
    }
    
}
