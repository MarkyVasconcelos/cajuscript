# Java Syntax #

This syntax come with CajuScript and can be used. But you can [create new syntax](sampleSyntax.md).

To use this syntax is necessary that first line of the script must be equal at:
"**caju.syntax: CajuJava**"

**Command line**:

> java -jar cajuscript.jar syntax\_java.cj

**syntax\_java.cj**:
```
caju.syntax: CajuJava

    import java.lang

    // IF
    x = 10
    if x < 10 & x > 0 {
        System.out.println("X is less than 10!")
    } else if x > 10 & x ! 10 {
        System.out.println("X is greater than 10!")
    } else if x = 10 {
        System.out.println("X is 10!")
    } else {
        System.out.println("X is less than 1!")
    }

    // LOOP
    x = 0
    while x < 100 & x >= 0 {
        System.out.println(x)
        x += 1
        if x = 10 {
            break
        } else {
            continue
        }
    }

    // FUNCTION
    x = 5
    function addWithX(v1, v2) {
        return root.x + v1 + v2
    }
    x = addWithX(10, 20)
    System.out.println("X = "+ x)

    // TRY/CATCH
    try e {
        "".substring(0, -1)
    } catch {
        System.out.println("Error: "+ e.getMessage())
    } finally {
        System.out.println("Finally...")
    }
```