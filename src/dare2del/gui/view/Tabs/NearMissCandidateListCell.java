package dare2del.gui.view.Tabs;

import dare2del.gui.controller.DeletionReasonController;
import dare2del.gui.controller.MainWindowController;
import dare2del.gui.model.DeletionDecision;
import dare2del.gui.view.Messages;
import dare2del.logic.DetailedFile;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class NearMissCandidateListCell extends ListCell<DetailedFile> {
    private MainWindowController mainWindowController;
    private DeletionReasonController reasonController;

    private BorderPane borderPane;
    private Label filenameLabel;

    private DeletionDecision decision = DeletionDecision.DONTKNOW;
    // TODO: Refactor
    private final Image DELETE_DECISION_ICON = new Image("file:resources/icons/delete.png");
    private final Image KEEP_DECISION_ICON = new Image("file:resources/icons/keep.png");
    private final Image DONT_KNOW_DECISON_ICON = new Image("file:resources/icons/dont-know.png");

    public NearMissCandidateListCell(MainWindowController mainWindowController) {
        super();
        this.mainWindowController = mainWindowController;

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                // TODO Auto-generated method stub
                // Just do nothing?
            }
        });

        Button showReasonButton = createShowReasonButton();

        filenameLabel = new Label();
        filenameLabel.setPadding(new Insets(0, 10, 0, 10));
        filenameLabel.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS); // not obvious enough?

        borderPane = new BorderPane();
        borderPane.setCenter(filenameLabel);
        borderPane.setRight(showReasonButton);
        BorderPane.setAlignment(filenameLabel, Pos.CENTER_LEFT);

        setText(null);
    }

    private Button createShowReasonButton() {
        Button button = new Button(Messages.getString("DeletionCandidateListCell.explainButton"));
        button.setOnMouseClicked(event -> reasonController.showDeletionReasonStage());
        return button;
    }

    public void updateItem(DetailedFile item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            reasonController = new DeletionReasonController(item);
            String filePath = item.getPath().toString().replace(mainWindowController.getRootPath().toString(), "");
            filenameLabel.setText(filePath);
            filenameLabel.setTooltip(new Tooltip(filePath));
            setGraphic(borderPane);
        }
    }
}
