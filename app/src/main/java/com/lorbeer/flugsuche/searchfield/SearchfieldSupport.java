package com.lorbeer.flugsuche.searchfield;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class SearchfieldSupport {

    private static String loadJsonFromAssets(Context context) throws IOException {
        final InputStream jsonStream = context.getAssets().open("airports.json");
        final int size = jsonStream.available();
        byte[] buffer = new byte[size];
        jsonStream.read(buffer);
        jsonStream.close();
        return new String(buffer, "UTF-8");
    }

    public static List<SearchResult> getSearchFields(Context context) throws IOException {
        final Type listType = new TypeToken<ArrayList<SearchResult>>() {
        }.getType();
        return new Gson().fromJson(loadJsonFromAssets(context), listType);
    }


}
