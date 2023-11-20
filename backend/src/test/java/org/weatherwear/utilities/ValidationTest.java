package org.weatherwear.utilities;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.DateTimeException;

public class ValidationTest {
    private final Validation validation = new Validation();

    @Test
    public void isDateValid_withSlashes_returnsFalse() {
        boolean isDateValid = validation.isDateValid("2023/01/01");
        Assertions.assertFalse(isDateValid);
    }
    @Test
    public void isDateValid_LeapYearDate_Valid() {
        Assertions.assertTrue(validation.isDateValid("2024-02-29"));
    }

    @Test
    public void isDateValid_NonLeapYearDate_Invalid() {
        Assertions.assertFalse(validation.isDateValid("2023-02-29"));
    }


    @Test
    public void isDateValid_WithDashes_returnsTrue() {
        boolean isDateValid = validation.isDateValid("2023-01-01");
        Assertions.assertTrue(isDateValid);
    }

    @Test
    public void isDateValid_NullInput_Invalid() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            validation.isDateValid(null);
        });
    }
    @Test
    public void dayDifference_secondDayOneDayBeforeFirstDay_returnsNegativeOneDifference() {
        Long dayDifference = validation.dayDifference("2023-01-01", "2022-12-31");
        Assertions.assertEquals(-1, dayDifference);
    }

    @Test
    public void dayDifference_oneYearAndOneDayDifference_threeHundredAndSixtySixDayDifference() {
        Long dayDifference = validation.dayDifference("2023-01-01", "2024-01-02");
        Assertions.assertEquals(366, dayDifference);
    }

    @Test
    public void dayDifference_NonLeapYear_threeHundredAndSixtyFiveDaysDifference() {
        Assertions.assertEquals(365, validation.dayDifference("2023-01-01", "2024-01-01"));
    }

    @Test
    public void dayDifference_EndOfMonthComparedToBeginningOfNextMonth_oneDayDifference() {
        Assertions.assertEquals(1, validation.dayDifference("2023-01-31", "2023-02-01"));
    }

    @Test
    public void dayDifference_LeapYear_threeHundredAndSixtySixDaysDifference() {
        Assertions.assertEquals(366, validation.dayDifference("2024-01-01", "2025-01-01"));
    }

    //Expected Exception Test
    @Test
    public void dayDifference_firstDayInvalid_DateTimeExceptionWithExpectedMessageThrown() {
        DateTimeException exception = Assertions.assertThrows(DateTimeException.class, () -> {
            validation.dayDifference("2023/01/01", "2023-01-01");
        }, "DateTimeException is expected");
        Assertions.assertEquals("Expected argument date to be in format YYYY-MM-DD", exception.getMessage());
    }

    @Test
    public void dayDifference_secondDayInvalid_DateTimeExceptionWithExpectedMessageThrown() {
        DateTimeException exception = Assertions.assertThrows(DateTimeException.class, () -> {
            validation.dayDifference("2023-01-01", "2023-02-29");
        }, "DateTimeException is expected");
        Assertions.assertEquals("Expected argument date to be in format YYYY-MM-DD", exception.getMessage());
    }

    @Test
    public void dayDifference_LargeSpan_ShouldBeCorrect() {
        Assertions.assertEquals(36525, validation.dayDifference("2000-01-01", "2100-01-01"));
    }

    @Test
    public void checkingTheDifferenceBetweenTwoIdenticalDates_ZeroDayDifference() {
        Long dayDifference = validation.dayDifference("2023-01-01", "2023-01-01");
        Assertions.assertEquals(0, dayDifference);
    }

    @Test
    public void isIATAValid_ContainsNumericCharacters_IATAInvalid() {
        Assertions.assertFalse(validation.isIATAValid("M1A"));
    }

    @Test
    public void isIATAValid_ContainsSpecialCharacters_IATAInvalid() {
        Assertions.assertFalse(validation.isIATAValid("M@A"));
    }

    @Test
    public void isIATAValid_ValidCodeWithSpace_IATAInvalid() {
        Assertions.assertFalse(validation.isIATAValid("M A"));
    }


    @Test
    public void isIATAValid_TwoCharactersOnly_IATAInvalid() {
        String invalidIATA = "ML";
        boolean isValid = validation.isIATAValid(invalidIATA);
        Assertions.assertFalse(isValid);
    }

    @Test
    public void isIATAValid_FourCharacters_Invalid() {
        String invalidIATA = "mlaa";
        boolean isValid = validation.isIATAValid(invalidIATA);
        Assertions.assertFalse(isValid);
    }

    @Test
    public void isIATAValid_ThreeCapitalCharacters_Valid() {
        String invalidIATA = "MLA";
        boolean isValid = validation.isIATAValid(invalidIATA);
        Assertions.assertTrue(isValid);
    }

    @Test
    public void isIATAValid_ThreeLowercaseCharacters_Invalid() {
        //exercise
        boolean isValid = validation.isIATAValid("mla");
        //verify
        Assertions.assertFalse(isValid);
    }


    @Test
    public void isIATAValid_NullInput_Invalid() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            validation.isIATAValid(null);
        });
    }

}

