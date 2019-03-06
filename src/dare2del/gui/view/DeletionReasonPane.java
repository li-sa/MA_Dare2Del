package dare2del.gui.view;

import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class DeletionReasonPane extends VBox {
	
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
	public DeletionReasonPane() {
		String contentHTML = String.format(reason);
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		//webEngine.loadContent(contentHTML);
//		String url = getClass().getResource("/reason.html").toExternalForm();
//		System.out.println(url);
//		webEngine.load(url);
//		this.getChildren().add(browser);
	}

}
