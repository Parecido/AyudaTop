package topologyreader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class TopologyReader extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int height = (int) (gd.getDisplayMode().getHeight() / 1.1);

        Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));
        stage.setScene(new Scene(root));
        stage.setHeight(height);
        stage.setTitle("AyudaTop");
        stage.show();

        Platform.setImplicitExit(true);
        stage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
    }
}