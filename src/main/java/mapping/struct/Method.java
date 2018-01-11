package mapping.struct;

import java.util.ArrayList;
import java.util.List;

import party.unknown.detection.hook.json.JsonEventInjection;
import party.unknown.detection.hook.json.JsonMethodMapping;

/**
 * Method mapping wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public abstract class Method {
	private final List<JsonMethodMapping> mappingList = new ArrayList<>();

	public void add(String id, String mcpName, String mcpDesc) {
		mappingList.add(new JsonMethodMapping(mcpName, mcpDesc, id, null));
	}

	public void add(String id, String mcpName, String mcpDesc, JsonEventInjection[] inj) {
		mappingList.add(new JsonMethodMapping(mcpName, mcpDesc, id, inj));
	}

	public JsonMethodMapping[] array() {
		return mappingList.toArray(new JsonMethodMapping[0]);
	}
}