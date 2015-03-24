# Introduction #

This pages teachs you how to get and compile CajuScript on your machine.
You will need:

  1. Java - JDK (http://www.java.sun.com)
  1. Apache Ant (http://ant.apache.org)
  1. Subversion
  1. CajuScript
  1. Dependencies:
    * **Version 0.3.5:** Put your JDK "lib/tools.jar" in the CLASSPATH
    * **Version 0.4 or later:** Put the **bcel.jar** (you can find here [jakarta.apache.org/bcel](http://jakarta.apache.org/bcel/)) in the CLASSPATH

## Getting CajuScript ##

  * Open a prompt (DOS, Bash, etc) and go to the desired folder.
  * Type the command:

`svn checkout http://cajuscript.googlecode.com/svn/trunk/ cajuscript`

  * Now, you will have a folder named “cajuscript”. Enter it.
  * Inside the folder, type the command:

`ant`

  * It will compile all files and generate a .jar file inside the “dist” folder.
  * Now, let´s have some fun:

`java –cp dist/cajuscript-DATE.jar   org.cajuscript.irc.CajuConsole`