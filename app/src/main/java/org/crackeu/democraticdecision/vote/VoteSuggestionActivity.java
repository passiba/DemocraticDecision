package org.crackeu.democraticdecision.vote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;

public class VoteSuggestionActivity extends AppCompatActivity {

    private static final String TAG = "VoteSuggestionActivity";

    private DatabaseReference mRef;


    private DatabaseReference mSuggestionRef;
    private RecyclerView mSuggestionsVotes;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<VoteSuggestion, VoteSuggestionHolder> mRecyclerViewAdapter;

    private Button mSendButton;
    private EditText mSuggestionVoteEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_suggestion);
        mRef = FirebaseDatabase.getInstance().getReference();

        mSuggestionRef = mRef.child("suggestions");
        mSendButton = (Button) findViewById(R.id.button_send_eu_referendum_button);
        mSuggestionVoteEdit = (EditText) findViewById(R.id.editTextSuggestion);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "pekkatestaaaja";
                String name = "User " + uid.substring(0, 6);

                VoteSuggestion suggestionvote = new VoteSuggestion(name, uid, mSuggestionVoteEdit.getText().toString(), "FI");
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
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
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
        TextView field = (TextView) mView.findViewById(R.id.editTextSuggestion);

        public VoteSuggestionHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.editTextSuggestion);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.editTextSuggestion);
            field.setText(text);
        }
    }
}
