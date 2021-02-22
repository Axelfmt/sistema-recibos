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

public class NuevoRegistroControlador {

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<String> comboBoxClausula;
    @FXML
    private TextField campoDia;
    @FXML
    private TextField campoCliente;
    @FXML
    private TextField vehiculo;
    @FXML
    private TextField campoPlacas;
    @FXML
    private TextField campoTarifa;

    @FXML
    public void initialize() {
        Task<ObservableList<String>> task1 = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().listarEstacionamientos()
                );
            }
        };
        comboBox.itemsProperty().bind(task1.valueProperty());
        new Thread(task1).start();
        Task<ObservableList<String>> task2 = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().listarClausulas()
                );
            }
        };
        comboBoxClausula.itemsProperty().bind(task2.valueProperty());
        new Thread(task2).start();
    }

    public void processResult() throws InvalidParameterException {
        String selectedBranch = comboBox.getSelectionModel().getSelectedItem();
        Integer dia = Integer.parseInt(campoDia.getText());
        String name = campoCliente.getText();
        String vehiculoText = vehiculo.getText();
        String placas = campoPlacas.getText();
        Double tarifa = Double.parseDouble(campoTarifa.getText());
        String clausula = comboBoxClausula.getSelectionModel().getSelectedItem();
        System.out.println(clausula);
        if (selectedBranch.trim().length() == 0 || name.trim().length() == 0
                || vehiculoText.trim().length() == 0 || tarifa.equals(null)
                || (dia < 1 || dia > 31) || tarifa < 0 || clausula.length() == 0) {
            throw new InvalidParameterException();
        }
        try {
            Integer id = GestorBD.getInstancia().getId(selectedBranch);
            GestorBD.getInstancia().crearRegistro(id, dia, name, vehiculoText, placas, tarifa, clausula);
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

    public String getCliente() {
        return this.campoCliente.getText();
    }

    public String getEstacionamiento() {
        return this.comboBox.getSelectionModel().getSelectedItem();
    }
}
