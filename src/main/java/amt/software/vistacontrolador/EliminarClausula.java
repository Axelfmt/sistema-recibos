package amt.software.vistacontrolador;

import amt.software.modelo.GestorBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.util.Optional;

public class EliminarClausula {

    @FXML
    private ComboBox<String> comboBox;

    public void initialize() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarClausulas());
            }
        };
        comboBox.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public void processResults() throws Exception {
        String id = comboBox.getSelectionModel().getSelectedItem();
        if (id == null) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().eliminarClausula(id);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error inesperado al intentar guardar " +
                    "elemento en la base de datos");
            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent()) {
                alert.close();
            }
        }
    }
}
