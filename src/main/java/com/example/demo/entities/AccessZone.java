package com.example.demo.entities;

import java.util.HashMap;

public class AccessZone {
    public HashMap<MeanOfTransport, Double> transportMap = new HashMap<>(3);

    public void initialiseZone(Double PedestrianDistance, Double PublicTransportDistance, Double CarDistance) {
        transportMap.put(MeanOfTransport.PEDESTRIAN, PedestrianDistance);
        transportMap.put(MeanOfTransport.PUBLIC_TRANSPORT, PublicTransportDistance);
        transportMap.put(MeanOfTransport.CAR, CarDistance);
    }

    public HashMap<MeanOfTransport, Double> getTransportMap() {
        return transportMap;
    }

}

enum MeanOfTransport {
    PEDESTRIAN,
    PUBLIC_TRANSPORT,
    CAR
}
