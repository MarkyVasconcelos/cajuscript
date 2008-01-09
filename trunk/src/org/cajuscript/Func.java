/*
 * Func.java
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
 * All functions of the CajuScript are instance this class.
 * <p>CajuScript:</p>
 * <p><blockquote><pre>
 *     func v1 v2 v3 #
 *         v = v1 + v2 + v3
 *         ~ v
 *     #
 *     funcResult = func(1, 2 , 3)
 * </pre></blockquote></p>
 * Are allowed parenthesis on definition:
  * <p><blockquote><pre>
 *     func(v1, v2, v3) #
 *         ...
 *     #
 *     func(v1 v2 v3) #
 *         ...
 *     #
 * </pre></blockquote></p>
 * <p>Java:</p>
 * <p><blockquote><pre>
 *     org.cajuscript.CajuScript caju = new org.cajuscript.CajuScript();
 *     String funcDef = "func v1 v2 v3";
 *     String funcScript = "v = v1 + v2 + v3;~ v";
 *     org.cajuscript.Func cajuFunc = new org.cajuscript.Func(caju, funcDef, funcScript, 0);
 *     System.out.println("Java invoke = " + cajuFunc.invoke(caju.toValue(new Integer(1)), caju.toValue(new Integer(2)), caju.toValue(new Integer(3))).getValue());
 *     System.out.println("CajuScript invoke = " + caju.eval("~ func(1, 2 , 3)").getValue());
 * </pre></blockquote></p>
 * @author eduveks
 */
public class Func {   
    private String name = "";
    private String[] paramKey = new String[0];
    private String script = "";
    private CajuScript cajuScript = null;
    private int lineNumber;
    /**
     * Create a new function.
     * @param caju Instace of the CajuScript
     * @param funcDef Function definition. Definition is the name and the parameters
     * @param funcScript Script of the function
     * @param line Line where the function is started
     * @throws org.cajuscript.CajuScriptException Errors ocurred on funcion execution
     */
    public Func(CajuScript caju, String funcDef, String funcScript, int line) throws CajuScriptException {
        cajuScript = caju;
        funcDef = funcDef.trim();
        int p1 = funcDef.indexOf('(');
        if (p1 > -1) {
            int p2 = funcDef.lastIndexOf(')');
            name = funcDef.substring(0, p1).trim();
            String param = funcDef.substring(p1 + 1, p2).trim();
            if (param.indexOf(',') > -1) {
                paramKey = param.split("\\,");
            } else {
                paramKey = param.split(" ");
            }
            for (int x = 0; x < paramKey.length; x++) {
                paramKey[x] = paramKey[x].trim();
            }
        } else {
            int p = funcDef.indexOf(' ');
            if (p > -1) {
                name = funcDef.substring(0, p).trim();
                String param = funcDef.substring(p + 1).trim();
                if (param.indexOf(',') > -1) {
                    paramKey = param.split("\\,");
                } else {
                    paramKey = param.split(" ");
                }
                for (int x = 0; x < paramKey.length; x++) {
                    paramKey[x] = paramKey[x].trim();
                }
            } else {
                name = funcDef.trim();
            }
        }
        script = funcScript;
        lineNumber = line;
    }
    /**
     * Name of funcion
     * @return Name
     */
    public String getName() {
        return name;
    }
    /**
     * Invoke the function executing the script
     * @param paramValue Values of the parameters
     * @return Value returned by script
     * @throws org.cajuscript.CajuScriptException
     */
    public Value invoke(Value... paramValue) throws CajuScriptException {
        for (int x = 0; x < paramValue.length; x++) {
            cajuScript.addVar(paramKey[x], paramValue[x]);
        }
        int backupLineNumber = cajuScript.getLine();
        cajuScript.setLine(lineNumber);
        Value r = cajuScript.eval(script);
        cajuScript.setLine(backupLineNumber);
        return r;
    }
}