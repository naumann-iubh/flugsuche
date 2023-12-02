package com.lorbeer.flugsuche;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lorbeer.flugsuche.searchRequest.SearchResponse;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private static final String TAG = "CustomAdapter";

    Context context;
    SearchResponse response;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, SearchResponse res){
        this.context = applicationContext;
        this.response = res;
        this.inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return response.getContent().getResults().getItineraries().size();
    }

    @Override
    public Object getItem(int position) {
        return new ArrayList<>(response.getContent().getResults().getItineraries().values()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_listview, null);

        ArrayList<SearchResponse.Itineraries> itinerariesValues = new ArrayList<>(response.getContent().getResults().getItineraries().values());
        ArrayList<String> itinerariesKey = new ArrayList<>(response.getContent().getResults().getItineraries().keySet());

        TextView startTime = convertView.findViewById(R.id.startTime);
        TextView endTime = convertView.findViewById(R.id.endTime);
        TextView duration = convertView.findViewById(R.id.duration);
        TextView price = convertView.findViewById(R.id.price);

        double convertPrice = Double.parseDouble(itinerariesValues.get(position).getPricingOptions().get(0).price.amount);
        price.setText(String.valueOf(convertPrice/1000) + " €");

        String key = itinerariesKey.get(position);
        Log.v(TAG, "key " + key);
        if (key.split("\\|").length > 1) {
            String keyReturn = key.split("\\|")[1];
            key = key.split("\\|")[0];

            convertView.findViewById(R.id.returnFlight).setVisibility(View.VISIBLE);
            SearchResponse.Legs legs = response.getContent().getResults().getLegs().get(keyReturn);
            TextView startTime2 = convertView.findViewById(R.id.startTime2);
            TextView endTime2 = convertView.findViewById(R.id.endTime2);
            TextView duration2 = convertView.findViewById(R.id.duration2);

            startTime2.setText(legs.getDepartureDateTime().getTime());


            endTime2.setText(legs.getArrivalDateTime().getTime());

            duration2.setText(legs.getDurationInMinutes() +" min");

        }
        SearchResponse.Legs legs = response.getContent().getResults().getLegs().get(key);


        startTime.setText(legs.getDepartureDateTime().getTime());


        endTime.setText(legs.getArrivalDateTime().getTime());

        duration.setText(legs.getDurationInMinutes() +" min");
        return convertView;
    }
}
