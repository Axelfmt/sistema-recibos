package amt.software.vistacontrolador;

import amt.software.impresion.Listado;
import amt.software.impresion.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.ComboBox;
import amt.software.modelo.GestorBD;
import amt.software.modelo.Registro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintLists {

    @FXML
    private ComboBox<String> comboBoxEst;
    @FXML
    private ComboBox<String> comboBoxMes;

    public void initialize() {

        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().listarEstacionamientos()
                );
            }
        };
        comboBoxEst.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().getMeses()
                );
            }
        };
        comboBoxMes.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public void printResult() throws IOException {

        List<Listado> totalAImprimir = new ArrayList<>();
        List<Record> registros = new ArrayList<>();

        String estacionamiento = comboBoxEst.getValue();
        Integer id = GestorBD.getInstancia().getId(estacionamiento);
        List<Registro> registrosBD = GestorBD.getInstancia().listarRegistrosPorEstacionamiento(id);

        for (Registro registro : registrosBD) {
            Record record = new Record(registro);
            registros.add(record);
        }

        System.out.println(registros.size());
        Double numFormatos = Math.ceil(registros.size()/30d);
        Integer residuo = (registros.size() % 30);
        Integer vuelta = 1;
        Integer cuenta = 0;
        if (residuo == 0) {
            for (int i = 0; i < numFormatos; i++) {
                Listado l = new Listado(comboBoxEst.getValue(), comboBoxMes.getValue());
                for (int j = cuenta; j < (30*vuelta); j++) {
                    Record r = registros.get(j);
                    l.getChildren().add(r);
                    cuenta++;
                }
                totalAImprimir.add(l);
                vuelta++;
            }
        }
        else {
            int i = 0;
            while (i < numFormatos) {
                Listado l = new Listado(comboBoxEst.getValue(), comboBoxMes.getValue());
                if (i+1 == numFormatos) {
                    int x = cuenta + residuo;
                    for (int j = cuenta; j < x; j++) {
                        Record r = registros.get(j);
                        l.getChildren().add(r);
                        cuenta++;
                    }
                } else {
                    for (int k = cuenta; k < (30*vuelta); k++) {
                        Record r = registros.get(k);
                        l.getChildren().add(r);
                        cuenta++;
                    }
                }
                vuelta++;
                i++;
                totalAImprimir.add(l);
            }
        }
        doPrint(totalAImprimir);
        }

    public static boolean doPrint(List<Listado> items) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) return false;
        if (!job.showPrintDialog(null)) return false;
        for (Listado listado : items) {
            if (!job.printPage(listado)) return false;
        }
        return job.endJob();
    }
}
