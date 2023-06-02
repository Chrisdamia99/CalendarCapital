package com.example.calendarcapital;



import android.os.Build;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class CalendarUtils {


    public static LocalDate selectedDate;


    public static String formattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);


            return date.format(formatter);


    }

    public static String formattedDateEventEdit(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("dd MM yyyy", locale);

            return date.format(formatter);

    }

    public static String DailyViewFormattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("MMMM", locale);

            return date.format(formatter);


    }

    public static Calendar dateToCalendar(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date dateForMinusDay(LocalDate date,LocalTime time)
    {   Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

            cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
            cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


            cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());

        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        date.minusDays(1);
        cReminder.setTime(Date.from(date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cReminder.set(Calendar.HOUR_OF_DAY,time.getHour());
        cReminder.set(Calendar.MINUTE,time.getMinute());
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();
    }

    public static Date dateForOneHourBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour() - 1);
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();
    }


    public static Date dateForHalfHourBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 30);
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();
    }


    public static Date dateForFifteenMinBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 15);
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();    }


    public static Date dateForTenMinBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 10);
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();
    }


    public static Date dateForFiveMinBefore(LocalDate date,LocalTime time)
    {   Calendar cReminder = Calendar.getInstance();
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 5);
        cReminder.set(Calendar.MILLISECOND,0);

        return cReminder.getTime();
    }
    public static Date stringToDateFormat(String myDate) {


        Locale locale = new Locale("el", "GR");


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        try {
            return dateFormat.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();

            return null;
        }


    }

    public static String dateToStringFormat(Date myDate) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("dd MM yyyy", locale);


        String dateTime = null;
            dateTime = formatter.format((TemporalAccessor) myDate);

        return dateTime;
    }

    public static String formattedTime(LocalTime time) {

        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

            return time.format(formatter);

    }

    public static String formattedShortTime(LocalTime time) {

        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("HH:mm");

            return time.format(formatter);

    }

    public static String monthYearFromDate(LocalDate date) //Initialize the pattern type that will be used
    {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale);

            return date.format(formatter);

    }


    public static LocalTime dateToLocalTimeFormatted(LocalTime date)
    {
        Locale locale = new Locale("el", "GR");
        String pattern = "HH:mm";
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern(pattern, locale);

        String formattedDate = null;
            formattedDate = date.format(formatter);

            return LocalTime.parse(formattedDate, formatter);

    }
    public static LocalDate dateToLocalDate(Date date)
    {
        Locale locale = new Locale("el", "GR");

        LocalDate localDate = null;
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern(pattern, locale);

        Instant instant = null;
            instant = date.toInstant();

            localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        String formattedDate = null;
            formattedDate = localDate.format(formatter);

            return LocalDate.parse(formattedDate, formatter);

    }
    public static LocalTime dateToLocalTime(Date date)
    {   LocalTime localTime = null;
        Locale locale = new Locale("el", "GR");
        String pattern = "HH:mm";
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern(pattern, locale);

        Instant instant = null;
            instant = date.toInstant();

            localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();

        String formattedDate = null;
            formattedDate = localTime.format(formatter);


            return LocalTime.parse(formattedDate,formatter);

    }


    public static String monthDayFromDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("MMMM d", locale);

            return date.format(formatter);

    }



    public static LocalDate stringToLocalDate(String date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);



        //convert String to LocalDate
        LocalDate localDate = null;
            localDate = LocalDate.parse(date, formatter);


        return localDate;
    }



    public static String dateForReminder(Date date) {
        return (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss a", date);
    }

    public static ArrayList<LocalDate> daysInMonthArray() //The arraylist that month will be stored
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = null;
            yearMonth = YearMonth.from(selectedDate);

        int daysInMonth = 0; //get how many days are in month
            daysInMonth = yearMonth.lengthOfMonth();


        LocalDate prevMonth = null;
            prevMonth = selectedDate.minusMonths(1);

        LocalDate nextMonth = null;
            nextMonth = selectedDate.plusMonths(1);


        YearMonth prevYearMonth = null;
            prevYearMonth = YearMonth.from(prevMonth);

        int prevDaysInMonth = 0;
            prevDaysInMonth = prevYearMonth.lengthOfMonth();


        LocalDate firstOfMonth = null; //get first day of month
            firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);

        int dayOfWeek = 0; // return int between 0-7 which is the day of the week
            dayOfWeek = firstOfMonth.getDayOfWeek().getValue();


        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {

                    daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));

                if (dayOfWeek == 7 && daysInMonthArray.size() >= 7) {

                    daysInMonthArray.clear();
                }


            } else if (i > daysInMonth + dayOfWeek) {

                    daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));



            }
            else {
                    daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));


            }

        }


        return daysInMonthArray;


    }


    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = null;
            assert current != null;
            endDate = current.plusWeeks(1);


            while (current.isBefore(endDate)) {
                days.add(current);
                current = current.plusDays(1);
            }

        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = null;
            oneWeekAgo = current.minusWeeks(1);


            while (current.isAfter(oneWeekAgo)) {
                if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                    return current;

                current = current.minusDays(1);
            }


        return null;
    }


}