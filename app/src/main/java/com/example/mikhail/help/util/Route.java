package com.example.mikhail.help.util;

import android.graphics.Bitmap;

public class Route {

    public static final String
            ID = "id",
            PLACES = "places",
            DESCRIPTION = "description",
            NAME = "name",
            ROUTES = "routes",
            GET = "get";

    private int id;
    private String name, description;
    private String[] places;
    private Bitmap image;


    public Route(Integer id, String name, String description, String[] places) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.places = places;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String[] getPlaces() {
        return places;
    }
}
