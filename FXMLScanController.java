package topologyreader;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import topologyreader.data.Attractor;
import topologyreader.data.type.Mode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;

public class FXMLScanController extends FXMLController implements Initializable {
    @FXML private ComboBox core1, core2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Attractor> cores = dataIRC.get(0).getCores();

        core1.setItems(FXCollections.observableArrayList(cores));
        core2.setItems(FXCollections.observableArrayList(cores));

        core1.getSelectionModel().selectFirst();
        core2.getSelectionModel().selectFirst();

        core1.setConverter(new StringConverter<Attractor>() {
            @Override
            public String toString(Attractor attractor) {
                return attractor.getName();
            }

            @Override
            public Attractor fromString(String s) {
                return null;
            }
        });

        core2.setConverter(new StringConverter<Attractor>() {
            @Override
            public String toString(Attractor attractor) {
                return attractor.getName();
            }

            @Override
            public Attractor fromString(String s) {
                return null;
            }
        });
    }

    @FXML
    private void startSearch(ActionEvent event) throws Exception {
        Attractor selCore1 = (Attractor) core1.getSelectionModel().getSelectedItem();
        Attractor selCore2 = (Attractor) core2.getSelectionModel().getSelectedItem();

        if (selCore1 != selCore2) {
            List<Attractor> selectedCores = new ArrayList<>();
            selectedCores.add(selCore1);
            selectedCores.add(selCore2);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLChart.fxml"));
            fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
                if (controllerClass == FXMLChartController.class) {
                    FXMLChartController window = new FXMLChartController();
                    window.setMode(Mode.SCAN);
                    window.setDataIRC(dataIRC);
                    window.setDataTable(dataTable);
                    window.setTableTop(tableTop);
                    window.setTableFile(tableFile);
                    window.setFileLabel(fileLabel);
                    window.setSettings(settings);
                    window.setCores(selectedCores);
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
            stage.setTitle("Scan");
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Scan");
            alert.setHeaderText(null);
            alert.setContentText("You cannot scan two identical core basins!");
            alert.getDialogPane().getStylesheets().add("/topologyreader/style/bootstrap3.css");
            alert.showAndWait();
        }
    }
}
