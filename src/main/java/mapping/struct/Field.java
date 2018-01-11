package mapping.struct;

import java.util.ArrayList;
import java.util.List;

import party.unknown.detection.hook.json.JsonFieldMapping;

/**
 * Field mapping wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public abstract class Field {
	private final List<JsonFieldMapping> mappingList = new ArrayList<>();

	public void add(String id, String... mcpNames) {
		mappingList.add(new JsonFieldMapping(mcpNames, null, id));
	}

	public JsonFieldMapping[] array() {
		return mappingList.toArray(new JsonFieldMapping[0]);
	}
}