package amt.software.vistacontrolador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import amt.software.modelo.GestorBD;

import java.sql.SQLException;
import java.util.Optional;

public class EliminarEstacionamiento {

    @FXML
    private ComboBox<String> comboBox;

    public void initialize() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarEstacionamientos());
            }
        };
        comboBox.itemsProperty().bind(task.valueProperty());
        new Thread(task).run();
    }

    public void processResult() throws Exception {
        String nombre = comboBox.getValue();
        Integer id = GestorBD.getInstancia().getId(nombre);
        if (nombre.equals(null)) {
            throw new Exception();
        }
        if (GestorBD.getInstancia().listarRegistrosPorEstacionamiento(id).size() >= 1) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().eliminaEstacionamiento(nombre);
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error inesperado al intentar eliminar " +
                    "elemento en la base de datos");
            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent()) {
                alert.close();
            }
        }
    }
}
