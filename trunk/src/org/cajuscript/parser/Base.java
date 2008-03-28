/*
 * Base.java
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

import java.util.ArrayList;
import java.util.List;
import org.cajuscript.CajuScript;
import org.cajuscript.Context;
import org.cajuscript.Syntax;
import org.cajuscript.Value;
import org.cajuscript.CajuScriptException;

/**
 * Base to do script parse.
 * @author eduveks
 */
public class Base implements Element {
    protected List<Element> elements = new ArrayList<Element>();
    protected LineDetail baseLineDetail = null;
    protected Syntax baseSyntax = null;
    private static int staticVarsGroupCounter = 1;
    private static int staticVarsParameterCounter = 1;
    /**
     * Base
     * @param line Line detail
     * @param syntax Syntax style
     */
    public Base(LineDetail line, Syntax syntax) {
        this.baseLineDetail = line;
        this.baseSyntax = syntax;
    }
    /**
     * Elements
     * @return All child elements
     */
    public List<Element> getElements() {
        return elements;
    }
    /**
     * Append child element.
     * @param element Element
     */
    public void addElement(Element element) {
        elements.add(element);
    }
    /**
     * Remove child element.
     * @param element Element
     */
    public void removeElement(Element element) {
        elements.remove(element);
    }
    /**
     * Get line detail.
     * @return Line detail
     */
    public LineDetail getLineDetail() {
        return baseLineDetail;
    }
    /**
     * Get syntax.
     * @return Syntax
     */
    public Syntax getSyntax() {
        return baseSyntax;
    }
    /**
     * If element can return value.
     * @param element Element
     * @return If element can return.
     */
    protected boolean canElementReturn(Element element) {
        if (!(element instanceof Command) && !(element instanceof Operation) && !(element instanceof Variable)) {
            return true;
        }
        return false;
    }
    /**
     * Executed this element and all childs elements.
     * @param caju CajuScript instance
     * @return Value returned by execution
     * @throws org.cajuscript.CajuScriptException Errors ocurred on execution
     */
    public Value execute(CajuScript caju, Context context) throws CajuScriptException {
        caju.setRunningLine(getLineDetail());
        for (Element element : elements) {
            Value v = element.execute(caju, context);
            if (v != null && canElementReturn(element)) {
                return v;
            }
        }
        return null;
    }
    /**
     * Script parse.
     * @param caju CajuScript instance
     * @param script Script to be parsed
     * @param syntax Syntax style of the script
     * @throws org.cajuscript.CajuScriptException Errors ocurred on parsing
     */
    public void parse(CajuScript caju, String script, Syntax syntax) throws CajuScriptException {
        parse(null, caju, null, script, syntax);
    }
    private void parse(Element base, CajuScript caju, LineDetail lineDetail, String script, Syntax syntax) throws CajuScriptException {
        if (base == null) {
            base = this;
        }
        String[] lines = script.split(CajuScript.SUBLINE_LIMITER);
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y].trim();
            LineDetail __lineDetail = loadLineDetail(line);
            if (__lineDetail != null) {
                lineDetail = __lineDetail;
                line = lineDetail.getContent().trim();
            }
            String label = "";
            if (line.indexOf(syntax.getLabel()) > -1) {
                label = line.substring(0, line.indexOf(syntax.getLabel())).trim();
                line = line.substring(line.indexOf(syntax.getLabel()) + syntax.getLabel().length()).trim();
            }
            if (line.startsWith(syntax.getReturn())) {
                if (line.equals(syntax.getReturn().trim())) {
                    base.addElement(new Return(lineDetail, syntax));
                } else {
                    Return r = new Return(lineDetail, syntax);
                    r.setValue(evalValue(base, caju, lineDetail, syntax, line.substring(syntax.getReturn().length())));
                    base.addElement(r);
                }
            } else if (line.startsWith(syntax.getIf()) && line.endsWith(syntax.getIfBegin())) {
                String scriptIFCondition = line.substring(syntax.getIf().length(), line.length() - syntax.getIfBegin().length());
                StringBuffer scriptIF = new StringBuffer();
                List<String> ifs = new ArrayList<String>();
                int ifLevel = 0;
                int minElseIfDef = syntax.getElseIf().length() + syntax.getElseIfBegin().length();
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalIFline = lines[z];
                    String scriptIFline = originalIFline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptIFline);
                    if (_lineDetail != null) {
                        scriptIFline = _lineDetail.getContent().trim();
                    }
                    if (ifLevel == 0 && scriptIFline.length() > minElseIfDef && scriptIFline.startsWith(syntax.getElseIf()) && scriptIFline.endsWith(syntax.getElseIfBegin())) {
                        ifs.add(scriptIFCondition + syntax.getIfEnd() + scriptIF);
                        String condition = scriptIFline.substring(syntax.getElseIf().length(), scriptIFline.length() - syntax.getElseIfBegin().length());
                        if (condition.trim().equals("")) {
                            condition = "1 "+ syntax.getOperatorEqual() +" 1";
                        }
                        scriptIFCondition = condition;
                        scriptIF.delete(0, scriptIF.length());
                        continue;
                    } else if (ifLevel == 0 && scriptIFline.equals(syntax.getElse())) {
                        ifs.add(scriptIFCondition + syntax.getIfEnd() + scriptIF);
                        scriptIFCondition = "1 "+ syntax.getOperatorEqual() +" 1";
                        scriptIF.delete(0, scriptIF.length());
                        continue;
                    } else if (isStatementBegins(scriptIFline, syntax)) {
                        ifLevel++;
                    } else if (isStatementEnds(scriptIFline, syntax)) {
                        if (ifLevel == 0) {
                            ifs.add(scriptIFCondition + syntax.getIfEnd() + scriptIF);
                            break;
                        }
                        ifLevel--;
                    }
                    scriptIF.append(originalIFline + CajuScript.SUBLINE_LIMITER);
                }
                IfGroup ifGroup = new IfGroup(lineDetail, syntax);
                for (String content : ifs) {
                    int p1 = content.indexOf(syntax.getIfEnd());
                    int p2 = p1 + syntax.getIfEnd().length();
                    String _ifCondition = content.substring(0, p1);
                    String _ifContent = content.substring(p2);
                    _ifCondition = _ifCondition.trim();
                    
                    If _if = new If(lineDetail, syntax);
                    Variable var = new Variable(lineDetail, syntax);
                    var.setValue(evalValue(var, caju, lineDetail, syntax, _ifCondition));
                    _if.setCondition(var);
                    parse(_if, caju, lineDetail, _ifContent, syntax);
                    ifGroup.addElement(_if);
                }
                base.addElement(ifGroup);
            } else if (line.startsWith(syntax.getLoop()) && line.endsWith(syntax.getLoopBegin())) {
                String scriptLOOPCondition = line.substring(syntax.getLoop().length(), line.length() - syntax.getLoopBegin().length());
                StringBuffer scriptLOOP = new StringBuffer();
                int loopLevel = 0;
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalLOOPline = lines[z];
                    String scriptLOOPline = originalLOOPline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptLOOPline);
                    if (_lineDetail != null) {
                        scriptLOOPline = _lineDetail.getContent().trim();
                    }
                    if (isStatementBegins(scriptLOOPline, syntax)) {
                        loopLevel++;
                    } else if (isStatementEnds(scriptLOOPline, syntax)) {
                        if (loopLevel == 0) {
                            break;
                        }
                        loopLevel--;
                    }
                    scriptLOOP.append(originalLOOPline + CajuScript.SUBLINE_LIMITER);
                }
                Loop loop = new Loop(lineDetail, syntax);
                loop.setLabel(label);
                Variable var = new Variable(lineDetail, syntax);
                var.setValue(evalValue(var, caju, lineDetail, syntax, scriptLOOPCondition));
                loop.setCondition(var);
                parse(loop, caju, lineDetail, scriptLOOP.toString(), syntax);
                base.addElement(loop);
            } else if (line.startsWith(syntax.getFunction()) && line.endsWith(syntax.getFunctionBegin())) {
                String scriptFUNCDef = line.substring(syntax.getFunction().length(), line.length() - syntax.getFunctionBegin().length()).trim();
                StringBuffer scriptFUNC = new StringBuffer();
                int loopLevel = 0;
                int minFuncDef = syntax.getFunction().length() + syntax.getFunctionBegin().length();
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalFUNCline = lines[z];
                    String scriptFUNCline = originalFUNCline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptFUNCline);
                    if (_lineDetail != null) {
                        scriptFUNCline = _lineDetail.getContent().trim();
                    }
                    if (scriptFUNCline.length() > minFuncDef && scriptFUNCline.startsWith(syntax.getFunction()) && scriptFUNCline.endsWith(syntax.getFunctionBegin())) {
                        loopLevel++;
                    } else if (isStatementBegins(scriptFUNCline, syntax)) {
                        loopLevel++;
                    } else if (scriptFUNCline.equals(syntax.getFunctionEnd()) || isStatementEnds(scriptFUNCline, syntax)) {
                        if (loopLevel == 0) {
                            break;
                        }
                        loopLevel--;
                    }
                    scriptFUNC.append(originalFUNCline + CajuScript.SUBLINE_LIMITER);
                }
                Function func = new Function(lineDetail, syntax);
                func.setDefinition(scriptFUNCDef);
                parse(func, caju, lineDetail, scriptFUNC.toString(), syntax);
                caju.setFunc(func.getName(), func);
            } else if (line.startsWith(syntax.getTry()) && line.endsWith(syntax.getTryBegin())) {
                String scriptTRYCATCHerrorVar = line.substring(syntax.getTry().length(), line.length() - syntax.getTryBegin().length()).trim();
                StringBuffer scriptTRY = new StringBuffer();
                StringBuffer scriptCATCH = new StringBuffer();
                StringBuffer scriptFINALLY = new StringBuffer();
                boolean isTry = true;
                boolean isCatch = false;
                boolean isFinally = false;
                int loopLevel = 0;
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalTRYCATCHline = lines[z];
                    String scriptTRYCATCHline = originalTRYCATCHline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptTRYCATCHline);
                    if (_lineDetail != null) {
                        scriptTRYCATCHline = _lineDetail.getContent().trim();
                    }
                    if (scriptTRYCATCHline.equals(syntax.getTryCatch()) && loopLevel == 0) {
                        isTry = false;
                        isCatch = true;
                        isFinally = false;
                        continue;
                    } else if (scriptTRYCATCHline.equals(syntax.getTryFinally()) && loopLevel == 0) {
                        isTry = false;
                        isCatch = false;
                        isFinally = true;
                        continue;
                    } else if (isStatementBegins(scriptTRYCATCHline, syntax)) {
                        loopLevel++;
                    } else if (isStatementEnds(scriptTRYCATCHline, syntax)) {
                        if (loopLevel == 0) {
                            break;
                        }
                        loopLevel--;
                    }
                    if (isTry) {
                        scriptTRY.append(originalTRYCATCHline + CajuScript.SUBLINE_LIMITER);
                    } else if (isCatch) {
                        scriptCATCH.append(originalTRYCATCHline + CajuScript.SUBLINE_LIMITER);
                    } else if (isFinally) {
                        scriptFINALLY.append(originalTRYCATCHline + CajuScript.SUBLINE_LIMITER);
                    }
                }
                TryCatch tryCatch = new TryCatch(lineDetail, syntax);
                Variable error = new Variable(lineDetail, syntax);
                error.setKey(scriptTRYCATCHerrorVar);
                Base _try = new Base(lineDetail, syntax);
                parse(_try, caju, lineDetail, scriptTRY.toString(), syntax);
                Base _catch = new Base(lineDetail, syntax);
                parse(_catch, caju, lineDetail, scriptCATCH.toString(), syntax);
                Base _finally = new Base(lineDetail, syntax);
                parse(_finally, caju, lineDetail, scriptFINALLY.toString(), syntax);
                tryCatch.setError(error);
                tryCatch.setTry(_try);
                tryCatch.setCatch(_catch);
                tryCatch.setFinally(_finally);
                base.addElement(tryCatch);
            } else if (line.indexOf(syntax.getOperatorEqual()) > -1) {
                try {
                    int p = line.indexOf(syntax.getOperatorEqual());
                    String keys = line.substring(0, p);
                    String value = line.substring(p + 1, line.length());
                    String action = "";
                    if (keys.endsWith("" + syntax.getOperatorAddition())
                        || keys.endsWith("" + syntax.getOperatorSubtraction())
                        || keys.endsWith("" + syntax.getOperatorMultiplication())
                        || keys.endsWith("" + syntax.getOperatorDivision())
                        || keys.endsWith("" + syntax.getOperatorModules())) {
                        action = keys.substring(keys.length() - 1);
                        p--;
                    }
                    String[] allKeys = keys.substring(0, p).replaceAll(" ", "").split(",");
                    for (String key : allKeys) {
                        if (!action.equals("")) {
                            value = key + " " + action + "(" + value + ")";
                        }
                        Variable var = new Variable(lineDetail, syntax);
                        var.setKey(key);
                        var.setValue(evalValue(var, caju, lineDetail, syntax, value));
                        base.addElement(var);
                    }
                } catch (CajuScriptException e) {
                    throw e;
                } catch (Exception e) {
                    throw CajuScriptException.create(caju, caju.getContext(), "Incorrect definition", e);
                }
            } else if (line.startsWith(syntax.getImport())) {
                String path = line.substring(syntax.getImport().length()).trim();
                Import i = new Import(lineDetail, syntax);
                i.setPath(path);
                base.addElement(i);
            } else if (line.startsWith(syntax.getBreak()) && !line.startsWith(syntax.getContinue())) {
                Break b = new Break(lineDetail, syntax);
                b.setLabel(line.substring(syntax.getBreak().length()).trim());
                base.addElement(b);
            } else if (line.startsWith(syntax.getContinue())) {
                Continue c = new Continue(lineDetail, syntax);
                c.setLabel(line.substring(syntax.getContinue().length()).trim());
                base.addElement(c);
            } else {
                if (!line.equals("")) {
                    base.addElement(evalValue(base, caju, lineDetail, syntax, line));
                }
            }
        }
    }
    private LineDetail loadLineDetail(String line) {
        if (line.startsWith(">")) {
            int lineNumber = Integer.parseInt(line.substring(1, line.indexOf(":")));
            line = line.substring((lineNumber + "").length() + 2);
            return new LineDetail(lineNumber, line);
        }
        return null;
    }
    private boolean isStatementBegins(String line, Syntax syntax) {
        int minIfDef = syntax.getIf().length() + syntax.getIfBegin().length();
        int minLoopDef = syntax.getLoop().length() + syntax.getLoopBegin().length();
        int minTryDef = syntax.getTry().length() + syntax.getTryBegin().length();
        if (line.length() > minIfDef && line.startsWith(syntax.getIf()) && line.endsWith(syntax.getIfBegin()) && !line.startsWith(syntax.getElseIf()) && !line.equals(syntax.getElse())) {
            return true;
        } else if (line.length() > minLoopDef && line.startsWith(syntax.getLoop()) && line.endsWith(syntax.getLoopBegin())) {
            return true;
        } else if (line.length() > minTryDef && line.startsWith(syntax.getTry()) && line.endsWith(syntax.getTryBegin()) && !line.equals(syntax.getTryCatch())) {
            return true;
        }
        return false;
    }
    private boolean isStatementEnds(String line, Syntax syntax) {
        if (line.equals(syntax.getIfEnd()) || line.equals(syntax.getLoopEnd()) || line.equals(syntax.getTryEnd())) {
            return true;
        }
        return false;
    }   
    private Element condition(Element base, CajuScript caju, LineDetail lineDetail, Syntax syntax, String script) throws CajuScriptException {
        try {
            script = script.trim();
            int s1 = script.indexOf(syntax.getOperatorAnd());
            int s2 = script.indexOf(syntax.getOperatorOr());
            s1 = s1 == -1 ? Integer.MAX_VALUE : s1;
            s2 = s2 == -1 ? Integer.MAX_VALUE : s2;
            int min1 = Math.min(s1, s2);
            if (s1 < Integer.MAX_VALUE && min1 == s1) {
                Operation o = new Operation(lineDetail, syntax);
                o.setCommands(condition(base, caju, lineDetail, syntax, script.substring(0, s1)), syntax.getOperatorAnd(), condition(base, caju, lineDetail, syntax, script.substring(s1 + 1)));
                return o;
            } else if (s2 < Integer.MAX_VALUE && min1 == s2) {
                Operation o = new Operation(lineDetail, syntax);
                o.setCommands(condition(base, caju, lineDetail, syntax, script.substring(0, s2)), syntax.getOperatorOr(), condition(base, caju, lineDetail, syntax, script.substring(s2 + 1)));
                return o;
            } else if (!script.equals("")) {
                int cs1 = script.indexOf(syntax.getOperatorEqual());
                int cs2 = script.indexOf(syntax.getOperatorNotEqual());
                int cs3 = script.indexOf(syntax.getOperatorGreaterEqual());
                int cs4 = script.indexOf(syntax.getOperatorLessEqual());
                int cs5 = script.indexOf(syntax.getOperatorGreater());
                int cs6 = script.indexOf(syntax.getOperatorLess());
                if (cs3 > -1 || cs4 > -1) {
                    cs1 = -1;
                }
                int max = Math.max(cs1, Math.max(cs2, Math.max(cs3, Math.max(cs4, Math.max(cs5, cs6)))));
                if (max > -1) {
                    int len = max == cs3 || max == cs4 ? 2 : 1;
                    Element e1 = evalValue(base, caju, lineDetail, syntax, script.substring(0, max));
                    Element e2 = evalValue(base, caju, lineDetail, syntax, script.substring(max + len));
                    Operation o = new Operation(lineDetail, syntax);
                    if (cs1 > -1) {
                        o.setCommands(e1, syntax.getOperatorEqual(), e2);
                        return o;
                    } else if (cs2 > -1) {
                        o.setCommands(e1, syntax.getOperatorNotEqual(), e2);
                        return o;
                    } else if (cs3 > -1) {
                        o.setCommands(e1, syntax.getOperatorGreaterEqual(), e2);
                        return o;
                    } else if (cs4 > -1) {
                        o.setCommands(e1, syntax.getOperatorLessEqual(), e2);
                        return o;
                    } else if (cs5 > -1) {
                        o.setCommands(e1, syntax.getOperatorGreater(), e2);
                        return o;
                    } else if (cs6 > -1) {
                        o.setCommands(e1, syntax.getOperatorLess(), e2);
                        return o;
                    }
                } else {
                    return evalValue(base, caju, lineDetail, syntax, script);
                }
            }
            throw CajuScriptException.create(caju, caju.getContext(), "Sintax error");
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(caju, caju.getContext(), "Sintax error", e);
        }
    }
    private Element evalValue(Element base, CajuScript caju, LineDetail lineDetail, Syntax syntax, String script) throws CajuScriptException {
        return evalValueGroup(base, caju, lineDetail, syntax, script);
    }
    private Element evalValueSingle(CajuScript caju, LineDetail lineDetail, Syntax syntax, String script) throws CajuScriptException {
        try {
            script = script.trim();
            String scriptSign = script;
            boolean startWithSign = false;
            if (script.startsWith(syntax.getOperatorSubtraction() + "")
                || script.startsWith(syntax.getOperatorAddition() + "")) {
                startWithSign = true;
                scriptSign = script.substring(1);
            }
            int s1 = scriptSign.indexOf(syntax.getOperatorAddition());
            int s2 = scriptSign.indexOf(syntax.getOperatorSubtraction());
            int s3 = scriptSign.indexOf(syntax.getOperatorMultiplication());
            int s4 = scriptSign.indexOf(syntax.getOperatorDivision());
            int s5 = scriptSign.indexOf(syntax.getOperatorModules());
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
                char[] scriptChars = script.toCharArray();
                int min2 = Math.min(s3, Math.min(s4, s5));
                String allOperators = syntax.getAllCalculatorOperators();
                if (min2 > min1 && min2 < Integer.MAX_VALUE) {
                    String script1 = "";
                    String operator1 = "";
                    String scriptValue1 = "";
                    String operator = scriptChars[min2] + "";
                    String scriptValue2 = "";
                    String operator2 = "";
                    String script2 = "";
                    for (int x = min2 - 2; x > 0; x--) {
                        if (allOperators.indexOf(scriptChars[x]) > -1) {
                            script1 = script.substring(0, x);
                            operator1 = scriptChars[x] + "";
                            for (int y = min2 + 1; y < scriptChars.length; y++) {
                                if (allOperators.indexOf(scriptChars[y]) > -1) {
                                    operator2 = scriptChars[y] + "";
                                    script2 = script.substring(y + 1);
                                    break;
                                } else {
                                    scriptValue2 += scriptChars[y];
                                }
                            }
                            break;
                        } else {
                            scriptValue1 += scriptChars[x];
                        }
                    }
                    Command c1 = new Command(lineDetail, syntax);
                    c1.setCommand(scriptValue1);
                    Command c2 = new Command(lineDetail, syntax);
                    c2.setCommand(scriptValue2);
                    Operation o = new Operation(lineDetail, syntax);
                    o.setCommands(c1, operator, c2);
                    if (operator1.equals("") && operator2.equals("")) {
                        return o;
                    }
                    Element e1 = o;
                    if (!operator1.equals("")) {
                        Operation o1 = new Operation(lineDetail, syntax);
                        o1.setCommands(evalValueSingle(caju, lineDetail, syntax, script1), operator1, o);
                        if (operator2.equals("")) {
                            return o1;
                        }
                        e1 = o1;
                    }
                    Operation o2 = new Operation(lineDetail, syntax);
                    o2.setCommands(e1, operator2, evalValueSingle(caju, lineDetail, syntax, script2));
                    return o2;
                }
                Command value1 = new Command(lineDetail, syntax);
                value1.setCommand(script.substring(0, min1));
                Element value2 = null;
                String scriptValue2 = "";
                for (int x = min1 + 1; x < scriptChars.length; x++) {
                    scriptValue2 += scriptChars[x];
                    if (allOperators.indexOf(scriptChars[x]) > -1) {
                        Command c = new Command(lineDetail, syntax);
                        c.setCommand(scriptValue2.substring(0, scriptValue2.length() - 1));
                        Operation o = new Operation(lineDetail, syntax);
                        o.setCommands(c, ""+ scriptChars[x], evalValueSingle(caju, lineDetail, syntax, script.substring(x + 1)));
                        value2 = o;
                        break;
                    }
                }
                if (value2 == null) {
                    Command c = new Command(lineDetail, syntax);
                    c.setCommand(script.substring(min1 + 1));
                    value2 = c;
                }
                Operation operation = new Operation(lineDetail, syntax);
                operation.setCommands(value1, scriptChars[min1] + "", value2);
                return operation;
            } else {
                Command cmd = new Command(lineDetail, syntax);
                cmd.setCommand(script.trim());
                return cmd;
            }
        } catch (CajuScriptException e) {
            throw e;
        } catch (Exception e) {
            throw CajuScriptException.create(caju, caju.getContext(), "Sintax error", e);
        }
    }
    private Element evalValueGroup(Element base, CajuScript caju, LineDetail lineDetail, Syntax syntax, String script) throws CajuScriptException {
        if (script.indexOf('(') > -1) {
            try {
                String allOperators = syntax.getAllOperators();
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
                            if ((allOperators + "(),").indexOf(scriptChars[y]) > -1) {
                                break;
                            }
                            if (scriptChars[y] != ' ') {
                                groupType = 1;
                            }
                            scriptGroupFunc = scriptChars[y] + scriptGroupFunc;
                        }
                    } else if (scriptChars[x] == ')') {
                        String varKey = CajuScript.CAJU_VARS_GROUP + staticVarsGroupCounter;
                        staticVarsGroupCounter++;
                        String valueScript = scriptGroup;
                        if (!scriptGroupFunc.trim().equals("")
                            && scriptGroupFunc.indexOf(syntax.getOperatorAnd()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorOr()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorEqual()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorNotEqual()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorGreater()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorGreaterEqual()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorLess()) == -1
                            && scriptGroupFunc.indexOf(syntax.getOperatorLessEqual()) == -1) {
                            String scriptParams = scriptGroup;
                            if (!scriptGroup.trim().equals("")) {
                                scriptParams = "";
                                String[] params = scriptGroup.split(",");
                                for (int k = 0; k < params.length; k++) {
                                    if (k > 0) {
                                        scriptParams += ",";
                                    }
                                    String varParamKey = CajuScript.CAJU_VARS_PARAMETER + staticVarsParameterCounter;
                                    staticVarsParameterCounter++;
                                    Variable var = new Variable(lineDetail, syntax);
                                    var.setKey(varParamKey);
                                    var.setValue(evalValueSingle(caju, lineDetail, syntax, params[k]));
                                    base.addElement(var);
                                    scriptParams += varParamKey;
                                }
                            }
                            valueScript = scriptGroupFunc + "(" + scriptParams + ")";
                        }
                        Variable var = new Variable(lineDetail, syntax);
                        var.setKey(varKey);
                        if (scriptGroup.indexOf(syntax.getOperatorAnd()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorOr()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorEqual()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorNotEqual()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorGreater()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorGreaterEqual()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorLess()) > -1
                            || scriptGroup.indexOf(syntax.getOperatorLessEqual()) > -1) {
                            groupType = 0;
                            var.setValue(condition(base, caju, lineDetail, syntax, scriptGroup));
                        } else {
                            Element e = null;
                            if (groupType == 0) {
                                e = evalValueSingle(caju, lineDetail, syntax, valueScript);
                            } else {
                                Command c = new Command(lineDetail, syntax);
                                c.setCommand(valueScript);
                                e = c;
                            }
                            var.setValue(e);
                        }
                        base.addElement(var);
                        if (groupType == 0) {
                            scriptGroupFunc = "";
                        }
                        script = script.replace(scriptGroupFunc + "(" + scriptGroup + ")", varKey);
                        if (script.indexOf('(') > -1) {
                            return evalValueGroup(base, caju, lineDetail, syntax, script);
                        } else {
                            if (script.indexOf(syntax.getOperatorAnd()) > -1
                                || script.indexOf(syntax.getOperatorOr()) > -1
                                || script.indexOf(syntax.getOperatorEqual()) > -1
                                || script.indexOf(syntax.getOperatorNotEqual()) > -1
                                || script.indexOf(syntax.getOperatorGreater()) > -1
                                || script.indexOf(syntax.getOperatorGreaterEqual()) > -1
                                || script.indexOf(syntax.getOperatorLess()) > -1
                                || script.indexOf(syntax.getOperatorLessEqual()) > -1) {
                                return condition(base, caju, lineDetail, syntax, script);
                            } else {
                                return evalValueSingle(caju, lineDetail, syntax, script);
                            }
                        }
                    } else {
                        scriptGroup += scriptChars[x];
                    }
                }
            } catch (CajuScriptException e) {
                throw e;
            } catch (Exception e) {
                throw CajuScriptException.create(caju, caju.getContext(), "Sintax error", e);
            }
            throw CajuScriptException.create(caju, caju.getContext(), "Sintax error");
        } else {
            if (script.indexOf(syntax.getOperatorAnd()) > -1
                || script.indexOf(syntax.getOperatorOr()) > -1
                || script.indexOf(syntax.getOperatorEqual()) > -1
                || script.indexOf(syntax.getOperatorNotEqual()) > -1
                || script.indexOf(syntax.getOperatorGreater()) > -1
                || script.indexOf(syntax.getOperatorGreaterEqual()) > -1
                || script.indexOf(syntax.getOperatorLess()) > -1
                || script.indexOf(syntax.getOperatorLessEqual()) > -1) {
                return condition(base, caju, lineDetail, syntax, script);
            } else {
                
            }
            return evalValueSingle(caju, lineDetail, syntax, script);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        for (Element element : elements) {
            elements.remove(element);
        }
        elements = null;
        baseLineDetail = null;
        baseSyntax = null;
    }
}
