/*
 * Reflection.java
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

package org.cajuscript.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.cajuscript.CajuScript;
import org.cajuscript.CajuScriptException;
import org.cajuscript.Context;
import org.cajuscript.Syntax;
import org.cajuscript.SyntaxPosition;
import org.cajuscript.Value;

/**
 * Java invoke with reflection.
 * @author eduveks
 */
public class Reflection {
    /**
     * Invoke native classes, methods and attributes.
     * @param cajuScript CajuScript instance
     * @param context Context
     * @param syntax Syntax
     * @param value Object value to be invoked
     * @param script Script command
     * @param scriptCommand ScriptCommand instance to save the procedure in cache
     * @return Object returned by invokation
     * @throws org.cajuscript.CajuScriptException Invocation exceptions
     */
    public static Object invokeNative(CajuScript cajuScript, Context context, Syntax syntax, Object value, String script, ScriptCommand scriptCommand) throws CajuScriptException {
        try {
            Class<?> c = null;
            String cName = "";
            if (scriptCommand.getClassReference() == null || (scriptCommand.getMethod() == null && scriptCommand.getConstructor() == null && scriptCommand.getParamName().length() == 0)) {
                if (script == null || script.length() == 0) {
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
                        path = path.concat(".");
                        realPath = realPath.concat(".");
                    }
                    if (realPath.endsWith("..")) {
                        realClassName = path.substring(0, path.lastIndexOf('.') - 1);
                        c = Class.forName(realClassName);
                    }
                    SyntaxPosition syntaxParameterBegin = syntax.matcherPosition(scriptPart, syntax.getFunctionCallParametersBegin());
                    if (syntaxParameterBegin.getStart() > -1 && value == null) {
                        cName = scriptPart.substring(0, syntaxParameterBegin.getStart());
                        realPath = realPath.concat(cName);
                        cName = cName.trim();
                        path = path.concat(cName);
                    } else if (syntaxParameterBegin.getStart() > -1 && value != null) {
                        cName = scriptPart.substring(0, syntaxParameterBegin.getStart());
                        realPath = realPath.concat(cName);
                        cName = cName.trim();
                        path = path.concat(cName);
                    } else {
                        path = path.concat(scriptPart.trim());
                        realPath = realPath.concat(scriptPart);
                    }
                    if (value == null) {
                        try {
                            if (context.getClassCache(path) != null) {
                                c = context.getClassCache(path);
                            } else {
                                try {
                                    c = Class.forName(path);
                                    context.setClassCache(path, c);
                                } catch (Throwable e) {
                                    for(String i : cajuScript.getImports()) {
                                        if (i.endsWith(path)) {
                                            try {
                                                c = Class.forName(i);
                                                context.setClassCache(path, c);
                                            } catch (Throwable ex) { }
                                        } else {
                                            try {
                                                c = Class.forName(i.concat(".").concat(path));
                                                context.setClassCache(path, c);
                                            } catch (Throwable ex) { }
                                        }
                                        if (c != null) {
                                            break;
                                        }
                                    }
                                    if (c == null) {
                                        throw e;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            if (!e.getMessage().equals(path)) {
                                throw e;
                            }
                            boolean isRootContext = false;
                            Value _value = context.getVar(path);
                            if (_value == null) {
                                isRootContext = true;
                                _value = cajuScript.getVar(path);
                            }
                            if (_value != null) {
                                scriptCommand.setVar(path);
                                scriptCommand.setValue(_value);
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
                if (cName.length() != 0 && value == null) {
                    Object[] values = invokeFunctionValues(cajuScript, context, syntax, scriptPart.substring(cName.length()), scriptCommand);
                    return invokeConstructor(cajuScript, c, values, script, scriptCommand);
                } else if (cName.length() != 0 && value != null) {
                    Object[] values = invokeFunctionValues(cajuScript, context, syntax, script, scriptCommand);
                    return invokeMethod(cajuScript, c, value, cName, values, script, scriptCommand);
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
                            sc.setVar(scriptCommand.getVar());
                            Object oParam = c.getField(paramName).get(value);
                            if (oParam != null) {
                                sc.setClassReference(oParam.getClass());
                                sc.setClassPath(oParam.getClass().getName());
                            }
                            sc.setValue(cajuScript.toValue(oParam));
                            Object o = invokeNative(cajuScript, context, syntax, oParam, script, sc);
                            scriptCommand.setNextScriptCommand(sc);
                            return o;
                        }
                        if (paramName.equals("class")) {
                            return invokeNative(cajuScript, context, syntax, c, script, scriptCommand);
                        } else {
                            try {
                                String enumName = c.getName().concat("$").concat(paramName);
                                Class.forName(enumName);
                                Object o = invokeNative(cajuScript, context, syntax, null, enumName.concat(script), scriptCommand);
                                return o;
                            } catch (Exception e) {
                                scriptCommand.setParamName(paramName);
                                ScriptCommand sc = new ScriptCommand(script, ScriptCommand.Type.NATIVE_OBJECT);
                                Object oParam = c.getField(paramName).get(c);
                                if (oParam != null) {
                                    sc.setClassReference(oParam.getClass());
                                    sc.setClassPath(oParam.getClass().getName());
                                }
                                Object o = invokeNative(cajuScript, context, syntax, oParam, script, sc);
                                scriptCommand.setNextScriptCommand(sc);
                                return o;
                            }
                        }
                    } else {
                        Object[] values = invokeFunctionValues(cajuScript, context, syntax, script.substring(syntaxParameterBegin.getStart()), scriptCommand);
                        String propName = script.substring(0, syntaxParameterBegin.getStart());
                        return invokeMethod(cajuScript, c, value, propName, values, script, scriptCommand);
                    }
                }
            } else {
                c = scriptCommand.getClassReference();
                if (scriptCommand.getMethod() != null) {
                    Object[] values = invokeFunctionValues(cajuScript, context, syntax, null, scriptCommand);
                    return invokeMethod(cajuScript, c, value, null, values, null, scriptCommand);
                } else if (scriptCommand.getConstructor() != null) {
                    Object[] values = invokeFunctionValues(cajuScript, context, syntax, null, scriptCommand);
                    return invokeConstructor(cajuScript, c, values, script, scriptCommand);
                } else if (scriptCommand.getParamName().length() != 0) {
                    if (scriptCommand.getValue() != null) {
                        return invokeNative(cajuScript, context, syntax, c.getField(scriptCommand.getParamName()).get(scriptCommand.getValue().getValue()), scriptCommand.getScript().substring(scriptCommand.getScript().indexOf(".".concat(script)) + script.length() + 1), scriptCommand.getNextScriptCommand());
                    } else {
                        return invokeNative(cajuScript, context, syntax, c.getField(scriptCommand.getParamName()).get(c), scriptCommand.getScript().substring(scriptCommand.getScript().indexOf(".".concat(script)) + script.length() + 1), scriptCommand.getNextScriptCommand());
                    }
                }
                throw new Exception("Cannot invoke ".concat(scriptCommand.getScript()));
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Throwable e) {
            throw CajuScriptException.create(cajuScript, context, e.getMessage(), e);
        }
    }

    private static boolean foundMethod(CajuScript cajuScript, Object[] values, Class<?>[] cx, boolean allowAutoPrimitiveCast, ScriptCommand scriptCommand) {
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

    private static Object invokeConstructor(CajuScript cajuScript, Class<?> c, Object[] values, String script, ScriptCommand scriptCommand) throws Exception {
        if (scriptCommand.getConstructor() != null) {
            return scriptCommand.getConstructor().newInstance(getParams(cajuScript, values, scriptCommand.getConstructor().getParameterTypes(), scriptCommand));
        }
        Constructor<?>[] cn = c.getDeclaredConstructors();
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
            Class<?> cx[] = cn[x].getParameterTypes();
            if (values.length != cx.length) {
                continue;
            }
            if (foundMethod(cajuScript, values, cx, allowAutoPrimitiveCast, scriptCommand)) {
                scriptCommand.setConstructor(cn[x]);
                scriptCommand.setType(ScriptCommand.Type.NATIVE_OBJECT);
                return cn[x].newInstance(getParams(cajuScript, values, cx, scriptCommand));
            }
        }
        throw new Exception("Constructor \"".concat(c.getName()).concat("\" cannot be invoked"));
    }

    private static Object invokeMethod(CajuScript cajuScript, Class<?> c, Object o, String name, Object[] values, String script, ScriptCommand scriptCommand) throws Exception {
        if (scriptCommand.getMethod() != null) {
            Object r = scriptCommand.getMethod().invoke(o, getParams(cajuScript, values, scriptCommand.getMethod().getParameterTypes(), scriptCommand));
            return r;
        }
        Class<?>[] classes = null;
        if (c.isMemberClass()) {
            Class<?>[] interfaces = c.getInterfaces();
            classes = new Class[interfaces.length + 1];
            for (int i = 0; i < interfaces.length; i++) {
                classes[i] = interfaces[i];
            }
            classes[classes.length - 1] = c.getSuperclass();
        } else {
            classes = new Class[] {c};
        }
        for (Class<?> cls : classes) {
            Method[] mt = cls.getMethods();
            boolean allowAutoPrimitiveCast = true;
            for (int i = 0; i < mt.length; i++) {
                if (i == 0) {
                   allowAutoPrimitiveCast = !allowAutoPrimitiveCast;
                }
                int x = i;
                if (i == mt.length -1 && !allowAutoPrimitiveCast) {
                    i = -1;
                }
                if (!mt[x].getName().equals(name)) {
                    continue;
                }
                Class<?>[] cx = mt[x].getParameterTypes();
                if (values.length != cx.length) {
                    continue;
                }
                if (foundMethod(cajuScript, values, cx, allowAutoPrimitiveCast, scriptCommand)) {
                    scriptCommand.setMethod(mt[x]);
                    scriptCommand.setType(ScriptCommand.Type.NATIVE_OBJECT);
                    return mt[x].invoke(o, getParams(cajuScript, values, cx, scriptCommand));
                }
            }
        }
        throw new Exception("Method \"".concat(name).concat("\" cannot be invoked"));
    }

    /**
     * Catch values from function syntax to be used into invocations.
     * @param cajuScript CajuScript instance.
     * @param context Context
     * @param syntax Syntax
     * @param script Parameters script
     * @param scriptCommand ScriptCommand instance to save the procedure in cache
     * @return Objects values from the parameters.
     * @throws org.cajuscript.CajuScriptException Catching values exception
     */
    public static Object[] invokeFunctionValues(CajuScript cajuScript, Context context, Syntax syntax, String script, ScriptCommand scriptCommand) throws CajuScriptException {
        return invokeValues(cajuScript, context, syntax, script, scriptCommand, syntax.getFunctionCallParametersBegin(), syntax.getFunctionCallParametersSeparator(), syntax.getFunctionCallParametersEnd());
    }

    /**
     * Catch values from array syntax to be used into invocations.
     * @param cajuScript CajuScript instance.
     * @param context Context
     * @param syntax Syntax
     * @param script Parameters script
     * @param scriptCommand ScriptCommand instance to save the procedure in cache
     * @return Objects values from the parameters.
     * @throws org.cajuscript.CajuScriptException Catching values exception
     */
    public static Object[] invokeArrayValues(CajuScript cajuScript, Context context, Syntax syntax, String script, ScriptCommand scriptCommand) throws CajuScriptException {
        return invokeValues(cajuScript, context, syntax, script, scriptCommand, syntax.getArrayCallParametersBegin(), syntax.getArrayCallParametersSeparator(), syntax.getArrayCallParametersEnd());
    }

    /**
     * Catch values to be used into invocations.
     * @param cajuScript CajuScript instance.
     * @param context Context
     * @param syntax Syntax
     * @param script Parameters script
     * @param scriptCommand ScriptCommand instance to save the procedure in cache
     * @param parametersBegin Parameters begin syntax
     * @param parametersSeparator Parameters separator syntax
     * @param parametersEnd Parameters end syntax
     * @return Objects values from the parameters.
     * @throws org.cajuscript.CajuScriptException Catching values exception
     */
    public static Object[] invokeValues(CajuScript cajuScript, Context context, Syntax syntax, String script, ScriptCommand scriptCommand, Pattern parametersBegin, Pattern parametersSeparator, Pattern parametersEnd) throws CajuScriptException {
        if (scriptCommand.getParams() != null) {
            String[] paramsVal = scriptCommand.getParams();
            Object[] values = new Object[paramsVal.length];
            for (int x = 0; x < paramsVal.length; x++) {
                values[x] = context.getVar(paramsVal[x]).getValue();
            }
            return values;
        }
        int lenBegin = (syntax.matcherPosition(script, parametersBegin)).getEnd();
        int lenEnd = (syntax.matcherPosition(script, parametersEnd)).getStart();
        String params = script.substring(lenBegin, lenEnd);
        if (params.trim().length() == 0) {
            scriptCommand.setParams(new String[0]);
            return new Object[0];
        }
        String[] paramsKeys = parametersSeparator.split(params);
        String[] paramsVal = new String[paramsKeys.length];
        Object[] values = new Object[paramsKeys.length];
        for (int x = 0; x < paramsKeys.length; x++) {
            /*SyntaxPosition syntaxRootContext = syntax.matcherPosition(paramsKeys[x], syntax.getRootContext());
            if (syntaxRootContext.getStart() == 0) {
                paramsKeys[x] = paramsKeys[x].substring(syntaxRootContext.getEnd());
                values[x] = cajuScript.getVar(paramsKeys[x]);
            } else {
                values[x] = context.getVar(paramsKeys[x]);
                if (values[x] == null) {
                    values[x] = cajuScript.getVar(paramsKeys[x]);
                }
            }*/
            paramsVal[x] = paramsKeys[x];
            values[x] = context.getVar(paramsKeys[x]).getValue();
        }
        scriptCommand.setParams(paramsVal);
        return values;
    }

    private static Object[] getParams(CajuScript cajuScript, Object[] values, Class<?>[] cx, ScriptCommand scriptCommand) throws Exception {
        if (Arrays.equals(values, scriptCommand.getParamsValues())) {
            return scriptCommand.getParamsFinal();
        }
        Object[] params = new Object[cx.length];
        for (int x = 0; x < cx.length; x++) {
            if (scriptCommand.getParamsValues() != null && scriptCommand.getParamsValues()[x].equals(values[x])) {
                params[x] = scriptCommand.getParamsValues()[x];
            }
            if (CajuScript.isPrimitiveType(cx[x].getName())) {
                params[x] = cajuScript.cast(values[x], cx[x].getName());
            } else {
                params[x] = cx[x].cast(values[x]);
            }
        }
        scriptCommand.setParamsValues(values);
        scriptCommand.setParamsFinal(params);
        return params;
    }
}
