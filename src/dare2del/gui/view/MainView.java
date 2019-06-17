package dare2del.gui.view;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Tabs.DeletionListPane;
import dare2del.gui.view.Tabs.NearMissListPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.eclipse.fx.ui.controls.filesystem.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainView implements Observer {
    private final DeletionModel deletionModel;
    private final Stage primaryStage;

    private DirectoryTreeView tv;
    private DirectoryView v;
    private TabPane candidateTabs;
    private Button deleteButton;

    private DeletionListPane delList;
    private NearMissListPane nearMissList;

    private MenuItem openFileItem;
    private MenuItem reloadItem;
    private MenuItem exitItem;

    public MainView(DeletionModel deletionModel, Stage primaryStage) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);
        this.primaryStage = primaryStage;

        initView();
    }

    public void initView() {
        deletionModel.myLogger.info("[MainView] initView().");

        Path rootPath = deletionModel.getRootPath();

        ObservableList<DirItem> rootDirs = FXCollections.observableArrayList();

        File rootFile = rootPath.toFile();
        if (rootFile.isDirectory()) {
            rootDirs.add(ResourceItem.createPath(rootPath)); //createObservedPath(rootPath)
        } else {
            for (File dir : rootPath.toFile().listFiles(File::isDirectory)) {
                rootDirs.add(ResourceItem.createPath(dir.toPath())); //createObservedPath(rootPath)
            }
        }

        tv = new DirectoryTreeView();
        tv.setIconSize(IconSize.MEDIUM);
        tv.setRootDirectories(rootDirs);

        v = new DirectoryView();
        v.setIconSize(IconSize.MEDIUM);
        v.setDir(tv.getRootDirectories().get(0));

        SplitPane thirdColumn = createThirdColumn();

        SplitPane hPane = new SplitPane(tv, v, thirdColumn);
        hPane.setDividerPositions(0.2, 0.6);

        BorderPane borderPane = new BorderPane(hPane);
        borderPane.setTop(createMenuBar());

        Scene s = new Scene(borderPane, 1600, 800);
        primaryStage.setScene(s);
        primaryStage.setTitle("Dare2Del");
        primaryStage.show();
    }

    public SplitPane createThirdColumn() {
        candidateTabs = createCandidateTabs();
        deleteButton = new Button(Messages.getString("DeletionButton.Label"));
        deleteButton.setDisable(true);
        DeletionReasonPane reasonPane = new DeletionReasonPane(this.deletionModel);
        SplitPane thirdColumn = new SplitPane();
        thirdColumn.setOrientation(Orientation.VERTICAL);
        thirdColumn.setDividerPositions(0.45, 0.9);
        deleteButton.prefWidthProperty().bind(thirdColumn.widthProperty());
        thirdColumn.getItems().addAll(candidateTabs, reasonPane, deleteButton);
        return thirdColumn;
    }

    private TabPane createCandidateTabs() {
        candidateTabs = new TabPane();

        delList = new DeletionListPane(deletionModel);
        Tab deletionTab = new Tab();
        deletionTab.setText("Deletion Candidates");
        deletionTab.setContent(delList);

        nearMissList = new NearMissListPane(deletionModel);
        Tab nearMissTab = new Tab();
        nearMissTab.setText("Global Near Misses");
        nearMissTab.setContent(nearMissList);

        candidateTabs.getTabs().addAll(deletionTab, nearMissTab);
        candidateTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return candidateTabs;
    }

    private MenuBar createMenuBar() {
        //Menu
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");

        openFileItem = new MenuItem("Open Directory");
        openFileItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));

        reloadItem = new MenuItem("Reload current directory");

        exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        fileMenu.getItems().addAll(openFileItem, reloadItem, exitItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        return menuBar;
    }

    public void update(Observable observable, Object object) {
        if (object instanceof List) {
            if(deletionModel.getFilesSelectedForDeletion().isEmpty()) {
                deleteButton.setDisable(true);
            } else {
                deleteButton.setDisable(false);
            }
        }
    }

    public MenuItem getOpenFileItem() {
        return openFileItem;
    }

    public MenuItem getReloadItem() {
        return reloadItem;
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    public DirectoryTreeView getDirectoryTreeView() {
        return tv;
    }

    public DirectoryView getDirectoryView() {
        return v;
    }

    public TabPane getCandidateTabs() {
        return candidateTabs;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public DeletionListPane getDelList() {
        return delList;
    }

    public NearMissListPane getNearMissList() {
        return nearMissList;
    }
}
