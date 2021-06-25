package topologyreader;

import com.google.common.io.Files;
import java.awt.Desktop;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import topologyreader.data.DataIRC;
import topologyreader.data.DataTable;
import topologyreader.data.FileResult;
import topologyreader.data.SearchTable;
import topologyreader.data.type.ExportType;
import topologyreader.data.type.FileType;
import topologyreader.data.type.Mode;
import topologyreader.tools.Tools;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import topologyreader.data.Basin;
import topologyreader.data.Statistic;

public class FXMLMainController implements Initializable {
    @FXML private Label fileLabel, dirN, fileN, nameError, populationError;
    @FXML private MenuItem menuExport;
    @FXML private MenuItem menuSearch, menuScan, menuExportAtom, menuExportAtt, menuHausdorff;
    @FXML private TableColumn<DataTable, Number> table_id;
    @FXML private TableColumn table_bas, table_N, table_pab, table_paa, table_pbb, table_s2, table_std;
    @FXML private TableColumn table_att, table_elf;
    @FXML private TableColumn table_index, table_file;
    @FXML private TableView tableTop, tableFile;
    @FXML private TextField filterField;
    
    @FXML private Label labelBasinN, labelCoreN, labelMonosinapticN;
    @FXML private Label labelDisinapticN, labelPolisinapticN, labelAsynapticN, labelDressedN;
    @FXML private Label labelBasinAbove, labelBasinUnder;
    @FXML private Label labelMinELF, labelMaxELF, labelAboveELF, labelUnderELF;
    
    private final List<DataTable> dataTable = new ArrayList<>();
    private final List<DataIRC> dataIRC = new ArrayList<>();
    private final List<SearchTable> searchData = new ArrayList<>();
    private final Properties settings = new Properties();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table_id.setCellValueFactory(new PropertyValueFactory<>("Index"));
        table_bas.setCellValueFactory(new PropertyValueFactory<>("BasinName"));
        table_N.setCellValueFactory(new PropertyValueFactory<>("N"));
        table_pab.setCellValueFactory(new PropertyValueFactory<>("PAB"));
        table_paa.setCellValueFactory(new PropertyValueFactory<>("PAA"));
        table_pbb.setCellValueFactory(new PropertyValueFactory<>("PBB"));
        table_s2.setCellValueFactory(new PropertyValueFactory<>("S2"));
        table_std.setCellValueFactory(new PropertyValueFactory<>("STD"));
        table_att.setCellValueFactory(new PropertyValueFactory<>("AttractorName"));
        table_elf.setCellValueFactory(new PropertyValueFactory<>("Value"));
        
        table_index.setCellValueFactory(new PropertyValueFactory<>("Index"));
        table_file.setCellValueFactory(new PropertyValueFactory<>("FileName"));

        try (InputStream input = new FileInputStream("settings")) {
            settings.load(input);
        } catch (IOException e) {
            settings.setProperty("search.name", "1");       // 0 - Attractor, 1 - Basin
            settings.setProperty("show.cores", "0");        // 0 - false, 1 - true
            settings.setProperty("chart.line", "0");        // 0 - false, 1 - true
            settings.setProperty("export.name", "1");       // 0 - Attractor, 1 - Basin
            settings.setProperty("export.unit", "0");       // 0 - Angstrem, 1 - Bohr
            settings.setProperty("export.printH", "1");     // 0 - false, 1 - true
            settings.setProperty("export.asynaptic", "Kr");
            settings.setProperty("export.monosynaptic", "Ne");
            settings.setProperty("export.disynaptic", "He");
            settings.setProperty("export.polysynaptic", "Xe");
            settings.setProperty("export.hydrogensynaptic", "H");

            try (OutputStream output = new FileOutputStream("settings")) {
                settings.store(output, null);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredData();
        });

