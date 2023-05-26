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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }else
        {
            return null;
        }

    }

    public static String formattedDateEventEdit(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd MM yyyy", locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }else
        {
            return null;
        }
    }

    public static String DailyViewFormattedDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MMMM", locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }else
        {
            return null;
        }

    }

    public static Calendar dateToCalendar(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date dateForMinusDay(LocalDate date,LocalTime time)
    {   Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

            cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
            cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());

        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        date.minusDays(1);
        cReminder.setTime(Date.from(date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cReminder.set(Calendar.HOUR_OF_DAY,time.getHour());
        cReminder.set(Calendar.MINUTE,time.getMinute());
        cReminder.set(Calendar.MILLISECOND,0);
        }
        return cReminder.getTime();
    }

    public static Date dateForOneHourBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour() - 1);
        cReminder.set(Calendar.MILLISECOND,0);
        }
        return cReminder.getTime();
    }


    public static Date dateForHalfHourBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 30);
        cReminder.set(Calendar.MILLISECOND,0);
        }
        return cReminder.getTime();
    }


    public static Date dateForFifteenMinBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 15);
        cReminder.set(Calendar.MILLISECOND,0);
        }
        return cReminder.getTime();    }


    public static Date dateForTenMinBefore(LocalDate date,LocalTime time)
    {Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 10);
        cReminder.set(Calendar.MILLISECOND,0);
        }
        return cReminder.getTime();
    }


    public static Date dateForFiveMinBefore(LocalDate date,LocalTime time)
    {   Calendar cReminder = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cReminder.set(Calendar.YEAR, date.getYear());

        cReminder.set(Calendar.MONTH, date.getMonth().getValue() - 1);
        cReminder.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());


        cReminder.set(Calendar.HOUR_OF_DAY, time.getHour());
        cReminder.set(Calendar.MINUTE, time.getMinute());
        cReminder.set(Calendar.SECOND, time.getSecond());
        cReminder.set(Calendar.MINUTE, time.getMinute() - 5);
        cReminder.set(Calendar.MILLISECOND,0);
        }
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd MM yyyy", locale);
        }

        String dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = formatter.format((TemporalAccessor) myDate);
        }
        return dateTime;
    }

    public static String formattedTime(LocalTime time) {

        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return time.format(formatter);
        }else
        {
            return null;
        }
    }

    public static String formattedShortTime(LocalTime time) {

        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return time.format(formatter);
        }else
        {
            return null;
        }
    }

    public static String monthYearFromDate(LocalDate date) //Initialize the pattern type that will be used
    {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }else
        {
            return null;
        }
    }


    public static LocalTime dateToLocalTimeFormatted(LocalTime date)
    {
        Locale locale = new Locale("el", "GR");
        String pattern = "HH:mm";
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(pattern, locale);
        }
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = date.format(formatter);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.parse(formattedDate, formatter);
        }else
        {
            return null;
        }
    }
    public static LocalDate dateToLocalDate(Date date)
    {
        Locale locale = new Locale("el", "GR");

        LocalDate localDate = null;
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(pattern, locale);
        }
        Instant instant = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = date.toInstant();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String formattedDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedDate = localDate.format(formatter);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.parse(formattedDate, formatter);
        }else
        {
            return null;
        }
    }
    public static LocalTime dateToLocalTime(Date date)
    {   LocalTime localTime = null;
        Locale locale = new Locale("el", "GR");
        String pattern = "HH:mm";
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(pattern, locale);
        }
        Instant instant = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = date.toInstant();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        }
        String formattedDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedDate = localTime.format(formatter);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.parse(formattedDate,formatter);
        }else
        {
            return null;
        }
    }


    public static String monthDayFromDate(LocalDate date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MMMM d", locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }else
        {
            return null;
        }
    }



    public static LocalDate stringToLocalDate(String date) {
        Locale locale = new Locale("el", "GR");
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
        }


        //convert String to LocalDate
        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.parse(date, formatter);
        }

        return localDate;
    }



    public static String dateForReminder(Date date) {
        return (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss a", date);
    }

    public static ArrayList<LocalDate> daysInMonthArray() //The arraylist that month will be stored
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yearMonth = YearMonth.from(selectedDate);
        }
        int daysInMonth = 0; //get how many days are in month
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            daysInMonth = yearMonth.lengthOfMonth();
        }

        LocalDate prevMonth = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prevMonth = selectedDate.minusMonths(1);
        }
        LocalDate nextMonth = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nextMonth = selectedDate.plusMonths(1);
        }

        YearMonth prevYearMonth = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prevYearMonth = YearMonth.from(prevMonth);
        }
        int prevDaysInMonth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prevDaysInMonth = prevYearMonth.lengthOfMonth();
        }

        LocalDate firstOfMonth = null; //get first day of month
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        }
        int dayOfWeek = 0; // return int between 0-7 which is the day of the week
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        }

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
                }
                if (dayOfWeek == 7 && daysInMonthArray.size() >= 7) {

                    daysInMonthArray.clear();
                }


            } else if (i > daysInMonth + dayOfWeek) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));
                }


            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
                }

            }

        }


        return daysInMonthArray;


    }


    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert current != null;
            endDate = current.plusWeeks(1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (current.isBefore(endDate)) {
                days.add(current);
                current = current.plusDays(1);
            }
        }
        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            oneWeekAgo = current.minusWeeks(1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (current.isAfter(oneWeekAgo)) {
                if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                    return current;

                current = current.minusDays(1);
            }
        }

        return null;
    }


}