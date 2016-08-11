package ast.statements;

import ast.expressions.Constant;
import ast.expressions.ExpressionCost;
import ast.expressions.Identifier;
import ast.statements.streamIT.Push;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatementCost {
    private static Logger logger = Logger.getLogger("compiler");
    
    public static int getCost(Statement s) {
        if (s == null) 
            return 0;
        int actualCost = 0;
        for (Statement0 s0 : s.children) {
            actualCost += getCost(s0);
        }
        return actualCost;
    }
    

    private static int getCost(Statement0 s0) {
        switch (s0.getClass().getCanonicalName()) {
            case "ast.statements.If":
                return ifCost((If) s0);
            case "ast.statements.For":
                return forCost((For) s0);
            case "ast.statements.Block":
                return blockCost((Block) s0);
            case "ast.statements.Assignment":
                return assignmentCost((Assignment) s0);
            case "ast.statements.ExpressionStmt":
                return expressionStmtCost((ExpressionStmt) s0);
            case "ast.statements.streamIT.Push":
                return pushCost((Push)s0);
            default:
                return unknownCost(s0);
        }
    }

    private static int pushCost(Push s0) {
        return 1;
    }
    
    private static int expressionStmtCost(ExpressionStmt s0) {
        switch (s0.e.child.getClass().getCanonicalName()) {
            case "ast.expressions.Call": 
                return ExpressionCost.expressionCost(s0.e.child);
            case "ast.expressions.streamIT.Pop":
                return 2;
            default: 
                logger.log(Level.WARNING,"Unknown expressionStmt: "+s0.e.child.getClass().getCanonicalName());
                return 1;
        }
    }

    private static int assignmentCost(Assignment s0) {
        return 1; //fc
    }

    private static int blockCost(Block s0) {
        int blockCost = 0;
        for (Statement s : s0.stmts) {
            blockCost += getCost(s);
        }
        return blockCost;
    }
    
    private static int forCost(For s0) {
        int condExpression = 1;
        try {
            Assignment initAssign = (Assignment)s0.init.get(0).children.get(0);
            ExpressionCost.calculateForLoopCount((Identifier)initAssign.lhs.child,(Constant)initAssign.rhs.child,s0.cond.child);
        } catch (UnsupportedOperationException e) {
            logger.log(Level.WARNING,"UnsupportedOperationException: " + e.getMessage() + "(Next cost will be " + condExpression + ")");
        } catch (Exception e) {
           logger.log(Level.WARNING,"Exception!!!");
	}
        final int bodyCost = getCost(s0.body);
        //TODO: consider incr 
        return bodyCost * condExpression;
    }

    private static int ifCost(If s0) {
        final int thenCost = getCost(s0.Then);
        final int elseCost = getCost(s0.Else);
        return (int) (thenCost * 0.5 + elseCost * 0.5);
    }

    private static int unknownCost(Statement0 s0) {
        logger.log(Level.WARNING,"StatementCost: " + s0.getClass().getCanonicalName() + " is unknown");
        return 0;
        
    }
}
