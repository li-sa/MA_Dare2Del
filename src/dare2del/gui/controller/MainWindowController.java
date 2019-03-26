package dare2del.gui.controller;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.MainView;
import dare2del.logic.DetailedFile;
import dare2del.logic.FileCrawler;
import dare2del.logic.PrologFileWriter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.fx.ui.controls.filesystem.DirectoryTreeView;
import org.eclipse.fx.ui.controls.filesystem.DirectoryView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowController {

    private final String default_rootPath = "C:\\Users\\Lisa\\Desktop\\MA_Beispiele\\TestDir_2";

    private final Stage primaryStage;
    private final DeletionModel deletionModel;

    private final MainView mainView;

    private Path rootPath;

    public MainWindowController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.deletionModel = new DeletionModel();

//        String chosenRootPath = showDirectoryFileChooser(primaryStage);
        validatePath(default_rootPath); // just for testing! -> parameter might be chosenRootPath
        // Preparation: Crawl files within rootPath and write metadata to prolog file clauses.pl
        initProlog();

        this.deletionModel.initDeletionModel();

        //Create MainView
        mainView = new MainView(deletionModel, primaryStage);
        setAllListeners();
    }

    private void setAllListeners() {
        setEventHandler_OpenFile(mainView.getOpenFileItem());
        setEventHandler_Exit(mainView.getExitItem());
        createListenerForTreeView(mainView.getDirectoryTreeView(), mainView.getDirectoryView());
        createListenerForDirectoryView(mainView.getDirectoryView());
        createListenerForDeletionCells(mainView.getDelList().getDeletionCandidatesCellList());
    }

    private String showDirectoryFileChooser(Stage primaryStage) {
        String rootName = default_rootPath;

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            rootName = selectedDirectory.getAbsolutePath();
        }

        deletionModel.myLogger.info("[MainWindowController] Selected root path: " + rootName + ".");
        return rootName;
    }

    private void initProlog() {
        FileCrawler fileCrawler = new FileCrawler(rootPath, deletionModel.myLogger);
        List<DetailedFile> fileList = fileCrawler.getFileList();
        PrologFileWriter prologFileWriter = new PrologFileWriter(fileList, deletionModel.myLogger);

        this.deletionModel.setFileList(fileList);
    }

    private void validatePath(String pathName) {
        try {
            rootPath = Paths.get(pathName);
            deletionModel.setRootPath(rootPath);
        } catch (Exception e) {
            deletionModel.myLogger.warning("Root folder ist no valid path (" + pathName + ").");
            throw new IllegalArgumentException();
        }

        if (!rootPath.toFile().isDirectory()) {
            deletionModel.myLogger.warning("Root folder ist no valid directory (" + pathName + ").");
            throw new IllegalArgumentException();
        }
    }

    private void setEventHandler_OpenFile(MenuItem openFileItem) {
        openFileItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deletionModel.resetFileList();
                validatePath(showDirectoryFileChooser(primaryStage));
                initProlog();
                deletionModel.initDeletionModel();
                mainView.initView();
                setAllListeners();
            }
        });
    }

    private void setEventHandler_Exit(MenuItem exitItem) {
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
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

    private void createListenerForDirectoryView(DirectoryView v) {
        v.getSelectedItems().addListener((javafx.beans.Observable o) -> {
            if (!v.getSelectedItems().isEmpty()) {
                File selectedItem_file = null;
                try {
                    Path selectedItem_path = Paths.get(v.getSelectedItems().get(0).getUri().replace("file:/", ""));
                    selectedItem_file = selectedItem_path.toFile();
                } catch (Exception e) {
                    deletionModel.myLogger.warning("[MainWindowController] Exception in createListenerForDirectoryView(): " + e.getMessage());
                    throw new IllegalArgumentException(e);
                }

//                if (selectedItem_file != null && selectedItem_file.isDirectory()) {
//                    ObservableList<DirItem> dirs_temp = FXCollections.observableArrayList();
//                    dirs_temp.add(ResourceItem.createObservedPath(selectedItem_file.toPath()));
//                    tv.setRootDirectories(dirs_temp);
//                }
            }
        });
    }

    private void createListenerForDeletionCells(List<ListCell<DetailedFile>> deletionCandidatesCellList) {
        this.mainView.getDelList().getDeletionCandidates();
    }
}
