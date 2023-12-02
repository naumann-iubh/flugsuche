package com.lorbeer.flugsuche.searchRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class SearchResponse {
    @SerializedName("sessionToken")
    String sessionToken;

    @SerializedName("status")
    String status;

    @SerializedName("action")
    String action;

    @SerializedName("content")
    Content content;


    public String getSessionToken() {
        return sessionToken;
    }

    public String getStatus() {
        return status;
    }

    public String getAction() {
        return action;
    }

    public Content getContent() {
        return content;
    }

    public static class Content{
        @SerializedName("results")
        Results results;

        public Results getResults() {
            return results;
        }

        public void setResults(Results results) {
            this.results = results;
        }
    }

    public static class Results{
        @SerializedName("itineraries")
        Map<String, Itineraries> itineraries;

        @SerializedName("legs")
        Map<String, Legs> legs;

        public Map<String, Itineraries> getItineraries() {
            return itineraries;
        }

        public Map<String, Legs> getLegs() {
            return legs;
        }
    }

    public static class Legs{
        @SerializedName("originPlaceId")
        @Expose
        public String originPlaceId;
        @SerializedName("destinationPlaceId")
        @Expose
        public String destinationPlaceId;
        @SerializedName("departureDateTime")
        @Expose
        public DepartureDateTime departureDateTime;
        @SerializedName("arrivalDateTime")
        @Expose
        public ArrivalDateTime arrivalDateTime;
        @SerializedName("durationInMinutes")
        @Expose
        public Integer durationInMinutes;
        @SerializedName("stopCount")
        @Expose
        public Integer stopCount;
        @SerializedName("marketingCarrierIds")
        @Expose
        public List<String> marketingCarrierIds;
        @SerializedName("operatingCarrierIds")
        @Expose
        public List<String> operatingCarrierIds;
        @SerializedName("segmentIds")
        @Expose
        public List<String> segmentIds;

        public String getOriginPlaceId() {
            return originPlaceId;
        }

        public String getDestinationPlaceId() {
            return destinationPlaceId;
        }

        public DepartureDateTime getDepartureDateTime() {
            return departureDateTime;
        }

        public ArrivalDateTime getArrivalDateTime() {
            return arrivalDateTime;
        }

        public Integer getDurationInMinutes() {
            return durationInMinutes;
        }

        public Integer getStopCount() {
            return stopCount;
        }

        public List<String> getMarketingCarrierIds() {
            return marketingCarrierIds;
        }

        public List<String> getOperatingCarrierIds() {
            return operatingCarrierIds;
        }

        public List<String> getSegmentIds() {
            return segmentIds;
        }
    }

    public class ArrivalDateTime {

        @SerializedName("year")
        @Expose
        public Integer year;
        @SerializedName("month")
        @Expose
        public Integer month;
        @SerializedName("day")
        @Expose
        public Integer day;
        @SerializedName("hour")
        @Expose
        public Integer hour;
        @SerializedName("minute")
        @Expose
        public Integer minute;
        @SerializedName("second")
        @Expose
        public Integer second;

        public String getTime() {
            return String.format("%02d:%02d", hour, minute);
        }

        public Integer getYear() {
            return year;
        }

        public Integer getMonth() {
            return month;
        }

        public Integer getDay() {
            return day;
        }

        public Integer getHour() {
            return hour;
        }

        public Integer getMinute() {
            return minute;
        }

        public Integer getSecond() {
            return second;
        }
    }

    public class DepartureDateTime{

        @SerializedName("year")
        @Expose
        public Integer year;
        @SerializedName("month")
        @Expose
        public Integer month;
        @SerializedName("day")
        @Expose
        public Integer day;
        @SerializedName("hour")
        @Expose
        public Integer hour;
        @SerializedName("minute")
        @Expose
        public Integer minute;
        @SerializedName("second")
        @Expose
        public Integer second;

        public String getTime() {
            return String.format("%02d:%02d", hour, minute);
        }
        public Integer getYear() {
            return year;
        }

        public Integer getMonth() {
            return month;
        }

        public Integer getDay() {
            return day;
        }

        public Integer getHour() {
            return hour;
        }

        public Integer getMinute() {
            return minute;
        }

        public Integer getSecond() {
            return second;
        }
    }


    public static class Itineraries{

        @SerializedName("pricingOptions")
        List<PricingOptions> pricingOptions;

        @SerializedName("legIds")
        List<String> legIds;

        public List<PricingOptions> getPricingOptions() {
            return pricingOptions;
        }

        public List<String> getLegIds() {
            return legIds;
        }
    }

    public static class PricingOptions{
        @SerializedName("price")
        public Price price;
        @SerializedName("agentIds")
        public List<String> agentIds;
        @SerializedName("items")
        public List<Item> items;
        @SerializedName("transferType")
        public String transferType;
        @SerializedName("id")
        public String id;
        @SerializedName("pricingOptionFare")
        public Object pricingOptionFare;

        public Price getPrice() {
            return price;
        }

        public List<String> getAgentIds() {
            return agentIds;
        }

        public List<Item> getItems() {
            return items;
        }

        public String getTransferType() {
            return transferType;
        }

        public String getId() {
            return id;
        }

        public Object getPricingOptionFare() {
            return pricingOptionFare;
        }
    }

    public static class Fare{
        @SerializedName("segmentId")
        public String segmentId;
        @SerializedName("bookingCode")
        public String bookingCode;
        @SerializedName("fareBasisCode")
        public String fareBasisCode;

        public String getSegmentId() {
            return segmentId;
        }

        public String getBookingCode() {
            return bookingCode;
        }

        public String getFareBasisCode() {
            return fareBasisCode;
        }
    }

    public static class Item{
        @SerializedName("price")
        public Price price;
        @SerializedName("agentId")
        public String agentId;
        @SerializedName("deepLink")
        public String deepLink;
        @SerializedName("fares")
        public List<Fare> fares;

        public Price getPrice() {
            return price;
        }

        public String getAgentId() {
            return agentId;
        }

        public String getDeepLink() {
            return deepLink;
        }

        public List<Fare> getFares() {
            return fares;
        }
    }

    public static class Price{
        @SerializedName("amount")
        public String amount;
        @SerializedName("unit")
        public String unit;
        @SerializedName("updateStatus")
        public String updateStatus;

        public String getAmount() {
            return amount;
        }

        public String getUnit() {
            return unit;
        }

        public String getUpdateStatus() {
            return updateStatus;
        }
    }
}
