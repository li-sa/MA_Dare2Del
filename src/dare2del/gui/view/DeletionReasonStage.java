package dare2del.gui.view;

import dare2del.gui.controller.DeletionReasonController;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class DeletionReasonStage extends Stage {

    public DeletionReasonStage(DeletionReasonController deletionReasonController) {
        String contentHTML = String.format(Messages.getString("DeletionReasonStage.reasonHTML"),
                deletionReasonController.getFilePath().toString(),
                deletionReasonController.getReason());
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.loadContent(contentHTML);

        this.setScene(new Scene(browser));
        this.setMinWidth(480);
        this.setMinHeight(320);
        this.setTitle(Messages.getString("DeletionReasonStage.windowTitle"));
    }

}
