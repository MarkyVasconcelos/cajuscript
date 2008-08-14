/*
 * Syntax.java
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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.cajuscript.parser.Operation.Operator;

/**
 * Script syntax.
 * <p>Java Basic style:</p>
 * <p><blockquote><pre>
 *      Syntax syntaxJ = new Syntax();
 *      syntaxJ.setIf("if ");
 *      syntaxJ.setIfBegin("{");
 *      syntaxJ.setElseIf("} else if ");
 *      syntaxJ.setElseIfBegin("{");
 *      syntaxJ.setElse("} else {");
 *      syntaxJ.setIfEnd("}");
 *      syntaxJ.setLoop("while ");
 *      syntaxJ.setLoopBegin("{");
 *      syntaxJ.setLoopEnd("}");
 *      syntaxJ.setTry("try ");
 *      syntaxJ.setTryBegin("{");
 *      syntaxJ.setTryCatch("} catch {");
 *      syntaxJ.setTryEnd("}");
 *      syntaxJ.setFunction("function ");
 *      syntaxJ.setFunctionBegin("{");
 *      syntaxJ.setFunctionEnd("}");
 *      syntaxJ.setReturn("return");
 *      syntaxJ.setImport("import ");
 *      syntaxJ.setRootContext("root.");
 *      syntaxJ.setContinue("continue");
 *      syntaxJ.setBreak("break");
 *      // Registering:
 *      CajuScript.addGlobalSyntax("CajuJava", syntaxJ);
 *      // To use set the first line of script: caju.syntax CajuJava
 * </pre></blockquote></p>
 * <p>Syntax Basic style:</p>
 * <p><blockquote><pre>
 *      Syntax syntaxB = new Syntax();
 *      syntaxB.setIf("if ");
 *      syntaxB.setIfBegin("");
 *      syntaxB.setElseIf("elseif ");
 *      syntaxB.setElseIfBegin("");
 *      syntaxB.setElse("else");
 *      syntaxB.setIfEnd("end");
 *      syntaxB.setLoop("while ");
 *      syntaxB.setLoopBegin("");
 *      syntaxB.setLoopEnd("end");
 *      syntaxB.setTry("try ");
 *      syntaxB.setTryBegin("");
 *      syntaxB.setTryCatch("catch");
 *      syntaxB.setTryEnd("end");
 *      syntaxB.setFunction("function ");
 *      syntaxB.setFunctionBegin("");
 *      syntaxB.setFunctionEnd("end");
 *      syntaxB.setReturn("return");
 *      syntaxB.setImport("import ");
 *      syntaxB.setRootContext("root.");
 *      syntaxB.setContinue("continue");
 *      syntaxB.setBreak("break");
 *      // Registering:
 *      CajuScript.addGlobalSyntax("CajuBasic", syntaxB);
 *      // To use set the first line of script: caju.syntax CajuBasic
 * </pre></blockquote></p>
 * @author eduveks
 */
