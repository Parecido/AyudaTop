package topologyreader.data;

public class Attractor {
    private final String id;
    private final String name;
    private final String value;
    private final String volume;
    private final String x;
    private final String y;
    private final String z;
    private final String type;

    public Attractor(String id, String name, String value, String volume, String x, String y, String z, String type) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.volume = volume;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getVolume() {
        return volume;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public String getType() {
        return type;
    }

    public boolean isCore() {
        return getName().substring(0, 1).equals("C");
    }
}
