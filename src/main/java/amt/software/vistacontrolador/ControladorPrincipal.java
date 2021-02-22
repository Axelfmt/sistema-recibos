package amt.software.vistacontrolador;

import amt.software.impresion.Clause_2;
import amt.software.modelo.GestorBD;
import amt.software.modelo.Registro;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControladorPrincipal {

    @FXML
    private BorderPane ventanaPrincipal;
    @FXML
    private ListView<String> listViewEstacionamientos;
    @FXML
    private TableView<Registro> tableViewEstacionamiento;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private Label totalCientes;
    @FXML
    private Label estacionamientoId;
    @FXML
    private Label seleccionados;

    public void initialize() {

        listViewEstacionamientos.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observableValue, String s, String t1) -> {
                    if (t1 != null) {
                        String estacionamiento = listViewEstacionamientos.getSelectionModel().getSelectedItem();
                        Integer id = GestorBD.getInstancia().getId(estacionamiento);
                        listaRegistrosPorEstacionamiento(id);
                    }
                }
        );
        listViewEstacionamientos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewEstacionamientos.getSelectionModel().selectFirst();
        listViewEstacionamientos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                estacionamientoId.setText(listViewEstacionamientos.getSelectionModel().getSelectedItem());
            }
        });
        listViewEstacionamientos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(() -> totalCientes.setText(String.valueOf(tableViewEstacionamiento.getItems().size())));
            }
        });

        contextMenu = new ContextMenu();
        MenuItem actualizaRegistro = new MenuItem("Actualizar");
        actualizaRegistro.setOnAction(actionEvent -> actualizaRegistro());
        MenuItem eliminaRegistro = new MenuItem("Eliminar");
        eliminaRegistro.setOnAction(actionEvent -> eliminaRegistro());
        MenuItem imprimeRegistro = new MenuItem("Imprimir sólo recibos");
        imprimeRegistro.setOnAction(actionEvent -> {
            try {
                imprimeRegistros();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        MenuItem imprimeDobleCara = new MenuItem("Imprimir a doble cara");
        imprimeDobleCara.setOnAction(actionEvent -> {
            try {
                imprimeRegistrosDobleCara();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        contextMenu.getItems().addAll(actualizaRegistro, imprimeDobleCara, imprimeRegistro, eliminaRegistro);
        tableViewEstacionamiento.setContextMenu(contextMenu);
        tableViewEstacionamiento.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewEstacionamiento.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Registro>() {
            @Override
            public void changed(ObservableValue<? extends Registro> observable, Registro oldValue, Registro newValue) {
                Platform.runLater(() -> seleccionados.setText(
                        String.valueOf(tableViewEstacionamiento.getSelectionModel().getSelectedItems().size())));
            }
        });
    }

    @FXML
    public void listaEstacionamientos() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() {
                return FXCollections.observableArrayList(GestorBD.getInstancia().listarEstacionamientos());
            }
        };
        listViewEstacionamientos.itemsProperty().bind(task.valueProperty());
        task.setOnSucceeded((WorkerStateEvent e) -> {
            listViewEstacionamientos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            listViewEstacionamientos.getSelectionModel().selectFirst();
            estacionamientoId.setText(listViewEstacionamientos.getSelectionModel().getSelectedItem());
        });
        new Thread(task).run();
    }

    @FXML
    public void listaRegistrosPorEstacionamiento(Integer estacionamiento) {
        Task<ObservableList<Registro>> task = new Task<ObservableList<Registro>>() {
            @Override
            protected ObservableList<Registro> call() {
                return FXCollections.observableArrayList(
                        GestorBD.getInstancia().listarRegistrosPorEstacionamiento(estacionamiento));
            }
        };
        tableViewEstacionamiento.itemsProperty().bind(task.valueProperty());
        new Thread(task).run();
    }

    @FXML
    public void nuevoEstacionamiento() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("nuevoestacionamiento.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NuevoEstacionamientoDialogo controller = fxmlLoader.getController();
            try {
                controller.processResults();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Llenar los campos correspondientes");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    nuevoEstacionamiento();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void nuevaClausula() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("NuevaClausula.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NuevaClausula nuevaClausula = fxmlLoader.getController();
            try {
                nuevaClausula.processResults();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Llenar los campos correspondientes");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    nuevaClausula();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void nuevoRegistro() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("nuevoregistro.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            NuevoRegistroControlador controller = fxmlLoader.getController();
            alert1.setTitle("Esperando Confirmación");
            alert1.setHeaderText("Esta agregado al cliente: " + controller.getCliente() + "\n" +
                    " al estacionamiento " + controller.getEstacionamiento() + "\n" + " ¿Desea continuar?");
            Optional<ButtonType> result2 = alert1.showAndWait();
            if (result2.isPresent() && result2.get() == ButtonType.OK) {
                try {
                    controller.processResult();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Registro Inválido:\n" + "1. Todos los campos (a excepción de placas) deben " +
                            "ser completados.\n" +
                            "2. Tanto el día como la tarifa deben de ser de tipo numérico:\n" +
                            "i) En el caso del día, este debe estar dentro del rango del 1 al 31.\n" +
                            "ii) En el caso de la tarifa esta debe ser de valor positivo.\n" +
                            "De lo contrario este mensaje seguirá apareciendo");
                    Optional<ButtonType> result3 = alert.showAndWait();
                    if (result3.isPresent()) {
                        nuevoRegistro();
                    }
                }
                listaEstacionamientos();
                return;
            } else nuevoRegistro();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void actualizaEstacionamiento() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("actualizarestacionamiento.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActualizarEstacionamiento controller = fxmlLoader.getController();
            try {
                controller.processResult();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Llenar los campos correspondientes");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    actualizaEstacionamiento();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void actualizaRegistro() {
        List<Registro> selected = tableViewEstacionamiento.getSelectionModel().getSelectedItems();
        if (selected.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Esta tarea solo puede ser realizada con un solo registro.\n " +
                    "Por favor seleccione un solo registro");
            Optional<ButtonType> result2 = alert.showAndWait();
            if (result2.isPresent()) {
                return;
            }
        } else {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(ventanaPrincipal.getScene().getWindow());
            ClassLoader cl = getClass().getClassLoader();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(cl.getResource("actualizaregistro.fxml"));
            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
                return;
            }
            ActualizaRegistro controller = fxmlLoader.getController();
            controller.setOwner(this);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    controller.processResult(); //(id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Registro Inválido:\n" + "1. Todos los campos (a excepción de placas) deben " +
                            "ser completados.\n" +
                            "2. Tanto el día como la tarifa deben de ser de tipo numérico:\n" +
                            "i) En el caso del día, este debe estar dentro del rango del 1 al 31.\n" +
                            "ii) En el caso de la tarifa esta debe ser de valor positivo.\n" +
                            "De lo contrario este mensaje seguirá apareciendo");
                    Optional<ButtonType> result2 = alert.showAndWait();
                    if (result2.isPresent()) {
                        actualizaRegistro();
                    }
                }
                listaEstacionamientos();
            }
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
    }

    @FXML
    public void actualizarTarifas() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(cl.getResource("actualizartarifas.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println("Error al asignar fxml a dialogo en actualizarTarifas(): "
                    + e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActualizarTarifas controller = loader.getController();
            try {
                controller.processResult();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Los campos no pueden permanecer vacíos\n " +
                        "y en el caso de las tarifas tampoco pueden tener valor negativo");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    actualizarTarifas();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void actualizaClausula() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader();
        /* Aquí irá el fxml que muestra la lista de clausulas a elegir */
        loader.setLocation(cl.getResource("ActualizarClausula.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActualizarClausula controller = loader.getController();
            try {
                controller.processResults();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Debe elegir una cláusula para poder continuar.");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    actualizaClausula();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void eliminaEstacionamiento() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(cl.getResource("eliminaestacionamiento.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println("Error al asignar fxml a dialogo en eliminaEstacionamiento(): "
                    + e.getMessage());
            return;
        }
        dialog.setHeaderText("NOTA:\n " +
                "Para eliminar un estacionamiento primero\n " +
                "se deben eliminar todos sus registros");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EliminarEstacionamiento controller = loader.getController();
            try {
                controller.processResult();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Por favor seleccione un estacionamiento.\n " +
                        "Si seleccionó un estacionamiento verifique si tiene registros");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    eliminaEstacionamiento();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void eliminaRegistro() {
        List<Registro> selected = tableViewEstacionamiento.getSelectionModel().getSelectedItems();
        if (selected.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Esta tarea solo puede ser realizada con un solo registro.\n " +
                    "Por favor seleccione un solo registro");
            Optional<ButtonType> result2 = alert.showAndWait();
            if (result2.isPresent()) {
                return;
            }
        } else {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(ventanaPrincipal.getScene().getWindow());
            ClassLoader cl = getClass().getClassLoader();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(cl.getResource("eliminaregistro.fxml"));
            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
                return;
            }
            EliminaRegistro controller = fxmlLoader.getController();
            Registro registro = tableViewEstacionamiento.getSelectionModel().getSelectedItem();
            Integer id = registro.getId();
            System.out.println(id);
            controller.setId(id);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.setTitle("¡ALERTA!");
            dialog.setHeaderText("Está a punto de eliminar este registro.\n" +
                    "Si procede, no podrá recuperar la información.\n" +
                    "¿ESTA SEGURO DE CONTINUAR?");
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.processResults();
            }
            listaEstacionamientos();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
    }

    @FXML
    public void eliminaClausula() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(cl.getResource("ActualizarClausula.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActualizarClausula controller = loader.getController();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            alert.setTitle("ADVERTENCIA!");
            alert.setHeaderText("Si procede se eliminará la clausula seleccionada y no será posible recuperarla. " +
                    "\nSI PROCEDE A BORRAR LA CLÁUSULA DEBERÁ ACTUALIZAR A LOS CLIENTES " +
                    "\nQUE TIENEN ASIGNADA ESTA CLÁUSULA, DE LOS CONTRARIO NO PODRÁ IMPRIMIR SUS RECIBOS." +
                    "\nSE RECOMIENDA ACUALIZAR LA CLÁUSULA EN LUGAR DE BORRARLA.\n¿Desea continuar?");
            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.CANCEL) {
                alert.close();
                eliminaClausula();
            } else if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    controller.eliminar();
                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error!");
                    error.setHeaderText("Seleccione una opción");
                    Optional<ButtonType> resultado2 = error.showAndWait();
                    if (resultado2.isPresent()) {
                        error.close();
                    }
                }
                listaEstacionamientos();
            }
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void imprimeRegistros() throws IOException {
        System.out.println(tableViewEstacionamiento.getItems().size());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("monthyeardialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        MonthYearDialog controller = fxmlLoader.getController();
        controller.setOwner(this);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.imprimirRecibo();
        }
        listaEstacionamientos();
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void imprimeRegistrosDobleCara() throws IOException {
        System.out.println(tableViewEstacionamiento.getItems().size());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("monthyeardialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        MonthYearDialog controller = fxmlLoader.getController();
        controller.setOwner(this);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.imprimeDobleCara();
        }
        listaEstacionamientos();
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void printClauses() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(cl.getResource("PrintClausesDialog.fxml"));
        dialog.getDialogPane().setContent(loader.load());
        List<Integer> ints = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            ints.add(i);
        }
        ObservableList<Integer> obsInts = FXCollections.observableList(ints);
        loader.<PrintClausesDialog>getController().setComboBox(obsInts);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> optional = dialog.showAndWait();
        if (optional.isPresent() && optional.get() == ButtonType.OK) {
            Integer counter = 1;
            List<VBox> clauses = new ArrayList<>();
            VBox clausesVBox = new VBox();
            Integer copies = loader.<PrintClausesDialog>getController().getInt();
            String clause = loader.<PrintClausesDialog>getController().getTexto();
            String texto = GestorBD.getInstancia().getClausulas(clause);
            while (counter < copies + 1) {
                if (counter % 4 == 0) {
                    Clause_2 clause1 = new Clause_2(texto, 171);
                    clausesVBox.getChildren().add(clause1);
                    clauses.add(clausesVBox);
                } else if ((counter - 1) % 4 == 0 && (counter - 1) != 0) {
                    clausesVBox = new VBox();
                    Clause_2 clause1 = new Clause_2(texto, 171);
                    clausesVBox.getChildren().add(clause1);
                } else {
                    Clause_2 clause1 = new Clause_2(texto, 171);
                    clausesVBox.getChildren().add(clause1);
                }
                counter++;
            }
            if (copies % 4 != 0) clauses.add(clausesVBox);
            MonthYearDialog.doPrint(clauses);
        }
        listaEstacionamientos();
        if (optional.isPresent() && optional.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public void printListas() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ventanaPrincipal.getScene().getWindow());
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(cl.getResource("PrintLists.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            PrintLists controller = fxmlLoader.getController();
            try {
                controller.printResult();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Por favor seleccione un estacionamiento y un mes.");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    printListas();
                }
            }
            listaEstacionamientos();
        }
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
    }

    @FXML
    public List<Registro> getSelectedRecords() {
        List<Registro> registros = tableViewEstacionamiento.getSelectionModel().getSelectedItems();
        return registros;
    }

    @FXML
    public String getEstacionamientoSelected() {
        return listViewEstacionamientos.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void salir() {
        Platform.exit();
    }
}
