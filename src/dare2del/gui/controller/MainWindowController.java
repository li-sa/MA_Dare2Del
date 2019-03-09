package dare2del.gui.controller;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.DeletionReasonPane;
import dare2del.gui.view.Tabs.DeletionListPane;
import dare2del.gui.view.Tabs.NearMissListPane;
import dare2del.logic.DetailedFile;
import dare2del.logic.FileCrawler;
import dare2del.logic.PrologFileWriter;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.fx.ui.controls.filesystem.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowController {

    private final String default_rootPath = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir_2";

    private Stage primaryStage;
    public DeletionModel deletionModel;

    private Path rootPath;
    private List<DetailedFile> fileList;

    public MainWindowController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        String choosenRootPath = showDirectoryFileChooser(primaryStage);
        validatePath(choosenRootPath);
        // Preparation: Crawl files within rootPath and write metadata to prolog file clauses.pl
        initProlog();
        initView(primaryStage);
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

        // Context menu to load new Directory
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Open Directory");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                validatePath(showDirectoryFileChooser(primaryStage));
                initProlog();
                initView(primaryStage);
            }
        });
        contextMenu.getItems().addAll(item1);

        tv.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(tv, event.getScreenX(), event.getScreenY());
            }
        });
        // END Context menu

        DirectoryView v = new DirectoryView();
        v.setIconSize(IconSize.MEDIUM);
        v.setDir(tv.getRootDirectories().get(0));

        v.getSelectedItems().addListener((Observable o) -> {
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

        tv.getSelectedItems().addListener((Observable o) -> {
            if (!tv.getSelectedItems().isEmpty()) {
                v.setDir(tv.getSelectedItems().get(0));
            } else {
                v.setDir(null);
            }
        });

        DeletionListPane delList = new DeletionListPane(this);
        DeletionReasonPane delReason = new DeletionReasonPane();
        SplitPane splitPane_deletion = new SplitPane(delList, delReason);
        splitPane_deletion.setOrientation(Orientation.VERTICAL);
        splitPane_deletion.setDividerPositions(0.5);

        NearMissListPane nearMissList = new NearMissListPane(this);
        DeletionReasonPane nearMissReason = new DeletionReasonPane();
        SplitPane splitPane_nearMiss = new SplitPane(nearMissList, nearMissReason);
        splitPane_nearMiss.setOrientation(Orientation.VERTICAL);
        splitPane_nearMiss.setDividerPositions(0.5);

        TabPane tabPane = new TabPane();

        Tab deletionTab = new Tab();
        deletionTab.setText("Deletion Tab");
        deletionTab.setContent(splitPane_deletion);

        Tab nearMissTab = new Tab();
        nearMissTab.setText("Near Misses Tab");
        nearMissTab.setContent(splitPane_nearMiss);

        tabPane.getTabs().addAll(deletionTab, nearMissTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        SplitPane hPane = new SplitPane(tv, v, tabPane);
        hPane.setDividerPositions(0.2, 0.6);

        BorderPane borderPane = new BorderPane(hPane);
        borderPane.setTop(createMenuBar());

        Scene s = new Scene(borderPane, 1200, 600);
        primaryStage.setScene(s);
        primaryStage.setTitle("Dare2Del");
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");

        MenuItem openFileItem = new MenuItem("Open Directory");
        openFileItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                validatePath(showDirectoryFileChooser(primaryStage));
                initProlog();
                initView(primaryStage);
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        exitItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        fileMenu.getItems().addAll(openFileItem, exitItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        return menuBar;
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

    public Path getRootPath() {
        return rootPath;
    }
}
