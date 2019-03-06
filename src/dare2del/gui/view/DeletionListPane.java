package dare2del.gui.view;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.DetailedFile;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DeletionListPane extends VBox {

    private DeletionModel deletionModel;
    private ReadOnlyObjectProperty<DetailedFile> selectedItem;


    public DeletionListPane() {
        this.deletionModel = new DeletionModel();
        init();
    }

    public void init() {
        Label uWannaDeleteTheseFiles = new Label("Which of these files shall be deleted?"); //TODO: Messages.getString()
        uWannaDeleteTheseFiles.setPadding(new Insets(10, 10, 10, 10));

        ListView<DetailedFile> deletionCandidates = new ListView<>(
                deletionModel.getCandidates());
        deletionCandidates.setCellFactory(callback -> {
            ListCell<DetailedFile> cell = new DeletionCandidateListCell();
            // Resize the cell width automatically depending on the window width
            cell.setPrefWidth(this.getWidth());
            return cell;
        });


        this.getChildren().addAll(uWannaDeleteTheseFiles, deletionCandidates);
        this.selectedItem = deletionCandidates.getSelectionModel().selectedItemProperty();

    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox();

        Button notNowButton = new Button("Not now"); //TODO: Messages.getString()
        //notNowButton.setOnAction(event -> stage.close());

        Button confirmButton = new Button("Delete marked files"); //TODO: Messages.getString()
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


}