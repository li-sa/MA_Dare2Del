package dare2del;

import dare2del.gui.controller.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

//FileChooser: https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainWindowController mainWindowController = new MainWindowController(primaryStage);
    }

    public static void main(String[] args) {
//        Locale.setDefault(Locale.GERMAN);
        launch(args);
    }
}
