package party.unknown.detection.hook.json;

/**
 * @author bloo
 * @since 7/31/2017
 */
public class JsonEventInjection {
    private String id;
    private int pos;
    private int[] locals;

    public JsonEventInjection(String id, int pos, int...locals) {
        this.id = id;
        this.pos = pos;
        this.locals = locals;
    }

    public String getEventId() {
        return id;
    }

    public int getInjectionPos() {
        return pos;
    }

    public int[] getLocals() {
        return locals;
    }
}
