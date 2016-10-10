package org.crackeu.democraticdecision.vote.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class CountryNoVotesFragment extends PostListFragment {

    public CountryNoVotesFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // Yes votes
        /*String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_posts_query]
        databaseReference.orderByChild("voteCount").startAt(1);*/

        return databaseReference.orderByChild("noVoteCount").startAt(1);
    }
}
