package com.example.calendarcapital;


import android.icu.text.DateFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {


    public static LocalDate selectedDate;
    public static LocalTime selectedTime;


    public static String formattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);
        return date.format(formatter);
    }

    public static String DailyViewFormattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", locale);
        return date.format(formatter);

    }

    public static String formattedTime(LocalTime time) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }

    public static String formattedShortTime(LocalTime time) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date) //Initialize the pattern type that will be used
    {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale);
        return date.format(formatter);
    }

    public static String monthDayFromDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d", locale);
        return date.format(formatter);
    }

    public static LocalDate stringToLocalDate(String date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);


        //convert String to LocalDate
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate;
    }

    public static String dateForReminder(Date date)
    {
        return (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss a", date);
    }

    public static ArrayList<LocalDate> daysInMonthArray() //The arraylist that month will be stored
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth(); //get how many days are in month

        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1); //get first day of month
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // return int between 0-7 which is the day of the week

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {

                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
                if (dayOfWeek == 7 && daysInMonthArray.size() >= 7) {

                    daysInMonthArray.clear();
                }



            } else if (i > daysInMonth + dayOfWeek) {

                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));

                if (dayOfWeek<6 && daysInMonthArray.size()==41)
                {

                    daysInMonthArray.remove(40);
                    daysInMonthArray.remove(39);
                    daysInMonthArray.remove(38);
                    daysInMonthArray.remove(37);
                    daysInMonthArray.remove(36);
                    daysInMonthArray.remove(35);
                    daysInMonthArray.remove(34);




                }else if (dayOfWeek<6 && daysInMonthArray.size()==42)
                {

                    daysInMonthArray.remove(41);
                    daysInMonthArray.remove(40);
                    daysInMonthArray.remove(39);
                    daysInMonthArray.remove(38);
                    daysInMonthArray.remove(37);
                    daysInMonthArray.remove(36);
                    daysInMonthArray.remove(35);



                }else if (daysInMonthArray.size()==35)
                {
                    daysInMonthArray.remove(34);
                    daysInMonthArray.add(daysInMonthArray.get(33).plusDays(1));
                }


            } else {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));

            }
        }


        return daysInMonthArray;


    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }


}
