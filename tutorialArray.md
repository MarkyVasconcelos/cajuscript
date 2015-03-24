# Array #


### Creating: ###
```
    myArray = array.create("TYPE", LENGTH)
```

### Types allowed: ###

Int (case-insensitive):
```
    array.create("int", len)
    array.create("i", len)
```

Long (case-insensitive):
```
    array.create("long", len)
    array.create("l", len)
```

Float (case-insensitive):
```
    array.create("float", len)
    array.create("f", len)
```

Double (case-insensitive):
```
    array.create("double", len)
    array.create("d", len)
```

Char (case-insensitive):
```
    array.create("char", len)
    array.create("c", len)
```

Boolean (case-insensitive):
```
    array.create("boolean", len)
    array.create("bool", len)
    array.create("b", len)
```

Byte (case-insensitive):
```
    array.create("byte", len)
    array.create("bt", len)
```

String (case-insensitive):
```
    array.create("string", len)
    array.create("s", len)
```

Any class (case-sensitive):
```
    array.create("java.lang.Object", len)
```

### Set and get values from an array: ###
```
    STRING_ARRAY = array.create("s", 1)
    // SET
    array.set(STRING_ARRAY, 0, "Zero")
    // GET
    strZero = array.get(STRING_ARRAY, 0)
```

### Lenght: ###
```
    len = array.size(myArray)
```

### Interaction: ###
```
    // Create
    strArray = array.create("java.lang.String", 2)

    // Setting
    array.set(strArray, 0, "String 0")
    array.set(strArray, 1, "String 1")

    // Interaction
    x = 0
    x < array.size("s", 2) @
       // Getting
       System.out.println(array.get(strArray, x))
       x = x + 1
    @
```