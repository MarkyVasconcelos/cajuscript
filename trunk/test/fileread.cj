$"filereadall.cj"
$"filereadbyte.cj"




System.out.println(Integer.parseInt("111100000011", 2))
System.out.println(Integer.parseInt("0100000000", 2))
System.out.println(Integer.parseInt("0010000000", 2))
System.out.println(Integer.parseInt("0001000000", 2))
System.out.println(Integer.parseInt("0000100000", 2))
System.out.println(Integer.parseInt("0000010000", 2))
System.out.println(Integer.parseInt("0000001000", 2))
System.out.println(Integer.parseInt("0000000100", 2))
System.out.println(Integer.parseInt("0000000010", 2))
System.out.println(Integer.parseInt("0000000001", 2))

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
