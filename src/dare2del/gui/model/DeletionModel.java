package dare2del.gui.model;

import dare2del.logic.DeletionService;
import dare2del.logic.DetailedFile;
import dare2del.logic.QueryKind;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.web.WebEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DeletionModel extends Observable {

    public final Logger myLogger;

    private Path rootPath;
    public WebEngine webEngine;

    private List<DetailedFile> fileList;
    private DetailedFile currentSelectedDeletionCandidate;
    private DetailedFile currentSelectedNearMissCandidate;

    private ObservableList<DetailedFile> deletionCandidates;
    private ObservableList<DetailedFile> nearMissCandidates;
    private HashMap<List<String>, List<String>> deletionPairs_reasoned;
    private HashMap<List<String>, List<String>> nearMissPairs_reasoned;

    private HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs_grouped;
    private HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nearMissPairs_grouped;

    public DeletionModel() {
        myLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        initLogger();

        this.fileList = new ArrayList<>();
    }

    public void initDeletionModel() {
        DeletionService deletionService = new DeletionService(this);

        this.deletionCandidates = FXCollections.observableList(deletionService.getCandidates(QueryKind.IRRELEVANT));
        this.nearMissCandidates = FXCollections.observableList(deletionService.getCandidates(QueryKind.RELEVANT));
        this.deletionPairs_reasoned = deletionService.getCandidatesWithReasoning(QueryKind.IRRELEVANT);
        this.nearMissPairs_reasoned = deletionService.getCandidatesWithReasoning(QueryKind.RELEVANT);

        this.deletionPairs_grouped = deletionService.getCandidatesWithReasoning_Grouped(QueryKind.IRRELEVANT);
        this.nearMissPairs_grouped = deletionService.getCandidatesWithReasoning_Grouped(QueryKind.RELEVANT);
    }

    public void initLogger() {
        File logDir = new File("./logs/");
        if( !(logDir.exists()) )
            logDir.mkdir();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        FileHandler fh = null;
        try {
            fh = new FileHandler("logs/dar2del_log_" + dtf.format(localDate) + ".log", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fh.setFormatter(new SimpleFormatter());
        myLogger.addHandler(fh);
    }

    public void setCurrentSelectedDeletionCandidate(DetailedFile detailedFile) {
        this.currentSelectedDeletionCandidate = detailedFile;
        myLogger.info("[DeletionModel] Current selected deletion candidate: " + detailedFile.getName());

        this.setChanged();
        this.notifyObservers(currentSelectedDeletionCandidate);
    }

    public DetailedFile getCurrentSelectedDeletionCandidate() {
        return currentSelectedDeletionCandidate;
    }

    public void setCurrentSelectedNearMissCandidate(DetailedFile detailedFile) {
        this.currentSelectedNearMissCandidate = detailedFile;
        myLogger.info("[DeletionModel] Current selected near miss candidate: " + detailedFile.getName());

        this.setChanged();
        this.notifyObservers(currentSelectedNearMissCandidate);
    }

    public DetailedFile getCurrentSelectedNearMissCandidate() {
        return currentSelectedNearMissCandidate;
    }

    public void resetFileList() {
        this.fileList = new ArrayList<>();
    }

    public ObservableList<DetailedFile> getCandidates() {
        return deletionCandidates;
    }

    public ObservableList<DetailedFile> getNearMissCandidates() {
        return nearMissCandidates;
    }

    public HashMap<List<String>, List<String>> getDeletionPairs_reasoned() {
        return deletionPairs_reasoned;
    }

    public HashMap<List<String>, List<String>> getNearMissPairs_reasoned() {
        return nearMissPairs_reasoned;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public void setFileList(List<DetailedFile> fileList) {
        this.fileList = fileList;
    }

    public List<DetailedFile> getFileList() {
        return fileList;
    }

    public HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> getDeletionPairs_grouped() {
        return deletionPairs_grouped;
    }

    public HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> getNearMissPairs_grouped() {
        return nearMissPairs_grouped;
    }

    public void resetCurrentChoices() {
        this.currentSelectedDeletionCandidate = null;
        this.currentSelectedNearMissCandidate = null;
    }
}
