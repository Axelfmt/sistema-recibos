package amt.software.vistacontrolador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import amt.software.modelo.GestorBD;

import java.sql.SQLException;
import java.util.Optional;

public class ActualizarTarifas {
    
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField tarifaActual;
    @FXML
    private TextField nuevaTarifa;
    
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
        Integer estacionamiento = GestorBD.getInstancia().getId(comboBox.getValue());
        Double tarifaActual = Double.parseDouble(this.tarifaActual.getText());
        Double nuevaTarifa = Double.parseDouble(this.nuevaTarifa.getText());
        if (estacionamiento == null || tarifaActual.equals(null) || nuevaTarifa.equals(null)
            || tarifaActual < 0 || nuevaTarifa < 0) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().actualizaTarifas(estacionamiento, tarifaActual, nuevaTarifa);
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error inesperado al intentar actualizar " +
                    "tarifa en la base de datos");
            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent()) {
                alert.close();
            }
        }
    }
}
