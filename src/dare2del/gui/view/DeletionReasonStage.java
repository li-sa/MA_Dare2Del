package dare2del.gui.view;

import dare2del.gui.controller.DeletionReasonController;
import javafx.stage.Stage;

public class DeletionReasonStage extends Stage {

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

    public DeletionReasonStage(DeletionReasonController deletionReasonController) {
//        String contentHTML = String.format(Messages.getString("DeletionReasonStage.reasonHTML"),
//                deletionReasonController.getFilePath().toString(),
//                deletionReasonController.getReason());
//        final WebView browser = new WebView();
//        final WebEngine webEngine = browser.getEngine();
//        webEngine.loadContent(contentHTML);
//
//        this.setScene(new Scene(browser));
//        this.setMinWidth(480);
//        this.setMinHeight(320);
//        this.setTitle(Messages.getString("DeletionReasonStage.windowTitle"));
    }


}
