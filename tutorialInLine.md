# Programing in single line #

CajuScript supports programing in single line with **";"** separator.

**Sample:**

```
    $java.lang; x = 10;
    x < 10 & x > 0 ? System.out.println("X is less than 10!");
    ? x > 10 & x ! 10 ? System.out.println("X is greater than 10!");
    ? x = 10 ? System.out.println("X is 10!");
    ?? System.out.println("X is less than 1!");
    ?
    x = 0; x < 10 & x >= 0 @ System.out.println(x); x += 1; @
```