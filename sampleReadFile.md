# File Read #

This sample shows two ways of reading a file. The first reads byte by byte. Second reads all in a buffer. It also shows the time spent for each operation.
**Command line**:

> java -jar cajuscript.jar fileread.cj

**fileread.cj**:
```
    $java.lang
    $"filereadbyte.cj"
    $"filereadall.cj"

    System.out.println()
    System.out.println("====================")
    System.out.println("READ BYTE - file.txt")
    System.out.println("====================")
    time = System.currentTimeMillis()
    System.out.println(readByte("file.txt"))
    System.out.println(">>> Time reading: "+ (System.currentTimeMillis() - time) + " ms")

    System.out.println()
    System.out.println("===================")
    System.out.println("READ ALL - file.txt")
    System.out.println("===================")
    time = System.currentTimeMillis()
    System.out.println(readAll("file.txt"))
    System.out.println(">>> Time reading: "+ (System.currentTimeMillis() - time) + " ms")
```

**filereadbyte.cj**:
```
    $java.lang
    $java.io

    readByte(filename) #
	fileinput = FileInputStream(filename)
	content = ""
	loop = 1
	loop = 1 @
		byte = fileinput.read()
		byte = -1 ?
			loop = 2
		??
			content += caju.cast(byte, "c")
		?
	@
	~ content.toString()
    #
```

**filereadall.cj**:
```
    $java.lang
    $java.io

    readAll(filename) #
	fileinput = FileInputStream(filename)
	bytes = array.create("byte", fileinput.available())
	fileinput.read(bytes)
	~ String(bytes)
    #
```

**file.txt**:

> The Java Programming Language is a general-purpose, concurrent,

> strongly typed, class-based object-oriented language. It is

> normally compiled to the bytecode instruction set and binary

> format defined in the Java Virtual Machine Specification.