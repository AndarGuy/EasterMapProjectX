package com.example.mikhail.help.util;

import java.util.Calendar;

public class Message {
    private String text, name, id;
    private Calendar date;

    public Message(String name, String text, Calendar date) {
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Calendar getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
