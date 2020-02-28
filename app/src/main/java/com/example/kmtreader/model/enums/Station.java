package com.example.kmtreader.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Station {
    CKI("Cikini", 8),
    GDD("Gondangdia", 7),
    GMR("Gambir", 6),
    JAKK("Jakarta Kota", 1),
    JUA("Juanda", 5),
    JYK("Jayakarta", 2),
    MGB("Mangga Besar", 3),
    MRI("Manggarai", 9),
    SRP("Serpong", 83),
    SUD("Sudirman", 84),
    SW("Sawah Besar", 4),
    TEB("Tebet", 16),
    THB("Tanah Abang", 80);

    private String mStationName;

    private int mStationCode;

    Station(String stationName, int stationCode) {
        mStationName = stationName;
        mStationCode = stationCode;
    }

    public String getStationName() {
        return mStationName;
    }

    public int getStationCode() {
        return mStationCode;
    }

    public static Map<Integer, Station> getStationMap() {
        List<Station> stationList = Arrays.asList(values());
        return stationList.stream().collect(Collectors.toMap(Station::getStationCode, Function.identity()));
    }
}
