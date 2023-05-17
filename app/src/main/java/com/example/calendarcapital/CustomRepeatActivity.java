package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;


public class CustomRepeatActivity extends AppCompatActivity {
    private Spinner custom_repeat_spinner, monthChoiceSpinner;
    Button custom_repeat_btnSave;
    View sepView;
    TextView untilDate;
    TextView sundayChoice, mondayChoice, tuesdayChoice, wednesdayChoice, thursdayChoice, fridayChoice, saturdayChoice;
    EditText repeatCounterET, repeatSeperateCounter;
    RadioButton untilRB, repeatCounterRB;
    LinearLayout daysOfWeekChoice, monthSpinnerChoiceLinLay;
    ImageButton custom_repeat_back_button, custom_repeat_refresh_button, cancelUntil;
    String originalTextUntilDate;
    LocalDate untilRepeatDate;
    static boolean sundayFlag, mondayFlag, tuesdayFlag, wednesdayFlag, thursdayFlag, fridayFlag, saturdayFlag;
    static int repeatSeperateCounterInt;
    static int repeatCounterIntEnd;
    static int spinnerTimeSelection, monthSpinnerSelection;//spinnerTime: 0=day 1=week 2=month 3=year

    public static String textForEventEdit;
    private LocalDate flagDate;
    private static LocalDate dateToCustom;

