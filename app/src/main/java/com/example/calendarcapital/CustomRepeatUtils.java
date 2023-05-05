package com.example.calendarcapital;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class CustomRepeatUtils {


//    public static Calendar localDateToCalendar(LocalDate date)
//    {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
//
//        return calendar;
//    }

    public static Calendar convertLocalDateToCalendar(LocalDate localDate) {
        String format = "yyyy-MM-dd";
        String dateString = localDate.format(DateTimeFormatter.ofPattern(format));
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat(format).parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static LocalDate convertCalendarToLocalDate(Calendar calendar) {
        Date date = calendar.getTime();
        String format = "yyyy-MM-dd";
        String dateString = new SimpleDateFormat(format).format(date);
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
    }


}
