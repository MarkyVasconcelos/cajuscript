package org.cajuscript;

import java.util.regex.Pattern;
import org.cajuscript.parser.Operation.Operator;

public class SyntaxPosition {
    private int start = -1;
    private int end = -1;
    private String group = "";
    private String allContent = "";
    private Operator operator = null;
    public SyntaxPosition(Syntax syntax, Pattern pattern) {
        if (pattern.equals(syntax.getOperatorAnd())) {
            operator = Operator.AND;
        } else if (pattern.equals(syntax.getOperatorOr())) {
            operator = Operator.OR;
        } else if (pattern.equals(syntax.getOperatorEqual())) {
            operator = Operator.EQUAL;
        } else if (pattern.equals(syntax.getOperatorNotEqual())) {
            operator = Operator.NOT_EQUAL;
        } else if (pattern.equals(syntax.getOperatorGreater())) {
            operator = Operator.GREATER;
        } else if (pattern.equals(syntax.getOperatorLess())) {
            operator = Operator.LESS;
        } else if (pattern.equals(syntax.getOperatorGreaterEqual())) {
            operator = Operator.GREATER_EQUAL;
        } else if (pattern.equals(syntax.getOperatorLessEqual())) {
            operator = Operator.LESS_EQUAL;
        } else if (pattern.equals(syntax.getOperatorAddition())) {
            operator = Operator.ADDITION;
        } else if (pattern.equals(syntax.getOperatorSubtraction())) {
            operator = Operator.SUBTRACTION;
        } else if (pattern.equals(syntax.getOperatorMultiplication())) {
            operator = Operator.MULTIPLICATION;
        } else if (pattern.equals(syntax.getOperatorDivision())) {
            operator = Operator.DIVISION;
        } else if (pattern.equals(syntax.getOperatorModules())) {
            operator = Operator.MODULES;
        }
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
    
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAllContent() {
        return allContent;
    }

    public void setAllContent(String allContent) {
        this.allContent = allContent;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
