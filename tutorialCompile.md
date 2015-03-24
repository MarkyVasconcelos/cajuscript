# Compile #

Is possible compile a script to Java Class.

Actually is a code generator, building the code Java from the Parser. Final result is a Java Class that can load the Parser faster to be executed.

To work with compiled scripts is very easy. Just start the script with **caju.compile** like that sample:

```
    caju.compile: mypackage.MyClass
    System.out.println("Hello world!");
```

### Version 0.4 ###

For compile is necessary the **bcel.jar** you can find here [jakarta.apache.org/bcel](http://jakarta.apache.org/bcel/). Wherefore for compile and run do like that:

  * Unix/Linux:
```
    java -cp "___YOUR_JDK_HOME___\lib\tools.jar:cajuscript-0.4.jar" org.cajuscript.CajuScript YOUR_SCRIPT_FILE.cj
```

  * Windows:
```
    java -cp "___YOUR_JDK_HOME___\lib\tools.jar;cajuscript-0.4.jar" org.cajuscript.CajuScript YOUR_SCRIPT_FILE.cj
```


### Version 0.3.5 ###

For compile is necessary the tools.jar that come inside the JDK. Wherefore for compile and run do like that:

  * Unix/Linux:
```
    java -cp "___YOUR_JDK_HOME___\lib\tools.jar:cajuscript-0.3.5.jar" org.cajuscript.CajuScript YOUR_SCRIPT_FILE.cj
```

  * Windows:
```
    java -cp "___YOUR_JDK_HOME___\lib\tools.jar;cajuscript-0.3.5.jar" org.cajuscript.CajuScript YOUR_SCRIPT_FILE.cj
```

### New files been created... ###

Will be created a folder **./cajuscript-classes/mypackage/** with this files:

  * MyClass.cj
  * MyClass.class
  * MyClass.java _(Version 0.3.5 only!)_

The MyClass**.cj** is for comparing with the script in executing for know if is need rebuild or not. The MyClass**.class** is the result from Java compiler.

The MyClass**.java** is the source Java generated and compiled. _(Version 0.3.5 only!)_

### Directory ###

To define another folder have two ways (the folders are created automatically):

  * Defining the folder path into of CajuScript instance:
```
    org.cajuscript.CajuScript caju = new org.cajuscript.CajuScript();
    caju.setCompileBaseDirectory("MY/PATH/TO/FILES/COMPILED");
```

  * Or defining the folder path into of Script header:
```
    caju.compile.baseDirectory: MY/PATH/TO/FILES/COMPILED
    caju.compile: mypackage.MyClass
    System.out.println("Compile ok!");
```

### Class Path _(Version 0.3.5 only!)_ ###

Running CajuScript into a web container (GlassFish, Jetty, Tomcat) because of the ClassLoader the com.sun.tools.javac.Main.compile can't found the cajuscript.jar and can't compile. Wherefore to solve this problem is necessary set the cajuscript.jar into of class path this ways:

  * Defining the class path into of CajuScript instance:
```
    org.cajuscript.CajuScript caju = new org.cajuscript.CajuScript();
    caju.setCompileClassPath("PATH/TO/CAJUSCRIPT/JAR/cajuscript.jar");
```

  * Or defining the class path into of Script header:
```
    caju.compile.classPath: PATH/TO/CAJUSCRIPT/JAR/cajuscript.jar
    caju.compile: mypackage.MyClass
    System.out.println("Compile ok!");
```