package dare2del.gui.model;

import dare2del.logic.DeletionService;
import dare2del.logic.DetailedFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class DeletionModel {

    private DeletionService deletionService;
    private List<DetailedFile> fileList;
    private ObservableList<DetailedFile> deletionCandidates;

    public DeletionModel(List<DetailedFile> fileList) {
        this.fileList = fileList;
        this.deletionService = new DeletionService(this);
        this.deletionCandidates = FXCollections.observableList(deletionService.getDeletionCandidates());
    }

    public ObservableList<DetailedFile> getCandidates() {
        return deletionCandidates;
    }

    public List<DetailedFile> getFileList() {
        return fileList;
    }

}
