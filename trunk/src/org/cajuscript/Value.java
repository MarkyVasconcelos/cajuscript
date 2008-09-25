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
     * Types of values.
     */
    public static enum Type {
        NULL, NUMBER, STRING, OBJECT
    }
    /**
     * Types of numbers.
     */
    public static enum TypeNumber {
        INTEGER, LONG, DOUBLE, FLOAT
    }
    private Object value = null;
    private int valueNumberInteger = 0;
    private long valueNumberLong = 0;
    private float valueNumberFloat = 0;
    private double valueNumberDouble = 0;
    private String valueString = "";
    private TypeNumber typeNumber = null;
    private Type type = Type.NULL;
    private boolean _isCommand = false;
    private String command = "";
    private CajuScript cajuScript = null;
    private Context context = null;
    private Syntax syntax = null;
    private String flag = "";
    private ScriptCommand scriptCommand = null;
    private Object[] paramsValues = null;
    private Object[] paramsFinal = null;
    
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
            } else if ((script.startsWith("'") && script.endsWith("'"))
                        || (script.startsWith("\"") && script.endsWith("\""))) {
                script = script.replace((CharSequence)"\\t", (CharSequence)"\t");
                script = script.replace((CharSequence)"\\r", (CharSequence)"\r");
                script = script.replace((CharSequence)"\\n", (CharSequence)"\n");
                script = script.replace((CharSequence)"\\\"", (CharSequence)"\"");
                script = script.replace((CharSequence)"\\'", (CharSequence)"'");
                type = Type.STRING;
                valueString = script.substring(1, script.length() - 1);
                value = valueString;
            } else if (syntax.matcherEquals(script, syntax.getNull())) {
                value = null;
            } else {
                try {
                    loadNumberValue(script, true);
                } catch (Exception e) {
                    if (script.toLowerCase().equals("true")) {
                        value = new Boolean(true);
                        valueNumberInteger = 1;
                        valueString = valueNumberInteger + "";
                        type = Type.NUMBER;
                        typeNumber = TypeNumber.INTEGER;
                    } else if (script.toLowerCase().equals("false")) {
                        value = new Boolean(false);
                        valueNumberInteger = 0;
                        valueString = valueNumberInteger + "";
                        type = Type.NUMBER;
                        typeNumber = TypeNumber.INTEGER;
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
        Value v = null;
        boolean varMode = false;
        if (scriptCommand == null) {
            script = script.trim();
            command = script;
            if (scriptCommand != null && !script.equals(scriptCommand.getScript())) {
                scriptCommand = null;
            }
            SyntaxPosition syntaxRootContext = syntax.matcherPosition(script, syntax.getRootContext());
            SyntaxPosition syntaxPathSeparator = syntax.matcherPosition(script, syntax.getFunctionCallPathSeparator());
            SyntaxPosition syntaxParamBegin = syntax.matcherPosition(script, syntax.getFunctionCallParametersBegin());
            SyntaxPosition syntaxParamEnd = syntax.matcherPosition(script, syntax.getFunctionCallParametersEnd());
            if ((syntaxParamBegin.getStart() > -1 && syntaxParamBegin.getStart() < syntaxParamEnd.getStart()) || syntaxPathSeparator.getStart() > -1) {
                String path = syntaxParamBegin.getStart() > -1 ? script.substring(0, syntaxParamBegin.getStart()) : script;
                if (syntaxRootContext.getStart() == 0) {
                    path = path.substring(syntaxRootContext.getEnd());
                    syntaxPathSeparator = syntax.matcherPosition(path, syntax.getFunctionCallPathSeparator());
                }
                path = path.trim();
                String name = syntaxPathSeparator.getStart() > -1 && syntaxPathSeparator.getEnd() < syntaxParamBegin.getStart() ? path.substring(0, syntaxPathSeparator.getStart()) : path;
                name = name.trim();
                Function func = cajuScript.getFunc(path);
                Value val = null;
                boolean isRootContext = false;
                if (syntaxRootContext.getStart() != 0) {
                    val = context.getVar(name);
                    if (val == null) {
                        val = cajuScript.getVar(name);
                    }
                } else {
                    isRootContext = true;
                    val = cajuScript.getVar(name);
                }
                if (func != null) {
                    scriptCommand = new ScriptCommand(script, ScriptCommand.Type.FUNCTION);
                    script = script.substring(path.length());
                    invokeValues(script, scriptCommand);
                    scriptCommand.setClassPath(path);
                } else if (val != null) {
                    String _script = script;
                    if (syntaxPathSeparator.getStart() == -1) {
                        _script = "";
                    }
                    scriptCommand = new ScriptCommand(_script, isRootContext ? ScriptCommand.Type.NATIVE_OBJECT_ROOT : ScriptCommand.Type.NATIVE_OBJECT);
                    //scriptCommand.setObject(name);
                    scriptCommand.setValue(val);
                } else {
                    scriptCommand = new ScriptCommand(script, ScriptCommand.Type.NATIVE_CLASS);
                }
            } else {
                varMode = true;
                if (syntaxRootContext.getStart() == 0) {
                    scriptCommand = new ScriptCommand(script.substring(syntaxRootContext.getEnd()), ScriptCommand.Type.VARIABLE_ROOT);
                } else {
                    scriptCommand = new ScriptCommand(script, ScriptCommand.Type.VARIABLE);
                }
            }
        }
        if (scriptCommand != null) {
            switch (scriptCommand.getType()) {
                case VARIABLE_ROOT:
                    varMode = true;
                    v = cajuScript.getVar(scriptCommand.getScript());
                    break;
                case VARIABLE:
                    varMode = true;
                    v = context.getVar(scriptCommand.getScript());
                    if (v == null) {
                        v = cajuScript.getVar(scriptCommand.getScript());
                    }
                    break;
                case NATIVE_OBJECT_ROOT:
                    value = invokeNative(scriptCommand.getValue().getValue() == null ? null : scriptCommand.getValue().getValue(), scriptCommand.getScript(), scriptCommand);
                    break;
                case NATIVE_OBJECT:
                    String _script = scriptCommand.getScript();
                    if (scriptCommand.getValue() != null) {
                        SyntaxPosition syntaxPathSeparator = syntax.matcherPosition(_script, syntax.getFunctionCallPathSeparator());
                        _script = _script.substring(syntaxPathSeparator.getEnd());
                    }
                    value = invokeNative(scriptCommand.getValue() == null ? null : scriptCommand.getValue().getValue(), _script, scriptCommand);
                    break;
                case NATIVE_CLASS:
                    value = invokeNative(null, scriptCommand.getScript(), scriptCommand);
                    break;
                case FUNCTION:
                    Context funcContext = new Context();
                    Function func = cajuScript.getFunc(scriptCommand.getClassPath());
                    value = func.invoke(cajuScript, funcContext, syntax, scriptCommand.getParams()).getValue();
                    break;
                default:
                    break;
            }
        }
        if (varMode) {
            if (v == null) {
                SyntaxPosition syntaxParamBegin = syntax.matcherPosition(script, syntax.getFunctionCallParametersBegin());
                SyntaxPosition syntaxParamEnd = syntax.matcherPosition(script, syntax.getFunctionCallParametersEnd());
                if (script.indexOf(' ') > -1 || syntaxParamBegin.getStart() > -1 || syntaxParamEnd.getStart() > -1) {
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
            case INTEGER:
                return (double)valueNumberInteger;
            case FLOAT:
                return (double)valueNumberFloat;
            case LONG:
                return (double)valueNumberLong;
            case DOUBLE:
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
                type = Type.NULL;
            } else if (value instanceof String || value instanceof Character) {
                type = Type.STRING;
                valueString = value.toString();
            } else {
                type = Type.OBJECT;
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
     * Types: Type.NULL = 0, Type.NUMBER = 1, Type.STRING = 2, Type.OBJECT = 3.
     * @return Number of type.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Get the type internal of number value.
     * Types: TypeNumber.INTEGER = 1, TypeNumber.LONG = 2, TypeNumber.FLOAT = 3, TypeNumber.DOUBLE = 4.
     * @return Number of type.
     */
    public TypeNumber getTypeNumber() {
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
            type = Type.NUMBER;
            typeNumber = TypeNumber.INTEGER;
            return;
        } else if (o instanceof Float) {
            valueNumberFloat = ((Float)o).floatValue();
            valueNumberDouble = (double)valueNumberFloat;
            valueString = valueNumberFloat + "";
            type = Type.NUMBER;
            typeNumber = TypeNumber.FLOAT;
            return;
        } else if (o instanceof Long) {
            valueNumberLong = ((Long)o).longValue();
            valueNumberDouble = (double)valueNumberLong;
            valueString = valueNumberLong + "";
            type = Type.NUMBER;
            typeNumber = TypeNumber.LONG;
            return;
        } else if (o instanceof Double) {
            valueNumberDouble = (float)((Double)o).doubleValue();
            valueString = valueNumberDouble + "";
            type = Type.NUMBER;
            typeNumber = TypeNumber.DOUBLE;
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
    
    private Object invokeNative(Object value, String script, ScriptCommand scriptCommand) throws CajuScriptException {
        try {
            Class c = null;
            String cName = "";
            if (scriptCommand.getClassReference() == null || (scriptCommand.getMethod() == null && scriptCommand.getConstructor() == null && scriptCommand.getParamName().equals(""))) {
                if (script == null || script.equals("")) {
                    return value;
                }
                String realClassName = "";
                SyntaxPosition syntaxPathSeparator = syntax.matcherPosition(script, syntax.getFunctionCallPathSeparator());
                if (syntaxPathSeparator.getStart() == 0) {
                    script = script.substring(syntaxPathSeparator.getEnd());
                }
                script = script.trim();
                String path = "";
                String realPath = "";
                int p = -1;
                String scriptRest = script;

                String scriptPart = "";
                while (true) {
                    p++;
                    syntaxPathSeparator = syntax.matcherPosition(scriptRest, syntax.getFunctionCallPathSeparator());
                    if (syntaxPathSeparator.getStart() > -1) {
                        scriptPart = scriptRest.substring(0, syntaxPathSeparator.getStart());
                        scriptRest = scriptRest.substring(syntaxPathSeparator.getEnd());
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
                        throw new Exception("Cannot invoke " + realClassName);
                    }
                    SyntaxPosition syntaxParameterBegin = syntax.matcherPosition(scriptPart, syntax.getFunctionCallParametersBegin());
                    if (syntaxParameterBegin.getStart() > -1 && value == null) {
                        cName = scriptPart.substring(0, syntaxParameterBegin.getStart());
                        realPath += cName;
                        cName = cName.trim();
                        path += cName;
                    } else if (syntaxParameterBegin.getStart() > -1 && value != null) {
                        cName = scriptPart.substring(0, syntaxParameterBegin.getStart());
                        realPath += cName;
                        cName = cName.trim();
                        path += cName;
                    } else {
                        path += scriptPart.trim();
                        realPath += scriptPart;
                    }
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
                            boolean isRootContext = false;
                            Value _value = context.getVar(path);
                            if (_value == null) {
                                isRootContext = true;
                                _value = cajuScript.getVar(path);
                            }
                            if (_value != null) {
                                scriptCommand.setValue(_value);
                                //scriptCommand.setObject(path);
                                if (isRootContext) {
                                    scriptCommand.setType(ScriptCommand.Type.NATIVE_OBJECT_ROOT);
                                } else {
                                    scriptCommand.setType(ScriptCommand.Type.NATIVE_OBJECT);
                                }
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
                    scriptCommand.setClassPath(c.getName());
                    scriptCommand.setClassReference(c);
                    break;
                }
                if (!cName.equals("") && value == null) {
                    Object[] values = invokeValues(scriptPart.substring(cName.length()), scriptCommand);
                    return invokeConstructor(c, values, script, scriptCommand);
                } else if (!cName.equals("") && value != null) {
                    Object[] values = invokeValues(script, scriptCommand);
                    //script = script.substring(values[values.length - 1].toString().length());
                    return invokeMethod(c, value, cName, values, script, scriptCommand);
                } else {
                    syntaxPathSeparator = syntax.matcherPosition(script, syntax.getFunctionCallPathSeparator());
                    if (syntaxPathSeparator.getStart() == 0) {
                        script = script.substring(syntaxPathSeparator.getEnd());
                    }
                    syntaxPathSeparator = syntax.matcherPosition(script, syntax.getFunctionCallPathSeparator());
                    SyntaxPosition syntaxParameterBegin = syntax.matcherPosition(script, syntax.getFunctionCallParametersBegin());
                    boolean isMethod = false;
                    if (syntaxParameterBegin.getStart() > -1 && (syntaxParameterBegin.getStart() < syntaxPathSeparator.getStart() || syntaxPathSeparator.getStart() == -1)) {
                        isMethod = true;
                        scriptPart = script.substring(0, syntaxParameterBegin.getStart());
                    } else {
                        scriptPart = script;
                    }
                    syntaxPathSeparator = syntax.matcherPosition(script, syntax.getFunctionCallPathSeparator());
                    if (!isMethod || syntaxPathSeparator.getStart() > -1) {
                        String paramName = scriptPart;
                        if (syntaxPathSeparator.getStart() > -1) {
                            paramName = scriptPart.substring(0, syntaxPathSeparator.getStart());
                        }
                        script = script.substring(paramName.length());
                        if (value != null) {
                            scriptCommand.setParamName(paramName);
                            ScriptCommand sc = new ScriptCommand(script, ScriptCommand.Type.NATIVE_OBJECT);
                            Object oParam = c.getField(paramName).get(value);
                            sc.setClassReference(oParam.getClass());
                            sc.setClassPath(oParam.getClass().getName());
                            Object o = invokeNative(oParam, script, sc);
                            scriptCommand.setNextScriptCommand(sc);
                            return o;
                            //return invokeNative(value.getClass().getField(paramName).get(value), script, scriptCommand);
                        }
                        if (paramName.equals("class")) {
                            return invokeNative(c, script, scriptCommand);
                        } else {
                            scriptCommand.setParamName(paramName);
                            ScriptCommand sc = new ScriptCommand(script, ScriptCommand.Type.NATIVE_OBJECT);
                            Object oParam = c.getField(paramName).get(c);
                            sc.setClassReference(oParam.getClass());
                            sc.setClassPath(oParam.getClass().getName());
                            Object o = invokeNative(oParam, script, sc);
                            scriptCommand.setNextScriptCommand(sc);
                            return o;
                        }
                    } else {
                        Object[] values = invokeValues(script.substring(syntaxParameterBegin.getStart()), scriptCommand);
                        String propName = script.substring(0, syntaxParameterBegin.getStart());
                        //script = script.substring(propName.length() + values[values.length - 1].toString().length());
                        return invokeMethod(c, value, propName, values, script, scriptCommand);
                    }
                }
            } else {
                c = scriptCommand.getClassReference();
                if (scriptCommand.getMethod() != null) {
                    Object[] values = invokeValues(null, scriptCommand);
                    return invokeMethod(c, value, null, values, null, scriptCommand);
                } else if (scriptCommand.getConstructor() != null) {
                    Object[] values = invokeValues(null, scriptCommand);
                    return invokeConstructor(c, values, script, scriptCommand);
                } else if (!scriptCommand.getParamName().equals("")) {
                    return invokeNative(c.getField(scriptCommand.getParamName()).get(c), script, scriptCommand.getNextScriptCommand());
                }
                throw new Exception("Cannot invoke " + scriptCommand.getScript());
                //cName = scriptCommand.getClassPath();
                //scriptPart = scriptCommand.getScript();
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(cajuScript, context, e.getMessage(), e);
        }
    }
    
    private boolean foundMethod(Object[] values, Class[] cx, boolean allowAutoPrimitiveCast, ScriptCommand scriptCommand) {
        int count = 0;
        for (int x = 0; x < values.length; x++) {
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
        if (count == cx.length && values.length == count) {
            return true;
        } else {
            return false;
        }
    }
    
    private Object invokeConstructor(Class c, Object[] values, String script, ScriptCommand scriptCommand) throws Exception {
        if (scriptCommand.getConstructor() != null) {
            return scriptCommand.getConstructor().newInstance(getParams(values, scriptCommand.getConstructor().getParameterTypes(), scriptCommand));
        }
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
            if (values.length != cx.length) {
                continue;
            }
            if (foundMethod(values, cx, allowAutoPrimitiveCast, scriptCommand)) {
                scriptCommand.setConstructor(cn[x]);
                scriptCommand.setType(ScriptCommand.Type.NATIVE_OBJECT);
                return cn[x].newInstance(getParams(values, cx, scriptCommand));
            }
        }
        throw new Exception("Constructor \""+ c.getName() +"\" cannot be invoked");
    }
    
    private Object invokeMethod(Class c, Object o, String name, Object[] values, String script, ScriptCommand scriptCommand) throws Exception {
        if (scriptCommand.getMethod() != null) {
            return scriptCommand.getMethod().invoke(o, getParams(values, scriptCommand.getMethod().getParameterTypes(), scriptCommand));
        }
        Class[] classes = null;
        if (c.isMemberClass()) {
            Class[] interfaces = c.getInterfaces();
            classes = new Class[interfaces.length + 1];
            for (int i = 0; i < interfaces.length; i++) {
                classes[i] = interfaces[i];
            }
            classes[classes.length - 1] = c.getSuperclass();
        } else {
            classes = new Class[] {c};
        }
        for (Class cls : classes) {
            Method[] mt = cls.getMethods();
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
                if (values.length != cx.length) {
                    continue;
                }
                if (foundMethod(values, cx, allowAutoPrimitiveCast, scriptCommand)) {
                    scriptCommand.setMethod(mt[x]);
                    return mt[x].invoke(o, getParams(values, cx, scriptCommand));
                }
            }
        }
        throw new Exception("Method \""+ name +"\" cannot be invoked");
    }
    
    private Object[] invokeValues(String script, ScriptCommand scriptCommand) throws CajuScriptException {
        if (scriptCommand.getParams() != null) {
            Value[] paramsVal = scriptCommand.getParams();
            Object[] values = new Object[paramsVal.length];
            for (int x = 0; x < paramsVal.length; x++) {
                values[x] = paramsVal[x].getValue();
            }
            return values;
        }
        int lenBegin = (syntax.matcherPosition(script, syntax.getFunctionCallParametersBegin())).getEnd();
        int lenEnd = (syntax.matcherPosition(script, syntax.getFunctionCallParametersEnd())).getStart();
        String params = script.substring(lenBegin, lenEnd);
        if (params.trim().equals("")) {
            scriptCommand.setParams(new Value[0]);
            return new Object[0];
        }
        String[] paramsKeys = syntax.getFunctionCallParametersSeparator().split(params);
        Value[] paramsVal = new Value[paramsKeys.length];
        Object[] values = new Object[paramsKeys.length];
        for (int x = 0; x < paramsKeys.length; x++) {
            SyntaxPosition syntaxRootContext = syntax.matcherPosition(paramsKeys[x], syntax.getRootContext());
            if (syntaxRootContext.getStart() == 0) {
                paramsKeys[x] = paramsKeys[x].substring(syntaxRootContext.getEnd());
                values[x] = cajuScript.getVar(paramsKeys[x]);
            } else {
                values[x] = context.getVar(paramsKeys[x]);
                if (values[x] == null) {
                    values[x] = cajuScript.getVar(paramsKeys[x]);
                }
            }
            paramsVal[x] = (Value)values[x];
            values[x] = paramsVal[x].getValue();
        }
        scriptCommand.setParams(paramsVal);
        return values;
    }
    
    private Object[] getParams(Object[] values, Class[] cx, ScriptCommand scriptCommand) throws Exception {
        if (values.equals(paramsValues)) {
            return paramsFinal;
        }
        Object[] params = new Object[cx.length];
        for (int x = 0; x < cx.length; x++) {
            if (paramsValues != null && paramsValues[x].equals(values[x])) {
                params[x] = paramsValues[x];
            }
            if (CajuScript.isPrimitiveType(cx[x].getName())) {
                params[x] = cajuScript.cast(values[x], cx[x].getName());
            } else {
                params[x] = cx[x].cast(values[x]);
            }
        }
        paramsValues = values;
        paramsFinal = params;
        return params;
    }
    
    /**
     * Clone.
     * @return Object cloned.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Cannot clone this object.");
        }
    }
}

/**
 * To cache data from commands executed.
 * @author eduveks
 */
class ScriptCommand {
    /**
     * Type of script commands.
     */
    public static enum Type {
        VARIABLE, VARIABLE_ROOT, FUNCTION, NATIVE_CLASS, NATIVE_OBJECT, NATIVE_OBJECT_ROOT
    }
    private String script = "";
    private Type type = null;
    private Value[] params = null;
    private String function = "";
    private String classPath = "";
    private String paramName = "";
    private Class classReference = null;
    private String object = "";
    private Value value = null;
    private Constructor constructor = null;
    private Method method = null;
    private ScriptCommand nextScriptCommand = null;
    
    /**
     * Create new script command with an script and type.
     * @param script Script.
     * @param type Type.
     */
    public ScriptCommand(String script, Type type) {
        this.script = script;
        this.type = type;
    }
    
    /**
     * Get the script.
     * @return Script.
     */
    public String getScript() {
        return script;
    }
    
    /**
     * Set the script.
     * @param script Script.
     */
    public void setScript(String script) {
        this.script = script;
    }
    
    /**
     * Get the type of command.
     * @return Type.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Set the type of command.
     * @param type Type.
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Set function configuration.
     * @param function Function name.
     * @param params Parameters names.
     */
    public void setFunction(String function, Value[] params) {
        this.function = function;
        this.params = params;
    }
    
    /**
     * Set static method configuration.
     * @param classPath Class path.
     * @param method Method.
     * @param params Parameters.
     */
    public void setStatic(String classPath, Method method, Value[] params) {
        this.classPath = classPath;
        this.method = method;
        this.params = params;
    }
    
    /**
     * Set method configuration.
     * @param object Name of object.
     * @param method Method.
     * @param params Parameters.
     */
    public void setMethod(String object, Method method, Value[] params) {
        this.object = object;
        this.method = method;
        this.params = params;
    }
    
    /**
     * Set newly instance configuration.
     * @param classPath Class path.
     * @param constructor Constructor.
     * @param params Parameters.
     */
    public void setNewInstance(String classPath, Constructor constructor, Value[] params) {
        this.classPath = classPath;
        this.constructor = constructor;
        this.params = params;
    }

    /**
     * Get class reference.
     * @return Class reference.
     */
    public Class getClassReference() {
        return classReference;
    }

    /**
     * Set class reference.
     * @param classReference Class reference.
     */
    public void setClassReference(Class classReference) {
        this.classReference = classReference;
    }

    /**
     * Get constructor.
     * @return Constructor.
     */
    public Constructor getConstructor() {
        return constructor;
    }

    /**
     * Set constructor.
     * @param constructor Constructor.
     */
    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }

    /**
     * Get function.
     * @return Function.
     */
    public String getFunction() {
        return function;
    }

    /**
     * Set function.
     * @param function Function.
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * Get method.
     * @return Method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Set method.
     * @param method Method.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Get parameters.
     * @return Parameters.
     */
    public Value[] getParams() {
        return params;
    }

    /**
     * Set parameters.
     * @param params Parameters.
     */
    public void setParams(Value[] params) {
        this.params = params;
    }

    /**
     * Get class path.
     * @return Class path.
     */
    public String getClassPath() {
        return classPath;
    }

    /**
     * Set class path.
     * @param classPath Class path.
     */
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    /**
     * Get value.
     * @return Value.
     */
    public Value getValue() {
        return value;
    }

    /**
     * Set value.
     * @param value Value.
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Get next script command. Used if have others script commands in sequence.
     * @return Script command.
     */
    public ScriptCommand getNextScriptCommand() {
        return nextScriptCommand;
    }

    /**
     * Set next script command. Used if have others script commands in sequence.
     * @param nextScriptCommand Script command.
     */
    public void setNextScriptCommand(ScriptCommand nextScriptCommand) {
        this.nextScriptCommand = nextScriptCommand;
    }

    /**
     * Get parameter name.
     * @return Script Parameter name.
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * Set parameter name.
     * @param paramName Parameter name.
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}