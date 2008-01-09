$java.lang
$"filereadall.cj"
$"filereadbyte.cj"

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