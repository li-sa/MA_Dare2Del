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
    }

    private void showDel() {
        deletionModel.myLogger.info("[DeletionReasonPane] showDel().");

//        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../../deletion_dummy.html";
        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "deletion_dummy.html";

        DetailedFile currentSelected = deletionModel.getCurrentSelectedDeletionCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getDeletionPairs_grouped();

        Document reasonDoc = fillReasonHTML(pathString, currentSelected, allCandidates, QueryKind.IRRELEVANT);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private void showNearMiss() {
        deletionModel.myLogger.info("[DeletionReasonPane] showNearMiss().");

//        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../../nearmiss_dummy.html";
        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "nearmiss_dummy.html";

        DetailedFile currentSelected = deletionModel.getCurrentSelectedNearMissCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getNearMissPairs_grouped();

        Document reasonDoc = fillReasonHTML(pathString, currentSelected, allCandidates, QueryKind.NEARMISS);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private Document fillReasonHTML(String pathString, DetailedFile currentSelected, HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates, QueryKind queryKind) {
        deletionModel.myLogger.info("[DeletionReasonPane] fillReasonHTML(), path to dummy.html: " + pathString);

        Document doc;

        if (currentSelected == null) {
            return null;
        }

        HashMap<DetailedFile, List<String>> reasonForCurrentDeletionCandidate = allCandidates.get(currentSelected);

        try {
            File dummyFile = new File(pathString);
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
                    appendReasonElement(currentSelected, ul_itself, reason, "fileA");
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
                        appendReasonElement(reason_detailedFile, ul_another, reason, "fileB");
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

    private void appendReasonElement(DetailedFile reason_detailedFile, Element ul_another, String reason, String fileB) {
        Element elem_reason = ul_another.appendElement("li");
        elem_reason.attr("id", reason);

        Element elem_spanFileB = elem_reason.appendElement("span");
        elem_spanFileB.attr("class", fileB);
        elem_spanFileB.text(reason_detailedFile.getName());

        elem_reason.appendText(" is " + reason.replace("_", " "));
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
