/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cajuscript;

import java.util.List;
import java.util.Set;
import org.cajuscript.parser.Base;
import org.cajuscript.parser.Function;
import org.cajuscript.parser.LineDetail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

/**
 *
 * @author msp0047
 */
public class CajuScriptTest {
  
    public CajuScriptTest() throws CajuScriptException {
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addGlobalSyntax and getGlobalSyntax, of class CajuScript.
     */
    @Test
    public void globalSyntax() {
        System.out.println("globalSyntax");
        Syntax syntax = new Syntax();
        String name = "CajuTest";
        CajuScript.addGlobalSyntax(name, syntax);
        Syntax syntaxX = CajuScript.getGlobalSyntax(name);
        assertEquals(syntax, syntaxX);
    }

    private void syntaxReload(CajuScript caju) throws CajuScriptException {
        String scriptReload = "";
        scriptReload += "x = 0;";
        scriptReload += "countIf = 0;";
        scriptReload += "countElseIf1 = 0;";
        scriptReload += "countElseIf2 = 0;";
        scriptReload += "countElse = 0;";
        scriptReload += "countLoop = 0;";
        scriptReload += "countBreak = 0;";
        scriptReload += "countContinue = 0;";
        scriptReload += "countTry = 0;";
        scriptReload += "countCatch = 0;";
        scriptReload += "countFinally = 0;";
        scriptReload += "countFunc = 0;";
        scriptReload += "xIf = 0;";
        scriptReload += "xElseIf1 = 0;";
        scriptReload += "xElseIf2 = 0;";
        scriptReload += "xElse = 0;";
        scriptReload += "xLoop = 0;";
        scriptReload += "xBreak = 0;";
        scriptReload += "xContinue = 0;";
        scriptReload += "xTry = 0;";
        scriptReload += "xCatch = 0;";
        scriptReload += "xFinally = 0;";
        scriptReload += "xFunc = 0;";
        caju.eval(scriptReload);
    }
    
    @Test
    public void syntaxIf() throws CajuScriptException {
        System.out.println("syntaxIf");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptIf = "";
        scriptIf += "x = 1 | x = 0 ?";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    x <= 1 | x > 5 @";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    @;";
        scriptIf += "    ex ^";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    ^^";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^~^";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^;";
        scriptIf += "    x % 2 = 0 ?";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ? x % 2 = 0 ?";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ?  x % 2 = 0 ?";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ??";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ?;";
        scriptIf += "?;";
        scriptIf += "x = 1 ?";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "? x % 3 = 0 ?";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    (x < 8 & x > 6) @";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    @;";
        scriptIf += "    ex ^";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    ^^";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^~^";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^;";
        scriptIf += "    (x % 5 = 0) ?";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ? (x % 5 = 1) ? ";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ? (x % 5 = 0) ?";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ??";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ?;";
        scriptIf += "??";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "?;";
        scriptIf += "x = 10 ?";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "? x = 11 ?";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "??";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    (x >= 13 & x <= 13) @";
        scriptIf += "	     countLoop += 1;";
        scriptIf += "	     xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    @;";
        scriptIf += "    ex ^";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    ^^";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^~^";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ^;";
        scriptIf += "    (x % 5 = 0) ?";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ? (x % 5 = 0) ?";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ? (x = 17) ?";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ??";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    ?;";
        scriptIf += "?;";
        caju.eval(scriptIf);
        syntaxCheckIf(caju);
        syntaxCheckIfCache(caju, scriptIf);
    }
    
    @Test
    public void syntaxLoop() throws CajuScriptException {
        System.out.println("syntaxLoop");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptLoop = "";
        scriptLoop += "loop: (x >= 0 & x <= 10) @";
        scriptLoop += "    countLoop += 1;";
        scriptLoop += "    xLoop += x;";
        scriptLoop += "    x += 1;";
        scriptLoop += "    (x = 7) @";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        !!;";
        scriptLoop += "    @;";
        scriptLoop += "    (x = 5) @";
        scriptLoop += "	       countLoop = 10 ?";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "	           ! !;";
        scriptLoop += "	       ?;";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        countContinue += 1;";
        scriptLoop += "        xContinue += x;";
        scriptLoop += "        . .;";
        scriptLoop += "        x += 1;";
        scriptLoop += "    @;";
        scriptLoop += "    ex ^";
        scriptLoop += "        countTry += 1;";
        scriptLoop += "        xTry += x;";
        scriptLoop += "        caju.error();";
        scriptLoop += "    ^^";
        scriptLoop += "        countCatch += 1;";
        scriptLoop += "        xCatch += x;";
        scriptLoop += "    ^~^";
        scriptLoop += "        countFinally += 1;";
        scriptLoop += "        xFinally += x;";
        scriptLoop += "    ^;";
        scriptLoop += "    (x % 3 = 0) ?";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "    ? (x % 5 = 0) ?";
        scriptLoop += "        countElseIf1 += 1;";
        scriptLoop += "        xElseIf1 += x;";
        scriptLoop += "    ? (x % 4 = 0) ?";
        scriptLoop += "        countElseIf2 += 1;";
        scriptLoop += "        xElseIf2 += x;";
        scriptLoop += "    ??";
        scriptLoop += "        countElse += 1;";
        scriptLoop += "        xElse += x;";
        scriptLoop += "    ?;";
        scriptLoop += "    (x = 10) ?";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        (true) @";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "            !! loop;";
        scriptLoop += "        @;";
        scriptLoop += "    ??";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        (true) @";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countContinue += 1;";
        scriptLoop += "            xContinue += x;";
        scriptLoop += "            .. loop;";
        scriptLoop += "        @;";
        scriptLoop += "    ?;";
        scriptLoop += "@;";
        caju.eval(scriptLoop);
        syntaxCheckLoop(caju);
        syntaxCheckLoopCache(caju, scriptLoop);
    }
    
    @Test
    public void syntaxFunction() throws CajuScriptException {
        System.out.println("syntaxFunction");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptFunc = "";
        scriptFunc += "func x #";
        scriptFunc += "    x += 1;";
        scriptFunc += "    .x += 1;";
        scriptFunc += "    .countFunc += 1;";
        scriptFunc += "    (x >= 1 & x <= 1) @";
        scriptFunc += "	       .countLoop += 1;";
        scriptFunc += "	       .xLoop += x;";
        scriptFunc += "        x += 1;";
        scriptFunc += "    @;";
        scriptFunc += "    ex ^";
        scriptFunc += "        .countTry += 1;";
        scriptFunc += "        .xTry += x;";
        scriptFunc += "        caju.error();";
        scriptFunc += "    ^^";
        scriptFunc += "        .countCatch += 1;";
        scriptFunc += "        .xCatch += x;";
        scriptFunc += "    ^~^";
        scriptFunc += "        .countFinally += 1;";
        scriptFunc += "        .xFinally += x;";
        scriptFunc += "    ^;";
        scriptFunc += "    x = 2 ?";
        scriptFunc += "        .x += 1;";
        scriptFunc += "        .countIf += 1;";
        scriptFunc += "        .xIf += x;";
        scriptFunc += "    ? (x % 4 = 0) ?";
        scriptFunc += "        .x += 1;";
        scriptFunc += "        .countElseIf1 += 1;";
        scriptFunc += "        .xElseIf1 += x;";
        scriptFunc += "    ? (x % 2 = 0) ?";
        scriptFunc += "        .x += 1;";
        scriptFunc += "        .countElseIf2 += 1;";
        scriptFunc += "        .xElseIf2 += x;";
        scriptFunc += "    ??";
        scriptFunc += "        .x += 1;";
        scriptFunc += "        .countElse += 1;";
        scriptFunc += "        .xElse += x;";
        scriptFunc += "    ?;";
        scriptFunc += "    x < 10 ?";
        scriptFunc += "        ~ func(x);";
        scriptFunc += "    ?;";
        scriptFunc += "    .x += 1;";
        scriptFunc += "    ~ x;";
        scriptFunc += "#";
        scriptFunc += "xFunc += func(x);";
        scriptFunc += "func1 x, y #";
        scriptFunc += "    .x += 1;";
        scriptFunc += "    .countFunc += 1;";
        scriptFunc += "    ~ x + y;";
        scriptFunc += "#;";
        scriptFunc += "xFunc += func1(x, 5);";
        scriptFunc += "func2 (x, y) #";
        scriptFunc += "    .x += 1;";
        scriptFunc += "    .countFunc += 1;";
        scriptFunc += "    ~ x + y;";
        scriptFunc += "#";
        scriptFunc += "xFunc += func2(x, 5);";
        scriptFunc += "func3 #";
        scriptFunc += "    .x += 1;";
        scriptFunc += "    .countFunc += 1;";
        scriptFunc += "    ~ x + 1;";
        scriptFunc += "#";
        scriptFunc += "xFunc += func3();";
        caju.eval(scriptFunc);
        syntaxCheckFunction(caju);
        syntaxCheckFunctionCache(caju, scriptFunc);
    }
    
    @Test
    public void syntaxTryCatch() throws CajuScriptException {
        System.out.println("syntaxTryCatch");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptTry = "";
        scriptTry += "e ^";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    (x = 0 | x = 1) @";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    @;";
        scriptTry += "    ex ^";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    ^^";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    ^~^";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    ^;";
        scriptTry += "    x = 0 | x = 5 ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    ? (x % 4 = 0) ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    ? (x % 2 = 0) ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    ??";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    ?;";
        scriptTry += "    caju.error();";
        scriptTry += "^^";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "    (x = 7) @";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    @;";
        scriptTry += "    ex ^";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    ^^";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    ^~^";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    ^;";
        scriptTry += "    x = 0 ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    ? (x = 11) ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    ? (x = 0) ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    ??";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    ?;";
        scriptTry += "^~^";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "    (x = 13) @";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    @;";
        scriptTry += "    ex ^";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    ^^";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    ^~^";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    ^;";
        scriptTry += "    x = 0 ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    ? x = 0 ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    ? x = 0 ?";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    ??";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    ?;";
        scriptTry += "^;";
        scriptTry += "e ^";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry = x;";
        scriptTry += "^;";
        scriptTry += "e ^";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += "^^";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "^;";
        scriptTry += "e ^";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += "^~^";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "^;";
        caju.eval(scriptTry);
        syntaxCheckTryCatch(caju);
        syntaxCheckTryCatchCache(caju, scriptTry);
    }
    
    @Test
    public void syntaxJavaIf() throws CajuScriptException {
        System.out.println("syntaxJavaIf");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptIf = "caju.syntax: CajuJava;";
        scriptIf += "if x = 1 | x = 0 {";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while x <= 1 | x > 5 {";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    try ex {";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    } catch {";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } finally {";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    if x % 2 = 0 {";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if x % 2 = 0 {";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if x % 2 = 0 {";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else {";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "}";
        scriptIf += "if x = 1 {";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "} else if x % 3 = 0 {";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while (x < 8 & x > 6) {";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    try ex {";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    } catch {";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } finally {";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    if (x % 5 = 0) {";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if (x % 5 = 1) {";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if (x % 5 = 0) {";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else {";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "} else {";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "}";
        scriptIf += "if x = 10 {";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "} else if x = 11 {";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "} else {";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while (x >= 13 & x <= 13) {";
        scriptIf += "	     countLoop += 1;";
        scriptIf += "	     xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    try ex {";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    } catch {";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } finally {";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "    if (x % 5 = 0) {";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if (x % 5 = 0) {";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else if (x = 17) {";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    } else {";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    }";
        scriptIf += "}";
        caju.eval(scriptIf);
        syntaxCheckIf(caju);
        syntaxCheckIfCache(caju, scriptIf);
    }
    
    @Test
    public void syntaxJavaLoop() throws CajuScriptException {
        System.out.println("syntaxJavaLoop");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptLoop = "caju.syntax: CajuJava;";
        scriptLoop += "loop: while (x >= 0 & x <= 10) {";
        scriptLoop += "    countLoop += 1;";
        scriptLoop += "    xLoop += x;";
        scriptLoop += "    x += 1;";
        scriptLoop += "    while (x = 7) {";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        break;";
        scriptLoop += "    }";
        scriptLoop += "    while (x = 5) {";
        scriptLoop += "	       if countLoop = 10 {";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "	           break;";
        scriptLoop += "	       }";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        countContinue += 1;";
        scriptLoop += "        xContinue += x;";
        scriptLoop += "        continue;";
        scriptLoop += "        x += 1;";
        scriptLoop += "    }";
        scriptLoop += "    try ex {";
        scriptLoop += "        countTry += 1;";
        scriptLoop += "        xTry += x;";
        scriptLoop += "        caju.error();";
        scriptLoop += "    } catch {";
        scriptLoop += "        countCatch += 1;";
        scriptLoop += "        xCatch += x;";
        scriptLoop += "    } finally {";
        scriptLoop += "        countFinally += 1;";
        scriptLoop += "        xFinally += x;";
        scriptLoop += "    }";
        scriptLoop += "    if (x % 3 = 0) {";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "    } else if (x % 5 = 0) {";
        scriptLoop += "        countElseIf1 += 1;";
        scriptLoop += "        xElseIf1 += x;";
        scriptLoop += "    } else if (x % 4 = 0) {";
        scriptLoop += "        countElseIf2 += 1;";
        scriptLoop += "        xElseIf2 += x;";
        scriptLoop += "    } else {";
        scriptLoop += "        countElse += 1;";
        scriptLoop += "        xElse += x;";
        scriptLoop += "    }";
        scriptLoop += "    if (x = 10) {";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        while (true) {";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "            break loop;";
        scriptLoop += "        }";
        scriptLoop += "    } else {";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        while (true) {";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countContinue += 1;";
        scriptLoop += "            xContinue += x;";
        scriptLoop += "            continue loop;";
        scriptLoop += "        }";
        scriptLoop += "    }";
        scriptLoop += "}";
        caju.eval(scriptLoop);
        syntaxCheckLoop(caju);
        syntaxCheckLoopCache(caju, scriptLoop);
    }
    
    @Test
    public void syntaxJavaFunction() throws CajuScriptException {
        System.out.println("syntaxJavaFunction");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptFunc = "caju.syntax: CajuJava;";
        scriptFunc += "function func x {";
        scriptFunc += "    x += 1;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    while (x >= 1 & x <= 1) {";
        scriptFunc += "	       root.countLoop += 1;";
        scriptFunc += "	       root.xLoop += x;";
        scriptFunc += "        x += 1;";
        scriptFunc += "    }";
        scriptFunc += "    try ex {";
        scriptFunc += "        root.countTry += 1;";
        scriptFunc += "        root.xTry += x;";
        scriptFunc += "        caju.error();";
        scriptFunc += "    } catch {";
        scriptFunc += "        root.countCatch += 1;";
        scriptFunc += "        root.xCatch += x;";
        scriptFunc += "    } finally {";
        scriptFunc += "        root.countFinally += 1;";
        scriptFunc += "        root.xFinally += x;";
        scriptFunc += "    }";
        scriptFunc += "    if x = 2 {";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countIf += 1;";
        scriptFunc += "        root.xIf += x;";
        scriptFunc += "    } else if (x % 4 = 0) {";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElseIf1 += 1;";
        scriptFunc += "        root.xElseIf1 += x;";
        scriptFunc += "    } else if (x % 2 = 0) {";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElseIf2 += 1;";
        scriptFunc += "        root.xElseIf2 += x;";
        scriptFunc += "    } else {";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElse += 1;";
        scriptFunc += "        root.xElse += x;";
        scriptFunc += "    }";
        scriptFunc += "    if x < 10 {";
        scriptFunc += "        return func(x);";
        scriptFunc += "    }";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    return x;";
        scriptFunc += "}";
        scriptFunc += "xFunc += func(x);";
        scriptFunc += "function func1 x, y {";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + y;";
        scriptFunc += "}";
        scriptFunc += "xFunc += func1(x, 5);";
        scriptFunc += "function func2 (x, y) {";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + y;";
        scriptFunc += "}";
        scriptFunc += "xFunc += func2(x, 5);";
        scriptFunc += "function func3 {";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + 1;";
        scriptFunc += "}";
        scriptFunc += "xFunc += func3();";
        caju.eval(scriptFunc);
        syntaxCheckFunction(caju);
        syntaxCheckFunctionCache(caju, scriptFunc);
    }
    
    @Test
    public void syntaxJavaTryCatch() throws CajuScriptException {
        System.out.println("syntaxJavaTryCatch");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptTry = "caju.syntax: CajuJava;";
        scriptTry += "try e {";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    while (x = 0 | x = 1) {";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    }";
        scriptTry += "    try ex {";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    } catch {";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    } finally {";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    }";
        scriptTry += "    if x = 0 | x = 5 {";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    } else if (x % 4 = 0) {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    } else if (x % 2 = 0) {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    } else {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    }";
        scriptTry += "    caju.error();";
        scriptTry += "} catch {";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "    while (x = 7) {";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    }";
        scriptTry += "    try ex {";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    } catch {";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    } finally {";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    }";
        scriptTry += "    if x = 0 {";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    } else if (x = 11) {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    } else if (x = 0) {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    } else {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    }";
        scriptTry += "} finally {";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "    while (x = 13) {";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    }";
        scriptTry += "    try ex {";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    } catch {";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    } finally {";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    }";
        scriptTry += "    if x = 0 {";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    } else if x = 0 {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    } else if x = 0 {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    } else {";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    }";
        scriptTry += "}";
        scriptTry += "try e {";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry = x;";
        scriptTry += "}";
        scriptTry += "try e {";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += "} catch {";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "}";
        scriptTry += "try e {";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += "} finally {";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "}";
        caju.eval(scriptTry);
        syntaxCheckTryCatch(caju);
        syntaxCheckTryCatchCache(caju, scriptTry);
    }
    
    @Test
    public void syntaxBasicIf() throws CajuScriptException {
        System.out.println("syntaxBasicIf");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptIf = "caju.syntax: CajuBasic;";
        scriptIf += "if x = 1 | x = 0 ;";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while x <= 1 | x > 5 ;";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    try ex ;";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    catch;";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    finally;";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    if x % 2 = 0 ;";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif x % 2 = 0 ;";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif x % 2 = 0 ;";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    else ;";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "end;";
        scriptIf += "if x = 1 ;";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "elseif x % 3 = 0 ;";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while (x < 8 & x > 6) ;";
        scriptIf += "	   countLoop += 1;";
        scriptIf += "	   xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    try ex ;";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    catch;";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    finally;";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    if (x % 5 = 0) ;";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif (x % 5 = 1);";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif (x % 5 = 0);";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    else;";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "else;";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "end;";
        scriptIf += "if x = 10;";
        scriptIf += "    countIf += 1;";
        scriptIf += "    xIf += x;";
        scriptIf += "elseif x = 11 ;";
        scriptIf += "    countElseIf1 += 1;";
        scriptIf += "    xElseIf1 += x;";
        scriptIf += "else;";
        scriptIf += "    countElse += 1;";
        scriptIf += "    xElse += x;";
        scriptIf += "    x += 1;";
        scriptIf += "    while (x >= 13 & x <= 13);";
        scriptIf += "	     countLoop += 1;";
        scriptIf += "	     xLoop += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    try ex;";
        scriptIf += "        countTry += 1;";
        scriptIf += "        xTry += x;";
        scriptIf += "        x += 1;";
        scriptIf += "        caju.error();";
        scriptIf += "    catch;";
        scriptIf += "        countCatch += 1;";
        scriptIf += "        xCatch += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    finally;";
        scriptIf += "        countFinally += 1;";
        scriptIf += "        xFinally += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "    if (x % 5 = 0);";
        scriptIf += "        countIf += 1;";
        scriptIf += "        xIf += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif (x % 5 = 0);";
        scriptIf += "        countElseIf1 += 1;";
        scriptIf += "        xElseIf1 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    elseif (x = 17);";
        scriptIf += "        countElseIf2 += 1;";
        scriptIf += "        xElseIf2 += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    else;";
        scriptIf += "        countElse += 1;";
        scriptIf += "        xElse += x;";
        scriptIf += "        x += 1;";
        scriptIf += "    end;";
        scriptIf += "end;";
        caju.eval(scriptIf);
        syntaxCheckIf(caju);
        syntaxCheckIfCache(caju, scriptIf);
    }
    
    @Test
    public void syntaxBasicLoop() throws CajuScriptException {
        System.out.println("syntaxBasicLoop");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptLoop = "caju.syntax: CajuBasic;";
        scriptLoop += "loop: while (x >= 0 & x <= 10) ;";
        scriptLoop += "    countLoop += 1;";
        scriptLoop += "    xLoop += x;";
        scriptLoop += "    x += 1;";
        scriptLoop += "    while (x = 7) ;";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        break;";
        scriptLoop += "    end;";
        scriptLoop += "    while (x = 5) ;";
        scriptLoop += "	       if countLoop = 10 ;";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "	           break;";
        scriptLoop += "	       end;";
        scriptLoop += "	       countLoop += 1;";
        scriptLoop += "	       xLoop += x;";
        scriptLoop += "        countContinue += 1;";
        scriptLoop += "        xContinue += x;";
        scriptLoop += "        continue;";
        scriptLoop += "        x += 1;";
        scriptLoop += "    end;";
        scriptLoop += "    try ex ;";
        scriptLoop += "        countTry += 1;";
        scriptLoop += "        xTry += x;";
        scriptLoop += "        caju.error();";
        scriptLoop += "    catch ;";
        scriptLoop += "        countCatch += 1;";
        scriptLoop += "        xCatch += x;";
        scriptLoop += "    finally ;";
        scriptLoop += "        countFinally += 1;";
        scriptLoop += "        xFinally += x;";
        scriptLoop += "    end;";
        scriptLoop += "    if (x % 3 = 0) ;";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "    elseif (x % 5 = 0) ;";
        scriptLoop += "        countElseIf1 += 1;";
        scriptLoop += "        xElseIf1 += x;";
        scriptLoop += "    elseif (x % 4 = 0) ;";
        scriptLoop += "        countElseIf2 += 1;";
        scriptLoop += "        xElseIf2 += x;";
        scriptLoop += "    else;";
        scriptLoop += "        countElse += 1;";
        scriptLoop += "        xElse += x;";
        scriptLoop += "    end;";
        scriptLoop += "    if (x = 10) ;";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        while (true) ;";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countBreak += 1;";
        scriptLoop += "            xBreak += x;";
        scriptLoop += "            break loop;";
        scriptLoop += "        end;";
        scriptLoop += "    else ;";
        scriptLoop += "        countIf += 1;";
        scriptLoop += "        xIf += x;";
        scriptLoop += "        while (true) ;";
        scriptLoop += "            countLoop += 1;";
        scriptLoop += "            xLoop += x;";
        scriptLoop += "            countContinue += 1;";
        scriptLoop += "            xContinue += x;";
        scriptLoop += "            continue loop;";
        scriptLoop += "        end;";
        scriptLoop += "    end;";
        scriptLoop += "end;";
        caju.eval(scriptLoop);
        syntaxCheckLoop(caju);
        syntaxCheckLoopCache(caju, scriptLoop);
    }
    
    @Test
    public void syntaxBasicFunction() throws CajuScriptException {
        System.out.println("syntaxBasicFunction");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptFunc = "caju.syntax: CajuBasic;";
        scriptFunc += "function func x ;";
        scriptFunc += "    x += 1;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    while (x >= 1 & x <= 1) ;";
        scriptFunc += "	       root.countLoop += 1;";
        scriptFunc += "	       root.xLoop += x;";
        scriptFunc += "        x += 1;";
        scriptFunc += "    end;";
        scriptFunc += "    try ex ;";
        scriptFunc += "        root.countTry += 1;";
        scriptFunc += "        root.xTry += x;";
        scriptFunc += "        caju.error();";
        scriptFunc += "    catch ;";
        scriptFunc += "        root.countCatch += 1;";
        scriptFunc += "        root.xCatch += x;";
        scriptFunc += "    finally ;";
        scriptFunc += "        root.countFinally += 1;";
        scriptFunc += "        root.xFinally += x;";
        scriptFunc += "    end;";
        scriptFunc += "    if x = 2 ;";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countIf += 1;";
        scriptFunc += "        root.xIf += x;";
        scriptFunc += "    elseif (x % 4 = 0) ;";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElseIf1 += 1;";
        scriptFunc += "        root.xElseIf1 += x;";
        scriptFunc += "    elseif (x % 2 = 0) ;";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElseIf2 += 1;";
        scriptFunc += "        root.xElseIf2 += x;";
        scriptFunc += "    else ;";
        scriptFunc += "        root.x += 1;";
        scriptFunc += "        root.countElse += 1;";
        scriptFunc += "        root.xElse += x;";
        scriptFunc += "    end;";
        scriptFunc += "    if x < 10 ;";
        scriptFunc += "        return func(x);";
        scriptFunc += "    end;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    return x;";
        scriptFunc += "end;";
        scriptFunc += "xFunc += func(x);";
        scriptFunc += "function func1 x, y ;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + y;";
        scriptFunc += "end;";
        scriptFunc += "xFunc += func1(x, 5);";
        scriptFunc += "function func2 (x, y) ;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + y;";
        scriptFunc += "end;";
        scriptFunc += "xFunc += func2(x, 5);";
        scriptFunc += "function func3 ;";
        scriptFunc += "    root.x += 1;";
        scriptFunc += "    root.countFunc += 1;";
        scriptFunc += "    return x + 1;";
        scriptFunc += "end;";
        scriptFunc += "xFunc += func3();";
        caju.eval(scriptFunc);
        syntaxCheckFunction(caju);
        syntaxCheckFunctionCache(caju, scriptFunc);
    }
    
    @Test
    public void syntaxBasicTryCatch() throws CajuScriptException {
        System.out.println("syntaxBasicTryCatch");
        CajuScript caju = new CajuScript();
        syntaxReload(caju);
        String scriptTry = "caju.syntax: CajuBasic;";
        scriptTry += "try e ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    while (x = 0 | x = 1) ;";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    end;";
        scriptTry += "    try ex ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    catch ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    finally ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    end;";
        scriptTry += "    if x = 0 | x = 5 ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    elseif (x % 4 = 0) ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    elseif (x % 2 = 0) ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    else ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    end;";
        scriptTry += "    caju.error();";
        scriptTry += "catch ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "    while (x = 7) ;";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    end;";
        scriptTry += "    try ex ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    catch ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    finally ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    end;";
        scriptTry += "    if x = 0 ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    elseif (x = 11);";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    elseif (x = 0) ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    else ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    end;";
        scriptTry += "finally;";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "    while (x = 13) ;";
        scriptTry += "        x += 1;";
        scriptTry += "	      countLoop += 1;";
        scriptTry += "	      xLoop += x;";
        scriptTry += "    end;";
        scriptTry += "    try ex ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countTry += 1;";
        scriptTry += "        xTry += x;";
        scriptTry += "        caju.error();";
        scriptTry += "    catch ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countCatch += 1;";
        scriptTry += "        xCatch += x;";
        scriptTry += "    finally ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countFinally += 1;";
        scriptTry += "        xFinally += x;";
        scriptTry += "    end;";
        scriptTry += "    if x = 0 ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countIf += 1;";
        scriptTry += "        xIf += x;";
        scriptTry += "    elseif x = 0 ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf1 += 1;";
        scriptTry += "        xElseIf1 += x;";
        scriptTry += "    elseif x = 0 ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElseIf2 += 1;";
        scriptTry += "        xElseIf2 += x;";
        scriptTry += "    else ;";
        scriptTry += "        x += 1;";
        scriptTry += "        countElse += 1;";
        scriptTry += "        xElse += x;";
        scriptTry += "    end;";
        scriptTry += "end;";
        scriptTry += "try e ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry = x;";
        scriptTry += "end;";
        scriptTry += "try e ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += "catch ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countCatch += 1;";
        scriptTry += "    xCatch += x;";
        scriptTry += "end;";
        scriptTry += "try e ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countTry += 1;";
        scriptTry += "    xTry += x;";
        scriptTry += "    caju.error();";
        scriptTry += " finally ;";
        scriptTry += "    x += 1;";
        scriptTry += "    countFinally += 1;";
        scriptTry += "    xFinally += x;";
        scriptTry += "end;";
        caju.eval(scriptTry);
        syntaxCheckTryCatch(caju);
        syntaxCheckTryCatchCache(caju, scriptTry);
    }
    
    private void syntaxCheckIfCache(CajuScript caju, String script) throws CajuScriptException {
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckIf(caju);
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckIf(caju);
        caju = new CajuScript();
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckIf(caju);
    }
    
    private void syntaxCheckIf(CajuScript caju) throws CajuScriptException {
        syntaxCheck(caju, 18, 1, 2, 1, 2, 3, 0, 0, 3, 3, 3, 0, 0, 17, 17, 17, 21, 0, 0, 24, 27, 30, 0);
    }
    
    private void syntaxCheckLoopCache(CajuScript caju, String script) throws CajuScriptException {
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckLoop(caju);
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckLoop(caju);
        caju = new CajuScript();
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckLoop(caju);
    }

    private void syntaxCheckLoop(CajuScript caju) throws CajuScriptException {
        syntaxCheck(caju, 10, 13, 2, 2, 3, 22, 2, 10, 10, 10, 10, 0, 73, 15, 12, 10, 112, 15, 50, 55, 55, 55, 0);
    }
    
    private void syntaxCheckFunctionCache(CajuScript caju, String script) throws CajuScriptException {
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckFunction(caju);
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckFunction(caju);
        caju = new CajuScript();
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckFunction(caju);
    }
    
    private void syntaxCheckFunction(CajuScript caju) throws CajuScriptException {
        syntaxCheck(caju, 22, 1, 2, 2, 4, 1, 0, 0, 9, 9, 9, 12, 2, 12, 16, 24, 1, 0, 0, 54, 54, 54, 82);
    }
    
    private void syntaxCheckTryCatchCache(CajuScript caju, String script) throws CajuScriptException {
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckTryCatch(caju);
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckTryCatch(caju);
        caju = new CajuScript();
        syntaxReload(caju);
        caju.eval("caju.cache: test;"+ script);
        syntaxCheckTryCatch(caju);
    }
    
    private void syntaxCheckTryCatch(CajuScript caju) throws CajuScriptException {
        syntaxCheck(caju, 23, 1, 1, 0, 1, 3, 0, 0, 7, 5, 5, 0, 6, 12, 0, 18, 24, 0, 0, 61, 58, 69, 0);
    }
    
    private void syntaxCheck(CajuScript caju, 
    int x, int countIf, int countElseIf1, int countElseIf2, int countElse, 
    int countLoop, int countBreak, int countContinue, int countTry, int countCatch,
    int countFinally, int countFunc, int xIf, int xElseIf1, int xElseIf2, int xElse,
    int xLoop, int xBreak, int xContinue, int xTry, int xCatch, int xFinally, int xFunc
    ) throws CajuScriptException {
        if (((Integer)caju.get("x")).intValue() != x) {
            fail("x is "+ caju.get("x") +". Need be "+ x +"!");
        }
        if (((Integer)caju.get("countIf")).intValue() != countIf) {
            fail("countIf is "+ caju.get("countIf") +". Need be "+ countIf +"!");
        }
        if (((Integer)caju.get("countElseIf1")).intValue() != countElseIf1) {
            fail("countElseIf1 is "+ caju.get("countElseIf1") +". Need be "+ countElseIf1 +"!");
        }
        if (((Integer)caju.get("countElseIf2")).intValue() != countElseIf2) {
            fail("countElseIf2 is "+ caju.get("countElseIf2") +". Need be "+ countElseIf2 +"!");
        }
        if (((Integer)caju.get("countElse")).intValue() != countElse) {
            fail("countElse is "+ caju.get("countElse") +". Need be "+ countElse +"!");
        }
        if (((Integer)caju.get("countLoop")).intValue() != countLoop) {
            fail("countLoop is "+ caju.get("countLoop") +". Need be "+ countLoop +"!");
        }
        if (((Integer)caju.get("countBreak")).intValue() != countBreak) {
            fail("countBreak is "+ caju.get("countBreak") +". Need be "+ countBreak +"!");
        }
        if (((Integer)caju.get("countContinue")).intValue() != countContinue) {
            fail("countContinue is "+ caju.get("countContinue") +". Need be "+ countContinue +"!");
        }
        if (((Integer)caju.get("countFunc")).intValue() != countFunc) {
            fail("countFunc is "+ caju.get("countFunc") +". Need be "+ countFunc +"!");
        }
        if (((Integer)caju.get("countTry")).intValue() != countTry) {
            fail("countTry is "+ caju.get("countTry") +". Need be "+ countTry +"!");
        }
        if (((Integer)caju.get("countCatch")).intValue() != countCatch) {
            fail("countCatch is "+ caju.get("countCatch") +". Need be "+ countCatch +"!");
        }
        if (((Integer)caju.get("countFinally")).intValue() != countFinally) {
            fail("countFinally is "+ caju.get("countFinally") +". Need be "+ countFinally +"!");
        }
        if (((Integer)caju.get("xIf")).intValue() != xIf) {
            fail("xIf is "+ caju.get("xIf") +". Need be "+ xIf +"!");
        }
        if (((Integer)caju.get("xElseIf1")).intValue() != xElseIf1) {
            fail("xElseIf1 is "+ caju.get("xElseIf1") +". Need be "+ xElseIf1 +"!");
        }
        if (((Integer)caju.get("xElseIf2")).intValue() != xElseIf2) {
            fail("xElseIf2 is "+ caju.get("xElseIf2") +". Need be "+ xElseIf2 +"!");
        }
        if (((Integer)caju.get("xElse")).intValue() != xElse) {
            fail("xElse is "+ caju.get("xElse") +". Need be "+ xElse +"!");
        }
        if (((Integer)caju.get("xLoop")).intValue() != xLoop) {
            fail("xLoop is "+ caju.get("xLoop") +". Need be "+ xLoop +"!");
        }
        if (((Integer)caju.get("xBreak")).intValue() != xBreak) {
            fail("xBreak is "+ caju.get("xBreak") +". Need be xBreak!");
        }
        if (((Integer)caju.get("xContinue")).intValue() != xContinue) {
            fail("xContinue is "+ caju.get("xContinue") +". Need be "+ xContinue +"!");
        }
        if (((Integer)caju.get("xFunc")).intValue() != xFunc) {
            fail("xFunc is "+ caju.get("xFunc") +". Need be "+ xFunc +"!");
        }
        if (((Integer)caju.get("xTry")).intValue() != xTry) {
            fail("xTry is "+ caju.get("xTry") +". Need be "+ xTry +"!");
        }
        if (((Integer)caju.get("xCatch")).intValue() != xCatch) {
            fail("xCatch is "+ caju.get("xCatch") +". Need be "+ xCatch +"!");
        }
        if (((Integer)caju.get("xFinally")).intValue() != xFinally) {
            fail("xFinally is "+ caju.get("xFinally") +". Need be "+ xFinally +"!");
        }
    }

    /**
     * Test of getSyntax method, of class CajuScript.
     */
    @Test
    public void syntax() throws CajuScriptException {
        System.out.println("syntax");
        CajuScript caju = new CajuScript();
        caju.setSyntax(CajuScript.getGlobalSyntax("CajuBasic"));
        assertEquals(CajuScript.getGlobalSyntax("CajuBasic"), caju.getSyntax());
        Syntax syntax = new Syntax();
        caju.addSyntax("CajuTest", syntax);
        assertEquals(syntax, caju.getSyntax("CajuTest"));
    }

    /**
     * Test of context, of class CajuScript.
     */
    @Test
    public void context() throws CajuScriptException {
        System.out.println("context");
        CajuScript caju = new CajuScript();
        Context context = new Context();
        caju.setContext(context);
        assertEquals(context, caju.getContext());
    }

    /**
     * Test of eval method, of class CajuScript.
     */
    @Test
    public void evalGroup() throws Exception {
        System.out.println("evalGroup");
        String script = "x = 22 * (((78.3 + 87.6) % 1.006) + (3.8 * 3 / 6.4 - (65.8 - 34 % 4.8765)));";
        double x = 22 * (((78.3 + 87.6) % 1.006) + (3.8 * 3 / 6.4 - (65.8 - 34 % 4.8765)));
        System.out.println(x);
        CajuScript caju = new CajuScript();
        Value result = caju.eval(script);
        //-1283.9585000000004
        System.out.println(caju.get("x"));
        //assertEquals(, result);
    }

    /**
     * Test of evalFile method, of class CajuScript.
     */
    @Test
    public void evalFile() throws Exception {
        System.out.println("evalFile");
        String path = "";
        CajuScript instance = new CajuScript();
        Value expResult = null;
        Value result = instance.evalFile(path);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFunc method, of class CajuScript.
     */
    @Test
    public void getFunc() throws CajuScriptException {
        System.out.println("getFunc");
        String key = "";
        CajuScript instance = new CajuScript();
        Function expResult = null;
        Function result = instance.getFunc(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFunc method, of class CajuScript.
     */
    @Test
    public void setFunc() throws CajuScriptException {
        System.out.println("setFunc");
        String key = "";
        Function func = null;
        CajuScript instance = new CajuScript();
        instance.setFunc(key, func);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVar method, of class CajuScript.
     */
    @Test
    public void getVar() throws CajuScriptException {
        System.out.println("getVar");
        String key = "";
        CajuScript instance = new CajuScript();
        Value expResult = null;
        Value result = instance.getVar(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllKeys method, of class CajuScript.
     */
    @Test
    public void getAllKeys() throws CajuScriptException {
        System.out.println("getAllKeys");
        CajuScript instance = new CajuScript();
        Set<String> expResult = null;
        Set<String> result = instance.getAllKeys();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setVar method, of class CajuScript.
     */
    @Test
    public void setVar() throws CajuScriptException {
        System.out.println("setVar");
        String key = "";
        Value value = null;
        CajuScript instance = new CajuScript();
        instance.setVar(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toValue method, of class CajuScript.
     */
    @Test
    public void toValue() throws Exception {
        System.out.println("toValue");
        Object obj = null;
        CajuScript instance = new CajuScript();
        Value expResult = null;
        Value result = instance.toValue(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of set method, of class CajuScript.
     */
    @Test
    public void set() throws Exception {
        System.out.println("set");
        String key = "";
        Object value = null;
        CajuScript instance = new CajuScript();
        instance.set(key, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class CajuScript.
     */
    @Test
    public void get() throws Exception {
        System.out.println("get");
        String key = "";
        CajuScript instance = new CajuScript();
        Object expResult = null;
        Object result = instance.get(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImports method, of class CajuScript.
     */
    @Test
    public void getImports() throws CajuScriptException {
        System.out.println("getImports");
        CajuScript instance = new CajuScript();
        List<String> expResult = null;
        List<String> result = instance.getImports();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addImport method, of class CajuScript.
     */
    @Test
    public void addImport() throws CajuScriptException {
        System.out.println("addImport");
        String i = "";
        CajuScript instance = new CajuScript();
        instance.addImport(i);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeImport method, of class CajuScript.
     */
    @Test
    public void removeImport() throws CajuScriptException {
        System.out.println("removeImport");
        String s = "";
        CajuScript instance = new CajuScript();
        instance.removeImport(s);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cast method, of class CajuScript.
     */
    @Test
    public void cast() throws Exception {
        System.out.println("cast");
        Object value = null;
        String type = "";
        CajuScript instance = new CajuScript();
        Object expResult = null;
        Object result = instance.cast(value, type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of error method, of class CajuScript.
     */
    @Test
    public void error() throws Exception {
        System.out.println("error");
        CajuScript.error();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSameType method, of class CajuScript.
     */
    @Test
    public void isSameType() {
        System.out.println("isSameType");
        String type1 = "";
        String type2 = "";
        boolean expResult = false;
        boolean result = CajuScript.isSameType(type1, type2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPrimitiveType method, of class CajuScript.
     */
    @Test
    public void isPrimitiveType() {
        System.out.println("isPrimitiveType");
        String type = "";
        boolean expResult = false;
        boolean result = CajuScript.isPrimitiveType(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isInstance method, of class CajuScript.
     */
    @Test
    public void isInstance() throws CajuScriptException {
        System.out.println("isInstance");
        Object o = null;
        Class c = null;
        CajuScript instance = new CajuScript();
        boolean expResult = false;
        boolean result = instance.isInstance(o, c);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class CajuScript.
     */
    @Test
    public void main() throws Exception {
        System.out.println("main");
        String[] args = null;
        CajuScript.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}