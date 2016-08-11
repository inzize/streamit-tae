package ast.expressions;

import ast.members.Function;
import ast.statements.StatementCost;
import ast.statements.VarDecl;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpressionCost {
    private static Logger logger = Logger.getLogger("compiler");
    public static int expressionCost(Expression0 e) {
        switch (e.getClass().getCanonicalName()) {
            case "ast.expressions.Binary": 
                return expBinary((Binary)e);
            case "ast.expressions.Call":
                return callCost((Call)e);
            default:
                logger.log(Level.WARNING,"Unknown Expression "+e.getClass().getCanonicalName());
                return 0;
        }
    }
    
    public static int calculateForLoopCount(Identifier conVar, Constant conInitValue,Expression0 cond) {
       try {
         Binary condBin = (Binary)cond;
         if (!((Identifier)condBin.e1.child).declaration.equals(conVar.declaration)) {
           logger.log(Level.WARNING,"unknown for type detected");
           return 1;
        }
         Identifier maxValueI = (Identifier)condBin.e2.child;
         VarDecl maxValue = (VarDecl)maxValueI.declaration;
         if (maxValue.value==null) {
             logger.log(Level.WARNING,"could't estimate the value for \""+maxValue.name+"\"");
             return 1;       
         }
         
         logger.log(Level.SEVERE, maxValue.value.child.toString());
         System.exit(1);
         return 0;
       } catch (ClassCastException e) {
           logger.log(Level.WARNING,"new for type ("+e.getMessage()+")");
           return 0;
       }
    }
    
    private static int callCost(Call e) {
        final String funcName = ((Identifier)e.function.child).name;
        Function f = e.ResolveFunction(funcName);
        if (f==null) {
            logger.log(Level.WARNING,"Function \""+funcName+"\" not found");
            return 1;
        }
        final int callingCost = 5;
        return callingCost+StatementCost.getCost(f.body);
    }
    
    private static int expBinary(Binary e) {
        expressionCost(e.e2.child);
        logger.log(Level.WARNING,e.e1.toString() + e.e2.toString() + e.op);
        return 0;
    }
    
    
}
