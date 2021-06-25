package topologyreader.tools;

import com.google.common.collect.Multimap;
import javafx.geometry.Point3D;
import topologyreader.data.*;
import topologyreader.data.type.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ResultParser {
    private final Mode mode;
    private Properties settings;
    private Multimap<String, Point> dataPoints;
    private List<SearchTable> searchData;
    private List<Attractor> cores;
    private int statisticType;

    public ResultParser(Mode mode) {
        this.mode = mode;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    public void setDataPoints(Multimap<String, Point> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public void setSearchData(List<SearchTable> searchData) {
        this.searchData = searchData;
    }

    public void setCores(List<Attractor> cores) {
        this.cores = cores;
    }

    public void setStatisticType(int statisticType) {
        this.statisticType = statisticType;
    }

    public void parseData(DataIRC data) {
        List<Attractor> attractors = data.getAttractors();
        List<Basin> basins = data.getBasins();

        switch (mode) {
            case SEARCH:
                int index = data.getIndex();

                for (int i = 0; i < attractors.size(); i++) {
                    for (SearchTable sData : searchData) {
                        int start = sData.getFileNumStart();
                        int end = sData.getFileNumEnd();
                        boolean range = index >= start && index <= end;
                        String name = sData.getName().getText();

                        if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                            String attractorName = attractors.get(i).getName();
                            boolean nameCheck = name.equals(attractorName);
                            if (nameCheck && range) {
                                float population = Float.parseFloat(basins.get(i).getN());
                                dataPoints.put(attractorName, new Point(data, population));
                            }
                        } else {
                            String basinName = basins.get(i).getName();
                            boolean nameCheck = name.equals(basinName);
                            if (nameCheck && range) {
                                float population = Float.parseFloat(basins.get(i).getN());
                                dataPoints.put(basinName, new Point(data, population));
                            }
                        }
                    }
                }
                break;
            case SCAN:
                List<Attractor> coreList = new ArrayList<>();
                attractors.forEach((attractor) -> {
                    String name = attractor.getName();
                    if (name.equals(cores.get(0).getName()) || name.equals(cores.get(1).getName())) {
                        coreList.add(attractor);
                    }
                });

                float atom0X = Float.parseFloat(coreList.get(0).getX());
                float atom0Y = Float.parseFloat(coreList.get(0).getY());
                float atom0Z = Float.parseFloat(coreList.get(0).getZ());

                float atom1X = Float.parseFloat(coreList.get(1).getX());
                float atom1Y = Float.parseFloat(coreList.get(1).getY());
                float atom1Z = Float.parseFloat(coreList.get(1).getZ());

                Point3D atom0 = new Point3D(atom0X, atom0Y, atom0Z);
                Point3D atom1 = new Point3D(atom1X, atom1Y, atom1Z);

                for (int i = 0; i < attractors.size(); i++) {
                    String name = attractors.get(i).getName();
                    if (!name.substring(0, 1).equals("C")) {
                        float X = Float.parseFloat(attractors.get(i).getX());
                        float Y = Float.parseFloat(attractors.get(i).getY());
                        float Z = Float.parseFloat(attractors.get(i).getZ());
                        float volume = Float.parseFloat(attractors.get(i).getVolume());

                        Point3D att = new Point3D(X, Y, Z);
                        float R = (float) Math.pow(3 / (4 * Math.PI) * volume, 1 / 3);

                        Point3D[] result = Tools.findLineSphereIntersections(atom0, atom1, att, R);

                        if (result.length > 0) {
                            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                                dataPoints.put(attractors.get(i).getName(), new Point(data, Float.parseFloat(basins.get(i).getN())));
                            } else {
                                dataPoints.put(basins.get(i).getName(), new Point(data, Float.parseFloat(basins.get(i).getN())));
                            }
                        }
                    }
                }
                break;
            case STATISTIC:
                Statistic statistic = data.getStatistic();
                if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                    switch (statisticType) {
                        case 0:
                            dataPoints.put("Basins", new Point(data, statistic.getBasins().size()));
                            break;
                        case 1:
                            dataPoints.put("Cores", new Point(data, statistic.getCores().size()));
                            break;
                        case 2:
                            dataPoints.put("Monosynaptic basins", new Point(data, statistic.getMonosinapticA().size()));
                            break;
                        case 3:
                            dataPoints.put("Disynaptic basins", new Point(data, statistic.getDisinapticA().size()));
                            break;
                        case 4:
                            dataPoints.put("Polysynaptic basins", new Point(data, statistic.getPolisinapticA().size()));
                            break;
                        case 5:
                            dataPoints.put("Asynaptic basins", new Point(data, statistic.getAsynapticA().size()));
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (statisticType) {
                        case 0:
                            dataPoints.put("Basins", new Point(data, statistic.getBasins().size()));
                            break;
                        case 1:
                            dataPoints.put("Cores", new Point(data, statistic.getCores().size()));
                            break;
                        case 2:
                            dataPoints.put("Monosynaptic basins", new Point(data, statistic.getMonosinapticB().size()));
                            break;
                        case 3:
                            dataPoints.put("Disynaptic basins", new Point(data, statistic.getDisinapticB().size()));
                            break;
                        case 4:
                            dataPoints.put("Polysynaptic basins", new Point(data, statistic.getPolisinapticB().size()));
                            break;
                        case 5:
                            dataPoints.put("Asynaptic basins", new Point(data, statistic.getAsynapticB().size()));
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void calculateHausdorff(List<DataIRC> dataIRC) {
        for (int i = 0; i < dataIRC.size() - 1; i++) {
            DataIRC data = dataIRC.get(i);
            List<Attractor> list = data.getAttractors();

            DataIRC nData = dataIRC.get(i + 1);
            List<Attractor> nextList = nData.getAttractors();

            float hausdorff = 0;
            for (Attractor attractor1 : list) {
                float shortest = Float.MAX_VALUE;
                float X1 = Float.parseFloat(attractor1.getX());
                float Y1 = Float.parseFloat(attractor1.getY());
                float Z1 = Float.parseFloat(attractor1.getZ());

                for (Attractor attractor2 : nextList) {
                    float X2 = Float.parseFloat(attractor2.getX());
                    float Y2 = Float.parseFloat(attractor2.getY());
                    float Z2 = Float.parseFloat(attractor2.getZ());
                    float d = (float) Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2) + Math.pow(Z1 - Z2, 2));

                    if (d < shortest) {
                        shortest = d;
                    }
                }

                if (shortest > hausdorff) {
                    hausdorff = shortest;
                }
            }
            
            hausdorff = (float) (Math.round(hausdorff * 1000000.0) / 1000000.0);
            dataPoints.put("Hausdorff distance 1", new Point(data, hausdorff));
        }

        for (int i = 0; i < dataIRC.size() - 1; i++) {
            DataIRC data = dataIRC.get(i + 1);
            List<Attractor> list = data.getAttractors();

            DataIRC nData = dataIRC.get(i);
            List<Attractor> nextList = nData.getAttractors();

            float hausdorff = 0;
            for (Attractor attractor1 : list) {
                float shortest = Float.MAX_VALUE;
                float X1 = Float.parseFloat(attractor1.getX());
                float Y1 = Float.parseFloat(attractor1.getY());
                float Z1 = Float.parseFloat(attractor1.getZ());

                for (Attractor attractor2 : nextList) {
                    float X2 = Float.parseFloat(attractor2.getX());
                    float Y2 = Float.parseFloat(attractor2.getY());
                    float Z2 = Float.parseFloat(attractor2.getZ());
                    float d = (float) Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2) + Math.pow(Z1 - Z2, 2));

                    if (d < shortest) {
                        shortest = d;
                    }
                }

                if (shortest > hausdorff) {
                    hausdorff = shortest;
                }
            }

            hausdorff = (float) (Math.round(hausdorff * 1000000.0) / 1000000.0);
            dataPoints.put("Hausdorff distance 2", new Point(nData, hausdorff));
        }
    }
}
