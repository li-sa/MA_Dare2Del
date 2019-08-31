package dare2del;

import dare2del.gui.controller.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @SuppressWarnings("unused")
    @Override
    public void start(Stage primaryStage) {
        MainWindowController mainWindowController = new MainWindowController(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
