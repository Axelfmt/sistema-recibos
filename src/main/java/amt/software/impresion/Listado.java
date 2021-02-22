package amt.software.impresion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Listado extends VBox {

    @FXML
    private Label estacionamiento;
    @FXML
    private Label mes;

    public Listado(String estacionamiento, String mes) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader(
                cl.getResource("Listado.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        this.estacionamiento.setText(estacionamiento);
        this.mes.setText(mes);
    }
}
