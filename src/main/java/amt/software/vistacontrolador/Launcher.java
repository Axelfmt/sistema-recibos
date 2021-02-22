package amt.software.vistacontrolador;

import amt.software.modelo.GestorBD;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Launcher extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GestorBD.getInstancia().listarEstacionamientos();
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader(cl.getResource("ventanaPrincipal.fxml"));
        Parent root = loader.load();
        ControladorPrincipal controladorPrincipal = loader.getController();
//        feedDB();
        controladorPrincipal.listaEstacionamientos();
        Dimension dimensionPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        double anchura = dimensionPantalla.getWidth() / 2.75;
        double altura = dimensionPantalla.getHeight() / 1.75;
        try {
            primaryStage.setTitle("Recibos");
            primaryStage.setScene(new Scene(root, anchura, altura));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        if (!GestorBD.getInstancia().open()) {
            System.out.println("ERROR FATAL: Conexión con base de datos fallida");
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        GestorBD.getInstancia().close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void feedDB() throws Exception {
        for (int i = 0; i < 100; i++) {
            GestorBD.getInstancia().crearRegistro(
                    2,
                    1,
                    "Cliente " + i,
                    "Vehículo " + i,
                    "Placas " + i,
                    1200d,
                    i % 2 == 0 ? "Cláusula-Vehículos" : "Cláusula-Puesto"
            );
            GestorBD.getInstancia().crearRegistro(
                    1,
                    1,
                    "Cliente " + i,
                    "Vehículo " + i,
                    "Placas " + i,
                    1200d,
                    i % 2 == 0 ? "Cláusula-Vehículos" : "Cláusula-Puesto"
            );
        }
    }
}
