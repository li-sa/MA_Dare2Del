package dare2del.gui.view.Tabs;

import dare2del.gui.controller.MainWindowController;
import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Messages;
import dare2del.logic.DetailedFile;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class NearMissListPane extends VBox {
    private DeletionModel deletionModel;
    private MainWindowController mainWindowController;
    private ReadOnlyObjectProperty<DetailedFile> selectedItem;


    public NearMissListPane(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
        this.deletionModel = mainWindowController.deletionModel;
        init();
    }

    public void init() {
        Label label_nearMisses = new Label(Messages.getString("NearMissListPane.topLabel"));
        label_nearMisses.setPadding(new Insets(10, 10, 10, 10));

        ListView<DetailedFile> deletionCandidates = new ListView<>(deletionModel.getNearMissCandidates());
        deletionCandidates.setCellFactory(callback -> {
            ListCell<DetailedFile> cell = new NearMissCandidateListCell(mainWindowController);
            cell.setPrefWidth(this.getWidth());
            return cell;
        });

        this.getChildren().addAll(label_nearMisses, deletionCandidates);
        this.selectedItem = deletionCandidates.getSelectionModel().selectedItemProperty();

    }

    public ReadOnlyObjectProperty<DetailedFile> getSelectedItemProperty() {
        return selectedItem;
    }

    public DetailedFile getSelectedItem() {
        return selectedItem.get();
    }


}
