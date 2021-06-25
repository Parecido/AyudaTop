package topologyreader.tools;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import topologyreader.data.Attractor;
import topologyreader.data.Basin;
import topologyreader.data.DataIRC;
import topologyreader.data.DataTable;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;

public class Tools {
    public static int extractNumber(String name) {
        int i = 0;
        try {
            Pattern pattern = Pattern.compile("(\\d+)\\D*$");
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                i = Integer.parseInt(matcher.group(1));
            }
        } catch (NumberFormatException e) {
            i = 0;
        }
        return i;
    }

    public static Point3D[] findLineSphereIntersections(Point3D linePoint0, Point3D linePoint1, Point3D circleCenter, float circleRadius) {
        float cx = (float) circleCenter.getX();
        float cy = (float) circleCenter.getY();
        float cz = (float) circleCenter.getZ();

        float px = (float) linePoint0.getX();
        float py = (float) linePoint0.getY();
        float pz = (float) linePoint0.getZ();

        float vx = (float) linePoint1.getX() - px;
        float vy = (float) linePoint1.getY() - py;
        float vz = (float) linePoint1.getZ() - pz;

        float A = vx * vx + vy * vy + vz * vz;
        float B = 2 * (px * vx + py * vy + pz * vz - vx * cx - vy * cy - vz * cz);
        float C = px * px - 2 * px * cx + cx * cx + py * py - 2 * py * cy + cy * cy +
                pz * pz - 2 * pz * cz + cz * cz - circleRadius * circleRadius;

        float D = B * B - 4 * A * C;

        float t1 = (float) (-B - Math.sqrt(D)) / (2 * A);

        Point3D solution1 = new Point3D(linePoint0.getX() * (1 - t1) + t1 * linePoint1.getX(),
                linePoint0.getY() * (1 - t1) + t1 * linePoint1.getY(),
                linePoint0.getZ() * (1 - t1) + t1 * linePoint1.getZ());

        float t2 = (float) (-B + Math.sqrt(D)) / (2 * A);
        Point3D solution2 = new Point3D(linePoint0.getX() * (1 - t2) + t2 * linePoint1.getX(),
                linePoint0.getY() * (1 - t2) + t2 * linePoint1.getY(),
                linePoint0.getZ() * (1 - t2) + t2 * linePoint1.getZ());

        if (D < 0 || t1 > 1 || t2 > 1) {
            return new Point3D[0];
        } else if (D == 0) {
            return new Point3D[]{solution1};
        } else {
            return new Point3D[]{solution1, solution2};
        }
    }

    public static void showTable(float w, float h, List<TableColumn> columns, ObservableList<?> items, String title) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = (int) (gd.getDisplayMode().getWidth() / w);
        int height = (int) (gd.getDisplayMode().getHeight() / h);

        TableView table = new TableView();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        for (TableColumn<?, Number> column : columns) {
            column.setEditable(false);
            column.setSortable(false);
        }

        table.getColumns().addAll(columns);

        StackPane pane = new StackPane();
        pane.getChildren().add(table);
        pane.getStyleClass().add("pane");

        Scene scene = new Scene(pane, width, height);
        scene.getStylesheets().add("/topologyreader/style/TableTheme.css");
        scene.getStylesheets().add("/topologyreader/style/bootstrap3.css");

        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void updateTableTop(DataIRC data, List<DataTable> dataTable, TableView tableTop, Properties settings) {
        dataTable.clear();

        List<Attractor> attractors = data.getAttractors();
        List<Basin> basins = data.getBasins();

        if (Integer.parseInt(settings.getProperty("show.cores")) == 0) {
            List<Attractor> attractorsNotCores = attractors.stream()
                    .filter(predicate -> !predicate.isCore())
                    .collect(Collectors.toList());

            List<Basin> basinsNotCores = basins.stream()
                    .filter(predicate -> !predicate.isCore())
                    .collect(Collectors.toList());

            for (int i = 0; i < attractorsNotCores.size(); i++) {
                dataTable.add(new DataTable(attractorsNotCores.get(i), basinsNotCores.get(i)));
            }
        } else {
            for (int i = 0; i < attractors.size(); i++) {
                dataTable.add(new DataTable(attractors.get(i), basins.get(i)));
            }
        }

        tableTop.setItems(FXCollections.observableArrayList(dataTable));
    }

    public static void showMessage(String title, VBox vbox, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        if (vbox != null) {
            alert.getDialogPane().contentProperty().set(vbox);
        } else {
            alert.setContentText(message);
        }
        alert.getDialogPane().getStylesheets().add("/topologyreader/style/bootstrap3.css");
        alert.showAndWait();
    }

    public static boolean emptyCores(List<DataIRC> dataIRC, String title, String text) {
        boolean showWindow = true;

        for (DataIRC data : dataIRC) {
            if (data.getAtoms().isEmpty()) {
                showWindow = false;
            }
        }

        if (!showWindow) {
            Tools.showMessage(title, null, text);
        }

        return showWindow;
    }
}
