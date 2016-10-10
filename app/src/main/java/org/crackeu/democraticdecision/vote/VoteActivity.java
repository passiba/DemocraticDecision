package org.crackeu.democraticdecision.vote;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoteActivity extends BaseVoteActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private static final String TAG = "VoteActivity";
    protected DatabaseReference mRef;
    protected DatabaseReference mVoteRef,mVoteStatsReference;
    protected RadioButton mVoteYesRadioButton;
    protected RadioButton mVoteNoRadioButton;
    String selectedEuCountry;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private Button mSendButton;
    private boolean leaveEu;
    private RecyclerView mVotes;
    private LinearLayoutManager mManager;
    private PieChart mChart;
    private FirebaseRecyclerAdapter<Vote, VoteHolder> mRecyclerViewAdapter;

    private Typeface tf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        super.initializeEUCountries();

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
        mVoteStatsReference = mRef.child("stats");
        mVoteStatsReference.addChildEventListener(childEventListener);
        // initializeVoteStatDb();
        //queryContryNovotes=queryCountryTotalVotes.orderByChild(INDEX_LEAVE).equalTo("false");


        mVoteRef.limitToLast(100);
        mVoteRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                long count = dataSnapshot.getChildrenCount();
                Vote vote = dataSnapshot.getValue(Vote.class);

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
                mSendButton.setEnabled(isSignedIn());
            }
        });
        mVoteNoRadioButton = (RadioButton) findViewById(R.id.noradioButton);
        mVoteNoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEu = false;
                mSendButton.setEnabled(true);
                mSendButton.setEnabled(isSignedIn());
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isSignedIn()) {


                    // String uid = mFirebaseUser.getUid();
                    //  String name = mFirebaseUser.getDisplayName();
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
                    writeNewVoteStats(vote);
                }
            }

        });


      /*   mVotes = (RecyclerView) findViewById(R.id.voteList);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mVotes.setHasFixedSize(false);
        mVotes.setLayoutManager(mManager);
        */

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

    private void setSingleEUCountrydata(Stats votestas) {


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
        // if (!isSignedIn()) {

        // } else {
            //attachRecyclerViewAdapter();
        // }
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


    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");

    }


    private void writetoJsonDb(Stats stats)
    {
       /* DatabaseReference mVoteStatsReference = FirebaseDatabase.getInstance().getReference();
            mVoteStatsReference = mVoteStatsReference.child("stats")
       Map<String, Object> postValues = stats.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/stats/" + stats.getEucontryKey(), postValues);
        childUpdates.put("/country-stats/" + stats.getUid() + "/" + stats.getEucontryKey(), postValues);
        // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        mVoteStatsReference.updateChildren(childUpdates);*/
        stats.setEucontryKey(mVoteStatsReference.child(stats.getEucountry()).push().getKey());

       /* mVoteStatsReference.push().setValue(stats, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null) {
                    Log.e(TAG, "Failed to write vote statistics message", databaseError.toException());
                }
            }
        });*/
    }

    /* private void initializeVoteStatDb()
     {
         for(Stats stats:euCountrieStat)
         {
             String key = mVoteStatsReference.push().getKey();
             stats.setEucontryKey(key);
             //set key to colletion
             Map<String, Object> postValues = stats.toMap();
             Map<String, Object> childUpdates = new HashMap<>();
             childUpdates.put(key, postValues);
             mVoteStatsReference.updateChildren(childUpdates);
         }
     }*/
    private void addStatistics(Stats model) {
        String key = mVoteStatsReference.push().getKey();
        model.setEucontryKey(key);
        //set key to colletion
        euCountryKeys.put(model.getEucountry(), model);
        Map<String, Object> postValues = model.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mVoteStatsReference.updateChildren(childUpdates);

    }

    private Stats addVoteStatistics(Vote vote) {
        Stats stats = new Stats(vote.getEucountry());
        stats.isLeavingEuCount(vote.isLeaveEu());
        String key = mVoteStatsReference.push().getKey();
        stats.setEucontryKey(key);
        //set key to colletion
        euCountryKeys.put(vote.getEucountry(),stats);
        Map<String, Object> postValues = stats.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mVoteStatsReference.updateChildren(childUpdates);
        return stats;
    }

    public void updateVoteStatistics(Stats model) {

        mVoteStatsReference.child(model.getEucontryKey()).setValue(model);
    }

    private void writeNewVoteStats(Vote vote) {


        // Create new vote statistics at /stats/$userid/$postid and at
        // /posts/$postid simultaneously
        if (vote != null) {


            int index = euCountries.indexOf(vote.getEucountry());
            if(index!=-1)
            {
                Stats stats = new Stats(vote.getEucountry());
                if (!euCountryKeys.isEmpty() && euCountryKeys.containsKey(vote.getEucountry())) {
                    stats = euCountryKeys.get(vote.getEucountry());
                }


                //calculate statistics

               /* stats.setVoteCount(stats.getVoteCount()+1);
               if(vote.isLeaveEu())
                {
                    stats.setYesVoteCount(stats.getYesVoteCount()+1);
                }else
                {
                    stats.setNoVoteCount(stats.getNoVoteCount()+1);
                }*/
                stats.isLeavingEuCount(vote.isLeaveEu);
                if (euCountryKeys != null && !euCountryKeys.isEmpty() && euCountryKeys.containsKey(vote.getEucountry())) {
                    updateVoteStatistics(stats);

                   /* mVoteStatsReference.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Stats st=mutableData.getValue(Stats.class);
                            st.setVoteCount(st.getVoteCount()+1);
                            if(vote.isLeaveEu())
                            {
                                st.setYesVoteCount(st.getYesVoteCount()+1);
                            }else
                            {
                                st.setNoVoteCount(st.getNoVoteCount()+1);
                            }
                            mutableData.setValue(st);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        }
                    });*/
                } else {
                    addStatistics(stats);
                }
                setSingleEUCountrydata(stats);
            }
        }
    }


    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.d(TAG, "stats onChildAdded" +dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            Log.d(TAG, "stats onChildChanged" +dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "stats onChildRemoved" +dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext()," stats Could not update.",Toast.LENGTH_SHORT).show();

        }
    };


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
