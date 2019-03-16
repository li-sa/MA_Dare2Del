package dare2del.gui.controller;

import dare2del.gui.view.DeletionReasonPane;
import dare2del.logic.DetailedFile;

public class DeletionReasonController {


    private DeletionReasonPane reasonPane;

    private DetailedFile deletionCandidate;

    public DeletionReasonController() {
//        reasonPane = new DeletionReasonPane();
//        reasonPane.setDeletionReasonController(this);
    }

    public void showDeletionReasonStage() {
//        reasonPane.show();
    }


    public void setDeletionCandidate(DetailedFile deletionCandidate) {
        this.deletionCandidate = deletionCandidate;
    }

    public String getFilePath() {
        return deletionCandidate.getPath().toString();
    }

    public String getReason() {
        return "Test Explanation!";
    }

}
