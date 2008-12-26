package org.cajuscript.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
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
import org.cajuscript.parser.If;
import org.cajuscript.parser.IfGroup;
import org.cajuscript.parser.Import;
import org.cajuscript.parser.LineDetail;
import org.cajuscript.parser.Loop;
import org.cajuscript.parser.Operation;
import org.cajuscript.parser.Return;
import org.cajuscript.parser.TryCatch;
import org.cajuscript.parser.Variable;

public class Compiler {
    private static String classPath = "";
    private static File baseDir = new File("cajuscript-classes");
    private StringBuilder source = new StringBuilder();
    private String packagePath = null;
    private String className = null;
    private File packageDir = null;
    private File javaFile = null;
    private File scriptFile = null;

    public Compiler(String path) {
        packagePath = path.substring(0, path.lastIndexOf("."));
        className = path.substring(path.lastIndexOf(".") + 1);
        packageDir = new File(baseDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(packagePath.replace('.', File.separatorChar)));
        javaFile = new File(packageDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(className).concat(".java"));
        scriptFile = new File(packageDir.getAbsolutePath().concat(Character.toString(File.separatorChar)).concat(className).concat(".cj"));
    }

    public static File getBaseDir() {
        return baseDir;
    }

    public static void setBaseDir(File baseDir) {
        Compiler.baseDir = baseDir;
    }

    public static String getClassPath() {
        return classPath;
    }

    public static void setClassPath(String classPath) {
        Compiler.classPath = classPath;
    }

    public Value execute(CajuScript caju, Context context, Syntax syntax) throws CajuScriptException {
        try {
            URLClassLoader sysLoader = new URLClassLoader(new URL[] { baseDir.toURI().toURL() });
            org.cajuscript.compiler.Executable parserExecute = (org.cajuscript.compiler.Executable)sysLoader.loadClass(packagePath.concat(".").concat(className)).newInstance();
            return parserExecute.execute(caju, context, syntax);
        } catch (Exception e) {
            throw CajuScriptException.create(caju, context, e.getMessage(), e);
        }
    }

