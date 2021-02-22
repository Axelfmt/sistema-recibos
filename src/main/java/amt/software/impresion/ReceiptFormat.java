package amt.software.impresion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ReceiptFormat extends AnchorPane {

    @FXML
    private Label labelSerie;
    @FXML
    private Label labelImporteDouble;
    @FXML
    private Label labelPlacas;
    @FXML
    private Label labelVehiculo;
    @FXML
    private Label labelCliente;
    @FXML
    private Label labelImporteString;
    @FXML
    private Label labelDia;
    @FXML
    private Label labelMes;
    @FXML
    private Label labelYear;
    @FXML
    private Label labelEstacionamiento;
    @FXML
    private Label tipoRecibo;

    private ReceiptFormat copy;
    private double height;


    public ReceiptFormat(Integer serie, Double importeDouble, String placas, String vehiculo, String cliente,
                         String importeString, Integer dia, String mes, Integer year, String estacionamiento, double height) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(
                cl.getResource("ReceiptFormat.fxml")
        );
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        labelSerie.setText(String.valueOf(serie));
        labelImporteDouble.setText(String.valueOf(importeDouble));
        labelPlacas.setText(placas);
        labelVehiculo.setText(vehiculo);
        labelCliente.setText(cliente);
        labelImporteString.setText(importeString);
        labelDia.setText(String.valueOf(dia));
        labelMes.setText(mes);
        labelYear.setText(String.valueOf(year));
        labelEstacionamiento.setText(estacionamiento);
        this.height = height;
        this.setPrefHeight(this.height);
    }

    public ReceiptFormat getCopy() throws IOException {
        Integer serie = Integer.valueOf(labelSerie.getText());
        Double importeDouble = Double.valueOf(labelImporteDouble.getText());
        String placas = labelPlacas.getText();
        String vehiculo = labelVehiculo.getText();
        String cliente = labelCliente.getText();
        String importeString = labelImporteString.getText();
        Integer dia = Integer.valueOf(labelDia.getText());
        String mes = labelMes.getText();
        Integer year = Integer.valueOf(labelYear.getText());
        String estacionamiento = labelEstacionamiento.getText();
        copy = new ReceiptFormat(serie, importeDouble, placas, vehiculo,
                cliente, importeString, dia, mes, year, estacionamiento, this.height);
        copy.setLabelTipoRecibo();
        return copy;
    }

    private void setLabelTipoRecibo() {
        this.tipoRecibo.setText("COPIA ADMINISTRACION");
    }
}
