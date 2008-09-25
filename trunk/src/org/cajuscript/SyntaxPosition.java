/*
 * SyntaxPosition.java
 * 
 * This file is part of CajuScript.
 * 
 * CajuScript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3, or (at your option) 
 * any later version.
 * 
 * CajuScript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CajuScript.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cajuscript;

import java.util.regex.Pattern;
import org.cajuscript.parser.Operation.Operator;

/**
 * Sintax matchers return an instance from this class with useful data to 
 * manipulate dynamically the sintax.
 * @author eduveks
 */
public class SyntaxPosition {
    private int start = -1;
    private int end = -1;
    private String group = "";
    private String allContent = "";
    private Operator operator = null;
    
    /**
     * Newly instance to save useful data to manipulate dynamically the sintax.
     * @param syntax Current syntax.
     * @param pattern Parttern was matcher.
     */
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
    
    /**
     * Get end of text that was caught from the matcher pattern.
     * @return End index.
     */
    public int getEnd() {
        return end;
    }
    
    /**
     * Set end of text that was caught from the matcher pattern.
     * @param end End index.
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * Get start of text that was caught from the matcher pattern.
     * @return start index.
     */
    public int getStart() {
        return start;
    }

    /**
     * Set start of text that was caught from the matcher pattern.
     * @param start Start index.
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Get text was caught from the matcher pattern.
     * @return Text caught.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set text group was caught from the matcher pattern.
     * @param group Text group caught.
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Get all content was caught from the matcher pattern.
     * @return Content caught.
     */
    public String getAllContent() {
        return allContent;
    }

    /**
     * Set all content was caught from the matcher pattern.
     * @param allContent Content caught.
     */
    public void setAllContent(String allContent) {
        this.allContent = allContent;
    }

    /**
     * Get operator from the pattern.
     * @return Operator
     */
    public Operator getOperator() {
        return operator;
    }
}
