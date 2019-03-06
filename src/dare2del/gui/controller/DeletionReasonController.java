package dare2del.gui.controller;

import dare2del.gui.view.DeletionReasonStage;
import dare2del.logic.DetailedFile;

public class DeletionReasonController {

    private DeletionReasonStage reasonStage;
    private DetailedFile deletionCandidate;

    public DeletionReasonController(DetailedFile deletionCandidate) {
        this.deletionCandidate = deletionCandidate;
        reasonStage = new DeletionReasonStage(this);
    }

    public void showDeletionReasonStage() {
        reasonStage.show();
    }

    public String getFilePath() {
        return deletionCandidate.getPath().toString();
    }

    public String getReason() {
        return "Test Explanation!";
    }

}
