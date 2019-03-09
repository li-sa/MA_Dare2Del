package dare2del.gui.model;

import dare2del.logic.DeletionService;
import dare2del.logic.DetailedFile;
import dare2del.logic.QueryKind;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;

public class DeletionModel {

    private DeletionService deletionService;
    private List<DetailedFile> fileList;
    private ObservableList<DetailedFile> deletionCandidates;
    private List<DetailedFile> nearMissCandidates;
    private HashMap<List<String>, List<String>> deletionCandidatePairsWithReasons;
    private HashMap<List<String>, List<String>> nearMissCandidatePairsWithReasons;

    public DeletionModel(List<DetailedFile> fileList) {
        this.fileList = fileList;
        this.deletionService = new DeletionService(this);
        this.deletionCandidates = FXCollections.observableList(deletionService.getCandidates(QueryKind.IRRELEVANT));
        this.nearMissCandidates = deletionService.getCandidates(QueryKind.RELEVANT);
        this.deletionCandidatePairsWithReasons = deletionService.getCandidatesWithReasoning(QueryKind.IRRELEVANT);
        this.nearMissCandidatePairsWithReasons = deletionService.getCandidatesWithReasoning(QueryKind.RELEVANT);
    }

    public ObservableList<DetailedFile> getCandidates() {
        return deletionCandidates;
    }

    public List<DetailedFile> getNearMissCandidates() {
        return nearMissCandidates;
    }

    public HashMap<List<String>, List<String>> getDeletionCandidatePairsWithReasons() {
        return deletionCandidatePairsWithReasons;
    }

    public HashMap<List<String>, List<String>> getNearMissCandidatePairsWithReasons() {
        return nearMissCandidatePairsWithReasons;
    }

    public List<DetailedFile> getFileList() {
        return fileList;
    }

}
