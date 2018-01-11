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
			String ver = "1.12.2";
			//String s = new MCPOnlineJsonGen(ver, false, "snapshot").createJSON();
			String s = new MCPJsonGen(ver, false).createJSON();
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
