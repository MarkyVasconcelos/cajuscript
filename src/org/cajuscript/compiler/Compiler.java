/*
 * Compiler.java
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

package org.cajuscript.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cajuscript.CajuScript;
import org.cajuscript.CajuScriptException;
import org.cajuscript.Context;
import org.cajuscript.Syntax;
import org.cajuscript.Value;
import org.cajuscript.parser.Base;
import org.cajuscript.parser.Break;
import org.cajuscript.parser.Command;
import org.cajuscript.parser.Continue;
import org.cajuscript.parser.Element;
import org.cajuscript.parser.Function;
import org.cajuscript.parser.If;
import org.cajuscript.parser.IfGroup;
import org.cajuscript.parser.Import;
import org.cajuscript.parser.LineDetail;
import org.cajuscript.parser.Loop;
import org.cajuscript.parser.Operation;
import org.cajuscript.parser.Return;
import org.cajuscript.parser.TryCatch;
import org.cajuscript.parser.Variable;

/**
 * CajuScript Compiler
 * @author eduveks
 */
public class Compiler {
    private File baseDir = null;
    private static Map<String, Class> classes = new HashMap<String, Class>();
    private StringBuilder source = new StringBuilder();
    private String packagePath = null;
    private String className = null;
    private File packageDir = null;
    private File javaFile = null;
    private File scriptFile = null;
    private File classFile = null;
    private CajuScript caju = null;
    private long varCount = 1;
    private LineDetail lastLiteDetail = null;

    /**
     * Compiler an script.
     * @param cajuScript CajuScript instance
     * @param path Class path to be created new class from the script.
     */
    public Compiler(CajuScript cajuScript, String path) {
        this.caju = cajuScript;
        baseDir = new File(caju.getCompileBaseDirectory());
        if (path.lastIndexOf(".") > -1) {
            packagePath = path.substring(0, path.lastIndexOf("."));
            className = path.substring(path.lastIndexOf(".") + 1);
            packageDir = new File(baseDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(packagePath.replace('.', File.separatorChar)));
        } else {
            packagePath = "";
            className = path;
            packageDir = baseDir;
        }
        javaFile = new File(packageDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(className).concat(".java"));
        scriptFile = new File(packageDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(className).concat(".cj"));
        classFile = new File(packageDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(className).concat(".class"));
    }

    /**
     * Execute an script compiled.
     * @param context Context
     * @param syntax Syntax
     * @return Value returned by the script
     * @throws org.cajuscript.CajuScriptException Script executing exceptions
     */
    public Value execute(Context context, Syntax syntax) throws CajuScriptException {
        try {
            String path = packagePath.equals("") ? className : packagePath.concat(".").concat(className);
            if (classes.get(path) == null) {
                loadClass(context);
            }
            return ((org.cajuscript.compiler.Executable)classes.get(path).newInstance()).execute(caju, context, syntax);
        } catch (Exception e) {
            throw CajuScriptException.create(caju, context, e.getMessage(), e);
        }
    }

