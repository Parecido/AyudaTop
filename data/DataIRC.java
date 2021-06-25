package topologyreader.data;

import java.util.List;
import java.util.stream.Collectors;

public class DataIRC {
    private final FileResult file;
    private final List<Attractor> attractors;
    private final List<Basin> basins;
    private final List<Atom> atoms;
    private final Statistic statistic;
    private final float intDensity;
    private final float sumPopulation;
    
    public DataIRC(FileResult file, List<Attractor> attractors, List<Basin> basins, List<Atom> atoms, Statistic statistic, float intDensity, float sumPopulation) {
        this.file = file;
        this.attractors = attractors;
        this.basins = basins;
        this.atoms = atoms;
        this.statistic = statistic;
        this.intDensity = intDensity;
        this.sumPopulation = sumPopulation;
    }

    public FileResult getFileResult() {
        return file;
    }

    public int getIndex() {
        return file.getIndex();
    }

    public String getFileName() {
        return file.getName();
    }

    public List<Attractor> getAttractors() {
        return attractors;
    }

    public List<Basin> getBasins() {
        return basins;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public List<Attractor> getCores() {
        return attractors
                .stream()
                .filter(attractor -> attractor.isCore())
                .collect(Collectors.toList());
    }

    public List<Attractor> getHydrogens() {
        return attractors
                .stream()
                .filter(attractor -> attractor.getType().equals("2"))
                .collect(Collectors.toList());
    }
    
    public Statistic getStatistic() {
        return statistic;
    }

    public String calculateSynError() {
        int count = 0;

        for (int i = 0; i < attractors.size(); i++) {
            Attractor attractor = attractors.get(i);
            Basin basin = basins.get(i);

            if (attractor.getName().equals(basin.getName())) {
                count++;
            }
        }

        float percent = ((float) count / (float) attractors.size()) * 100;
        return Math.round(percent) + "% " + "(" + count + ")";
    }
    
    public String calculatePopError() {
        float error = intDensity - sumPopulation;
        return (Math.round(error * 100.0) / 100.0) + " e";
    }
}
