package amt.software.impresion;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Clause extends AnchorPane {

    public Clause() throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(
                cl.getResource("Clauses.fxml")
        );
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }
}