public class Syntax {
    private Pattern operatorAddition = Pattern.compile("\\+");
    private Pattern operatorSubtraction = Pattern.compile("\\-");
    private Pattern operatorMultiplication = Pattern.compile("\\*");
    private Pattern operatorDivision = Pattern.compile("\\/");
    private Pattern operatorModules = Pattern.compile("\\%");
    private Pattern operatorAnd = Pattern.compile("\\&");
    private Pattern operatorOr = Pattern.compile("\\|");
    private Pattern operatorEqual = Pattern.compile("\\=");
    private Pattern operatorNotEqual = Pattern.compile("\\!");
    private Pattern operatorLess = Pattern.compile("\\<");
    private Pattern operatorGreater = Pattern.compile("\\>");
    private Pattern operatorLessEqual = Pattern.compile("\\<\\s*\\=");
    private Pattern operatorGreaterEqual = Pattern.compile("\\>\\s*\\=");
    private Pattern number = Pattern.compile("\\-*\\d+[\\.\\d+]*");
    private Pattern ifStart = Pattern.compile("([^\\?]+)\\?");
    private Pattern elseIfStart = Pattern.compile("\\?\\s*(.+)\\s*\\?");
    private Pattern elseStart = Pattern.compile("\\?\\s*\\?");
    private Pattern ifEnd = Pattern.compile("\\?");
    private Pattern loopStart = Pattern.compile("([^\\@]+)\\@");
    private Pattern loopEnd = Pattern.compile("\\@");
    private Pattern functionStart = Pattern.compile("([^\\#]+)\\s*(.+)\\s*\\#");
    private Pattern functionEnd = Pattern.compile("\\#");
    private Pattern tryStart = Pattern.compile("([^\\^]+)\\^");
    private Pattern tryCatchStart = Pattern.compile("\\^\\s*\\^");
    private Pattern tryFinallyStart = Pattern.compile("\\^\\s*\\~\\s*\\^");
    private Pattern tryEnd = Pattern.compile("\\^");
    private Pattern _return = Pattern.compile("\\~");
    private Pattern _import = Pattern.compile("\\$");
    private Pattern _null = Pattern.compile("\\$");
    private Pattern _break = Pattern.compile("\\!\\s*\\!");
    private Pattern _continue = Pattern.compile("\\.\\s*\\.");
    private Pattern rootContext = Pattern.compile("\\.");
    private Pattern label = Pattern.compile("\\:");
    private Pattern[] comments = new Pattern[]{Pattern.compile("\\\\"), Pattern.compile("\\-\\s*\\-"), Pattern.compile("\\/\\s*\\/")};
    private Pattern group = Pattern.compile("\\(([[^\\(\\)]|[.]]*)\\)");
    private Pattern functionCall = Pattern.compile("[\\w|\\s|\\.]*[\\w]+[\\w|\\s|\\.]*\\([[^\\(\\)]|[.]]*\\)");
    private Pattern functionCallPathSeparator = Pattern.compile("\\.");
    private Pattern functionCallParametersBegin = Pattern.compile("\\(");
    private Pattern functionCallParametersSeparator = Pattern.compile("\\,");
    private Pattern functionCallParametersEnd = Pattern.compile("\\)");

    /**
     * Create new Syntax.
     */
    public Syntax() {

    }

    /**
     * Get If syntax. Default "". Sample: "if ".
     * @return If.
     */
    public Pattern getIf() {
        return ifStart;
    }

    /**
     * Get If end syntax. Default "?". Sample: "end", "}".
     * @return If end.
     */
    public Pattern getIfEnd() {
        return ifEnd;
    }

    /**
     * Get ElseIF syntax. Default "?". Sample: "elseif ", "} else if "
     * @return ElseIF.
     */
    public Pattern getElseIf() {
        return elseIfStart;
    }

    /**
     * Get Else syntax. Default "??". Sample: "else", "} else {"
     * @return Else.
     */
    public Pattern getElse() {
        return elseStart;
    }

    /**
     * Get Loop syntax. Default "". Sample: "while ".
     * @return Loop.
     */
    public Pattern getLoop() {
        return loopStart;
    }

    /**
     * Get Loop end syntax. Default "@". Sample: "end", "}".
     * @return Loop end.
     */
    public Pattern getLoopEnd() {
        return loopEnd;
    }

    /**
     * Get Function syntax. Default "". Sample: "function ".
     * @return Function.
     */
    public Pattern getFunction() {
        return functionStart;
    }

    /**
     * Get Function end syntax. Default "#". Sample: "end", "}".
     * @return Function end.
     */
    public Pattern getFunctionEnd() {
        return functionEnd;
    }

    /**
     * Get Try syntax. Default "". Sample: "try ".
     * @return Try.
     */
    public Pattern getTry() {
        return tryStart;
    }

    /**
     * Get Try end syntax. Default "^". Sample: "end", "}".
     * @return Try end.
     */
    public Pattern getTryEnd() {
        return tryEnd;
    }

