package dare2del.gui.view;

import dare2del.gui.controller.DeletionReasonController;
import dare2del.gui.controller.MainWindowController;
import dare2del.gui.model.DeletionModel;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class DeletionReasonPane extends VBox implements Observer {

    private DeletionModel deletionModel;

    private MainWindowController mainWindowController;
    private DeletionReasonController deletionReasonController;

    final WebView browser;
    final WebEngine webEngine;

    private String reason_default = "<span style='font-family:sans-serif;'>[REASON]</span>";

    public DeletionReasonPane(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        browser = new WebView();
        webEngine = browser.getEngine();
        this.getChildren().add(browser);
        show();
    }

    public void show() {
        URL url = getClass().getResource("/reason_dummy.html");
        System.out.println(url);
        webEngine.load(url.toString());
    }

    public void showDefault() {
        String contentHTML = String.format(reason_default);
        webEngine.loadContent(contentHTML);
    }

    public void setDeletionReasonController(DeletionReasonController deletionReasonController) {
        this.deletionReasonController = deletionReasonController;
    }

    public void update(Observable observable, Object object) {
        System.out.println("DeletionResonPane > update: Observable: " + observable + " | object: " + object);
//        this.show(object.toString());
    }
}
