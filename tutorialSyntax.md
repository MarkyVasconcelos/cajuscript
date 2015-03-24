# Syntax #

See too about [custom syntax](sampleSyntax.md) and these proposals:
  * [Basic](sampleSyntaxBasic.md)
  * [Java](sampleSyntaxJava.md)
  * [Portuguese](sampleSyntaxPortuguese.md)

### [Imports and include file](tutorialImportAndInclude.md): ###
```
     // Import
     $java.lang
     // Include file
     $"script.cj"
```

### Variables: ###
```
     // Defining a new variable
     x = 0
     x = "Programing 'Java' with CajuScript!"
     x = 'Programing "Java" with CajuScript!'
     x = "Programing \"Java\" with CajuScript!"
     x = 'Programing \'Java\' with CajuScript!'
     x = StringBuffer(x)
     x.append(" "+ x)
     System.out.println(x.toString())
```

### [IF](tutorialIf.md): ###
```
     // IF
     x = 10
     x < 10 & x > 0 ?
         System.out.println("X is less than 10!")
     ? x > 10 & x ! 10 ?
         System.out.println("X is greater than 10!")
     ? x = 10 ?
         System.out.println("X is 10!")
     ??
         System.out.println("X is less than 1!")
     ?
```

### [LOOP](tutorialLoop.md): ###
```
     // LOOP
     x = 0
     x < 10 & x >= 0 @
        System.out.println(x)
        x += 1
     @
```

### [FUNCTION](tutorialFunction.md): ###
```
     // FUNCTION
     // Allowed:
     // addWithX v1, v2 # ... #
     // addWithX(v1 v2) # ... #
     x = 5
     addWithX(v1, v2) #
         // "~" is the return
         ~ x + v1 + v2
     #
     x = addWithX(10, 20)
     System.out.println("X = "+ x)
```

### Null value: ###
```
     // $ is the null value
     x = $
```

### [Arithmetic Operators](tutorialArithmetic.md): ###
```
     // + Addition
     x = 0 + 1
     x += 1
     
     // - Subtraction
     x = 0 - 1
     x -= 1
     
     // * Multiplication
     x = 0 * 1
     x *= 1
     
     // / Division
     x = 0 / 1
     x /= 1
     
     // % Modulus
     x = 0 % 1
     x %= 1
```

### [Comparison Operators](tutorialComparison.md): ###
```
     // = Equal to
     (x = y)
     
     // ! Not equal to
     (x ! y)
     
     // < Less Than
     (x < y)
     
     // > Greater Than
     (x > y)
     
     // < Less Than or Equal To
     (x <= y)
     
     // > Greater Than or Equal To
     (x >= y)
```

### [Comments allowed](tutorialComment.md): ###
```
     -- Comment...

     // Comment...

     \ Comment...
```

### [Try/Catch](tutorialTryCatch.md): ###
```
     // Try/Catch
     e ^
          "".substring(0, -1)
     ^^
          System.out.println("Error: "+ e.getMessage())
     ^~^
          System.out.println("Finally...")
     ^
```