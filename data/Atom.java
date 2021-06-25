package topologyreader.data;

public class Atom {
    private final String name;
    private final String x;
    private final String y;
    private final String z;

    public Atom(String name, String x, String y, String z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
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
}
