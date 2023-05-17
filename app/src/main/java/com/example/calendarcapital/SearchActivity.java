package com.example.calendarcapital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    ListView searchListView;
    ImageView BackMenuBtnSearch;

    HourAdapter hourAdapter;
    String stackNow;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initWidgets();
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                {
                    hourAdapter.clear();
                }else
                {
                    filterList(newText);
                }


                return false;
            }
        });



        setIntentFromMainActivity();
        BackMenuBtnSearch.setOnClickListener(v -> {
            Intent i = new Intent(SearchActivity.this, MainActivity.class);

            i.putExtra("stack", stackNow);

            String myTemp = CalendarUtils.selectedDate.toString();
            i.putExtra("tempDate", myTemp);

            startActivity(i);

        });


    }

    private void initWidgets()
    {
        searchView = findViewById(R.id.searchView);
        searchListView = findViewById(R.id.searchRecylerView);
        BackMenuBtnSearch = findViewById(R.id.BackMenuBtnSearch);

    }

    private void setIntentFromMainActivity()
    {
        if (getIntent().hasExtra("stack")) {
            stackNow = getIntent().getStringExtra("stack");
        }
    }

    private void filterList(String text)
    {

        List<HourEvent> filteredList = new ArrayList<>();

        ArrayList<HourEvent> test = AllEventsList.hourEventListAllEventsAndRepeatingEvents(getApplicationContext(),myDB);

        if(!test.isEmpty())
        {
            for (int i=0; i<test.size(); i++)
            {
                if (test.get(i).getEvents().get(0).getName().toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(test.get(i));
                }
            }
        }else
        {
            Toast.makeText(this, "Δεν υπάρχουν διαθέσιμα γεγονότα.", Toast.LENGTH_SHORT).show();
        }


        if (filteredList.isEmpty())
        {
            Toast.makeText(this, "Δεν υπάρχουν διαθέσιμα γεγονότα.", Toast.LENGTH_SHORT).show();
        }else
        {
            hourAdapter = new HourAdapter(getApplicationContext(), filteredList);
            searchListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
            searchListView.setAdapter(hourAdapter);
        }


    }
}