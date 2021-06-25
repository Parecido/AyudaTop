package topologyreader;

import com.google.common.io.Files;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import topologyreader.data.Attractor;
import topologyreader.data.Basin;
import topologyreader.data.DataIRC;
import topologyreader.tools.Tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FXMLExportController extends FXMLController implements Initializable {
    @FXML private ComboBox fileBox;
    @FXML private ChoiceBox delimiter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prepareFileBox(fileBox);
        delimiter.getSelectionModel().selectFirst();
    }

    private void prepareFileBox(ComboBox box) {
        box.setItems(FXCollections.observableArrayList(dataIRC));
        box.setConverter(new StringConverter<DataIRC>() {
            @Override
            public String toString(DataIRC data) {
                return data.getFileName();
            }

            @Override
            public DataIRC fromString(String s) {
                return null;
            }
        });

        box.getSelectionModel().select(0);
    }

    @FXML
    private void startExport(ActionEvent event) throws Exception {
        DataIRC dataIRC = (DataIRC) fileBox.getSelectionModel().getSelectedItem();
        String fileName = Files.getNameWithoutExtension(dataIRC.getFileName());
        String data = generateFile();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(fileName);
        File file_o = fileChooser.showSaveDialog(new Stage());

        if (file_o != null) {
            try {
                PrintWriter writer = new PrintWriter(file_o);
                writer.print(data);
                writer.close();
                showSavedMessage(file_o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateFile() {
        String del = "";
        String firstLine = "";
        String dataLine = "";

        if (delimiter.getSelectionModel().getSelectedIndex() == 0) {
            del = ",";
        } else if (delimiter.getSelectionModel().getSelectedIndex() == 1) {
            del = ";";
        }

        firstLine += "Att name" + del;
        firstLine += "Bas name" + del;
        firstLine += "ELF" + del;
        firstLine += "X" + del;
        firstLine += "Y" + del;
        firstLine += "Z" + del;
        firstLine += "N" + del;
        firstLine += "PAB" + del;
        firstLine += "PAA" + del;
        firstLine += "PBB" + del;
        firstLine += "S2" + del;
        firstLine += "STD" + "\n";

        dataLine += firstLine;

        DataIRC data = (DataIRC) fileBox.getSelectionModel().getSelectedItem();
        List<Attractor> attractors = data.getAttractors();
        List<Basin> basins = data.getBasins();

        for (int i = 0; i < attractors.size(); i++) {
            dataLine += "\"" + attractors.get(i).getName() + "\"" + del;
            dataLine += "\"" + basins.get(i).getName() + "\"" + del;
            dataLine += attractors.get(i).getValue() + del;
            dataLine += attractors.get(i).getX() + del;
            dataLine += attractors.get(i).getY() + del;
            dataLine += attractors.get(i).getZ() + del;
            dataLine += basins.get(i).getN() + del;
            dataLine += basins.get(i).getPAB() + del;
            dataLine += basins.get(i).getPAA() + del;
            dataLine += basins.get(i).getPBB() + del;
            dataLine += basins.get(i).getS2() + del;
            dataLine += basins.get(i).getSTD() + "\n";
        }

        return dataLine;
    }

    private void showSavedMessage(File file) {
        VBox vbox = new VBox();
        Label message = new Label("Finished with CSV file " + file.getAbsolutePath());
        vbox.setPadding(new Insets(25, 50, 10, 10));
        vbox.getChildren().addAll(message);
        
        Tools.showMessage("Export data", vbox, null);
    }
}
