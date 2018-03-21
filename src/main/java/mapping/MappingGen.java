package mapping;

/**
 * JSON generation CL.
 * 
 * @author GenericSkid
 * @since 1/16/2018
 */
public class MappingGen {

	public static void main(String[] args) {
		try {
			// default version
			String ver = "1.12.2";
			if (args.length > 0) {
				ver = args[0];
			}
			// generator type
			AbstractJsonGen gen = null;
			if (args.length > 1) {
				String type = args[1];
				if (type.equals("mcp") || type.equals("online")) {
					gen = new MCPOnlineJsonGen(ver, false, "snapshot");
				} else {
					gen = new MCPJsonGen(ver, false);
				}
			}
			// print version mapping via generator
			String s = gen.createJSON();
			System.out.println(s);
		} catch (Exception e) {
			System.err.println("Failure to generate mappings: " + e.getMessage());
		}
	}

}
