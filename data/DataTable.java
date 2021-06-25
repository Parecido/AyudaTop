package topologyreader.data;

public class DataTable {
    private final Attractor attractor;
    private final Basin basin;

    public DataTable(Attractor attractor, Basin basin) {
        this.attractor = attractor;
        this.basin = basin;
    }

    public Attractor getAttractor() {
        return attractor;
    }

    public Basin getBasin() {
        return basin;
    }

    public String getIndex() {
        return attractor.getId();
    }

    public String getAttractorName() {
        return attractor.getName();
    }

    public String getValue() {
        return attractor.getValue();
    }

    public String getX() {
        return attractor.getX();
    }

    public String getY() {
        return attractor.getY();
    }

    public String getZ() {
        return attractor.getZ();
    }

    public String getBasinName() {
        return basin.getName();
    }

    public String getN() {
        return basin.getN();
    }

    public String getPAB() {
        return basin.getPAB();
    }

    public String getPAA() {
        return basin.getPAA();
    }

    public String getPBB() {
        return basin.getPBB();
    }

    public String getS2() {
        return basin.getS2();
    }

    public String getSTD() {
        return basin.getSTD();
    }
}
