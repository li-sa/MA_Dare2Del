package dare2del.gui.view;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Tabs.DeletionListPane;
import dare2del.gui.view.Tabs.NearMissListPane;
import dare2del.logic.DetailedFile;
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
import java.util.Observable;
import java.util.Observer;

public class MainView implements Observer {
    private DeletionModel deletionModel;
    private Stage primaryStage;

    //Main components
    private DirectoryTreeView tv;
    private DirectoryView v;
    private TabPane tabPane;
    private SplitPane hPane;
    private BorderPane borderPane;
    private Scene s;

    //TabPane components
    DeletionReasonPane reasonPane;
    DeletionListPane delList;
    SplitPane splitPane_deletion;
    NearMissListPane nearMissList;
    SplitPane splitPane_nearMiss;
    Tab deletionTab;
    Tab nearMissTab;

    //Menu
    MenuBar menuBar;
    Menu fileMenu;
    Menu editMenu;
    Menu helpMenu;
    MenuItem openFileItem;
    MenuItem exitItem;

    public MainView(DeletionModel deletionModel, Stage primaryStage) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        this.primaryStage = primaryStage;

        initView();
    }

    public void initView() {
        Path rootPath = deletionModel.getRootPath();

        ObservableList<DirItem> rootDirs = FXCollections.observableArrayList();

        File rootfile = rootPath.toFile();
        if (rootfile.isDirectory()) {
            rootDirs.add(ResourceItem.createObservedPath(rootPath));
        } else {
            for (File dir : rootPath.toFile().listFiles(f -> f.isDirectory())) {
                rootDirs.add(ResourceItem.createObservedPath(dir.toPath()));
            }
        }

        tv = new DirectoryTreeView();
        tv.setIconSize(IconSize.MEDIUM);
        tv.setRootDirectories(rootDirs);

        v = new DirectoryView();
        v.setIconSize(IconSize.MEDIUM);
        v.setDir(tv.getRootDirectories().get(0));

        tabPane = createTabs();
        reasonPane = new DeletionReasonPane(this.deletionModel);
        SplitPane thirdColumn = new SplitPane(tabPane, reasonPane);
        thirdColumn.setOrientation(Orientation.VERTICAL);
        thirdColumn.setDividerPositions(0.5);

        hPane = new SplitPane(tv, v, thirdColumn);
        hPane.setDividerPositions(0.2, 0.6);

        borderPane = new BorderPane(hPane);
        borderPane.setTop(createMenuBar());

        s = new Scene(borderPane, 1600, 800);
        primaryStage.setScene(s);
        primaryStage.setTitle("Dare2Del");
        primaryStage.show();
    }

    private TabPane createTabs() {
        tabPane = new TabPane();

        delList = new DeletionListPane(deletionModel);
        deletionTab = new Tab();
        deletionTab.setText("Deletion Tab");
        deletionTab.setContent(delList);

        nearMissList = new NearMissListPane(deletionModel);
        nearMissTab = new Tab();
        nearMissTab.setText("Near Misses Tab");
        nearMissTab.setContent(nearMissList);

        tabPane.getTabs().addAll(deletionTab, nearMissTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabPane;
    }

    private MenuBar createMenuBar() {
        menuBar = new MenuBar();

        fileMenu = new Menu("File");
        editMenu = new Menu("Edit");
        helpMenu = new Menu("Help");

        openFileItem = new MenuItem("Open Directory");

        exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        fileMenu.getItems().addAll(openFileItem, exitItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        return menuBar;
    }

    public void update(Observable observable, Object object) {
    }

    public MenuItem getOpenFileItem() {
        return openFileItem;
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

    public DeletionListPane getDelList() {
        return delList;
    }

    public NearMissListPane getNearMissList() {
        return nearMissList;
    }

}
