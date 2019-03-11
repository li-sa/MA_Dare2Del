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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * Custom {@code ListCell} class that shows a list element as a {@code Button}
 * cycling through icons on the left, a simple text {@code Label} in the middle
 * and a {@code Button} offering more information on the right.
 */
class DeletionCandidateListCell extends ListCell<DetailedFile> {

    private MainWindowController mainWindowController;
    private DeletionReasonController reasonController;

    private BorderPane borderPane;
    private Label filenameLabel;

    private DeletionDecision decision = DeletionDecision.DONTKNOW;
    private final Image DELETE_DECISION_ICON = new Image(getClass().getClassLoader().getResource("icons/delete.png").toExternalForm());
    private final Image KEEP_DECISION_ICON = new Image(getClass().getClassLoader().getResource("icons/keep.png").toExternalForm());
    private final Image DONT_KNOW_DECISON_ICON = new Image(getClass().getClassLoader().getResource("icons/dont-know.png").toExternalForm());

    public DeletionCandidateListCell(MainWindowController mainWindowController) {
        super();
        this.mainWindowController = mainWindowController;

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                // TODO Auto-generated method stub
                // Just do nothing?
            }
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

    private Button createCycleDecisionButton() {
        Button button = new Button();
        button.setGraphic(new ImageView(DONT_KNOW_DECISON_ICON));
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
                    button.setGraphic(new ImageView(DONT_KNOW_DECISON_ICON));
                    break;
            }
        });
        return button;
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
