package org.cajuscript.compiler;

import org.cajuscript.CajuScript;
import org.cajuscript.CajuScriptException;
import org.cajuscript.Context;
import org.cajuscript.Syntax;
import org.cajuscript.Value;
import org.cajuscript.parser.Element;

public interface Executable {
    public Element load() throws CajuScriptException;
    public Value execute(CajuScript caju, Context context, Syntax syntax) throws CajuScriptException;
}
