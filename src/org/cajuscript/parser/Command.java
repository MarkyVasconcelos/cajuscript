/*
 * Command.java
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
 * Script element of type command.
 * @author eduveks
 */
public class Command extends Base {
    private String command = "";
    private String contextsKey;
    private String valueKey;
    
    /**
     * Create new Command.
     * @param line Line detail
     */
    public Command(LineDetail line) {
        super(line);
        contextsKey = org.cajuscript.CajuScript.CAJU_VARS + "_contexts_" + Integer.toString(this.hashCode());
        valueKey = org.cajuscript.CajuScript.CAJU_VARS + "_value_" + Integer.toString(this.hashCode());
    }
    
    /**
     * Get command.
     * @return Script of the command
     */
    public String getCommand() {
        return command;
    }
    
    /**
     * Set command.
     * @param c  Script of the command
     */
    public void setCommand(String c) {
        command = c.trim();
    }
    
    /**
     * Executed this element.
     * @param caju CajuScript
     * @param context Context
     * @param syntax Syntax
     * @return Value returned by execution
     * @throws org.cajuscript.CajuScriptException Errors ocurred on execution
     */
    @Override
    public Value execute(CajuScript caju, Context context, Syntax syntax) throws CajuScriptException {
        caju.setRunningLine(getLineDetail());
        Value contextsValue = context.getVar(contextsKey);
        if (contextsValue == null) {
            contextsValue = caju.toValue(new java.util.ArrayList<Context>());
            context.setVar(contextsKey, contextsValue);
        }
        java.util.ArrayList<Context> contexts = (java.util.ArrayList<Context>)contextsValue.getValue();
        Value value = context.getVar(valueKey);
        if (value == null || !contexts.contains(context)) {
            contexts.add(context);
            value = new Value(caju, context, syntax, command);
            context.setVar(valueKey, value);
        } else if (value.isCommand()) {
            value.setContext(context);
            value.setCommand(command);
        }
        return value;
    }
}