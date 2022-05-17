package com.parth.androidtraining.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.parth.androidtraining.R;
import com.parth.androidtraining.adapter.MatchViewRecyclerAdapter;
import com.parth.androidtraining.adapter.MultipleViewTypeAdapter;
import com.parth.androidtraining.model.enums.MatchCardPopulateType;
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.util.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewMatchListFragment extends Fragment {

    private RecyclerView matchListRecyclerView;
    private MatchType matchType;
    private String url;
    private final SimpleDateFormat dateSDF = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat clubbedDateSDF = new SimpleDateFormat("EEE, d MMM");


    public RecyclerViewMatchListFragment(MatchType matchType, String url) {
        this.matchType = matchType;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recycler_view_match_list, container, false);
        matchListRecyclerView = view.findViewById(R.id.matchListRV);
        matchListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getUpcomingMatches(url);

        return view;
    }

    public void getUpcomingMatches(String url){
        JsonArrayRequest upcomingMatchRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                populateViews(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Ayush",error.getMessage());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONArray response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        RequestQueue requestQueue = VolleyRequestQueue.getInstance(getContext()).getRequestQueue();
        requestQueue.add(upcomingMatchRequest);
    }

    private void populateViews(JSONArray response) {
        List<MatchCardPopulateType> matchCardPopulateTypes = convertJSONToListOfMatchCardPopulateType(response);
        matchCardPopulateTypes.add(new MatchCardPopulateType(MatchCardPopulateType.END_TYPE,null,null));
        MatchViewRecyclerAdapter matchViewRecyclerAdapter = new MatchViewRecyclerAdapter(getContext(),matchCardPopulateTypes
                ,matchType);
        MultipleViewTypeAdapter multipleViewTypeAdapter = new MultipleViewTypeAdapter(getContext(),
                matchCardPopulateTypes,matchType);
        matchListRecyclerView.setAdapter(multipleViewTypeAdapter);
    }

    private List<MatchCardPopulateType> convertJSONToListOfMatchCardPopulateType(JSONArray responseArray){
        List<MatchCardPopulateType> matchCardPopulateTypes = new ArrayList<>();

        for(int i=0;i<responseArray.length();i++){
            try {
                JSONObject clubbedMatchObject = responseArray.getJSONObject(i);
                String clubedDate = clubbedMatchObject.getString("date");
                matchCardPopulateTypes.add(new MatchCardPopulateType(MatchCardPopulateType.DATE_TYPE,
                        null,getDayValue(clubedDate)));
                JSONArray matchesOnSameDateArray = clubbedMatchObject.getJSONArray("m");
                for(int j=0;j< matchesOnSameDateArray.length();j++){
                    JSONObject matchObject = matchesOnSameDateArray.getJSONObject(j);
                    matchCardPopulateTypes.add(new MatchCardPopulateType(MatchCardPopulateType.MATCH_TYPE,
                            matchObject,null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return matchCardPopulateTypes;
    }

    public String getDayValue(String dateString){
        try
        {
            Date date= dateSDF.parse(dateString);
            clubbedDateSDF.applyPattern("EEE, d MMMM");
            String str = clubbedDateSDF.format(date);
            if(DateUtils.isToday(date.getTime())){
                return "Today"+str.substring(3);
            }else if(DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS)){
                return "Tomorrow"+str.substring(3);
            }else{
                return str;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}