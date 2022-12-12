package com.example.duckdbfirst;

public record SpeedFragment(
    long trafficId,
    long linkId,
    int speed,
    long timestamp
) {

}
