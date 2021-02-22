package amt.software.impresion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import amt.software.modelo.Registro;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class Record extends HBox {

    @FXML
    private Label dia;
    @FXML
    private Label vehiculo;
    @FXML
    private Label placas;
    @FXML
    private Label tarifa;
    @FXML
    private Label cliente;

    public Record(Registro r) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader(
                cl.getResource("Record.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        this.dia.setText(String.valueOf(r.getDia()));
        this.vehiculo.setText(r.getVehiculo());
        this.placas.setText(r.getPlacas());
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        this.tarifa.setText(format.format(r.getTarifa()));
        this.cliente.setText(r.getCliente());
    }

    public String getDia() {
        return dia.getText();
    }

    public String getVehiculo() {
        return vehiculo.getText();
    }

    public String getPlacas() {
        return placas.getText();
    }

    public String getTarifa() {
        return tarifa.getText();
    }

    public String getCliente() {
        return cliente.getText();
    }

    @Override
    public String toString() {
        return getCliente();
    }
}
