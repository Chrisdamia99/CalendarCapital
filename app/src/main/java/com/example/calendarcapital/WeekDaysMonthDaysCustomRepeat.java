package com.example.calendarcapital;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class WeekDaysMonthDaysCustomRepeat {
public static int weekEventsCounterRepeatCounter;

    //--------------------------------------Repeat Until End-------------------------------------------------------------------------
    public static ArrayList<LocalDate> mondayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
       ArrayList<LocalDate> mondaysList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.MONDAY)
                {
                    mondaysList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }

        return mondaysList;
    }

    public static ArrayList<LocalDate> tuesdayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
         ArrayList<LocalDate> tuesdayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.TUESDAY)
                {
                    tuesdayList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }

        return tuesdayList;
    }

    public static ArrayList<LocalDate> wednesdayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
         ArrayList<LocalDate> wednesdayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.WEDNESDAY)
                {
                    wednesdayList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }
        return wednesdayList;
    }

    public static ArrayList<LocalDate> thursdayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
         ArrayList<LocalDate> thursdayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.THURSDAY)
                {
                    thursdayList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }
        return thursdayList;
    }

    public static ArrayList<LocalDate> fridayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
         ArrayList<LocalDate> fridayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.FRIDAY)
                {
                    fridayList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }

        return fridayList;
    }

    public static ArrayList<LocalDate> saturdayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
         ArrayList<LocalDate> saturdayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {

                if (flagDate.getDayOfWeek() == DayOfWeek.SATURDAY)
                {
                    saturdayList.add(flagDate);
                }

                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                    flagDate =flagDate.plusDays(1);



            }
        }
        return saturdayList;
    }

    public static ArrayList<LocalDate> sundayChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt) {
        ArrayList<LocalDate> sundayList = new ArrayList<>();

        flagDate = dateToCustom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!flagDate.isAfter(untilRepeatDate))
            {
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    sundayList.add(flagDate);
                }
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
                flagDate =flagDate.plusDays(1);
            }
        }
        return sundayList;
    }

    public static ArrayList<LocalDate> addDaysOfWeekChosenUntilRepeat(boolean mondayFlag, boolean tuesdayFlag, boolean wednesdayFlag,
                                                                      boolean thursdayFlag, boolean fridayFlag, boolean saturdayFlag,
                                                                      boolean sundayFlag, LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt)
    {
        ArrayList<LocalDate> customDatesToSaveLocalDate = new ArrayList<>();
    if (mondayFlag)
    {
        customDatesToSaveLocalDate.addAll(mondayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (tuesdayFlag)
    {
        customDatesToSaveLocalDate.addAll(tuesdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (wednesdayFlag)
    {
        customDatesToSaveLocalDate.addAll(wednesdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (thursdayFlag)
    {
        customDatesToSaveLocalDate.addAll(thursdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (fridayFlag)
    {
        customDatesToSaveLocalDate.addAll(fridayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (saturdayFlag)
    {
        customDatesToSaveLocalDate.addAll(saturdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }

    if (sundayFlag)
    {
        customDatesToSaveLocalDate.addAll(sundayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
    }
        return customDatesToSaveLocalDate;
    }


    public static boolean checkIfIsLastWeekOfMonth(LocalDate dateToCustom)
    {

        int dayOfMonth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfMonth = dateToCustom.getDayOfMonth();
        }
        int daysInMonth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            daysInMonth = dateToCustom.getMonth().length(dateToCustom.isLeapYear());
        }
        int dayOfWeek = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeek = dateToCustom.getDayOfWeek().getValue();
        }

        boolean isLastWeek = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isLastWeek = (dayOfMonth > daysInMonth - 7) && (dayOfWeek <= DayOfWeek.SATURDAY.getValue());
        }

        return isLastWeek;
    }

    public static String numberOfWeekDateMonth(LocalDate dateToCustom)
    {


        // Get the day of the month
        int dayOfMonth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfMonth = dateToCustom.getDayOfMonth();
        }

        // Calculate the week of the month
        int weekOfMonth = (dayOfMonth - 1) / 7 + 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY)
            {
                return "Μηνιαία κάθε " + weekOfMonth +"ο" + " " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")) + ".";

            }else
            {
                return "Μηνιαία κάθε " + weekOfMonth +"η" + " " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")) + ".";

            }
        }else
        {
            return null;
        }
    }

    public static String numberOfWeekDateMonthTextEventEdit(LocalDate dateToCustom)
    {


        // Get the day of the month
        int dayOfMonth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayOfMonth = dateToCustom.getDayOfMonth();
        }

        // Calculate the week of the month
        int weekOfMonth = (dayOfMonth - 1) / 7 + 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY)
            {
                return "κάθε " + weekOfMonth +"o" + " " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

            }else
            {
                return "κάθε " + weekOfMonth +"η" + " " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

            }
        }else
        {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> addDaysOfMonthChosenUntilRepeat(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate,
                                                                       int monthSpinnerSelection, int repeatSeperateCounterInt)
    {
        ArrayList<LocalDate> monthDaysChoice = new ArrayList<>();

        if (monthSpinnerSelection==0)
        {
            flagDate = dateToCustom;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                while (!flagDate.isAfter(untilRepeatDate))
                {
                   if (flagDate.getDayOfMonth() == dateToCustom.getDayOfMonth())
                   {
                       monthDaysChoice.add(flagDate);
                   }
                   if ((YearMonth.from(flagDate).lengthOfMonth()-flagDate.getDayOfMonth())==0)
                   {
                       flagDate =  flagDate.plusMonths(repeatSeperateCounterInt);
                   }
                    flagDate = flagDate.plusDays(1);

                }
            }
        }else if (monthSpinnerSelection==1)
        {
            int dayOfMonth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayOfMonth = dateToCustom.getDayOfMonth();
            }
            int weekOfMonth = (dayOfMonth - 1) / 7 + 1;
            DayOfWeek dayOfWeek = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayOfWeek = dateToCustom.getDayOfWeek();
            }

            flagDate = dateToCustom;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                while (!flagDate.isAfter(untilRepeatDate))
                {
                    int dayOfMonthFlag = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dayOfMonthFlag = flagDate.getDayOfMonth();
                    }
                    int weekOfMonthFlag = (dayOfMonthFlag - 1) / 7 + 1;
                    DayOfWeek dayOfWeekFlag = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dayOfWeekFlag = flagDate.getDayOfWeek();
                    }

                    if (weekOfMonth==weekOfMonthFlag && dayOfWeek==dayOfWeekFlag)
                    {
                        monthDaysChoice.add(flagDate);
                    }
                    if ((YearMonth.from(flagDate).lengthOfMonth()-flagDate.getDayOfMonth())==0)
                    {
                        flagDate =     flagDate.plusMonths(repeatSeperateCounterInt);
                    }
                    flagDate =   flagDate.plusDays(1);
                }
            }


        }else if (monthSpinnerSelection==2)
        {

            int dayOfWeek = dateToCustom.getDayOfWeek().getValue();


            flagDate = dateToCustom;
            while (!flagDate.isAfter(untilRepeatDate))
            {
                int dayOfMonthFlag = flagDate.getDayOfMonth();
                int daysInMonthFlag = flagDate.getMonth().length(flagDate.isLeapYear());
                int dayOfWeekFlag = flagDate.getDayOfWeek().getValue();

                boolean isLastWeekFlag = (dayOfMonthFlag > daysInMonthFlag - 7) && (dayOfWeekFlag <= DayOfWeek.SATURDAY.getValue());
                if (isLastWeekFlag && dayOfWeekFlag==dayOfWeek)
                {
                    monthDaysChoice.add(flagDate);
                }

                if ((YearMonth.from(flagDate).lengthOfMonth()-flagDate.getDayOfMonth())==0)
                {
                    flagDate =     flagDate.plusMonths(repeatSeperateCounterInt);
                }
                flagDate = flagDate.plusDays(1);
            }

        }

        return monthDaysChoice;

    }
    //--------------------------------------Repeat Counter End-------------------------------------------------------------------------
    public static ArrayList<LocalDate> mondayChosenRepeatCounter(LocalDate flagDate)
    {ArrayList<LocalDate> mondaysList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.MONDAY)
            {
                mondaysList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return mondaysList;
    }
    public static ArrayList<LocalDate> tuesdayChosenRepeatCounter(LocalDate flagDate)
    {ArrayList<LocalDate> tuesdayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.TUESDAY)
            {
                tuesdayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return tuesdayList;
    }
    public static ArrayList<LocalDate> wednesdayChosenRepeatCounter( LocalDate flagDate)
    {ArrayList<LocalDate> wednesdayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.WEDNESDAY)
            {
                wednesdayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return wednesdayList;
    }
    public static ArrayList<LocalDate> thursdayChosenRepeatCounter(LocalDate flagDate)
    {ArrayList<LocalDate> thursdayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.THURSDAY)
            {
                thursdayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return thursdayList;
    }
    public static ArrayList<LocalDate> fridayChosenRepeatCounter( LocalDate flagDate)
    {ArrayList<LocalDate> fridayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.FRIDAY)
            {
                fridayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return fridayList;
    }
    public static ArrayList<LocalDate> saturdayChosenRepeatCounter( LocalDate flagDate)
    {
        ArrayList<LocalDate> saturdayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.SATURDAY)
            {
                saturdayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return saturdayList;
    }
    public static ArrayList<LocalDate> sundayChosenRepeatCounter( LocalDate flagDate)
    {
        ArrayList<LocalDate> sundayList = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                sundayList.add(flagDate);
                weekEventsCounterRepeatCounter++;
            }
        }


        return sundayList;
    }

    public static ArrayList<LocalDate> addDaysOfWeekChosenRepeatCounter(boolean mondayFlag, boolean tuesdayFlag, boolean wednesdayFlag,
                                                                        boolean thursdayFlag, boolean fridayFlag, boolean saturdayFlag,
                                                                        boolean sundayFlag, LocalDate dateToCustom, int repeatSeperateCounterInt, int repeatCounterIntEnd)
    {
        ArrayList<LocalDate> customDatesToSaveLocalDate = new ArrayList<>();
        LocalDate flagDate = dateToCustom;
        while (weekEventsCounterRepeatCounter!=repeatCounterIntEnd) {
            if (mondayFlag) {
                customDatesToSaveLocalDate.addAll(mondayChosenRepeatCounter(flagDate));
            }

            if (tuesdayFlag) {
                customDatesToSaveLocalDate.addAll(tuesdayChosenRepeatCounter(flagDate));
            }

            if (wednesdayFlag) {
                customDatesToSaveLocalDate.addAll(wednesdayChosenRepeatCounter(flagDate));
            }

            if (thursdayFlag) {
                customDatesToSaveLocalDate.addAll(thursdayChosenRepeatCounter(flagDate));
            }

            if (fridayFlag) {
                customDatesToSaveLocalDate.addAll(fridayChosenRepeatCounter(flagDate));
            }

            if (saturdayFlag) {
                customDatesToSaveLocalDate.addAll(saturdayChosenRepeatCounter(flagDate));
            }

            if (sundayFlag) {
                customDatesToSaveLocalDate.addAll(sundayChosenRepeatCounter(flagDate));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flagDate = flagDate.plusDays(1);
            }
        }
        return customDatesToSaveLocalDate;
    }

    public static ArrayList<LocalDate> addDaysOfMonthChosenRepeatCounter(LocalDate dateToCustom,
                                                                         int monthSpinnerSelection,int repeatSeperateCounterInt,int repeatCounterIntEnd)
    {
        ArrayList<LocalDate> monthDaysChoice = new ArrayList<>();

        LocalDate flagDate;
        if (monthSpinnerSelection==0)
        {
            flagDate = dateToCustom;
            while (weekEventsCounterRepeatCounter!=repeatCounterIntEnd) {
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (flagDate.getDayOfMonth() == dateToCustom.getDayOfMonth()) {
                            monthDaysChoice.add(flagDate);
                            weekEventsCounterRepeatCounter++;
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if ((YearMonth.from(flagDate).lengthOfMonth() - flagDate.getDayOfMonth()) == 0) {
                            flagDate = flagDate.plusMonths(repeatSeperateCounterInt);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        flagDate = flagDate.plusDays(1);
                    }

                }
            }
        }else if (monthSpinnerSelection==1)
        {
            int dayOfMonth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayOfMonth = dateToCustom.getDayOfMonth();
            }
            int weekOfMonth = (dayOfMonth - 1) / 7 + 1;
            DayOfWeek dayOfWeek = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayOfWeek = dateToCustom.getDayOfWeek();
            }

            flagDate = dateToCustom;
            while (weekEventsCounterRepeatCounter!=repeatCounterIntEnd) {
                int dayOfMonthFlag = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dayOfMonthFlag = flagDate.getDayOfMonth();
                }
                int weekOfMonthFlag = (dayOfMonthFlag - 1) / 7 + 1;
                DayOfWeek dayOfWeekFlag = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dayOfWeekFlag = flagDate.getDayOfWeek();
                }

                if (weekOfMonth==weekOfMonthFlag && dayOfWeek==dayOfWeekFlag)
                {
                    monthDaysChoice.add(flagDate);
                    weekEventsCounterRepeatCounter++;

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if ((YearMonth.from(flagDate).lengthOfMonth()- flagDate.getDayOfMonth())==0)
                    {
                        flagDate =     flagDate.plusMonths(repeatSeperateCounterInt);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flagDate =   flagDate.plusDays(1);
                }
            }


        }else if (monthSpinnerSelection==2)
        {

            int dayOfWeek = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dayOfWeek = dateToCustom.getDayOfWeek().getValue();
            }


            flagDate = dateToCustom;
            while (weekEventsCounterRepeatCounter!=repeatCounterIntEnd) {
                int dayOfMonthFlag = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dayOfMonthFlag = flagDate.getDayOfMonth();
                }
                int daysInMonthFlag = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    daysInMonthFlag = flagDate.getMonth().length(flagDate.isLeapYear());
                }
                int dayOfWeekFlag = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dayOfWeekFlag = flagDate.getDayOfWeek().getValue();
                }

                boolean isLastWeekFlag = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    isLastWeekFlag = (dayOfMonthFlag > daysInMonthFlag - 7) && (dayOfWeekFlag <= DayOfWeek.SATURDAY.getValue());
                }
                if (isLastWeekFlag && dayOfWeekFlag==dayOfWeek)
                {
                    monthDaysChoice.add(flagDate);
                    weekEventsCounterRepeatCounter++;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if ((YearMonth.from(flagDate).lengthOfMonth()- flagDate.getDayOfMonth())==0)
                    {
                        flagDate =     flagDate.plusMonths(repeatSeperateCounterInt);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flagDate = flagDate.plusDays(1);
                }
            }

        }

        return monthDaysChoice;

    }

    //-----------------------------------------------------If allFlagsFalse---------------------------------------------------------------------------------------------

    public static ArrayList<LocalDate> addDaysOfWeekChosenUntilRepeatAllFalse(LocalDate untilRepeatDate, LocalDate dateToCustom, LocalDate flagDate, int repeatSeperateCounterInt)
    {
        boolean mondayFlag=false;
        boolean tuesdayFlag=false;
        boolean wednesdayFlag=false;
        boolean thursdayFlag=false;
        boolean fridayFlag=false;
        boolean saturdayFlag=false;
        boolean sundayFlag=false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (dateToCustom.getDayOfWeek() == DayOfWeek.MONDAY)
            {
                mondayFlag=true;
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (dateToCustom.getDayOfWeek() == DayOfWeek.TUESDAY)
                {
                    tuesdayFlag=true;
                }else if (dateToCustom.getDayOfWeek() == DayOfWeek.WEDNESDAY)
                {
                    wednesdayFlag=true;
                }else if (dateToCustom.getDayOfWeek() == DayOfWeek.THURSDAY)
                {
                    thursdayFlag=true;
                }else if (dateToCustom.getDayOfWeek() == DayOfWeek.FRIDAY)
                {
                    fridayFlag=true;
                }else if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY)
                {
                    saturdayFlag=true;
                }else if (dateToCustom.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    sundayFlag=true;
                }
            }
        }
        ArrayList<LocalDate> customDatesToSaveLocalDate = new ArrayList<>();
        if (mondayFlag)
        {
            customDatesToSaveLocalDate.addAll(mondayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (tuesdayFlag)
        {
            customDatesToSaveLocalDate.addAll(tuesdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (wednesdayFlag)
        {
            customDatesToSaveLocalDate.addAll(wednesdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (thursdayFlag)
        {
            customDatesToSaveLocalDate.addAll(thursdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (fridayFlag)
        {
            customDatesToSaveLocalDate.addAll(fridayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (saturdayFlag)
        {
            customDatesToSaveLocalDate.addAll(saturdayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }

        if (sundayFlag)
        {
            customDatesToSaveLocalDate.addAll(sundayChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt));
        }
        return customDatesToSaveLocalDate;
    }

    public static ArrayList<LocalDate> addDaysOfWeekChosenRepeatCounterAllFalse( LocalDate dateToCustom, int repeatSeperateCounterInt, int repeatCounterIntEnd)
    {
        ArrayList<LocalDate> customDatesToSaveLocalDate = new ArrayList<>();
        LocalDate flagDate = dateToCustom;

        boolean mondayFlag=false;
        boolean tuesdayFlag=false;
        boolean wednesdayFlag=false;
        boolean thursdayFlag=false;
        boolean fridayFlag=false;
        boolean saturdayFlag=false;
        boolean sundayFlag=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (dateToCustom.getDayOfWeek() == DayOfWeek.MONDAY)
            {
                mondayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.TUESDAY)
            {
                tuesdayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.WEDNESDAY)
            {
                wednesdayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.THURSDAY)
            {
                thursdayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.FRIDAY)
            {
                fridayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY)
            {
                saturdayFlag=true;
            }else if (dateToCustom.getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                sundayFlag=true;
            }
        }
        while (weekEventsCounterRepeatCounter!=repeatCounterIntEnd) {
            if (mondayFlag) {
                customDatesToSaveLocalDate.addAll(mondayChosenRepeatCounter(flagDate));
            }

            if (tuesdayFlag) {
                customDatesToSaveLocalDate.addAll(tuesdayChosenRepeatCounter(flagDate));
            }

            if (wednesdayFlag) {
                customDatesToSaveLocalDate.addAll(wednesdayChosenRepeatCounter(flagDate));
            }

            if (thursdayFlag) {
                customDatesToSaveLocalDate.addAll(thursdayChosenRepeatCounter(flagDate));
            }

            if (fridayFlag) {
                customDatesToSaveLocalDate.addAll(fridayChosenRepeatCounter(flagDate));
            }

            if (saturdayFlag) {
                customDatesToSaveLocalDate.addAll(saturdayChosenRepeatCounter(flagDate));
            }

            if (sundayFlag) {
                customDatesToSaveLocalDate.addAll(sundayChosenRepeatCounter(flagDate));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (flagDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    flagDate = flagDate.plusWeeks(repeatSeperateCounterInt);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flagDate = flagDate.plusDays(1);
            }
        }
        return customDatesToSaveLocalDate;
    }
}
