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
			//
			// This will work when MCP is released.
			// Until then you have to install the mappings yourself if you want to update the config file.
			//
			// The mappings are under "docs/versions/1.13.0/"
			//
			// Extract the file to: "C:\Users\You\.gradle\caches\minecraft\de\oceanlabs\mcp\mcp_snapshot\" 
			String ver = "1.13";
			if (args.length > 0) {
				ver = args[0];
			}
			// generator type
			AbstractJsonGen gen = new MCPJsonGen(ver, false);
			if (args.length > 1) {
				String type = args[1];
				if (type.equals("mcp") || type.equals("online")) {
					gen = new MCPOnlineJsonGen(ver, false, "snapshot");
				}
			}
			// print version mapping via generator
			String s = gen.createJSON();
			System.out.println(s);
		} catch (Exception e) {
			System.err.println("Failure to generate mappings: " + e.getMessage());
			if (e.getMessage() == null) {
				e.printStackTrace();
			}
		}
	}

}
