package amt.software.vistacontrolador;

import amt.software.modelo.Registro;
import javafx.application.Platform;
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
import java.util.List;
import java.util.Optional;

public class ActualizaRegistro {

    @FXML
    private ComboBox<String> comboBox;
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

    private ControladorPrincipal controladorPrincipal;

    private Registro registro;

    public void setOwner(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        registro = controladorPrincipal.getSelectedRecords().get(0);
        this.campoDia.setText(String.valueOf(registro.getDia()));
        this.campoCliente.setText(registro.getCliente());
        this.vehiculo.setText(registro.getVehiculo());
        this.campoPlacas.setText(registro.getPlacas());
        this.campoTarifa.setText(String.valueOf(registro.getTarifa()));
    }

    public void initialize() {
        Task<ObservableList<String>> task1 = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().listarClausulas()
                );
            }
        };
        comboBox.itemsProperty().bind(task1.valueProperty());
        new Thread(task1).start();
    }


    public void processResult() throws Exception {
        Integer id = registro.getId();
        Integer dia = Integer.parseInt(campoDia.getText());
        String cliente = campoCliente.getText();
        String vehiculo = this.vehiculo.getText();
        String placas = campoPlacas.getText();
        Double tarifa = Double.parseDouble(campoTarifa.getText());
        String clausula = comboBox.getSelectionModel().getSelectedItem();
        if (cliente.trim().length() == 0 || vehiculo.trim().length() == 0 || tarifa.equals(null)
                || (dia < 1 || dia > 31) || tarifa < 0 || clausula.length() == 0) {
            throw new Exception();
        }
        try {
            GestorBD.getInstancia().actualizarRegistroIndividual(id, dia, cliente, vehiculo, placas, tarifa, clausula);
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
