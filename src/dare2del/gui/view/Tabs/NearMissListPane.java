package dare2del.gui.view.Tabs;

import dare2del.gui.model.DeletionModel;
import dare2del.gui.view.Messages;
import dare2del.logic.DetailedFile;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class NearMissListPane extends VBox implements Observer {
    private DeletionModel deletionModel;

    private ListView<DetailedFile> nearMissCandidates;
    private List<ListCell<DetailedFile>> nearMissCandidatesCellList;
    private ReadOnlyObjectProperty<DetailedFile> selectedItem;

    public NearMissListPane(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        this.deletionModel.addObserver(this);

        nearMissCandidatesCellList = new ArrayList<>();

        init();
    }

    public void update(Observable observable, Object object) {

    }

    public void init() {
        Label label_nearMisses = new Label(Messages.getString("NearMissListPane.topLabel"));
        label_nearMisses.setPadding(new Insets(10, 10, 10, 10));

        nearMissCandidates = new ListView<>(deletionModel.getNearMissCandidates());
        nearMissCandidates.setCellFactory(callback -> {
            ListCell<DetailedFile> cell = new NearMissCandidateListCell(deletionModel);
            cell.setPrefWidth(this.getWidth());
            nearMissCandidatesCellList.add(cell);
            return cell;
        });

        this.getChildren().addAll(label_nearMisses, nearMissCandidates);
        this.selectedItem = nearMissCandidates.getSelectionModel().selectedItemProperty();

    }

    public ReadOnlyObjectProperty<DetailedFile> getSelectedItemProperty() {
        return selectedItem;
    }

    public DetailedFile getSelectedItem() {
        return selectedItem.get();
    }


}
