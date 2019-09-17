package dare2del.gui.controller;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.MainView;
import dare2del.logic.DetailedFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.fx.ui.controls.filesystem.DirectoryTreeView;
import org.eclipse.fx.ui.controls.filesystem.DirectoryView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowController {

    private final String MA_PDF = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../LisaSchatt_Masterthesis.pdf";
    private final String EXAMPLE_DIRECTORY = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../DirectoryExample";
    private final Path exampleDirPath = Paths.get(EXAMPLE_DIRECTORY.replaceFirst("/", ""));

    private final Stage primaryStage;
    private final DeletionModel deletionModel;

    private final MainView mainView;

    private Path rootPath;

    public MainWindowController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        deletionModel = new DeletionModel();

        validatePath(exampleDirPath.toString());
        deletionModel.initProlog(this.rootPath);
        deletionModel.initDeletionModel();

        //Create MainView
        mainView = new MainView(deletionModel, primaryStage);
        setAllListeners();
    }

    private void setAllListeners() {
        setEventHandler_OpenFile(mainView.getOpenFileItem());
        setEventHandler_ReloadFile(mainView.getReloadItem());
        setEventHandler_Exit(mainView.getExitItem());
        setEventHandler_OpenMA(mainView.getOpenMAItem());
        createListenerForTreeView(mainView.getDirectoryTreeView(), mainView.getDirectoryView());
        createListenerForDeletionButton(mainView.getDeleteButton());

        mainView.getCandidateTabs().getSelectionModel().selectedIndexProperty()
                .addListener((ov, oldValue, newValue) -> deletionModel.resetCurrentChoices());
    }

    private String showDirectoryFileChooser(Stage primaryStage) {
        String rootName = exampleDirPath.toString();

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            rootName = selectedDirectory.getAbsolutePath();
        }

        deletionModel.myLogger.info("[MainWindowController] Selected root path: " + rootName + ".");
        return rootName;
    }

    private void validatePath(String pathName) {
        try {
            rootPath = Paths.get(pathName);
            deletionModel.setRootPath(rootPath);
        } catch (Exception e) {
            deletionModel.myLogger.warning("Root folder is no valid path (" + pathName + ").");
            throw new IllegalArgumentException();
        }

        if (!rootPath.toFile().isDirectory()) {
            deletionModel.myLogger.warning("Root folder is no valid directory (" + pathName + ").");
            throw new IllegalArgumentException();
        }
    }

    private void setEventHandler_OpenFile(MenuItem openFileItem) {
        openFileItem.setOnAction(event -> {
            deletionModel.resetFileList();
            validatePath(showDirectoryFileChooser(primaryStage));
            deletionModel.initProlog(rootPath);
            deletionModel.initDeletionModel();
            mainView.initView();
            setAllListeners();
        });
    }

    private void setEventHandler_ReloadFile(MenuItem reloadFileItem) {
        reloadFileItem.setOnAction(event -> {
            deletionModel.resetFileList();
            deletionModel.initProlog(rootPath);
            deletionModel.initDeletionModel();
            mainView.initView();
            setAllListeners();
        });
    }

    private void setEventHandler_Exit(MenuItem exitItem) {
        exitItem.setOnAction(event -> System.exit(0));
    }

    private void setEventHandler_OpenMA(MenuItem openMAItem) {
        openMAItem.setOnAction(event -> {
            try {
                File thesisPdfFile = new File(MA_PDF);
                if (thesisPdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(thesisPdfFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createListenerForTreeView(DirectoryTreeView tv, DirectoryView v) {
        tv.getSelectedItems().addListener((javafx.beans.Observable o) -> {
            if (!tv.getSelectedItems().isEmpty()) {
                v.setDir(tv.getSelectedItems().get(0));
            } else {
                v.setDir(null);
            }
        });
    }

    private void createListenerForDeletionButton(Button deleteButton) {
        deleteButton.setOnAction(event -> {
            boolean successfulDeletion = deletionModel.confirmDeletion();

            if (successfulDeletion) {
                deletionModel.resetFileList();
                deletionModel.initProlog(rootPath);
                deletionModel.initDeletionModel();
                mainView.initView();
                setAllListeners();
            }
        });
    }
}
