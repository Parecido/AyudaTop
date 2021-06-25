package topologyreader.data;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.CheckBox;

public class Editor {
    private final CheckBox checkbox;
    private final Data data;

    public Editor(Data data) {
        this.checkbox = new CheckBox();
        this.data = data;
    }

    public CheckBox getCheckBox() {
        return checkbox;
    }

    public Data getData() {
        return data;
    }

    public String getFile() {
        return data.getXValue().toString();
    }

    public String getValue() {
        return data.getYValue().toString();
    }
}
