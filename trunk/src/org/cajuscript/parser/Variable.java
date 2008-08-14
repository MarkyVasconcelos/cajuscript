/*
 * Variable.java
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
import org.cajuscript.SyntaxPosition;
import org.cajuscript.Syntax;
import org.cajuscript.CajuScriptException;

/**
 * Script element of type variable.
 * @author eduveks
 */
public class Variable extends Base {
    private String key = "";
    private Element value = null;
    /**
     * Create new Variable.
     * @param line Line detail
     * @param syntax Syntax style
     */
    public Variable(LineDetail line, Syntax syntax) {
        super(line, syntax);
    }
    /**
     * Get variable key.
     * @return Key
     */
    public String getKey() {
        return key;
    }
    /**
     * Set variable key.
     * @param key Key
     */
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * Get variable value.
     * @return Element of value
     */
    public Element getValue() {
        return value;
    }
    /**
     * Set variable value.
     * @param value Element of value
     */
    public void setValue(Element value) {
        this.value = value;
    }
    /**
     * Executed this element and all childs elements.
     * @param caju CajuScript instance
     * @return Value returned by execution
     * @throws org.cajuscript.CajuScriptException Errors ocurred on execution
     */
    @Override
    public Value execute(CajuScript caju, Context context) throws CajuScriptException {
        if (key.equals("__caju_param_2")) {
            context.toString();
        }
        caju.setRunningLine(getLineDetail());
        for (Element element : elements) {
            element.execute(caju, context);
        }
        Value v = value.execute(caju, context);
        if (!key.equals("")) {
            SyntaxPosition syntaxPosition = getSyntax().matcherPosition(key, getSyntax().getRootContext());
            if (syntaxPosition.getStart() == 0) {
                caju.setVar(key.substring(syntaxPosition.getEnd()), v);
            } else {
                context.setVar(key, v);
            }
        }
        return v;
    }
    
    @Override
    protected void finalize() throws Throwable {
        value = null;
    }
}
