package com.example.mycalendar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class MyTestUnit {
    @Test
    public void testDaysInMonthArray() {
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 100; i++) {
            int year = currentDate.getYear() + i;
            for (int month = 1; month <= 12; month++) {
                ArrayList<LocalDate> expected = generateExpected(year, month);
                ArrayList<LocalDate> actual = daysInMonthArray(LocalDate.of(year, month, 1));
                assertEquals("Mismatch in daysInMonthArray result for year " + year + ", month " + month,
                        expected, actual);
            }
        }
    }
    private ArrayList<LocalDate> daysInMonthArray(LocalDate selectedDate) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);
        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
                if (dayOfWeek == 7 && daysInMonthArray.size() >= 7) {
                    daysInMonthArray.clear();
                }
            } else if (i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));
            } else {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }
    private ArrayList<LocalDate> generateExpected(int year, int month) {
        ArrayList<LocalDate> expected = new ArrayList<>();
        LocalDate selectedDate = LocalDate.of(year, month, 1);
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);
        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {
                expected.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
                if (dayOfWeek == 7 && expected.size() >= 7) {
                    expected.clear();
                }
            } else if (i > daysInMonth + dayOfWeek) {
                expected.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));
            } else {
                expected.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return expected;
    }
}
