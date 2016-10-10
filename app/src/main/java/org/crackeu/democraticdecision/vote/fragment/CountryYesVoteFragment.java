package org.crackeu.democraticdecision.vote.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class CountryYesVoteFragment extends PostListFragment {

    public CountryYesVoteFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All yes counts
        return databaseReference.orderByChild("yesVoteCount").startAt(1);
    }
}
