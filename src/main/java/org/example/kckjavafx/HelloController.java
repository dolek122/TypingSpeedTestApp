package org.example.kckjavafx;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;

    // Konstruktor
    public HelloController() {
    }

    // Inicjalizacja kontrolera
    @FXML
    public void initialize() {
        // Wczytanie pliku CSS
        welcomeText.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    }

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Hello, JavaFX!");
    }
}
