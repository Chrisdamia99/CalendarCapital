package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.Calendar;

public class CustomRepeatActivity extends AppCompatActivity {
    private Spinner custom_repeat_spinner, monthChoiceSpinner;
    Button custom_repeat_btnSave;
    View sepView;
    TextView untilDate;
    TextView sundayChoice,mondayChoice,tuesdayChoice,wednesdayChoice,thursdayChoice,fridayChoice,saturdayChoice;
    EditText repeatCounterET, repeatSeperateCounter;
    RadioButton  untilRB, repeatCounterRB;
    LinearLayout daysOfWeekChoice, monthSpinnerChoiceLinLay;
    ImageButton custom_repeat_back_button, custom_repeat_refresh_button, cancelUntil;
    String originalTextUntilDate;
    LocalDate customRepeatDate;
    static boolean sundayFlag,mondayFlag,tuesdayFlag,wednesdayFlag,thursdayFlag,fridayFlag,saturdayFlag;
    static int repeatSeperateCounterInt;
    static int spinnerTimeSelection,monthSpinnerSelection;//0=day 1=week 2=month 3=year
    Calendar cCustom = Calendar.getInstance();
    private static LocalDate dateToCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_repeat_layout);

        initWidgets();
        setCustom_repeat_spinner();
        setAdapterMonthlySpinner();
        radioButtonsClickListeners();
        selectDaysOfWeek();
        initBooleansWeekDays();
        setDateFromIntent();
        cancelUntil.setVisibility(View.GONE);

        originalTextUntilDate = untilDate.getText().toString();

        custom_repeat_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iEE = new Intent(CustomRepeatActivity.this, EventEdit.class);
                Intent iEU = new Intent(CustomRepeatActivity.this, Edit_Update_Activity.class);
                Intent iM = new Intent(CustomRepeatActivity.this, MainActivity.class);

                if (getIntent().hasExtra("flagBack")) {
                    if (getIntent().getStringExtra("flagBack").equals("0")) {
                        startActivity(iEE);
                    } else if (getIntent().getStringExtra("flagBack").equals("1")) {
                        startActivity(iEU);
                    }
                } else {
                    Boolean myBool = true;
                    iM.putExtra("bool", myBool);
                    startActivity(iM);
                }


            }
        });

        custom_repeat_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllEventsList.reloadActivity(CustomRepeatActivity.this);
            }
        });

        custom_repeat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

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

                if (position==0)
                {
                    spinnerTimeSelection=0;
                }else if (position==1) {
                    spinnerTimeSelection = 1;
                }else if (position==2)
                {
                    spinnerTimeSelection=2;
                }else if (position==3)
                {
                    spinnerTimeSelection=3;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CustomRepeatActivity.this, "test ONNOTHING", Toast.LENGTH_SHORT).show();            }
        });


        monthChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0)
                {
                    monthSpinnerSelection=0;
                }else if (position==1)
                {
                    monthSpinnerSelection=1;
                }else if (position==2)
                {
                    monthSpinnerSelection=2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        repeatCounterRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                repeatCounterET.setEnabled(isChecked);

            }
        });

        untilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDate(cCustom.get(Calendar.YEAR), cCustom.get(Calendar.MONTH), cCustom.get(Calendar.DAY_OF_MONTH));
            }
        });

        cancelUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                untilDate.setText(originalTextUntilDate);
                cancelUntil.setVisibility(View.GONE);
            }
        });

        custom_repeat_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomRepeat();
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

    private void initBooleansWeekDays()
    {
        sundayFlag=false;
        mondayFlag=false;
        tuesdayFlag=false;
        wednesdayFlag=false;
        thursdayFlag=false;
        fridayFlag=false;
        saturdayFlag=false;

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
        ArrayAdapter<CharSequence> custom_monthly_repeat_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.custom_monthly_repeat_spinner, android.R.layout.simple_spinner_item);

        custom_monthly_repeat_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        monthChoiceSpinner.setAdapter(custom_monthly_repeat_spinner_adapter);
    }

    private void radioButtonsClickListeners() {


        untilRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (untilRB.isChecked()) {
                    repeatCounterRB.setChecked(false);
                }
            }
        });

        repeatCounterRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (repeatCounterRB.isChecked()) {

                    untilRB.setChecked(false);
                }
            }
        });
    }

    private void showChangeDate(int year, int month, int dayofmonth) {


        final DatePickerDialog StartTime = new DatePickerDialog(this, R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int trueMonth = monthOfYear + 1;

                if (trueMonth < 10 && dayOfMonth >= 10) {


                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                    cancelUntil.setVisibility(View.VISIBLE);


                } else if (dayOfMonth < 10 && trueMonth < 10) {


                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                    cancelUntil.setVisibility(View.VISIBLE);
                } else if (dayOfMonth < 10 && trueMonth >= 10) {

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);

                    untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                    cancelUntil.setVisibility(View.VISIBLE);


                } else {

                    LocalDate myDD = LocalDate.of(year, trueMonth, dayOfMonth);
                    untilDate.setText(CalendarUtils.formattedDateEventEdit(myDD));
                    cancelUntil.setVisibility(View.VISIBLE);

                }
                customRepeatDate = LocalDate.of(year, trueMonth, dayOfMonth);


            }


        }, year, month, dayofmonth);


        StartTime.setTitle("Select Date");
        StartTime.show();


    }

    private void selectDaysOfWeek()
    {

        sundayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sundayFlag)
                {
                    sundayFlag = true;
                    sundayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    sundayFlag = false;
                    sundayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }




            }
        });

        mondayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!mondayFlag)
                {
                    mondayFlag = true;
                    mondayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    mondayFlag = false;
                    mondayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }

            }
        });

        tuesdayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!tuesdayFlag)
                {
                    tuesdayFlag = true;
                    tuesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    tuesdayFlag = false;
                    tuesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }

            }
        });

        wednesdayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!wednesdayFlag)
                {
                    wednesdayFlag = true;
                    wednesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    wednesdayFlag = false;
                    wednesdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }

            }
        });

        thursdayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!thursdayFlag)
                {
                    thursdayFlag = true;
                    thursdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    thursdayFlag = false;
                    thursdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }

            }
        });

        fridayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!fridayFlag)
                {
                    fridayFlag = true;
                    fridayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    fridayFlag = false;
                    fridayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }
            }
        });

        saturdayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!saturdayFlag)
                {
                    saturdayFlag = true;
                    saturdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_grey));

                }else
                {
                    saturdayFlag = false;
                    saturdayChoice.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.simple_borders_rounded_white));

                }

            }
        });

    }

    private void setDateFromIntent()
    {
        Intent retriveIntent = getIntent();
        String localDate = retriveIntent.getStringExtra("date");
        if (localDate != null)
        {
            dateToCustom = CalendarUtils.stringToLocalDate(localDate);
        }
    }

private void endOfRepeat()
{
    if (untilRB.isChecked())
    {

    }else if (repeatCounterRB.isChecked())
    {

    }else
    {
        Toast.makeText(this, "error endOfRepeat", Toast.LENGTH_SHORT).show();
    }
}

    private void saveCustomRepeat()
    {
        repeatSeperateCounterInt = Integer.parseInt(repeatSeperateCounter.getText().toString());
        if (untilRB.isChecked() && customRepeatDate.compareTo(dateToCustom)<0)
        {
            Toast.makeText(this, "Η ημερομηνία λήξης δε μπορεί να είναι μικρότερητου συμβάντος.", Toast.LENGTH_SHORT).show();

        }else if (untilRB.isChecked() && (customRepeatDate.compareTo(dateToCustom)==0 || customRepeatDate.compareTo(dateToCustom)>0))
        {

        }

    }
}