package com.parth.androidtraining.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.parth.androidtraining.adapter.MatchViewRecyclerAdapter;
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.util.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class RecyclerViewMatchListFragment extends Fragment {

    private RecyclerView matchListRecyclerView;
    private TextView moreMatchMessageTV;
    private RelativeLayout moreMatchTabLayout;
    private MatchType matchType;
    private String url;

    public RecyclerViewMatchListFragment(MatchType matchType, String url) {
        // Required empty public constructor
        this.matchType = matchType;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recycler_view_match_list, container, false);
        moreMatchTabLayout = view.findViewById(R.id.more_match_tab_layout);
        moreMatchMessageTV = view.findViewById(R.id.more_match_message_tv);
        matchListRecyclerView = view.findViewById(R.id.matchListRV);

        String toastMessage = null;
        if(matchType.equals(MatchType.UPCOMING)){
            toastMessage = "Open All Upcoming Matches";
            moreMatchMessageTV.setText("All Upcoming Matches");
        }else{
            toastMessage = "Open All Finished Matches";
            moreMatchMessageTV.setText("All Finished Matches");
        }

        String finalToastMessage = toastMessage;
        moreMatchTabLayout.setOnClickListener(v -> Toast.
                makeText(getContext(),finalToastMessage,Toast.LENGTH_SHORT).show());

        getUpcomingMatches(url);

        matchListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
        MatchViewRecyclerAdapter matchViewRecyclerAdapter = new MatchViewRecyclerAdapter(getContext(),response,matchType);
        matchListRecyclerView.setAdapter(matchViewRecyclerAdapter);
    }
}