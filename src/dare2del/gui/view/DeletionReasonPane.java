package dare2del.gui.view;

import dare2del.gui.controller.DeletionReasonController;
import dare2del.gui.controller.MainWindowController;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class DeletionReasonPane extends VBox {

    private MainWindowController mainWindowController;
    private DeletionReasonController deletionReasonController;

    final WebView browser;
    final WebEngine webEngine;

    private String reason_default = "<span style='font-family:sans-serif;'>[REASON]</span>";

    public DeletionReasonPane() {
        browser = new WebView();
        webEngine = browser.getEngine();
        this.getChildren().add(browser);

//        showDefault();
    }

    public void show() {
        String url = getClass().getResource("/reason_dummy.html").toExternalForm();
        System.out.println(url);
        webEngine.loadContent(url);
    }

    public void showDefault() {
        String contentHTML = String.format(reason_default);
        webEngine.loadContent(contentHTML);
    }

    public void setDeletionReasonController(DeletionReasonController deletionReasonController) {
        this.deletionReasonController = deletionReasonController;
    }
}
