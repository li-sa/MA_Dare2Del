package dare2del.gui.view.Tabs;

import dare2del.gui.model.DeletionDecision;
import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Messages;
import dare2del.logic.DetailedFile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

class DeletionCandidateListCell extends ListCell<DetailedFile> implements Observer {

    private final DeletionModel deletionModel;

    private DetailedFile detailedFile;

    private final BorderPane borderPane;
    private final Label filenameLabel;

    private DeletionDecision decision = DeletionDecision.DONTKNOW;
    private final Image DELETE_DECISION_ICON = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icons/delete.png")).toExternalForm());
    private final Image KEEP_DECISION_ICON = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icons/keep.png")).toExternalForm());
    private final Image DONT_KNOW_DECISION_ICON = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icons/dont-know.png")).toExternalForm());

    public DeletionCandidateListCell(DeletionModel deletionModel) {
        super();
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        this.setOnMouseClicked(arg0 -> {
//                deletionReasonController.showDeletionReasonStage();
        });

        Button cycleDecisionButton = createCycleDecisionButton();
        Button showReasonButton = createShowReasonButton();

        filenameLabel = new Label();
        filenameLabel.setPadding(new Insets(0, 10, 0, 10));
        filenameLabel.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS); // not obvious enough?

        borderPane = new BorderPane();
        borderPane.setLeft(cycleDecisionButton);
        borderPane.setCenter(filenameLabel);
        borderPane.setRight(showReasonButton);
        BorderPane.setAlignment(filenameLabel, Pos.CENTER_LEFT);

        setText(null);
    }

    public void update(Observable observable, Object object) {

    }

    private Button createCycleDecisionButton() {
        Button button = new Button();
        button.setGraphic(new ImageView(DONT_KNOW_DECISION_ICON));
        button.setOnMouseClicked(event -> {
            // This just cycles through the icons right now, the decision is not stored in any way.
            switch (decision) {
                case DONTKNOW:
                    decision = DeletionDecision.DELETE;
                    button.setGraphic(new ImageView(DELETE_DECISION_ICON));
                    break;
                case DELETE:
                    decision = DeletionDecision.KEEP;
                    button.setGraphic(new ImageView(KEEP_DECISION_ICON));
                    break;
                case KEEP:
                    decision = DeletionDecision.DONTKNOW;
                    button.setGraphic(new ImageView(DONT_KNOW_DECISION_ICON));
                    break;
            }
        });
        return button;
    }

    private Button createShowReasonButton() {
        Button button_deletion_explain = new Button(Messages.getString("DeletionCandidateListCell.explainButtonSimple"));
        button_deletion_explain.setOnAction(event -> {
            deletionModel.resetCurrentChoices();
            deletionModel.setCurrentSelectedDeletionCandidate(detailedFile);
        });

        return button_deletion_explain;
    }

    public void updateItem(DetailedFile item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            this.detailedFile = item;
            String filePath = item.getPath().toString().replace(deletionModel.getRootPath().toString(), "");
            filenameLabel.setText(filePath);
            filenameLabel.setTooltip(new Tooltip(item.getPath().toString()));
            setGraphic(borderPane);
        }
    }
}