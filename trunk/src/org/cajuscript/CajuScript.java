/*
 * CajuScript.java
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
import org.cajuscript.parser.LineDetail;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.cajuscript.parser.Function;
import org.cajuscript.parser.Base;
/**
 * The core of the <code>CajuScript</code> language.
 * <p>Sample:</p>
 * <p><blockquote><pre>
 * try {
 *     CajuScript caju = new CajuScript();
 *     String javaHello = "Java: Hello!";
 *     caju.set("javaHello", javaHello);
 *     String script = "$java.lang;";
 *     script += "System.out.println(javaHello);";
 *     script += "cajuHello = \"Caju: Hi!\";";
 *     caju.eval(script);
 *     System.out.println(caju.get("cajuHello"));
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * </pre></blockquote></p>
 * <p>To execute a file:</p>
 * <p><blockquote><pre>
 * try {
 *     CajuScript caju = new CajuScript();
 *     caju.evalFile("file.cj");
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * </pre></blockquote></p>
 * @author eduveks
 */
public class CajuScript {
    /**
     * Core name.
     */
    public static final String NAME = "CajuScript";
    /**
     * Core version.
     */
    public static final String VERSION = "0.2";
    /**
     * Language version.
     */
    public static final String LANGUAGE_VERSION = "0.2";
    /**
     * Line Limiter.
     */
    public static final String LINE_LIMITER = "\n";
    /**
     * SubLine Limiter.
     */
    public static final String SUBLINE_LIMITER = ";";
    /**
     * Prefix of the variables created automaticaly.
     */
    public static final String CAJU_VARS = "__caju";
    /**
     * Strings along the code are replaced by statics variables with this name.
     */
    public static final String CAJU_VARS_STATIC_STRING = CAJU_VARS + "_static_string_";
    /**
     * Commands embraced by parenthesis are executed and the value is saved on
     * variables with this name. All parenthesis are replaced by variables with
     * the final value when the line is interpreted.
     */
    public static final String CAJU_VARS_GROUP = CAJU_VARS + "_group_";
    /**
     * Functions parameters are going to variables setting with this name.
     */
    public static final String CAJU_VARS_PARAMETER = CAJU_VARS + "_param_";
    private static Map<String, Syntax> globalSyntaxs = new HashMap<String, Syntax>();
    private Context context = new Context();
    private LineDetail runningLine = new LineDetail(0, "");
    private Syntax syntax = new Syntax();
    private Map<String, Syntax> syntaxs = new HashMap<String, Syntax>();
    private static Map<String, String> cacheScripts = new HashMap<String, String>();
    private static Map<String, Base> cacheParsers = new HashMap<String, Base>();
    private static Map<String, Context> cacheStaticContexts = new HashMap<String, Context>();
    /**
     * Create a newly instance of Caju Script. The variables caju and array
     * are initialized.
     * @throws org.cajuscript.CajuScriptException Problems on starting.
     */
    public CajuScript() throws CajuScriptException {
        context.setVar("caju", toValue(this));
        context.setVar("array", toValue(new Array()));
    }
    /**
     * Add custom syntax for all instances of CajuScript.
     * @param name Syntax name.
     * @param syntax Syntax instance.
     */
    public static void addGlobalSyntax(String name, Syntax syntax) {
        globalSyntaxs.put(name, syntax);
    }
    /**
     * Get global custom syntax by name.
     * @param name Syntax name.
     * @return Syntax instance.
     */
    public static Syntax getGlobalSyntax(String name) {
        return globalSyntaxs.get(name);
    }
    /**
     * Get default syntax.
     * @return Syntax.
     */
    public Syntax getSyntax() {
        return syntax;
    }
    /**
     * Set default syntax.
     * @param s Syntax.
     */
    public void setSyntax(Syntax s) {
        this.syntax = s;
    }
    /**
     * Add custom syntax.
     * @param name Syntax name.
     * @param syntax Syntax instance.
     */
    public void addSyntax(String name, Syntax syntax) {
        syntaxs.put(name, syntax);
    }
    /**
     * Get custom syntax by name.
     * @param name Syntax name.
     * @return Syntax instance.
     */
    public Syntax getSyntax(String name) {
        return syntaxs.get(name);
    }
    /**
     * Get root context.
     * @return Context.
     */
    public Context getContext() {
        return context;
    }
    /**
     * Set root context.
     * @param c Context.
     */
    public void setContext(Context c) {
        this.context = c;
    }
    /**
     * Get line detail in execution.
     * @return Line detail.
     */
    public LineDetail getRunningLine() {
        return runningLine;
    }
    /**
     * Set line detail in execution.
     * @param l Line detail.
     */
    public void setRunningLine(LineDetail l) {
        runningLine = l;
    }
    /**
     * Script execute.
     * @param script Script to be executed.
     * @return Value returned by script.
     * @throws org.cajuscript.CajuScriptException Errors ocurred on script execution.
     */
    public Value eval(String script) throws CajuScriptException {
        return eval(script, syntax);
    }
    /**
     * Script execute with specific syntax.
     * @param script Script to be executed.
     * @param syntax Syntax of the script.
     * @return Value returned by script.
     * @throws org.cajuscript.CajuScriptException Errors ocurred on script execution.
     */
    public Value eval(String script, Syntax syntax) throws CajuScriptException {
        String originalScript = script;
        if (script.equals("")) {
            return null;
        }
        script += LINE_LIMITER;
        script = script.replace((CharSequence)"\r\n", LINE_LIMITER);
        script = script.replace((CharSequence)"\n\r", LINE_LIMITER);
        script = script.replace((CharSequence)"\n", LINE_LIMITER);
        script = script.replace((CharSequence)"\r", LINE_LIMITER);
        String[] lines = script.split(LINE_LIMITER);
        script = "";
        StringBuffer scriptBuffer = new StringBuffer();
        String staticStringKey = "";
        String staticStringValue = "";
        String previousLine = "";
        boolean isString1 = false;
        boolean isString2 = false;
        int lineNumber = 0;
        String cacheId = "";
        boolean config = true;
        lines: for (String line : lines) {
            line = line.trim();
            lineNumber++;
            if (config) {
                while (true) {
                    int lineLimiter = line.indexOf(SUBLINE_LIMITER);
                    String configLine = line;
                    if (lineLimiter > -1) {
                        configLine = configLine.substring(0, lineLimiter);
                    }
                    configLine = configLine.replace('\t', ' ').trim();
                    if (configLine.startsWith("caju.syntax")) {
                        String syntaxName = configLine.substring(configLine.lastIndexOf(' ') + 1);
                        Syntax _syntax = getSyntax(syntaxName);
                        Syntax __syntax = getGlobalSyntax(syntaxName);
                        if (_syntax != null) {
                            syntax = _syntax;
                        } else if (__syntax != null) {
                            syntax = __syntax;
                        } else {
                            throw CajuScriptException.create(this, context, "Syntax \""+ syntaxName +"\" not found.");
                        }
                    } else if (configLine.startsWith("caju.cache")) {
                        cacheId = configLine.substring(configLine.lastIndexOf(' ') + 1);
                        Base cacheParser = cacheParsers.get(cacheId);
                        String cacheScript = cacheScripts.get(cacheId);
                        if (cacheParser != null && cacheScript.equals(originalScript)) {
                            Context staticContexts = cacheStaticContexts.get(cacheId);
                            Set<String> keys = staticContexts.getAllKeys(true);
                            for (String key : keys) {
                                context.setVar(key, staticContexts.getVar(key));
                            }
                            return cacheParser.execute(this, context);
                        }
                    } else {
                        config = false;
                        break;
                    }
                    if (lineLimiter > -1) {
                        line = line.substring(lineLimiter + 1);
                    } else {
                        line = "";
                    }
                }
            }
            if (isString1 || isString2) {
                setRunningLine(new LineDetail(lineNumber, previousLine));
                throw CajuScriptException.create(this, context, "String not closed");
            }
            String lineGarbage = line.trim();
            if (lineGarbage.equals("")) {
                continue;
            }
            for (Pattern comment : syntax.getComments()) {
                Matcher m = comment.matcher(lineGarbage);
                if (m.find() && m.start() == 0) {
                    continue lines;
                } 
            }
            char[] chars = line.toCharArray();
            previousLine = line;
            isString1 = false;
            isString2 = false;
            char cO = (char)-1;
            for (char c : chars) {
                switch (c) {
                    case '\'':
                        if (cO != '\\' && !isString2) {
                            if (isString1) {
                                isString1 = false;
                                context.setVar(staticStringKey, new Value(this, getContext(), syntax, "'" + staticStringValue + "'"));
                                line = line.replace((CharSequence)("'" + staticStringValue + "'"), staticStringKey); 
                                staticStringKey = "";
                                staticStringValue = "";
                            } else {
                                isString1 = true;
                                staticStringKey = CAJU_VARS_STATIC_STRING + context.getVars().size();
                            }
                        } else if (isString2 || cO == '\\') {
                            staticStringValue += c;
                        }
                        break;
                    case '"':
                        if (cO != '\\' && !isString1) {
                            if (isString2) {
                                isString2 = false;
                                context.setVar(staticStringKey, new Value(this, getContext(), syntax, "\"" + staticStringValue + "\""));
                                line = line.replace((CharSequence)("\"" + staticStringValue + "\""), staticStringKey); 
                                staticStringKey = "";
                                staticStringValue = "";
                            } else {
                                isString2 = true;
                                staticStringKey = CAJU_VARS_STATIC_STRING + context.getVars().size();
                            }
                        } else if (isString1 || cO == '\\') {
                            staticStringValue += c;
                        }
                        break;
                    default:
                        if (isString1 || isString2) {
                            staticStringValue += c;
                        }
                        break;
                }
                cO = c;
            }
            scriptBuffer.append(">" + lineNumber + ":" + line);
            scriptBuffer.append(SUBLINE_LIMITER);
        }
        lines = scriptBuffer.toString().split(SUBLINE_LIMITER);
        scriptBuffer = new StringBuffer();
        for (String line : lines) {
            line = line.trim();
            String lineN = "";
            if (line.startsWith(">")) {
                lineN = line.substring(0, line.indexOf(":") + 1);
                line = line.substring(lineN.length()).trim();
            }
            int p = endLineIndex(line, syntax);
            if (p > -1) {
                String endLine = line.substring(p);
                if (!endLine.trim().equals("")) {
                    endLine += SUBLINE_LIMITER;
                }
                scriptBuffer.append(lineN + line.substring(0, p) + SUBLINE_LIMITER + endLine);
            } else {
                scriptBuffer.append(lineN + line + SUBLINE_LIMITER);
            }
        }
        script = scriptBuffer.toString();
        org.cajuscript.parser.Base base = new org.cajuscript.parser.Base(new LineDetail(-1, ""), syntax);
        base.parse(this, script, syntax);
        if (!cacheId.equals("")) {
            Context staticContexts = new Context();
            Set<String> keys = context.getAllKeys(true);
            for (String key : keys) {
                staticContexts.setVar(key, context.getVar(key));
            }
            cacheScripts.put(cacheId, originalScript);
            cacheParsers.put(cacheId, base);
            cacheStaticContexts.put(cacheId, staticContexts);
        }
        return base.execute(this, context);
    }
    private int endLineIndex(String line, Syntax syntax) {
        if (line.equals("")) {
            return -1;
        }
        int p = -1;
        if ((p = syntax.matcherPosition(line, syntax.getIf()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getElseIf()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getElse()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getLoop()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getFunction()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getTry()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getTryCatch()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getTryFinally()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getIfEnd()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getLoopEnd()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getFunctionEnd()).getEnd()) > -1) {
            return p;
        }
        if ((p = syntax.matcherPosition(line, syntax.getTryEnd()).getEnd()) > -1) {
            return p;
        }
        return -1;
    }
    /**
     * File exucute.
     * @param path File to be executed.
     * @return Value returned by script.
     * @throws org.cajuscript.CajuScriptException File cannot be executed or error ocurred on execution.
     */
    public Value evalFile(String path) throws CajuScriptException {
        return evalFile(path, syntax);
    }
    /**
     * File execute with specific syntax.
     * @param path File to be executed.
     * @param syntax Syntax of the script in file.
     * @return Value returned by script.
     * @throws org.cajuscript.CajuScriptException File cannot be executed or error ocurred on execution.
     */
    public Value evalFile(String path, Syntax syntax) throws CajuScriptException {
        java.io.InputStream is = null;
        try {
            is = new java.io.FileInputStream(path);
            byte[] b = new byte[is.available()];
            is.read(b);
            return eval(new String(b), syntax);
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(this, context, "Cannot read file \"" + e.getMessage() + "\"", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) { }
            }
        }
    }
    /**
     * Get function.
     * @param key Function name.
     * @return Function object.
     */
    public Function getFunc(String key) {
        return context.getFunc(key);
    }
    /**
     * Define a function.
     * @param key Function name.
     * @param func Object of the function.
     */
    public void setFunc(String key, Function func) {
        context.setFunc(key, func);
    }
    /**
     * Get variable value.
     * @param key Variable name.
     * @return Variable object.
     */
    public Value getVar(String key) {
        return context.getVar(key);
    }
    /**
     * Get all name of variables without variables created automaticaly by
     * CajuScript.
     * @return List of all variables names.
     */
    public Set<String> getAllKeys() {
        return getAllKeys(false);
    }
    /**
     * Get all name of variables including variables created automaticaly by
     * CajuScript if parameter are true.
     * @param withCajuVars Including variables created automaticaly by CajuScript or not.
     * @return List of all variables names.
     */
    public Set<String> getAllKeys(boolean withCajuVars) {
        return context.getAllKeys(withCajuVars);
    }
    /**
     * Setting new variable.
     * @param key Variable name.
     * @param value Variable value.
     */
    public void setVar(String key, Value value) {
        context.setVar(key.trim(), value);
    }
    /**
     * Convert Java objects to CajuScript object to be used like value of variables.
     * @param obj Object to be converted in Value.
     * @return New value generated from object.
     * @throws org.cajuscript.CajuScriptException Errors.
     */
    public Value toValue(Object obj) throws CajuScriptException {
        return toValue(obj, getContext(), getSyntax());
    }
    /**
     * Convert Java objects to CajuScript object to be used like value of variables.
     * @param obj Object to be converted in Value.
     * @param context Context for this value.
     * @param syntax Syntax for this value.
     * @return New value generated from object.
     * @throws org.cajuscript.CajuScriptException Errors.
     */
    public Value toValue(Object obj, Context context, Syntax syntax) throws CajuScriptException {
        Value v = new Value(this, context, syntax);
        v.setValue(obj);
        return v;
    }
    /**
     * Defining new variable and setting value from a Java object.
     * @param key Variable name.
     * @param value Variable value.
     * @throws org.cajuscript.CajuScriptException Errors.
     */
    public void set(String key, Object value) throws CajuScriptException {
        setVar(key.trim(), toValue(value));
    }
    /**
     * Getting value from variables to Java object.
     * @param key Variable name.
     * @return Variable value in Java.
     * @throws org.cajuscript.CajuScriptException Errors.
     */
    public Object get(String key) throws CajuScriptException {
        return getVar(key).getValue();
    }
    /**
     * Get list of all imports used by script in execution.
     * @return List of imports defined.
     */
    public List<String> getImports() {
        return context.getImports();
    }
    /**
     * Define a new import to be used. Only Java package.
     * @param i The content of importing is only Java package.
     */
    public void addImport(String i) {
        context.addImport(i);
    }
    /**
     * Remove import.
     * @param s Import content to be removed.
     */
    public void removeImport(String s) {
        context.removeImport(s);
    }
    /**
     * Convert value to type specified.
     * @param value Value to be converted.
     * @param type Types: "int" or "i", "long" or "l", "double" or "d",
     * "float" or "f", "char" or "c", "boolean" or "b", "byte" or "bt",
     * "string" or "s", "java.ANY_CLASS".
     * @return Object converted.
     * @throws org.cajuscript.CajuScriptException Errors ocurred on converting.
     */
    public Object cast(Object value, String type) throws Exception {
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("java.lang.Integer") || type.equalsIgnoreCase("i")) {
            if (value.toString().indexOf('.') > -1) {
                return Integer.valueOf((int)Double.valueOf(value.toString()).doubleValue());
            } else {
                return Integer.valueOf(value.toString());
            }
        }
        if (type.equalsIgnoreCase("long") || type.equalsIgnoreCase("java.lang.Long") || type.equalsIgnoreCase("l")) {
            return Long.valueOf(value.toString());
        }
        if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("java.lang.Double") || type.equalsIgnoreCase("d")) {
            return Double.valueOf(value.toString());
        }
        if (type.equalsIgnoreCase("float") || type.equalsIgnoreCase("java.lang.Float") || type.equalsIgnoreCase("f")) {
            return Float.valueOf(value.toString());
        }
        if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("java.lang.Character") || type.equalsIgnoreCase("c")) {
            try {
                return (char)Integer.valueOf(value.toString()).intValue();
            } catch (Exception e) {
                return value.toString().toCharArray()[0];
            }
        }
        if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("java.lang.Boolean") || type.equalsIgnoreCase("b")  || type.equalsIgnoreCase("bool")) {
            try {
                if (Integer.parseInt(cast(value, "i").toString()) > 0) {
                    return Boolean.valueOf(true);
                } else {
                    return Boolean.valueOf(false);
                }
            } catch (Exception e) {
                return Boolean.valueOf(value.toString());
            }
        }
        if (type.equalsIgnoreCase("byte") || type.equalsIgnoreCase("java.lang.Byte") || type.equalsIgnoreCase("bt")) {
            try {
                return (byte)Integer.valueOf(value.toString()).intValue();
            } catch (Exception e) {
                return value.toString().getBytes()[0];
            }
        }
        if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("java.lang.String") || type.equalsIgnoreCase("s")) {
            return value.toString();
        }
        return Class.forName(type).cast(value);
    }
    /**
     * Generate an exception.
     * @throws org.cajuscript.CajuScriptException Exception generated.
     */
    public static void error() throws CajuScriptException {
        throw new CajuScriptException();
    }
    /**
     * Generate an exception with custom message.
     * @param msg Message of the exception.
     * @throws org.cajuscript.CajuScriptException Exception generated.
     */
    public static void error(String msg) throws CajuScriptException {
        throw new CajuScriptException(msg);
    }
    /**
     * If two Object.getClass().getName() are from same type.
     * @param type1 First type, Object.getClass().getName().
     * @param type2 Second type, Object.getClass().getName().
     * @return Is from same type or not.
     */
    public static boolean isSameType(String type1, String type2) {
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
    /**
     * If Object.getClass().getName() is from primitive type.
     * @param type Object type, Object.getClass().getName().
     * @return Is from primitive type or not.
     */
    public static boolean isPrimitiveType(String type) {
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
    /**
     * If the object is instance of specified class.
     * @param o Object.
     * @param c Class.
     * @return If the object is instance of class or not.
     */
    public boolean isInstance(Object o, Class c) {
        if (c.isInstance(o)) {
            return true;
        } else {
            for (int y = 0; y < o.getClass().getClasses().length; y++) {
                if (isSameType(o.getClass().getClasses()[y].getName(), c.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Entry point to running.
     * @param args Arguments.
     */
    public static void main(String[] args) throws CajuScriptException {
        if (args.length > 0) {
            CajuScript caju = new CajuScript();
            caju.set("args", args);
            caju.evalFile(args[0]);
        }
    }
    static {
        Syntax syntaxJ = new Syntax();
        syntaxJ.setIf(Pattern.compile("if\\s*([\\s+|[\\s*\\(]].+)\\{"));
        syntaxJ.setElseIf(Pattern.compile("\\}else\\s+if\\s*([\\s+|[\\s*\\(]].+)\\{"));
        syntaxJ.setElse(Pattern.compile("\\}\\s*else\\s*\\{"));
        syntaxJ.setIfEnd(Pattern.compile("\\}"));
        syntaxJ.setLoop(Pattern.compile("while\\s*([\\s+|[\\s*\\(]].+)\\{"));
        syntaxJ.setLoopEnd(Pattern.compile("\\}"));
        syntaxJ.setTry(Pattern.compile("try\\s*([\\s+|[\\s*\\(]].+)\\{"));
        syntaxJ.setTryCatch(Pattern.compile("\\}\\s*catch\\s*\\{"));
        syntaxJ.setTryFinally(Pattern.compile("\\}\\s*finally\\s*\\{"));
        syntaxJ.setTryEnd(Pattern.compile("\\}"));
        syntaxJ.setFunction(Pattern.compile("function\\s*([\\s+|[\\s*\\(]].+)\\{"));
        syntaxJ.setFunctionEnd(Pattern.compile("\\}"));
        syntaxJ.setReturn(Pattern.compile("return"));
        syntaxJ.setImport(Pattern.compile("import\\s+"));
        syntaxJ.setRootContext(Pattern.compile("root."));
        syntaxJ.setContinue(Pattern.compile("continue"));
        syntaxJ.setBreak(Pattern.compile("break"));
        globalSyntaxs.put("CajuJava", syntaxJ);
        Syntax syntaxB = new Syntax();
        syntaxB.setIf(Pattern.compile("if\\s*([\\s+|[\\s*\\(]].+)\\s*"));
        syntaxB.setElseIf(Pattern.compile("elseif\\s*([\\s+|[\\s*\\(]].+)\\s*"));
        syntaxB.setElse(Pattern.compile("else"));
        syntaxB.setIfEnd(Pattern.compile("end"));
        syntaxB.setLoop(Pattern.compile("while\\s*([\\s+|[\\s*\\(]].+)\\s*"));
        syntaxB.setLoopEnd(Pattern.compile("end"));
        syntaxB.setTry(Pattern.compile("try\\s*([\\s+|[\\s*\\(]].+)\\s*"));
        syntaxB.setTryCatch(Pattern.compile("catch"));
        syntaxB.setTryFinally(Pattern.compile("finally"));
        syntaxB.setTryEnd(Pattern.compile("end"));
        syntaxB.setFunction(Pattern.compile("function\\s*([\\s+|[\\s*\\(]].+)\\s*"));
        syntaxB.setFunctionEnd(Pattern.compile("end"));
        syntaxB.setReturn(Pattern.compile("return"));
        syntaxB.setImport(Pattern.compile("import\\s+"));
        syntaxB.setRootContext(Pattern.compile("root."));
        syntaxB.setContinue(Pattern.compile("continue"));
        syntaxB.setBreak(Pattern.compile("break"));
        globalSyntaxs.put("CajuBasic", syntaxB);
    }
    
    @Override
    protected void finalize() throws Throwable {
        syntaxs.clear();
        syntaxs = null;
        context = null;
    }
}