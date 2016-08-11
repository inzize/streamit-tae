package backend.common.partitioner;

import gurobi.*;
import backend.common.evaluators.Evaluator;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.estimators.Estimator;
import static backend.common.partitioner.MITPartitioner.evaluator;
import backend.common.partitioner.mutator.Mutation;
import hierarchicalGraph.FilterInstance;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import hierarchicalGraph.StreamNode;
import java.util.HashMap;

public class TaePartitionerILP_NotFission {
    
    SortedMap<StreamNode, Processor> partitioning = new TreeMap<>();
    StreamNode graph = FilterInstance.globalGraph;
    SortedMap<StreamNode, Processor> partition;
    List<FilterInstance> newFilters;
    Chip chip;
    Map<StreamNode, Integer[]> fissionRatio = new HashMap();
    Map<String, Integer> partitionPlan = new HashMap();
    List<Evaluator> evaluators;
    
    public SortedMap<FilterInstance, Processor> partition(List<FilterInstance> filters, Chip chip, Estimator estimator, List<Evaluator> evaluators) {
        this.evaluators = evaluators;
        this.chip = chip;

        evaluator = evaluators.get(evaluators.size() - 1);

        Processor[] processors = chip.getProcessors();  

        int nFilters = filters.size();
        int nCores = processors.length;

        int count = 0;
        //Create array of filters
        FilterInstance[] f = new FilterInstance[nFilters];
        boolean[] filterStatus = new boolean[nFilters];
        for (FilterInstance filter : filters){
            f[filter.filter_id] = filter;
            filterStatus[filter.filter_id] = filter.isStateless();
            if(filter.isStateless()){
                count++;
            }
        }
        System.out.println("Total number of filters = "+filters.size());
        System.out.println("Total number of stateful = "+(filters.size()-count));
        System.out.println("Total number of stateless = "+count);
        
        
        System.out.println("********** TEST ILP PROGRAM ***********");
    
        try {
            GRBEnv    env   = new GRBEnv("ilp.log");
            GRBModel  model = new GRBModel(env);

            // Create variables

            System.out.println("--> Filters = "+nFilters);
            System.out.println("--> Cores = "+nCores);

            int nFissibleFilters = 0;
            double[] filterWork = new double[nFilters];
            //Random generate total work for each filter
           System.out.println("********* List of filter status and total of work ********");

            System.out.println("\t\t\tisFissible \tTotal Work");
            for(int filter=0; filter<nFilters; filter++){
                filterWork[filter] = estimator.estimateFilter(f[filter]);
                if(f[filter].isFissable()){
                    nFissibleFilters++;
                }
                System.out.println("--> StreamitNodeName: "+f[filter].StreamNodeName()+"  FilterId:"+f[filter].filter_id+"\t\t"+f[filter].isFissable()+"\t\t"+filterWork[filter]);
            }
            double e = 0.0;
            double splitterWork = 0.0;
            double joinerWork = 0.0;

            System.out.println("--> Splitter Work = "+splitterWork);
            System.out.println("--> Joiner Work = "+joinerWork);
            System.out.println("--> e = "+e);
            System.out.println("**************************************");


            //Create filter instances
            GRBVar[][][][] filterInstance = new GRBVar[nFilters][nCores][nCores+3][nCores];
            int Q;
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=0; j<Q; j++){
                    for(int k=0; k<j+3; k++){
                        for(int l=0; l<nCores; l++){    
                            filterInstance[i][j][k][l] = model.addVar(0, GRB.INFINITY, 0, GRB.BINARY, "a_"+i+"_"+j+"_"+k+"_"+l); 
                        }
                    }

                }
            }


