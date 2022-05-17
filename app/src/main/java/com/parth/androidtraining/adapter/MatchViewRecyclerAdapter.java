package com.parth.androidtraining.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parth.androidtraining.R;
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.views.MatchCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchViewRecyclerAdapter extends RecyclerView.Adapter<MatchViewRecyclerAdapter.ViewHolder> {
    private Context context;
    private JSONArray responseArray;
    private MatchType matchType;
    private static final SimpleDateFormat dateSDF = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat clubbedDateSDF = new SimpleDateFormat("EEE, d MMM");

    public MatchViewRecyclerAdapter(Context context, JSONArray responseArray, MatchType matchType) {
        this.context = context;
        this.responseArray = responseArray;
        this.matchType = matchType;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubbed_match_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject clubbedMatchObject = responseArray.getJSONObject(position);
            holder.populateCard(clubbedMatchObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return responseArray == null ? 0: responseArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clubbedDateTextView;
        LinearLayout clubbedMatchLL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clubbedDateTextView = itemView.findViewById(R.id.clubbed_match_date_tv);
            clubbedMatchLL = itemView.findViewById(R.id.clubbed_match_ll);
        }

        public void populateCard(JSONObject clubbedMatchObject) {
            try {
                String clubedDate = clubbedMatchObject.getString("date");
                clubbedDateTextView.setText(getDayValue(clubedDate));
                JSONArray matchesOnSameDateArray = clubbedMatchObject.getJSONArray("m");
                populateMatchCard(matchesOnSameDateArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        public void generateCardView(JSONObject matchObject){
            View cardView = new MatchCardView(context, matchType ,matchObject);
            cardView.setId(View.generateViewId());
            cardView.setLayoutParams(setLayoutParams());
            clubbedMatchLL.addView(cardView);
        }

        public LinearLayout.LayoutParams setLayoutParams(){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,40,0,0);
            return layoutParams;
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

}
