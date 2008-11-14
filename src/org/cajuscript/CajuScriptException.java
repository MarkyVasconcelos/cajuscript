/*
 * CajuScriptException.java
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

/**
 * Exceptions of the CajuScript.
 * @author eduveks
 */
public class CajuScriptException extends Exception {
    /**
     * Newly exception.
     */
    public CajuScriptException() {
	super();
    }
    
    /**
     * Newly exception.
     * @param message Message.
     */
    public CajuScriptException(String message) {
	super(message);
    }
    
    /**
     * Newly exception.
     * @param cause More exceptions.
     */
    public CajuScriptException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Newly exception.
     * @param message Message.
     * @param cause More exceptions.
     */
    public CajuScriptException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create an newly exception.
     * @param caju CajuScript instance.
     * @param message Message of the exception.
     * @return Newly exception.
     * @throws org.cajuscript.CajuScriptException Creating exception.
     */
    public static CajuScriptException create(CajuScript caju, Context context, String message) throws CajuScriptException {
        return create(caju, context, message, null);
    }
    
    /**
     * Create an newly exception.
     * @param caju CajuScript instance.
     * @param message Message of the exception.
     * @param cause More exceptions.
     * @return Newly exception.
     * @throws org.cajuscript.CajuScriptException Creating exception.
     */
    public static CajuScriptException create(CajuScript caju, Context context, String message, Throwable cause) throws CajuScriptException {
        if (message == null) {
            return new CajuScriptException(null, cause);
        }
        return new CajuScriptException(message.concat(" > ").concat(Integer.toString(caju.getRunningLine().getNumber())).concat(": ").concat(formatScript(caju, context, caju.getRunningLine().getContent())), cause);
    }
    
    private static String formatScript(CajuScript caju, Context context, String script) {
        for (String key : caju.getAllKeys(true)) {
            if (key.startsWith(CajuScript.CAJU_VARS)) {
                script = script.replace((CharSequence)key, "\"".concat(context.getVar(key).toString()).concat("\""));
            }
        }
        return script;
    }
}