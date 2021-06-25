package topologyreader;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import topologyreader.data.DataIRC;
import topologyreader.data.DataTable;

import java.util.List;
import java.util.Properties;

public class FXMLController {
    public List<DataIRC> dataIRC;
    public List<DataTable> dataTable;
    public TableView tableTop, tableFile;
    public Label fileLabel;
    public Properties settings;

    public void setDataIRC(List<DataIRC> dataIRC) {
        this.dataIRC = dataIRC;
    }

    public void setDataTable(List<DataTable> dataTable) {
        this.dataTable = dataTable;
    }

    public void setTableTop(TableView tableTop) {
        this.tableTop = tableTop;
    }

    public void setTableFile(TableView tableFile) {
        this.tableFile = tableFile;
    }

    public void setFileLabel(Label fileLabel) {
        this.fileLabel = fileLabel;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }
}
