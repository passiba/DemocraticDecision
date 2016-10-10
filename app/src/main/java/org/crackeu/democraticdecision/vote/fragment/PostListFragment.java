package org.crackeu.democraticdecision.vote.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.chart.PiePolylineChartVoteActivity;
import org.crackeu.democraticdecision.chart.PiePolylineChartVoteActivity.StatsHolder;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;
import org.crackeu.democraticdecision.vote.BaseVoteActivity.Stats;

//import org.crackeu.democraticdecision.vote.model.VoteStats;
//import org.crackeu.democraticdecision.vote.viewholder.VoteViewHolder;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Stats, StatsHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public PostListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_votes, container, false);
        //View rootView = inflater.inflate(R.layout.activity_pie_polyline_chart_vote, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mDatabase = mDatabase.child("stats");
        mRecycler = (RecyclerView) rootView.findViewById(R.id.voteStatsList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);


        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Stats, StatsHolder>(Stats.class, R.layout.votestats,
                StatsHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final StatsHolder viewHolder, final Stats model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostVoteActivity
                        Intent intent = new Intent(getActivity(), PiePolylineChartVoteActivity.class);
                        //intent.putExtra(PiePolylineChartVoteActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });
                viewHolder.bindToPost(model, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(getActivity(), PiePolylineChartVoteActivity.class);
                                //intent.putExtra(PiePolylineChartVoteActivity.EXTRA_POST_KEY, postKey);
                                startActivity(intent);

                            }
                        }
                );
/*
                // Determine if the current user has liked this post and set UI accordingly
                if (model.votes.containsKey(getUid())) {
                    viewHolder.countryFlagView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.countryFlagView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("stats").child(postRef.getKey());
                       // DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.getUid()).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                       // onStarClicked(userPostRef);
                    }
                });*/
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
      /*  postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
               VoteStats p = mutableData.getValue(VoteStats.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.votes.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.voteCount = p.voteCount - 1;
                    p.votes.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.voteCount = p.voteCount + 1;
                    p.votes.put(getUid(), 1);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });*/
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
