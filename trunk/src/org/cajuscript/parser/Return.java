/*
 * Return.java
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
 * Script element of type return.
 * @author eduveks
 */
public class Return extends Base {
    private Element value = null;
    /**
     * Create new Return.
     * @param line Line detail
     * @param syntax Syntax style
     */
    public Return(LineDetail line, Syntax syntax) {
        super(line, syntax);
    }
    /**
     * Get value to be returned.
     * @return Element of the value
     */
    public Element getValue() {
        return value;
    }
    /**
     * Set value to be returned.
     * @param v Element of the value
     */
    public void setValue(Element v) {
        value = v;
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
        if (value != null) {
            return value.execute(caju, context);
        }
        return new Value(caju, context, getSyntax());
    }
    
    @Override
    protected void finalize() throws Throwable {
        value = null;
    }
}
