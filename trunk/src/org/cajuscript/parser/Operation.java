/*
 * Operation.java
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

package org.cajuscript.parser;

import org.cajuscript.CajuScript;
import org.cajuscript.Context;
import org.cajuscript.Value;
import org.cajuscript.Syntax;
import org.cajuscript.CajuScriptException;

/**
 * Script element of type operation.
 * @author eduveks
 */
public class Operation extends Base {
    private static final int ACTION_ID_ADDITION = 1;
    private static final int ACTION_ID_SUBTRACTION = 2;
    private static final int ACTION_ID_MULTIPLICATION = 3;
    private static final int ACTION_ID_DIVISION = 4;
    private static final int ACTION_ID_MODULES = 5;
    private static final int ACTION_ID_AND = 6;
    private static final int ACTION_ID_OR = 7;
    private static final int ACTION_ID_EQUAL = 8;
    private static final int ACTION_ID_NOT_EQUAL = 9;
    private static final int ACTION_ID_LESS = 10;
    private static final int ACTION_ID_GREATER = 11;
    private static final int ACTION_ID_LESS_EQUAL = 12;
    private static final int ACTION_ID_GREATER_EQUAL = 13;
    private static final Boolean BOOLEAN_TRUE = new Boolean(true);
    private static final Boolean BOOLEAN_FALSE = new Boolean(false);
    private Element firstCommand = null;
    private Element secondCommand = null;
    private int action = 0;
    private Value v = null;
    /**
     * Create new Operation.
     * @param line Line detail
     * @param syntax Syntax style
     */
    public Operation(LineDetail line, Syntax syntax) {
        super(line, syntax);
    }
    /**
     * Set commands of this operation
     * @param firstCommand First command
     * @param action Action
     * @param secondCommand Second command
     * @throws org.cajuscript.CajuScriptException Operator is invalid!
     */
    public void setCommands(Element firstCommand, String action, Element secondCommand) throws CajuScriptException {
        this.firstCommand = firstCommand;
        if (action.equals(""+ getSyntax().getOperatorAddition())) {
            this.action = ACTION_ID_ADDITION;
        } else if (action.equals(""+ getSyntax().getOperatorSubtraction())) {
            this.action = ACTION_ID_SUBTRACTION;
        } else if (action.equals(""+ getSyntax().getOperatorMultiplication())) {
            this.action = ACTION_ID_MULTIPLICATION;
        } else if (action.equals(""+ getSyntax().getOperatorDivision())) {
            this.action = ACTION_ID_DIVISION;
        } else if (action.equals(""+ getSyntax().getOperatorModules())) {
            this.action = ACTION_ID_MODULES;
        } else if (action.equals(getSyntax().getOperatorAnd())) {
            this.action = ACTION_ID_AND;
        } else if (action.equals(getSyntax().getOperatorOr())) {
            this.action = ACTION_ID_OR;
        } else if (action.equals(getSyntax().getOperatorEqual())) {
            this.action = ACTION_ID_EQUAL;
        } else if (action.equals(getSyntax().getOperatorNotEqual())) {
            this.action = ACTION_ID_NOT_EQUAL;
        } else if (action.equals(getSyntax().getOperatorLess())) {
            this.action = ACTION_ID_LESS;
        } else if (action.equals(getSyntax().getOperatorGreater())) {
            this.action = ACTION_ID_GREATER;
        } else if (action.equals(getSyntax().getOperatorLessEqual())) {
            this.action = ACTION_ID_LESS_EQUAL;
        } else if (action.equals(getSyntax().getOperatorGreaterEqual())) {
            this.action = ACTION_ID_GREATER_EQUAL;
        } else {
            throw new CajuScriptException("Operator "+ action + " is invalid!");
        }
        this.secondCommand = secondCommand;
    }
    /**
     * Executed this element and all childs elements.
     * @param caju CajuScript instance
     * @return Value returned by execution
     * @throws org.cajuscript.CajuScriptException Errors ocurred on execution
     */
    @Override
    public Value execute(CajuScript caju, Context context) throws CajuScriptException {
        caju.setRunningLine(getLineDetail());
        for (Element element : elements) {
            element.execute(caju, context);
        }
        if (v == null) {
            v = new Value(caju, context, getSyntax());
        }
        v.setContext(context);
        v.setSyntax(getSyntax());
        Value v1 = firstCommand.execute(caju, context);
        Value v2 = secondCommand.execute(caju, context);
        switch(action) {
            case ACTION_ID_ADDITION:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER && v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER) {
                        v.setValue(new Integer(v1.getNumberIntegerValue() + v2.getNumberIntegerValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)) {
                        v.setValue(new Float(v1.getNumberFloatValue() + v2.getNumberFloatValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_LONG)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_LONG)) {
                        v.setValue(new Long(v1.getNumberLongValue() + v2.getNumberLongValue()));
                        return v;
                    } else {
                        v.setValue(new Double(v1.getNumberDoubleValue() + v2.getNumberDoubleValue()));
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    v.setValue(v1.getStringValue() + v2.getStringValue());
                    return v;
                }
                return null;
            case ACTION_ID_SUBTRACTION:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER && v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER) {
                        v.setValue(new Integer(v1.getNumberIntegerValue() - v2.getNumberIntegerValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)) {
                        v.setValue(new Float(v1.getNumberFloatValue() - v2.getNumberFloatValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_LONG)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_LONG)) {
                        v.setValue(new Long(v1.getNumberLongValue() - v2.getNumberLongValue()));
                        return v;
                    } else {
                        v.setValue(new Double(v1.getNumberDoubleValue() - v2.getNumberDoubleValue()));
                        return v;
                    }
                }
                return null;
            case ACTION_ID_MULTIPLICATION:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER && v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER) {
                        v.setValue(new Integer(v1.getNumberIntegerValue() * v2.getNumberIntegerValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)) {
                        v.setValue(new Float(v1.getNumberFloatValue() * v2.getNumberFloatValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_LONG)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_LONG)) {
                        v.setValue(new Long(v1.getNumberLongValue() * v2.getNumberLongValue()));
                        return v;
                    } else {
                        v.setValue(new Double(v1.getNumberDoubleValue() * v2.getNumberDoubleValue()));
                        return v;
                    }
                }
                return null;
            case ACTION_ID_DIVISION:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER && v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER) {
                        v.setValue(new Integer(v1.getNumberIntegerValue() / v2.getNumberIntegerValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)) {
                        v.setValue(new Float(v1.getNumberFloatValue() / v2.getNumberFloatValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_LONG)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_LONG)) {
                        v.setValue(new Long(v1.getNumberLongValue() / v2.getNumberLongValue()));
                        return v;
                    } else {
                        v.setValue(new Double(v1.getNumberDoubleValue() / v2.getNumberDoubleValue()));
                        return v;
                    }
                }
                return null;
            case ACTION_ID_MODULES:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER && v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER) {
                        v.setValue(new Integer(v1.getNumberIntegerValue() % v2.getNumberIntegerValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_FLOAT)) {
                        v.setValue(new Float(v1.getNumberFloatValue() % v2.getNumberFloatValue()));
                        return v;
                    } else if ((v1.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v1.getTypeNumber() == Value.TYPE_NUMBER_LONG)
                        && (v2.getTypeNumber() == Value.TYPE_NUMBER_INTEGER || v2.getTypeNumber() == Value.TYPE_NUMBER_LONG)) {
                        v.setValue(new Long(v1.getNumberLongValue() % v2.getNumberLongValue()));
                        return v;
                    } else {
                        v.setValue(new Double(v1.getNumberDoubleValue() % v2.getNumberDoubleValue()));
                        return v;
                    }
                }
                return null;
            case ACTION_ID_AND:
                if (((Boolean)v1.getValue()).booleanValue() == true && ((Boolean)v2.getValue()).booleanValue() == true) {
                    v.setValue(BOOLEAN_TRUE);
                    return v;
                } else {
                    v.setValue(BOOLEAN_FALSE);
                    return v;
                }
            case ACTION_ID_OR:
                if (((Boolean)v1.getValue()).booleanValue() == true || ((Boolean)v2.getValue()).booleanValue() == true) {
                    v.setValue(BOOLEAN_TRUE);
                    return v;
                } else {
                    v.setValue(BOOLEAN_FALSE);
                    return v;
                }
            case ACTION_ID_EQUAL:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() == v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                    return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    if (v1.getStringValue().equals(v2.getStringValue())) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                v.setValue(new Boolean(v1.getValue().equals(v2.getValue())));
                return v;
            case ACTION_ID_NOT_EQUAL:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() != v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    if (!v1.getStringValue().equals(v2.getStringValue())) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                v.setValue(new Boolean(!v1.getValue().equals(v2.getValue())));
                return v;
            case ACTION_ID_LESS:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() < v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    if (v1.getStringValue().compareTo(v2.getStringValue()) < 0) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                return null;
            case ACTION_ID_GREATER:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() > v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    if (v1.getStringValue().compareTo(v2.getStringValue()) > 0) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                return null;
            case ACTION_ID_LESS_EQUAL:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() <= v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    int c = v1.getStringValue().compareTo(v2.getStringValue());
                    if (c < 0 || c == 0) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                return null;
            case ACTION_ID_GREATER_EQUAL:
                if (v1.getType() == Value.TYPE_NUMBER && v2.getType() == Value.TYPE_NUMBER) {
                    if (v1.getNumberValue() >= v2.getNumberValue()) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                } else if ((v1.getType() == Value.TYPE_STRING || v1.getType() == Value.TYPE_NUMBER)
                    && (v2.getType() == Value.TYPE_STRING || v2.getType() == Value.TYPE_NUMBER)) {
                    int c = v1.getStringValue().compareTo(v2.getStringValue());
                    if (c > 0 || c == 0) {
                        v.setValue(BOOLEAN_TRUE);
                        return v;
                    } else {
                        v.setValue(BOOLEAN_FALSE);
                        return v;
                    }
                }
                return null;
            default:
                break;
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        firstCommand = null;
        secondCommand = null;
        v = null;
    }
}
