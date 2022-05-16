package com.parth.androidtraining.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.parth.androidtraining.R;
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.util.MatchCardTimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchCardView extends CardView {
    private View myCardView;
    private static final SimpleDateFormat timeSDF = new SimpleDateFormat("hh:mm aa");
    private static final SimpleDateFormat monthNameSDF = new SimpleDateFormat("MMMM");
    private static final SimpleDateFormat dateSDF = new SimpleDateFormat("d");
    private MatchType matchType;
    private JSONObject matchObject;
    private Context matchCardContext;

    private TextView matchDetailsTV;
    private ImageView team1ImageView;
    private TextView team1NameTextView;
    private TextView team1ScoreTextView;
    private TextView team1OverTextView;
    private ImageView team2ImageView;
    private TextView team2NameTextView;
    private TextView team2ScoreTextView;
    private TextView team2OverTextView;
    private TextView favourTeamNameTv;
    private TextView favourOddsTV;
    private TextView againstOddsTV;
    private RelativeLayout oddsLayout;
    private TextView matchTimeTV;
    private TextView matchDateTV;
    private View lineSeperatorForOddsLayout;

    public MatchCardView(Context context, MatchType matchType, JSONObject matchObject) {
        super(context);
        this.matchCardContext = context;
        this.matchType = matchType;
        this.matchObject = matchObject;
        initView(context);
    }

    private void initView(Context context){
        setBackgroundColor(Color.TRANSPARENT);
        myCardView = LayoutInflater.from(getContext()).inflate(R.layout.match_details_card,null);
        matchDetailsTV = myCardView.findViewById(R.id.match_details_tv);
        team1ImageView = myCardView.findViewById(R.id.team_1_image_view);
        team1NameTextView = myCardView.findViewById(R.id.team_1_tv);
        team1ScoreTextView = myCardView.findViewById(R.id.team_1_score_tv);
        team1OverTextView = myCardView.findViewById(R.id.team_1_over_tv);
        team2ImageView = myCardView.findViewById(R.id.team_2_image_view);
        team2NameTextView = myCardView.findViewById(R.id.team_2_tv);
        team2ScoreTextView = myCardView.findViewById(R.id.team_2_score_tv);
        team2OverTextView = myCardView.findViewById(R.id.team_2_over_tv);
        favourTeamNameTv = myCardView.findViewById(R.id.favour_team_name_tv);
        favourOddsTV = myCardView.findViewById(R.id.favour_odds_tv);
        againstOddsTV = myCardView.findViewById(R.id.against_odds_tv);
        oddsLayout = myCardView.findViewById(R.id.odds_layout);
        matchTimeTV = myCardView.findViewById(R.id.match_time_tv);
        matchDateTV = myCardView.findViewById(R.id.match_date_tv);
        lineSeperatorForOddsLayout = myCardView.findViewById(R.id.line_843);

        try{
            if(matchType.equals(MatchType.UPCOMING))
                setUpcomingMatchCard(context,matchObject);
            else
                setFinishedMatchCard(context,matchObject);
        }catch (JSONException e){
            e.printStackTrace();
        }
        addView(myCardView);
    }

    private void setMatchDetails(String matchDetails){
        matchDetailsTV.setText(matchDetails);
    }

    public void setTeam1Details(Context context, String url, String teamName){
        team1NameTextView.setText(teamName);
        Glide.with(context)
                .load(url)
                .into(team1ImageView);
    }

    public void setTeam2Details(Context context, String url, String teamName){
        team2NameTextView.setText(teamName);
        Glide.with(context)
                .load(url)
                .into(team2ImageView);
    }

    public void setOddsFavourTeamName(String teamName){
        favourTeamNameTv.setText(teamName);
    }

    public void setOddsFavourValue(String oddFavourValue){
        favourOddsTV.setText(oddFavourValue);
    }

    public void setOddsAgainstValue(String oddsAgainstValue){
        againstOddsTV.setText(oddsAgainstValue);
    }

    public void toggleVisibilityOfOddsLayout(){
        oddsLayout.setVisibility(VISIBLE);
        lineSeperatorForOddsLayout.setVisibility(VISIBLE);
    }

    public void showTimingsForUpcomingMatches(long matchTimestampInMillis){
        long currentTimestampInMillis = new Timestamp(System.currentTimeMillis()).getTime();
        if(matchTimestampInMillis > currentTimestampInMillis &&
                (matchTimestampInMillis-currentTimestampInMillis) < 10800000){
            matchTimeTV.setText("Starting in");
            matchDateTV.setTextColor(getResources().getColor(R.color.timer_color));
            matchDateTV.setTextSize(16);
            matchDateTV.setTypeface(Typeface.DEFAULT_BOLD);
            new MatchCardTimer(8000,matchDateTV);
        }else{
            Date dt = new Date(matchTimestampInMillis);
            matchTimeTV.setText(timeSDF.format(dt));
            matchDateTV.setText(dateSDF.format(dt)+" "+monthNameSDF.format(dt));
        }
    }

    public void makeScoreAndOverTextVisible(String score1, String over1,String score2, String over2){
        team1OverTextView.setVisibility(VISIBLE);
        team1ScoreTextView.setVisibility(VISIBLE);
        team2OverTextView.setVisibility(VISIBLE);
        team2ScoreTextView.setVisibility(VISIBLE);
        score1 = score1.replace("/","-");
        score2 = score2.replace("/","-");
        team1ScoreTextView.setText(score1);
        team1OverTextView.setText(over1);
        team2ScoreTextView.setText(score2);
        team2OverTextView.setText(over2);
    }

    public void setResult(String result){
        int index=result.indexOf("by");
        String winner = result.substring(0,index-1);
        String winStat = result.substring(index);
        matchTimeTV.setText(winner);
        matchTimeTV.setTextSize(16);
        matchTimeTV.setTextColor(getResources().getColor(R.color.winner_result_color));
        matchTimeTV.setTypeface(ResourcesCompat.getFont(matchCardContext,R.font.poppins_semibold));

        matchDateTV.setText(winStat);
        matchDateTV.setTextSize(11);
        matchDateTV.setTypeface(ResourcesCompat.getFont(matchCardContext,R.font.poppins));
    }

    public void setUpcomingMatchCard(Context context, JSONObject upcomingMatchDetails) throws JSONException {
        setMatchDetails(upcomingMatchDetails.getString("match_no"));
        setTeam1Details(context,upcomingMatchDetails.getString("t1flag"),
                upcomingMatchDetails.getString("t1"));
        setTeam2Details(context,upcomingMatchDetails.getString("t2flag"),
                upcomingMatchDetails.getString("t2"));
        showTimingsForUpcomingMatches((long) upcomingMatchDetails.get("t"));
        if(upcomingMatchDetails.has("odds")) {
            JSONObject oddsObject = upcomingMatchDetails.getJSONObject("odds");
            toggleVisibilityOfOddsLayout();
            setOddsFavourTeamName(oddsObject.getString("rate_team"));
            setOddsFavourValue(oddsObject.getString("rate"));
            setOddsAgainstValue(oddsObject.getString("rate2"));
        }
    }

    public void setFinishedMatchCard(Context context, JSONObject upcomingMatchDetails) throws JSONException {
        setMatchDetails(upcomingMatchDetails.getString("match_no"));
        setTeam1Details(context,upcomingMatchDetails.getString("t1flag"),
                upcomingMatchDetails.getString("t1"));
        setTeam2Details(context,upcomingMatchDetails.getString("t2flag"),
                upcomingMatchDetails.getString("t2"));
        makeScoreAndOverTextVisible(upcomingMatchDetails.getString("score1"),
                upcomingMatchDetails.getString("overs1"),upcomingMatchDetails.getString("score2")
        ,upcomingMatchDetails.getString("overs2"));
        setResult(upcomingMatchDetails.getString("result"));
    }
}
