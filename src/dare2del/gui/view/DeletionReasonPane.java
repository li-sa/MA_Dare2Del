package dare2del.gui.view;

import dare2del.gui.controller.MainWindowController;
import dare2del.gui.model.DeletionModel;
import dare2del.logic.DetailedFile;
import dare2del.logic.QueryKind;
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

    private final DeletionModel deletionModel;

    private MainWindowController mainWindowController;

    private final WebView browser;
    private final WebEngine webEngine;

    public DeletionReasonPane(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        browser = new WebView();
        webEngine = browser.getEngine();
        this.getChildren().add(browser);

        deletionModel.webEngine = this.webEngine;

        showDel();
    }

    private void showDel() {
        deletionModel.myLogger.info("[DeletionReasonPane] showDel().");

        URL url = getClass().getResource("/deletion_dummy.html");

        DetailedFile currentSelected = deletionModel.getCurrentSelectedDeletionCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getDeletionPairs_grouped();

        Document reasonDoc = fillReasonHTML(url, currentSelected, allCandidates, QueryKind.IRRELEVANT);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private void showNearMiss() {
        deletionModel.myLogger.info("[DeletionReasonPane] showNearMiss().");

        URL url = getClass().getResource("/nearmiss_dummy.html");

        DetailedFile currentSelected = deletionModel.getCurrentSelectedNearMissCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getNearMissPairs_grouped();

        Document reasonDoc = fillReasonHTML(url, currentSelected, allCandidates, QueryKind.RELEVANT);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private Document fillReasonHTML(URL url, DetailedFile currentSelected, HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates, QueryKind queryKind) {
        Document doc = null;

        if (currentSelected == null) {
            return doc;
        }

        HashMap<DetailedFile, List<String>> reasonForCurrentDeletionCandidate = allCandidates.get(currentSelected);

        try {
            File dummyFile = new File(url.getPath());
            doc = Jsoup.parse(dummyFile, "UTF-8");
            doc.outputSettings().prettyPrint(false);

            Element element_relevance = doc.getElementById(queryKind.toString().toLowerCase());
            element_relevance.attr("class", "");

            Elements elements_FileA = doc.getElementsByClass("fileA");
            for (Element eachFileA : elements_FileA) {
                eachFileA.text(currentSelected.getName());
            }

            //reasons according to file itself
            if (reasonForCurrentDeletionCandidate.containsKey(currentSelected)) {
                Element ul_itself = doc.getElementsByClass("itself").first();

                List<String> reasons_olderThanOneYear = reasonForCurrentDeletionCandidate.get(currentSelected);
                for (String reason : reasons_olderThanOneYear) {
                    Element elem_reason = ul_itself.appendElement("li");
                    elem_reason.attr("id", reason);

                    Element elem_spanFileA = elem_reason.appendElement("span");
                    elem_spanFileA.attr("class", "fileA");
                    elem_spanFileA.text(currentSelected.getName());

                    elem_reason.appendText(" is " + reason.replace("_", " "));
                }
            } else {
                Element ul_itself = doc.getElementsByClass("itself").first();
                ul_itself.remove();
            }

            //reasons according to other files
            for (DetailedFile reason_detailedFile : reasonForCurrentDeletionCandidate.keySet()) {
                if (reason_detailedFile != currentSelected) {
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
            deletionModel.myLogger.warning("[DeletionReasonPane] Exception in fillReasonHTML(): " + e.getMessage());
            throw new IllegalArgumentException();
        }

        return doc;
    }

    public void update(Observable observable, Object object) {
        deletionModel.myLogger.info("[DeletionReasonPane] update().");

        if (object instanceof DetailedFile) {
            if (deletionModel.getCurrentSelectedDeletionCandidate() != null) {
                showDel();
            } else if (deletionModel.getCurrentSelectedNearMissCandidate() != null) {
                showNearMiss();
            }
        }
    }
}
