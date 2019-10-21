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

class DeletionReasonPane extends VBox implements Observer {

    private final DeletionModel deletionModel;

    private final WebEngine webEngine;

    public DeletionReasonPane(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        WebView browser = new WebView();
        webEngine = browser.getEngine();
        this.getChildren().add(browser);

    }

    private void showDel() {
        DetailedFile currentSelected = deletionModel.getCurrentSelectedDeletionCandidate();
        Document reasonDoc;

        deletionModel.myLogger.info("[DeletionReasonPane] showDel().");

        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../htmlFiles/deletion_dummy.html";
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, List<String>> reasonForCurrentCandidate = allCandidates.get(currentSelected);

        reasonDoc = fillReasonHTML(pathString, currentSelected, reasonForCurrentCandidate, QueryKind.IRRELEVANT);


        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private void showNearMiss() {
        deletionModel.myLogger.info("[DeletionReasonPane] showNearMiss().");

        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../htmlFiles/nearmiss_dummy.html";

        DetailedFile currentSelected = deletionModel.getCurrentSelectedNearMissCandidate();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allCandidates = deletionModel.getNearMissPairs_grouped();
        HashMap<DetailedFile, List<String>> reasonForCurrentCandidate = allCandidates.get(currentSelected);

        Document reasonDoc = fillReasonHTML(pathString, currentSelected, reasonForCurrentCandidate, QueryKind.NEARMISS);

        if (reasonDoc != null) {
            String textInDoc = reasonDoc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    private Document fillReasonHTML(String pathString, DetailedFile currentSelected, HashMap<DetailedFile, List<String>> reasonForCurrentCandidate, QueryKind queryKind) {
        deletionModel.myLogger.info("[DeletionReasonPane] fillReasonHTML(), path to dummy.html: " + pathString);

        String queryKindString = queryKind.toString().toLowerCase();

        Document doc;

        if (currentSelected == null) {
            return null;
        }

        try {
            File dummyFile = new File(pathString);
            doc = Jsoup.parse(dummyFile, "UTF-8");
            doc.outputSettings().prettyPrint(false);

            Element element_relevance = doc.getElementById(queryKindString);

            Elements elements_FileA = doc.getElementsByClass("fileA");
            for (Element eachFileA : elements_FileA) {
                eachFileA.text(currentSelected.getName());
            }

            //reasons according to file itself
            if (reasonForCurrentCandidate.containsKey(currentSelected)) {
                Element ul_itself = doc.getElementsByClass("itself_" + queryKindString).first();

                List<String> reasons_olderThanOneYear = reasonForCurrentCandidate.get(currentSelected);
                for (String reason : reasons_olderThanOneYear) {
                    appendReasonElement(currentSelected, ul_itself, reason, "fileA");
                }
            } else {
                Element ul_itself = doc.getElementsByClass("itself_" + queryKindString).first();
                ul_itself.remove();
            }

            //reasons according to other files
            for (DetailedFile reason_detailedFile : reasonForCurrentCandidate.keySet()) {
                if (reason_detailedFile != currentSelected) {
                    Element ul_another = element_relevance.getElementsByClass("another_" + queryKindString).first().clone();

                    List<String> reasonList = reasonForCurrentCandidate.get(reason_detailedFile);
                    for (String reason : reasonList) {
                        appendReasonElement(reason_detailedFile, ul_another, reason, "fileB");
                    }

                    doc.getElementById(queryKindString).appendChild(ul_another);
                }
            }

            //reasoning by near miss in case of deletion candidate
            if (queryKind == QueryKind.IRRELEVANT) {
                doc = fillReasonHTML_NMsForDeletion(doc, currentSelected);
            }

        } catch (IOException e) {
            deletionModel.myLogger.warning("[DeletionReasonPane] Exception in fillReasonHTML(): " + e.getMessage());
            throw new IllegalArgumentException();
        }

        return doc;
    }

    private Document fillReasonHTML_NMsForDeletion(Document doc, DetailedFile currentSelected) {
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> correspondingNMs = findNearMissForSpecificDeletionCandidate(currentSelected);

        deletionModel.myLogger.info("[DeletionReasonPane] fillReasonHTML_NMsForDeletion().");

        Element element_nearmiss = doc.getElementById("nearmiss");

        if (correspondingNMs.isEmpty()) {
            element_nearmiss.children().remove();
            element_nearmiss.appendText("[No near miss for this file.]");
            return doc;
        }

        for (DetailedFile eachNM : correspondingNMs.keySet()) {
            Element element_each = element_nearmiss.getElementById("eachNM").clone();
            element_each.attr("id", "");

            Elements elements_FileA = element_each.getElementsByClass("fileA");
            for (Element eachFileA : elements_FileA) {
                eachFileA.attr("class", "fileB");
                eachFileA.text(eachNM.getName());
            }

            HashMap<DetailedFile, List<String>> reasonForCurrentCandidate = correspondingNMs.get(eachNM);

            for (DetailedFile reason_detailedFile : reasonForCurrentCandidate.keySet()) {
                Element ul_another = element_nearmiss.getElementsByClass("another_nearmiss").first().clone();

                List<String> reasonList = reasonForCurrentCandidate.get(reason_detailedFile);
                for (String reason : reasonList) {
                    appendReasonElement(reason_detailedFile, ul_another, reason, "fileA");
                }

                element_each.appendChild(ul_another);
            }

            element_nearmiss.appendChild(element_each);
        }


        doc.getElementById("eachNM").remove();
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

    private HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> findNearMissForSpecificDeletionCandidate(DetailedFile currentSelectedDeletionCandidate) {
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> allNearMissCandidates = deletionModel.getNearMissPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> correspondingNearMissCandidates = new HashMap<DetailedFile, HashMap<DetailedFile, List<String>>>();

        for (DetailedFile eachNM : allNearMissCandidates.keySet()) {
            HashMap<DetailedFile, List<String>> nm_candidate = allNearMissCandidates.get(eachNM);

            for (DetailedFile each : nm_candidate.keySet()) {
                if (each == currentSelectedDeletionCandidate) {
                    HashMap<DetailedFile, List<String>> correspondingNM = new HashMap<DetailedFile, List<String>>();
                    correspondingNM.put(currentSelectedDeletionCandidate, nm_candidate.get(each));
                    correspondingNearMissCandidates.put(eachNM, correspondingNM);
                }
            }
        }

        return correspondingNearMissCandidates;
    }

    private void clearReasonPane() {
        deletionModel.myLogger.info("[DeletionReasonPane] clearReasonPane().");
        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../htmlFiles/nearmiss_dummy.html";

        Document doc;
        try {
            File dummyFile = new File(pathString);
            doc = Jsoup.parse(dummyFile, "UTF-8");
            doc.outputSettings().prettyPrint(false);

            Elements allChildren = doc.getElementsByTag("body");
            for (Element each : allChildren) {
                each.remove();
            }

        } catch (IOException e) {
            deletionModel.myLogger.warning("[DeletionReasonPane] Exception in clearReasonPane(): " + e.getMessage());
            throw new IllegalArgumentException();
        }

        if (doc != null) {
            String textInDoc = doc.toString();
            webEngine.loadContent(textInDoc);
        }
    }

    public void update(Observable observable, Object object) {
        deletionModel.myLogger.info("[DeletionReasonPane] update().");

        if (object instanceof DetailedFile) {
            if (deletionModel.getCurrentSelectedDeletionCandidate() != null) {
                showDel();
            } else if (deletionModel.getCurrentSelectedNearMissCandidate() != null) {
                showNearMiss();
            }
        } else if (object == null) {
            clearReasonPane();
        }
    }
}
