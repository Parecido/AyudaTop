package topologyreader.data;

import java.util.ArrayList;
import java.util.List;

public class Statistic {
    private final List<Basin> basins = new ArrayList<>();
    private final List<Basin> cores = new ArrayList<>();

    private final List<Basin> mBasinsA = new ArrayList<>();
    private final List<Basin> dBasinsA = new ArrayList<>();
    private final List<Basin> pBasinsA = new ArrayList<>();
    private final List<Basin> aBasinsA = new ArrayList<>();
    private final List<Basin> dressedA = new ArrayList<>();

    private final List<Basin> mBasinsB = new ArrayList<>();
    private final List<Basin> dBasinsB = new ArrayList<>();
    private final List<Basin> pBasinsB = new ArrayList<>();
    private final List<Basin> aBasinsB = new ArrayList<>();
    private final List<Basin> dressedB = new ArrayList<>();

    private float minELF = Float.MAX_VALUE;
    private final List<Basin> minListA = new ArrayList<>();
    private final List<Basin> minListB = new ArrayList<>();

    private float maxELF = 0;
    private final List<Basin> maxListA = new ArrayList<>();
    private final List<Basin> maxListB = new ArrayList<>();

    private final List<Basin> aboveListA = new ArrayList<>();
    private final List<Basin> underListA = new ArrayList<>();

    private final List<Basin> aboveListB = new ArrayList<>();
    private final List<Basin> underListB = new ArrayList<>();

    private final List<Basin> basinAboveA = new ArrayList<>();
    private final List<Basin> basinUnderA = new ArrayList<>();

    private final List<Basin> basinAboveB = new ArrayList<>();
    private final List<Basin> basinUnderB = new ArrayList<>();

    public List<Basin> getBasins() {
        return basins;
    }

    public List<Basin> getCores() {
        return cores;
    }

    public List<Basin> getMonosinapticA() {
        return mBasinsA;
    }

    public List<Basin> getDisinapticA() {
        return dBasinsA;
    }

    public List<Basin> getPolisinapticA() {
        return pBasinsA;
    }

    public List<Basin> getAsynapticA() {
        return aBasinsA;
    }
    
    public List<Basin> getDressedA() {
        return dressedA;
    }

    public List<Basin> getMonosinapticB() {
        return mBasinsB;
    }

    public List<Basin> getDisinapticB() {
        return dBasinsB;
    }

    public List<Basin> getPolisinapticB() {
        return pBasinsB;
    }

    public List<Basin> getAsynapticB() {
        return aBasinsB;
    }
    
    public List<Basin> getDressedB() {
        return dressedB;
    }

    public float getMinELF() {
        return minELF;
    }

    public void setMinELF(float minELF) {
        this.minELF = minELF;
    }

    public List<Basin> getMinListA() {
        return minListA;
    }

    public List<Basin> getMinListB() {
        return minListB;
    }

    public float getMaxELF() {
        return maxELF;
    }

    public void setMaxELF(float maxELF) {
        this.maxELF = maxELF;
    }

    public List<Basin> getMaxListA() {
        return maxListA;
    }

    public List<Basin> getMaxListB() {
        return maxListB;
    }

    public List<Basin> getAttractorAboveA() {
        return aboveListA;
    }

    public List<Basin> getAttractorUnderA() {
        return underListA;
    }

    public List<Basin> getAttractorAboveB() {
        return aboveListB;
    }

    public List<Basin> getAttractorUnderB() {
        return underListB;
    }

    public List<Basin> getBasinAboveA() {
        return basinAboveA;
    }

    public List<Basin> getBasinUnderA() {
        return basinUnderA;
    }

    public List<Basin> getBasinAboveB() {
        return basinAboveB;
    }

    public List<Basin> getBasinUnderB() {
        return basinUnderB;
    }
}
