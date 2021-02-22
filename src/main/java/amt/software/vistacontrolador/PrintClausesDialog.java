package amt.software.vistacontrolador;

import amt.software.modelo.GestorBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class PrintClausesDialog {

    @FXML
    private ComboBox<Integer> comboBox;
    @FXML
    private ComboBox<String> comboClause;

    public void initialize() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarClausulas());
            }
        };
        comboClause.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public void setComboBox(ObservableList<Integer> integers) {
        comboBox.setItems(integers);
        comboBox.getSelectionModel().select(0);
    }

    public Integer getInt() {
        return comboBox.getValue();
    }
    public String getTexto() {
        return comboClause.getValue();
    }
}
