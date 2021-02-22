package amt.software.vistacontrolador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import amt.software.modelo.GestorBD;

import java.sql.SQLException;
import java.util.Optional;

public class EliminaRegistro {

    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    @FXML
    public void processResults() {
        try {
            GestorBD.getInstancia().eliminarRegistro(id);
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
