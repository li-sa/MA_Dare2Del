package dare2del;

import dare2del.gui.controller.MainWindowController;
import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

//FileChooser: https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String rootName = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir";

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            rootName = selectedDirectory.getAbsolutePath();
        }

        MainWindowController mainWindowController = new MainWindowController(primaryStage, rootName);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }


    public static void main(String[] args) {
//        Locale.setDefault(Locale.GERMAN);
        launch(args);
    }
}
