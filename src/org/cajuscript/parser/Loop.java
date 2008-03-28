/*
 * Loop.java
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
 * Script element of type loop.
 * @author eduveks
 */
public class Loop extends Base {
    private Element condition = null;
    private String label = "";
    /**
     * Create new Loop.
     * @param line Line detail.
     * @param syntax Syntax style.
     */
    public Loop(LineDetail line, Syntax syntax) {
        super(line, syntax);
    }
    /**
     * Get condition.
     * @return Element of condition.
     */
    public Element getCondition() {
        return condition;
    }
    /**
     * Set condition.
     * @param condition Element of condition.
     */
    public void setCondition(Element condition) {
        this.condition = condition;
    }
    /**
     * Get Label.
     * @return Label.
     */
    public String getLabel() {
        return label;
    }
    /**
     * Set Label.
     * @param label Label.
     */
    public void setLabel(String label) {
        this.label = label;
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
        loop: while (((Boolean)condition.execute(caju, context).getValue()).booleanValue()) {
            for (Element element : elements) {
                Value v = element.execute(caju, context);
                if (v != null && canElementReturn(element)) {
                    if (v.getFlag().startsWith("break")) {
                        if (v.getFlag().indexOf(getSyntax().getLabel()) > -1 && !v.getFlag().substring("break".length() + getSyntax().getLabel().length()).equals(getLabel())) {
                            return v;
                        }
                        break loop;
                    } else if (v.getFlag().startsWith("continue")) {
                        if (v.getFlag().indexOf(getSyntax().getLabel()) > -1 && !v.getFlag().substring("continue".length() + getSyntax().getLabel().length()).equals(getLabel())) {
                            return v;
                        }
                        continue loop;
                    } else {
                        return v;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        condition = null;
    }
}
