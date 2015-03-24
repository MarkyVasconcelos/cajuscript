# CajuScript Introdution #

CajuScript:

  * is an easy to use script language for Java.
  * has good features to create newly instances of class and invocation of methods.

In CajuScript was implemented many ways to make easy to programme in Java.

Supports all basics resources of programing like:

  * Variables
  * [Arrays](tutorialArray.md)
  * [If](tutorialIf.md)
  * [Loop](tutorialLoop.md)
  * [Function](tutorialFunction.md)
  * [Imports and Includes](tutorialImportAndInclude.md)
  * [Cast](tutorialCast.md)
  * [Try/Catch](tutorialTryCatch.md)

About [custom syntax](sampleSyntax.md) and these proposals:
  * [Basic](sampleSyntaxBasic.md)
  * [Java](sampleSyntaxJava.md)
  * [Portuguese](sampleSyntaxPortuguese.md)

Easy to use Java. Sample:
```
    $java.lang
    str = "Hello world!"
    len = 0
    len < str.length() @
        System.out.println(str.substring(len, len + 1))
        len += 1
    @
    System.out.println(str.replaceAll("Hello", "Hi"))
    str = StringBuilder(str)
    str.append(" Bye bye bye...")
    System.out.println(str)
```

CajuScript supports [programming in single line](tutorialInLine.md).

You can see the [overview of syntax](http://code.google.com/p/cajuscript/wiki/tutorialSyntax).