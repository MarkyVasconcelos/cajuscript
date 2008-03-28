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
    private char operatorAddition = '+';
    private char operatorSubtraction = '-';
    private char operatorMultiplication = '*';
    private char operatorDivision = '/';
    private char operatorModules = '%';
    private String operatorAnd = "&";
    private String operatorOr = "|";
    private String operatorEqual = "=";
    private String operatorNotEqual = "!";
    private String operatorLess = "<";
    private String operatorGreater = ">";
    private String operatorLessEqual = "<=";
    private String operatorGreaterEqual = ">=";
    private String ifStart = "";
    private String ifBegin = "?";
    private String elseIfStart = "?";
    private String elseIfBegin = "?";
    private String elseStart = "??";
    private String ifEnd = "?";
    private String loopStart = "";
    private String loopBegin = "@";
    private String loopEnd = "@";
    private String functionStart = "";
    private String functionBegin = "#";
    private String functionEnd = "#";
    private String tryStart = "";
    private String tryBegin = "^";
    private String tryCatchStart = "^^";
    private String tryFinallyStart = "^~^";
    private String tryEnd = "^";
    private String _return = "~";
    private String _import = "$";
    private String _null = "$";
    private String _break = "!!";
    private String _continue = "..";
    private String rootContext = ".";
    private String label = ":";
    private String[] comments = new String[] { "\\", "--", "//" };
    /**
     * Create new Syntax.
     */
    public Syntax() {
        
    }
    /**
     * Get If syntax. Default "". Sample: "if ".
     * @return If.
     */
    public String getIf() {
        return ifStart;
    }
    /**
     * Get If begin syntax. Default "?". Sample: "", "{".
     * @return If begin.
     */
    public String getIfBegin() {
        return ifBegin;
    }
    /**
     * Get If end syntax. Default "?". Sample: "end", "}".
     * @return If end.
     */
    public String getIfEnd() {
        return ifEnd;
    }
    /**
     * Get ElseIF syntax. Default "?". Sample: "elseif ", "} else if "
     * @return ElseIF.
     */
    public String getElseIf() {
        return elseIfStart;
    }
    /**
     * Get ElseIf begin syntax. Default "?". Sample: "", "{".
     * @return ElseIf begin.
     */
    public String getElseIfBegin() {
        return elseIfBegin;
    }
    /**
     * Get Else syntax. Default "??". Sample: "else", "} else {"
     * @return Else.
     */
    public String getElse() {
        return elseStart;
    }
    /**
     * Get Loop syntax. Default "". Sample: "while ".
     * @return Loop.
     */
    public String getLoop() {
        return loopStart;
    }
    /**
     * Get Loop begin syntax. Default "@". Sample: "", "{".
     * @return Loop begin.
     */
    public String getLoopBegin() {
        return loopBegin;
    }
    /**
     * Get Loop end syntax. Default "@". Sample: "end", "}".
     * @return Loop end.
     */
    public String getLoopEnd() {
        return loopEnd;
    }
    /**
     * Get Function syntax. Default "". Sample: "function ".
     * @return Function.
     */
    public String getFunction() {
        return functionStart;
    }
    /**
     * Get Function begin syntax. Default "#". Sample: "", "{".
     * @return Function begin.
     */
    public String getFunctionBegin() {
        return functionBegin;
    }
    /**
     * Get Function end syntax. Default "#". Sample: "end", "}".
     * @return Function end.
     */
    public String getFunctionEnd() {
        return functionEnd;
    }
    /**
     * Get Try syntax. Default "". Sample: "try ".
     * @return Try.
     */
    public String getTry() {
        return tryStart;
    }
    /**
     * Get Try begin syntax. Default "^". Sample: "", "{".
     * @return Try begin.
     */
    public String getTryBegin() {
        return tryBegin;
    }
    /**
     * Get Try end syntax. Default "^". Sample: "end", "}".
     * @return Try end.
     */
    public String getTryEnd() {
        return tryEnd;
    }
    /**
     * Get Catch syntax. Default "^^". Sample: "catch", "} catch {".
     * @return Catch.
     */
    public String getTryCatch() {
        return tryCatchStart;
    }
    /**
     * Get Finally syntax. Default "^~^". Sample: "finally", "} finally {".
     * @return Finally.
     */
    public String getTryFinally() {
        return tryFinallyStart;
    }
    /**
     * Get Import syntax. Default "$". Sample: "import ", "using ".
     * @return Import.
     */
    public String getImport() {
        return _import;
    }
    /**
     * Get Null syntax. Default "$". Sample: "null", "nil".
     * @return Import.
     */
    public String getNull() {
        return _null;
    }
    /**
     * Get Return syntax. Default "~". Sample: "return ".
     * @return Return.
     */
    public String getReturn() {
        return _return;
    }
    /**
     * Get Break syntax. Default "!!". Sample: "break".
     * @return Break.
     */
    public String getBreak() {
        return _break;
    }
    /**
     * Get Continue syntax. Default "..". Sample: "continue".
     * @return Continue.
     */
    public String getContinue() {
        return _continue;
    }
    /**
     * Get Root Context syntax. Default: ".".
     * @return Root context.
     */
    public String getRootContext() {
        return rootContext;
    }
    /**
     * Get Addition operator syntax. Default: "+".
     * @return Addition operator.
     */
    public char getOperatorAddition() {
        return operatorAddition;
    }
    /**
     * Get Subtraction operator syntax. Default: "-".
     * @return Subtraction operator.
     */
    public char getOperatorSubtraction() {
        return operatorSubtraction;
    }
    /**
     * Get Multiplication operator syntax. Default: "*".
     * @return Multiplication operator.
     */
    public char getOperatorMultiplication() {
        return operatorMultiplication;
    }
    /**
     * Get Division operator syntax. Default: "/".
     * @return Division operator.
     */
    public char getOperatorDivision() {
        return operatorDivision;
    }
    /**
     * Get Modules operator syntax. Default: "%".
     * @return Modules operator.
     */
    public char getOperatorModules() {
        return operatorModules;
    }
    /**
     * Get And operator syntax. Default: "&".
     * @return And operator.
     */
    public String getOperatorAnd() {
        return operatorAnd;
    }
    /**
     * Get Or operator syntax. Default: "|".
     * @return Or operator.
     */
    public String getOperatorOr() {
        return operatorOr;
    }
    /**
     * Get Equal operator syntax. Default: "=".
     * @return Equal operator.
     */
    public String getOperatorEqual() {
        return operatorEqual;
    }
    /**
     * Get Not Equal operator syntax. Default: "!".
     * @return Not Equal operator.
     */
    public String getOperatorNotEqual() {
        return operatorNotEqual;
    }
    /**
     * Get Less operator syntax. Default: "&lt;".
     * @return Less operator.
     */
    public String getOperatorLess() {
        return operatorLess;
    }
    /**
     * Get Greater operator syntax. Default: "&gt;".
     * @return Greater operator.
     */
    public String getOperatorGreater() {
        return operatorGreater;
    }
    /**
     * Get Less Equal operator syntax. Default: "&lt;=".
     * @return Less Equal operator.
     */
    public String getOperatorLessEqual() {
        return operatorLessEqual;
    }
    /**
     * Get Greater Equal operator syntax. Default: "&gt;=".
     * @return Greater Equal operator.
     */
    public String getOperatorGreaterEqual() {
        return operatorGreaterEqual;
    }
    /**
     * Get Label signal. Default: ":".
     * @return Label signal.
     */
    public String getLabel() {
        return label;
    }
    /**
     * Get Comments. Default: "\", "--", "//".
     * @return Comments.
     */
    public String[] getComments() {
        return comments;
    }
    /**
     * Set If syntax. Default "". Sample: "if ".
     * @param ifStart If.
     */
    public void setIf(String ifStart) {
        this.ifStart = ifStart;
    }
    /**
     * Set If begin syntax. Default "?". Sample: "", "{".
     * @param ifBegin If begin.
     */
    public void setIfBegin(String ifBegin) {
        this.ifBegin = ifBegin;
    }
    /**
     * Set If end syntax. Default "?". Sample: "end", "}".
     * @param ifEnd If end.
     */
    public void setIfEnd(String ifEnd) {
        this.ifEnd = ifEnd;
    }
    /**
     * Set ElseIF syntax. Default "?". Sample: "elseif ", "} else if "
     * @param elseIfStart ElseIF.
     */
    public void setElseIf(String elseIfStart) {
        this.elseIfStart = elseIfStart;
    }
    /**
     * Set ElseIf begin syntax. Default "?". Sample: "", "{".
     * @param elseIfBegin ElseIf begin.
     */
    public void setElseIfBegin(String elseIfBegin) {
        this.elseIfBegin = elseIfBegin;
    }
    /**
     * Set Else syntax. Default "??". Sample: "else", "} else {"
     * @param elseStart Else.
     */
    public void setElse(String elseStart) {
        this.elseStart = elseStart;
    }
    /**
     * Set Loop syntax. Default "". Sample: "while ".
     * @param loopStart Loop.
     */
    public void setLoop(String loopStart) {
        this.loopStart = loopStart;
    }
    /**
     * Set Loop begin syntax. Default "@". Sample: "", "{".
     * @param loopBegin Loop begin.
     */
    public void setLoopBegin(String loopBegin) {
        this.loopBegin = loopBegin;
    }
    /**
     * Set Loop end syntax. Default "@". Sample: "}", "end".
     * @param loopEnd Loop end.
     */
    public void setLoopEnd(String loopEnd) {
        this.loopEnd = loopEnd;
    }
    /**
     * Set Function syntax. Default "". Sample: "function ".
     * @param functionStart Function.
     */
    public void setFunction(String functionStart) {
        this.functionStart = functionStart;
    }
    /**
     * Set Function begin syntax. Default "#". Sample: "", "{".
     * @param functionBegin Function begin.
     */
    public void setFunctionBegin(String functionBegin) {
        this.functionBegin = functionBegin;
    }
    /**
     * Set Function end syntax. Default "#". Sample: "end", "}".
     * @param functionEnd Function end.
     */
    public void setFunctionEnd(String functionEnd) {
        this.functionEnd = functionEnd;
    }
    /**
     * Set Try syntax. Default "". Sample: "try ".
     * @param tryStart Try.
     */
    public void setTry(String tryStart) {
        this.tryStart = tryStart;
    }
    /**
     * Set Try begin syntax. Default "^". Sample: "", "{".
     * @param tryBegin Try begin.
     */
    public void setTryBegin(String tryBegin) {
        this.tryBegin = tryBegin;
    }
    /**
     * Set Try end syntax. Default "^". Sample: "}", "end".
     * @param tryEnd Try end.
     */
    public void setTryEnd(String tryEnd) {
        this.tryEnd = tryEnd;
    }
    /**
     * Set Catch syntax. Default "^^". Sample: "catch", "} catch {".
     * @param catchStart Catch.
     */
    public void setTryCatch(String catchStart) {
        this.tryCatchStart = catchStart;
    }
    /**
     * Set Finally syntax. Default "^~^". Sample: "finally", "} finally {".
     * @param finallyStart Finally.
     */
    public void setTryFinally(String finallyStart) {
        this.tryFinallyStart = finallyStart;
    }
    /**
     * Set Import syntax. Default "$". Sample: "import ", "using ".
     * @param i Import.
     */
    public void setImport(String i) {
        this._import = i;
    }
    /**
     * Set Null syntax. Default "$". Sample: "null", "nil".
     * @param n Import.
     */
    public void setNull(String n) {
        this._null = n;
    }
    /**
     * Set Return syntax. Default "~". Sample: "return ".
     * @param r Return.
     */
    public void setReturn(String r) {
        this._return = r;
    }
    /**
     * Set Break syntax. Default "!!". Sample: "break".
     * @param b Break.
     */
    public void setBreak(String b) {
        this._break = b;
    }
    /**
     * Set Continue syntax. Default "..". Sample: "continue".
     * @param c Continue.
     */
    public void setContinue(String c) {
        this._continue = c;
    }
    /**
     * Set Root Context syntax. Default: ".".
     * @param c Root Context.
     */
    public void setRootContext(String c) {
        this.rootContext = c;
    }
    /**
     * Set Addition operator syntax. Default: "+".
     * @param operatorAddition Addition operator.
     */
    public void setOperatorAddition(char operatorAddition) {
        this.operatorAddition = operatorAddition;
    }
    /**
     * Set Subtraction operator syntax. Default: "-".
     * @param operatorSubtraction Subtraction operator.
     */
    public void setOperatorSubtraction(char operatorSubtraction) {
        this.operatorSubtraction = operatorSubtraction;
    }
    /**
     * Set Multiplication operator syntax. Default: "*".
     * @param operatorMultiplication Multiplication operator.
     */
    public void setOperatorMultiplication(char operatorMultiplication) {
        this.operatorMultiplication = operatorMultiplication;
    }
    /**
     * Set Division operator syntax. Default: "/".
     * @param operatorDivision Division operator.
     */
    public void setOperatorDivision(char operatorDivision) {
        this.operatorDivision = operatorDivision;
    }
    /**
     * Set Modules operator syntax. Default: "%".
     * @param operatorModules Modules operator.
     */
    public void setOperatorModules(char operatorModules) {
        this.operatorModules = operatorModules;
    }
    /**
     * Set And operator syntax. Default: "&".
     * @param operatorAnd And operator.
     */
    public void setOperatorAnd(String operatorAnd) {
        this.operatorAnd = operatorAnd;
    }
    /**
     * Set Or operator syntax. Default: "|".
     * @param operatorOr Or operator.
     */
    public void setOperatorOr(String operatorOr) {
        this.operatorOr = operatorOr;
    }
    /**
     * Set Equal operator syntax. Default: "=".
     * @param operatorEqual Equal operator.
     */
    public void setOperatorEqual(String operatorEqual) {
        this.operatorEqual = operatorEqual;
    }
    /**
     * Set Not Equal operator syntax. Default: "!".
     * @param operatorNotEqual Not Equal operator.
     */
    public void setOperatorNotEqual(String operatorNotEqual) {
        this.operatorNotEqual = operatorNotEqual;
    }
    /**
     * Set Less operator syntax. Default: "&lt;".
     * @param operatorLess Less operator.
     */
    public void setOperatorLess(String operatorLess) {
        this.operatorLess = operatorLess;
    }
    /**
     * Set Greater operator syntax. Default: "&gt;".
     * @param operatorGreater Greater operator.
     */
    public void setOperatorGreater(String operatorGreater) {
        this.operatorGreater = operatorGreater;
    }
    /**
     * Set Less Equal operator syntax. Default: "&lt;=".
     * @param operatorLessEqual Less Equal operator.
     */
    public void setOperatorLessEqual(String operatorLessEqual) {
        this.operatorLessEqual = operatorLessEqual;
    }
    /**
     * Set Greater Equal operator syntax. Default: "&gt;=".
     * @param operatorGreaterEqual Greater Equal operator.
     */
    public void setOperatorGreaterEqual(String operatorGreaterEqual) {
        this.operatorGreaterEqual = operatorGreaterEqual;
    }
    /**
     * Set Label signal. Default: ":".
     * @param label Label signal.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * Set Comments. Default: "\", "--", "//".
     * @param comments Comments.
     */
    public void setComments(String[] comments) {
        this.comments = comments;
    }
    /**
     * All calculator operators in an string.
     * @return All calculator operators.
     */
    public String getAllCalculatorOperators() {
        return "" + getOperatorAddition() + getOperatorSubtraction() + getOperatorMultiplication() + getOperatorDivision() + getOperatorModules();
    }
    
    /**
     * All operators in an string.
     * @return All operators.
     */
    public String getAllOperators() {
        return getAllCalculatorOperators() + getOperatorAnd() + getOperatorOr() + getOperatorEqual() + getOperatorNotEqual() + getOperatorGreaterEqual() + getOperatorLessEqual() + getOperatorGreater() + getOperatorLess();
    }
}
