package com.example.demo.entities;

import java.util.HashMap;

public class MainObject {

    String address;
    double lat;
    double lon;
    HashMap<String, AccessZone> accessZones = new HashMap<>();

    public MainObject() {}

    public MainObject (String address) {
        this.address = address;
    }

    public MainObject (String address, float lat, float lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

}
