package org.weatherwear.menu;

import org.weatherwear.clothesrecommender.IRecommendClothing;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Menu {

    private final PrintStream printStream;
    private final IRecommendClothing IRecommendClothing;

    private final Scanner scanner;

    public Menu(InputStream inputStream, PrintStream printStream, IRecommendClothing IRecommendClothing) {
        this.printStream = printStream;
        scanner = new Scanner(inputStream);
        this.IRecommendClothing = IRecommendClothing;
    }

    private void printMenu(String[] options) {
        printStream.println("WeatherWear.com");
        printStream.println("____________________________________________________");
        for (String option : options) {
            printStream.println(option);
        }
        printStream.print("Choose your option: ");
    }

    public void start() {

        String[] options = {
                "1- Recommend Clothing for current location",
                "2- Recommend clothing for future location",
                "3- Exit",
        };

        int option = 1;
        while (option != 3) {
            printMenu(options);
            try {
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        option1();
                        break;
                    case 2:
                        option2();
                        break;
                    case 3:
                        printStream.println("Exiting WeatherWear.com");
                        break;
                    default:
                        printStream.println("Option " + option + " is invalid. Please enter an integer value between 1 and " + options.length);
                }
            } catch (Exception ex) {
                String invalidOption = scanner.next();
                printStream.println("Option " + invalidOption + " is invalid. Please enter an integer value between 1 and " + options.length);
            }
        }
    }

    private void option1() throws Exception {
        printStream.println(IRecommendClothing.recommendClothing());
    }

    private void option2() {
        try {
            printStream.print("Enter 3 digit airport IATA (in uppercase format) : ");
            String IATA = scanner.next();
            printStream.print("Enter day of arrival (in format YYYY-MM-DD): ");
            String futureDate = scanner.next();
            printStream.println(IRecommendClothing.recommendClothing(IATA, futureDate));
        } catch (Exception e) {
            printStream.println("Error - " + e.getMessage());
        }

    }
}