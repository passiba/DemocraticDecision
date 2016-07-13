package org.crackeu.democraticdecision.vote;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;

public class VoteSuggestionActivity extends BaseVoteActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "VoteSuggestionActivity";

    private DatabaseReference mRef;


    private DatabaseReference mSuggestionRef;
    private RecyclerView mSuggestionsVotes;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<VoteSuggestion, VoteSuggestionHolder> mRecyclerViewAdapter;
    String selectedEuCountry;
    private Button mSendButton;
    private EditText mSuggestionVoteEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_suggestion);


        super.initializeEUCountries();
        // Set up ListView and Adapter
        ListView listView = (ListView) findViewById(R.id.listEuCountries);
        ArrayAdapter<String> adaptercountries = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, euCountries);
        listView.setAdapter(adaptercountries);
        listView.setOnItemClickListener(this);
        listView.setOnItemSelectedListener(this);
        listView.setOnItemLongClickListener(this);
        mRef = FirebaseDatabase.getInstance().getReference();

        mSuggestionRef = mRef.child("suggestions");
        mSuggestionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                VoteSuggestion suggestionvote = dataSnapshot.getValue(VoteSuggestion.class);
                Log.d(TAG, "suggestion added" + suggestionvote.getEucountry() + " " + suggestionvote.getName() + " " + suggestionvote.getSuggestion());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                VoteSuggestion suggestionvote = dataSnapshot.getValue(VoteSuggestion.class);
                Log.d(TAG, "suggestion changed" + suggestionvote.getEucountry() + " " + suggestionvote.getName() + " " + suggestionvote.getSuggestion());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                VoteSuggestion suggestionvote = dataSnapshot.getValue(VoteSuggestion.class);
                Log.d(TAG, "suggestion removed" + suggestionvote.getEucountry() + " " + suggestionvote.getName() + " " + suggestionvote.getSuggestion());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendButton = (Button) findViewById(R.id.button_send_eu_referendum_button);
        mSuggestionVoteEdit = (EditText) findViewById(R.id.editTextSuggestion);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "pekkatestaaaja";
                String name = "User " + uid.substring(0, 6);

                VoteSuggestion suggestionvote = new VoteSuggestion(name, uid, mSuggestionVoteEdit.getText().toString(), selectedEuCountry);
                mSuggestionRef.push().setValue(suggestionvote, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Log.e(TAG, "Failed to write message", databaseError.toException());
                        }
                    }
                });

                mSuggestionVoteEdit.setText("");
            }
        });

        mSuggestionsVotes = (RecyclerView) findViewById(R.id.voteSuggstionList);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mSuggestionsVotes.setHasFixedSize(false);
        mSuggestionsVotes.setLayoutManager(mManager);


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.cleanup();
        }
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        selectedEuCountry = euCountries.get(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        selectedEuCountry = euCountries.get(position);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedEuCountry = euCountries.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class VoteSuggestion {

        String name;
        String suggestion;
        String uid;
        String eucountry;

        public VoteSuggestion() {
        }

        public VoteSuggestion(String name, String uid, String suggestion, String country) {
            this.name = name;
            this.suggestion = suggestion;
            this.uid = uid;
            this.eucountry = country;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public String getEucountry() {
            return eucountry;
        }
    }

    public static class VoteSuggestionHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView fieldsuggestion;
        TextView fieldsuggester;

        TextView fieldcountry;


        public VoteSuggestionHolder(View itemView) {
            super(itemView);
            mView = itemView;
            fieldsuggestion = (TextView) itemView.findViewById(R.id.suggestion_text);
            fieldsuggester = (TextView) itemView.findViewById(R.id.suggester_text);
            fieldcountry = (TextView) itemView.findViewById(R.id.country_text);
        }

        public void setFieldsuggestion(String suggestion) {
            TextView fieldsuggestion = (TextView) itemView.findViewById(R.id.suggestion_text);
            fieldsuggestion.setText(suggestion);
        }

        public void setFieldsuggester(String fieldsuggester) {
            TextView suggester = (TextView) itemView.findViewById(R.id.suggester_text);
            suggester.setText(fieldsuggester);
        }

        public void setFieldcountry(String counrtry) {
            TextView fieldcounrtry = (TextView) itemView.findViewById(R.id.country_text);

            fieldcounrtry.setText(counrtry);
        }

    }

    private void attachRecyclerViewAdapter() {
        //Query lastFifty = mVotes.limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<VoteSuggestion, VoteSuggestionHolder>(
                VoteSuggestion.class, R.layout.suggestion, VoteSuggestionHolder.class, mSuggestionRef) {

            @Override
            public void populateViewHolder(VoteSuggestionHolder view, VoteSuggestion suggestion, int position) {

                view.setFieldcountry(suggestion.getEucountry());
                view.setFieldsuggester(suggestion.getName());
                view.setFieldsuggestion(suggestion.getSuggestion());
            }
        };

        // Scroll to bottom on new messages
        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mSuggestionsVotes, null, mRecyclerViewAdapter.getItemCount());
            }
        });

        mSuggestionsVotes.setAdapter(mRecyclerViewAdapter);


    }
}
