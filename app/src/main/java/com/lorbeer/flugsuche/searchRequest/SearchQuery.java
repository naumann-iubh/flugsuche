package com.lorbeer.flugsuche.searchRequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchQuery {

    public SearchQuery(Query query) {
        this.query = query;
    }

    @SerializedName("query")
    Query query;

    public static class Query {

        public Query(List<QueryLegs> query_legs, int adults) {
            this.query_legs = query_legs;
            this.adults = adults;
        }

        @SerializedName("market")
        String market = "DE";

        @SerializedName("locale")
        String locale = "de-DE";

        @SerializedName("currency")
        String currency = "EUR";

        @SerializedName("cabin_class")
        String cabin_class = "CABIN_CLASS_ECONOMY";

        @SerializedName("adults")
        int adults;

        @SerializedName("query_legs")
        List<QueryLegs> query_legs;
    }

    public static class QueryLegs {
        public QueryLegs(PlaceId origin_place_id, PlaceId destination_place_id, Date date) {
            this.origin_place_id = origin_place_id;
            this.destination_place_id = destination_place_id;
            this.date = date;
        }

        @SerializedName("origin_place_id")
        PlaceId origin_place_id;


        @SerializedName("destination_place_id")
        PlaceId destination_place_id;

        @SerializedName("date")
        Date date;
    }

    public static class PlaceId{
        public PlaceId(String iata) {
            this.iata = iata;
        }
        @SerializedName("iata")
        String iata;
    }

    public static class Date{
        public Date(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @SerializedName("year")
        int year;
        @SerializedName("month")
        int month;
        @SerializedName("day")
        int day;
    }
}