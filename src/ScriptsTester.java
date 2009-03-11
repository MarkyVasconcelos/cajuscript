public class ScriptsTester {
	public static void main(String[] args) throws Exception {
		runTester();
	}

	public static void runTester() throws Exception {
		org.cajuscript.CajuScript caju = new org.cajuscript.CajuScript();
		StringBuilder builder = new StringBuilder();
		builder.append("name =\"\"\n");
		builder.append("setName name #\n");
		builder.append(".name = name\n");
		builder.append("#\n");
		builder.append("getName #\n");
		builder.append("~name\n");
		builder.append("#\n");
		Nameable obj = caju.asObject(builder.toString(), Nameable.class);
		obj.setName("Marky");
		System.out.println(obj.getName());
		obj.setName("Marcos");
		System.out.println(obj.getName());
	}

	interface Nameable {
		String getName();

		void setName(String a);
	}
}
