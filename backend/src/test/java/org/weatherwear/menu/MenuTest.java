package org.weatherwear.menu;

import org.junit.jupiter.api.*;
import org.weatherwear.clothesrecommender.IRecommendClothing;
import org.weatherwear.clothesrecommender.RecommendClothing;
import org.mockito.Mockito;

import java.io.*;


public class MenuTest {
    private IRecommendClothing recommendClothingMock;
    private static final ByteArrayOutputStream DUMMY_OUTPUT_STREAM = new ByteArrayOutputStream();
    private static final PrintStream PRINT_STREAM = new PrintStream(DUMMY_OUTPUT_STREAM);
    private static final String CLOTHES_RECOMMENDATION_MESSAGE = "It is warm so you should wear light clothing.\nIt is not raining so you don't need an umbrella.";

    private String printMenu() {
        return "WeatherWear.com\n" + "____________________________________________________\n" + "1- Recommend Clothing for current location\n" + "2- Recommend clothing for future location\n" + "3- Exit\n" + "Choose your option: ";
    }

    @BeforeEach
    void setupBeforeEach() {
        recommendClothingMock = Mockito.mock(RecommendClothing.class);
    }

    @AfterEach
    public void teardownAfterEach() throws IOException {
        DUMMY_OUTPUT_STREAM.reset(); //clear contents from output stream
    }

    @AfterAll
    public static void teardownAfterAll() {
        PRINT_STREAM.close(); //closing print stream after test finish

    }

    @Test
    public void whenRunningMenu_option3_expectedQuitMessage() {
        //setup
        String option = "3\n";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        //exercise
        menu.start();
        //verify
        Assertions.assertEquals(printMenu() + "Exiting WeatherWear.com", DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_option1option3_expectedRecommendedClothingAndQuitMessage() throws Exception {
        //setup
        Mockito.when(recommendClothingMock.recommendClothing()).thenReturn(CLOTHES_RECOMMENDATION_MESSAGE);
        Menu menu;
        String option = "1\n3\n";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        //exercise
        menu.start(); // start menu
        //verify
        String expectedMenuOutputContent = printMenu() + "It is warm so you should wear light clothing.\nIt is not raining so you don't need an umbrella.\n" + printMenu() + "Exiting WeatherWear.com";
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_option2option3_expectedRecommendedClothingAndQuitMessage() throws Exception {
        //setup
        Mockito.when(recommendClothingMock.recommendClothing("MLA", "2023-01-01")).thenReturn(CLOTHES_RECOMMENDATION_MESSAGE);
        String option = "2\nMLA\n2023-01-01\n3\n";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        menu.start(); // start menu
        //exercise
        String expectedMenuOutputContent = printMenu() + "Enter 3 digit airport IATA (in uppercase format) : Enter day of arrival (in format YYYY-MM-DD): It is warm so you should wear light clothing.\nIt is not raining so you don't need an umbrella.\n" + printMenu() + "Exiting WeatherWear.com";
        //assert
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_option2WithWrongIATAoption3_expectedWrongIATAMessageAndQuitMessage() throws Exception {
        //setup
        Mockito.when(recommendClothingMock.recommendClothing("mla", "2023-01-01")).thenThrow(new IllegalArgumentException("IATA is invalid"));
        String option = "2\nmla\n2023-01-01\n3\n";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        //exercise
        menu.start(); // start menu
        //verify
        String expectedMenuOutputContent = printMenu() + "Enter 3 digit airport IATA (in uppercase format) : " + "Enter day of arrival (in format YYYY-MM-DD): " + "Error - IATA is invalid\n" + printMenu() + "Exiting WeatherWear.com";
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_integerOptionOutOfRange_expectedErrorMessageAndQuitMessage() {
        //setup
        int invalidOption = 4;
        String option = invalidOption + "\n3\n";
        String expectedErrorMessage = "Option " + invalidOption + " is invalid. Please enter an integer value between 1 and 3";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        //exercise
        menu.start(); // start menu
        //verify
        String expectedMenuOutputContent = printMenu() + expectedErrorMessage + "\n" + printMenu() + "Exiting WeatherWear.com";
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_letterInvalidOption_expectedErrorMessageAndQuitMessage() {
        //setup
        String invalidOption = "a";
        String option = invalidOption + "\n3\n";
        String expectedErrorMessage = "Option " + invalidOption + " is invalid. Please enter an integer value between 1 and 3";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);
        //verify
        menu.start();
        //assert
        String expectedMenuOutputContent = printMenu() + expectedErrorMessage + "\n" + printMenu() + "Exiting WeatherWear.com";
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }

    @Test
    public void whenRunningMenu_option2WithInvalidDateFormat_expectedErrorMessageAndQuitMessage() throws Exception {
        //setup
        String invalidDate = "01-01-2023";
        Mockito.when(recommendClothingMock.recommendClothing("MLA", "01-01-2023")).thenThrow(new IllegalArgumentException("Expected argument date to be in format YYYY-MM-DD"));
        String option = "2\nMLA\n" + invalidDate + "\n3\n";
        InputStream dummyInputStream = new ByteArrayInputStream(option.getBytes());
        Menu menu = new Menu(dummyInputStream, PRINT_STREAM, recommendClothingMock);

        //exercise
        menu.start(); // start menu

        //verify
        String expectedMenuOutputContent = printMenu() + "Enter 3 digit airport IATA (in uppercase format) : " + "Enter day of arrival (in format YYYY-MM-DD): " + "Error - Expected argument date to be in format YYYY-MM-DD\n" + printMenu() + "Exiting WeatherWear.com";
        Assertions.assertEquals(expectedMenuOutputContent, DUMMY_OUTPUT_STREAM.toString().trim());
    }
}