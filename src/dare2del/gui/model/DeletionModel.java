package dare2del.gui.model;

import dare2del.logic.*;
import dare2del.logic.prolog.PrologFileWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DeletionModel extends Observable {

    public final Logger myLogger;
    private DeletionService deletionService;
    private final PrologFileWriter prologFileWriter;

    private Path rootPath;

    private List<DetailedFile> fileList;
    private DetailedFile currentSelectedDeletionCandidate;
    private DetailedFile currentSelectedNearMissCandidate;
    private List<DetailedFile> filesSelectedForDeletion;

    private ObservableList<DetailedFile> deletionCandidates;
    private ObservableList<DetailedFile> nearMissCandidates;

    private HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs_grouped;
    private HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nearMissPairs_grouped;

    public DeletionModel() {
        myLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        initLogger();

        prologFileWriter = new PrologFileWriter(myLogger);

        this.fileList = new ArrayList<>();
    }

    public void initDeletionModel() {
        deletionService = new DeletionService(this);

        this.deletionPairs_grouped = deletionService.getCandidatesWithReasoning(QueryKind.IRRELEVANT);
        this.nearMissPairs_grouped = deletionService.getCandidatesWithReasoning(QueryKind.NEARMISS);

        List<DetailedFile> deletionCandidateList = new ArrayList<>(deletionPairs_grouped.keySet());
        List<DetailedFile> nearmissCandidateList = new ArrayList<>(nearMissPairs_grouped.keySet());
        this.deletionCandidates = FXCollections.observableList(deletionCandidateList);
        this.nearMissCandidates = FXCollections.observableList(nearmissCandidateList);

        this.filesSelectedForDeletion = new ArrayList<>();
    }

    public void resetCurrentChoices() {
        this.currentSelectedDeletionCandidate = null;
        this.currentSelectedNearMissCandidate = null;

        this.setChanged();
        notifyObservers(currentSelectedDeletionCandidate);
    }

    public boolean confirmDeletion() {
        myLogger.info("[DeletionModel] deleteAllSelectedFiles().");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete all selected files?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            return deletionService.deleteSelectedFiles();
        } else {
            return false;
        }
    }

    private void initLogger() {
        File logDir = new File("./logs/");

        if (!(logDir.exists()))
            logDir.mkdir();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        FileHandler fh = null;
        try {
            fh = new FileHandler("logs/dar2del_log_" + dtf.format(localDate) + ".log", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(fh).setFormatter(new SimpleFormatter());
        myLogger.addHandler(fh);
    }

    public void updateFilesForDeletion(DetailedFile detailedFile, boolean toDelete) {
        if (toDelete && !filesSelectedForDeletion.contains(detailedFile)) {
            filesSelectedForDeletion.add(detailedFile);
        } else if (!toDelete) {
            filesSelectedForDeletion.remove(detailedFile);
        }

        this.setChanged();
        this.notifyObservers(filesSelectedForDeletion);
    }

    public List<DetailedFile> getFilesSelectedForDeletion() {
        return filesSelectedForDeletion;
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

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    private void setFileList(List<DetailedFile> fileList) {
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

    public void initProlog(Path rootPath) {
        FileCrawler fileCrawler = new FileCrawler(rootPath, myLogger);
        List<DetailedFile> fileList = fileCrawler.getFileList();
        prologFileWriter.write(fileList);
        setFileList(fileList);
    }
}
