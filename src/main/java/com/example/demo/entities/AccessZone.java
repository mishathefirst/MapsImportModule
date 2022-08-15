package com.example.demo.entities;

import java.util.HashMap;

public class AccessZone {
    public HashMap<MeanOfTransport, Double> transportMap = new HashMap<>(3);

    public void initialiseZone() {
        transportMap.put(MeanOfTransport.DEFAULT, 0.0);
        transportMap.put(MeanOfTransport.PUBLIC_TRANSPORT, 0.0);
        transportMap.put(MeanOfTransport.CAR, 0.0);
    }




}

enum MeanOfTransport {
    DEFAULT,
    PUBLIC_TRANSPORT,
    CAR
}
