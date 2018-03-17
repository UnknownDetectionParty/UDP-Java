package party.detection.unknown.hook.json;

/**
 * @author bloo
 * @since 7/14/2017
 */
public class JsonFieldMapping {
	private final String[] mcpAliases;
	private String obfName, desc, id;

	public JsonFieldMapping(String aliases[], String desc, String id) {
		this.mcpAliases = aliases;
		this.desc = desc;
		this.id = id;
	}

	public String[] getMcpAliases() {
		return mcpAliases;
	}

	public void setObfName(String obfName) {
		this.obfName = obfName;
	}

	public String getObfName() {
		return obfName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

}
