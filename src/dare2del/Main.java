package dare2del;

import dare2del.gui.controller.MainWindowController;
import dare2del.gui.view.DeletionListPane;
import dare2del.gui.view.DeletionReasonPane;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.eclipse.fx.ui.controls.filesystem.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String rootName = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir_2";
        MainWindowController mainWindowController = new MainWindowController(rootName);

        Path rootPath;
        if (!mainWindowController.validatePath(rootName)) {
            throw new IllegalArgumentException("No valid path :" + rootName);
        } else {
            rootPath = Paths.get(rootName);
        }

        ObservableList<DirItem> rootDirs = FXCollections.observableArrayList();

        File rootfile = rootPath.toFile();
        if (rootfile.isDirectory()) {
            rootDirs.add(ResourceItem.createObservedPath(rootPath));
        } else {
            for (File dir : rootPath.toFile().listFiles(f -> f.isDirectory())) {
                rootDirs.add(ResourceItem.createObservedPath(dir.toPath()));
            }
        }

        DirectoryTreeView tv = new DirectoryTreeView();
        tv.setIconSize(IconSize.MEDIUM);
        tv.setRootDirectories(rootDirs);

        DirectoryView v = new DirectoryView();
        v.setIconSize(IconSize.MEDIUM);

        tv.getSelectedItems().addListener((Observable o) -> {
            if (!tv.getSelectedItems().isEmpty()) {
                v.setDir(tv.getSelectedItems().get(0));
            } else {
                v.setDir(null);
            }
        });

        DeletionListPane delList = new DeletionListPane(mainWindowController);
        DeletionReasonPane delReason = new DeletionReasonPane();
        SplitPane vPaneRight = new SplitPane(delList, delReason);
        vPaneRight.setOrientation(Orientation.VERTICAL);
        vPaneRight.setDividerPositions(0.4);

        SplitPane hPane = new SplitPane(tv, v, vPaneRight);
        hPane.setDividerPositions(0.235, 0.69);

        Scene s = new Scene(hPane, 1000, 600);
        primaryStage.setScene(s);
        primaryStage.setTitle("Dare2Del");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
