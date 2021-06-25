package topologyreader;

import com.google.common.io.Files;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import topologyreader.data.Attractor;
import topologyreader.data.Basin;
import topologyreader.data.DataIRC;
import topologyreader.data.type.ExportType;
import topologyreader.tools.Tools;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FXMLExporterController extends FXMLController implements Initializable {
    @FXML private ComboBox filesBox;

    private ExportType type;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filesBox.setItems(FXCollections.observableArrayList(dataIRC));
        filesBox.getSelectionModel().selectFirst();
        filesBox.setConverter(new StringConverter<DataIRC>() {
            @Override
            public String toString(DataIRC data) {
                return data.getFileName();
            }

            @Override
            public DataIRC fromString(String s) {
                return null;
            }
        });
    }

    public void setType(ExportType type) {
        this.type = type;
    }

    @FXML
    private void exportSingle(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) filesBox.getSelectionModel().getSelectedItem();
        String fileName = Files.getNameWithoutExtension(data.getFileName());

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XYZ files (*.xyz)", "*.xyz");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(fileName);

        File file_o = fileChooser.showSaveDialog(new Stage());
        if (file_o != null) {
            PrintWriter writer = new PrintWriter(file_o);

            if (type == ExportType.ATOMS) {
                generateAtomFile(writer, data);
            } else {
                generateAttractorFile(writer, data);
            }

            writer.close();
            showSavedMessage("Finished with coordinate file " + file_o.getAbsolutePath());
        }
    }

    @FXML
    private void exportMultipleSeparate(ActionEvent event) throws Exception {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select folder");

        File selectedDirectory = chooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            for (DataIRC data : dataIRC) {
                String fileName = Files.getNameWithoutExtension(data.getFileName());
                String location = selectedDirectory + "/" + fileName + ".xyz";
                PrintWriter writer = new PrintWriter(location);

                if (type == ExportType.ATOMS) {
                    generateAtomFile(writer, data);
                } else {
                    generateAttractorFile(writer, data);
                }

                writer.close();
            }

            showSavedMessage("Finished with coordinate files in " + selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void exportMultipleOne(ActionEvent event) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XYZ files (*.xyz)", "*.xyz");
        fileChooser.getExtensionFilters().add(extFilter);

        File file_o = fileChooser.showSaveDialog(new Stage());
        if (file_o != null) {
            PrintWriter writer = new PrintWriter(file_o);

            for (DataIRC data : dataIRC) {
                if (type == ExportType.ATOMS) {
                    generateAtomFile(writer, data);
                } else {
                    generateAttractorFile(writer, data);
                }
            }

            writer.close();
            showSavedMessage("Finished with coordinate file " + file_o.getAbsolutePath());
        }
    }

    private PrintWriter generateAttractorFile(PrintWriter writer, DataIRC data) {
        int attractorsN = data.getAttractors().size();
        if (Integer.parseInt(settings.getProperty("export.printH")) == 0) {
            attractorsN -= data.getHydrogens().size();
        }

        writer.println(attractorsN);
        writer.println(data.getIndex());

        List<Attractor> attractors = data.getAttractors();
        List<Basin> basins = data.getBasins();

        for (int i = 0; i < attractors.size(); i++) {
            Attractor attractor = attractors.get(i);
            Basin basin = basins.get(i);

            String name = basin.getName();
            if (Integer.parseInt(settings.getProperty("export.name")) == 0) {
                name = attractor.getName();
            }

            float X = Float.parseFloat(attractor.getX());
            float Y = Float.parseFloat(attractor.getY());
            float Z = Float.parseFloat(attractor.getZ());
            if (Integer.parseInt(settings.getProperty("export.unit")) == 0) {
                X *= 0.529177249f;
                Y *= 0.529177249f;
                Z *= 0.529177249f;
            }

            if (!attractor.getType().equals("2")) {
                if (name.startsWith("V(")) {
                    if (name.contains("Asyn")) {
                        name = settings.getProperty("export.asynaptic");
                    } else {
                        switch (name.split(",").length) {
                            case 1:
                                name = settings.getProperty("export.monosynaptic");
                                break;
                            case 2:
                                name = settings.getProperty("export.disynaptic");
                                break;
                            default:
                                name = settings.getProperty("export.polysynaptic");
                                break;
                        }
                    }
                } else {
                    name = getAtomName(name);
                }
            } else {
                name = settings.getProperty("export.hydrogensynaptic");
            }

            if (!attractor.getType().equals("2")) {
                writer.println(name + " " + X + " " + Y + " " + Z);
            } else {
                if (Integer.parseInt(settings.getProperty("export.printH")) == 1) {
                    writer.println(name + " " + X + " " + Y + " " + Z);
                }
            }
        }

        return writer;
    }

    private PrintWriter generateAtomFile(PrintWriter writer, DataIRC data) {
        writer.println(data.getAtoms().size());
        writer.println(data.getIndex());

        data.getAtoms().forEach((atom) -> {
            String name = atom.getName();

            float X = Float.parseFloat(atom.getX());
            float Y = Float.parseFloat(atom.getY());
            float Z = Float.parseFloat(atom.getZ());
            if (Integer.parseInt(settings.getProperty("export.unit")) == 0) {
                X *= 0.529177249f;
                Y *= 0.529177249f;
                Z *= 0.529177249f;
            }

            writer.println(name + " " + X + " " + Y + " " + Z);
        });

        return writer;
    }

    private String getAtomName(String asdf) {
        String name = asdf.replaceAll("[0-9]", "");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(name);

        while (m.find()) {
            return m.group(1);
        }

        return null;
    }
    
    private void showSavedMessage(String text) {
        String title = type == ExportType.ATOMS ? "Export atoms" : "Export attractors";
        
        VBox vbox = new VBox();
        Label message = new Label(text);
        vbox.setPadding(new Insets(25, 50, 10, 10));
        vbox.getChildren().addAll(message);
        
        Tools.showMessage(title, vbox, null);
    }
}
