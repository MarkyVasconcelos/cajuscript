# Try/Catch #

The definition is:
```
     e ^
          "".substring(0, -1)
     ^^
          System.out.println("Error: "+ e.getMessage())
     ^~^
          System.out.println("Finally...")
     ^
```


### Try/Catch: ###

Simple way to catch an exception:
```
     e ^
          "".substring(0, -1)
     ^^
          System.out.println("Error: "+ e.getMessage())
     ^
```

### Try/Finally: ###

Is possible use the variable of exception over Finally:
```
     e ^
          "".substring(0, -1)
     ^~^
          System.out.println("Finally... error: "+ e.getMessage())
     ^
```

### Exceptions Generate: ###

Create a new exceptions:
```
     e ^
          caju.error()
     ^^
          System.out.println(e.getMessage())
     ^
```

Create a new exceptions with an message:
```
     e ^
          caju.error("Error!")
     ^^
          System.out.println(e.getMessage())
     ^
```