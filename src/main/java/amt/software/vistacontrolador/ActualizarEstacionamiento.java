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

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Optional;

public class ActualizarEstacionamiento {

    @FXML
    private TextField textField;
    @FXML
    private ComboBox<String> comboBox;

    public void initialize() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarEstacionamientos());

            }
        };
        comboBox.itemsProperty().bind(task.valueProperty());
        comboBox.getSelectionModel().selectFirst();
        new Thread(task).run();
    }

    public void processResult() throws InvalidParameterException {
        String actual = comboBox.getSelectionModel().getSelectedItem();
        String nuevo = textField.getText();
        if (nuevo.trim().length() == 0) {
            throw new InvalidParameterException();
        }
        try {
            GestorBD.getInstancia().actualizarEstacionamiento(actual, nuevo);
        }
        catch (SQLException e) {
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
