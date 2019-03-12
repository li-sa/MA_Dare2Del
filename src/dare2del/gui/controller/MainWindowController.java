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

    private final String default_rootPath = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir_2";

    private Stage primaryStage;
    public DeletionModel deletionModel;

    private MainView mainView;

    private Path rootPath;
    private List<DetailedFile> fileList;

    public MainWindowController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.deletionModel = new DeletionModel();

        String choosenRootPath = showDirectoryFileChooser(primaryStage);
        validatePath(choosenRootPath); // just for testing! -> parameter might be choosenRootPath
        // Preparation: Crawl files within rootPath and write metadata to prolog file clauses.pl
        initProlog();

        this.deletionModel.initDeletionModel();

        //Create MainView
        mainView = new MainView(deletionModel, primaryStage);
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
        return rootName;
    }

    private void initProlog() {
        FileCrawler fileCrawler = new FileCrawler(rootPath);
        fileList = fileCrawler.getFileList();
        PrologFileWriter prologFileWriter = new PrologFileWriter(fileList);

        this.deletionModel.setFileList(fileList);
    }

    public boolean validatePath(String pathName) {
        try {
            rootPath = Paths.get(pathName);
            deletionModel.setRootPath(rootPath);
        } catch (Exception e) {
            System.out.println("INFO. Root folder ist no valid path (" + pathName + ").");
            throw new IllegalArgumentException();
        }

        if (rootPath.toFile().isDirectory()) {
            return true;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void setEventHandler_OpenFile(MenuItem openFileItem) {
        openFileItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                validatePath(showDirectoryFileChooser(primaryStage));
                initProlog();
                mainView.initView();
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
                    throw new IllegalArgumentException(e);
                }

                if (selectedItem_file != null && selectedItem_file.isDirectory()) {
//                    ObservableList<DirItem> dirs_temp = FXCollections.observableArrayList();
//                    dirs_temp.add(ResourceItem.createObservedPath(selectedItem_file.toPath()));
//                    tv.setRootDirectories(dirs_temp);
                }
            }
        });
    }

    private void createListenerForDeletionCells(List<ListCell<DetailedFile>> deletionCandidatesCellList) {
        this.mainView.getDelList().getDeletionCandidates();
    }
}
