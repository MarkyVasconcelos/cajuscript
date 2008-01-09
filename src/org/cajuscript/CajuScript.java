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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
/**
 * The core of the CajuScript language.
 * <p>Sample:</p>
 * <p><blockquote><pre>
 * try {
 *     CajuScript caju = new CajuScript();
 *     String javaHello = "Java told for the Caju: Hello!";
 *     caju.set("javaHello", javaHello);
 *     String script = "$java.lang;";
 *     script += "System.out.println(javaHello);";
 *     script += "cajuHello = \"Caju told for the Java: Hi!\";";
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
 * <p>CajuScript syntax:</p>
 * <p><blockquote><pre>
 * \ Imports
 * $java.lang
 * 
 * \ Defining a new variable
 * x = 0
 * 
 * \ LOOP
 * x &lt; 10 & x >= 0 &#64;
 *    System.out.println(x)
 *    x = x + 1
 * &#64;
 * 
 * \ IF
 * x &lt; 10 | x &gt; 0 ?
 *     System.out.println("X is less than 10!")
 * ? x &gt; 10 & x = 10 ?
 *     System.out.println("X is greater than 10!")
 * ??
 *     System.out.println("X = "+ x)
 * ?
 * 
 * \ FUNCTION
 * \ Allowed:
 * \ addWithX v1, v2 # ... #
 * \ addWithX(v1 v2) # ... #
 * addWithX(v1, v2) #
 *     \ "~" is the return
 *     ~ x + v1 + v2
 * #
 * 
 * x = addWithX(1, 2)
 * 
 * System.out.println("X = "+ x)
 * </pre></blockquote></p>
 * <p>Null value:</p>
 * <p><blockquote><pre>
 * \ $ is the null value
 * x = $
 * </pre></blockquote></p>
 * <p>Arithmetic Operators:</p>
 * <p><blockquote><pre>
 * \ + Addition
 * x = 0 + 1
 * x += 1
 * 
 * \ - Subtraction
 * x = 0 - 1
 * x -= 1
 * 
 * \ * Multiplication
 * x = 0 * 1
 * x *= 1
 * 
 * \ / Division
 * x = 0 / 1
 * x /= 1
 * 
 * \ % Modulus
 * x = 0 % 1
 * x %= 1
 * </pre></blockquote></p>
 * <p>Comparison Operators:</p>
 * <p><blockquote><pre>
 * \ = Equal to
 * (x = y)
 * 
 * \ ! Not equal to
 * (x ! y)
 * 
 * \ &lt; Less Than
 * (x &lt; y)
 * 
 * \ &gt; Greater Than
 * (x &gt; y)
 * 
 * \ &lt; Less Than or Equal To
 * (x &lt;= y)
 * 
 * \ &gt; Greater Than or Equal To
 * (x &gt;= y)
 * </pre></blockquote></p>
 * <p>Imports or include file:</p>
 * <p><blockquote><pre>
 * \ Import
 * $java.lang
 * 
 * \ Include file
 * $"script.cj"
 * </pre></blockquote></p>
 * @author eduveks
 */