        tableFile.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataIRC>() {
            @Override
            public void changed(ObservableValue<? extends DataIRC> observable, DataIRC oldValue, DataIRC newValue) {
                if (newValue != null) {
                    Tools.updateTableTop(newValue, dataTable, tableTop, settings);
                    fileLabel.setText(newValue.getFileName());
                    nameError.setText(newValue.calculateSynError());
                    populationError.setText(newValue.calculatePopError());
                    
                    Statistic statistic = newValue.getStatistic();
                    List<Basin> basins, cores, mBasin, dBasin, pBasin, aBasin, dressed;
                    List<Basin> basinAbove, basinUnder;
                    List<Basin> above, under;
                    
                    basins = statistic.getBasins();
                    cores = statistic.getCores();
                        
                    if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                        mBasin = statistic.getMonosinapticA();
                        dBasin = statistic.getDisinapticA();
                        pBasin = statistic.getPolisinapticA();
                        aBasin = statistic.getAsynapticA();
                        dressed = statistic.getDressedA();
                        
                        basinAbove = statistic.getBasinAboveA();
                        basinUnder = statistic.getBasinUnderA();
                        
                        above = statistic.getAttractorAboveA();
                        under = statistic.getAttractorUnderA();
                    } else {
                        mBasin = statistic.getMonosinapticB();
                        dBasin = statistic.getDisinapticB();
                        pBasin = statistic.getPolisinapticB();
                        aBasin = statistic.getAsynapticB();
                        dressed = statistic.getDressedB();
      
                        basinAbove = statistic.getBasinAboveB();
                        basinUnder = statistic.getBasinUnderB();

                        above = statistic.getAttractorAboveB();
                        under = statistic.getAttractorUnderB();
                    }
                    
                    labelBasinN.setText(Integer.toString(basins.size()));
                    labelCoreN.setText(Integer.toString(cores.size()));
                    labelMonosinapticN.setText(Integer.toString(mBasin.size()));
                    labelDisinapticN.setText(Integer.toString(dBasin.size()));
                    labelPolisinapticN.setText(Integer.toString(pBasin.size()));
                    labelAsynapticN.setText(Integer.toString(aBasin.size()));
                    labelDressedN.setText(Integer.toString(dressed.size()));
                    
                    labelBasinAbove.setText(Integer.toString(basinAbove.size()));
                    labelBasinUnder.setText(Integer.toString(basinUnder.size()));
                    
                    labelMinELF.setText(Float.toString(statistic.getMinELF()));
                    labelMaxELF.setText(Float.toString(statistic.getMaxELF()));
                    labelAboveELF.setText(Integer.toString(above.size()));
                    labelUnderELF.setText(Integer.toString(under.size()));
                }
            }
        });
    }

    @FXML
    private void startExport(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLExport.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLExportController.class) {
                FXMLExportController window = new FXMLExportController();
                window.setDataIRC(dataIRC);
                window.setDataTable(dataTable);
                window.setTableTop(tableTop);
                window.setTableFile(tableFile);
                window.setFileLabel(fileLabel);
                window.setSettings(settings);
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
        stage.setTitle("Export data");
        stage.show();
    }

    @FXML
    private void startSettings(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSettings.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLSettingsController.class) {
                FXMLSettingsController window = new FXMLSettingsController();
                window.setDataIRC(dataIRC);
                window.setDataTable(dataTable);
                window.setTableTop(tableTop);
                window.setTableFile(tableFile);
                window.setFileLabel(fileLabel);
                window.setSettings(settings);
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
        stage.setTitle("Settings");
        stage.show();
    }

    @FXML
    private void startSearch(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSearch.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLSearchController.class) {
                FXMLSearchController window = new FXMLSearchController();
                window.setDataIRC(dataIRC);
                window.setDataTable(dataTable);
                window.setTableTop(tableTop);
                window.setTableFile(tableFile);
                window.setFileLabel(fileLabel);
                window.setSettings(settings);
                window.setSearchData(searchData);
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
        stage.setTitle("Search");
        stage.show();
    }

    @FXML
    private void selectFile(ActionEvent event) throws Exception {
        List<FileResult> files = new ArrayList<>();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select *.top file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TOP (*.top)", "*.top"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            searchData.clear();
            dataIRC.clear();
            dataTable.clear();
            tableTop.getItems().clear();
            tableFile.getItems().clear();
            fileLabel.setText("---");
            nameError.setText("---");
            populationError.setText("---");

            files.add(new FileResult(FileType.FORWARD, file.toPath()));

            menuExport.setDisable(false);
            menuSearch.setDisable(true);
            menuScan.setDisable(true);
            menuExportAtom.setDisable(false);
            menuExportAtt.setDisable(false);
            menuHausdorff.setDisable(true);
            dirN.setText(Integer.toString(files.size()));

            if (!dataIRC.isEmpty()) {
                fileLabel.setText(file.getName());
            }

            parseFiles(files);
        }
    }

    @FXML
    private void selectDirectory(ActionEvent event) throws Exception {
        List<FileResult> files = new ArrayList<>();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select folder with *.top files");

        File selectedDirectory = chooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            searchData.clear();
            dataIRC.clear();
            dataTable.clear();
            tableTop.getItems().clear();
            tableFile.getItems().clear();
            fileLabel.setText("---");
            nameError.setText("---");
            populationError.setText("---");

            Arrays.asList(selectedDirectory.listFiles())
                    .stream()
                    .filter(prdct -> !prdct.isHidden())
                    .filter(prdct -> Files.getFileExtension(prdct.getAbsolutePath()).equalsIgnoreCase("top"))
                    .sorted((File o1, File o2) -> {
                        int n1 = Tools.extractNumber(o1.getName());
                        int n2 = Tools.extractNumber(o2.getName());
                        return n1 - n2;
                    })
                    .forEach(file -> {
                        files.add(new FileResult(FileType.FORWARD, file.toPath()));
                    });

            if (files.size() > 0) {
                menuExport.setDisable(false);
                menuSearch.setDisable(false);
                menuScan.setDisable(false);
                menuExportAtom.setDisable(false);
                menuExportAtt.setDisable(false);
                menuHausdorff.setDisable(false);
                dirN.setText(Integer.toString(files.size()));

                parseFiles(files);
            }
        }
    }

    @FXML
    private void selectDirectoryFull(ActionEvent event) throws Exception {
        List<FileResult> files = new ArrayList<>();

        DirectoryChooser chooser1 = new DirectoryChooser();
        chooser1.setTitle("Select folder with *.top files for reverse direction");

        File selectedDirectory1 = chooser1.showDialog(new Stage());
        if (selectedDirectory1 != null) {
            searchData.clear();
            dataIRC.clear();
            dataTable.clear();
            tableTop.getItems().clear();
            tableFile.getItems().clear();
            fileLabel.setText("---");
            nameError.setText("---");
            populationError.setText("---");

            Arrays.asList(selectedDirectory1.listFiles())
                    .stream()
                    .filter(prdct -> !prdct.isHidden())
                    .filter(prdct -> Files.getFileExtension(prdct.getAbsolutePath()).equalsIgnoreCase("top"))
                    .sorted((File o1, File o2) -> {
                        int n1 = Tools.extractNumber(o1.getName());
                        int n2 = Tools.extractNumber(o2.getName());
                        return n2 - n1;
                    })
                    .forEach(file -> {
                        files.add(new FileResult(FileType.REVERSE, file.toPath()));
                    });
        }

        DirectoryChooser chooser2 = new DirectoryChooser();
        chooser2.setTitle("Select folder with *.top files for forward direction");

        File selectedDirectory2 = chooser2.showDialog(new Stage());
        if (selectedDirectory2 != null) {
            if (selectedDirectory1 == null) {
                searchData.clear();
                dataIRC.clear();
                dataTable.clear();
                tableTop.getItems().clear();
                tableFile.getItems().clear();
                fileLabel.setText("---");
                nameError.setText("---");
                populationError.setText("---");
            }

            Arrays.asList(selectedDirectory2.listFiles())
                    .stream()
                    .filter(prdct -> !prdct.isHidden())
                    .filter(prdct -> Files.getFileExtension(prdct.getAbsolutePath()).equalsIgnoreCase("top"))
                    .sorted((File o1, File o2) -> {
                        int n1 = Tools.extractNumber(o1.getName());
                        int n2 = Tools.extractNumber(o2.getName());
                        return n1 - n2;
                    })
                    .forEach(file -> {
                        files.add(new FileResult(FileType.FORWARD, file.toPath()));
                    });
        }

        if (files.size() > 0) {
            menuExport.setDisable(false);
            menuSearch.setDisable(false);
            menuScan.setDisable(false);
            menuExportAtom.setDisable(false);
            menuExportAtt.setDisable(false);
            menuHausdorff.setDisable(false);
            dirN.setText(Integer.toString(files.size()));

            parseFiles(files);
        }
    }

    @FXML
    private void exportAttractors(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLExporter.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLExporterController.class) {
                FXMLExporterController window = new FXMLExporterController();
                window.setDataIRC(dataIRC);
                window.setSettings(settings);
                window.setType(ExportType.ATTRACTORS);
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
        stage.setTitle("Export attractors");
        stage.show();
    }

    @FXML
    private void exportAtoms(ActionEvent event) throws Exception {
        String title = "Export atoms";
        String message = "There is no information about the position of the atoms. The AIM calculations have not been performed.";

        if (Tools.emptyCores(dataIRC, title, message)) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLExporter.fxml"));
            fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
                if (controllerClass == FXMLExporterController.class) {
                    FXMLExporterController window = new FXMLExporterController();
                    window.setDataIRC(dataIRC);
                    window.setSettings(settings);
                    window.setType(ExportType.ATOMS);
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
            stage.setTitle("Export atoms");
            stage.show();
        }
    }

    @FXML
    private void startScan(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLScan.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLScanController.class) {
                FXMLScanController window = new FXMLScanController();
                window.setDataIRC(dataIRC);
                window.setDataTable(dataTable);
                window.setTableTop(tableTop);
                window.setTableFile(tableFile);
                window.setFileLabel(fileLabel);
                window.setSettings(settings);
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
    }

    @FXML
    private void calculateHausdorff(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLChart.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLChartController.class) {
                FXMLChartController window = new FXMLChartController();
                window.setMode(Mode.HAUSDORFF);
                window.setDataIRC(dataIRC);
                window.setDataTable(dataTable);
                window.setTableTop(tableTop);
                window.setTableFile(tableFile);
                window.setFileLabel(fileLabel);
                window.setSettings(settings);
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
        stage.setTitle("Hausdorff distance");
        stage.show();
    }

    @FXML
    private void showVersion(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Version");
        alert.setHeaderText(null);
        
        VBox vbox = new VBox();
        Label version = new Label("AyudaTop 2.0.0\n");
        Hyperlink author = new Hyperlink("Author: Michał Michalski");
        Label contact = new Label("michalski.michal@outlook.com");
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25, 50, 10, 10));
        vbox.getChildren().addAll(version, author, contact);
        
        author.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL("https://www.researchgate.net/profile/Michal_Michalski2").toURI());
                } catch (IOException | URISyntaxException x) {
                    x.printStackTrace();
                }
            }
        });
        
        alert.getDialogPane().contentProperty().set(vbox);
        alert.getDialogPane().getStylesheets().add("/topologyreader/style/bootstrap3.css");
        alert.showAndWait();
    }

    private void updateFilteredData() {
        tableTop.getItems().clear();
        dataTable.forEach((data) -> {
            if (matchesFilter(data)) {
                tableTop.getItems().add(data);
            }
        });
    }

    private boolean matchesFilter(DataTable data) {
        String filterString = filterField.getText();
        if (filterString == null || filterString.isEmpty()) {
            return true;
        }

        String lowerCaseFilterString = filterString.toLowerCase();
        String basinName = data.getBasinName();

        return basinName.toLowerCase().contains(lowerCaseFilterString);
    }

    private void parseFiles(List<FileResult> files) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLLoading.fxml"));
            fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
                if (controllerClass == FXMLLoadingController.class) {
                    FXMLLoadingController window = new FXMLLoadingController();
                    window.setFiles(files);
                    window.setDataIRC(dataIRC);
                    window.setFileN(fileN);
                    window.setTableFile(tableFile);
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
            stage.setTitle("Loading");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showBasinTable(List<Basin> basins, String title) {
        String nameText = Integer.parseInt(settings.getProperty("search.name")) == 0 ? "Attractor" : "Basin";

        TableColumn name = new TableColumn(nameText);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn N = new TableColumn("N");
        N.setCellValueFactory(new PropertyValueFactory<>("N"));

        List<TableColumn> columns = new ArrayList<>(Arrays.asList(name, N));
        ObservableList<Basin> items = FXCollections.observableArrayList(basins);
        Tools.showTable(4.5f, 1.5f, columns, items, title);
    }
    
    private void showAttractorTable(List<Basin> attractors, String title) {
        String nameText = Integer.parseInt(settings.getProperty("search.name")) == 0 ? "Attractor" : "Basin";

        TableColumn name = new TableColumn(nameText);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn elf = new TableColumn("ELF");
        elf.setCellValueFactory(new PropertyValueFactory<>("Volume"));

        List<TableColumn> columns = new ArrayList<>(Arrays.asList(name, elf));
        ObservableList<Basin> items = FXCollections.observableArrayList(attractors);
        Tools.showTable(4.5f, 1.5f, columns, items, title);
    }
    
    @FXML
    private void showBasins(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Basins [" + file.getName() + "]";

            List<Basin> basins = statistic.getBasins();
            showBasinTable(basins, title);
        }
    }

    @FXML
    private void showCores(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Cores [" + file.getName() + "]";

            List<Basin> cores = statistic.getCores();
            showBasinTable(cores, title);
        }
    }

    @FXML
    private void showMBasins(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Monosynaptic basins [" + file.getName() + "]";

            List<Basin> mBasin;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                mBasin = statistic.getMonosinapticA();
            } else {
                mBasin = statistic.getMonosinapticB();
            }

            showBasinTable(mBasin, title);
        }
    }

    @FXML
    private void showDBasins(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Disynaptic basins [" + file.getName() + "]";

            List<Basin> dBasin;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                dBasin = statistic.getDisinapticA();
            } else {
                dBasin = statistic.getDisinapticB();
            }

            showBasinTable(dBasin, title);
        }
    }

    @FXML
    private void showPBasins(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Polysynaptic basins [" + file.getName() + "]";

            List<Basin> pBasin;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                pBasin = statistic.getPolisinapticA();
            } else {
                pBasin = statistic.getPolisinapticB();
            }

            showBasinTable(pBasin, title);
        }
    }

    @FXML
    private void showABasins(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Asynaptic basins [" + file.getName() + "]";

            List<Basin> aBasin;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                aBasin = statistic.getAsynapticA();
            } else {
                aBasin = statistic.getAsynapticB();
            }

            showBasinTable(aBasin, title);
        }
    }
    
    @FXML
    private void showBasinAbove(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Basins with population above 1e [" + file.getName() + "]";

            List<Basin> basinAbove;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                basinAbove = statistic.getBasinAboveA();
            } else {
                basinAbove = statistic.getBasinAboveB();
            }

            showBasinTable(basinAbove, title);
        }
    }

    @FXML
    private void showBasinUnder(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Basins with population under 1e [" + file.getName() + "]";

            List<Basin> basinUnder;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                basinUnder = statistic.getBasinUnderA();
            } else {
                basinUnder = statistic.getBasinUnderB();
            }

            showBasinTable(basinUnder, title);
        }
    }
    
    @FXML
    private void showAttractorsMin(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "The lowest attractor value [" + file.getName() + "]";

            List<Basin> min;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                min = statistic.getMinListA();
            } else {
                min = statistic.getMinListB();
            }

            showAttractorTable(min, title);
        }
    }

    @FXML
    private void showAttractorsMax(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "The highest attractor value [" + file.getName() + "]";

            List<Basin> max;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                max = statistic.getMaxListA();
            } else {
                max = statistic.getMaxListB();
            }

            showAttractorTable(max, title);
        }
    }

    @FXML
    private void showAttractorsAbove(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Attractors above 0.5 ELF [" + file.getName() + "]";

            List<Basin> above;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                above = statistic.getAttractorAboveA();
            } else {
                above = statistic.getAttractorAboveB();
            }

            showAttractorTable(above, title);
        }
    }

    @FXML
    private void showAttractorsUnder(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Attractors under 0.5 ELF [" + file.getName() + "]";

            List<Basin> under;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                under = statistic.getAttractorUnderA();
            } else {
                under = statistic.getAttractorUnderB();
            }

            showAttractorTable(under, title);
        }
    }
    
    @FXML
    private void showDressed(ActionEvent event) throws Exception {
        DataIRC data = (DataIRC) tableFile.getSelectionModel().getSelectedItem();
        if (data != null) {
            FileResult file = data.getFileResult();
            Statistic statistic = data.getStatistic();
            String title = "Dressed protons [" + file.getName() + "]";

            List<Basin> dressed;
            if (Integer.parseInt(settings.getProperty("search.name")) == 0) {
                dressed = statistic.getDressedA();
            } else {
                dressed = statistic.getDressedB();
            }

            showBasinTable(dressed, title);
        }
    }
}
