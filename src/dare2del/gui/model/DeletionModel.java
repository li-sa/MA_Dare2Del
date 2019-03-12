package dare2del.gui.model;

import dare2del.logic.DeletionService;
import dare2del.logic.DetailedFile;
import dare2del.logic.QueryKind;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

public class DeletionModel extends Observable {

    private DeletionService deletionService;

    private Path rootPath;

    private List<DetailedFile> fileList;
    private DetailedFile currentSelectedDeletionCandidate;
    private DetailedFile currentSelectedNearMissCandidate;

    private ObservableList<DetailedFile> deletionCandidates;
    private ObservableList<DetailedFile> nearMissCandidates;
    private HashMap<List<String>, List<String>> deletionCandidatePairsWithReasons;
    private HashMap<List<String>, List<String>> nearMissCandidatePairsWithReasons;

    public DeletionModel() {
        this.fileList = new ArrayList<>();
    }

    public void initDeletionModel() {
        this.deletionService = new DeletionService(this);

        this.deletionCandidates = FXCollections.observableList(deletionService.getCandidates(QueryKind.IRRELEVANT));
        this.nearMissCandidates = FXCollections.observableList(deletionService.getCandidates(QueryKind.RELEVANT));
        this.deletionCandidatePairsWithReasons = deletionService.getCandidatesWithReasoning(QueryKind.IRRELEVANT);
        this.nearMissCandidatePairsWithReasons = deletionService.getCandidatesWithReasoning(QueryKind.RELEVANT);
    }

    public void setCurrentSelectedDeletionCandidate(DetailedFile detailedFile) {
        this.currentSelectedDeletionCandidate = detailedFile;
        System.out.println(">> Current selected deletion candidate: " + detailedFile.getName());
    }

    public void setCurrentSelectedNearMissCandidate(DetailedFile detailedFile) {
        this.currentSelectedNearMissCandidate = detailedFile;
        System.out.println(">> Current selected near miss candidate: " + detailedFile.getName());
    }

    public ObservableList<DetailedFile> getCandidates() {
        return deletionCandidates;
    }

    public ObservableList<DetailedFile> getNearMissCandidates() {
        return nearMissCandidates;
    }

    public HashMap<List<String>, List<String>> getDeletionCandidatePairsWithReasons() {
        return deletionCandidatePairsWithReasons;
    }

    public HashMap<List<String>, List<String>> getNearMissCandidatePairsWithReasons() {
        return nearMissCandidatePairsWithReasons;
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
}
