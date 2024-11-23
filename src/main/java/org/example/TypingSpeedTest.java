package org.example;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TypingSpeedTest {
    private static final String[] SAMPLE_TEXTS = {
            "Politechnika Białostocka to najlepsza uczelnia",
            "Monika wyszła z psem na spacer",
            "Mama gotuję obiad dla swojego dziecka",
            "Pies bawi się na podwórku",
            "Franek tworzy nową aplikacje w języku Java"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printHeader("TYPING SPEED TEST");
            printMenu();

            System.out.print("➔ Wybierz opcję: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    startTest(scanner);
                    break;
                case "2":
                    printInstructions();
                    break;
                case "3":
                    System.out.println("Dziękuje za skorzystanie z aplikacji! Do zobaczenia!");
                    running = false;
                    break;
                default:
                    System.out.println("Nieprawidłowa opcja, spróbuj ponownie.");
                    break;
            }

            if (running) {
                System.out.println("\nNaciśnij Enter, aby kontynuować...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void printHeader(String title) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.printf("║                  %s                 ║%n", title);
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║       ⌨  TYPING SPEED TEST  ⌨      ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("1. ➔ Rozpocznij test");
        System.out.println("2. ℹ Instrukcje");
        System.out.println("3. ❌ Wyjdź");
        System.out.println("=".repeat(50));
    }

    private static void printInstructions() {
        System.out.println("\nInstrukcje:");
        System.out.println("1. Wybierz opcję 'Rozpocznij test' z menu.");
        System.out.println("2. Przepisz wyświetlony tekst jak najszybciej.");
        System.out.println("3. Po zakończeniu wpisywania, naciśnij Enter.");
        System.out.println("4. Aplikacja wyświetli Twój wynik w słowach na minutę (WPM).");
        System.out.println("5. Staraj się pisać dokładnie, aby wynik był poprawny!");
    }

    private static void startTest(Scanner scanner) {
        System.out.println("\nRozpoczynamy test...");
        String sampleText = getRandomText();
        System.out.println("\nPrzepisz poniższy tekst jak najszybciej:");
        System.out.println("==================================================");
        System.out.println(sampleText);
        System.out.println("==================================================");

        System.out.print("➔ Wciśnij Enter, aby rozpocząć...");
        scanner.nextLine();

        long startTime = System.nanoTime();
        System.out.print("\n➔ Twoja odpowiedź: ");
        String userInput = scanner.nextLine();
        long endTime = System.nanoTime();

        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        double wordsPerMinute = (sampleText.split("\\s+").length / durationInSeconds) * 60;

        if (userInput.equals(sampleText)) {
            System.out.println("\n✔ Gratulacje! Przepisałeś tekst poprawnie.");
            System.out.printf("⏱ Twój wynik: %.2f słów na minutę (WPM)%n", wordsPerMinute);
        } else {
            int correctCharacters = calculateCorrectCharacters(sampleText, userInput);
            double accuracy = (correctCharacters / (double) sampleText.length()) * 100;

            System.out.println("\n✘ Tekst nie jest dokładny.");
            System.out.printf("📝 Poprawność: %.2f%% %n", accuracy);
            System.out.printf("⏱ Twój wynik: %.2f słów na minutę (WPM)%n", wordsPerMinute);
        }
    }

    private static int calculateCorrectCharacters(String sampleText, String userInput) {
        int correctCharacters = 0;
        int minLength = Math.min(sampleText.length(), userInput.length());

        for (int i = 0; i < minLength; i++) {
            if (sampleText.charAt(i) == userInput.charAt(i)) {
                correctCharacters++;
            }
        }

        return correctCharacters;
    }

    private static String getRandomText() {
        int randomIndex = (int) (Math.random() * SAMPLE_TEXTS.length);
        return SAMPLE_TEXTS[randomIndex];
    }
}
