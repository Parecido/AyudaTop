package topologyreader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import topologyreader.data.Attractor;
import topologyreader.data.DataIRC;
import topologyreader.data.Point;
import topologyreader.data.SearchTable;
import topologyreader.data.type.FileType;
import topologyreader.data.type.Mode;
import topologyreader.tools.ResultParser;

public class FXMLChartController extends FXMLController implements Initializable {
    @FXML private NumberAxis xAxis, yAxis;
    @FXML private LineChart<Number, Number> chart;
    
    private Mode mode;
    private List<SearchTable> searchData;
    private List<Attractor> cores;
    private int statisticsType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Integer.parseInt(settings.getProperty("chart.line")) == 1) {
            chart.setVerticalGridLinesVisible(true);
            chart.setHorizontalGridLinesVisible(true);
        }

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.intValue() != object.doubleValue()) {
                    return "";
                }
                return "" + object.intValue();
            }

            @Override
            public Number fromString(String string) {
                Number val = Double.parseDouble(string);
                return val.intValue();
            }
        });
        
        result();
    }
    
    @FXML
    private void startEdit(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLEditor.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLEditorController.class) {
                FXMLEditorController window = new FXMLEditorController();
                window.setSeries(chart.getData());
                return window;
            } else {
                try {
                    return controllerClass.newInstance();
                } catch (IllegalAccessException | InstantiationException exc) {
                    throw new RuntimeException(exc);
                }
            }
        });

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chart data");
        stage.show();
    }
    
    private static ObservableList<Series<Number, Number>> getScatterData(Multimap<String, Point> dataPoints) {
        ObservableList<XYChart.Series<Number, Number>> seriesList = FXCollections.observableArrayList();

        dataPoints.keySet().forEach((key) -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(key);

            dataPoints.get(key).forEach((points) -> {
                DataIRC data = points.getDataIRC();
                int X = points.getIndex();
                float Y = points.getValue();

                series.getData().add(new Data(X, Y, data));
            });

            seriesList.addAll(series);
        });

        return seriesList;
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setSearchData(List<SearchTable> searchData) {
        this.searchData = searchData;
    }

    public void setCores(List<Attractor> cores) {
        this.cores = cores;
    }

    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }

    public void result() {
        int first = dataIRC.get(0).getIndex();
        int second = dataIRC.get(1).getIndex();
        int last = dataIRC.get(dataIRC.size() - 1).getIndex();

        xAxis.setLowerBound(dataIRC.get(0).getFileResult().getType() == FileType.REVERSE ? -first : first);
        xAxis.setUpperBound(last);
        xAxis.setMinorTickVisible(false);

        int step = second - first;
        xAxis.setTickUnit(Math.abs(step));
        xAxis.setLabel("Step");

        switch (mode) {
            case HAUSDORFF:
                yAxis.setLabel("Hausdorff distance");
                break;
            case STATISTICS:
                yAxis.setLabel("Number of basins");
                break; 
            default:
                yAxis.setLabel("Basin population");
                break;
        }

        yAxis.setTickLabelRotation(-90);

        ObservableList<Series<Number, Number>> scatterData = getScatterData(parseData());
        chart.setData(scatterData);

        chart.setLegendVisible(mode != Mode.HAUSDORFF);
        chart.setId(mode != Mode.HAUSDORFF ? "disableLine" : "enableLine");

        scatterData.forEach((series) -> {
            for (int i = 0; i < series.getData().size(); i++) {
                Data<Number, Number> point = series.getData().get(i);

                DataIRC data = (DataIRC) point.getExtraValue();
                Number xValue = point.getXValue();
                String text = xValue.toString();

                if (mode == Mode.HAUSDORFF) {
                    if (i < series.getData().size() - 1) {
                        Number next = series.getData().get(i + 1).getXValue();
                        text = xValue.toString() + "\n" + next.intValue();
                    } else {
                        text = xValue.toString() + "\n" + last;
                    }
                }

                Tooltip.install(point.getNode(), new Tooltip(text));

                point.getNode().setOnMouseClicked(e -> {
                    tableFile.getSelectionModel().select(data);
                    tableFile.scrollTo(data);
                });
            }
        });
    }

    private Multimap<String, Point> parseData() {
        Multimap<String, Point> dataPoints = ArrayListMultimap.create();

        ResultParser result = new ResultParser(mode);
        result.setSettings(settings);
        result.setDataPoints(dataPoints);
        result.setSearchData(searchData);
        result.setCores(cores);
        result.setStatisticsType(statisticsType);

        if (mode == Mode.HAUSDORFF) {
            result.calculateHausdorff(dataIRC);
            return dataPoints;
        }

        dataIRC.forEach((data) -> {
            result.parseData(data);
        });

        return dataPoints;
    }
}