    private void loadClass(Context context) throws CajuScriptException {
        try {
            String path = packagePath.equals("") ? className : packagePath.concat(".").concat(className);
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { baseDir.toURI().toURL() }, CajuScript.class.getClassLoader());
            Executable parserExecute = (Executable)urlClassLoader.loadClass(path).newInstance();
            classes.put(path, parserExecute.getClass());
        } catch (Exception e) {
            throw CajuScriptException.create(caju, context, e.getMessage(), e);
        }
    }

    /**
     * The class compiled is the latest version
     * @param script Script
     * @return Is latest version
     * @throws org.cajuscript.CajuScriptException Looking if is latest version exceptions.
     */
    public boolean isLatest(String script) throws CajuScriptException {
        if (!scriptFile.exists() || !classFile.exists()) {
            return false;
        }
        String scriptClass = "";
        java.io.InputStream is = null;
        try {
            is = new java.io.FileInputStream(scriptFile);
            byte[] b = new byte[is.available()];
            is.read(b);
            scriptClass = new String(b);
        } catch (Exception e) {
            throw new CajuScriptException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) { }
            }
        }
        return scriptClass.equals(script);
    }

    /**
     * Compile an script.
     * @param staticContext Static context
     * @param script Script to be compiled
     * @param base Parser base
     * @throws org.cajuscript.CajuScriptException Compiling exceptions
     */
    public void compile(Context staticContext, String script, Element base) throws CajuScriptException {
        List<String> valueKeys = new ArrayList<String>();
        for (String key : caju.getContext().getFuncs().keySet()) {
            compileElement(valueKeys, caju.getContext().getFuncs().get(key));
        }
        String __return = compileElement(valueKeys, base);
        packageDir.mkdirs();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(javaFile));
            if (!packagePath.equals("")) {
                out.print("package ");
                out.print(packagePath);
                out.print(";");
            }
            out.print("import org.cajuscript.CajuScript;");
            out.print("import org.cajuscript.CajuScriptException;");
            out.print("import org.cajuscript.Context;");
            out.print("import org.cajuscript.Syntax;");
            out.print("import org.cajuscript.Value;");
            out.print("import org.cajuscript.compiler.DefaultExecutable;");
            out.print("import org.cajuscript.parser.Function;");
            out.print("import org.cajuscript.parser.Operation;");
            out.print("public class ");
            out.print(className);
            out.print(" implements org.cajuscript.compiler.Executable{");
            out.print("public ");
            out.print(className);
            out.print("(){}");
            out.print("public Value execute(CajuScript caju,Context context,Syntax syntax)throws CajuScriptException{");
            out.println();
            for (String key : staticContext.getAllKeys(true)) {
                out.print("caju.set(");
                out.print(toString(key));
                out.print(",");
                out.print(toString(staticContext.getVar(key).toString()));
                out.print(");");
                out.println();
            }
            for (String key : valueKeys) {
                out.print("Value ");
                out.print(key);
                out.print("=new Value(caju,context,syntax);");
                out.println();
            }
            out.print(source.toString());
            out.println();
            if (!__return.equals("__return")) {
                out.println("return null;");
            }
            out.print("}");
            out.print("}");
        } catch (IOException ex) {
            throw new CajuScriptException(ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        Writer fwScript = null;
        try {
            fwScript = new FileWriter(scriptFile);
            fwScript.append(script);
        } catch (IOException ex) {
            throw new CajuScriptException(ex);
        } finally {
            if (fwScript != null) {
                try {
                    fwScript.close();
                } catch (IOException ex) {
                    throw new CajuScriptException(ex);
                }
            }
        }
        Writer swJavaC = new StringWriter();
        com.sun.tools.javac.Main.compile(!caju.getCompileClassPath().equals("") ? new String[] {
            "-classpath", caju.getCompileClassPath(), javaFile.getAbsolutePath() }
            : new String[] { javaFile.getAbsolutePath() }
        , new PrintWriter(swJavaC));
        String outputJavaC = swJavaC.toString();
        if (!outputJavaC.trim().equals("")) {
            throw new CajuScriptException(outputJavaC);
        }
        loadClass(staticContext);
    }

    private String compileElement(List<String> valueKeys, Element element) {
        String key = "";
        if (element == null) {
            return null;
        }
        boolean addKey = true;
        boolean isReturn = false;
        boolean isBreak = false;
        boolean isContinue = false;
        if (element instanceof Command) {
            compileElements(valueKeys, element);
            Command command = (Command)element;
            key = "c".concat(Long.toString(varCount++));
            source.append(lineDetail(element.getLineDetail()));
            source.append(key);
            source.append(".setScript(");
            source.append(toString(command.getCommand()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof Variable) {
            compileElements(valueKeys, element);
            Variable variable = (Variable)element;
            String keyValue = compileElement(valueKeys, variable.getValue());
            if (variable.getKey().equals("")) {
                addKey = false;
                key = keyValue;
            } else {
                source.append(lineDetail(element.getLineDetail()));
                if (variable.isKeyRootContext(caju.getSyntax())) {
                    source.append("caju.setVar(");
                    source.append(toString(variable.getKeyRootContext(caju.getSyntax())));
                } else {
                    source.append("context.setVar(");
                    source.append(toString(variable.getKey()));
                }
                source.append(",");
                if (keyValue == null || keyValue.equals("")) {
                    source.append("null");
                } else {
                    source.append(keyValue);
                }
                source.append(");");
                source.append("\n");
            }
        } else if (element instanceof Operation) {
            compileElements(valueKeys, element);
            Operation operation = (Operation)element;
            key = "o".concat(Long.toString(varCount++));
            String firstCommand = compileElement(valueKeys, operation.getFirstCommand());
            String secondCommand = compileElement(valueKeys, operation.getSecondCommand());
            source.append(lineDetail(element.getLineDetail()));
            source.append("Operation.compare(");
            source.append(key);
            source.append(",");
            source.append(firstCommand);
            source.append(",Operation.Operator.");
            source.append(operation.getOperator().name());
            source.append(",");
            source.append(secondCommand);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Return) {
            compileElements(valueKeys, element);
            Return _return = (Return)element;
            String valueKey = compileElement(valueKeys, _return.getValue());
            source.append(lineDetail(element.getLineDetail()));
            source.append("return ");
            source.append(valueKey);
            source.append(";");
            source.append("\n");
            isReturn = true;
        } else if (element instanceof Break) {
            compileElements(valueKeys, element);
            Break _break = (Break)element;
            source.append(lineDetail(element.getLineDetail()));
            source.append("break ");
            source.append(_break.getLabel());
            source.append(";");
            source.append("\n");
            isBreak = true;
        } else if (element instanceof Continue) {
            compileElements(valueKeys, element);
            Continue _continue = (Continue)element;
            source.append(lineDetail(element.getLineDetail()));
            source.append("continue ");
            source.append(_continue.getLabel());
            source.append(";");
            source.append("\n");
            isContinue = true;
        } else if (element instanceof Import) {
            compileElements(valueKeys, element);
            Import _import = (Import)element;
            source.append(lineDetail(element.getLineDetail()));
            if (_import.getPath().startsWith(CajuScript.CAJU_VARS)) {
                source.append("caju.evalFile(context.getVar(");
                source.append(toString(_import.getPath()));
                source.append(").toString());");
            } else {
                source.append("context.addImport(");
                source.append(toString(_import.getPath()));
                source.append(");");
            }
            source.append("\n");
        } else if (element instanceof IfGroup) {
            source.append(lineDetail(element.getLineDetail()));
            String keyCode = Long.toString(varCount++);
            source.append("for(int ");
            source.append("i".concat(keyCode));
            source.append("=0;");
            source.append("i".concat(keyCode));
            source.append("<1;");
            source.append("i".concat(keyCode));
            source.append("++){");
            source.append("\n");
            compileElements(valueKeys, element);
            source.append("}");
            source.append("\n");
        } else if (element instanceof If) {
            If _if = (If)element;
            String conditionKey = compileElement(valueKeys, _if.getCondition());
            source.append(lineDetail(element.getLineDetail()));
            source.append("if(");
            source.append(conditionKey);
            source.append(".getBooleanValue()){");
            source.append("\n");
            String __key = compileElements(valueKeys, element);
            if (!__key.equals("__return")
                && !__key.equals("__break")
                && !__key.equals("__continue")) {
                source.append("break;");
                source.append("\n");
            }
            source.append("}");
            source.append("\n");
        } else if (element instanceof Loop) {
            Loop loop = (Loop)element;
            source.append(lineDetail(element.getLineDetail()));
            if (!loop.getLabel().equals("")) {
                source.append(loop.getLabel());
                source.append(" : ");
            }
            source.append("while(true){");
            source.append("\n");
            String conditionKey = compileElement(valueKeys, loop.getCondition());
            source.append("if(!");
            source.append(conditionKey);
            source.append(".getBooleanValue()){");
            source.append("\n");
            source.append("break;");
            source.append("\n");
            source.append("}");
            source.append("\n");
            compileElements(valueKeys, element);
            source.append("\n");
            source.append("}");
            source.append("\n");
        } else if (element instanceof Function) {
            addKey = false;
            Function function = (Function)element;
            key = "f".concat(Long.toString(varCount++));
            source.append(lineDetail(element.getLineDetail()));
            source.append("context.setFunc(");
            source.append(toString(function.getName()));
            source.append(",new Function(new DefaultExecutable(){");
            source.append("\n");
            source.append("@Override");
            source.append("\n");
            source.append("public Value execute(CajuScript caju,Context context,Syntax syntax)throws CajuScriptException{");
            source.append("\n");
            source.append("<<[".concat(key).concat("]>>"));
            List<String> funcValueKeys = new ArrayList<String>();
            String __return = compileElements(funcValueKeys, function);
            String _source = source.toString();
            source.delete(0, source.length());
            for (String valueKey : funcValueKeys) {
                source.append("Value ");
                source.append(valueKey);
                source.append("=new Value(caju,context,syntax);");
                source.append("\n");
            }
            String funcKeys = source.toString();
            source.delete(0, source.length());
            source.append(_source.replace("<<[".concat(key).concat("]>>"), funcKeys));
            if (!__return.equals("__return")) {
                source.append("return null;");
                source.append("\n");
            }
            source.append("}");
            source.append("\n");
            source.append("},new String[]{");
            for (int i = 0; i < function.getParameters().length; i++) {
                if (i > 0) {
                    source.append(",");
                }
                source.append(toString(function.getParameters()[i]));
            }
            source.append("}");
            source.append("));");
            source.append("\n");
        } else if (element instanceof TryCatch) {
            TryCatch tryCatch = (TryCatch)element;
            key = "t".concat(Long.toString(varCount++));
            compileElement(valueKeys, tryCatch.getError());
            source.append(lineDetail(element.getLineDetail()));
            source.append("try{");
            source.append("\n");
            compileElement(valueKeys, tryCatch.getTry());
            source.append("}catch(Throwable ");
            source.append(key);
            source.append("t){");
            source.append("\n");
            source.append(key);
            source.append(".setValue(");
            source.append(key);
            source.append("t);");
            source.append("\n");
            source.append("context.setVar(");
            source.append(toString(tryCatch.getError().getKey()));
            source.append(", ");
            source.append(key);
            source.append(");");
            source.append("\n");
            compileElement(valueKeys, tryCatch.getCatch());
            source.append("}finally{");
            source.append("\n");
            compileElement(valueKeys, tryCatch.getFinally());
            source.append("}");
            source.append("\n");
        } else if (element instanceof Base) {
            compileElements(valueKeys, element);
        }
        if (!key.equals("") && addKey) {
            valueKeys.add(key);
        }
        if (isReturn) {
            return "__return";
        }
        if (isBreak) {
            return "__break";
        }
        if (isContinue) {
            return "__continue";
        }
        return key;
    }

    private String compileElements(List<String> valueKeys, Element elements) {
        String key = "";
        for (Element element : elements.getElements()) {
            key = compileElement(valueKeys, element);
            if (key.equals("__return")
                || key.equals("__break")
                || key.equals("__continue")) {
                break;
            }
        }
        return key;
    }

    private String lineDetail(LineDetail lineDetail) {
        if (lastLiteDetail != null
                && lastLiteDetail.getNumber() == lineDetail.getNumber()
                && lastLiteDetail.getContent().equals(lineDetail.getContent())) {
            return "";
        }
        lastLiteDetail = lineDetail;
        String cmd = "caju.getRunningLine().set(";
        cmd = cmd.concat(Integer.toString(lineDetail.getNumber()));
        cmd = cmd.concat(",");
        cmd = cmd.concat(toString(lineDetail.getContent()));
        cmd = cmd.concat(");");
        cmd = cmd.concat("\n");
        return cmd;
    }

    private String toString(String str) {
        str = str.replace("\\", "\\\\");
        str = str.replace("\"", "\\\"");
        str = str.replace("\r", "\\r");
        str = str.replace("\n", "\\n");
        str = str.replace("\t", "\\t");
        return "\"".concat(str).concat("\"");
    }
}
