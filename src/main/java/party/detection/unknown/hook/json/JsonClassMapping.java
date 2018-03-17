package party.detection.unknown.hook.json;

/**
 * @author bloo
 * @since 7/14/2017
 */
public class JsonClassMapping {
    private String obfName, id;
    private JsonFieldMapping[] fieldMappings;
    private JsonMethodMapping[] methodMappings;

    public JsonClassMapping(String obfName, String id, JsonFieldMapping[] fieldMappings, JsonMethodMapping[] methodMappings) {
        this.obfName = obfName;
        this.id = id;
        this.fieldMappings = fieldMappings;
        this.methodMappings = methodMappings;
    }

    public void setObfName(String obfName) {
        this.obfName = obfName;
    }

    public String getObfName() {
        return obfName;
    }

    public String getId() {
        return id;
    }

    public JsonFieldMapping[] getFieldMappings() {
        return fieldMappings;
    }

    public JsonMethodMapping[] getMethodMappings() {
        return methodMappings;
    }
}