    /**
     * Get Catch syntax. Default "^^". Sample: "catch", "} catch {".
     * @return Catch.
     */
    public Pattern getTryCatch() {
        return tryCatchStart;
    }

    /**
     * Get Finally syntax. Default "^~^". Sample: "finally", "} finally {".
     * @return Finally.
     */
    public Pattern getTryFinally() {
        return tryFinallyStart;
    }

    /**
     * Get Import syntax. Default "$". Sample: "import ", "using ".
     * @return Import.
     */
    public Pattern getImport() {
        return _import;
    }

    /**
     * Get Null syntax. Default "$". Sample: "null", "nil".
     * @return Import.
     */
    public Pattern getNull() {
        return _null;
    }

    /**
     * Get Return syntax. Default "~". Sample: "return ".
     * @return Return.
     */
    public Pattern getReturn() {
        return _return;
    }

    /**
     * Get Break syntax. Default "!!". Sample: "break".
     * @return Break.
     */
    public Pattern getBreak() {
        return _break;
    }

    /**
     * Get Continue syntax. Default "..". Sample: "continue".
     * @return Continue.
     */
    public Pattern getContinue() {
        return _continue;
    }

    /**
     * Get Root Context syntax. Default: ".".
     * @return Root context.
     */
    public Pattern getRootContext() {
        return rootContext;
    }

    /**
     * Get Addition operator syntax. Default: "+".
     * @return Addition operator.
     */
    public Pattern getOperatorAddition() {
        return operatorAddition;
    }

    /**
     * Get Subtraction operator syntax. Default: "-".
     * @return Subtraction operator.
     */
    public Pattern getOperatorSubtraction() {
        return operatorSubtraction;
    }

    /**
     * Get Multiplication operator syntax. Default: "*".
     * @return Multiplication operator.
     */
    public Pattern getOperatorMultiplication() {
        return operatorMultiplication;
    }

    /**
     * Get Division operator syntax. Default: "/".
     * @return Division operator.
     */
    public Pattern getOperatorDivision() {
        return operatorDivision;
    }

    /**
     * Get Modules operator syntax. Default: "%".
     * @return Modules operator.
     */
    public Pattern getOperatorModules() {
        return operatorModules;
    }

    /**
     * Get And operator syntax. Default: "&".
     * @return And operator.
     */
    public Pattern getOperatorAnd() {
        return operatorAnd;
    }

    /**
     * Get Or operator syntax. Default: "|".
     * @return Or operator.
     */
    public Pattern getOperatorOr() {
        return operatorOr;
    }

    /**
     * Get Equal operator syntax. Default: "=".
     * @return Equal operator.
     */
    public Pattern getOperatorEqual() {
        return operatorEqual;
    }

    /**
     * Get Not Equal operator syntax. Default: "!".
     * @return Not Equal operator.
     */
    public Pattern getOperatorNotEqual() {
        return operatorNotEqual;
    }

    /**
     * Get Less operator syntax. Default: "&lt;".
     * @return Less operator.
     */
    public Pattern getOperatorLess() {
        return operatorLess;
    }

    /**
     * Get Greater operator syntax. Default: "&gt;".
     * @return Greater operator.
     */
    public Pattern getOperatorGreater() {
        return operatorGreater;
    }

    /**
     * Get Less Equal operator syntax. Default: "&lt;=".
     * @return Less Equal operator.
     */
    public Pattern getOperatorLessEqual() {
        return operatorLessEqual;
    }

    /**
     * Get Greater Equal operator syntax. Default: "&gt;=".
     * @return Greater Equal operator.
     */
    public Pattern getOperatorGreaterEqual() {
        return operatorGreaterEqual;
    }

    public Pattern getNumber() {
        return number;
    }
    
    /**
     * Get Label signal. Default: ":".
     * @return Label signal.
     */
    public Pattern getLabel() {
        return label;
    }

