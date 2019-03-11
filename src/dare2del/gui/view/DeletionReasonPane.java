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

    private String reason = "<span style='font-family:sans-serif;'>File "
            + "<span style='font-family:monospaced; padding: 0 0.5em;'>KI_Conference_v3.pptx</span> "
            + "may be deleted because <ul>"
            + "<li>file <span style='font-family:monospaced; padding: 0 0.5em;'>KI_Conference_final.pptx</span> "
            + "is in the same directory,</li>"
            + "<li>files <span style='font-family:monospaced; padding: 0 0.5em;'>KI_Conference_v3.pptx</span> and "
            + "<span style='font-family:monospaced;'>KI_Conference_final.pptx</span> are very similar,</li>"
            + "<li>files <span style='font-family:monospaced;'>KI_Conference_v3.pptx</span> and "
            + "<span style='font-family:monospaced;'>KI_Conference_final.pptx</span> start with"
            + "(at least) 5 identical characters, and</li>"
            + "<li>file <span style='font-family:monospaced;'>KI_Conference_final.pptx</span> is newer "
            + "than file <span style='font-family:monospaced;'>KI_Conference_v3.pptx</span>.</li>"
            + "</span>";

    public DeletionReasonPane(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;

        browser = new WebView();
        webEngine = browser.getEngine();
    }

    public DeletionReasonPane(DeletionReasonController deletionReasonController) {
        this.deletionReasonController = deletionReasonController;

        browser = new WebView();
        webEngine = browser.getEngine();
    }

    public void show() {
        String contentHTML = String.format(reason);
        webEngine.loadContent(contentHTML);
//        String url = getClass().getResource("/reason.html").toExternalForm();
//        System.out.println(url);
//        webEngine.load(url);
        this.getChildren().add(browser);
    }

    public void setDeletionReasonController(DeletionReasonController deletionReasonController) {
        this.deletionReasonController = deletionReasonController;
    }
}
