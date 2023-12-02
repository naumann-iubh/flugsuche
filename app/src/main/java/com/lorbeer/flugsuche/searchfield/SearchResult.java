package com.lorbeer.flugsuche.searchfield;

import com.google.gson.annotations.SerializedName;

public class SearchResult {

    @SerializedName("iata")
    String iata;

    @SerializedName("lon")
    String lon;
    @SerializedName("iso")
    String iso;
    @SerializedName("name")
    String name;

    @SerializedName("continent")
    String continent;
    @SerializedName("type")
    String type;
    @SerializedName("lat")
    String lat;

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "iata='" + iata + '\'' +
                ", lon='" + lon + '\'' +
                ", iso='" + iso + '\'' +
                ", name='" + name + '\'' +
                ", continent='" + continent + '\'' +
                ", type='" + type + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}
