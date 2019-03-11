package dare2del.gui.controller;

import dare2del.gui.view.DeletionReasonPane;
import dare2del.logic.DetailedFile;

public class DeletionReasonController {

    private DeletionReasonPane reasonPane;
    private DetailedFile deletionCandidate;

    public DeletionReasonController(DetailedFile deletionCandidate) {
        this.deletionCandidate = deletionCandidate;
        reasonPane = new DeletionReasonPane(this);
        reasonPane.setDeletionReasonController(this);
    }

    public void showDeletionReasonStage() {
        reasonPane.show();
//        reasonStage.show();
    }

    public String getFilePath() {
        return deletionCandidate.getPath().toString();
    }

    public String getReason() {
        return "Test Explanation!";
    }

}
