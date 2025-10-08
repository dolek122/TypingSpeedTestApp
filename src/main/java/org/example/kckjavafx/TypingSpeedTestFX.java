package org.example.kckjavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class TypingSpeedTestFX extends Application {

    private static final String[] EASY_TEXTS = {
            "Dzieci bawią się na podwórku, śmiech rozbrzmiewa w całym ogrodzie. Złote promienie słońca padają na zieloną trawę, a ptaki śpiewają radośnie. Cała okolica jest spokojna i pełna życia.",
            "Piesek biega po parku, merda ogonkiem, a jego właściciel cieszy się, że może spędzić czas na świeżym powietrzu. Ludzie spacerują po alejach, a w powietrzu czuć zapach wiosny.",
            "Wspaniałe chwile spędzane na łonie natury sprawiają, że zapominamy o troskach dnia codziennego. Czas płynie wolniej, a otaczająca nas przyroda daje poczucie spokoju i harmonii."
    };

    private static final String[] MEDIUM_TEXTS = {
            "Wielu ludzi marzy o tym, by osiągnąć sukces, ale nie zdają sobie sprawy, że droga do celu nie zawsze jest łatwa. Często trzeba pokonać wiele trudności, zmagać się z własnymi lękami i ograniczeniami, aby zrealizować swoje pasje i marzenia.",
            "Zrównoważony rozwój to temat, który zyskuje na znaczeniu w dzisiejszym świecie. W miarę jak rośnie świadomość ekologiczna, coraz więcej osób stara się podejmować działania mające na celu ochronę środowiska i zmniejszenie negatywnego wpływu na naszą planetę.",
            "Podróże to nie tylko zwiedzanie nowych miejsc, ale także szansa na odkrywanie siebie. Każda podróż, niezależnie od tego, czy jest krótka, czy długa, może zmienić nasze spojrzenie na życie i pozwolić nam na lepsze zrozumienie innych kultur."
    };

    private static final String[] HARD_TEXTS = {
            "W dzisiejszym zglobalizowanym świecie, gdzie technologie rozwijają się w zawrotnym tempie, wyzwań stojących przed ludzkością jest coraz więcej. W obliczu kryzysu klimatycznego, niestabilnych rynków finansowych oraz rosnących nierówności społecznych, konieczne staje się podejmowanie zdecydowanych działań na rzecz przyszłości naszej planety.",
            "Rozważając wpływ sztucznej inteligencji na rynek pracy, nie można zignorować potencjalnych konsekwencji dla tradycyjnych zawodów. Automatyzacja i rozwój technologii mogą prowadzić do powstania nowych miejsc pracy, ale również zagrażają wielu stanowiskom, które dotychczas były uważane za bezpieczne i stabilne.",
            "Współczesna nauka stoi przed wieloma trudnymi pytaniami, na które nie ma jeszcze jednoznacznych odpowiedzi. Zagadnienia związane z genetyką, sztuczną inteligencją oraz etyką badań naukowych wywołują liczne kontrowersje, które zmieniają nasze rozumienie moralności i odpowiedzialności w dobie postępu technologicznego."
    };

    private final ArrayList<Double> wpmHistory = new ArrayList<>();
    private long startTime;
    private Label testLabel;
    private TextArea userInputArea;
    private Label resultLabel;
    private ComboBox<String> difficultyBox;
    private Button startButton;
    private Button submitButton;
    private boolean testInProgress = false;
    private int testDuration = 30; // Domyślny czas w sekundach
    private boolean timerRunning = false;
    private Thread timerThread;
    private ComboBox<Integer> timeBox; // Zmienna `timeBox` jako pole klasy

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Typing Speed Test");

        TabPane tabPane = new TabPane();
        Tab testTab = new Tab("Test", createTestTab());
        Tab instructionsTab = new Tab("Instrukcje", createInstructionsTab());
        Tab statsTab = new Tab("Statystyki", createStatsTab());
        Tab settingsTab = new Tab("Ustawienia", createSettingsTab());

        tabPane.getTabs().addAll(testTab, instructionsTab, statsTab, settingsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTestTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Łatwy", "Średni", "Trudny");
        difficultyBox.setValue("Łatwy");

        testLabel = new Label("Wybierz poziom trudności i kliknij Start");

        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: lightgray;");

        userInputArea = new TextArea();
        userInputArea.setWrapText(true);

        startButton = new Button("Start");
        submitButton = new Button("Zakończ");
        resultLabel = new Label();

        Button resetButton = new Button("Zresetuj test");
        resetButton.setOnAction(e -> resetTest());

        startButton.setOnAction(e -> startTest());
        submitButton.setOnAction(e -> finishTest());

        timeBox = new ComboBox<>();
        timeBox.getItems().addAll(15, 30, 60);
        timeBox.setValue(30);

        timeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            startButton.setDisable(newValue == null);
        });

        userInputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            highlightErrors(textFlow, newValue);
        });

        userInputArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!testInProgress) {
                    startTest();
                    testInProgress = true;
                } else {
                    finishTest();
                    testInProgress = false;
                }
                event.consume();
            }
        });

        layout.getChildren().addAll(difficultyBox, timeBox, testLabel, textFlow, userInputArea, startButton, submitButton, resetButton, resultLabel);
        return layout;
    }

    private void resetTest() {
        testLabel.setText("Wybierz poziom trudności i kliknij Start");
        userInputArea.clear();
        resultLabel.setText("");
        startButton.setDisable(false);
        submitButton.setDisable(false);
        testInProgress = false;
        userInputArea.setStyle("-fx-control-inner-background: white;");
    }


    private VBox createInstructionsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        Label instructions = new Label("Instrukcje:\n1. Wybierz poziom trudności.\n"
                + "2. Kliknij 'Start', aby rozpocząć.\n3. Przepisz wyświetlony tekst.\n"
                + "4. Kliknij 'Zakończ' lub naciśnij Enter, aby zobaczyć wynik.");
        layout.getChildren().add(instructions);
        return layout;
    }

    private VBox createStatsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Próba");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("WPM");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Historia wyników");
        chart.setLegendVisible(false);

        layout.getChildren().add(chart);
        return layout;
    }

    private VBox createSettingsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label themeLabel = new Label("Motyw:");
        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton lightMode = new RadioButton("Jasny");
        lightMode.setToggleGroup(themeGroup);
        lightMode.setSelected(true);
        RadioButton darkMode = new RadioButton("Ciemny");
        darkMode.setToggleGroup(themeGroup);

        themeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == lightMode) {
                layout.getScene().getStylesheets().clear();
                layout.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            } else {
                layout.getScene().getStylesheets().clear();
                layout.getScene().getStylesheets().add(getClass().getResource("/dark-style.css").toExternalForm());
            }
        });

        layout.getChildren().addAll(themeLabel, lightMode, darkMode);
        return layout;
    }

    private void startTest() {
        String difficulty = difficultyBox.getValue();
        String text;

        switch (difficulty) {
            case "Średni":
                text = getRandomText(MEDIUM_TEXTS);
                break;
            case "Trudny":
                text = getRandomText(HARD_TEXTS);
                break;
            default:
                text = getRandomText(EASY_TEXTS);
        }

        testLabel.setText(text);
        userInputArea.clear();
        resultLabel.setText("");
        userInputArea.setStyle("-fx-control-inner-background: white;");
        startTime = System.nanoTime();
        startButton.setDisable(true);

        testDuration = timeBox.getValue();
        timerRunning = true;

        timerThread = new Thread(() -> {
            try {
                for (int i = testDuration; i > 0; i--) {
                    int remainingTime = i;
                    Platform.runLater(() -> resultLabel.setText("Pozostały czas: " + remainingTime + "s"));
                    Thread.sleep(1000);
                }
                Platform.runLater(this::finishTest);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void finishTest() {
        if (timerThread != null && timerRunning) {
            timerRunning = false;
            timerThread.interrupt();
        }

        String sampleText = testLabel.getText().trim();
        String userInput = userInputArea.getText().trim();
        long endTime = System.nanoTime();

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        double wordsPerMinute = (sampleText.split("\\s+").length / durationInSeconds) * 60;
        wpmHistory.add(wordsPerMinute);

        if (userInput.equals(sampleText)) {
            resultLabel.setText(String.format("✔ Poprawnie! WPM: %.2f", wordsPerMinute));
            userInputArea.setStyle("-fx-control-inner-background: #e8ffe8;");  // Zielona
        } else {
            int correctCharacters = calculateCorrectCharacters(sampleText, userInput);
            double accuracy = (correctCharacters / (double) sampleText.length()) * 100;
            resultLabel.setText(String.format("✘ Niepoprawnie. WPM: %.2f, Poprawność: %.2f%%", wordsPerMinute, accuracy));
            userInputArea.setStyle("-fx-control-inner-background: #ffe8e8;");  // Czerwona
        }

        startButton.setDisable(false);
        testInProgress = false;
        updateChartData();
    }

    private void updateChartData() {
        Tab statsTab = ((TabPane) startButton.getScene().getRoot()).getTabs().get(2);
        LineChart<String, Number> chart = (LineChart<String, Number>) ((VBox) statsTab.getContent()).getChildren().get(0);

        chart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < wpmHistory.size(); i++) {
            series.getData().add(new XYChart.Data<>("Test " + (i + 1), wpmHistory.get(i)));
        }

        chart.getData().add(series);
    }

    private void highlightErrors(TextFlow textFlow, String userInput) {
        textFlow.getChildren().clear();

        String sampleText = testLabel.getText();
        int minLength = Math.min(sampleText.length(), userInput.length());

        for (int i = 0; i < minLength; i++) {
            Text character = new Text(String.valueOf(sampleText.charAt(i)));
            if (sampleText.charAt(i) == userInput.charAt(i)) {
                character.setFill(javafx.scene.paint.Color.BLACK); // Correct character
            } else {
                character.setFill(javafx.scene.paint.Color.RED); // Incorrect character
            }
            textFlow.getChildren().add(character);
        }

        if (userInput.length() > sampleText.length()) {
            Text extra = new Text(userInput.substring(sampleText.length()));
            extra.setFill(javafx.scene.paint.Color.RED); // Extra characters
            textFlow.getChildren().add(extra);
        } else if (userInput.length() < sampleText.length()) {
            Text remaining = new Text(sampleText.substring(userInput.length()));
            remaining.setFill(javafx.scene.paint.Color.GRAY); // Missing characters
            textFlow.getChildren().add(remaining);
        }
    }

    private String getRandomText(String[] texts) {
        int randomIndex = new Random().nextInt(texts.length);
        return texts[randomIndex];
    }

    private int calculateCorrectCharacters(String sampleText, String userInput) {
        int correctCount = 0;
        for (int i = 0; i < Math.min(sampleText.length(), userInput.length()); i++) {
            if (sampleText.charAt(i) == userInput.charAt(i)) {
                correctCount++;
            }
        }
        return correctCount;
    }
}
