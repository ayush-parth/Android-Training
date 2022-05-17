package com.parth.androidtraining.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.parth.androidtraining.R;
import com.parth.androidtraining.model.enums.MatchCardPopulateType;
import com.parth.androidtraining.model.enums.MatchType;
import com.parth.androidtraining.views.MatchCardView;

import java.text.SimpleDateFormat;
import java.util.List;

public class MultipleViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MatchCardPopulateType> matchCardPopulateTypes;
    private MatchType matchType;

    public MultipleViewTypeAdapter(Context context, List<MatchCardPopulateType> matchCardPopulateTypes, MatchType matchType) {
        this.context = context;
        this.matchCardPopulateTypes = matchCardPopulateTypes;
        this.matchType = matchType;
    }

    @Override
    public int getItemViewType(int position) {
        switch (matchCardPopulateTypes.get(position).type) {
            case 0:
                return MatchCardPopulateType.DATE_TYPE;
            case 1:
                return MatchCardPopulateType.MATCH_TYPE;
            case 2:
                return MatchCardPopulateType.END_TYPE;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case MatchCardPopulateType.DATE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubbed_match_card,
                        parent,false);
                return new DateTextTypeViewHolder(view);
            case MatchCardPopulateType.MATCH_TYPE:
                view = new MatchCardView(parent.getContext());
                return new MatchCardTypeViewHolder(view);
            case MatchCardPopulateType.END_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_more_matches_view,
                        parent,false);
                return new MoreMatchesEndTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MatchCardPopulateType matchCardPopulateType = matchCardPopulateTypes.get(position);
        if(matchCardPopulateType != null){
            switch (matchCardPopulateType.type){
                case MatchCardPopulateType.DATE_TYPE:
                    ((DateTextTypeViewHolder)holder).dateText
                            .setText(matchCardPopulateType.clubedDate);
                    break;
                case MatchCardPopulateType.MATCH_TYPE:
                    ((MatchCardTypeViewHolder)holder).matchCardView.populateMatchCardView(context,matchType,matchCardPopulateType.matchObject);
                    break;
                case MatchCardPopulateType.END_TYPE:
                    if(matchType.equals(MatchType.UPCOMING)){
                        ((MoreMatchesEndTypeViewHolder)holder).moreMatchesTV.setText("All Upcoming Matches");
                        ((MoreMatchesEndTypeViewHolder)holder).moreMatchesRV.setOnClickListener(v -> Toast.
                                makeText(context,"Open All Upcoming Matches",Toast.LENGTH_SHORT).show());
                    }else{
                        ((MoreMatchesEndTypeViewHolder)holder).moreMatchesTV.setText("All Finished Matches");
                        ((MoreMatchesEndTypeViewHolder)holder).moreMatchesRV.setOnClickListener(v -> Toast.
                                makeText(context,"Open All Finished Matches",Toast.LENGTH_SHORT).show());
                    }
            }
        }
    }

    @Override
    public int getItemCount() {
        return matchCardPopulateTypes.size();
    }


    public static class DateTextTypeViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        public DateTextTypeViewHolder(View itemView) {
            super(itemView);
            this.dateText = itemView.findViewById(R.id.clubbed_match_date_tv);
        }
    }

    public static class MatchCardTypeViewHolder extends RecyclerView.ViewHolder {
        MatchCardView matchCardView;

        public MatchCardTypeViewHolder(View itemView) {
            super(itemView);
            matchCardView = (MatchCardView) itemView;
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20,20,20,20);
            matchCardView.setLayoutParams(layoutParams);
        }
    }

    public static class MoreMatchesEndTypeViewHolder extends RecyclerView.ViewHolder {
        TextView moreMatchesTV;
        RelativeLayout moreMatchesRV;

        public MoreMatchesEndTypeViewHolder(View itemView) {
            super(itemView);
            moreMatchesTV = itemView.findViewById(R.id.more_match_message_tv);
            moreMatchesRV = itemView.findViewById(R.id.more_match_tab_layout);
        }
    }
}
