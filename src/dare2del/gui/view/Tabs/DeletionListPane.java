package dare2del.gui.view.Tabs;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Messages;
import dare2del.logic.DetailedFile;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DeletionListPane extends VBox implements Observer {

    private DeletionModel deletionModel;

    private ListView<DetailedFile> deletionCandidates;
    private List<ListCell<DetailedFile>> deletionCandidatesCellList;
    private ReadOnlyObjectProperty<DetailedFile> selectedItem;

    public DeletionListPane(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        deletionCandidatesCellList = new ArrayList<>();

        init();
    }

    public void update(Observable observable, Object object) {

    }

    public void init() {
        Label label_filesToDelete = new Label(Messages.getString("DeletionWindowStage.topLabel"));
        label_filesToDelete.setPadding(new Insets(10, 10, 10, 10));

        deletionCandidates = new ListView<>(deletionModel.getCandidates());
        deletionCandidates.setCellFactory(callback -> {
            ListCell<DetailedFile> cell = new DeletionCandidateListCell(deletionModel);
            cell.setPrefWidth(this.getWidth());
            deletionCandidatesCellList.add(cell);
            return cell;
        });

        this.getChildren().addAll(label_filesToDelete, deletionCandidates);
        this.selectedItem = deletionCandidates.getSelectionModel().selectedItemProperty();
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox();

        Button notNowButton = new Button(Messages.getString("DeletionWindowStage.cancelButton"));
        //notNowButton.setOnAction(event -> stage.close());

        Button confirmButton = new Button(Messages.getString("DeletionWindowStage.okButton"));
        //confirmButton.setOnAction(event -> stage.close());

        buttonBox.getChildren().addAll(notNowButton, confirmButton);
        return buttonBox;
    }

    public ReadOnlyObjectProperty<DetailedFile> getSelectedItemProperty() {
        return selectedItem;
    }

    public DetailedFile getSelectedItem() {
        return selectedItem.get();
    }

    public ListView<DetailedFile> getDeletionCandidates() {
        return deletionCandidates;
    }

    public List<ListCell<DetailedFile>> getDeletionCandidatesCellList() {
        return deletionCandidatesCellList;
    }
}