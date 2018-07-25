package party.detection.unknown.hook.json;

/**
 * @author bloo
 * @since 7/14/2017
 */
public class JsonMethodMapping {
	private final String mcpName;
	private String obfName, desc, id;
	private JsonEventInjection[] inj;

	public JsonMethodMapping(String mcpName, String desc, String id, JsonEventInjection[] inj) {
		this.mcpName = mcpName;
		this.desc = desc;
		this.id = id;
		this.inj = inj;
	}

	public String getMcpAlias() {
		return mcpName;
	}

	public void setObfName(String obfName) {
		this.obfName = obfName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getObfName() {
		return obfName;
	}

	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

	public JsonEventInjection[] getInjections() {
		return inj;
	}

}
