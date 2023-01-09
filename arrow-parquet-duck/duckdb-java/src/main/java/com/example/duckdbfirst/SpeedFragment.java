package com.example.duckdbfirst;

public class SpeedFragment{
    long trafficId;
    long linkId;
    int speed;
    long timestamp;

  public SpeedFragment(long trafficId, long linkId, int speed, long timestamp) {
    this.trafficId = trafficId;
    this.linkId = linkId;
    this.speed = speed;
    this.timestamp = timestamp;
  }
}