            double[][][][] w = new double[nFilters][nCores][nCores+3][nCores];
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=0; j<Q; j++){
                    for(int k=0; k<j+3; k++){
                        for(int l=0; l<nCores; l++){
                            w[i][j][k][l] = 0.0;
                        }
                    }
                }
            }


            //Create array of Indicator variables
            GRBVar[][] indicatorVariable = new GRBVar[nFilters][nCores];

            for(int i=0; i<nFilters; i++){
                for(int l=0; l<nCores; l++){
                    indicatorVariable[i][l] = model.addVar(0, GRB.INFINITY, 0, GRB.BINARY, "b_"+i+"_"+l);
                }
            }


            // Create  variables for TW_P
            GRBVar[] TW = new GRBVar[nCores];

            for (int p = 0; p<nCores; p++) {
                TW[p] = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "TW_"+p);
            }


            //Create variable for II
            GRBVar II = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "II");


            // Integrate new variables
            model.update();


            // Display variable
            boolean listFilterInstanceName = false;
            boolean listIndicatorInstanceName = false;
            boolean listTW = false;
            count = 0;
            if(listFilterInstanceName){
                System.out.println("******* List of filter instances ******");
                for(int l=0; l<nCores; l++){
                    for(int filter=0; filter<nFilters; filter++){
                        if(filterStatus[filter]) Q = nCores; else Q = 1;
                        for(int version=0; version<Q; version++){
                            for(int k=0; k<version+3; k++){
                                System.out.println(" --> "+filterInstance[filter][version][k][l].get(GRB.StringAttr.VarName));
                                count++;
                            }
                        }
                    }
                }
                System.out.println("--> Total filter instances = "+count);
                System.out.println("***************************************");
            }
            count = 0;
            if(listIndicatorInstanceName){
                System.out.println("***** List of indicatoe instances *****");
                for(int i=0; i<nFilters; i++){
                    for(int l=0; l<nCores; l++){
                        System.out.println("--> "+indicatorVariable[i][l].get(GRB.StringAttr.VarName));
                        count++;
                    }
                }
                System.out.println("--> Total indicator = "+count);
                System.out.println("***************************************");
            }
            if(listTW){
                System.out.println("************** List of TW *************");
                for(GRBVar tw : TW){
                    System.out.println("--> "+tw.get(GRB.StringAttr.VarName));
                }
                System.out.println("--> Total TW = "+TW.length);
                System.out.println("***************************************");
            }




            //***************************** OBJECTIVE FUNCTION *********************************//
            // Set objective: minimize II
            GRBLinExpr exprX = new GRBLinExpr();
            exprX.addTerm(1.0, II);
            model.setObjective(exprX, GRB.MINIMIZE);
            //*********************************************************************************//




            //***************************** SET UP CONSTRAINTS ********************************//
            System.out.println("****** Number of each constraint ******");

            
            // Add constraint 1: a_x_x_x_x <= 1
            count = 0;
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=0; j<Q; j++){
                        for(int k=0; k<j+3; k++){
                            GRBLinExpr expr = new GRBLinExpr();
                            for(int l=0; l<nCores; l++){
                                expr.addTerm(1.0, filterInstance[i][j][k][l]);
                            }
                            model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "Constraint_1_filter_"+i);
                            count++;
                        }
                }

            }
            System.out.println("--> Total constraint_1 = "+count);




            // Add constraint 2: SUM(a_x_x_x_x) - b_x_x = 0
            count = 0;
            for(int i=0; i<nFilters; i++){
                GRBLinExpr expr = new GRBLinExpr();
                for(int l=0; l<nCores; l++){
                    expr.addTerm(1.0, filterInstance[i][0][0][l]);
                }
                expr.addTerm(-1.0, indicatorVariable[i][0]);
                model.addConstr(expr, GRB.EQUAL, 0, "Constraint_2_version_0_filter_"+i);
                count++;
            }
            System.out.println("--> Total constraint_2 = "+count);
            
            
            // Add constraint 2.1: Fix b_X_0 = 1
            count = 0;
            for(int i=0; i<nFilters; i++){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, indicatorVariable[i][0]);
                model.addConstr(expr, GRB.EQUAL, 1, "Constraint_2_1_version_0_filter_"+i);
                count++;
            }
            System.out.println("--> Total constraint_2.1 = "+count);



            /*
            //// Add constraint 3: a_x_x_x_x - (1+M) * b_x_x <= J+3
            count = 0;
            int M = nCores+10;
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=1; j<Q; j++){
                    GRBLinExpr expr = new GRBLinExpr();
                    for(int k=0; k<j+3; k++){
                        for(int l=0; l<nCores; l++){
                            expr.addTerm(1.0, filterInstance[i][j][k][l]);
                        }
                    } 
                    expr.addTerm(-(1+M), indicatorVariable[i][j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 0, "Constraint_3_filter_"+i+"_version_"+j);
                    count++;
                }
            }
            System.out.println("--> Total constraint_3 = "+count);




            //// Add constraint 4: SUM(a_x_x_x_x) - (1+M) * b_x_x >= j+4-M
            count = 0;
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=1; j<Q; j++){
                    GRBLinExpr expr = new GRBLinExpr();
                    for(int k=0; k<j+3; k++){
                        for(int l=0; l<nCores; l++){
                            expr.addTerm(1.0, filterInstance[i][j][k][l]);
                        }
                    } 
                    expr.addTerm(-(1+M), indicatorVariable[i][j]);
                    model.addConstr(expr, GRB.GREATER_EQUAL, (j+2)-M, "Constraint_4_filter_"+i+"_version_"+j);
                    count++;
                }
            }
            System.out.println("--> Total constraint_4 = "+count);
            */



            //// Add constraint 5: b_x_0 + b_x_1 + b_x_2 + b_x_n = 1
            count = 0;
            for(int i=0; i<nFilters; i++){
                GRBLinExpr expr = new GRBLinExpr();
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=0; j<Q; j++){
                    expr.addTerm(1.0, indicatorVariable[i][j]); 
                }
                model.addConstr(expr, GRB.EQUAL, 1.0, "Constraint_5_filter_"+i);
                count++;
            }

            //Debugging
            //model.addConstr(indicatorVariable[0][1], GRB.EQUAL, 1.0, "Constraint_Test");



            //Equation 6
            count = 0;
            double ww;
            for(int i=0; i<nFilters; i++){
                if(filterStatus[i]) Q = nCores; else Q = 1;
                for(int j=0; j<Q; j++){
                    for(int k=0; k<j+3; k++){
                        for(int l=0; l<nCores; l++){
                            if(j==0){
                                w[i][0][0][l] = filterWork[i];
                            }else if(j>=1 && k<j+1){
                                ww = filterWork[i]/(j+1)+e;
                                w[i][j][k][l] = ww;
                                count++;
                            }else if(j>=1 && k==j+1){
                                w[i][j][k][l] = splitterWork;
                                count++;
                            }else if(j>=1 && k==j+2){
                                w[i][j][k][l] = joinerWork;
                                count++;
                            }
                        }
                    }
                }
            }
            System.out.println("--> Total constraint_6 = "+count);
            //Print work of each filter instance
            boolean filterInstanceWork = false;
            if(filterInstanceWork){
                for(int i=0; i<nFilters; i++){
                    if(filterStatus[i]) Q = nCores; else Q = 1;
                    for(int j=0; j<Q; j++){
                        for(int k=0; k<j+3; k++){
                            for(int l=0; l<nCores; l++){
                                System.out.println("w["+i+"]["+j+"]["+k+"]["+l+"] = "+w[i][j][k][l]);
                            }
                        }
                    }
                }
            }




            //// Add constraint 7
            count = 0;
            for(int p=0; p<nCores; p++){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, TW[p]);
                for(int i=0; i<nFilters; i++){
                    if(filterStatus[i]) Q = nCores; else Q = 1;
                    for(int j=0; j<Q; j++){
                        for(int k=0; k<j+3; k++){
                            expr.addTerm(-w[i][j][k][p], filterInstance[i][j][k][p]);
                            //System.out.print(filterInstance[i][j][k][p].get(GRB.StringAttr.VarName)+" ");
                        }  
                    }    
                }
                model.addConstr(expr, GRB.EQUAL, 0.0, "Constraint_7_core_"+p);
                count++;
            }
            System.out.println("--> Total constraint_7 = "+count);




            //// Add constraint 8: TW_P - II <= 0
            count = 0;
            for(int p=0; p<nCores; p++){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, TW[p]); expr.addTerm(-1.0, II);
                model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "Constraint_8_core_"+p);
                count++;
            }
            System.out.println("--> Total constraint_8 = "+count);
            System.out.println("***************************************");




            // Optimize model
            model.optimize();       

            model.write("model.lp");        

            /*
            System.out.println();
            System.out.println("********** Result **********");
            //Filter instance status
            System.out.println("*** Selected filters ***");
            boolean displayOnlySelectedFilters = true;
            for(int core=0; core<nCores; core++){
                for(int filter=0; filter<nFilters; filter++){
                    if(filterStatus[filter]) Q = nCores; else Q = 1;
                    for(int version=0; version<Q; version++){
                        for(int k=0; k<version+3; k++){
                            if(displayOnlySelectedFilters){
                                if(filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)==1)
                                System.out.println(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)
                                    + " " +filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X));
                            }else{
                                System.out.println(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)
                                    + " " +filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X));
                            }
                        }
                    }
                }
            }
            //Version
            System.out.println();
            System.out.println("*** Selected version ***");
            boolean printOnlySelectedVersion = true;
            for(int filter=0; filter<nFilters; filter++){
                if(filterStatus[filter]) Q = nCores; else Q = 1;
                for(int version=0; version<Q; version++){
                    if(printOnlySelectedVersion){
                        if(indicatorVariable[filter][version].get(GRB.DoubleAttr.X)==1){
                            System.out.println(indicatorVariable[filter][version].get(GRB.StringAttr.VarName)
                                + " " +indicatorVariable[filter][version].get(GRB.DoubleAttr.X));
                        }
                    }else{
                        System.out.println(indicatorVariable[filter][version].get(GRB.StringAttr.VarName)
                            + " " +indicatorVariable[filter][version].get(GRB.DoubleAttr.X));
                    }
                }
            }
            
            //Total work each core
            System.out.println();
            System.out.println("*** Total work each core ***");
            for(GRBVar tw : TW){
                System.out.println(tw.get(GRB.StringAttr.VarName)
                            + " " +tw.get(GRB.DoubleAttr.X));
            }
            
            //II
            System.out.println();
            System.out.println(II.get(GRB.StringAttr.VarName)
                            + " " +II.get(GRB.DoubleAttr.X));

            System.out.println();
            System.out.println("****************************");
            System.out.println("Objective: " + model.get(GRB.DoubleAttr.ObjVal));
            System.out.println("****************************");


            boolean debuggingAllConstraints = false;
            if(debuggingAllConstraints){
                System.out.println();
                System.out.println("****** Debug All Constraints *******");
                double result = 0;
                System.out.println("Constraint 1");
                for(int core=0; core<nCores; core++){
                    for(int filter=0; filter<nFilters; filter++){
                        if(filterStatus[filter]) Q = nCores; else Q = 1;
                        for(int version=0; version<Q; version++){
                            for(int k=0; k<version+3; k++){
                                if(filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)==1)
                                    System.out.print(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)+"  ");
                                    result += filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X);
                            }
                        }
                    }
                }
                System.out.println();
                System.out.println(result + " <= 1.0");
                System.out.println(result <= 1.0);
                System.out.println("======================");

                System.out.println("Constraint 2");
                for(int filter=0; filter<nFilters; filter++){
                    result = 0;
                    for(int core=0; core<nCores; core++){
                        if(filterInstance[filter][0][0][core].get(GRB.DoubleAttr.X)==1){
                            System.out.print(filterInstance[filter][0][0][core].get(GRB.StringAttr.VarName)+"  ");
                            result += filterInstance[filter][0][0][core].get(GRB.DoubleAttr.X);
                        }

                    }
                    System.out.print("- "+indicatorVariable[filter][0].get(GRB.StringAttr.VarName));
                    result = result - indicatorVariable[filter][0].get(GRB.DoubleAttr.X);
                    System.out.println();
                    System.out.println(result + " = 0.0");
                    System.out.println(result==0.0);
                }
                System.out.println("======================");

                System.out.println("Constraint 3");
                for(int filter=0; filter<nFilters; filter++){
                    result = 0;
                    if(filterStatus[filter]) Q = nCores; else Q = 1;
                    for(int version=1; version<Q; version++){
                        for(int k=0; k<version+3; k++){
                            for(int core=0; core<nCores; core++){
                                if(filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)==1){
                                    System.out.print(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)+"  ");
                                    result += filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X);
                                }
                            }
                        }
                        result = result - (1+M)*indicatorVariable[filter][version].get(GRB.DoubleAttr.X);
                        System.out.println(result + " <= 0.0");
                        System.out.println(result <= 0.0);
                    }
                }
                System.out.println("======================");

                System.out.println("Constraint 4");
                for(int filter=0; filter<nFilters; filter++){
                    result = 0;
                    if(filterStatus[filter]) Q = nCores; else Q = 1;
                    for(int version=1; version<Q; version++){
                        for(int k=0; k<version+3; k++){
                            for(int core=0; core<nCores; core++){
                                if(filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)==1){
                                    System.out.print(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)+"  ");
                                    result += filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X);
                                }
                            }
                        }
                        result = result - (1+M)*indicatorVariable[filter][version].get(GRB.DoubleAttr.X);
                        System.out.println(result + " >= " + ((version+2)- M));
                        System.out.println(result >= ((version+2)- M));
                    }
                }
                System.out.println("======================");

                System.out.println("Constraint 5");
                for(int filter=0; filter<nFilters; filter++){
                    result = 0;
                    if(filterStatus[filter]) Q = nCores; else Q = 1;
                    for(int version=0; version<Q; version++){
                        System.out.print(indicatorVariable[filter][version].get(GRB.StringAttr.VarName)+
                                "("+indicatorVariable[filter][version].get(GRB.DoubleAttr.X)+")  ");
                        result += indicatorVariable[filter][version].get(GRB.DoubleAttr.X);

                    }
                    System.out.println("\n" + result + " = 1.0");
                    System.out.println(result==1.0);
                }
                System.out.println("======================");

                System.out.println("Constraint 7");
                for(int core=0; core<nCores; core++){
                    result = 0;
                    for(int filter=0; filter<nFilters; filter++){
                        if(filterStatus[filter]) Q = nCores; else Q = 1;
                        for(int version=0; version<Q; version++){
                            for(int k=0; k<version+3; k++){
                                if(filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)==1){
                                    System.out.print(filterInstance[filter][version][k][core].get(GRB.StringAttr.VarName)+
                                            "("+filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X)+") x "+
                                            w[filter][version][k][core]+" ");
                                    result += filterInstance[filter][version][k][core].get(GRB.DoubleAttr.X) * w[filter][version][k][core];
                                }
                                if(k==version+2 && version==nCores-1 && filter==nFilters-1)
                                    System.out.println();
                            }
                        }
                    }
                    System.out.println("\nTW_" + core + " = " + result);
                }
                System.out.println("======================");

                System.out.println("Constraint 8");
                for(int core=0; core<nCores; core++){

                }
                System.out.println("II = " + II.get(GRB.DoubleAttr.X));
                System.out.println("======================");

                System.out.println("************************************");
            }
            */

            
            /*
            System.out.println("***** To do partitioning here... *****");
            System.out.println();

            int[] arrayCopies = new int[nFissibleFilters];
            Map<Integer, Integer> fissionDetail = new HashMap();
            // Create fissionRatio
            count = 0;
            for(int filter=0; filter<nFilters; filter++){
                if(filterStatus[filter]){ //yes=isFissible
                    System.out.print("a_"+filter+" is fissible ");
                    for(int version=0; version<nCores; version++){
                        if(indicatorVariable[filter][version].get(GRB.DoubleAttr.X)==1 && version!=0){
                            System.out.println(indicatorVariable[filter][version].get(GRB.StringAttr.VarName)+" and version = "+version);

                            Integer[] ratio = new Integer[version+1];
                            for (int i = 0; i < version+1; i++) {
                                ratio[i] = 1;
                            }

                            fissionRatio.put((StreamNode)f[filter], ratio);
                            arrayCopies[count++] = version+1;
                            fissionDetail.put(f[filter].streamnode_id, version+1);
                        }else if(indicatorVariable[filter][version].get(GRB.DoubleAttr.X)==1 && version==0){
                            arrayCopies[count++] = 1;
                        }
                    }
                }
            }

            System.out.println("\n*********** Fission Ratio ***********");
            for(StreamNode key : fissionRatio.keySet()){
                System.out.println(key.StreamNodeName()+" : "+fissionRatio.get(key));
            }
            System.out.println("*************************************");

            System.out.println("\n********** Fission Detail ***********");
            for(Integer key : fissionDetail.keySet()){
                System.out.println(key+" : "+fissionDetail.get(key));
            }
            System.out.println("*************************************");


            fissFilters(fissionRatio);
            
            
            count = 0;
            System.out.println("\n******All filters before fissed******");
            for(FilterInstance filter : filters){
                System.out.println(filter.streamnode_id+" : "+filter.StreamNodeName()+" : "+filter.isFissable());
                if(filter.isFissable()){
                    count++;
                }
                
            }
            System.out.println("Total number of stateless filters = "+count);
            System.out.println("*************************************");
            
            
//            System.out.println("\n**All filter instances after fissed**");
//            for(FilterInstance filter : newFilters){
//                System.out.println(filter.streamnode_id + " : " + filter.StreamNodeName());
//            }
//            System.out.println("*************************************");
            

            // Create partitionPlan by solution

            int _filter = 0;
            int _version = 0;
            int _copy = 0;
            int arrayVersionCount = 0;
            StreamNode[][][] arrayNewFilterList = new StreamNode[nFilters][nCores][nCores+1];
            for (StreamNode filter : newFilters){ // for stateless filters
                if(filter.isFissable()){

                    arrayNewFilterList[_filter][arrayCopies[arrayVersionCount]-1][_copy] = filter;  // version = copies-1
                    System.out.println("\t--> "+filter.StreamNodeName()+"("+filter.isFissable()+") : arrayVersionCount="+arrayVersionCount+
                            " : filter="+_filter+" : version="+(arrayCopies[arrayVersionCount]-1)+" : copy="+_copy);

                    if(_copy<arrayCopies[arrayVersionCount]-1){
                        _copy++;
                    }else{
                        if(arrayVersionCount<arrayCopies.length-1){
                            arrayVersionCount++;
                        }
                        _filter++;
                        _copy = 0;
                    }

                }else{ // for stateful filters
                    arrayNewFilterList[_filter][0][0] = filter;
                    System.out.println("\t--> "+filter.StreamNodeName()+"("+filter.isFissable()+") : arrayVersionCount="+arrayVersionCount+
                            " : filter="+_filter+" : version=0 : copy=0");
                    _filter++;
                    _copy = 0;
                }
            }
            System.out.println();

            partitionPlan.clear();
            for(int l=0; l<nCores; l++){
                for(int filter=0; filter<nFilters; filter++){
                    if(filterStatus[filter]) Q = nCores; else Q = 1;
                    for(int version=0; version<Q; version++){
                        for(int k=0; k<version+1; k++){
                            if(filterInstance[filter][version][k][l].get(GRB.DoubleAttr.X) == 1){
                                if(arrayNewFilterList[filter][version][k] != null){
                                    //System.out.println("\t--> a["+filter+"]["+version+"]["+k+"] = "+
                                    //    arrayNewFilterList[filter][version][k].StreamNodeName()+" -> "+l);
                                    partitionPlan.put(arrayNewFilterList[filter][version][k].StreamNodeName(), l);
                                }
                            }
                        }
                    }
                }
            }

            
//            System.out.println("\n******** PartitionPlan result *********");
//            for (Map.Entry entry : partitionPlan.entrySet()) { 
//                System.out.println(entry.getKey() + " : " + entry.getValue()); 
//            }
//            System.out.println("***************************************");
//
//            
//            Random rand = new Random(42);
//            for (StreamNode filter : newFilters) {
//                partitionPlan.put(filter.StreamNodeName(), rand.nextInt(processors.length));
//            }
            

            partitionByPlan(partitionPlan);
            StreamNode.setRootMult(1);

            System.out.println("\n******** Partitioned result *********");
            for (Map.Entry entry : partitioning.entrySet()) { 
                System.out.println(entry.getKey() + " : " + entry.getValue()); 
            }
            System.out.println("*************************************");
            System.out.println();
            */

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: "+ e.getErrorCode()+". " +e.getMessage());
        }

        //return partitioning2;
        return Mutation.flattenPartition(partitioning);

    }
    
    public void fissFilters(Map<StreamNode, Integer[]> ratio) {
        StreamNode.restoreGraph();
        List<StreamNode> fissables = graph.getFissables();
        if(ratio != null && ratio.size() > 0)
            for (StreamNode f : fissables) {
                if (ratio.containsKey(f)) {
                    f.FissNode(ratio.get(f));
                }
            }
        graph.SteadySchedule(null);
        graph.InitSchedule(0);
        graph.Flatten();
        newFilters = graph.InOrder();
        streamit2.Compiler.SimplifyEdges(this.newFilters);
        //streamit2.Compiler.PrimePumpSchedule(streamit2.Compiler.NormalPrimePump(filters));
    }
    
    protected final void partitionByPlan(Map<String, Integer> pp){
        partitioning.clear();
        List<StreamNode> filters = graph.getFissables();
        for(StreamNode filter : filters){
            String targetFilterName = filter.StreamNodeName();
            int targetProcessorID = pp.get(targetFilterName);
            partitioning.put(filter, chip.getProcessors()[targetProcessorID]);
        }
    }

}