package dare2del.gui.view;

import com.sun.deploy.security.DecisionTime;
import dare2del.gui.controller.DeletionReasonController;
import dare2del.gui.controller.MainWindowController;
import dare2del.gui.model.DeletionModel;
import dare2del.logic.DetailedFile;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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

        deletionModel.webEngine = this.webEngine;

        showDel();
    }

    public void showDel() {
        URL url = getClass().getResource("/reason_dummy.html");
        Document reasonDoc = fillDeletionReasons(url);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    public void showNearMiss() {
        URL url = getClass().getResource("/reason_dummy.html");
        Document reasonDoc = fillNearMissReasons(url);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        } else {
            webEngine.loadContent(reason_default);
        }
    }

    private Document fillNearMissReasons(URL url) {
        Document doc = null;

        return doc;
    }

    private Document fillDeletionReasons(URL url) {
        Document doc = null;
        DetailedFile currentSelectedDeletionCandidate = deletionModel.getCurrentSelectedDeletionCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allDeletionCandidates = deletionModel.getDeletionCandidatePairsWithReasonsGROUPED();

        if (currentSelectedDeletionCandidate == null) {
            return doc;
        }

        HashMap<DetailedFile, List<String>> reasonForCurrentDeletionCandidate = allDeletionCandidates.get(currentSelectedDeletionCandidate);

        try {
            File dummyFile = new File(url.getPath());
            doc = Jsoup.parse(dummyFile, "UTF-8");
            doc.outputSettings().prettyPrint(false);

            Elements elements_FileA = doc.getElementsByClass("fileA");
            for (Element eachFileA : elements_FileA) {
                eachFileA.text(currentSelectedDeletionCandidate.getName());
            }

            //reasons according to file itself
            if (reasonForCurrentDeletionCandidate.containsKey(currentSelectedDeletionCandidate)) {
                Element ul_itself = doc.getElementsByClass("itself").first();

                List<String> reasons_olderThanOneYear = reasonForCurrentDeletionCandidate.get(currentSelectedDeletionCandidate);
                for (String reason : reasons_olderThanOneYear) {
                    Element elem_reason = ul_itself.appendElement("li");
                    elem_reason.attr("id", reason);

                    Element elem_spanFileA = elem_reason.appendElement("span");
                    elem_spanFileA.attr("class", "fileA");
                    elem_spanFileA.text(currentSelectedDeletionCandidate.getName());

                    elem_reason.appendText(" is " + reason.replace("_", " "));
                }
            }

            //reasons according to other files
            for (DetailedFile reason_detailedFile : reasonForCurrentDeletionCandidate.keySet()) {
                if (reason_detailedFile != currentSelectedDeletionCandidate) {
                    Element ul_another = doc.getElementsByClass("another").first().clone();

                    List<String> reasonList = reasonForCurrentDeletionCandidate.get(reason_detailedFile);
                    for (String reason : reasonList) {
                        Element elem_reason = ul_another.appendElement("li");
                        elem_reason.attr("id", reason);

                        Element elem_spanFileB = elem_reason.appendElement("span");
                        elem_spanFileB.attr("class", "fileB");
                        elem_spanFileB.text(reason_detailedFile.getName());

                        elem_reason.appendText(" is " + reason.replace("_", " "));
                    }

                    doc.getElementsByTag("body").first().appendChild(ul_another);
                }
            }

        } catch (IOException e) {
            //TODO: Exception Handling!
            System.out.println("ERROR in DeletionReasonPane, fillDeletionReasons(). " + e.getMessage());
        }

        return doc;
    }

    public void setDeletionReasonController(DeletionReasonController deletionReasonController) {
        this.deletionReasonController = deletionReasonController;
    }

    public void update(Observable observable, Object object) {
        if (object instanceof DetailedFile) {
            if (deletionModel.getCurrentSelectedDeletionCandidate() != null) {
                showDel();
            } else if (deletionModel.getCurrentSelectedNearMissCandidate() != null) {
                showNearMiss();
            }
        }

        System.out.println("DeletionResonPane > update: Observable: " + observable + " | object: " + object);
//        this.showDel(object.toString());
    }
}