    public static ArrayList<LocalDate> customDatesToSaveLocalDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_repeat_layout);

        initWidgets();
        setDateFromIntent();
        selectDaysOfWeek();
        setCustom_repeat_spinner();
        setAdapterMonthlySpinner();
        radioButtonsClickListeners();
        initBooleansWeekDays();
        setWeekChoiceByDate();
        cancelUntil.setVisibility(View.GONE);

        customDatesToSaveLocalDate = new ArrayList<>();

        originalTextUntilDate = untilDate.getText().toString();

        custom_repeat_back_button.setOnClickListener(v -> {

            Intent iEE = new Intent(CustomRepeatActivity.this, EventEdit.class);
            Intent iEU = new Intent(CustomRepeatActivity.this, Edit_Update_Activity.class);

            if (getIntent().hasExtra("flagBack")) {
                if (getIntent().getStringExtra("flagBack").equals("0")) {
                    if (getIntent().hasExtra("tittle") && getIntent().hasExtra("comment")) {

                        String tittle = getIntent().getStringExtra("tittle");
                        String comment = getIntent().getStringExtra("comment");


                        iEE.putExtra("tittle", tittle);
                        iEE.putExtra("comment", comment);


                    }
                    if (getIntent().hasExtra("stack")) {
                        String stack = getIntent().getStringExtra("stack");
                        iEE.putExtra("stack", stack);
                    }
                    startActivity(iEE);
                } else if (getIntent().getStringExtra("flagBack").equals("1")) {

                    if (getIntent().hasExtra("tittle") && getIntent().hasExtra("comment")) {

                        String tittle = getIntent().getStringExtra("tittle");
                        String comment = getIntent().getStringExtra("comment");


                        iEU.putExtra("tittle", tittle);
                        iEU.putExtra("comment", comment);


                    }
                    if (getIntent().hasExtra("stack")) {
                        String stack = getIntent().getStringExtra("stack");
                        iEU.putExtra("stack", stack);
                    }
                    startActivity(iEU);
                }
            }


        });

        custom_repeat_refresh_button.setOnClickListener(v -> AllEventsList.reloadActivity(CustomRepeatActivity.this));

        custom_repeat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                customDatesToSaveLocalDate.clear();
                if (!(position == 1)) {
                    daysOfWeekChoice.setVisibility(View.GONE);
                } else {
                    daysOfWeekChoice.setVisibility(View.VISIBLE);
                }

                if (!(position == 2)) {
                    monthSpinnerChoiceLinLay.setVisibility(View.GONE);
                } else {
                    monthSpinnerChoiceLinLay.setVisibility(View.VISIBLE);
                }

                if (position == 2 || position == 1) {
                    sepView.setVisibility(View.VISIBLE);
                } else {

                    sepView.setVisibility(View.GONE);
                }

                if (position == 0) {
                    spinnerTimeSelection = 0;
                } else if (position == 1) {
                    spinnerTimeSelection = 1;
                } else if (position == 2) {
                    spinnerTimeSelection = 2;
                } else if (position == 3) {
                    spinnerTimeSelection = 3;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CustomRepeatActivity.this, "test ONNOTHING", Toast.LENGTH_SHORT).show();
            }
        });


        untilDate.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
            }
            if (!untilRB.isChecked()) {
                untilRB.setChecked(true);
            }
            if (repeatCounterRB.isChecked()) {
                repeatCounterRB.setChecked(false);
            }
        });

        cancelUntil.setOnClickListener(v -> {
            untilDate.setText(originalTextUntilDate);
            untilRepeatDate = null;
            cancelUntil.setVisibility(View.GONE);
            untilRB.setChecked(false);
        });

        custom_repeat_btnSave.setOnClickListener(v -> saveCustomRepeat());


        repeatCounterET.setOnClickListener(v -> {
            if (untilRB.isChecked()) {
                repeatCounterRB.setChecked(true);
                untilRB.setChecked(false);
            } else {
                repeatCounterRB.setChecked(true);
            }
        });
    }


    private void initWidgets() {
        custom_repeat_spinner = findViewById(R.id.dwmy_spinner);
        monthChoiceSpinner = findViewById(R.id.monthChoiceSpinner);
        custom_repeat_back_button = findViewById(R.id.customRepeatBackButton);
        custom_repeat_refresh_button = findViewById(R.id.customRepeatRefreshButton);
        daysOfWeekChoice = findViewById(R.id.daysOfWeekChoice);
        monthSpinnerChoiceLinLay = findViewById(R.id.monthSpinnerChoiceLinLay);
        repeatCounterET = findViewById(R.id.repeatCounterET);
        untilRB = findViewById(R.id.untilRB);
        repeatCounterRB = findViewById(R.id.repeatCounterRB);
        sepView = findViewById(R.id.sepView);
        repeatSeperateCounter = findViewById(R.id.repeatSeperateCounter);
        untilDate = findViewById(R.id.untilDate);
        cancelUntil = findViewById(R.id.cancelUntil);
        sundayChoice = findViewById(R.id.sundayChoice);
        mondayChoice = findViewById(R.id.mondayChoice);
        tuesdayChoice = findViewById(R.id.tuesdayChoice);
        wednesdayChoice = findViewById(R.id.wednesdayChoice);
        thursdayChoice = findViewById(R.id.thursdayChoice);
        fridayChoice = findViewById(R.id.fridayChoice);
        saturdayChoice = findViewById(R.id.saturdayChoice);
        custom_repeat_btnSave = findViewById(R.id.btnSaveCustomRepeat);

    }

    private void initBooleansWeekDays() {
        sundayFlag = false;
        mondayFlag = false;
        tuesdayFlag = false;
        wednesdayFlag = false;
        thursdayFlag = false;
        fridayFlag = false;
        saturdayFlag = false;

    }

    private void setCustom_repeat_spinner() {

        ArrayAdapter<CharSequence> custom_repeat_spinner_adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.custom_repeat_spinner_less, android.R.layout.simple_spinner_item);

        custom_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        custom_repeat_spinner.setAdapter(custom_repeat_spinner_adapter);

        repeatSeperateCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    int value = Integer.parseInt(charSequence.toString());
                    if (value > 1) {
                        ArrayAdapter<CharSequence> custom_repeat_spinner_adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.custom_repeat_spinner_more, android.R.layout.simple_spinner_item);

                        custom_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        custom_repeat_spinner.setAdapter(custom_repeat_spinner_adapter);
                    } else {
                        ArrayAdapter<CharSequence> custom_repeat_spinner_adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.custom_repeat_spinner_less, android.R.layout.simple_spinner_item);

                        custom_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        custom_repeat_spinner.setAdapter(custom_repeat_spinner_adapter);
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void setAdapterMonthlySpinner() {


        ArrayList<CharSequence> notLastWeek = new ArrayList<>();
        ArrayList<CharSequence> lastWeek = new ArrayList<>();
        if (WeekDaysMonthDaysCustomRepeat.checkIfIsLastWeekOfMonth(dateToCustom)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lastWeek.add("Μηνιαία στις " + dateToCustom.getDayOfMonth() + ".");
            }
            lastWeek.add(WeekDaysMonthDaysCustomRepeat.numberOfWeekDateMonth(dateToCustom));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    lastWeek.add("Μηνιαία το τελευταίο " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                } else {
                    lastWeek.add("Μηνιαία την τελευταία " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
            }

            ArrayAdapter<CharSequence> custom_monthly_repeat_spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lastWeek);
            custom_monthly_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthChoiceSpinner.setAdapter(custom_monthly_repeat_spinner_adapter);


        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notLastWeek.add("Μηνιαία στις " + dateToCustom.getDayOfMonth());
            }
            notLastWeek.add(WeekDaysMonthDaysCustomRepeat.numberOfWeekDateMonth(dateToCustom));
            ArrayAdapter<CharSequence> custom_monthly_repeat_spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, notLastWeek);
            custom_monthly_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthChoiceSpinner.setAdapter(custom_monthly_repeat_spinner_adapter);

        }
        monthChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    monthSpinnerSelection = 0;
                } else if (position == 1) {
                    monthSpinnerSelection = 1;
                } else if (position == 2) {
                    monthSpinnerSelection = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void radioButtonsClickListeners() {


        untilRB.setOnClickListener(v -> {
            if (untilRB.isChecked()) {
                repeatCounterRB.setChecked(false);
            }
        });

        repeatCounterRB.setOnClickListener(v -> {

            if (repeatCounterRB.isChecked()) {

                untilRB.setChecked(false);
            }
        });
    }

    private void showChangeDate(int year, int month, int dayofmonth) {


        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, (view, year1, monthOfYear, dayOfMonth) -> {
            int trueMonth = monthOfYear + 1;

            if (trueMonth < 10 && dayOfMonth >= 10) {


                LocalDate myDD = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDD = LocalDate.of(year1, trueMonth, dayOfMonth);
                }
                untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                cancelUntil.setVisibility(View.VISIBLE);


            } else if (dayOfMonth < 10 && trueMonth < 10) {


                LocalDate myDD = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDD = LocalDate.of(year1, trueMonth, dayOfMonth);
                }
                untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                cancelUntil.setVisibility(View.VISIBLE);
            } else if (dayOfMonth < 10 && trueMonth >= 10) {

                LocalDate myDD = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDD = LocalDate.of(year1, trueMonth, dayOfMonth);
                }

                untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                cancelUntil.setVisibility(View.VISIBLE);


            } else {

                LocalDate myDD = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDD = LocalDate.of(year1, trueMonth, dayOfMonth);
                }
                untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                cancelUntil.setVisibility(View.VISIBLE);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                untilRepeatDate = LocalDate.of(year1, trueMonth, dayOfMonth);
            }


        }, year, month, dayofmonth);


        StartTime.setTitle("Select Date");
        StartTime.show();


    }

    private void setWeekChoiceByDate() {
        DayOfWeek existedDateDayOfWeek;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            existedDateDayOfWeek = dateToCustom.getDayOfWeek();


            if (existedDateDayOfWeek == DayOfWeek.MONDAY) {
                mondayFlag = true;
                mondayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            } else if (existedDateDayOfWeek == DayOfWeek.TUESDAY) {
                tuesdayFlag = true;
                tuesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            } else if (existedDateDayOfWeek == DayOfWeek.WEDNESDAY) {
                wednesdayFlag = true;
                wednesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else if (existedDateDayOfWeek == DayOfWeek.THURSDAY) {
                thursdayFlag = true;
                thursdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            } else if (existedDateDayOfWeek == DayOfWeek.FRIDAY) {
                fridayFlag = true;
                fridayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            } else if (existedDateDayOfWeek == DayOfWeek.SATURDAY) {
                saturdayFlag = true;
                saturdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            } else if (existedDateDayOfWeek == DayOfWeek.SUNDAY) {
                sundayFlag = true;
                sundayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));
            }
        }
    }

    private void selectDaysOfWeek() {

        sundayChoice.setOnClickListener(v -> {

            if (!sundayFlag) {
                sundayFlag = true;
                sundayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                sundayFlag = false;
                sundayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }


        });

        mondayChoice.setOnClickListener(v -> {


            if (!mondayFlag) {
                mondayFlag = true;
                mondayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                mondayFlag = false;
                mondayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }

        });

        tuesdayChoice.setOnClickListener(v -> {


            if (!tuesdayFlag) {
                tuesdayFlag = true;
                tuesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                tuesdayFlag = false;
                tuesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }

        });

        wednesdayChoice.setOnClickListener(v -> {


            if (!wednesdayFlag) {
                wednesdayFlag = true;
                wednesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                wednesdayFlag = false;
                wednesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }

        });

        thursdayChoice.setOnClickListener(v -> {


            if (!thursdayFlag) {
                thursdayFlag = true;
                thursdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                thursdayFlag = false;
                thursdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }

        });

        fridayChoice.setOnClickListener(v -> {


            if (!fridayFlag) {
                fridayFlag = true;
                fridayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                fridayFlag = false;
                fridayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }
        });

        saturdayChoice.setOnClickListener(v -> {


            if (!saturdayFlag) {
                saturdayFlag = true;
                saturdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_grey));

            } else {
                saturdayFlag = false;
                saturdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.simple_borders_rounded_white));

            }

        });

    }

    private void setDateFromIntent() {
        Intent retriveIntent = getIntent();
        String localDate = retriveIntent.getStringExtra("date");
        if (localDate != null) {
            dateToCustom = CalendarUtils.stringToLocalDate(localDate);
        }
    }

    private boolean daysOfWeekAllFalse() {
        if (!sundayFlag && !mondayFlag && !tuesdayFlag && !wednesdayFlag && !thursdayFlag
                && !fridayFlag && !saturdayFlag) {
            return true;
        } else {
            return false;
        }
    }

    private String textForWeek(int repeatSeperateCounterInt) {

        if (repeatSeperateCounterInt > 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textForEventEdit = "Ανά " + repeatSeperateCounterInt + " εβδομάδες ";
                if (mondayFlag) {

                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (tuesdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (wednesdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (thursdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (fridayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (saturdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (sundayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (daysOfWeekAllFalse()) {
                    textForEventEdit = "Ανά " + repeatSeperateCounterInt + " εβδομάδες.";

                }
            }
            return textForEventEdit;

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textForEventEdit = "Ανά " + repeatSeperateCounterInt + " εβδομάδα ";
                if (mondayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));
                }
                if (tuesdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (wednesdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (thursdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (fridayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (saturdayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (sundayFlag) {
                    textForEventEdit = textForEventEdit.concat("," + DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR")));

                }
                if (daysOfWeekAllFalse()) {
                    textForEventEdit = "Ανά " + repeatSeperateCounterInt + " εβδομάδα";

                }
            }
            return textForEventEdit;

        }

    }

    private String textForMonth(int repeatSeperateCounterInt, int monthSpinnerSelection) {

        if (repeatSeperateCounterInt > 1) {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μήνες, ";

            if (monthSpinnerSelection == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textForEventEdit = textForEventEdit + " κάθε " + dateToCustom.getDayOfMonth() + " ";
                }
            } else if (monthSpinnerSelection == 1) {
                textForEventEdit = textForEventEdit + WeekDaysMonthDaysCustomRepeat.numberOfWeekDateMonthTextEventEdit(dateToCustom);

            } else if (monthSpinnerSelection == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY) {
                        textForEventEdit = textForEventEdit + "το τελευταίο" + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

                    } else {
                        textForEventEdit = textForEventEdit + "την τελευταία" + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

                    }
                }
            }
            return textForEventEdit;
        } else {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μήνα, ";

            if (monthSpinnerSelection == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textForEventEdit = textForEventEdit + " κάθε " + dateToCustom.getDayOfMonth() + " ";
                }

            } else if (monthSpinnerSelection == 1) {
                textForEventEdit = textForEventEdit + WeekDaysMonthDaysCustomRepeat.numberOfWeekDateMonthTextEventEdit(dateToCustom);

            } else if (monthSpinnerSelection == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (dateToCustom.getDayOfWeek() == DayOfWeek.SATURDAY) {
                        textForEventEdit = textForEventEdit + "το τελευταίο " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

                    } else {
                        textForEventEdit = textForEventEdit + "την τελευταία " + dateToCustom.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("el", "GR"));

                    }
                }

            }
            return textForEventEdit;
        }
    }

    private void dayChoiceUntilRepeat(int repeatSeperateCounterInt) {
        customDatesToSaveLocalDate.clear();
        if (untilRepeatDate == null) {
            Toast.makeText(this, "Εισάγετε ημερομηνία λήξης.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (untilRepeatDate.isBefore(dateToCustom)) {
                    Toast.makeText(this, "Η ημερομηνία λήξης δε μπορεί να είναι μικρότερητου συμβάντος.", Toast.LENGTH_SHORT).show();

                } else if (untilRepeatDate.isAfter(dateToCustom)) {
                    flagDate = dateToCustom;

                    while (flagDate.isBefore(untilRepeatDate)) {
                        customDatesToSaveLocalDate.add(flagDate);
                        flagDate = flagDate.plusDays(repeatSeperateCounterInt);
                    }
                    if (repeatSeperateCounterInt > 1) {
                        textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μέρες, μέχρι " + untilRepeatDate.toString().trim();

                    } else {
                        textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μέρα, μέχρι " + untilRepeatDate.toString().trim();

                    }
                } else {
                    Toast.makeText(this, "dayChoice error", Toast.LENGTH_SHORT).show();

                }
            }
        }


        customDatesToSaveLocalDate.size();
    }

    private void weekChoiceUntilRepeat(int repeatSeperateCounterInt) {
        customDatesToSaveLocalDate.clear();
        if (untilRepeatDate == null) {
            Toast.makeText(this, "Εισάγετε ημερομηνία λήξης.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (untilRepeatDate.isBefore(dateToCustom)) {
                    Toast.makeText(this, "Η ημερομηνία λήξης δε μπορεί να είναι μικρότερητου συμβάντος.", Toast.LENGTH_SHORT).show();

                } else {
                    if (daysOfWeekAllFalse()) {
                        customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfWeekChosenUntilRepeatAllFalse(untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt);
                        textForEventEdit = textForWeek(repeatSeperateCounterInt) + " μέχρι " + untilRepeatDate.toString().trim();
                    } else {
                        customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfWeekChosenUntilRepeat(mondayFlag, tuesdayFlag, wednesdayFlag, thursdayFlag, fridayFlag, saturdayFlag, sundayFlag, untilRepeatDate, dateToCustom, flagDate, repeatSeperateCounterInt - 1);
                        textForEventEdit = textForWeek(repeatSeperateCounterInt) + " μέχρι " + untilRepeatDate.toString().trim();

                    }

                }
            }
        }

        customDatesToSaveLocalDate.size();
    }


    private void monthChoiceUntilRepeat(int repeatSeperateCounterInt) {
        customDatesToSaveLocalDate.clear();
        if (untilRepeatDate == null) {
            Toast.makeText(this, "Εισάγετε ημερομηνία λήξης.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (untilRepeatDate.isBefore(dateToCustom)) {
                    Toast.makeText(this, "Η ημερομηνία λήξης δε μπορεί να είναι μικρότερητου συμβάντος.", Toast.LENGTH_SHORT).show();

                } else {
                    customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfMonthChosenUntilRepeat(untilRepeatDate, dateToCustom, flagDate, monthSpinnerSelection, repeatSeperateCounterInt - 1);
                    textForEventEdit = textForMonth(repeatSeperateCounterInt, monthSpinnerSelection) + " μέχρι " + untilRepeatDate.toString().trim();
                }
            }
        }
        customDatesToSaveLocalDate.size();

    }

    private void yearChoiceUntilRepeat(int repeatSeperateCounterInt) {
        customDatesToSaveLocalDate.clear();
        if (untilRepeatDate == null) {
            Toast.makeText(this, "Εισάγετε ημερομηνία λήξης.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showChangeDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (untilRepeatDate.isBefore(dateToCustom)) {
                    Toast.makeText(this, "Η ημερομηνία λήξης δε μπορεί να είναι μικρότερητου συμβάντος.", Toast.LENGTH_SHORT).show();

                } else if (untilRepeatDate.isAfter(dateToCustom)) {
                    flagDate = dateToCustom;

                    while (flagDate.isBefore(untilRepeatDate)) {
                        customDatesToSaveLocalDate.add(flagDate);
                        flagDate = flagDate.plusYears(repeatSeperateCounterInt);
                    }
                    if (repeatSeperateCounterInt > 1) {
                        textForEventEdit = "Ανά " + repeatSeperateCounterInt + " χρόνια, μέχρι " + untilRepeatDate.toString().trim();

                    } else {
                        textForEventEdit = "Ανά " + repeatSeperateCounterInt + " χρόνο, μέχρι " + untilRepeatDate.toString().trim();

                    }
                } else {
                    Toast.makeText(this, "dayChoice error", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }


    private void dayChoiceRepeatCounter(int repeatSeperateCounterInt, int repeatCounterIntEnd) {
        customDatesToSaveLocalDate.clear();


        flagDate = dateToCustom;
        for (int i = 0; i < repeatCounterIntEnd; i++) {
            customDatesToSaveLocalDate.add(flagDate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flagDate = flagDate.plusDays(repeatSeperateCounterInt);
            }
        }

        if (repeatSeperateCounterInt > 1) {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μέρες, για " + repeatCounterIntEnd + " επαναλήψεις.";

        } else {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " μέρα,  για " + repeatCounterIntEnd + " επαναλήψεις.";

        }
    }

    private void weekChoiceRepeatCounter(int repeatSeperateCounterInt, int repeatCounterIntEnd) {
        customDatesToSaveLocalDate.clear();
        if (daysOfWeekAllFalse()) {
            customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfWeekChosenRepeatCounterAllFalse(dateToCustom, repeatSeperateCounterInt, repeatCounterIntEnd);
            textForEventEdit = textForWeek(repeatSeperateCounterInt) + " για " + repeatCounterIntEnd + " επαναλήψεις.";

        } else {
            customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfWeekChosenRepeatCounter(mondayFlag, tuesdayFlag, wednesdayFlag, thursdayFlag, fridayFlag, saturdayFlag, sundayFlag, dateToCustom, repeatSeperateCounterInt - 1, repeatCounterIntEnd);
            textForEventEdit = textForWeek(repeatSeperateCounterInt) + " για " + repeatCounterIntEnd + " επαναλήψεις.";

        }


        customDatesToSaveLocalDate.size();
    }

    private void monthChoiceRepeatCounter(int repeatSeperateCounterInt, int repeatCounterIntEnd) {
        customDatesToSaveLocalDate.clear();
        customDatesToSaveLocalDate = WeekDaysMonthDaysCustomRepeat.addDaysOfMonthChosenRepeatCounter(dateToCustom, monthSpinnerSelection, repeatSeperateCounterInt - 1, repeatCounterIntEnd);
        textForEventEdit = textForMonth(repeatSeperateCounterInt, monthSpinnerSelection) + " για " + repeatCounterIntEnd + " επαναλήψεις.";

        customDatesToSaveLocalDate.size();
    }

    private void yearChoiceRepeatCounter(int repeatSeperateCounterInt, int repeatCounterIntEnd) {
        customDatesToSaveLocalDate.clear();


        flagDate = dateToCustom;
        for (int i = 0; i < repeatCounterIntEnd; i++) {
            customDatesToSaveLocalDate.add(flagDate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flagDate = flagDate.plusYears(repeatSeperateCounterInt);
            }
        }
        if (repeatSeperateCounterInt > 1) {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " χρόνια, για " + repeatCounterIntEnd + " επαναλήψεις.";

        } else {
            textForEventEdit = "Ανά " + repeatSeperateCounterInt + " χρόνο, για " + repeatCounterIntEnd + " επαναλήψεις.";

        }
        customDatesToSaveLocalDate.size();
    }

    private void saveCustomRepeat() {

        if (!repeatSeperateCounter.getText().toString().equals("")) {
            repeatSeperateCounterInt = Integer.parseInt(repeatSeperateCounter.getText().toString());

            if (repeatSeperateCounterInt < 1) {
                Toast.makeText(this, "Συμπληρώστε επανάληψη κάθε μεγαλύτερο από το μηδέν.", Toast.LENGTH_SHORT).show();
            } else {
                if (untilRB.isChecked()) {
                    if (spinnerTimeSelection == 0) {
                        dayChoiceUntilRepeat(repeatSeperateCounterInt);
                    } else if (spinnerTimeSelection == 1) {
                        weekChoiceUntilRepeat(repeatSeperateCounterInt);

                    } else if (spinnerTimeSelection == 2) {
                        monthChoiceUntilRepeat(repeatSeperateCounterInt);
                    } else if (spinnerTimeSelection == 3) {
                        yearChoiceUntilRepeat(repeatSeperateCounterInt);
                    }

                } else if (repeatCounterRB.isChecked()) {
                    if (!repeatCounterET.getText().toString().equals("")) {
                        repeatCounterIntEnd = Integer.parseInt(repeatCounterET.getText().toString());

                        if (spinnerTimeSelection == 0) {
                            dayChoiceRepeatCounter(repeatSeperateCounterInt, repeatCounterIntEnd);
                        } else if (spinnerTimeSelection == 1) {
                            weekChoiceRepeatCounter(repeatSeperateCounterInt, repeatCounterIntEnd);
                        } else if (spinnerTimeSelection == 2) {
                            monthChoiceRepeatCounter(repeatSeperateCounterInt, repeatCounterIntEnd);
                        } else if (spinnerTimeSelection == 3) {
                            yearChoiceRepeatCounter(repeatSeperateCounterInt, repeatCounterIntEnd);
                        }
                    } else {
                        Toast.makeText(this, "Συμπληρώστε επανάληψη λήξης.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Επιλέξτε λήξη επανάληψης.", Toast.LENGTH_SHORT).show();
                }
            }


        } else {
            Toast.makeText(this, "Συμπληρώστε επανάληψη κάθε.", Toast.LENGTH_SHORT).show();
        }

        if (!customDatesToSaveLocalDate.isEmpty()) {
            Intent iEE = new Intent(CustomRepeatActivity.this, EventEdit.class);
            Intent iEU = new Intent(CustomRepeatActivity.this, Edit_Update_Activity.class);

            if (getIntent().hasExtra("flagBack")) {
                if (getIntent().getStringExtra("flagBack").equals("0")) {
                    if (getIntent().hasExtra("tittle") && getIntent().hasExtra("comment")) {

                        String tittle = getIntent().getStringExtra("tittle");
                        String comment = getIntent().getStringExtra("comment");


                        iEE.putExtra("tittle", tittle);
                        iEE.putExtra("comment", comment);


                    }
                    if (getIntent().hasExtra("stack")) {
                        String stack = getIntent().getStringExtra("stack");
                        iEE.putExtra("stack", stack);
                    }
                    iEE.putExtra("5", "null");
                    startActivity(iEE);
                } else if (getIntent().getStringExtra("flagBack").equals("1")) {

                    if (getIntent().hasExtra("tittle") && getIntent().hasExtra("comment")) {

                        String tittle = getIntent().getStringExtra("tittle");
                        String comment = getIntent().getStringExtra("comment");


                        iEU.putExtra("tittle", tittle);
                        iEU.putExtra("comment", comment);


                    }
                    if (getIntent().hasExtra("stack")) {
                        String stack = getIntent().getStringExtra("stack");
                        iEU.putExtra("stack", stack);
                    }
                    iEU.putExtra("5", "null");

                    startActivity(iEU);
                }
            }


        }
    }
}