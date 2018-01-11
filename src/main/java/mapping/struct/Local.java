package mapping.struct;

import java.util.ArrayList;
import java.util.List;

import party.unknown.detection.hook.json.JsonEventInjection;
import party.unknown.detection.hook.json.JsonMappingHandler;

/**
 * Method local variable mapping wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public abstract class Local {
	private final List<JsonEventInjection> injList = new ArrayList<>();

	public void add(Class<?> eventClass, int pos, int... locals) {
		String id = JsonMappingHandler.INSTANCE.getEventID(eventClass);
		injList.add(new JsonEventInjection(id, pos, locals));
	}

	public JsonEventInjection[] array() {
		return injList.toArray(new JsonEventInjection[0]);
	}
}