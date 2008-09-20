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
import org.cajuscript.SyntaxPosition;
import org.cajuscript.Value;
import org.cajuscript.CajuScriptException;
import org.cajuscript.parser.Operation.Operator;

/**
 * Base to do script parse.
 * @author eduveks
 */
public class Base implements Element {
    protected List<Element> elements = new ArrayList<Element>();
    protected LineDetail baseLineDetail = null;
    protected Syntax baseSyntax = null;
    private static long staticVarsGroupCounter = 0;
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
            SyntaxPosition syntaxPosition = null;
            if ((syntaxPosition = syntax.matcherPosition(line, syntax.getLabel())).getStart() > -1) {
                label = line.substring(0, syntaxPosition.getStart()).trim();
                line = line.substring(syntaxPosition.getEnd()).trim();
            }
            if ((syntaxPosition = syntax.matcherPosition(line, syntax.getReturn())).getStart() == 0) {
                if (syntax.matcherEquals(line, syntax.getReturn())) {
                    base.addElement(new Return(lineDetail, syntax));
                } else {
                    Return r = new Return(lineDetail, syntax);
                    r.setValue(evalValue(base, caju, lineDetail, syntax, line.substring(syntaxPosition.getEnd())));
                    base.addElement(r);
                }
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getIf())).getStart() == 0) {
                SyntaxPosition syntaxPositionIf = syntaxPosition;
                String scriptIFCondition = syntaxPositionIf.getGroup();
                StringBuffer scriptIF = new StringBuffer();
                List<String> ifsConditions = new ArrayList();
                List<String> ifsStatements = new ArrayList();
                int ifLevel = 0;
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalIFline = lines[z];
                    String scriptIFline = originalIFline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptIFline);
                    if (_lineDetail != null) {
                        scriptIFline = _lineDetail.getContent().trim();
                    }
                    SyntaxPosition syntaxPositionElseIf = null;
                    if (ifLevel == 0 && (syntaxPositionElseIf = syntax.matcherPosition(scriptIFline, syntax.getElseIf())).getStart() == 0) {
                        ifsConditions.add(scriptIFCondition);
                        ifsStatements.add(scriptIF.toString());
                        String condition = syntaxPositionElseIf.getGroup();
                        if (condition.trim().equals("")) {
                            condition = "1";
                        }
                        scriptIFCondition = condition;
                        scriptIF.delete(0, scriptIF.length());
                        continue;
                    } else if (ifLevel == 0 && (syntaxPositionElseIf = syntax.matcherPosition(scriptIFline, syntax.getElse())).getStart() == 0) {
                        ifsConditions.add(scriptIFCondition);
                        ifsStatements.add(scriptIF.toString());
                        scriptIFCondition = "1";
                        scriptIF.delete(0, scriptIF.length());
                        continue;
                    } else if (isStatementBegins(scriptIFline, syntax)) {
                        ifLevel++;
                    } else if (isStatementEnds(scriptIFline, syntax)) {
                        if (ifLevel == 0) {
                            ifsConditions.add(scriptIFCondition);
                            ifsStatements.add(scriptIF.toString());
                            break;
                        }
                        ifLevel--;
                    }
                    scriptIF.append(originalIFline + CajuScript.SUBLINE_LIMITER);
                }
                IfGroup ifGroup = new IfGroup(lineDetail, syntax);
                for (int i = 0; i < ifsConditions.size(); i++) {
                    String _ifCondition = ifsConditions.get(i);
                    String _ifContent = ifsStatements.get(i);
                    _ifCondition = _ifCondition.trim();
                    
                    If _if = new If(lineDetail, syntax);
                    Variable var = new Variable(lineDetail, syntax);
                    var.setValue(evalValue(var, caju, lineDetail, syntax, _ifCondition));
                    _if.setCondition(var);
                    parse(_if, caju, lineDetail, _ifContent, syntax);
                    ifGroup.addElement(_if);
                }
                base.addElement(ifGroup);
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getLoop())).getStart() == 0) {
                SyntaxPosition syntaxPositionLoop = syntaxPosition;
                String scriptLOOPCondition = syntaxPositionLoop.getGroup();
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
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getFunction())).getStart() == 0) {
                String scriptFuncDef = syntaxPosition.getGroup();
                StringBuffer scriptFUNC = new StringBuffer();
                int funcLevel = 0;
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalFUNCline = lines[z];
                    String scriptFUNCline = originalFUNCline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptFUNCline);
                    if (_lineDetail != null) {
                        scriptFUNCline = _lineDetail.getContent().trim();
                    }
                    if (syntax.matcherPosition(scriptFUNCline, syntax.getFunction()).getStart() == 0) {
                        funcLevel++;
                    } else if (isStatementBegins(scriptFUNCline, syntax)) {
                        funcLevel++;
                    } else if (isStatementEnds(scriptFUNCline, syntax)) {
                        if (funcLevel == 0) {
                            break;
                        }
                        funcLevel--;
                    }
                    scriptFUNC.append(originalFUNCline + CajuScript.SUBLINE_LIMITER);
                }
                Function func = new Function(lineDetail, syntax);
                func.setDefinition(scriptFuncDef);
                parse(func, caju, lineDetail, scriptFUNC.toString(), syntax);
                caju.setFunc(func.getName(), func);
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getTry())).getStart() == 0) {
                String scriptTRYCATCHerrorVar = syntaxPosition.getGroup();
                StringBuffer scriptTRY = new StringBuffer();
                StringBuffer scriptCATCH = new StringBuffer();
                StringBuffer scriptFINALLY = new StringBuffer();
                boolean isTry = true;
                boolean isCatch = false;
                boolean isFinally = false;
                int tryLevel = 0;
                for (int z = y + 1; z < lines.length; z++) {
                    y++;
                    String originalTRYCATCHline = lines[z];
                    String scriptTRYCATCHline = originalTRYCATCHline.trim();
                    LineDetail _lineDetail = loadLineDetail(scriptTRYCATCHline);
                    if (_lineDetail != null) {
                        scriptTRYCATCHline = _lineDetail.getContent().trim();
                    }
                    if (tryLevel == 0 && syntax.matcherPosition(scriptTRYCATCHline, syntax.getTryCatch()).getStart() == 0) {
                        isTry = false;
                        isCatch = true;
                        isFinally = false;
                        continue;
                    } else if (tryLevel == 0 && syntax.matcherPosition(scriptTRYCATCHline, syntax.getTryFinally()).getStart() == 0) {
                        isTry = false;
                        isCatch = false;
                        isFinally = true;
                        continue;
                    } else if (isStatementBegins(scriptTRYCATCHline, syntax)) {
                        tryLevel++;
                    } else if (isStatementEnds(scriptTRYCATCHline, syntax)) {
                        if (tryLevel == 0) {
                            break;
                        }
                        tryLevel--;
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
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getOperatorEqual())).getStart() > 0) {
                try {
                    int p = syntaxPosition.getStart();
                    String keys = line.substring(0, p);
                    String value = line.substring(syntaxPosition.getEnd(), line.length());
                    
                    SyntaxPosition syntaxPositionOperator = syntax.lastOperatorMathematic(keys);
                    if (syntaxPositionOperator.getEnd() == keys.length()) {
                        p = syntaxPositionOperator.getStart();
                    }
                    String[] allKeys = keys.substring(0, p).replaceAll(" ", "").split(",");
                    for (String key : allKeys) {
                        Variable var = new Variable(lineDetail, syntax);
                        var.setKey(key);
                        if (syntaxPositionOperator.getOperator() != null) {
                            Command v = new Command(lineDetail, syntax);
                            v.setCommand(key);
                            Operation operation = new Operation(lineDetail, syntax);
                            operation.setCommands(v, syntaxPositionOperator.getOperator(), evalValue(base, caju, lineDetail, syntax, value));
                            var.setValue(operation);
                        } else {
                            var.setValue(evalValue(base, caju, lineDetail, syntax, value));
                        }
                        base.addElement(var);
                    }
                } catch (CajuScriptException e) {
                    throw e;
                } catch (Exception e) {
                    throw CajuScriptException.create(caju, caju.getContext(), "Incorrect definition", e);
                }
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getImport())).getStart() == 0) {
                String path = line.substring(syntaxPosition.getEnd()).trim();
                Import i = new Import(lineDetail, syntax);
                i.setPath(path);
                base.addElement(i);
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getContinue())).getStart() == -1
                    && (syntaxPosition = syntax.matcherPosition(line, syntax.getBreak())).getStart() == 0) {
                Break b = new Break(lineDetail, syntax);
                b.setLabel(line.substring(syntaxPosition.getEnd()).trim());
                base.addElement(b);
            } else if ((syntaxPosition = syntax.matcherPosition(line, syntax.getBreak())).getStart() == -1
                    && (syntaxPosition = syntax.matcherPosition(line, syntax.getContinue())).getStart() == 0) {
                Continue c = new Continue(lineDetail, syntax);
                c.setLabel(line.substring(syntaxPosition.getEnd()).trim());
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
        if (syntax.matcherPosition(line, syntax.getIf()).getStart() == 0) {
            return true;
        } else if (syntax.matcherPosition(line, syntax.getLoop()).getStart() == 0) {
            return true;
        } else if (syntax.matcherPosition(line, syntax.getTry()).getStart() == 0) {
            return true;
        } else if (syntax.matcherPosition(line, syntax.getFunction()).getStart() == 0) {
            return true;
        }
        return false;
    }
    private boolean isStatementEnds(String line, Syntax syntax) {
        if (syntax.matcherEquals(line, syntax.getIfEnd())) {
            return true;
        } else if (syntax.matcherEquals(line, syntax.getLoopEnd())) {
            return true;
        } else if (syntax.matcherEquals(line, syntax.getTryEnd())) {
            return true;
        } else if (syntax.matcherEquals(line, syntax.getFunctionEnd())) {
            return true;
        }
        return false;
    }   
    private Element condition(Element base, CajuScript caju, LineDetail lineDetail, Syntax syntax, String script) throws CajuScriptException {
        try {
            script = script.trim();
            SyntaxPosition syntaxPositionAnd = syntax.matcherPosition(script, syntax.getOperatorAnd());
            SyntaxPosition syntaxPositionOr = syntax.matcherPosition(script, syntax.getOperatorOr());
            int s1 = syntaxPositionAnd.getStart();
            int s2 = syntaxPositionOr.getStart();
            s1 = s1 == -1 ? Integer.MAX_VALUE : s1;
            s2 = s2 == -1 ? Integer.MAX_VALUE : s2;
            int min1 = Math.min(s1, s2);
            if (s1 < Integer.MAX_VALUE && min1 == s1) {
                Operation o = new Operation(lineDetail, syntax);
                o.setCommands(condition(base, caju, lineDetail, syntax, script.substring(0, syntaxPositionAnd.getStart())), Operation.Operator.AND, condition(base, caju, lineDetail, syntax, script.substring(syntaxPositionAnd.getEnd())));
                return o;
            } else if (s2 < Integer.MAX_VALUE && min1 == s2) {
                Operation o = new Operation(lineDetail, syntax);
                o.setCommands(condition(base, caju, lineDetail, syntax, script.substring(0, syntaxPositionOr.getStart())), Operation.Operator.OR, condition(base, caju, lineDetail, syntax, script.substring(syntaxPositionOr.getEnd())));
                return o;
            } else if (!script.equals("")) {
                SyntaxPosition syntaxPosition1 = syntax.matcherPosition(script, syntax.getOperatorEqual());
                SyntaxPosition syntaxPosition2 = syntax.matcherPosition(script, syntax.getOperatorNotEqual());
                SyntaxPosition syntaxPosition3 = syntax.matcherPosition(script, syntax.getOperatorGreaterEqual());
                SyntaxPosition syntaxPosition4 = syntax.matcherPosition(script, syntax.getOperatorGreaterEqual());
                SyntaxPosition syntaxPosition5 = syntax.matcherPosition(script, syntax.getOperatorGreater());
                SyntaxPosition syntaxPosition6 = syntax.matcherPosition(script, syntax.getOperatorLess());
                int cs1 = syntaxPosition1.getStart();
                int cs2 = syntaxPosition2.getStart();
                int cs3 = syntaxPosition3.getStart();
                int cs4 = syntaxPosition4.getStart();
                int cs5 = syntaxPosition5.getStart();
                int cs6 = syntaxPosition6.getStart();
                if (cs3 > -1 || cs4 > -1) {
                    cs1 = -1;
                }
                int max = Math.max(cs1, Math.max(cs2, Math.max(cs3, Math.max(cs4, Math.max(cs5, cs6)))));
                if (max > -1) {
                    int end = -1;
                    if (cs1 > -1) {
                        end = syntaxPosition1.getEnd();
                    } else if (cs2 > -1) {
                        end = syntaxPosition2.getEnd();
                    } else if (cs3 > -1) {
                        end = syntaxPosition3.getEnd();
                    } else if (cs4 > -1) {
                        end = syntaxPosition4.getEnd();
                    } else if (cs5 > -1) {
                        end = syntaxPosition5.getEnd();
                    } else if (cs6 > -1) {
                        end = syntaxPosition6.getEnd();
                    }
                    Element e1 = evalValue(base, caju, lineDetail, syntax, script.substring(0, max));
                    Element e2 = evalValue(base, caju, lineDetail, syntax, script.substring(end));
                    Operation o = new Operation(lineDetail, syntax);
                    if (cs1 > -1) {
                        o.setCommands(e1, Operation.Operator.EQUAL, e2);
                        return o;
                    } else if (cs2 > -1) {
                        o.setCommands(e1, Operation.Operator.NOT_EQUAL, e2);
                        return o;
                    } else if (cs3 > -1) {
                        o.setCommands(e1, Operation.Operator.GREATER_EQUAL, e2);
                        return o;
                    } else if (cs4 > -1) {
                        o.setCommands(e1, Operation.Operator.LESS_EQUAL, e2);
                        return o;
                    } else if (cs5 > -1) {
                        o.setCommands(e1, Operation.Operator.GREATER, e2);
                        return o;
                    } else if (cs6 > -1) {
                        o.setCommands(e1, Operation.Operator.LESS, e2);
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
            SyntaxPosition firstOperator = syntax.firstOperatorMathematic(script);
            if (firstOperator.getStart() > -1 && !syntax.matcherEquals(script, syntax.getNumber())) {
                if (firstOperator.getStart() == 0) {
                    firstOperator = syntax.firstOperatorMathematic(script.substring(firstOperator.getEnd()));
                }
                SyntaxPosition priorityOperator = syntax.firstOperator(script, syntax.getOperatorMultiplication(), syntax.getOperatorDivision(), syntax.getOperatorModules());
                if (priorityOperator.getStart() > firstOperator.getStart()) {
                    SyntaxPosition syntaxOperator1 = syntax.lastOperatorMathematic(script.substring(0, priorityOperator.getStart()));
                    String script1 = syntaxOperator1.getStart() > -1 ? script.substring(0, syntaxOperator1.getStart()) : "";
                    Operator operator1 = syntaxOperator1.getOperator();
                    String scriptValue1 = script.substring(0, priorityOperator.getStart()).substring(0, syntaxOperator1.getEnd());
                    Operation.Operator operator = priorityOperator.getOperator();
                    SyntaxPosition syntaxOperator2 = syntax.firstOperatorMathematic(script.substring(priorityOperator.getEnd()));
                    String scriptValue2 = script.substring(priorityOperator.getEnd(), syntaxOperator2.getStart() > -1 ? syntaxOperator2.getStart() : script.length());
                    Operator operator2 = syntaxOperator2.getOperator();
                    String script2 = syntaxOperator2.getEnd() > -1 ? script.substring(syntaxOperator2.getEnd()) : "";
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
                    if (operator1 != null) {
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
                value1.setCommand(script.substring(0, firstOperator.getStart()));
                Element value2 = null;
                SyntaxPosition value2Operator = syntax.firstOperatorMathematic(script.substring(firstOperator.getEnd()));
                if (value2Operator.getStart() > -1) {
                    Command c = new Command(lineDetail, syntax);
                    c.setCommand(script.substring(firstOperator.getEnd(), firstOperator.getEnd() + value2Operator.getStart()));
                    Operation o = new Operation(lineDetail, syntax);
                    o.setCommands(c, value2Operator.getOperator(), evalValueSingle(caju, lineDetail, syntax, script.substring(firstOperator.getEnd() + value2Operator.getEnd())));
                    value2 = o;
                }
                if (value2 == null) {
                    Command c = new Command(lineDetail, syntax);
                    c.setCommand(script.substring(firstOperator.getEnd()));
                    value2 = c;
                }
                Operation operation = new Operation(lineDetail, syntax);
                operation.setCommands(value1, firstOperator.getOperator(), value2);
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
        if (staticVarsGroupCounter == Long.MAX_VALUE) {
            staticVarsGroupCounter = 0;
        }
        SyntaxPosition syntaxPosition = null;
        if ((syntaxPosition = syntax.matcherPosition(script, syntax.getFunctionCall())).getStart() > -1) {
            String varKey = CajuScript.CAJU_VARS_GROUP + staticVarsGroupCounter;
            staticVarsGroupCounter++;
            Variable var = new Variable(lineDetail, syntax);
            var.setKey(varKey);
            Command c = new Command(lineDetail, syntax);
            String cmd = syntaxPosition.getGroup();
            String functionName = cmd.substring(0, syntax.matcherPosition(cmd, syntax.getFunctionCallParametersBegin()).getStart());
            SyntaxPosition syntaxFixOperator = syntax.lastOperatorLogical(functionName);
            if (syntaxFixOperator.getEnd() > -1) {
                syntaxPosition.setStart(syntaxPosition.getStart() + syntaxFixOperator.getEnd());
                cmd = cmd.substring(syntaxFixOperator.getEnd());
            }
            syntaxFixOperator = syntax.lastOperatorConditional(functionName);
            if (syntaxFixOperator.getEnd() > -1) {
                syntaxPosition.setStart(syntaxPosition.getStart() + syntaxFixOperator.getEnd());
                cmd = cmd.substring(syntaxFixOperator.getEnd());
            }
            syntaxFixOperator = syntax.lastOperatorMathematic(functionName);
            if (syntaxFixOperator.getEnd() > -1) {
                syntaxPosition.setStart(syntaxPosition.getStart() + syntaxFixOperator.getEnd());
                cmd = cmd.substring(syntaxFixOperator.getEnd());
            }
            String cmdBase = cmd;
            SyntaxPosition syntaxParameterBegin = syntax.matcherPosition(cmdBase, syntax.getFunctionCallParametersBegin());
            SyntaxPosition syntaxParameterEnd = syntax.matcherPosition(cmdBase, syntax.getFunctionCallParametersEnd());
            if (syntaxParameterBegin.getStart() > -1 && syntaxParameterBegin.getStart() < syntaxParameterEnd.getStart()) {
                String params = cmdBase.substring(syntaxParameterBegin.getEnd(), syntaxParameterEnd.getStart());
                cmd = cmd.substring(0, syntaxParameterBegin.getEnd());
                while(true) {
                    SyntaxPosition syntaxPositionParam = syntax.matcherPosition(params, syntax.getFunctionCallParametersSeparator());
                    int lenParamSeparatorStart = syntaxPositionParam.getStart();
                    int lenParamSeparatorEnd = syntaxPositionParam.getEnd();
                    if (lenParamSeparatorEnd == -1) {
                        lenParamSeparatorStart = params.length();
                        lenParamSeparatorEnd = params.length();
                    }
                    
                    if (!params.trim().equals("")) {
                        if (staticVarsGroupCounter == Long.MAX_VALUE) {
                            staticVarsGroupCounter = 0;
                        }
                        String varParamKey = CajuScript.CAJU_VARS_GROUP + staticVarsGroupCounter;
                        staticVarsGroupCounter++;
                        Variable varParam = new Variable(lineDetail, syntax);
                        varParam.setKey(varParamKey);
                        varParam.setValue(evalValueGroup(base, caju, lineDetail, syntax, params.substring(0, lenParamSeparatorStart)));
                        base.addElement(varParam);
                        if (syntaxPositionParam.getStart() == -1) {
                            cmd += varParamKey;
                        } else {
                            cmd += varParamKey + params.substring(lenParamSeparatorStart, lenParamSeparatorEnd);
                        }
                        
                        params = params.substring(lenParamSeparatorEnd);
                    }
                    if (syntaxPositionParam.getStart() == -1) {
                        break;
                    }
                }
                cmd += cmdBase.substring(syntaxParameterEnd.getStart());
            }
            c.setCommand(cmd);
            var.setValue(c);
            base.addElement(var);
            return evalValueGroup(base, caju, lineDetail, syntax, script.replace((CharSequence)cmdBase, (CharSequence)varKey));
        } else if ((syntaxPosition = syntax.matcherPosition(script, syntax.getGroup())).getStart() > -1) {
            String varKey = CajuScript.CAJU_VARS_GROUP + staticVarsGroupCounter;
            staticVarsGroupCounter++;
            Variable var = new Variable(lineDetail, syntax);
            var.setKey(varKey);
            var.setValue(evalValue(base, caju, lineDetail, syntax, syntaxPosition.getGroup()));
            base.addElement(var);
            return evalValueGroup(base, caju, lineDetail, syntax, script.replace((CharSequence)syntaxPosition.getAllContent(), (CharSequence)varKey));
        } else {
            if ((syntaxPosition = syntax.firstOperatorConditional(script)).getStart() > -1
                || (syntaxPosition = syntax.firstOperatorLogical(script)).getStart() > -1) {
                return condition(base, caju, lineDetail, syntax, script);
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
