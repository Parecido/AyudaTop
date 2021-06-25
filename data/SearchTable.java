package topologyreader.data;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class SearchTable {
    private final CheckBox checkbox;
    private final ComboBox fileStart;
    private final ComboBox fileEnd;
    private final TextField name;

    public SearchTable(List<DataIRC> dataIRC) {
        this.checkbox = new CheckBox();
        this.fileStart = new ComboBox();
        this.fileEnd = new ComboBox();
        this.name = new TextField();

        fileStart.setConverter(new StringConverter<DataIRC>() {
            @Override
            public String toString(DataIRC data) {
                return data.getFileName();
            }

            @Override
            public DataIRC fromString(String s) {
                return null;
            }
        });

        fileEnd.setConverter(new StringConverter<DataIRC>() {
            @Override
            public String toString(DataIRC data) {
                return data.getFileName();
            }

            @Override
            public DataIRC fromString(String s) {
                return null;
            }
        });

        fileStart.setItems(FXCollections.observableArrayList(dataIRC));
        fileEnd.setItems(FXCollections.observableArrayList(dataIRC));

        fileStart.getSelectionModel().selectFirst();
        fileEnd.getSelectionModel().selectLast();
    }

    public CheckBox getCheckBox() {
        return checkbox;
    }

    public ComboBox getFileStart() {
        return fileStart;
    }

    public ComboBox getFileEnd() {
        return fileEnd;
    }

    public TextField getName() {
        return name;
    }

    public FileResult getFileResultStart() {
        DataIRC selected = (DataIRC) fileStart.getSelectionModel().getSelectedItem();
        return selected.getFileResult();
    }

    public FileResult getFileResultEnd() {
        DataIRC selected = (DataIRC) fileEnd.getSelectionModel().getSelectedItem();
        return selected.getFileResult();
    }

    public int getFileNumStart() {
        DataIRC selected = (DataIRC) fileStart.getSelectionModel().getSelectedItem();
        return selected.getIndex();
    }

    public int getFileNumEnd() {
        DataIRC selected = (DataIRC) fileEnd.getSelectionModel().getSelectedItem();
        return selected.getIndex();
    }
}
