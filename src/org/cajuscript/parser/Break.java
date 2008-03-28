/*
 * Break.java
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
 * Script element of type break.
 * @author eduveks
 */
public class Break extends Base {
    private String label = "";
    /**
     * Create new Break.
     * @param line Line detail
     * @param syntax Syntax style
     */
    public Break(LineDetail line, Syntax syntax) {
        super(line, syntax);
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
        for (Element element : elements) {
            element.execute(caju, context);
        }
        Value v = new Value(caju, context, getSyntax());
        if (!getLabel().equals("")) {
            v.setFlag("break"+ getSyntax().getLabel() + getLabel());
        } else {
            v.setFlag("break");
        }
        return v;
    }
}
