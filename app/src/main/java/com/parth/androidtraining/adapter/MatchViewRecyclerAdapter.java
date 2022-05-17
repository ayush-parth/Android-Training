package com.parth.androidtraining.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parth.androidtraining.R;
import com.parth.androidtraining.model.enums.MatchCardPopulateType;
import com.parth.androidtraining.model.enums.MatchType;

import java.text.SimpleDateFormat;
import java.util.List;

public class MatchViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private MatchType matchType;
    private List<MatchCardPopulateType> matchCardPopulateTypes;
    private final SimpleDateFormat dateSDF = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat clubbedDateSDF = new SimpleDateFormat("EEE, d MMM");
    private final int CLUBBED_MATCHES_VIEW = 1;
    private final int MORE_MATCHES_VIEW = 2;


    public MatchViewRecyclerAdapter(Context context, List<MatchCardPopulateType> matchCardPopulateTypes, MatchType matchType) {
        this.context = context;
        this.matchCardPopulateTypes = matchCardPopulateTypes;
        this.matchType = matchType;
    }

    // determine which layout to use for the row
    @Override
    public int getItemCount() {
        return matchCardPopulateTypes == null ? 0: matchCardPopulateTypes.size();
    }



    @Override
    public int getItemViewType(int position) {
        return matchCardPopulateTypes.get(position).type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MatchCardPopulateType.DATE_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubbed_match_card, parent, false);
            return new ClubbedMatcheDateHolder(view);
        } else if(viewType == MatchCardPopulateType.MATCH_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_details_card, parent, false);
            return new ClubbedMatchesViewHolder(view);}
//        } if (viewType == MORE_MATCHES_VIEW) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_more_matches_view, parent, false);
//            return new MoreMatchesViewHolder(view);
//        } else {
            else{
            throw new RuntimeException("The type has to be CLUBBED MATCHES or MORE MATCHES");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case MatchCardPopulateType.DATE_TYPE:
                initClubbedMatchesDateLayout((ClubbedMatcheDateHolder) holder,holder.getAdapterPosition());
                break;
            case MatchCardPopulateType.MATCH_TYPE:
                initClubbedMatchesLayout((ClubbedMatchesViewHolder) holder,holder.getAdapterPosition());
                break;
            case MORE_MATCHES_VIEW:
                initMoreMatchesLayout((MoreMatchesViewHolder) holder, position);
                break;
            default:
                break;
        }
    }

    private void initClubbedMatchesDateLayout(ClubbedMatcheDateHolder holder, int pos) {
        holder.clubbedDateTextView.setText(matchCardPopulateTypes.get(pos).clubedDate);
    }

    private void initClubbedMatchesLayout(ClubbedMatchesViewHolder holder, int pos) {
        //holder.clubbedDateTextView.setText(matchCardPopulateTypes.get(pos).clubedDate);
    }

    private void initMoreMatchesLayout(MoreMatchesViewHolder holder, int pos) {
        String toastMessage;
        if(matchType.equals(MatchType.UPCOMING)){
            toastMessage = "Open All Upcoming Matches";
            holder.moreMatchMessageTV.setText("All Upcoming Matches");
        }else{
            toastMessage = "Open All Finished Matches";
            holder.moreMatchMessageTV.setText("All Finished Matches");
        }

        String finalToastMessage = toastMessage;
        holder.moreMatchTabLayout.setOnClickListener(v -> Toast.
                makeText(context,finalToastMessage,Toast.LENGTH_SHORT).show());
    }


    public class ClubbedMatcheDateHolder extends RecyclerView.ViewHolder {
        private TextView clubbedDateTextView;
        public ClubbedMatcheDateHolder(@NonNull View itemView) {
            super(itemView);
            clubbedDateTextView = itemView.findViewById(R.id.clubbed_match_date_tv);
        }
    }

    public class ClubbedMatchesViewHolder extends RecyclerView.ViewHolder {
        //private TextView clubbedDateTextView;
        public ClubbedMatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            //clubbedDateTextView = itemView.findViewById(R.id.clubbed_match_date_tv);
        }
    }

    public class MoreMatchesViewHolder extends RecyclerView.ViewHolder {
        public TextView moreMatchMessageTV;
        public RelativeLayout moreMatchTabLayout;
        public MoreMatchesViewHolder(View itemView) {
            super(itemView);
            moreMatchMessageTV = itemView.findViewById(R.id.more_match_message_tv);
            moreMatchTabLayout = itemView.findViewById(R.id.more_match_tab_layout);
        }
    }
}
