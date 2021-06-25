package topologyreader;

import com.sun.javafx.charts.Legend;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import topologyreader.data.Editor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import topologyreader.tools.Tools;

public class FXMLEditorController implements Initializable {
    @FXML private Button button_sel;
    @FXML private ComboBox seriesBox;
    @FXML private TableColumn table_checkBox, table_file, table_N;
    @FXML private TableView table;

    private List<Editor> remover = new ArrayList<>();
    private List<Series<Number, Number>> series;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        seriesBox.setItems(FXCollections.observableArrayList(series));
        table_checkBox.setCellValueFactory(new PropertyValueFactory<>("CheckBox"));
        table_file.setCellValueFactory(new PropertyValueFactory<>("File"));
        table_N.setCellValueFactory(new PropertyValueFactory<>("Value"));

        seriesBox.valueProperty().addListener(new ChangeListener<Series>() {
            @Override
            public void changed(ObservableValue ov, Series t, Series t1) {
                remover.clear();

                series.forEach((series) -> {
                    if (series.equals(t1)) {
                        series.getData().forEach((data) -> {
                            remover.add(new Editor(data));
                        });
                    }
                });

                ObservableList<Editor> dataTable = FXCollections.observableArrayList(remover);
                table.setItems(dataTable);

                button_sel.setText("Select all");
            }
        });

        seriesBox.setConverter(new StringConverter<Series>() {
            @Override
            public String toString(Series series) {
                return series.getName();
            }

            @Override
            public Series fromString(String s) {
                return null;
            }
        });

        seriesBox.getSelectionModel().select(0);
    }

    public void setSeries(List<Series<Number, Number>> series) {
        this.series = series;
    }

    @FXML
    private void delete(ActionEvent event) throws Exception {
        series.forEach((series) -> {
            remover.forEach((remover) -> {
                if (remover.getCheckBox().isSelected()) {
                    series.getData().remove(remover.getData());
                    table.getItems().remove(remover);
                }
            });
            if (series.getData().isEmpty()) {
                Legend legend = (Legend) series.getChart().lookup(".chart-legend");
                legend.getItems().removeIf(prdct -> prdct.getText().equals(series.getName()));
            }
        });

        remover.removeIf(prdct -> prdct.getCheckBox().isSelected());
    }

    @FXML
    private void selectAll(ActionEvent event) throws Exception {
        boolean sel = button_sel.getText().equals("Select all");

        remover.forEach((remover) -> {
            remover.getCheckBox().setSelected(sel);
        });

        button_sel.setText(sel ? "Deselect all" : "Select all");
    }

    @FXML
    private void export(ActionEvent event) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT text (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file_o = fileChooser.showSaveDialog(new Stage());

        if (file_o != null) {
            try {
                PrintWriter writer = new PrintWriter(file_o);
                series.forEach((series) -> {
                    if (series.equals(seriesBox.getSelectionModel().getSelectedItem())) {
                        writer.println(series.getName());
                        series.getData().forEach((data) -> {
                            writer.println(data.getXValue() + " " + data.getYValue());
                        });
                    }
                });

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            showSavedMessage(file_o);
        }
    }
    
    private void showSavedMessage(File file) {
        VBox vbox = new VBox();
        Label message = new Label("Finished with TXT file " + file.getAbsolutePath());
        vbox.setPadding(new Insets(25, 50, 10, 10));
        vbox.getChildren().addAll(message);
        
        Tools.showMessage("Chart data", vbox, null);
    }
}
