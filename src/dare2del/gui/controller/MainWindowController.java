package dare2del.gui.controller;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.DeletionListPane;
import dare2del.gui.view.DeletionReasonPane;
import dare2del.logic.DetailedFile;
import dare2del.logic.FileCrawler;
import dare2del.logic.PrologFileWriter;
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
import java.util.List;

public class MainWindowController {

    public DeletionModel deletionModel;

    // Views


    private Path rootPath;
    private List<DetailedFile> fileList;

    public MainWindowController(Stage primaryStage, String pathName) {
        if (!validatePath(pathName)) {
            throw new IllegalArgumentException("No valid path :" + pathName);
        } else {
            rootPath = Paths.get(pathName);
        }

        // Preparation: Crawl files within rootPath and write metadata to prolog file clauses.pl
        initProlog();

        initView(primaryStage);
    }

    private void initProlog() {
        FileCrawler fileCrawler = new FileCrawler(rootPath);
        fileList = fileCrawler.getFileList();
        PrologFileWriter prologFileWriter = new PrologFileWriter(fileList);

        this.deletionModel = new DeletionModel(fileList);
    }

    private void initView(Stage primaryStage) {
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

        DeletionListPane delList = new DeletionListPane(this);
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

    private void getDeletionCandidates() {

    }

    public boolean validatePath(String pathName) {
        try {
            rootPath = Paths.get(pathName);
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
}
