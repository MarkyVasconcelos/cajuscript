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
 * All exceptions are generated from this class.
 * @author eduveks
 */
public class CajuScriptException extends Exception {
    /**
     * Create a new exception.
     */
    public CajuScriptException() {
	super();
    }
    /**
     * Create a new exception.
     * @param message Exception message
     */
    public CajuScriptException(String message) {
	super(message);
    }
    /**
     * Create a new exception.
     * @param cause Another exceptions
     */
    public CajuScriptException(Throwable cause) {
        super(cause);
    }
    /**
     * Create a new exception.
     * @param message Exception message
     * @param cause Another exceptions
     */
    public CajuScriptException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Create a new exception.
     * @param caju CajuScript instance
     * @param message Message for the new exception
     * @param script Script where ocurred the exception
     * @return Exception generated
     * @throws org.cajuscript.CajuScriptException
     */
    public static CajuScriptException create(CajuScript caju, String message, String script) throws CajuScriptException {
        return create(caju, message, caju.getLine(), script, null);
    }
    /**
     * Create a new exception.
     * @param caju CajuScript instance
     * @param message Message for the new exception
     * @param line Line where ocurred the exception
     * @param script Script where ocurred the exception
     * @return Exception generated
     * @throws org.cajuscript.CajuScriptException
     */
    public static CajuScriptException create(CajuScript caju, String message, int line, String script) throws CajuScriptException {
        return create(caju, message, line, script, null);
    }
    /**
     * Create a new exception.
     * @param caju CajuScript instance
     * @param message Message for the new exception
     * @param script Script where ocurred the exception
     * @param cause Another exceptions
     * @return Exception generated
     * @throws org.cajuscript.CajuScriptException
     */
    public static CajuScriptException create(CajuScript caju, String message, String script, Throwable cause) throws CajuScriptException {
        return create(caju, message, caju.getLine(), script, cause);
    }
    /**
     * Create a new exception.
     * @param caju CajuScript instance
     * @param message Message for the new exception
     * @param line Line where ocurred the exception
     * @param script Script where ocurred the exception
     * @param cause Another exceptions
     * @return Exception generated
     * @throws org.cajuscript.CajuScriptException
     */
    public static CajuScriptException create(CajuScript caju, String message, int line, String script, Throwable cause) throws CajuScriptException {
        return new CajuScriptException(message + " > " + line + ": " + formatScript(caju, script) + "\n"+ caju.getLineContent(), cause);
    }
    private static String formatScript(CajuScript caju, String script) {
        for (String key : caju.getAllKeys(true)) {
            if (key.startsWith(CajuScript.CAJU_VARS)) {
                script = script.replace((CharSequence)key, "\""+ caju.getVar(key).toString() + "\"");
            }
        }
        return script;
    }
}