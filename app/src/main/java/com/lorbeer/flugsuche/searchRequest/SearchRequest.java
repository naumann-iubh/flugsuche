package com.lorbeer.flugsuche.searchRequest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SearchRequest {
    private static final String TAG = "SearchRequest";

    private static SearchRequest INSTANCE;

     private static OkHttpClient client;

     private Map<String, SearchResponse> results = new HashMap<>();

    public static SearchRequest getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchRequest();
            client = new OkHttpClient();
        }
        return INSTANCE;
    }

    private static String API_KEY = "sh428739766321522266746152871799";

    private static String REQUEST_URL = "https://partners.api.skyscanner.net/apiservices/v3/flights/live/search/create";

    private static String POLL_URL = "https://partners.api.skyscanner.net/apiservices/v3/flights/live/search/poll";

    public void sendSearchRequest(List<String> originIata, String destinationIata, String startDate, String endDate, int persons, boolean twoWay) throws IOException, ExecutionException, InterruptedException {
         SearchQuery query = createQuery(originIata, destinationIata, startDate, endDate, persons, twoWay);

        String json = new Gson().toJson(query);

        Log.v(TAG, json);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder().url(REQUEST_URL).addHeader("x-api-key", API_KEY).post(body).build();
        Future<SearchResponse> futureSearchResponse = sendRequest(request);
        SearchResponse searchResponse = futureSearchResponse.get();

        while (searchResponse.content.results.itineraries.isEmpty()) {
            Thread.sleep(100);
            futureSearchResponse = poll(searchResponse.sessionToken);
            searchResponse = futureSearchResponse.get();
        }

        results.put(originIata.get(0)+"-"+destinationIata+"-"+startDate+"-"+endDate, searchResponse);
    }

    public void sendSearchRequestDummy(Context context, List<String> originIata, String destinationIata, String startDate, String endDate, int persons, boolean twoWay) throws IOException, ExecutionException, InterruptedException {

        final InputStream jsonStream = context.getAssets().open("testReturn.json");
        final int size = jsonStream.available();
        byte[] buffer = new byte[size];
        jsonStream.read(buffer);
        jsonStream.close();
        String json=  new String(buffer, "UTF-8");

        SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);

        results.put(originIata.get(0)+"-"+destinationIata+"-"+startDate+"-"+endDate, searchResponse);
    }

    public SearchResponse retrieveResults(String id){
        if (results.containsKey(id)) {
            return  results.get(id);
        }
        return null;
    }

    private Future<SearchResponse> sendRequest(Request request)  {
        CompletableFuture<SearchResponse> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            try (Response response = client.newCall(request).execute()) {
                String data = response.body().string();
                Log.v(TAG, "statuscode " + response.code());
                Log.v(TAG, data);
                SearchResponse searchResponse = new Gson().fromJson(data, SearchResponse.class);
                completableFuture.complete(searchResponse);
                return null;
            }
        });
        return completableFuture;
    }

    private Future<SearchResponse> poll(String sessionToken)  {
        Log.v(TAG, "poll");
        RequestBody body = RequestBody.create("", MediaType.parse("application/json"));
        Request request = new Request.Builder().url(POLL_URL+"/"+sessionToken).addHeader("x-api-key", API_KEY).post(body).build();
        CompletableFuture<SearchResponse> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            try (Response response = client.newCall(request).execute()) {
                String data = response.body().string();
                Log.v(TAG, "statuscode " + response.code());
                Log.v(TAG, data);
                SearchResponse searchResponse = new Gson().fromJson(data, SearchResponse.class);
                completableFuture.complete(searchResponse);
                return null;
            }
        });
        return completableFuture;
    }

    private SearchQuery createQuery(List<String> originIata, String destinationIata, String startDate, String endDate, int persons, boolean twoWay) {
        String[] startDateArray = startDate.split("-");
        int startDay = Integer.parseInt(startDateArray[0]);
        int startMonth = Integer.parseInt(startDateArray[1]);
        int startYear = Integer.parseInt(startDateArray[2]);
        ArrayList<SearchQuery.QueryLegs> querylegs = new ArrayList<>();
        for (String o : originIata) {
            SearchQuery.QueryLegs legs = new SearchQuery.QueryLegs(new SearchQuery.PlaceId(o), new SearchQuery.PlaceId(destinationIata), new SearchQuery.Date(startYear, startMonth, startDay));
            querylegs.add(legs);
        }

        if (twoWay) {
            String[] endDateArray = endDate.split("-");
            int endDay = Integer.parseInt(endDateArray[0]);
            int endMonth = Integer.parseInt(endDateArray[1]);
            int endYear = Integer.parseInt(endDateArray[2]);
            for (String o : originIata) {
                SearchQuery.QueryLegs endlegs = new SearchQuery.QueryLegs(new SearchQuery.PlaceId(destinationIata), new SearchQuery.PlaceId(o), new SearchQuery.Date(endYear, endMonth, endDay));
                querylegs.add(endlegs);
            }
        }
        return new SearchQuery(new SearchQuery.Query(querylegs, persons));
    }


}
