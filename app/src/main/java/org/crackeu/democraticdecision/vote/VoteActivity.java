package org.crackeu.democraticdecision.vote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.auth.ChooserActivity;
import org.crackeu.democraticdecision.auth.GoogleSignInActivity;
import org.crackeu.democraticdecision.chart.PiePolylineChartVoteActivity;
import org.crackeu.democraticdecision.vote.model.VoteStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoteActivity extends BaseVoteActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private static final String TAG = "VoteActivity";
    protected DatabaseReference mRef;
    protected DatabaseReference mVoteRef;
    protected Query queryCountryTotalVotes, queryCountryYesvotes, queryContryNovotes;
    protected RadioButton mVoteYesRadioButton;
    protected RadioButton mVoteNoRadioButton;
    String selectedEuCountry;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private Button mSendButton;
    private boolean leaveEu;
    //private RecyclerView mVotes;
    private LinearLayoutManager mManager;
    private PieChart mChart;
    // private FirebaseRecyclerAdapter<VoteStats, VoteHolder> mRecyclerViewAdapter;

    private Typeface tf;

    private static synchronized void initializeVoteStatJsonDb() {
        DatabaseReference mVoteStatsReference = FirebaseDatabase.getInstance().getReference();
        mVoteStatsReference = mVoteStatsReference.child("stats");

        for (VoteStats stats : euCountriesstats) {
            stats.setEucontryKey(mVoteStatsReference.child(stats.getEucountry()).push().getKey());
            Map<String, Object> postValues = stats.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/stats/" + stats.getEucontryKey(), postValues);
            childUpdates.put("/country-stats/" + stats.getUid() + "/" + stats.getEucontryKey(), postValues);
            // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            mVoteStatsReference.updateChildren(childUpdates);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        super.initializeEUCountries();
        initializeVoteStatJsonDb();
        // Set up ListView and Adapter
        ListView listView = (ListView) findViewById(R.id.listEuCountries);
        ArrayAdapter<String> adaptercountries = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, euCountries);
        listView.setAdapter(adaptercountries);
        listView.setOnItemClickListener(this);
        listView.setOnItemSelectedListener(this);
        listView.setOnItemLongClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI();
            }
        });

        mFirebaseUser = mAuth.getCurrentUser();


        mRef = FirebaseDatabase.getInstance().getReference();
        mVoteRef = mRef.child("votes");

        //queryContryNovotes=queryCountryTotalVotes.orderByChild(INDEX_LEAVE).equalTo("false");


        mVoteRef.limitToLast(100);
        mVoteRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                long count = dataSnapshot.getChildrenCount();
                Vote vote = dataSnapshot.getValue(Vote.class);
                writeNewVoteStats(vote);
                Log.d(TAG, "vote added" + vote.eucountry + " " + vote.getName() + " " + vote.isLeaveEu());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Vote vote = dataSnapshot.getValue(Vote.class);
                Log.d(TAG, "vote changed" + vote.eucountry + " " + vote.getName() + " " + vote.isLeaveEu());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Vote vote = dataSnapshot.getValue(Vote.class);
                Log.d(TAG, "vote removed" + vote.eucountry + " " + vote.getName() + " " + vote.isLeaveEu());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSendButton = (Button) findViewById(R.id.button_send_eu_referendum_button);

        mVoteYesRadioButton = (RadioButton) findViewById(R.id.yesradioButton);
        mVoteYesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEu = true;
                mSendButton.setEnabled(true);
                // mSendButton.setEnabled(isSignedIn());
            }
        });
        mVoteNoRadioButton = (RadioButton) findViewById(R.id.noradioButton);
        mVoteNoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEu = false;
                mSendButton.setEnabled(true);
                //   mSendButton.setEnabled(isSignedIn());
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //if (isSignedIn()) {


                //String uid = mFirebaseUser.getUid();
                // String name = mFirebaseUser.getDisplayName();
                String uid = "pekkalitestaaja", name = "pekka testaaja";
                Vote vote = new Vote(name, uid, leaveEu, selectedEuCountry);

                mVoteRef.push().setValue(vote, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Log.e(TAG, "Failed to write message", databaseError.toException());
                        }
                    }
                });

                //}
            }

        });


        // mVotes = (RecyclerView) findViewById(R.id.voteList);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        //mVotes.setHasFixedSize(false);
        // mVotes.setLayoutManager(mManager);


        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(1, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);


    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Results");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);

        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(), 0);
        return s;
    }

    private void setSingleEUCountrydata(VoteStats votestas) {


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        PieData data = mChart.getData();

        if (data != null) {
            //int num = (data.getDataSetCount() + 1);

            float mult = 100;

            //for (int i = 0; i < num; i++) {

            if (BaseVoteActivity.euCountries.contains(votestas.getEucountry())) {

                int index = euCountries.indexOf(votestas.getEucountry());
                // entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mEuCountries[index % mEuCountries.length]));

                if (votestas.getYesVoteCount() > 0) {
                    entries.add(new PieEntry((float) (votestas.getYesVoteCount() * mult) + mult / 5, mEuCountries[index % mEuCountries.length]));
                }
                if (votestas.getNoVoteCount() > 0) {
                    entries.add(new PieEntry((float) (votestas.getNoVoteCount() * mult) + mult / 5, mEuCountries[index % mEuCountries.length]));
                }

            } else {

            }

            //}

            PieDataSet dataSet = new PieDataSet(entries, "Eu Referendum Voting Results");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);


            dataSet.setValueLinePart1OffsetPercentage(80.f);
            dataSet.setValueLinePart1Length(0.2f);
            dataSet.setValueLinePart2Length(0.4f);
            // dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            data.setDataSet(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(tf);
            mChart.setData(data);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    //float range 100
    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count; i++) {
            //entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mParties[i % mParties.length]));
            entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mEuCountries[i % mEuCountries.length]));

        }

        PieDataSet dataSet = new PieDataSet(entries, "Eu Referendum Voting Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        // dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedEuCountry = euCountries.get(position);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedEuCountry = euCountries.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        selectedEuCountry = euCountries.get(position);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.cleanup();
        }*/
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
        if (!isSignedIn()) {

        } else {
            //attachRecyclerViewAdapter();
        }
    }

    public boolean isSignedIn() {
        return (mAuth.getCurrentUser() != null);
    }

    public void updateUI() {
        // Sending only allowed when signed in
        mSendButton.setEnabled(isSignedIn());
        if (isSignedIn()) {
            Toast.makeText(VoteActivity.this, "Signed In",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(VoteActivity.this, "Signed out",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected Entry
     * @param h The corresponding highlight object that contains information
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;


        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }


    /*private void attachRecyclerViewAdapter() {
        //Query lastFifty = mVotes.limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<VoteStats, VoteHolder>(
                VoteStats.class, R.layout.vote, VoteHolder.class, mVoteRef) {

            @Override
            public void populateViewHolder(VoteHolder voteView, VoteStats vote, int position) {
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
    }*/

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.vote_explore_menu:
                mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(VoteActivity.this, "Signed In " + authResult.getUser(),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Sign in anonymously success:" + authResult.getUser());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Sign in anonymously failed :" + e);
                        Toast.makeText(VoteActivity.this, "Sign In Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(this, VoteActivity.class));
                return true;

            case R.id.choose_sign_in_menu:
                startActivity(new Intent(this, ChooserActivity.class));
                return true;

            case R.id.sign_out_menu:
                try {
                    mAuth.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    //throws IllegalStateexpeption as you are not yet sign in
                } catch (java.lang.IllegalStateException stateex) {
                    Log.d(TAG, "Sign out Failed:" + stateex);
                }

                mFirebaseUser = null;
                startActivity(new Intent(this, GoogleSignInActivity.class));
                return true;



            /*case R.id.sign_in_goolge_credientials_menu:
                startActivity(new Intent(this, GoogleSignInActivity.class));
                return true;

            case R.id.sign_in_facebook_credientials_menu:
                startActivity(new Intent(this, FacebookLoginActivity.class));
                return true;


            case R.id.sign_in_custom_menu:
                startActivity(new Intent(this, CustomAuthActivity.class));
                return true;

            case R.id.sign_in_emailpassword_menu:
                startActivity(new Intent(this, EmailPasswordActivity.class));
                return true;

            case R.id.sign_in_anomyous_menu:
                startActivity(new Intent(this, AnonymousAuthActivity.class));
                return true;*/

            case R.id.eu_referendumvote_menu:
                startActivity(new Intent(this, VoteActivity.class));
                return true;


            case R.id.eu_referendum_stats_menu:
                startActivity(new Intent(this, PiePolylineChartVoteActivity.class));
                return true;

            case R.id.eu_vote_suggestion_menu:
                startActivity(new Intent(this, VoteSuggestionActivity.class));
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeNewVoteStats(final Vote vote) {


        // Create new vote statistics at /stats/$userid/$postid and at
        // /posts/$postid simultaneously


        if (vote != null) {

            DatabaseReference mVoteStatsReference = FirebaseDatabase.getInstance().getReference();
            mVoteStatsReference = mVoteStatsReference.child("stats").child(vote.getEucountry());

            int index = euCountries.indexOf(vote.getEucountry());
            VoteStats stats = euCountriesstats.get(index);

            stats.isLeavingEuCount(vote.isLeaveEu());

            Map<String, Object> postValues = stats.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/stats/" + stats.getEucontryKey(), postValues);
            childUpdates.put("/country-stats/" + stats.getUid() + "/" + stats.getEucontryKey(), postValues);
            // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            mVoteStatsReference.updateChildren(childUpdates);
            // DatabaseReference mEucounrty=mVoteStatsReference.child(vote.getEucountry());

           /* mVoteStatsReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    org.crackeu.democraticdecision.vote.model.VoteStats vo = dataSnapshot.getValue( org.crackeu.democraticdecision.vote.model.VoteStats.class);


                    Log.d(TAG, "vote stats added" + vo.getUid() + " " + vo.getEucontryKey()+ " " +vo.getEucountry() );
                    setSingleEUCountrydata(vo);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    org.crackeu.democraticdecision.vote.model.VoteStats vo = dataSnapshot.getValue( org.crackeu.democraticdecision.vote.model.VoteStats.class);
                    Log.d(TAG, "vote stats added" + vo.getUid() + " " + vo.getEucontryKey()+ " " +vo.getEucountry() );
                    setSingleEUCountrydata(vo);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            // String uid = mFirebaseUser.getUid();
            //  String name = mFirebaseUser.getDisplayName();


            // Map<String, Object> postValues = v.toMap();
            // Map<String, Object> childUpdates = new HashMap<>();
            // childUpdates.put("/stats/" + country_key, postValues);
            // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            // mVoteStatsReference.updateChildren(childUpdates);
           /* mVoteStatsReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    org.crackeu.democraticdecision.vote.model.VoteStats p = mutableData.getValue(org.crackeu.democraticdecision.vote.model.VoteStats.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }

                    if (p.votes.containsKey(p.getEucontryKey())) {
                        // Unstar the post and remove self from stars
                        p.voteCount = p.voteCount + 1;
                        if(vote.isLeaveEu) {
                            p.yesVoteCount = p.yesVoteCount + 1;
                        }else {
                            p.noVoteCount = p.noVoteCount + 1;
                        }


                    } else {
                        // Star the post and add self to stars
                        //p.voteCount = p.voteCount + 1;
                        //p.votes.put(country_key, 1);
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

           /* mVoteStatsReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    org.crackeu.democraticdecision.vote.model.VoteStats p = mutableData.getValue(org.crackeu.democraticdecision.vote.model.VoteStats.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }

                    if (p.votes.containsKey(p.getEucontryKey())) {
                        // Unstar the post and remove self from stars
                        p.voteCount = p.voteCount + 1;
                        if(vote.isLeaveEu()) {
                            p.yesVoteCount = p.yesVoteCount + 1;
                        }else {
                            p.noVoteCount = p.noVoteCount + 1;
                        }


                    } else {

                        p.voteCount = p.voteCount + 1;
                        if(vote.isLeaveEu()) {
                            p.yesVoteCount = p.yesVoteCount + 1;
                        }else {
                            p.noVoteCount = p.noVoteCount + 1;
                        }
                        // Star the post and add self to stars

                        p.votes.put(p.getEucontryKey(), p.getEucountry());
                    }
                    // Set value and report transaction success
                     mutableData.setValue(p);
                    setSingleEUCountrydata(p);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                }
            });*/
            setSingleEUCountrydata(stats);
        }
    }
}
