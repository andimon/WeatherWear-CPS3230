package org.weatherwear.utilities;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Validation {
    // validation methods used in the entirity of the project
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE;
    public boolean isDateValid(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, dateTimeFormat);
        } catch (DateTimeException e) {
            return false;
        }
        return true;
    }
    public long dayDifference(String date1, String date2) {
        if (!isDateValid(date1) || !isDateValid(date2)) {
            throw new DateTimeException("Expected argument date to be in format YYYY-MM-DD");
        } else {
            LocalDate localDate1 = LocalDate.parse(date1, dateTimeFormat);
            LocalDate localDate2 = LocalDate.parse(date2, dateTimeFormat);
            return ChronoUnit.DAYS.between(localDate1, localDate2);
        }
    }
    public boolean isIATAValid(String IATA) {
        Pattern IATOPattern = Pattern.compile("[A-Z]{3}+");
        Matcher IATOMatcher = IATOPattern.matcher(IATA);
        return IATOMatcher.matches();
    }
}
