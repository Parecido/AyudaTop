package topologyreader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import topologyreader.data.DataIRC;
import topologyreader.tools.Tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLSettingsController extends FXMLController implements Initializable {
    @FXML private ChoiceBox overallName;
    @FXML private ChoiceBox showCores;
    @FXML private ChoiceBox overallLine;

    @FXML private ChoiceBox exportName;
    @FXML private ChoiceBox exportUnit;
    @FXML private ChoiceBox exportPrintH;
    @FXML private TextField exportAsynaptic;
    @FXML private TextField exportMonosynaptic;
    @FXML private TextField exportDisynaptic;
    @FXML private TextField exportPolysynaptic;
    @FXML private TextField exportHydrogensynaptic;

    @FXML private Tab tabOverall;
    @FXML private Tab tabExport;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int index1 = Integer.parseInt(settings.getProperty("search.name"));
        overallName.getSelectionModel().select(index1);

        int index2 = Integer.parseInt(settings.getProperty("show.cores"));
        showCores.getSelectionModel().select(index2);

        int index3 = Integer.parseInt(settings.getProperty("chart.line"));
        overallLine.getSelectionModel().select(index3);

        int index4 = Integer.parseInt(settings.getProperty("export.name"));
        exportName.getSelectionModel().select(index4);

        int index5 = Integer.parseInt(settings.getProperty("export.unit"));
        exportUnit.getSelectionModel().select(index5);

        int index6 = Integer.parseInt(settings.getProperty("export.printH"));
        exportPrintH.getSelectionModel().select(index6);

        boolean enabledH = index6 == 0;
        exportHydrogensynaptic.setDisable(enabledH);

        String text7 = settings.getProperty("export.asynaptic");
        exportAsynaptic.setText(text7);

        String text8 = settings.getProperty("export.monosynaptic");
        exportMonosynaptic.setText(text8);

        String text9 = settings.getProperty("export.disynaptic");
        exportDisynaptic.setText(text9);

        String text10 = settings.getProperty("export.polysynaptic");
        exportPolysynaptic.setText(text10);

        String text11 = settings.getProperty("export.hydrogensynaptic");
        exportHydrogensynaptic.setText(text11);

        exportPrintH.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                boolean enabledH = number2.intValue() == 0;
                exportHydrogensynaptic.setDisable(enabledH);
            }
        });
    }

    @FXML
    private void save(ActionEvent event) throws Exception {
        int index1 = overallName.getSelectionModel().getSelectedIndex();
        settings.setProperty("search.name", Integer.toString(index1));

        int index2 = showCores.getSelectionModel().getSelectedIndex();
        settings.setProperty("show.cores", Integer.toString(index2));

        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            Tools.updateTableTop(data, dataTable, tableTop, settings);
        }

        int index3 = overallLine.getSelectionModel().getSelectedIndex();
        settings.setProperty("chart.line", Integer.toString(index3));

        /*boolean enabledLine = index3 == 1;
        chart.setVerticalGridLinesVisible(enabledLine);
        chart.setHorizontalGridLinesVisible(enabledLine);*/

        int index4 = exportName.getSelectionModel().getSelectedIndex();
        settings.setProperty("export.name", Integer.toString(index4));

        int index5 = exportUnit.getSelectionModel().getSelectedIndex();
        settings.setProperty("export.unit", Integer.toString(index5));

        int index6 = exportPrintH.getSelectionModel().getSelectedIndex();
        settings.setProperty("export.printH", Integer.toString(index6));

        String text7 = exportAsynaptic.getText();
        settings.setProperty("export.asynaptic", text7);

        String text8 = exportMonosynaptic.getText();
        settings.setProperty("export.monosynaptic", text8);

        String text9 = exportDisynaptic.getText();
        settings.setProperty("export.disynaptic", text9);

        String text10 = exportPolysynaptic.getText();
        settings.setProperty("export.polysynaptic", text10);

        String text11 = exportHydrogensynaptic.getText();
        settings.setProperty("export.hydrogensynaptic", text11);

        try (OutputStream output = new FileOutputStream("settings")) {
            settings.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }

        Stage stage = (Stage) overallName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void restoreDefault(ActionEvent event) throws Exception {
        if (tabOverall.isSelected()) {
            overallName.getSelectionModel().select(1);
            showCores.getSelectionModel().select(0);
            overallLine.getSelectionModel().select(0);
        } else if (tabExport.isSelected()) {
            exportName.getSelectionModel().select(1);
            exportUnit.getSelectionModel().select(0);
            exportPrintH.getSelectionModel().select(1);
            exportAsynaptic.setText("Kr");
            exportMonosynaptic.setText("Ne");
            exportDisynaptic.setText("He");
            exportPolysynaptic.setText("Xe");
            exportHydrogensynaptic.setText("H");
        }
    }
}
