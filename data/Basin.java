package topologyreader.data;

public class Basin {
    private final String id;
    private final String name;
    private final String volume;
    private final String N;
    private final String pab;
    private final String paa;
    private final String pbb;
    private final String s2;
    private final String std;

    public Basin(String id, String name, String volume, String N, String pab, String paa, String pbb, String s2, String std) {
        this.id = id;
        this.name = name;
        this.volume = volume;
        this.N = N;
        this.pab = pab;
        this.paa = paa;
        this.pbb = pbb;
        this.s2 = s2;
        this.std = std;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVolume() {
        return volume;
    }

    public String getN() {
        return N;
    }

    public String getPAB() {
        return pab;
    }

    public String getPAA() {
        return paa;
    }

    public String getPBB() {
        return pbb;
    }

    public String getS2() {
        return s2;
    }

    public String getSTD() {
        return std;
    }

    public boolean isCore() {
        return getName().substring(0, 1).equals("C");
    }
}
