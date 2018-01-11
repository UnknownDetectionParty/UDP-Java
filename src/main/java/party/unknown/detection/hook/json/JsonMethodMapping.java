package party.unknown.detection.hook.json;

/**
 * @author bloo
 * @since 7/14/2017
 */
public class JsonMethodMapping {
    private String obfName, desc, id;
    private JsonEventInjection[] inj;

    public JsonMethodMapping(String obfName, String desc, String id, JsonEventInjection[] inj) {
        this.obfName = obfName;
        this.desc = desc;
        this.id = id;
        this.inj = inj;
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
