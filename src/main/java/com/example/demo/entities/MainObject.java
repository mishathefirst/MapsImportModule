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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, AccessZone> getAccessZones() {
        return accessZones;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessZones(HashMap<String, AccessZone> accessZones) {
        this.accessZones = accessZones;
    }
}