    /**
     * Get Comments. Default: "\", "--", "//".
     * @return Comments.
     */
    public Pattern[] getComments() {
        return comments;
    }

    public Pattern getGroup() {
        return group;
    }

    public Pattern getFunctionCall() {
        return functionCall;
    }

    public Pattern getFunctionCallPathSeparator() {
        return functionCallPathSeparator;
    }

    public Pattern getFunctionCallParametersBegin() {
        return functionCallParametersBegin;
    }

    public Pattern getFunctionCallParametersEnd() {
        return functionCallParametersEnd;
    }

    public Pattern getFunctionCallParametersSeparator() {
        return functionCallParametersSeparator;
    }

    /**
     * Set If syntax. Default "". Sample: "if ".
     * @param ifStart If.
     */
    public void setIf(Pattern ifStart) {
        this.ifStart = ifStart;
    }

    /**
     * Set If end syntax. Default "?". Sample: "end", "}".
     * @param ifEnd If end.
     */
    public void setIfEnd(Pattern ifEnd) {
        this.ifEnd = ifEnd;
    }

    /**
     * Set ElseIF syntax. Default "?". Sample: "elseif ", "} else if "
     * @param elseIfStart ElseIF.
     */
    public void setElseIf(Pattern elseIfStart) {
        this.elseIfStart = elseIfStart;
    }

    /**
     * Set Else syntax. Default "??". Sample: "else", "} else {"
     * @param elseStart Else.
     */
    public void setElse(Pattern elseStart) {
        this.elseStart = elseStart;
    }

    /**
     * Set Loop syntax. Default "". Sample: "while ".
     * @param loopStart Loop.
     */
    public void setLoop(Pattern loopStart) {
        this.loopStart = loopStart;
    }

    /**
     * Set Loop end syntax. Default "@". Sample: "}", "end".
     * @param loopEnd Loop end.
     */
    public void setLoopEnd(Pattern loopEnd) {
        this.loopEnd = loopEnd;
    }

    /**
     * Set Function syntax. Default "". Sample: "function ".
     * @param functionStart Function.
     */
    public void setFunction(Pattern functionStart) {
        this.functionStart = functionStart;
    }

    /**
     * Set Function end syntax. Default "#". Sample: "end", "}".
     * @param functionEnd Function end.
     */
    public void setFunctionEnd(Pattern functionEnd) {
        this.functionEnd = functionEnd;
    }

    /**
     * Set Try syntax. Default "". Sample: "try ".
     * @param tryStart Try.
     */
    public void setTry(Pattern tryStart) {
        this.tryStart = tryStart;
    }

    /**
     * Set Try end syntax. Default "^". Sample: "}", "end".
     * @param tryEnd Try end.
     */
    public void setTryEnd(Pattern tryEnd) {
        this.tryEnd = tryEnd;
    }

    /**
     * Set Catch syntax. Default "^^". Sample: "catch", "} catch {".
     * @param catchStart Catch.
     */
    public void setTryCatch(Pattern catchStart) {
        this.tryCatchStart = catchStart;
    }

    /**
     * Set Finally syntax. Default "^~^". Sample: "finally", "} finally {".
     * @param finallyStart Finally.
     */
    public void setTryFinally(Pattern finallyStart) {
        this.tryFinallyStart = finallyStart;
    }

    /**
     * Set Import syntax. Default "$". Sample: "import ", "using ".
     * @param i Import.
     */
    public void setImport(Pattern i) {
        this._import = i;
    }

    /**
     * Set Null syntax. Default "$". Sample: "null", "nil".
     * @param n Import.
     */
    public void setNull(Pattern n) {
        this._null = n;
    }

    /**
     * Set Return syntax. Default "~". Sample: "return ".
     * @param r Return.
     */
    public void setReturn(Pattern r) {
        this._return = r;
    }