    public boolean isLastest(String script) throws CajuScriptException {
        if (!scriptFile.exists()) {
            return false;
        }
        BufferedReader in = null;
        StringBuilder scriptFromFile = new StringBuilder();
        try {
            in = new BufferedReader(new FileReader(scriptFile));
            String line = "";
            while ((line = in.readLine()) != null) {
                scriptFromFile.append(line);
            }
        } catch (IOException ex) {
            throw new CajuScriptException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    throw new CajuScriptException(ex);
                }
            }
        }
        return script.equals(scriptFromFile.toString());
    }

    public void compile(CajuScript caju, Context staticContext, String script, Element base) throws CajuScriptException {
        String baseParserKey = compileElement(base);
        packageDir.mkdirs();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(javaFile));
            out.print("package ");
            out.print(packagePath);
            out.print(";");
            out.println();
            out.print("public class ");
            out.print(className);
            out.print(" implements org.cajuscript.compiler.Executable {");
            out.println();
            out.print("private org.cajuscript.parser.Element parser = null;");
            out.println();
            out.print("public ");
            out.print(className);
            out.print("() { }");
            out.println();
            out.print("public org.cajuscript.parser.Element load() throws org.cajuscript.CajuScriptException { ");
            out.println();
            out.print(source.toString());
            out.print("this.parser = ");
            out.print(baseParserKey);
            out.print(";");
            out.println();
            out.print("return ");
            out.print(baseParserKey);
            out.print(";");
            out.println();
            out.print("}");
            out.println();
            out.print("public org.cajuscript.Value execute(org.cajuscript.CajuScript caju, org.cajuscript.Context context, org.cajuscript.Syntax syntax) throws org.cajuscript.CajuScriptException { ");
            out.println();
            Set<String> keys = staticContext.getAllKeys(true);
            for (String key : keys) {
                out.print("caju.set(");
                out.print(toString(key));
                out.print(",");
                out.print(toString(staticContext.getVar(key).toString()));
                out.print(");");
                out.println();
            }
            out.print("if (this.parser == null) {");
            out.println();
            out.print("this.load();");
            out.println();
            out.print("}");
            out.println();
            out.print("return this.parser.execute(caju, context, syntax);");
            out.println();
            out.print("}");
            out.println();
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
        com.sun.tools.javac.Main.compile(!classPath.equals("") ? new String[] {
            "-classpath", classPath, javaFile.getAbsolutePath() }
            : new String[] { javaFile.getAbsolutePath() }
        , new PrintWriter(swJavaC));
        String outputJavaC = swJavaC.toString();
        if (!outputJavaC.trim().equals("")) {
            throw new CajuScriptException(outputJavaC);
        }
    }

    private String compileElement(Element element) {
        String key = "";
        String lineDetail = getLineDetail(element.getLineDetail());
        if (element instanceof Command) {
            Command command = (Command)element;
            key = "command".concat(Integer.toString(command.hashCode()));
            source.append("org.cajuscript.parser.Command ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Command(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setCommand(");
            source.append(toString(command.getCommand()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof Variable) {
            Variable variable = (Variable)element;
            key = "variable".concat(Integer.toString(variable.hashCode()));
            String valueKey = compileElement(variable.getValue());
            source.append("org.cajuscript.parser.Variable ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Variable(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setKey(");
            source.append(toString(variable.getKey()));
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setValue(");
            source.append(valueKey);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Operation) {
            Operation operation = (Operation)element;
            key = "operation".concat(Integer.toString(operation.hashCode()));
            String firstCommandKey = compileElement(operation.getFirstCommand());
            String secondCommandKey = compileElement(operation.getSecondCommand());
            source.append("org.cajuscript.parser.Operation ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Operation(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setCommands(");
            source.append(firstCommandKey);
            source.append(",");
            source.append("org.cajuscript.parser.Operation.Operator.");
            source.append(operation.getOperator().name());
            source.append(",");
            source.append(secondCommandKey);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Return) {
            Return _return = (Return)element;
            key = "return".concat(Integer.toString(_return.hashCode()));
            String valueKey = compileElement(_return.getValue());
            source.append("org.cajuscript.parser.Return ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Return(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setValue(");
            source.append(valueKey);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Break) {
            Break _break = (Break)element;
            key = "break".concat(Integer.toString(_break.hashCode()));
            source.append("org.cajuscript.parser.Break ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Break(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setLabel(");
            source.append(toString(_break.getLabel()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof Continue) {
            Continue _continue = (Continue)element;
            key = "break".concat(Integer.toString(_continue.hashCode()));
            source.append("org.cajuscript.parser.Continue ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Continue(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setLabel(");
            source.append(toString(_continue.getLabel()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof Import) {
            Import _import = (Import)element;
            key = "import".concat(Integer.toString(_import.hashCode()));
            source.append("org.cajuscript.parser.Import ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Import(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setPath(");
            source.append(toString(_import.getPath()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof IfGroup) {
            IfGroup ifGroup = (IfGroup)element;
            key = "ifGroup".concat(Integer.toString(ifGroup.hashCode()));
            source.append("org.cajuscript.parser.IfGroup ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.IfGroup(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
        } else if (element instanceof If) {
            If _if = (If)element;
            key = "if".concat(Integer.toString(_if.hashCode()));
            String conditionKey = compileElement(_if.getCondition());
            source.append("org.cajuscript.parser.If ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.If(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setCondition(");
            source.append(conditionKey);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Loop) {
            Loop loop = (Loop)element;
            key = "loop".concat(Integer.toString(loop.hashCode()));
            String conditionKey = compileElement(loop.getCondition());
            source.append("org.cajuscript.parser.Loop ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Loop(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setCondition(");
            source.append(conditionKey);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setLabel(");
            source.append(toString(loop.getLabel()));
            source.append(");");
            source.append("\n");
        } else if (element instanceof TryCatch) {
            TryCatch tryCatch = (TryCatch)element;
            key = "tryCatch".concat(Integer.toString(tryCatch.hashCode()));
            String errorKey = compileElement(tryCatch.getError());
            String tryKey = compileElement(tryCatch.getTry());
            String catchKey = compileElement(tryCatch.getCatch());
            String finallyKey = compileElement(tryCatch.getFinally());
            source.append("org.cajuscript.parser.TryCatch ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.TryCatch(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setError(");
            source.append(errorKey);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setTry(");
            source.append(tryKey);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setCatch(");
            source.append(catchKey);
            source.append(");");
            source.append("\n");
            source.append(key);
            source.append(".setFinally(");
            source.append(finallyKey);
            source.append(");");
            source.append("\n");
        } else if (element instanceof Base) {
            Base command = (Base)element;
            key = "base".concat(Integer.toString(command.hashCode()));
            source.append("org.cajuscript.parser.Base ");
            source.append(key);
            source.append(" = new org.cajuscript.parser.Base(");
            source.append(lineDetail);
            source.append(");");
            source.append("\n");
        }
        compileElements(key, element);
        return key;
    }

    private void compileElements(String key, Element elements) {
        for (Element element : elements.getElements()) {
            String elementKey = compileElement(element);
            source.append(key);
            source.append(".addElement(");
            source.append(elementKey);
            source.append(");");
            source.append("\n");
        }
    }

    private String getLineDetail(LineDetail lineDetail) {
        String cmd = "new org.cajuscript.parser.LineDetail(";
        cmd = cmd.concat(Integer.toString(lineDetail.getNumber()));
        cmd = cmd.concat(",");
        cmd = cmd.concat(toString(lineDetail.getContent()));
        cmd = cmd.concat(")");
        return cmd;
    }

    private String toString(String str) {
        str = str.replace("\"", "\\\"");
        str = str.replace("\r", "\\r");
        str = str.replace("\n", "\\n");
        str = str.replace("\t", "\\t");
        return "\"".concat(str).concat("\"");
    }
}
