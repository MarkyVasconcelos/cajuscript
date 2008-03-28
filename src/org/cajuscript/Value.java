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
import org.cajuscript.parser.Function;
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
public class Value implements Cloneable {
    /**
     * Value type Null.
     */
    public static final int TYPE_NULL = 0;
    /**
     * Value type Number.
     */
    public static final int TYPE_NUMBER = 1;
    /**
     * Value type String.
     */
    public static final int TYPE_STRING = 2;
    /**
     * Value type Object.
     */
    public static final int TYPE_OBJECT = 3;
    /**
     * Value number type Integer.
     */
    public static final int TYPE_NUMBER_INTEGER = 1;
    /**
     * Value number type Long.
     */
    public static final int TYPE_NUMBER_LONG = 2;
    /**
     * Value number type Float.
     */
    public static final int TYPE_NUMBER_FLOAT = 3;
    /**
     * Value number type Double.
     */
    public static final int TYPE_NUMBER_DOUBLE = 4;
    private Object value = null;
    private int valueNumberInteger = 0;
    private long valueNumberLong = 0;
    private float valueNumberFloat = 0;
    private double valueNumberDouble = 0;
    private String valueString = "";
    private int typeNumber = 0;
    private int type = 0;
    private boolean _isCommand = false;
    String command = "";
    private CajuScript cajuScript = null;
    private Context context = null;
    private Syntax syntax = null;
    private String flag = "";
    /**
     * Create a new value.
     * @param caju CajuScript.
     * @param context Context for this value.
     * @param syntax Syntax for this value.
     */
    public Value(CajuScript caju, Context context, Syntax syntax) {
        cajuScript = caju;
        this.context = context;
        this.syntax = syntax;
    }
    /**
     * Create a new value using a script.
     * @param caju Instance of the CajuScript.
     * @param script Script refering the value.
     * @param syntax Script syntax style.
     * @throws org.cajuscript.CajuScriptException Errors loading value with the script.
     */
    public Value(CajuScript caju, Context context, Syntax syntax, String script) throws CajuScriptException {
        try {
            cajuScript = caju;
            this.context = context;
            this.syntax = syntax;
            script = script.trim();
            if (script.equals("")) {
                return;
            } else if (script.equals(syntax.getNull())) {
                value = null;
            } else if ((script.startsWith("'") && script.endsWith("'"))
                        || (script.startsWith("\"") && script.endsWith("\""))) {
                script = script.replace((CharSequence)"\\t", (CharSequence)"\t");
                script = script.replace((CharSequence)"\\r", (CharSequence)"\r");
                script = script.replace((CharSequence)"\\n", (CharSequence)"\n");
                script = script.replace((CharSequence)"\\\"", (CharSequence)"\"");
                script = script.replace((CharSequence)"\\'", (CharSequence)"'");
                type = TYPE_STRING;
                valueString = script.substring(1, script.length() - 1);
                value = valueString;
            } else {
                try {
                    loadNumberValue(script, true);
                } catch (Exception e) {
                    if (script.toLowerCase().equals("true")) {
                        value = new Boolean(true);
                        valueNumberInteger = 1;
                        valueString = valueNumberInteger + "";
                        type = TYPE_NUMBER;
                        typeNumber = TYPE_NUMBER_INTEGER;
                    } else if (script.toLowerCase().equals("false")) {
                        value = new Boolean(false);
                        valueNumberInteger = 0;
                        valueString = valueNumberInteger + "";
                        type = TYPE_NUMBER;
                        typeNumber = TYPE_NUMBER_INTEGER;
                    } else {
                        setCommand(script);
                    }
                }
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(caju, context, "Invalid value: "+ script, e);
        }
    }
    /**
     * Get context.
     * @return Context.
     */
    public Context getContext() {
        return context;
    }
    /**
     * Set context.
     * @param context Context.
     */
    public void setContext(Context context) {
        this.context = context;
    }
    /**
     * Get syntax.
     * @return Syntax.
     */
    public Syntax getSyntax() {
        return syntax;
    }
    /**
     * Set syntax.
     * @param syntax Syntax.
     */
    public void setSyntax(Syntax syntax) {
        this.syntax = syntax;
    }
    /**
     * If value is command type.
     * @return Is command or not.
     */
    public boolean isCommand() {
        return _isCommand;
    }
    /**
     * Get command.
     * @return Command.
     */
    public String getCommand() {
        return command;
    }
    /**
     * Set command.
     * @param script Command script.
     * @throws org.cajuscript.CajuScriptException Errors loading command.
     */
    public void setCommand(String script) throws CajuScriptException {
        script = script.trim();
        command = script;
        int f1 = script.indexOf('(');
        int f2 = script.indexOf(')');
        int fd = script.indexOf('.');
        if (script.startsWith(syntax.getRootContext())) {
            fd = script.substring(fd + 1).indexOf('.');
        }
        if ((f1 > -1 && f1 < f2) || fd > -1) {
            String path = f1 > -1 ? script.substring(0, f1) : script;
            path = path.trim();
            String name = fd > -1 && fd < f1 ? path.substring(0, fd) : path;
            name = name.trim();
            Function func = cajuScript.getFunc(path);
            Value val = context.getVar(name);
            if (func != null) {
                Context funcContext = new Context();
                script = script.substring(path.length());
                Object[] _values = invokeValues(script);
                Value[] values = new Value[_values.length - 1];
                for (int x = 0; x < _values.length - 1; x++) {
                    values[x] = cajuScript.toValue(_values[x], funcContext, syntax);
                }
                script = script.substring(_values[values.length].toString().length());
                value = invokeNative(func.invoke(cajuScript, funcContext, values).getValue(), script);
            } else if (val != null) {
                script = script.substring(fd + 1);
                value = invokeNative(val.getValue(), script);
            } else {
                value = invokeNative(null, script);
            }
        } else {
            Value v = null;
            if (script.startsWith(syntax.getRootContext())) {
                v = cajuScript.getVar(script.substring(syntax.getRootContext().length()));
            } else {
                v = context.getVar(script);
                if (v == null) {
                    v = cajuScript.getVar(script);
                }
            }
            if (v == null) {
                if (script.indexOf(' ') > -1 || script.indexOf(')') > -1 || script.indexOf('(') > -1) {
                    throw CajuScriptException.create(cajuScript, context, "Syntax error");
                }
                throw CajuScriptException.create(cajuScript, context, script + " is not defined");
            }
            value = v.getValue();
        }
        setValue(value);
        _isCommand = true;
    }
    /**
     * Get value in string.
     * @return String value.
     */
    public String getStringValue() {
        return valueString;
    }
    /**
     * Get value in number.
     * @return Number value.
     */
    public double getNumberValue() {
        switch (typeNumber) {
            case TYPE_NUMBER_INTEGER:
                return (double)valueNumberInteger;
            case TYPE_NUMBER_FLOAT:
                return (double)valueNumberFloat;
            case TYPE_NUMBER_LONG:
                return (double)valueNumberLong;
            case TYPE_NUMBER_DOUBLE:
                return valueNumberDouble;
            default:
                return 0d;
        }
    }
    /**
     * Get value in integer.
     * @return Integer value.
     */
    public int getNumberIntegerValue() {
        return valueNumberInteger;
    }
    /**
     * Get value in long.
     * @return Long value.
     */
    public long getNumberLongValue() {
        return valueNumberLong;
    }
    /**
     * Get value in float.
     * @return Float value.
     */
    public float getNumberFloatValue() {
        return valueNumberFloat;
    }
    /**
     * Get value in double.
     * @return Double value.
     */
    public double getNumberDoubleValue() {
        return valueNumberDouble;
    }
    /**
     * Get the object of value.
     * @return Value.
     */
    public Object getValue() {
        return value;
    }
    /**
     * Define the value.
     * @param value Objet to be the value.
     */
    public void setValue(Object value) {
        valueNumberInteger = 0;
        valueNumberLong = 0;
        valueNumberFloat = 0;
        valueNumberDouble = 0;
        valueString = "";
        this.value = value;
        if (value instanceof Boolean) {
            return;
        }
        try {
            loadNumberValue(value, false);
        } catch (Exception e) {
            if (value == null) {
                type = TYPE_NULL;
            } else if (value instanceof String || value instanceof Character) {
                type = TYPE_STRING;
                valueString = value.toString();
            } else {
                type = TYPE_OBJECT;
            }
            this.value = value;
            valueNumberInteger = 0;
            valueNumberLong = 0;
            valueNumberFloat = 0;
            valueNumberDouble = 0;
        }
    }
    /**
     * Get the type internal of value.
     * Types: TYPE_NULL = 0, TYPE_NUMBER = 1, TYPE_STRING = 2, TYPE_OBJECT = 3.
     * @return Number of type.
     */
    public int getType() {
        return type;
    }
    /**
     * Get the type internal of number value.
     * Types: TYPE_NUMBER_INTEGER = 1, TYPE_NUMBER_LONG = 2, TYPE_NUMBER_FLOAT = 3, TYPE_NUMBER_DOUBLE = 4.
     * @return Number of type.
     */
    public int getTypeNumber() {
        return typeNumber;
    }
    /**
     * Get flag definition.
     * @return Information.
     */
    public String getFlag() {
        return flag;
    }
    /**
     * Set flag definition.
     * @param flag Information.
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }
    /**
     * Get value in string.
     * @return Value in string.
     */
    @Override
    public String toString() {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
    private void loadNumberValue(Object o, boolean loadValue) throws Exception {
        if (o instanceof Integer) {
            valueNumberInteger = ((Integer)o).intValue();
            valueNumberLong = (long)valueNumberInteger;
            valueNumberFloat = (float)valueNumberInteger;
            valueNumberDouble = (double)valueNumberInteger;
            valueString = valueNumberInteger + "";
            type = TYPE_NUMBER;
            typeNumber = TYPE_NUMBER_INTEGER;
            return;
        } else if (o instanceof Float) {
            valueNumberFloat = ((Float)o).floatValue();
            valueNumberDouble = (double)valueNumberFloat;
            valueString = valueNumberFloat + "";
            type = TYPE_NUMBER;
            typeNumber = TYPE_NUMBER_FLOAT;
            return;
        } else if (o instanceof Long) {
            valueNumberLong = ((Long)o).longValue();
            valueNumberDouble = (double)valueNumberLong;
            valueString = valueNumberLong + "";
            type = TYPE_NUMBER;
            typeNumber = TYPE_NUMBER_LONG;
            return;
        } else if (o instanceof Double) {
            valueNumberDouble = (float)((Double)o).doubleValue();
            valueString = valueNumberDouble + "";
            type = TYPE_NUMBER;
            typeNumber = TYPE_NUMBER_DOUBLE;
            return;
        }
        Double v = (Double)cajuScript.cast(o, "d");
        double d = v.doubleValue();
        if ((long)d == d) {
            if (d <= Integer.MAX_VALUE && d >= Integer.MIN_VALUE) {
                if (loadValue) {
                   value = Integer.valueOf((int)d);
                }
                loadNumberValue(Integer.valueOf((int)d), false);
            } else {
                if (loadValue) {
                   value = Long.valueOf((long)d);
                }
                loadNumberValue(Long.valueOf((long)d), false);
            }
        } else {
            if (d <= Float.MAX_VALUE && d >= Float.MIN_VALUE) {
                if (loadValue) {
                   value = Float.valueOf((float)d);
                }
                loadNumberValue(Float.valueOf((float)d), false);
            } else {
                if (loadValue) {
                   value = Double.valueOf(v);
                }
                loadNumberValue(v, false);
            }
        }
    }
    private Object invokeNative(Object value, String script) throws CajuScriptException {
        String realClassName = "";
        try {
            if (script == null || script.equals("")) {
                return value;
            }
            if (script.startsWith(".")) {
                script = script.substring(1);
            }
            script = script.trim();
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
                        Value _value = context.getVar(path);
                        if (_value == null) {
                            _value = cajuScript.getVar(path);
                        }
                        if (_value != null) {
                            value = _value.getValue();
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
            throw CajuScriptException.create(cajuScript, context, e.getMessage(), e);
        }
    }
    private boolean foundMethod(Object[] values, Class[] cx, boolean allowAutoPrimitiveCast) {
        int count = 0;
        for (int x = 0; x < values.length - 1; x++) {
            if (values[x] == null && !CajuScript.isPrimitiveType(cx[x].getName())) {
                count++;
            } else if (values[x] != null) {
                if (cx[x].getName().equals("java.lang.Object")
                    || (CajuScript.isPrimitiveType(values[x].getClass().getName()) && CajuScript.isPrimitiveType(cx[x].getName()) && allowAutoPrimitiveCast)
                    || values[x].getClass().getName().equals(cx[x].getName())
                    || CajuScript.isSameType(cx[x].getName(), values[x].getClass().getName())
                    || (cx[x].isArray() && values[x].getClass().isArray() && CajuScript.isSameType(cx[x].getComponentType().getName(), values[x].getClass().getComponentType().getName()))) {
                    count++;
                } else {
                    if (cajuScript.isInstance(values[x], cx[x])) {
                        count++;
                    } else {
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
            values[x] = new Value(cajuScript, context, syntax, _values[x]).getValue();
        }
        values[_values.length] = originalScript;
        return values;
    }
    private Object[] getParams(Object[] values, Class[] cx) throws Exception {
        Object[] params = new Object[cx.length];
        for (int x = 0; x < cx.length; x++) {
            if (CajuScript.isPrimitiveType(cx[x].getName())) {
                params[x] = cajuScript.cast(values[x], cx[x].getName());
            } else {
                params[x] = cx[x].cast(values[x]);
            }
        }
        return params;
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Cannot clone this object.");
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        value = null;
        cajuScript = null;
        context = null;
        syntax = null;
    }
}