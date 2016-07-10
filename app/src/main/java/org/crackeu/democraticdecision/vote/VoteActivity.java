package org.crackeu.democraticdecision.vote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;

import java.util.ArrayList;

public class VoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "VoteActivity";


    ArrayList<String> euCountries = new ArrayList<>();

    private DatabaseReference mRef;
    private DatabaseReference mVoteRef;

    private FirebaseAuth mAuth;
    private Button mSendButton;

    protected RadioButton mVoteYesRadioButton;
    protected RadioButton mVoteNoRadioButton;

    private boolean leaveEu;
    String selectedEuCountry = "Finland";
    private RecyclerView mVotes;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Vote, VoteHolder> mRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        initializeEUCountries();
        // Set up ListView and Adapter
        ListView listView = (ListView) findViewById(R.id.listEuCountries);
        ArrayAdapter<String> adaptercountries = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, euCountries);
        listView.setAdapter(adaptercountries);
        listView.setOnItemClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI();
            }
        });


        mRef = FirebaseDatabase.getInstance().getReference();
        mVoteRef = mRef.child("votes");
        mSendButton = (Button) findViewById(R.id.button_send_eu_referendum_button);

        mVoteYesRadioButton = (RadioButton) findViewById(R.id.yesradioButton);
        mVoteYesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEu = true;
            }
        });
        mVoteNoRadioButton = (RadioButton) findViewById(R.id.noradioButton);
        mVoteNoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEu = false;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "pekkatestaaaja";
                String name = "User " + uid.substring(0, 6);
                Vote vote = new Vote(name, uid, leaveEu, selectedEuCountry);
                mVoteRef.push().setValue(vote, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Log.e(TAG, "Failed to write message", databaseError.toException());
                        }
                    }
                });

            }
        });

        mVotes = (RecyclerView) findViewById(R.id.voteList);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mVotes.setHasFixedSize(false);
        mVotes.setLayoutManager(mManager);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedEuCountry = euCountries.get(position);

    }

    private void initializeEUCountries() {
        this.euCountries.add("Austria");
        this.euCountries.add("Belgium");
        this.euCountries.add("Bulgaria");
        this.euCountries.add("Croatia");
        this.euCountries.add("Cyprus");
        this.euCountries.add("Czech Republic");
        this.euCountries.add("Denmark");
        this.euCountries.add("Estonia");
        this.euCountries.add("Finland");
        this.euCountries.add("France");
        this.euCountries.add("Germany");
        this.euCountries.add("Greece");
        this.euCountries.add("Hungary");
        this.euCountries.add("Ireland");
        this.euCountries.add("Italy");
        this.euCountries.add("Latvia");
        this.euCountries.add("Lithuania");
        this.euCountries.add("Luxembourg");
        this.euCountries.add("Malta");
        this.euCountries.add("Neatherlands");
        this.euCountries.add("Poland");
        this.euCountries.add("Portugal");
        this.euCountries.add("Romania");
        this.euCountries.add("Slovakia");
        this.euCountries.add("Slovenia");
        this.euCountries.add("Spain");
        this.euCountries.add("Sweden");

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
        // Default Database rules do not allow unauthenticated reads, so we need to
        // sign in before attaching the RecyclerView adapter otherwise the Adapter will
        // not be able to read any data from the Database.
        //if (!isSignedIn()) {
        signInAnonymously();
        // } else {
        attachRecyclerViewAdapter();
        //}
    }

    public boolean isSignedIn() {
        return (mAuth.getCurrentUser() != null);
    }

    public void updateUI() {
        // Sending only allowed when signed in
        //mSendButton.setEnabled(isSignedIn());

    }

    private void signInAnonymously() {
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(VoteActivity.this, "Signed In",
                                    Toast.LENGTH_SHORT).show();
                            attachRecyclerViewAdapter();
                        } else {
                            Toast.makeText(VoteActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void attachRecyclerViewAdapter() {
        //Query lastFifty = mVotes.limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Vote, VoteHolder>(
                Vote.class, R.layout.vote, VoteHolder.class, mVoteRef) {

            @Override
            public void populateViewHolder(VoteHolder voteView, Vote vote, int position) {
                if (vote.isLeaveEu)
                    voteView.setFieldvote("yes");
                else {
                    voteView.setFieldvote("no");
                }
                voteView.setFieldvotercounrtry(vote.getEucountry());
                voteView.setFieldvotername(vote.getName());
            }
        };

        // Scroll to bottom on new messages
        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mVotes, null, mRecyclerViewAdapter.getItemCount());
            }
        });

        mVotes.setAdapter(mRecyclerViewAdapter);


    }

    public static class Vote {

        String name;
        boolean isLeaveEu;
        String uid;
        String eucountry;

        public Vote() {
        }

        public Vote(String name, String uid, boolean leave, String country) {
            this.name = name;
            this.isLeaveEu = leave;
            this.uid = uid;
            this.eucountry = country;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public boolean isLeaveEu() {
            return isLeaveEu;
        }

        public String getEucountry() {
            return eucountry;
        }
    }

    public static class VoteHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView fieldvote;
        TextView fieldvotername;

        TextView fieldvotercounrtry;

        public VoteHolder(View itemView) {
            super(itemView);
            mView = itemView;
            fieldvote = (TextView) itemView.findViewById(R.id.vote_text);
            fieldvotername = (TextView) itemView.findViewById(R.id.vote_username_text);
            fieldvotercounrtry = (TextView) itemView.findViewById(R.id.vote_usercountry_text);
        }

        public void setFieldvote(String fieldvote) {
            TextView vote = (TextView) mView.findViewById(R.id.vote_text);
            vote.setText(fieldvote);
        }

        public void setFieldvotername(String fieldvotername) {
            TextView votername = (TextView) mView.findViewById(R.id.vote_username_text);
            votername.setText(fieldvotername);
        }

        public void setFieldvotercounrtry(String fieldvotercounrtry) {
            TextView country = (TextView) mView.findViewById(R.id.vote_usercountry_text);
            country.setText(fieldvotercounrtry);
        }
    }
}
