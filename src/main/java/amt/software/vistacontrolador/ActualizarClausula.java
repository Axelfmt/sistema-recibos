package amt.software.vistacontrolador;

import amt.software.modelo.GestorBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Optional;

public class ActualizarClausula {


    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField textField;
    @FXML
    private TextArea clause;

    public void initialize() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarClausulas());
            }
        };
        comboBox.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    @FXML
    public void update() {
        textField.setText(comboBox.getSelectionModel().getSelectedItem());
        clause.setText(GestorBD.getInstancia().getClausulas(comboBox.getSelectionModel().getSelectedItem()));
    }

    public void processResults() throws Exception {
        String idNuevo = textField.getText();
        String texto = clause.getText();
        String idOriginal = comboBox.getSelectionModel().getSelectedItem();
        if (idNuevo.length() == 0 || texto.length() == 0 || idOriginal.length() == 0) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().actualizarClausula(idNuevo, texto, idOriginal);
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
    public void eliminar() throws Exception {
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
