package amt.software.vistacontrolador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import amt.software.modelo.GestorBD;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Optional;

public class NuevoEstacionamientoDialogo {

    @FXML
    private TextField textField;

    public void processResults() throws InvalidParameterException{
        String nombre = textField.getText();
        if (nombre.trim().length() == 0) {
            throw new InvalidParameterException();
        }
        try {
            GestorBD.getInstancia().crearEstacionamiento(nombre);
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
