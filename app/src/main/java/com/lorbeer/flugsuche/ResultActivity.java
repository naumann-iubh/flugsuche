package com.lorbeer.flugsuche;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;


import androidx.core.app.NavUtils;

import com.lorbeer.flugsuche.databinding.ActivityResultBinding;
import com.lorbeer.flugsuche.searchRequest.SearchRequest;
import com.lorbeer.flugsuche.searchRequest.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";
    private ActivityResultBinding binding;
    private static String IDENTIFIER = "SEARCHRESULT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        showSpinner();


        Intent intent = getIntent();
        String key = intent.getStringExtra(IDENTIFIER);

        String origin = key.split("-")[0];
        String destination = key.split("-")[1];

        binding.flight.setText(origin +" <-> " + destination);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean dataLoaded = false;
                while (!dataLoaded) {
                    dataLoaded = retrieveResults(key) != null;
                    Log.v(TAG, "still waiting");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                SearchResponse response = retrieveResults(key);

                handler.post(() -> {

                    CustomAdapter adapter = new CustomAdapter(getApplicationContext(),response);
                    binding.simpleListView.setAdapter(adapter);
                    hideSpinner();
                    binding.simpleListView.setVisibility(View.VISIBLE);
                });

            }
        });

        binding.simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResponse.Itineraries itineraries = (SearchResponse.Itineraries) binding.simpleListView.getAdapter().getItem(position);
                String link = itineraries.getPricingOptions().get(0).getItems().get(0).deepLink;
                Uri uri = Uri.parse(link);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
    }



    private SearchResponse retrieveResults(String key) {
        return SearchRequest.getInstance().retrieveResults(key);
    }
    private void showSpinner(){
        ProgressBar progressSpinner = (ProgressBar)findViewById(R.id.pBar);
        progressSpinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        ProgressBar progressSpinner = (ProgressBar)findViewById(R.id.pBar);
        progressSpinner.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}