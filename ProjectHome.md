![http://cajuscript.googlecode.com/svn/trunk/cajuscript_final.png](http://cajuscript.googlecode.com/svn/trunk/cajuscript_final.png)

### Goals ###
  * CajuScript is designed to be used in short and simple scripts
    * Don't use too many lines of code when writing scripts, you'll end up harming yourself. If your script is getting too complex, then do it in pure Java;

  * Programming in CajuScript isn't supposed to be easier than programming in Java
    * If you are looking for security, stability, performance,  object oriented etc... then use Java instead of scripts;

  * Why use CajuScript then?
    * Use it for short scripts, that reduce the impact on the whole performance of your solution;
    * If you need to have a user editing your scripts. You can use your own [custom syntax](sampleSyntax.md) to make it easier for the end user to edit the script;
    * Use it for dynamic configurations, custom charts, editable math formulas, flow control, and many other uses;
    * Think where you can use scripts to improve your solutions but use it moderately, otherwise you'll risk turning maintenance, performance, debugging and security a nightmare.


### Contribute ###
You can contribute to the development of CajuScript by sending feedback and bug reports. Or click on the PayPal button below to donate money.

[![](https://www.paypal.com/en_US/i/btn/x-click-but21.gif)](http://cajuscript.googlecode.com/svn/trunk/paypal.html)

**VERSION 0.4** - [Changelog](changelog.md)

  * Many bugs solved.
  * Performance increased, much more in compiled mode.
  * [Compiled mode uses now BCEL to build directly the bytecode.](tutorialCompile.md)
  * Suport to array syntax started but the development isn't finished.

[CajuScript .Net](tutorialCajuNet.md)

Subscribe to our [official mailing list](http://groups.google.com/group/cajuscript).

Simple and powerful script to use with **Java**.

Easy create newly instances of class and invocation of methods.

  * [Introdution](http://code.google.com/p/cajuscript/wiki/tutorialIntro)

  * [Syntax](http://code.google.com/p/cajuscript/wiki/tutorialSyntax)

Tuning using [cache](tutorialCache.md) and [compile](tutorialCompile.md).

For know how you can use **CajuScript** on **Java** please see [Javadoc](http://cajuscript.googlecode.com/svn/trunk/javadoc/index.html).

**CajuScript** implements **Script Interfaces** of the **Java 6**:
```
    javax.script.ScriptEngine caju = new org.cajuscript.CajuScriptEngine();
    String javaHello = "Java: Hello!";
    caju.put("javaHello", javaHello);
    String script = "$java.lang;";
    script += "System.out.println(javaHello);";
    script += "cajuHello = 'Caju: Hi!';";
    caju.eval(script);
    System.out.println(caju.get("cajuHello"));
```

See about **[customizable syntax](sampleSyntax.md)**:

  * [Basic](sampleSyntaxBasic.md)

  * [Java](sampleSyntaxJava.md)

  * [Portuguese](sampleSyntaxPortuguese.md)

### CajuScript is very faster ###

![http://cajuscript.googlecode.com/svn/trunk/scriptstester/cajuscript035executionmodes.png](http://cajuscript.googlecode.com/svn/trunk/scriptstester/cajuscript035executionmodes.png)

[See more about this test](testCajuScript035ExecutionModes.md)

![http://cajuscript.googlecode.com/svn/trunk/scriptstester/scriptstester.png](http://cajuscript.googlecode.com/svn/trunk/scriptstester/scriptstester.png)

[See more about this test](testScriptsTester.md)

### Samples ###

  * [Read File](sampleReadFile.md)

  * [Swing](sampleSwing.md)

  * [WebServer](sampleWebServer.md)

  * [Basic Syntax](sampleSyntaxBasic.md)

  * [Java Syntax](sampleSyntaxJava.md)

  * [Portuguese Syntax](sampleSyntaxPortuguese.md)



&lt;hr/&gt;



[Credits and special thanks](credits.md)