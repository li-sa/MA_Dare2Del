package dare2del.gui.view.Tabs;

import dare2del.gui.model.DeletionModel;
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

class NearMissCandidateListCell extends ListCell<DetailedFile> implements Observer {
    private final DeletionModel deletionModel;

    private DetailedFile detailedFile;

    private final BorderPane borderPane;
    private final Label filenameLabel;

    private final Image QUESTION_MARK_ICON = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icons/question-mark.png")).toExternalForm());

    public NearMissCandidateListCell(DeletionModel deletionModel) {
        super();
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        this.setOnMouseClicked(arg0 -> {
            // TODO
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

    public void update(Observable observable, Object object) {

    }

    private Button createShowReasonButton() {
        Button button_nearMiss_explain = new Button();
        button_nearMiss_explain.setGraphic(new ImageView(QUESTION_MARK_ICON));
        button_nearMiss_explain.setOnAction(event -> {
            deletionModel.resetCurrentChoices();
            deletionModel.setCurrentSelectedNearMissCandidate(detailedFile);
        });
        return button_nearMiss_explain;
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
            filenameLabel.setTooltip(new Tooltip(filePath));
            setGraphic(borderPane);
        }
    }
}
