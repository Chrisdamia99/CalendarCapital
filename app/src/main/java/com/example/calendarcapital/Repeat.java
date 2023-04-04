package com.example.calendarcapital;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Repeat {


    private String id;
    private String event_id;
    private LocalDate repeatDate;

    public Repeat(String id, String event_id, LocalDate repeatDate) {
        this.id = id;
        this.event_id = event_id;
        this.repeatDate = repeatDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public LocalDate getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(LocalDate repeatDate) {
        this.repeatDate = repeatDate;
    }
}