public class CajuScript {
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
     * Strings along the code are replaced by statics variables whit this name.
     */
    public static final String CAJU_VARS_STATIC_STRING = CAJU_VARS + "_static_string_";
    /**
     * Commands embraced by parenthesis are executed and the value is saved on
     * variables whit this name. All parenthesis are replaced by variables whit
     * the final value when the line is interpreted.
     */
    public static final String CAJU_VARS_GROUP = CAJU_VARS + "_group_";
    private List<String> imports = new ArrayList<String>();
    private Map<String, Value> vars = new HashMap<String, Value>();
    private Map<String, Func> funcs = new HashMap<String, Func>();
    private int lineNumber = 0;
    private String lineContent = "";
    /**
     * Create a newly instance of Caju Script. The variables caju and array
     * are initialized.
     * @throws org.cajuscript.CajuScriptException Problems on starting
     */
    public CajuScript() throws CajuScriptException {
        vars.put("caju", toValue(this));
        vars.put("array", toValue(new Array()));
    }
    /**
     * Get number of line in execution.
     * @return Line number
     */
    public int getLine() {
        return lineNumber;
    }
    /**
     * Set number of line in execution.
     * @param l Line number
     */
    public void setLine(int l) {
        lineNumber = l;
    }
    /**
     * Get content of line in execution.
     * @return Line content
     */
    public String getLineContent() {
        return lineContent;
    }
    /**
     * Excute a script.
     * @param script Script to be executed
     * @return Value returned by script
     * @throws org.cajuscript.CajuScriptException Errors ocurred on script execution
     */
    public Value eval(String script) throws CajuScriptException {
        String line = "";
        try {
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
            String contentCondition = "";
            String previousLine = "";
            boolean isString = false;
            boolean isCondition = false;
            int lineNumberBackup = getLine();
            for (String _line : lines) {
                lineContent = _line;
                line = _line;
                if (isString) {
                    throw CajuScriptException.create(this, "Sintax error", getLine(), previousLine);
                }
                lineNumber++;
                if (line.trim().startsWith("\\")) {
                    scriptBuffer.append(line + LINE_LIMITER);
                    continue;
                }
                char[] chars = line.toCharArray();
                previousLine = line;
                isString = false;
                char cO = (char)-1;
                for (char c : chars) {
                    switch (c) {
                    case '"':
                        if (cO != '\\') {
                            if (isString) {
                                isString = false;
                                vars.put(staticStringKey, new Value(this, "\"" + staticStringValue + "\""));
                                if (isCondition) {
                                    contentCondition += staticStringKey;
                                } else {
                                    scriptBuffer.append(staticStringKey);
                                }
                                staticStringKey = "";
                                staticStringValue = "";
                            } else {
                                isString = true;
                                staticStringKey = CAJU_VARS_STATIC_STRING + vars.size();
                            }
                        } else {
                            scriptBuffer.append(c);
                        }
                        break;
                    case '?':
                        if (isCondition) {
                            isCondition = false;
                            scriptBuffer.append("?" + contentCondition + "?" + SUBLINE_LIMITER);
                            contentCondition = "";
                        } else {
                            isCondition = true;
                        }
                        break;
                    default:
                        if (isString) {
                            staticStringValue += c;
                        } else if (isCondition) {
                            if (c == ';') {
                                isCondition = false;
                                scriptBuffer.append("?" + SUBLINE_LIMITER + contentCondition + SUBLINE_LIMITER);
                                contentCondition = "";
                            } else {
                                contentCondition += c;
                            }
                        //} else if (c == ';') {
                        //    script += LINE_LIMITER;
                        } else {
                            scriptBuffer.append(c);
                            if (!isString && (c == '?' || c == '@' || c == '#')) {
                                scriptBuffer.append(SUBLINE_LIMITER);
                            }
                        }
                        break;
                    }
                    cO = c;
                }
                if (isCondition) {
                    isCondition = false;
                    scriptBuffer.append("?" + SUBLINE_LIMITER + contentCondition);
                    contentCondition = "";
                }
                scriptBuffer.append(LINE_LIMITER);
            }
            script = scriptBuffer.toString();
            setLine(lineNumberBackup);
            lines = script.split(LINE_LIMITER);
            for (int y = 0; y < lines.length; y++) {
                lineNumber++;
                lineContent = lines[y];
                lines[y] = lines[y].trim();
                int commnetIndex = lines[y].indexOf("\\");
                if (commnetIndex == 0) {
                    continue;
                } else if (commnetIndex > -1) {
                    lines[y] = lines[y].substring(0, commnetIndex);
                }
                String[] subLines = lines[y].split("\\"+ SUBLINE_LIMITER);
                for (int x = 0; x < subLines.length; x++) {
                    line = subLines[x].trim();
                    if (line.startsWith("~")) {
                        if (line.length() == 1) {
                            return new Value(this);
                        } else {
                            return new Value(this, line.substring(1));
                        }
                    } else if (line.endsWith("?")) {
                        StringBuffer scriptIFCondition = new StringBuffer(line.substring(0, line.length() - 1));
                        StringBuffer scriptIF = new StringBuffer();
                        if (scriptIFCondition.indexOf("?") > -1) {
                            throw CajuScriptException.create(this, "Sintax error", line);
                        }
                        List<String> ifs = new ArrayList<String>();
                        int ifLineNumber = getLine();
                        int[] ifLineEnd = loadIfLine(ifs, ifLineNumber, subLines, x, scriptIFCondition, scriptIF, 0);
                        if (ifLineEnd[1] == 0) {
                            for (int z = y + 1; z < lines.length; z++) {
                                ifLineNumber++;
                                commnetIndex = lines[y].indexOf("\\");
                                if (commnetIndex == 0) {
                                    scriptIF.append(lines[z] + LINE_LIMITER);
                                    continue;
                                } else if (commnetIndex > -1) {
                                    lines[z] = lines[z].substring(0, commnetIndex);
                                }
                                subLines = lines[z].split("\\;");
                                ifLineEnd = loadIfLine(ifs, ifLineNumber, subLines, -1, scriptIFCondition, scriptIF, ifLineEnd[1]);
                                scriptIF.append(LINE_LIMITER);
                                if (ifLineEnd[0] != -1 && ifLineEnd[1] == -1) {
                                    x = ifLineEnd[0];
                                    y = z;
                                    break;
                                }
                            }
                        } else {
                            x = ifLineEnd[0];
                        }
                        for (String content : ifs) {
                            int p1 = content.indexOf("?");
                            int p2 = content.substring(p1 + 1).indexOf("?") + p1 + 1;
                            String _ifCondition = content.substring(0, p1);
                            String _ifLineNumber = content.substring(p1 + 1, p2);
                            String _ifContent = content.substring(p2 + 1);
                            _ifCondition = _ifCondition.trim();
                            if (((Boolean)cast(evalValue(_ifCondition), "b")).booleanValue()) {
                                setLine(Integer.valueOf(_ifLineNumber).intValue());
                                eval(_ifContent);
                                break;
                            }
                        }
                        setLine(ifLineNumber);
                    } else if (line.endsWith("@")) {
                        StringBuffer scriptLOOP = new StringBuffer();
                        int loopLineNumber = getLine();
                        int[] loopLineEnd = loadLoopLine(subLines, x, scriptLOOP, 0);
                        if (loopLineEnd[1] == 0) {
                            for (int z = y + 1; z < lines.length; z++) {
                                loopLineNumber++;
                                commnetIndex = lines[y].indexOf("\\");
                                if (commnetIndex == 0) {
                                    scriptLOOP.append(lines[z] + LINE_LIMITER);
                                    continue;
                                } else if (commnetIndex > -1) {
                                    lines[z] = lines[z].substring(0, commnetIndex);
                                }
                                subLines = lines[z].split("\\;");
                                loopLineEnd = loadLoopLine(subLines, -1, scriptLOOP, loopLineEnd[1]);
                                scriptLOOP.append(LINE_LIMITER);
                                if (loopLineEnd[0] != -1 && loopLineEnd[1] == -1) {
                                    x = loopLineEnd[0];
                                    y = z;
                                    break;
                                }
                            }
                        } else {
                            x = loopLineEnd[0];
                        }
                        line = line.substring(0, line.length() - 1);
                        lineNumberBackup = getLine();
                        while (((Boolean)cast(evalValue(line), "b")).booleanValue()) {
                            setLine(lineNumberBackup);
                            eval(scriptLOOP.toString());
                        }
                        setLine(loopLineNumber);
                    } else if (line.endsWith("#")) {
                        String scriptFuncDef = line.substring(0, line.length() - 1);
                        StringBuffer scriptFunc = new StringBuffer();
                        lineNumberBackup = getLine();
                        int loopLineNumber = getLine();
                        int funcLineEnd = loadFuncLine(subLines, x, scriptFunc);
                        if (funcLineEnd == -1) {
                            for (int z = y + 1; z < lines.length; z++) {
                                loopLineNumber++;
                                commnetIndex = lines[y].indexOf("\\");
                                if (commnetIndex == 0) {
                                    scriptFunc.append(lines[z] + LINE_LIMITER);
                                    continue;
                                } else if (commnetIndex > -1) {
                                    lines[z] = lines[z].substring(0, commnetIndex);
                                }
                                subLines = lines[z].split("\\;");
                                funcLineEnd = loadFuncLine(subLines, -1, scriptFunc);
                                scriptFunc.append(LINE_LIMITER);
                                if (funcLineEnd != -1) {
                                    x = funcLineEnd;
                                    y = z;
                                    break;
                                }
                            }
                        } else {
                            x = funcLineEnd;
                        }
                        Func func = new Func(this, scriptFuncDef, scriptFunc.toString(), lineNumberBackup);
                        funcs.put(func.getName(), func);
                    } else if (line.indexOf('=') > -1) {
                        try {
                            int p = line.indexOf('=');
                            String[] allKeys = line.substring(0, p).replaceAll(" ", "").split(",");
                            for (String key : allKeys) {
                                Value value = new Value(this, evalValue(line.substring(p + 1)));
                                if (key.endsWith("+") || key.endsWith("-") || key.endsWith("*") || key.endsWith("/") || key.endsWith("%")) {
                                    char s = key.substring(key.length() - 1).toCharArray()[0];
                                    key = key.substring(0, key.length() - 1);
                                    Value value1 = vars.get(key);
                                    Value value2 = value;
                                    Double n1 = new Double(0);
                                    Double n2 = new Double(0);
                                    if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                        n1 = (Double)value1.getValue();
                                        n2 = (Double)value2.getValue();
                                    }
                                    switch (s) {
                                    case '+':
                                        if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                            Double n = new Double(n1.doubleValue() + n2.doubleValue());
                                            value1.setValue(n);
                                            vars.put(key, value1);
                                        } else if (value1.getType() == Value.TYPE_STRING || value2.getType() == Value.TYPE_STRING) {
                                            value1.setValue(value1.getValue().toString() + value2.getValue().toString());
                                            vars.put(key, value1);
                                        }
                                        break;
                                    case '-':
                                        if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                            Double n = new Double(n1.doubleValue() - n2.doubleValue());
                                            value1.setValue(n);
                                            vars.put(key, value1);
                                        }
                                        break;
                                    case '*':
                                        if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                            Double n = new Double(n1.doubleValue() * n2.doubleValue());
                                            value1.setValue(n);
                                            vars.put(key, value1);
                                        }
                                        break;
                                    case '/':
                                        if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                            Double n = new Double(n1.doubleValue() / n2.doubleValue());
                                            value1.setValue(n);
                                            vars.put(key, value1);
                                        }
                                        break;
                                    case '%':
                                        if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                                            Double n = new Double(n1.doubleValue() % n2.doubleValue());
                                            value1.setValue(n);
                                            vars.put(key, value1);
                                        }
                                        break;
                                    default:
                                        break;
                                    }
                                } else { 
                                    vars.put(key, value);
                                }
                            }
                        } catch (CajuScriptException e) {
                            throw e;
                        } catch (Exception e) {
                            throw CajuScriptException.create(this, "Sintax error", line, e);
                        }
                    } else if (line.startsWith("$")) {
                        String path = line.substring(1).trim();
                        if (path.startsWith(CAJU_VARS)) {
                            evalFile(getVar(path).toString());
                        } else {
                            addImport(path);
                        }
                    } else {
                        if (!line.equals("")) {
                            new Value(this, evalValue(line));
                        }
                    }
                }
            }
            return new Value(this);
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(this, "Sintax error", line, e);
        }
    }
    private int[] loadIfLine(List<String> ifs, int lineNumber, String[] subLines, int subLinesIndex, StringBuffer scriptIFCondition, StringBuffer scriptIF, int ifLevel) throws CajuScriptException {
        int countSubLines = 0;
        for (int z = subLinesIndex + 1; z < subLines.length; z++) {
            String scriptIFline = subLines[z].trim();
            if (ifLevel == 0 && scriptIFline.length() > 1 && scriptIFline.startsWith("?") && scriptIFline.endsWith("?")) {
                ifs.add(scriptIFCondition + "?" + lineNumber + "?" + scriptIF);
                scriptIFCondition.delete(0, scriptIFCondition.length());
                String condition = scriptIFline.substring(1, scriptIFline.length() - 1);
                if (condition.trim().equals("")) {
                    condition = "1 = 1";
                }
                scriptIFCondition = scriptIFCondition.append(condition);
                scriptIF.delete(0, scriptIF.length());
                continue;
            } else if (scriptIFline.length() == 1 && scriptIFline.equals("?")) {
                if (ifLevel == 0) {
                    ifs.add(scriptIFCondition + "?" + lineNumber + "?" + scriptIF);
                    return new int[] {z, -1};
                }
                ifLevel--;
            } else if (scriptIFline.length() > 1 && !scriptIFline.startsWith("?") && scriptIFline.endsWith("?")) {
                ifLevel++;
            }
            if (!scriptIFline.equals("")) {
                scriptIF.append(countSubLines > 0 ? SUBLINE_LIMITER + scriptIFline : scriptIFline );
            }
            countSubLines++;
        }
        return new int[] {-1, ifLevel};
    }
    private int[] loadLoopLine(String[] subLines, int subLinesIndex, StringBuffer scriptLOOP, int loopLevel) throws CajuScriptException {
        int countSubLines = 0;
        for (int z = subLinesIndex + 1; z < subLines.length; z++) {
            String scriptLOOPline = subLines[z].trim();
            if (scriptLOOPline.length() > 1 && scriptLOOPline.endsWith("@")) {
                loopLevel++;
            } else if (scriptLOOPline.length() == 1 && scriptLOOPline.equals("@")) {
                if (loopLevel == 0) {
                    return new int[] {z, -1};
                }
                loopLevel--;
            }
            if (!scriptLOOPline.equals("")) {
                scriptLOOP.append(countSubLines > 0 ? SUBLINE_LIMITER + scriptLOOPline : scriptLOOPline);
            }
            countSubLines++;
        }
        return new int[] {-1, loopLevel};
    }
    private int loadFuncLine(String[] subLines, int subLinesIndex, StringBuffer scriptFUNC) throws CajuScriptException {
        int countSubLines = 0;
        for (int z = subLinesIndex + 1; z < subLines.length; z++) {
            String scriptFUNCline = subLines[z].trim();
            if (scriptFUNCline.equals("#")) {
                return z;
            }
            if (!scriptFUNCline.equals("")) {
                scriptFUNC.append(countSubLines > 0 ? SUBLINE_LIMITER + scriptFUNCline : scriptFUNCline);
            }
            countSubLines++;
        }
        return -1;
    }
    private boolean condition(String script) throws CajuScriptException {
        try {
            script = script.trim();
            int s1 = script.indexOf("&");
            int s2 = script.indexOf("|");
            s1 = s1 == -1 ? Integer.MAX_VALUE : s1;
            s2 = s2 == -1 ? Integer.MAX_VALUE : s2;
            int min1 = Math.min(s1, s2);
            if (s1 < Integer.MAX_VALUE && min1 == s1) {
                if (condition(script.substring(0, s1)) && condition(script.substring(s1 + 1))) {
                    return true;
                }
            } else if (s2 < Integer.MAX_VALUE && min1 == s2) {
                if (condition(script.substring(0, s2)) || condition(script.substring(s2 + 1))) {
                    return true;
                }
            } else if (!script.equals("")) {
                int cs1 = script.indexOf("=");
                int cs2 = script.indexOf("!");
                int cs3 = script.indexOf(">=");
                int cs4 = script.indexOf("<=");
                int cs5 = script.indexOf(">");
                int cs6 = script.indexOf("<");
                if (cs3 > -1 || cs4 > -1) {
                    cs1 = -1;
                }
                int max = Math.max(cs1, Math.max(cs2, Math.max(cs3, Math.max(cs4, Math.max(cs5, cs6)))));
                if (max > -1) {
                    int len = max == cs3 || max == cs4 ? 2 : 1;
                    Value value1 = new Value(this, script.substring(0, max));
                    Value value2 = new Value(this, script.substring(max + len));
                    if ((cs1 > -1 || cs2 > -1) && (value1.getValue() == null || value2.getValue() == null)) {
                        if (cs1 > -1 && value1.getValue() == value2.getValue()) {
                            return true;
                        } else if (cs2 > -1 && value1.getValue() != value2.getValue()) {
                            return true;
                        }
                    } else if ((cs1 > -1 || cs2 > -1) && (value1.getType() == Value.TYPE_NUMBER || value2.getType() == Value.TYPE_NUMBER)) {
                        if (cs1 > -1 && value1.getNumberValue() == value2.getNumberValue()) {
                            return true;
                        } else if (cs2 > -1 && value1.getNumberValue() != value2.getNumberValue()) {
                            return true;
                        }
                    } else if (cs1 > -1 && value1.getValue().equals(value2.getValue())) {
                        return true;
                    } else if (cs2 > -1 && !value1.getValue().equals(value2.getValue())) {
                        return true;
                    } else if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                        if (cs3 > -1 && value1.getNumberValue() >= value2.getNumberValue()) {
                            return true;
                        } else if (cs4 > -1 && value1.getNumberValue() <= value2.getNumberValue()) {
                            return true;
                        } else if (cs5 > -1 && value1.getNumberValue() > value2.getNumberValue()) {
                            return true;
                        } else if (cs6 > -1 && value1.getNumberValue() < value2.getNumberValue()) {
                            return true;
                        }
                    }
                } else {
                    Value value = new Value(this, script);
                    return ((Boolean)cast(value.getValue(), "b")).booleanValue();
                }
            }
            return false;
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(this, "Sintax error", script, e);
        }
    }
    private String evalValue(String script) throws CajuScriptException {
        return evalValueGroup(script);
    }
    private String evalValueSingle(String script) throws CajuScriptException {
        try {
            script = script.trim();
            String scriptSign = script;
            boolean startWithSign = false;
            if (script.startsWith("-") || script.startsWith("+")) {
                startWithSign = true;
                scriptSign = script.substring(1);
            }
            int sign = -1;
            int s1 = scriptSign.indexOf('+');
            int s2 = scriptSign.indexOf('-');
            int s3 = scriptSign.indexOf('*');
            int s4 = scriptSign.indexOf('/');
            int s5 = scriptSign.indexOf('%');
            s1 = s1 == -1 ? Integer.MAX_VALUE : s1;
            s2 = s2 == -1 ? Integer.MAX_VALUE : s2;
            s3 = s3 == -1 ? Integer.MAX_VALUE : s3;
            s4 = s4 == -1 ? Integer.MAX_VALUE : s4;
            s5 = s5 == -1 ? Integer.MAX_VALUE : s5;
            int min1 = Math.min(s1, Math.min(s2, Math.min(s3, Math.min(s4, s5))));
            if (min1 > -1 && min1 < Integer.MAX_VALUE) {
                if (startWithSign) {
                    if (min1 == s1) {
                        s1++;
                    } else if (min1 == s2) {
                        s2++;
                    } else if (min1 == s3) {
                        s3++;
                    } else if (min1 == s4) {
                        s4++;
                    } else if (min1 == s5) {
                        s5++;
                    }
                    min1++;
                }
                Value value1 = new Value(this, script.substring(0, min1));
                int min2 = Math.min(s3, Math.min(s4, s5));
                if (min2 > min1 && min2 < Integer.MAX_VALUE) {
                    for (int x = min2 - 2; x > 0; x--) {
                        if (script.substring(x).startsWith(" ")
                            || script.substring(x).startsWith("+")
                            || script.substring(x).startsWith("-")
                            || script.substring(x).startsWith("/")
                            || script.substring(x).startsWith("*")
                            || script.substring(x).startsWith("%")) {
                            script = evalValueSingle(script.substring(0, x+1) + evalValueSingle(script.substring(x+1)));
                            return script;
                        }
                    }
                }
                Value value2 = null;
                for (int x = min1 + 1; x < script.length(); x++) {
                    String nextValue = script.substring(min1 + 1, x);
                    String nextValueTrim = nextValue.trim();
                    if (!nextValueTrim.equals("") && !nextValueTrim.equals("-") && !nextValueTrim.equals("+") && (
                        nextValue.endsWith("/") || nextValue.endsWith("*") || nextValue.endsWith("%") || 
                        nextValue.endsWith("-") || nextValue.endsWith("+"))) {
                        value2 = new Value(this, nextValue.substring(0, nextValue.length() - 1));
                        script = script.substring(x - 1);
                        break;
                    }
                }
                if (value2 == null) {
                    value2 = new Value(this, script.substring(min1 + 1));
                    script = "";
                }
                String valueFinal = "";
                if (value1.getType() == Value.TYPE_NUMBER && value2.getType() == Value.TYPE_NUMBER) {
                    if (min1 == s1) {
                        valueFinal += (value1.getNumberValue() + value2.getNumberValue());
                    } else if (min1 == s2) {
                        valueFinal += (value1.getNumberValue() - value2.getNumberValue());
                    } else if (min1 == s3) {
                        valueFinal += (value1.getNumberValue() * value2.getNumberValue());
                    } else if (min1 == s4) {
                        valueFinal += (value1.getNumberValue() / value2.getNumberValue());
                    } else if (min1 == s5) {
                        valueFinal += (value1.getNumberValue() % value2.getNumberValue());
                    }
                } else {
                    String varKey = CAJU_VARS_STATIC_STRING + vars.size();
                    vars.put(varKey, new Value(this, "\"" + value1.getValue().toString() + value2.getValue().toString() + "\""));
                    valueFinal += varKey;
                }
                if (script.indexOf('+') > -1 || script.indexOf('-') > -1 ||
                    script.indexOf('*') > -1 || script.indexOf('/') > -1 || script.indexOf('%') > -1) {
                    return evalValueSingle(valueFinal + script);
                } else {
                    return valueFinal;
                }
            } else {
                return script.trim();
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(this, "Sintax error", script, e);
        }
    }
    private String evalValueGroup(String script) throws CajuScriptException {
        if (script.indexOf('(') > -1) {
            try {
                char[] scriptChars = script.toCharArray();
                String scriptGroup = "";
                String scriptGroupFunc = "";
                int groupType = 0;
                for (int x = 0; x < scriptChars.length; x++) {
                    if (scriptChars[x] == '(') {
                        scriptGroup = "";
                        scriptGroupFunc = "";
                        groupType = 0;
                        for (int y = x - 1; y >= 0; y--) {
                            if ("+*-/%(),".indexOf(scriptChars[y]) > -1) {
                                break;
                            }
                            if (scriptChars[y] != ' ') {
                                groupType = 1;
                            }
                            scriptGroupFunc = scriptChars[y] + scriptGroupFunc;
                        }
                    } else if (scriptChars[x] == ')') {
                        String varKey = CAJU_VARS_GROUP + vars.size();
                        String valueScript = scriptGroup;
                        if (!scriptGroupFunc.trim().equals("")
                            && scriptGroupFunc.indexOf("<") == -1 && scriptGroupFunc.indexOf("=") == -1 && scriptGroupFunc.indexOf(">") == -1
                            && scriptGroupFunc.indexOf("!") == -1 && scriptGroupFunc.indexOf("&") == -1 && scriptGroupFunc.indexOf("|") == -1) {
                            String[] params = scriptGroup.split(",");
                            String scriptParams = "";
                            for (int k = 0; k < params.length; k++) {
                                if (k > 0) {
                                    scriptParams += ",";
                                }
                                scriptParams += evalValueSingle(params[k]);
                            }
                            valueScript = scriptGroupFunc + "(" + scriptParams + ")";
                        }
                        if (scriptGroup.indexOf('<') > -1 || scriptGroup.indexOf('=') > -1 || scriptGroup.indexOf('>') > -1
                            || scriptGroup.indexOf("!") > -1 || scriptGroup.indexOf('&') > -1 || scriptGroup.indexOf('|') > -1) {
                            groupType = 0;
                            vars.put(varKey, new Value(this, condition(scriptGroup) ? "true" : "false"));
                        } else {
                            vars.put(varKey, new Value(this, groupType == 0 ? evalValueSingle(valueScript) : valueScript));
                        }
                        script = script.replace(groupType == 0 ? "(" + scriptGroup + ")" : scriptGroupFunc + "(" + scriptGroup + ")", varKey);
                        if (script.indexOf('(') > -1) {
                            return evalValueGroup(script);
                        } else {
                            if (script.indexOf('<') > -1 || script.indexOf('=') > -1 || script.indexOf('>') > -1
                                || script.indexOf("!") > -1 || script.indexOf('&') > -1 || script.indexOf('|') > -1) {
                                return condition(script) ? "true" : "false";
                            } else {
                                return evalValueSingle(script);
                            }
                        }
                    } else {
                        scriptGroup += scriptChars[x];
                    }
                }
            } catch (CajuScriptException e) {
                throw e;
            } catch (Exception e) {
                throw CajuScriptException.create(this, "Sintax error", script, e);
            }
            return "";
        } else {
            if (script.indexOf('<') > -1 || script.indexOf('=') > -1 || script.indexOf('>') > -1
                || script.indexOf("!") > -1 || script.indexOf('&') > -1 || script.indexOf('|') > -1) {
                return condition(script) ? "true" : "false";
            } else {
                return evalValueSingle(script);
            }
        }
    }
    /**
     * Exucute a file.
     * @param path File to be executed
     * @throws org.cajuscript.CajuScriptException File cannot be executed or error ocurred on execution
     */
    public void evalFile(String path) throws CajuScriptException {
        java.io.InputStream is = null;
        try {
            is = new java.io.FileInputStream(path);
            byte[] b = new byte[is.available()];
            is.read(b);
            eval(new String(b));
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(this, "Cannot read file \"" + e.getMessage() + "\"", path, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) { }
            }
        }
    }
    /**
     * Get funcion.
     * @param key Funcion name
     * @return Function object
     */
    public Func getFunc(String key) {
        return funcs.get(key);
    }
    /**
     * Define a funcion.
     * @param key Funcion name
     * @param func Object of the function
     */
    public void setFunc(String key, Func func) {
        funcs.put(key, func);
    }
    /**
     * Get variable.
     * @param key Variable name
     * @return Variable object
     */
    public Value getVar(String key) {
        return vars.get(key);
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
     * @param withCajuVars Including variables created automaticaly by CajuScript or not
     * @return List of all variables names
     */
    public Set<String> getAllKeys(boolean withCajuVars) {
        Set<String> keys = new HashSet<String>();
        for (String key : vars.keySet()) {
            if (!withCajuVars && key.startsWith(CAJU_VARS)) {
                continue;
            }
            keys.add(key);
        }
        return keys;
    }
    /**
     * Adding new variable.
     * @param key Variable name
     * @param value Variable value
     */
    public void addVar(String key, Value value) {
        vars.put(key, value);
    }
    /**
     * Convert Java objects to CajuScript object to be used like value of variables.
     * @param obj Object to be converted in Value
     * @return Newly Value generated from the Object
     * @throws org.cajuscript.CajuScriptException Errors
     */
    public Value toValue(Object obj) throws CajuScriptException {
        Value v = new Value(this);
        v.setValue(obj);
        return v;
    }
    /**
     * Defining new variable.
     * @param key Variable name
     * @param value Variable value
     * @throws org.cajuscript.CajuScriptException Errors
     */
    public void set(String key, Object value) throws CajuScriptException {
        addVar(key, toValue(value));
    }
    /**
     * Getting value of variables in Java object.
     * @param key Variable name
     * @return Variable value in Java
     * @throws org.cajuscript.CajuScriptException Errors
     */
    public Object get(String key) throws CajuScriptException {
        return getVar(key).getValue();
    }
    /**
     * Get list of all imports used by script in execution.
     * @return List of imports defined
     */
    public List<String> getImports() {
        return imports;
    }
    /**
     * Define a new import to be used. Only Java package.
     * @param i The content of importing is only Java package.
     */
    public void addImport(String i) {
        imports.add(i);
    }
    /**
     * Remove import.
     * @param s Import content to be removed.
     */
    public void removeImport(String s) {
        imports.remove(s);
    }
    /**
     * Convert value to type specified.
     * @param value Value to be converted
     * @param type Types: "int" = "i", "long" = "l", "double" = "d",
     * "float" = "f", "char" = "c", "boolean" = "b", "byte" = "bt",
     * "string" = "s", "java.ANY_CLASS"
     * @return Object converted
     * @throws org.cajuscript.CajuScriptException Errors ocurred on converting.
     */
    public Object cast(Object value, String type) throws CajuScriptException {
        try {
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
            if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("java.lang.Boolean") || type.equalsIgnoreCase("b")) {
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
            try {
                return Class.forName(type).cast(value);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw CajuScriptException.create(this, "Cannot convert \""+ value.toString() +"\" to "+ type, "convert("+ value.toString() +", "+ type +")", e);
        }
    }
    /**
     * Entry point to running.
     * @param args Arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                CajuScript caju = new CajuScript();
                caju.evalFile(args[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}