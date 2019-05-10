http://cajuscript.googlecode.com/svn/trunk/cajuscript_final.png



===Goals===
  * CajuScript is designed to be used in short and simple scripts
    * Don't use too many lines of code when writing scripts, you'll end up harming yourself. If your script is getting too complex, then do it in pure Java;

  * Programming in CajuScript isn't supposed to be easier than programming in Java
    * If you are looking for security, stability, performance,  object oriented etc... then use Java instead of scripts;

  * Why use CajuScript then?
    * Use it for short scripts, that reduce the impact on the whole performance of your solution;
    * If you need to have a user editing your scripts. You can use your own [sampleSyntax custom syntax] to make it easier for the end user to edit the script;
    * Use it for dynamic configurations, custom charts, editable math formulas, flow control, and many other uses;
    * Think where you can use scripts to improve your solutions but use it moderately, otherwise you'll risk turning maintenance, performance, debugging and security a nightmare.


===Contribute===
You can contribute to the development of CajuScript by sending feedback and bug reports. Or click on the PayPal button below to donate money.

[http://cajuscript.googlecode.com/svn/trunk/paypal.html https://www.paypal.com/en_US/i/btn/x-click-but21.gif]

*VERSION 0.4* - [changelog Changelog]

  * Many bugs solved.
  * Performance increased, much more in compiled mode.
  * [tutorialCompile Compiled mode uses now BCEL to build directly the bytecode.]
  * Suport to array syntax started but the development isn't finished.

[tutorialCajuNet CajuScript .Net]

Subscribe to our [http://groups.google.com/group/cajuscript official mailing list].

Simple and powerful script to use with *Java*.

Easy create newly instances of class and invocation of methods.

  * [http://code.google.com/p/cajuscript/wiki/tutorialIntro Introdution]

  * [http://code.google.com/p/cajuscript/wiki/tutorialSyntax Syntax]

Tuning using [tutorialCache cache] and [tutorialCompile compile].

For know how you can use *CajuScript* on *Java* please see [http://cajuscript.googlecode.com/svn/trunk/javadoc/index.html Javadoc].

*CajuScript* implements *Script Interfaces* of the *Java 6*:
{{{
    javax.script.ScriptEngine caju = new org.cajuscript.CajuScriptEngine();
    String javaHello = "Java: Hello!";
    caju.put("javaHello", javaHello);
    String script = "$java.lang;";
    script += "System.out.println(javaHello);";
    script += "cajuHello = 'Caju: Hi!';";
    caju.eval(script);
    System.out.println(caju.get("cajuHello"));
}}}

See about *[sampleSyntax customizable syntax]*:

  * [sampleSyntaxBasic Basic]

  * [sampleSyntaxJava Java]

  * [sampleSyntaxPortuguese Portuguese]

===CajuScript is very faster===

http://cajuscript.googlecode.com/svn/trunk/scriptstester/cajuscript035executionmodes.png

[testCajuScript035ExecutionModes See more about this test]

http://cajuscript.googlecode.com/svn/trunk/scriptstester/scriptstester.png

[testScriptsTester See more about this test]

===Samples===

   * [sampleReadFile Read File]

   * [sampleSwing Swing]

   * [sampleWebServer WebServer]

   * [sampleSyntaxBasic Basic Syntax]

   * [sampleSyntaxJava Java Syntax]

   * [sampleSyntaxPortuguese Portuguese Syntax]

<hr/>

[credits Credits and special thanks]
