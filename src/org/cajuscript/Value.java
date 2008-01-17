/*
 * Value.java
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
import java.lang.reflect.*;
/**
 * All values of variables of the CajuScript are instance this class.
 * <p>CajuScript:</p>
 * <p><blockquote><pre>
 *     x = 1
 *     s = "text..."
 * </pre></blockquote></p>
 * <p>Java:</p>
 * <p><blockquote><pre>
 *     org.cajuscript.CajuScript caju = new org.cajuscript.CajuScript();
 *     org.cajuscript.Value cajuValue = new org.cajuscript.Value(caju);
 *     cajuValue.setValue(new String("Text..."));
 *     caju.addVar("text", cajuValue);
 *     System.out.println(caju.get("text"));
 * </pre></blockquote></p>
 * @author eduveks
 */
public class Value {
    public static final int TYPE_NULL = 0;
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_OBJECT = 3;
    private Object value = null;
    private CajuScript cajuScript = null;
    /**
     * Create a new value.
     */
    public Value(CajuScript caju) {
        cajuScript = caju;
    }
    /**
     * Create a new value using a script.
     * @param caju Instance of the CajuScript.
     * @param script Script refering the value
     * @throws org.cajuscript.CajuScriptException Errors loading value of script
     */
    public Value(CajuScript caju, String script) throws CajuScriptException {
        try {
            cajuScript = caju;
            script = script.trim();
            if (script.equals("$")) {
                value = null;
            } else if ((script.startsWith("'") && script.endsWith("'"))
                        || (script.startsWith("\"") && script.endsWith("\""))) {
                script = script.replace((CharSequence)"\\t", (CharSequence)"\t");
                script = script.replace((CharSequence)"\\r", (CharSequence)"\r");
                script = script.replace((CharSequence)"\\n", (CharSequence)"\n");
                script = script.replace((CharSequence)"\\\"", (CharSequence)"\"");
                script = script.replace((CharSequence)"\\'", (CharSequence)"'");
                value = script.substring(1, script.length() - 1);
            } else {
                try {
                    value = Double.valueOf(script);
                } catch (Exception e) {
                    if (script.toLowerCase().equals("true")) {
                        value = new Integer(1);
                    } else if (script.toLowerCase().equals("false")) {
                        value = new Integer(0);
                    } else {
                        int f1 = script.indexOf('(');
                        int f2 = script.indexOf(')');
                        int fd = script.indexOf('.');
                        if ((f1 > -1 && f1 < f2) || fd > -1) {
                            String path = f1 > -1 ? script.substring(0, f1) : script;
                            path = path.trim();
                            String name = fd > -1 && fd < f1 ? path.substring(0, fd) : path;
                            name = name.trim();
                            Func func = cajuScript.getFunc(path);
                            Value val = cajuScript.getVar(name);
                            if (func != null) {
                                script = script.substring(path.length());
                                Object[] _values = invokeValues(script);
                                Value[] values = new Value[_values.length - 1];
                                for (int x = 0; x < _values.length - 1; x++) {
                                    values[x] = cajuScript.toValue(_values[x]);
                                }
                                script = script.substring(_values[values.length].toString().length());
                                value = invokeNative(func.invoke(values).getValue(), script);
                            } else if (val != null) {
                                script = script.substring(fd + 1);
                                value = invokeNative(val.getValue(), script);
                            } else {
                                value = invokeNative(null, script);
                            }
                        } else {
                            Value v = cajuScript.getVar(script);
                            if (v == null) {
                                if (script.indexOf(' ') > -1 || script.indexOf(')') > -1 || script.indexOf('(') > -1) {
                                    throw CajuScriptException.create(cajuScript, "Sintax error", script);
                                }
                                throw CajuScriptException.create(cajuScript, script + " is not defined", script);
                            }
                            value = v.getValue();
                        }
                    }
                }
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(caju, "Sintax error", script, e);
        }
    }
    /**
     * Get value in number.
     * @return Value in number
     */
    public double getNumberValue() {
        try {
            return Double.valueOf(value.toString()).doubleValue();
        } catch (Exception e) {
            return -1;
        }
    }
    /**
     * Get the object of value.
     * @return Value
     */
    public Object getValue() {
        return value;
    }
    /**
     * Define the value.
     * @param value Objet to be the value
     */
    public void setValue(Object value) {
        this.value = value;
    }
    /**
     * Get the type internal of value.
     * Types: TYPE_NULL = 0, TYPE_NUMBER = 1, TYPE_STRING = 2, TYPE_OBJECT = 3.
     * @return Number of type
     */
    public int getType() {
        if (value == null) {
            return TYPE_NULL;
        }
        try {
            Double.valueOf(value.toString()).doubleValue();
            return TYPE_NUMBER;
        } catch (Exception e) {
            if (value instanceof String) {
                return TYPE_STRING;
            } else {
                return TYPE_OBJECT;
            }
        }
    }
    /**
     * Get value in string.
     * @return Value in string
     */
    public String toString() {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
    private Object invokeNative(Object value, String script) throws CajuScriptException {
        String realScript = script;
        String realClassName = "";
        try {
            if (script == null || script.equals("")) {
                return value;
            }
            if (script.startsWith(".")) {
                script = script.substring(1);
            }
            script = script.trim();
            //String[] scriptParts = script.split("([a-Z])(\\.)([a-Z])");
            String path = "";
            String realPath = "";
            int p = -1;
            String scriptRest = script;
            while (true) {
                p++;
                String scriptPart = "";
                String cName = "";
                if (scriptRest.indexOf(".") > -1) {
                    scriptPart = scriptRest.substring(0, scriptRest.indexOf("."));
                    scriptRest = scriptRest.substring(scriptRest.indexOf(".") + 1);
                } else {
                    scriptPart = scriptRest;
                    scriptRest = "";
                }
                if (p > 0) {
                    path += ".";
                    realPath += ".";
                }
                if (realPath.endsWith("..")) {
                    realClassName = path.substring(0, path.lastIndexOf('.') - 1);
                    break;
                }
                if (scriptPart.indexOf("(") > -1 && value == null) {
                    cName = scriptPart.substring(0, scriptPart.indexOf("("));
                    realPath += cName;
                    cName = cName.trim();
                    path += cName;
                } else if (scriptPart.indexOf("(") > -1 && value != null) {
                    cName = scriptPart.substring(0, scriptPart.indexOf("("));
                    realPath += cName;
                    cName = cName.trim();
                    path += cName;
                } else {
                    path += scriptPart.trim();
                    realPath += scriptPart;
                }
                Class c = null;
                if (value == null) {
                    try {
                        try {
                            c = Class.forName(path);
                        } catch (Exception e) {
                            for(String i : cajuScript.getImports()) {
                                if (i.endsWith(path)) {
                                    try {
                                        c = Class.forName(i);
                                    } catch (Exception ex) { }
                                } else {
                                    try {
                                        c = Class.forName(i + "." + path);
                                    } catch (Exception ex) { }
                                }
                                if (c != null) {
                                    break;
                                }
                            }
                            if (c == null) {
                                throw new Exception();
                            }
                        }
                    } catch (Exception e) {
                        Value _value = cajuScript.getVar(path);
                        if (_value != null) {
                            value = _value;
                            c = value.getClass();
                        } else {
                            continue;
                        }
                    }
                    script = script.substring(realPath.length());
                } else {
                    c = value.getClass();
                }
                if (!cName.equals("") && value == null) {
                    Object[] values = invokeValues(scriptPart.substring(cName.length()));
                    script = script.substring(values[values.length - 1].toString().length());
                    return invokeConstructor(c, values, script);
                } else if (!cName.equals("") && value != null) {
                    Object[] values = invokeValues(script);
                    script = script.substring(values[values.length - 1].toString().length());
                    return invokeMethod(c, value, cName, values, script);
                } else {
                    if (script.startsWith(".")) {
                        script = script.substring(1);
                    }
                    boolean isMethod = false;
                    if (script.indexOf("(") > -1 && (script.indexOf("(") < script.indexOf(".") || script.indexOf(".") == -1)) {
                        isMethod = true;
                        scriptPart = script.substring(0, script.indexOf("("));
                    } else {
                        scriptPart = script;
                    }
                    int d = script.indexOf(".");
                    if (!isMethod || d > -1) {
                        String paramName = scriptPart;
                        if (d > -1) {
                            paramName = scriptPart.substring(0, d);
                        }
                        script = script.substring(paramName.length());
                        if (value != null) {
                            return invokeNative(value.getClass().getField(paramName).get(value), script);
                        }
                        if (paramName.equals("class")) {
                            return invokeNative(c, script);
                        } else {
                            return invokeNative(c.getField(paramName).get(c), script);
                        }
                    } else {
                        Object[] values = invokeValues(script.substring(script.indexOf("(")));
                        String propName = script.substring(0, script.indexOf("("));
                        script = script.substring(propName.length() + values[values.length - 1].toString().length());
                        return invokeMethod(c, value, propName, values, script);
                    }
                }
            }
            throw new Exception("Cannot invoke " + realClassName);
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(cajuScript, e.getMessage(), realScript, e);
        }
    }
    private boolean foundMethod(Object[] values, Class[] cx, boolean allowAutoPrimitiveCast) {
        int count = 0;
        for (int x = 0; x < values.length - 1; x++) {
            if (values[x] == null && !isPrimitiveType(cx[x].getName())) {
                count++;
            } else if (values[x] != null) {
                if (cx[x].getName().equals("java.lang.Object")
                    || (isPrimitiveType(values[x].getClass().getName()) && isPrimitiveType(cx[x].getName()) && allowAutoPrimitiveCast)
                    || values[x].getClass().getName().equals(cx[x].getName())
                    || isSameType(cx[x].getName(), values[x].getClass().getName())
                    || (cx[x].isArray() && values[x].getClass().isArray() && isSameType(cx[x].getComponentType().getName(), values[x].getClass().getComponentType().getName()))) {
                    count++;
                } else {
                    boolean isInstance = false;
                    if (allowAutoPrimitiveCast) {
                        if (cx[x].isInstance(values[x])) {
                            count++;
                            isInstance = true;
                        } else {
                            for (int y = 0; y < values[x].getClass().getClasses().length; y++) {
                                if (isSameType(values[x].getClass().getClasses()[y].getName(), cx[x].getName())) {
                                    count++;
                                    isInstance = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isInstance) {
                        count = 0;
                    }
                }
            } else {
                count = 0;
            }
        }
        if (count == cx.length && values.length - 1 == count) {
            return true;
        } else {
            return false;
        }
    }
    private Object invokeConstructor(Class c, Object[] values, String script) throws Exception {
        Constructor[] cn = c.getDeclaredConstructors();
        boolean allowAutoPrimitiveCast = true;
        for (int i = 0; i < cn.length; i++) {
            if (i == 0) {
                if (allowAutoPrimitiveCast) {
                    allowAutoPrimitiveCast = false;
                } else {
                    allowAutoPrimitiveCast = true;
                }
            }
            int x = i;
            if (i == cn.length -1 && !allowAutoPrimitiveCast) {
                i = -1;
            }
            Class cx[] = cn[x].getParameterTypes();
            if (values.length - 1 != cx.length) {
                continue;
            }
            if (foundMethod(values, cx, allowAutoPrimitiveCast)) {
                return invokeNative(cn[x].newInstance(getParams(values, cx)), script);
            }
        }
        throw new Exception("Constructor \""+ c.getName() +"\" cannot be invoked");
    }
    private Object invokeMethod(Class c, Object o, String name, Object[] values, String script) throws Exception {
        Method[] mt = c.getMethods();
        boolean allowAutoPrimitiveCast = true;
        for (int i = 0; i < mt.length; i++) {
            if (i == 0) {
                if (allowAutoPrimitiveCast) {
                    allowAutoPrimitiveCast = false;
                } else {
                    allowAutoPrimitiveCast = true;
                }
            }
            int x = i;
            if (i == mt.length -1 && !allowAutoPrimitiveCast) {
                i = -1;
            }
            if (!mt[x].getName().equals(name)) {
                continue;
            }
            Class[] cx = mt[x].getParameterTypes();
            if (values.length - 1 != cx.length) {
                continue;
            }
            if (foundMethod(values, cx, allowAutoPrimitiveCast)) {
                return invokeNative(mt[x].invoke(o, getParams(values, cx)), script);
            }
        }
        throw new Exception("Method \""+ name +"\" cannot be invoked");
    }
    private Object[] invokeValues(String script) throws CajuScriptException {
        String originalScript = script;
        script = script.substring(script.indexOf("(") + 1, script.indexOf("(") + script.substring(script.indexOf("(")).indexOf(")"));
        if (script.trim().equals("")) {
            return new Object[] {originalScript};
        }
        String[] _values = new String[1];
        _values[0] = "";
        int groupID = 0;
        for (char c : script.toCharArray()) {
            switch (c) {
            case '(':
                groupID++;
                break;
            case ')':
                groupID--;
                break;
            case ',':
                if (groupID == 0) {
                    _values = java.util.Arrays.copyOf(_values, _values.length + 1);
                    _values[_values.length - 1] = "";
                }
                continue;
            default:
                break;
            }
            _values[_values.length - 1] += ""+ c;
        }
        Object[] values = new Object[_values.length + 1];
        for (int x = 0; x < _values.length; x++) {
            values[x] = new Value(cajuScript, _values[x]).getValue();
        }
        values[_values.length] = originalScript;
        return values;
    }
    private boolean isSameType(String type1, String type2) {
        int p = type1.indexOf("$");
        if (type1.indexOf("$") > -1) {
            type1 = type1.substring(0, type1.indexOf("$"));
        }
        p = type2.indexOf("$");
        if (type2.indexOf("$") > -1) {
            type2 = type2.substring(0, type2.indexOf("$"));
        }
        if (type1.toLowerCase().endsWith("." + type2.toLowerCase()) || type2.toLowerCase().endsWith("." + type1.toLowerCase()) || type1.equals(type2)) {
            return true;
        }
        return false;
    }
    private boolean isPrimitiveType(String type) {
        if (type.equals("int") || type.equals("java.lang.Integer")
            || type.equals("long") || type.equals("java.lang.Long")
            || type.equals("double") || type.equals("java.lang.Double")
            || type.equals("float") || type.equals("java.lang.Float")
            || type.equals("char") || type.equals("java.lang.Character")
            || type.equals("byte") || type.equals("java.lang.Byte")
            || type.equals("boolean") || type.equals("java.lang.Boolean")) {
            return true;
        }
        return false;
    }
    private Object[] getParams(Object[] values, Class[] cx) throws CajuScriptException {
        Object[] params = new Object[cx.length];
        for (int x = 0; x < cx.length; x++) {
            if (isPrimitiveType(cx[x].getName())) {
                params[x] = cajuScript.cast(values[x], cx[x].getName());
            } else {
                params[x] = cx[x].cast(values[x]);
            }
        }
        return params;
    }
}