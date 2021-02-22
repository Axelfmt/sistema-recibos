package amt.software.vistacontrolador;

import amt.software.modelo.GestorBD;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class NuevaClausula {

    @FXML
    private TextField textField;
    @FXML
    private TextArea clause;

    @FXML
    public void processResults() throws Exception {
        String id = textField.getText();
        String texto = clause.getText();
        if (id.length() == 0 || texto.length() == 0) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().crearClausula(id, texto.toUpperCase());
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

    @FXML
    public String getClauseString() {
        return clause.getText();
    }

}
