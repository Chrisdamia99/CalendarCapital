package com.example.mycalendar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class TestFormattedDate {
    @Test
    public void testFormattedDate() {
        int year = 2023;

        for (Month month : Month.values()) {
            for (int day = 1; day <= month.length(Year.isLeap(year)); day++) {
                LocalDate date = LocalDate.of(year, month, day);
                String formatted = formattedDate(date);
                if (day<10)
                {
                    String expected ="0" + day + " " + month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR")) + " " + year;
                    assertEquals(expected, formatted);
                }else
                {
                    String expected = day + " " + month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR")) + " " + year;
                    assertEquals(expected, formatted);
                }

            }
        }
    }

    public static String formattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);
        return date.format(formatter);
    }
}
