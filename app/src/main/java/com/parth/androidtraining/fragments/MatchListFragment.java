package com.parth.androidtraining.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.util.VolleyRequestQueue;
import com.parth.androidtraining.views.MatchCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchListFragment extends Fragment {

    private RelativeLayout mainLayout;
    private RelativeLayout moreMatchTabLayout;
    private TextView moreMatchMessageTV;

    private static final SimpleDateFormat dateSDF = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat clubbedDateSDF = new SimpleDateFormat("EEE, d MMM");
    private View lastView = null;
    private String url;
    private MatchType matchType;

    public MatchListFragment(String url, MatchType matchType) {
        this.url = url;
        this.matchType = matchType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view =  inflater.inflate(R.layout.fragment_match_list, container, false);

        mainLayout = view.findViewById(R.id.upcoming_main_layout);
        moreMatchTabLayout = view.findViewById(R.id.more_match_tab_layout);
        moreMatchMessageTV = view.findViewById(R.id.more_match_message_tv);

        String toastMessage = null;
        if(matchType.equals(MatchType.UPCOMING)){
            toastMessage = "Open All Upcoming Matches";
        }else{
            toastMessage = "Open All Finished Matches";
        }

        String finalToastMessage = toastMessage;
        moreMatchTabLayout.setOnClickListener(v -> Toast.
                makeText(getContext(),finalToastMessage,Toast.LENGTH_SHORT).show());

        getUpcomingMatches(url);
        return view;
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

    public void generateDateTextView(String date){
        TextView textView = new TextView(getContext());
        textView.setId(View.generateViewId());
        textView.setText(getDayValue(date));
        textView.setTextColor(getResources().getColor(R.color.primeText));
        textView.setTextSize(14);
        textView.setId(View.generateViewId());
        textView.setLayoutParams(setLayoutParams());
        mainLayout.addView(textView);
        lastView = textView;
    }

    public void generateCardView(JSONObject matchObject){
        View cardView = new MatchCardView(getContext(), matchType ,matchObject);
        cardView.setId(View.generateViewId());
        cardView.setLayoutParams(setLayoutParams());
        mainLayout.addView(cardView);
        lastView = cardView;
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

    public void populateViews(JSONArray upcomingMatchResponse){
        for(int i=0;i< upcomingMatchResponse.length();i++){
            try {
                JSONObject clubedDateMatchObject = upcomingMatchResponse.getJSONObject(i);
                String clubedDate = clubedDateMatchObject.getString("date");
                generateDateTextView(clubedDate);
                JSONArray matchesOnSameDateArray = clubedDateMatchObject.getJSONArray("m");
                populateMatchCard(matchesOnSameDateArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showMoreMatchLayout();
    }

    public void populateMatchCard(JSONArray matchesOnSameDateArray){
        for(int i=0;i< matchesOnSameDateArray.length();i++){
            try {
                JSONObject matchObject = matchesOnSameDateArray.getJSONObject(i);
                generateCardView(matchObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public RelativeLayout.LayoutParams setLayoutParams(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,40,0,0);
        if(lastView != null) {
            layoutParams.addRule(RelativeLayout.BELOW, lastView.getId());
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        return layoutParams;
    }

    private void showMoreMatchLayout() {
        moreMatchTabLayout.setVisibility(View.VISIBLE);
        if(matchType.equals(MatchType.UPCOMING)){
            moreMatchMessageTV.setText("All Upcoming Matches");
        }else{
            moreMatchMessageTV.setText("All Finished Matches");
        }
    }

}