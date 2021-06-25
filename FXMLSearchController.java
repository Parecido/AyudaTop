package topologyreader;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import topologyreader.data.SearchTable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import topologyreader.data.type.Mode;

public class FXMLSearchController extends FXMLController implements Initializable {
    @FXML private TableColumn table_checkbox, table_filestart, table_fileend, table_name;
    @FXML private TableView tableData;

    private List<SearchTable> searchData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table_checkbox.setCellValueFactory(new PropertyValueFactory<>("CheckBox"));
        table_filestart.setCellValueFactory(new PropertyValueFactory<>("FileStart"));
        table_fileend.setCellValueFactory(new PropertyValueFactory<>("FileEnd"));
        table_name.setCellValueFactory(new PropertyValueFactory<>("Name"));

        tableData.setItems(FXCollections.observableArrayList(searchData));

        if (searchData.isEmpty()) {
            SearchTable data = new SearchTable(dataIRC);
            searchData.add(data);
            tableData.getItems().add(data);
        }
    }

    public void setSearchData(List<SearchTable> searchData) {
        this.searchData = searchData;
    }

    @FXML
    private void startSearch(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLChart.fxml"));
        fxmlLoader.setControllerFactory((Class<?> controllerClass) -> {
            if (controllerClass == FXMLChartController.class) {
                FXMLChartController window = new FXMLChartController();
                window.setMode(Mode.SEARCH);
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
    private void addRow(ActionEvent event) throws Exception {
        SearchTable data = new SearchTable(dataIRC);
        searchData.add(data);
        tableData.getItems().add(data);
    }

    @FXML
    private void removeRow(ActionEvent event) throws Exception {
        for (SearchTable data : searchData) {
            if (data.getCheckBox().isSelected()) {
                tableData.getItems().remove(data);
            }
        }

        searchData.removeIf(prdct -> prdct.getCheckBox().isSelected());
    }
}