    /**
     * Set Break syntax. Default "!!". Sample: "break".
     * @param b Break.
     */
    public void setBreak(Pattern b) {
        this._break = b;
    }

    /**
     * Set Continue syntax. Default "..". Sample: "continue".
     * @param c Continue.
     */
    public void setContinue(Pattern c) {
        this._continue = c;
    }

    /**
     * Set Root Context syntax. Default: ".".
     * @param c Root Context.
     */
    public void setRootContext(Pattern c) {
        this.rootContext = c;
    }

    /**
     * Set Addition operator syntax. Default: "+".
     * @param operatorAddition Addition operator.
     */
    public void setOperatorAddition(Pattern operatorAddition) {
        this.operatorAddition = operatorAddition;
    }

    /**
     * Set Subtraction operator syntax. Default: "-".
     * @param operatorSubtraction Subtraction operator.
     */
    public void setOperatorSubtraction(Pattern operatorSubtraction) {
        this.operatorSubtraction = operatorSubtraction;
    }

    /**
     * Set Multiplication operator syntax. Default: "*".
     * @param operatorMultiplication Multiplication operator.
     */
    public void setOperatorMultiplication(Pattern operatorMultiplication) {
        this.operatorMultiplication = operatorMultiplication;
    }

    /**
     * Set Division operator syntax. Default: "/".
     * @param operatorDivision Division operator.
     */
    public void setOperatorDivision(Pattern operatorDivision) {
        this.operatorDivision = operatorDivision;
    }

    /**
     * Set Modules operator syntax. Default: "%".
     * @param operatorModules Modules operator.
     */
    public void setOperatorModules(Pattern operatorModules) {
        this.operatorModules = operatorModules;
    }

    /**
     * Set And operator syntax. Default: "&".
     * @param operatorAnd And operator.
     */
    public void setOperatorAnd(Pattern operatorAnd) {
        this.operatorAnd = operatorAnd;
    }

    /**
     * Set Or operator syntax. Default: "|".
     * @param operatorOr Or operator.
     */
    public void setOperatorOr(Pattern operatorOr) {
        this.operatorOr = operatorOr;
    }

    /**
     * Set Equal operator syntax. Default: "=".
     * @param operatorEqual Equal operator.
     */
    public void setOperatorEqual(Pattern operatorEqual) {
        this.operatorEqual = operatorEqual;
    }

    /**
     * Set Not Equal operator syntax. Default: "!".
     * @param operatorNotEqual Not Equal operator.
     */
    public void setOperatorNotEqual(Pattern operatorNotEqual) {
        this.operatorNotEqual = operatorNotEqual;
    }

    /**
     * Set Less operator syntax. Default: "&lt;".
     * @param operatorLess Less operator.
     */
    public void setOperatorLess(Pattern operatorLess) {
        this.operatorLess = operatorLess;
    }

    /**
     * Set Greater operator syntax. Default: "&gt;".
     * @param operatorGreater Greater operator.
     */
    public void setOperatorGreater(Pattern operatorGreater) {
        this.operatorGreater = operatorGreater;
    }

    /**
     * Set Less Equal operator syntax. Default: "&lt;=".
     * @param operatorLessEqual Less Equal operator.
     */
    public void setOperatorLessEqual(Pattern operatorLessEqual) {
        this.operatorLessEqual = operatorLessEqual;
    }

    /**
     * Set Greater Equal operator syntax. Default: "&gt;=".
     * @param operatorGreaterEqual Greater Equal operator.
     */
    public void setOperatorGreaterEqual(Pattern operatorGreaterEqual) {
        this.operatorGreaterEqual = operatorGreaterEqual;
    }

    public void setNumber(Pattern number) {
        this.number = number;
    }

    /**
     * Set Label signal. Default: ":".
     * @param label Label signal.
     */
    public void setLabel(Pattern label) {
        this.label = label;
    }

    /**
     * Set Comments. Default: "\", "--", "//".
     * @param comments Comments.
     */
    public void setComments(Pattern[] comments) {
        this.comments = comments;
    }

