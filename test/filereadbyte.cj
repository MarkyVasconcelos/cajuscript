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

