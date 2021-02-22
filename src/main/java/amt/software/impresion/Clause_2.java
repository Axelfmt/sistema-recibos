package amt.software.impresion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

public class Clause_2 extends BorderPane {

    @FXML
    private VBox vBox;
    @FXML
    private Label label;

    public Clause_2(String clause, double height) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(
                cl.getResource("Clauses_2.fxml")
        );
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.setPrefHeight(height);
        String[] lines = clause.split("\\r?\\n");
        /*for (int i = 0; i < lines.length; i++) {
            Label label = new Label(lines[i]);
            label.layoutYProperty().setValue(-1.0);
            label.setMinHeight(7.0);
            label.setMaxHeight(7.0);
            label.setPrefHeight(7.0);
            label.setFont(new Font(6));
            this.getChildren().add(label);
        }*/
        for (String line : lines) {
            Label label = new Label(line);
//            label.layoutYProperty().setValue(-1.0);
            label.setMinHeight(5.5);
            label.setMaxHeight(5.5);
            label.setPrefHeight(5.5);
            label.setFont(new Font(5.5));
            this.vBox.getChildren().add(label);
        }
    }
}