    public void setGroup(Pattern group) {
        this.group = group;
    }

    public void setFunctionCall(Pattern functionCall) {
        this.functionCall = functionCall;
    }

    public void setFunctionCallPathSeparator(Pattern functionCallPathSeparator) {
        this.functionCallPathSeparator = functionCallPathSeparator;
    }

    public void setFunctionCallParametersBegin(Pattern functionCallParametersBegin) {
        this.functionCallParametersBegin = functionCallParametersBegin;
    }

    public void setFunctionCallParametersEnd(Pattern functionCallParametersEnd) {
        this.functionCallParametersEnd = functionCallParametersEnd;
    }

    public void setFunctionCallParametersSeparator(Pattern functionCallParametersSeparator) {
        this.functionCallParametersSeparator = functionCallParametersSeparator;
    }

    public SyntaxPosition matcherPosition(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            SyntaxPosition position = new SyntaxPosition(this, pattern);
            position.setAllContent(matcher.group());
            if (matcher.groupCount() > 0) {
                position.setGroup(matcher.group(1));
            } else {
                position.setGroup(matcher.group());
            }
            position.setStart(matcher.start());
            position.setEnd(matcher.end());
            return position;
        } else {
            SyntaxPosition position = new SyntaxPosition(this, pattern);
            position.setStart(-1);
            position.setEnd(-1);
            return position;
        }
    }

    public boolean matcherEquals(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    public SyntaxPosition firstOperatorLogical(String script) {
        return firstOperator(script, getOperatorAnd(), getOperatorOr());
    }

    public SyntaxPosition lastOperatorLogical(String script) {
        return lastOperator(script, getOperatorAnd(), getOperatorOr());
    }

    public SyntaxPosition firstOperatorConditional(String script) {
        return firstOperator(script, getOperatorEqual(), getOperatorNotEqual(),
                getOperatorGreater(), getOperatorLess(), getOperatorGreaterEqual(), getOperatorLessEqual());
    }

    public SyntaxPosition lastOperatorConditional(String script) {
        return lastOperator(script, getOperatorEqual(), getOperatorNotEqual(),
                getOperatorGreater(), getOperatorLess(), getOperatorGreaterEqual(), getOperatorLessEqual());
    }

    public SyntaxPosition firstOperatorMathematic(String script) {
        return firstOperator(script, getOperatorAddition(), getOperatorSubtraction(),
                getOperatorMultiplication(), getOperatorDivision(), getOperatorModules());
    }

    public SyntaxPosition lastOperatorMathematic(String script) {
        return lastOperator(script, getOperatorAddition(), getOperatorSubtraction(),
                getOperatorMultiplication(), getOperatorDivision(), getOperatorModules());
    }

    public SyntaxPosition firstOperator(String script, Pattern... patterns) {
        SyntaxPosition[] syntaxPositions = new SyntaxPosition[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            syntaxPositions[i] = matcherPosition(script, patterns[i]);
        }
        SyntaxPosition first = new SyntaxPosition(this, Pattern.compile(""));
        for (int i = 0; i < syntaxPositions.length; i++) {
            if (syntaxPositions[i].getStart() > -1 && (first.getStart() == -1 || syntaxPositions[i].getStart() < first.getStart())) {
                first = syntaxPositions[i];
            }
        }
        return first;
    }

    public SyntaxPosition lastOperator(String script, Pattern... patterns) {
        SyntaxPosition[] syntaxPositions = new SyntaxPosition[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            syntaxPositions[i] = matcherPosition(script, patterns[i]);
        }
        SyntaxPosition last = new SyntaxPosition(this, Pattern.compile(""));
        for (int i = 0; i < syntaxPositions.length; i++) {
            if (syntaxPositions[i].getStart() > -1 && (syntaxPositions[i].getStart() > last.getStart())) {
                last = syntaxPositions[i];
            }
        }
        return last;
    }
}
