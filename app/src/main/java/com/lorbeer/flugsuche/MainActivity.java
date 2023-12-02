package com.lorbeer.flugsuche;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.lorbeer.flugsuche.databinding.ActivityMainBinding;
import com.lorbeer.flugsuche.searchRequest.SearchRequest;
import com.lorbeer.flugsuche.searchfield.SearchResult;
import com.lorbeer.flugsuche.searchfield.SearchfieldSupport;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static String IDENTIFIER = "SEARCHRESULT";

    private ActivityMainBinding binding;

    private List<SearchResult> possibleAirports = new ArrayList<>();
    private static final String FINE_LOCATION =android. Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GPSTracker tracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapter = possibleSearchterms();

        binding.departure.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        binding.departure.setAdapter(adapter);
        binding.departure.setThreshold(1);
        binding.arrival.setAdapter(adapter);
        binding.arrival.setThreshold(1);

        setSupportActionBar(binding.toolbar);
        String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);

            tracker = new GPSTracker(this);

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                String startDate = binding.abflugdatePicked.getText().toString();
                String endDate = binding.ankunftdatePicked.getText().toString();
                String count = binding.count.getText().toString();
                boolean twoWay = binding.twoWay.isChecked();

                String origins = binding.departure.getText().toString();
                List<String> cleanOrigins = new ArrayList<>();
                if (origins.length() == 0) {
                    final String close = getClosestAirport();
                    Log.v(TAG, "close " + close);
                    cleanOrigins.add(close);
                }
                if (origins.length() > 0) {
                    for (String o : origins.trim().split(",")) {
                        cleanOrigins.add(cleanupIatas(o));
                    }
                }
                final String arrival = cleanupIatas(binding.arrival.getText().toString());
                intent.putExtra(IDENTIFIER, cleanOrigins.get(0)+"-" + arrival+"-" + startDate+"-" + endDate);

                requestResults(cleanOrigins, arrival, startDate, endDate, Integer.parseInt(count), twoWay);

                startActivity(intent);
            }
        });

        binding.departureDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.abflugdatePicked.setText(String.format(Locale.GERMAN, "%d-%d-%d", dayOfMonth, (month + 1), year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        binding.arrivalDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.ankunftdatePicked.setText(String.format(Locale.GERMAN, "%d-%d-%d", dayOfMonth, (month + 1), year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }



    private String getClosestAirport() {
        Log.v(TAG,"closestToAirport");
        final Map<Double, SearchResult> distanceMap = new HashMap<>();
        if (tracker.getIsGPSTrackingEnabled()) {
            Location current = tracker.location;
            for (SearchResult result : possibleAirports) {
                if (result.getLat() != null && result.getLon() != null) {
                    Location airport = new Location("Airport");
                    airport.setLongitude(Double.parseDouble(result.getLon()));
                    airport.setLatitude(Double.parseDouble(result.getLat()));
                    double distance = current.distanceTo(airport);
                    distanceMap.put(distance, result);
                }
            }
        }
        Map<Double, SearchResult> sortedDistanceMap = new TreeMap<>(distanceMap);
        return new ArrayList<>(sortedDistanceMap.values()).get(0).getIata();
    }

    private void requestResults(List<String> origin, String arrival, String startDate, String endDate, int count, boolean twoway) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SearchRequest request = SearchRequest.getInstance();
                try {
                    if (count == 99) {
                        request.sendSearchRequestDummy(getApplicationContext(),origin, arrival, startDate, endDate, count, twoway);
                    } else {
                        request.sendSearchRequest(origin, arrival, startDate, endDate, count, twoway);
                    }

                } catch (IOException | ExecutionException | InterruptedException e) {
                    Log.v(TAG, e.getMessage());
                }
            }
        });

        thread.start();
    }

    private String cleanupIatas(String searchTerm) {
        if (searchTerm.length() == 3) {
            searchTerm = searchTerm.toUpperCase();
        } else if (searchTerm.length() > 3) {
            final String search = searchTerm;
            Optional<SearchResult> result = possibleAirports.stream().filter(airport -> airport.getName().equals(search)).findFirst();
            if (!result.isPresent()) {
                Toast.makeText(getApplicationContext(), "Can not find airport " + searchTerm, Toast.LENGTH_SHORT).show();
            }
            searchTerm = result.get().getIata();
        }

        return searchTerm;
    }

    private ArrayAdapter<String> possibleSearchterms() {
        try {
            possibleAirports = SearchfieldSupport.getSearchFields(getApplicationContext());
        } catch (IOException e) {
            Toast.makeText(this, "Not able to load iata", Toast.LENGTH_SHORT).show();
        }

        Log.v(TAG, "possible Airports " + possibleAirports.size());
        Log.v(TAG, "possible Airports " + possibleAirports.get(0).toString());
        List<String> iatasAndNames = possibleAirports.stream().map(SearchResult::getIata).collect(Collectors.toList());
        iatasAndNames.addAll(possibleAirports.stream().map(SearchResult::getName).collect(Collectors.toList()));
        String[] iatas = iatasAndNames.toArray(new String[0]);
        Log.v(TAG, "iatas sisze " + iatas.length);

        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, iatas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}