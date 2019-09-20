package com.unp.maps_localizacao;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {

    public Duration duration;
    public Distance distance;
    public String endAddress;
    public String startAddress;
    public LatLng endLocation;
    public LatLng startLocation;

    public List<LatLng> points;



}
