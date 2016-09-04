package org.crackeu.democraticdecision.vote.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.vote.model.VoteStats;

public class VoteViewHolder extends RecyclerView.ViewHolder {

    public TextView countryView;

    public ImageView countryFlagView;
    public TextView numVotesView;
    public TextView numYesVotesView;
    public TextView numNoVotesView;

    public VoteViewHolder(View itemView) {
        super(itemView);

        countryView = (TextView) itemView.findViewById(R.id.vote_country);
        countryFlagView = (ImageView) itemView.findViewById(R.id.vote_country_photo);
        numVotesView = (TextView) itemView.findViewById(R.id.num_votes);
        numYesVotesView = (TextView) itemView.findViewById(R.id.num_yes_votes);
        numNoVotesView = (TextView)itemView.findViewById(R.id.num_no_votes);
    }
    public void bindToPost(VoteStats vote, View.OnClickListener starClickListener) {
        countryView.setText(vote.getEucountry());
        numVotesView.setText(String.valueOf(vote.voteCount));
        numNoVotesView.setText(String.valueOf(vote.noVoteCount));
        numYesVotesView.setText(String.valueOf(vote.yesVoteCount));
        countryFlagView.setOnClickListener(starClickListener);
    }
}
