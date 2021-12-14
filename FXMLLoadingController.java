package topologyreader;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import topologyreader.data.*;
import topologyreader.data.type.FileType;
import topologyreader.tools.FileParser;
import topologyreader.tools.Tools;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLLoadingController implements Initializable {
    @FXML private ProgressBar progressBar;
    @FXML private Label fileName;

    private List<FileResult> files;
    private List<DataIRC> dataIRC;
    private Label fileN;
    private TableView tableFile;

    public void setFiles(List<FileResult> files) {
        this.files = files;
    }

    public void setDataIRC(List<DataIRC> dataIRC) {
        this.dataIRC = dataIRC;
    }

    public void setFileN(Label fileN) {
        this.fileN = fileN;
    }

    public void setTableFile(TableView tableFile) {
        this.tableFile = tableFile;
    }

    private int getFilesR() {
        return (int) files.stream().filter(predicate ->
                predicate.getType() == FileType.REVERSE).count();
    }

    private int getFilesF() {
        return (int) files.stream().filter(predicate ->
                predicate.getType() == FileType.FORWARD).count();
    }

    private boolean compareCores(List<Attractor> attractors, List<Basin> basins) {
        int coresA = (int) attractors.stream().filter(predicate ->
                predicate.isCore()).count();

        int coresB = (int) basins.stream().filter(predicate ->
                predicate.isCore()).count();

        return coresA == coresB;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Task<Object> work = new Task<Object>() {
            @Override
            public Object call() throws Exception {
                if (dataIRC.isEmpty()) {
                    for (int i = 0; i < files.size(); i++) {
                        FileResult file = files.get(i);

                        updateProgress(i, files.size());
                        updateMessage(file.getName());

                        FileParser reader = new FileParser();
                        reader.parseFile(file);

                        List<Attractor> attractors = reader.getAttractors();
                        List<Basin> basins = reader.getBasins();
                        List<Atom> atoms = reader.getAtoms();

                        if (!attractors.isEmpty() && !basins.isEmpty()) {
                            if (attractors.size() == basins.size()) {
                                if (compareCores(attractors, basins)) {
                                    int lastIndex = file.getType() == FileType.REVERSE ? getFilesR() : getFilesF();
                                    int firstIndex = file.getType() == FileType.REVERSE ? -lastIndex : 0;
                                    int removeIndex = file.getType() == FileType.REVERSE ? 0 : getFilesR();
                                    int index = firstIndex + (i - removeIndex);
                                    file.setIndex(index);
                                    
                                    Statistics statistics = reader.getStatistics();
                                    float intDensity = reader.getIntDensity();
                                    float sumPopulation = reader.getSumPopulation();

                                    dataIRC.add(new DataIRC(file, attractors, basins, atoms, statistics, intDensity, sumPopulation));
                                } else {
                                    Platform.runLater(() -> {
                                        String title = "Loading error";
                                        String message = "The number of core attractors and basins is not the same in file " + file.getName();
                                        Tools.showMessage(title, null, message);
                                    });
                                }
                            } else {
                                Platform.runLater(() -> {
                                    String title = "Loading error";
                                    String message = "The number of attractors and basins is not the same in file " + file.getName();
                                    Tools.showMessage(title, null, message);
                                });
                            }
                        } else {
                            Platform.runLater(() -> {
                                String title = "Loading error";
                                String message = "Missing attractors or basins data in file " + file.getName();
                                Tools.showMessage(title, null, message);
                            });
                        }
                    }
                }

                return null;
            }
        };

        work.setOnSucceeded(e -> {
            fileN.setText(Integer.toString(dataIRC.size()));
            files.clear();

            tableFile.setItems(FXCollections.observableArrayList(dataIRC));
            tableFile.getSelectionModel().selectFirst();

            Stage stage = (Stage) progressBar.getScene().getWindow();
            stage.close();
        });

        progressBar.progressProperty().unbind();
        progressBar.visibleProperty().bind(work.runningProperty());
        progressBar.progressProperty().bind(work.progressProperty());

        fileName.textProperty().unbind();
        fileName.textProperty().bind(work.messageProperty());

        Thread th = new Thread(work);
        th.setDaemon(true);
        th.start();
    }
}
