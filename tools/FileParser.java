package topologyreader.tools;

import com.google.common.base.Splitter;
import topologyreader.data.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileParser {
    private final List<Attractor> attractors = new ArrayList<>();
    private final List<Basin> basins = new ArrayList<>();
    private final List<Atom> atoms = new ArrayList<>();
    private float intDensity;
    private float sumPopulation;

    public List<Attractor> getAttractors() {
        return attractors;
    }

    public List<Basin> getBasins() {
        return basins;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }
    
    public float getIntDensity() {
        return intDensity;
    }
    
    public float getSumPopulation() {
        return sumPopulation;
    }

    public void parseFile(FileResult file) {
        String line;
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        int attractorN = 0;
        int basinN = 0;

        try (BufferedReader in = new BufferedReader(new FileReader(file.getFile()))) {
            while ((line = in.readLine()) != null) {
                if (line.contains("number of attractors:")) {
                    attractorN = Integer.parseInt(splitLine(line).get(3));
                }

                if (line.contains("number of localization basins")) {
                    basinN = Integer.parseInt(splitLine(line).get(4));
                }

                if (line.contains("elapsed time in attractor assignment")) {
                    try (Stream<String> lines_attractors = in.lines()) {
                        lines_attractors.limit(attractorN + 4).forEach(line_attractor -> {
                            if (!line_attractor.isEmpty()) {
                                List<String> splitLine = splitLine(line_attractor);

                                String id = splitLine.get(0);
                                String name = splitLine.get(5);
                                String value = splitLine.get(4);
                                String volume = splitLine.size() == 10 ? splitLine.get(8) : splitLine.get(7);
                                String x = splitLine.get(1);
                                String y = splitLine.get(2);
                                String z = splitLine.get(3);
                                String type = splitLine.size() == 10 ? splitLine.get(9) : splitLine.get(8);

                                if (!volume.equals("0.00")) {
                                    Attractor attractor = new Attractor(id, name, value, volume, x, y, z, type);
                                    attractors.add(attractor);
                                }
                            }
                        });
                    }
                }

                if (line.contains("basin               vol.    pop.    pab     paa    pbb     sigma2  std. dev.")) {
                    try (Stream<String> lines_basins = in.lines()) {
                        lines_basins.limit(basinN + 3).forEach(line_basin -> {
                            if (!line_basin.isEmpty()) {
                                List<String> splitLine = splitLine(line_basin);

                                String id = splitLine.get(0);
                                String name = splitLine.get(1);
                                String volume = splitLine.get(2);
                                String N = splitLine.get(3);
                                String pab = splitLine.get(4);
                                String paa = splitLine.get(5);
                                String pbb = splitLine.get(6);
                                String s2 = splitLine.get(7);
                                String std = splitLine.get(8);

                                if (!volume.equals("0.00")) {
                                    Basin basin = new Basin(id, name, volume, N, pab, paa, pbb, s2, std);
                                    basins.add(basin);
                                }
                            }
                        });
                    }
                }

                if (line.contains("Atom(")) {
                    List<String> splitLine = splitLine(line);
                    if (splitLine.size() > 5) {
                        String name = splitLine.get(5);
                        String x = splitLine.get(1);
                        String y = splitLine.get(2);
                        String z = splitLine.get(3);

                        String atomSub;
                        int s = name.lastIndexOf('(') + 1;
                        if (name.contains(")")) {
                            int e = name.lastIndexOf(')');
                            atomSub = name.substring(s, e).trim();
                        } else {
                            atomSub = name.substring(s).trim();
                        }

                        Matcher matcher = pattern.matcher(atomSub);
                        if (matcher.find()) {
                            atoms.add(new Atom(matcher.group(), x, y, z));
                        }
                    }
                }
                
                if (line.contains("total integrated density")) {
                    intDensity = Float.parseFloat(splitLine(line).get(3));
                }
                
                if (line.contains("sum of populations")) {
                    sumPopulation = Float.parseFloat(splitLine(line).get(3));
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> splitLine(String line) {
        return Splitter.on(" ").omitEmptyStrings()
                .trimResults()
                .splitToList(line);
    }
    
    public Statistic getStatistics() {
        Statistic statistic = new Statistic();
        for (int i = 0; i < attractors.size(); i++) {
            Attractor attractor = attractors.get(i);
            Basin basin = basins.get(i);

            String id = attractor.getName();
            String name = attractor.getName();
            String ELF = attractor.getValue();
            float fELF = Float.parseFloat(ELF);

            String N = basin.getN();
            float fN = Float.parseFloat(N);
            String PAB = basin.getPAB();
            String PAA = basin.getPAA();
            String PBB = basin.getPBB();
            String S2 = basin.getS2();
            String STD = basin.getSTD();

            statistic.getBasins().add(basin);

            Basin basinWithAttractorNameELFvalue = new Basin(id, name, ELF, N, PAB, PAA, PBB, S2, STD);
            if (name.startsWith("V(")) {
                if (name.contains("Asyn")) {
                    statistic.getAsynapticA().add(basinWithAttractorNameELFvalue);
                } else {
                    switch (name.split(",").length) {
                        case 2:
                            statistic.getDisinapticA().add(basinWithAttractorNameELFvalue);
                            break;
                        case 1:
                            if (name.startsWith("V(H")) {
                                if (name.startsWith("V(H)") || Character.isDigit(name.charAt(3))) { 
                                    statistic.getDressedA().add(basinWithAttractorNameELFvalue);
                                }
                            }
                            statistic.getMonosinapticA().add(basinWithAttractorNameELFvalue);
                            break;
                        default:
                            statistic.getPolisinapticA().add(basinWithAttractorNameELFvalue);
                            break;
                    }
                }

                addMinList(fELF, statistic, statistic.getMinListA(), basinWithAttractorNameELFvalue);
                addMaxList(fELF, statistic, statistic.getMaxListA(), basinWithAttractorNameELFvalue);
            }

            name = basin.getName();
            Basin basinWithELFvalue = new Basin(id, name, ELF, N, PAB, PAA, PBB, S2, STD);

            if (name.startsWith("V(")) {
                if (name.contains("Asyn")) {
                    statistic.getAsynapticB().add(basinWithELFvalue);
                } else {
                    switch (name.split(",").length) {
                        case 2:
                            statistic.getDisinapticB().add(basinWithELFvalue);
                            break;
                        case 1:
                            if (name.startsWith("V(H")) {
                                if (name.startsWith("V(H)") || Character.isDigit(name.charAt(3))) { 
                                    statistic.getDressedB().add(basinWithELFvalue);
                                }
                            }
                            statistic.getMonosinapticB().add(basinWithELFvalue);
                            break;
                        default:
                            statistic.getPolisinapticB().add(basinWithELFvalue);
                            break;
                    }
                }
            } else {
                statistic.getCores().add(basinWithELFvalue);
            }

            addMinList(fELF, statistic, statistic.getMinListB(), basinWithELFvalue);
            addMaxList(fELF, statistic, statistic.getMaxListB(), basinWithELFvalue);

            if (fELF >= 0.5) {
                statistic.getAttractorAboveA().add(basinWithAttractorNameELFvalue);
                statistic.getAttractorAboveB().add(basinWithELFvalue);
            } else {
                statistic.getAttractorUnderA().add(basinWithAttractorNameELFvalue);
                statistic.getAttractorUnderB().add(basinWithELFvalue);
            }

            if (fN >= 1f) {
                statistic.getBasinAboveA().add(basinWithAttractorNameELFvalue);
                statistic.getBasinAboveB().add(basinWithELFvalue);
            } else {
                statistic.getBasinUnderA().add(basinWithAttractorNameELFvalue);
                statistic.getBasinUnderB().add(basinWithELFvalue);
            }
        }
        
        return statistic;
    }

    private void addMinList(float ELF, Statistic statistic, List<Basin> list, Basin basin) {
        boolean isCore = basin.isCore();

        if (ELF <= statistic.getMinELF() && !isCore) {
            statistic.setMinELF(ELF);

            if (!list.isEmpty()) {
                float value = Float.parseFloat(list.get(0).getVolume());
                if (value > ELF) {
                    list.clear();
                }
            }

            list.add(basin);
        }
    }

    private void addMaxList(float ELF, Statistic statistic, List<Basin> list, Basin basin) {
        boolean isCore = basin.isCore();

        if ((ELF >= statistic.getMaxELF() && ELF < 1) && !isCore) {
            statistic.setMaxELF(ELF);

            if (!list.isEmpty()) {
                float value = Float.parseFloat(list.get(0).getVolume());
                if (value < ELF) {
                    list.clear();
                }
            }

            list.add(basin);
        }
    }
}
