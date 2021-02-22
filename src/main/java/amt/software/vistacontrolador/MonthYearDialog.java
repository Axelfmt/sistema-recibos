package amt.software.vistacontrolador;

import amt.software.impresion.Clause_2;
import amt.software.impresion.ReceiptFormat;
import amt.software.modelo.GestorBD;
import amt.software.modelo.ImporteATexto;
import amt.software.modelo.Registro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonthYearDialog {

    @FXML
    private ComboBox<String> mes;
    @FXML
    private TextField year;

    private ControladorPrincipal controladorPrincipal;

    public void setOwner(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public void initialize() {
        Integer year = LocalDateTime.now().getYear();
        year = year % 100;
        this.year.setText(String.valueOf(year));
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().getMeses()
                );
            }
        };
        mes.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public void imprimirRecibo() throws IOException {
        List<Registro> selectedItems = controladorPrincipal.getSelectedRecords();
        VBox formatList = new VBox();
        List<VBox> items = new ArrayList<>();
        int counter = 0;
        for (Registro registro : selectedItems) {
            Integer serie = GestorBD.getInstancia().getMaxSerie();
            GestorBD.getInstancia().incrementaSerie(serie + 1);
            System.out.println("id cliente = " + registro.getId());
            System.out.println("id clausula = " + registro.getClausula());
            Double importeDouble = registro.getTarifa();
            String placas = registro.getPlacas();
            String vehiculo = registro.getVehiculo();
            String cliente = registro.getCliente();
            String importeString = ImporteATexto.getInstancia().cantidadConLetra(
                    String.valueOf(importeDouble));
            Integer dia = registro.getDia();
            String mes = this.mes.getValue();
            Integer year = Integer.valueOf(this.year.getText());
            String estacionamiento = controladorPrincipal.getEstacionamientoSelected();
            ReceiptFormat formato = new ReceiptFormat(
                    serie, importeDouble, placas,
                    vehiculo, cliente, importeString,
                    dia, mes, year, estacionamiento, 171);
            ReceiptFormat copy = formato.getCopy();
            if (counter % 2 == 1) {
                formatList.getChildren().addAll(formato, copy);
                items.add(formatList);
                counter++;
            } else if (counter % 2 == 0) {
                if (registro.getId() == selectedItems.get(selectedItems.size() - 1).getId()) {
                    formatList = new VBox();
                    formatList.getChildren().addAll(formato, copy);
                    items.add(formatList);
                } else {
                    formatList = new VBox();
                    formatList.getChildren().addAll(formato, copy);
                    counter++;
                }
            }

        }
        doPrint(items);
    }


    public void imprimeDobleCara() throws IOException {

        PrinterJob printerJob = Objects.requireNonNull(PrinterJob.createPrinterJob(), "Error al crear printer");
        JobSettings settings = printerJob.getJobSettings();
        PageLayout pageLayout = settings.getPageLayout();
        double indvHeight = (pageLayout.getPrintableHeight() / 4);

        List<Registro> selectedItems = controladorPrincipal.getSelectedRecords();
        VBox copiaPortada = new VBox();
        VBox copiaContra = new VBox();
        VBox originalPortada = new VBox();
        VBox originalContra = new VBox();
        List<VBox> items = new ArrayList<>();
        int counter = 1;
        for (Registro registro : selectedItems) {
            Integer serie = GestorBD.getInstancia().getMaxSerie();
            GestorBD.getInstancia().incrementaSerie(serie + 1);
            System.out.println("id cliente = " + registro.getId());
            System.out.println("id clausula = " + registro.getClausula());
            Double importeDouble = registro.getTarifa();
            String placas = registro.getPlacas();
            String vehiculo = registro.getVehiculo();
            String cliente = registro.getCliente();
            String importeString = ImporteATexto.getInstancia().cantidadConLetra(
                    String.valueOf(importeDouble));
            Integer dia = registro.getDia();
            String mes = this.mes.getValue();
            Integer year = Integer.valueOf(this.year.getText());
            String estacionamiento = controladorPrincipal.getEstacionamientoSelected();
            ReceiptFormat formato = new ReceiptFormat(
                    serie, importeDouble, placas,
                    vehiculo, cliente, importeString,
                    dia, mes, year, estacionamiento, indvHeight);
            ReceiptFormat copy = formato.getCopy();
            if (counter % 4 != 0) {
                copiaPortada.getChildren().add(copy);
                originalPortada.getChildren().add(formato);
                String clausula = GestorBD.getInstancia().getClausulas(registro.getClausula());
                originalContra.getChildren().add(new Clause_2(clausula, indvHeight));
                if (registro.getId() == selectedItems.get(selectedItems.size() - 1).getId()) {
                    items.add(originalPortada);
                    items.add(originalContra);
                    items.add(copiaPortada);
                    items.add(copiaContra);
                }
                counter++;
            } else {
                if (registro.getId() == selectedItems.get(selectedItems.size() - 1).getId()) {
                    copiaPortada.getChildren().add(copy);
                    originalPortada.getChildren().add(formato);
                    String clausula = GestorBD.getInstancia().getClausulas(registro.getClausula());
                    originalContra.getChildren().add(new Clause_2(clausula, indvHeight));
                    items.add(originalPortada);
                    items.add(originalContra);
                    items.add(copiaPortada);
                    items.add(copiaContra);
                } else {
                    copiaPortada.getChildren().add(copy);
                    originalPortada.getChildren().add(formato);
                    String clausula = GestorBD.getInstancia().getClausulas(registro.getClausula());
                    originalContra.getChildren().add(new Clause_2(clausula, indvHeight));
                    items.add(originalPortada);
                    items.add(originalContra);
                    items.add(copiaPortada);
                    items.add(copiaContra);
                    copiaPortada = new VBox();
                    copiaContra = new VBox();
                    originalPortada = new VBox();
                    originalContra = new VBox();
                    counter++;
                }
            }

        }
//        doPrint(items);
        printerJob.showPrintDialog(null);
        for (VBox item: items) {
            printerJob.printPage(item);
        }
        printerJob.endJob();
    }

    /*public static boolean doPrint(List<VBox> items) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) return false;
        if (!job.showPrintDialog(null)) return false;
        for (VBox box : items) {
            if (!job.printPage(box)) return false;
        }
        return job.endJob();
    }*/

    public static void doPrint(List<VBox> items) {

        PrinterJob printerJob = Objects.requireNonNull(PrinterJob.createPrinterJob(), "Error al crear printer");
        if (!printerJob.showPrintDialog(null))
            return;
        for (VBox box : items) {
            JobSettings settings = printerJob.getJobSettings();
            PageLayout pageLayout = settings.getPageLayout();
            ObservableList<Node> children = box.getChildren();
            System.out.println("->"+pageLayout.getPrintableWidth());
            for (Node child : children) {
                child.maxWidth(pageLayout.getPrintableWidth()-200);
            }
            box.setPrefWidth(pageLayout.getPrintableWidth());
            box.setPrefHeight(pageLayout.getPrintableHeight());
            if (!printerJob.printPage(box)) return;
        }
        printerJob.endJob();
    }
}













