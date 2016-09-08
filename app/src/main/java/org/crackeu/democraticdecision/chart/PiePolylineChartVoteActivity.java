package org.crackeu.democraticdecision.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.auth.AnonymousAuthActivity;
import org.crackeu.democraticdecision.auth.ChooserActivity;
import org.crackeu.democraticdecision.auth.CustomAuthActivity;
import org.crackeu.democraticdecision.auth.EmailPasswordActivity;
import org.crackeu.democraticdecision.auth.FacebookLoginActivity;
import org.crackeu.democraticdecision.auth.GoogleSignInActivity;
import org.crackeu.democraticdecision.data.FirebaseRecyclerAdapter;
import org.crackeu.democraticdecision.vote.BaseVoteActivity;


import org.crackeu.democraticdecision.vote.VoteActivity;
import org.crackeu.democraticdecision.vote.VoteSuggestionActivity;
import org.crackeu.democraticdecision.vote.model.VoteStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PiePolylineChartVoteActivity extends BaseVoteActivity implements
        OnChartValueSelectedListener {

    private static final String TAG = " PiePolylineChartVoteActivity";

    private PieChart mChart;

    private Typeface tf;
    private RecyclerView mVoteStats;
    private LinearLayoutManager mManager;
    protected DatabaseReference mRef;
    protected DatabaseReference mVoteStatsReference;

    private FirebaseRecyclerAdapter<VoteStats, VoteStatsHolder> mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pie_polyline_chart_vote);

        mRef = FirebaseDatabase.getInstance().getReference();
        mVoteStatsReference = mRef.child("stats");
        mVoteStatsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                VoteStats stats = dataSnapshot.getValue(VoteStats.class);

                Log.d(TAG,"stats added " +stats.getEucountry()  + " "+stats.getEucountry());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });

       /* mChart = (PieChart) findViewById(R.id.chart1);
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

        setData(mEuCountries.length, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
        */

        mVoteStats = (RecyclerView) findViewById(R.id.voteStatsList);
        mVoteStats.setHasFixedSize(false);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mVoteStats.setHasFixedSize(false);
        mVoteStats.setLayoutManager(mManager);
        //mManager.addView(mChart);

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
    /*private void writetoJsonDb(VoteStats stats)
    {
        Map<String, Object> postValues = stats.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/stats/" + stats.getEucontryKey(), postValues);
        childUpdates.put("/country-stats/" + stats.getUid() + "/" + stats.getEucontryKey(), postValues);
        // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        mVoteStatsReference.updateChildren(childUpdates);
    }*/
    //float range 100
    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
       /* for (int i = 0; i < count; i++) {
            //entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mParties[i % mParties.length]));
            entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mEuCountries[i % mEuCountries.length]));

        }*/

        /*for(VoteStats stats: euCountriesstats)
        {
            if(stats.getVoteCount()>0)
            {
                int index = euCountries.indexOf(stats.getEucountry());
                entries.add(new PieEntry((float) (stats.getVoteCount() * mult) + mult / 5, mEuCountries[index % mEuCountries.length]));
                //writetoJsonDb(stats);
            }
        }*/

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

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
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

                startActivity(new Intent(this, VoteActivity.class));
                return true;

            case R.id.choose_sign_in_menu:
                startActivity(new Intent(this, ChooserActivity.class));
                return true;

            case R.id.sign_out_menu:

                startActivity(new Intent(this, VoteActivity.class));
                return true;

           /* case R.id.sign_in_goolge_credientials_menu:
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
                return true;

            case R.id.eu_referendumvote_menu:
                startActivity(new Intent(this, VoteActivity.class));
                return true;


            case R.id.eu_referendum_stats_menu:
                startActivity(new Intent(this, PiePolylineChartVoteActivity.class));
                return true;

            case R.id.eu_vote_suggestion_menu:
                startActivity(new Intent(this, VoteSuggestionActivity.class));
                return true;*/


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Query getQuery() {
        // All Statistics wiht votecount greater than zero
        return mVoteStatsReference.orderByChild("voteCount").startAt(1);
    }
    private void attachRecyclerViewAdapter() {
        //Query lastFifty = mVotes.limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<VoteStats, VoteStatsHolder>(
                VoteStats.class, R.layout.votestats, VoteStatsHolder.class, getQuery()) {

            @Override
            public void populateViewHolder(VoteStatsHolder view, VoteStats stats, int position) {


                view.setCountry_text(stats.getEucountry());
                view.setVoteCountTotal_text(String.valueOf(stats.getVoteCount()));

                view.setVoteCountYes_text(String.valueOf(stats.getYesVoteCount()));
                view.setVoteCountNo_text(String.valueOf(stats.getNoVoteCount()));
                if ( stats.getCountryflagPhotUrl() == null) {
                    view.euflagImageView.setImageDrawable(ContextCompat.getDrawable(PiePolylineChartVoteActivity.this,
                            R.drawable.ic_person_white));
                } /*else {

                    Glide.with(VoteSuggestionActivity.this)
                            .load(suggestion.getCountryflagPhotUrl())
                            .into(view.euflagImageView);
                }*/
                view.setSingleEUCountrydata(stats);
            }
        };

        // Scroll to bottom on new messages
       mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mVoteStats, null, mRecyclerViewAdapter.getItemCount());
            }
        });

        mVoteStats.setAdapter(mRecyclerViewAdapter);


    }
    public static class VoteStatsHolder  extends RecyclerView.ViewHolder implements
            OnChartValueSelectedListener{
        View mView;
        public CircleImageView euflagImageView;

        private PieChart mChart;
        private Typeface tf;

        protected String[] mEuCountries = new String[]{
                "Austria", "Belgium", "Bulgaria", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Estonia",
                "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy", "Latvia",
                "Lithuania", "Luxembourg", "Malta", "Neatherlands", "Poland", "Portugal", "Romania", "Slovakia",
                "Slovenia", "Spain", "Sweden"
        };


        TextView country_text,voteCountTotal_text,voteCountYes_text,voteCountNo_text;

        public VoteStatsHolder(View itemView) {
            super(itemView);
            mView = itemView;
            country_text = (TextView) itemView.findViewById(R.id.country_text);
            voteCountTotal_text = (TextView) itemView.findViewById(R.id.voteCountTotal_text);
            voteCountYes_text= (TextView) itemView.findViewById(R.id.voteCountYes_text);
            voteCountNo_text= (TextView) itemView.findViewById(R.id.voteCountNo_text);
            euflagImageView = (CircleImageView) itemView.findViewById(R.id.flagImageView);


            mChart = (PieChart) itemView.findViewById(R.id.chart1);
            mChart.setUsePercentValues(true);
            mChart.setDescription("");
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            tf = Typeface.createFromAsset(mView.getContext().getAssets(), "OpenSans-Regular.ttf");

            mChart.setCenterTextTypeface(Typeface.createFromAsset(mView.getContext().getAssets(), "OpenSans-Light.ttf"));
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



            setData(1, 100);

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setEnabled(false);
        }

        public void setCountry_text(String country_text) {

            TextView country = (TextView) itemView.findViewById(R.id.country_text);
            country.setText(" Country: "+country_text);
        }

        public void setVoteCountTotal_text(String voteCountTotal_text) {
            TextView countTotal_text = (TextView) itemView.findViewById(R.id.voteCountTotal_text);
            countTotal_text.setText("Total  count: "+voteCountTotal_text);
        }

        public void setVoteCountYes_text(String voteCountYes_text) {
            TextView countYes_text= (TextView) itemView.findViewById(R.id.voteCountYes_text);
            countYes_text.setText("Yes counts : "+voteCountYes_text);
        }

        public void setVoteCountNo_text(String voteCountNo_text) {
            TextView  countNo_text= (TextView) itemView.findViewById(R.id.voteCountNo_text);
            countNo_text.setText("No counts; " +voteCountNo_text);
        }
        public void setEuflagImageView(CircleImageView euflagImageView) {
            this.euflagImageView = euflagImageView;
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
       /* for (int i = 0; i < count; i++) {
            //entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mParties[i % mParties.length]));
            entries.add(new PieEntry((float) (Math.random() * mult) + mult / 5, mEuCountries[i % mEuCountries.length]));

        }

            for(VoteStats stats: euCountriesstats)
            {
                if(stats.getVoteCount()>0)
                {
                    int index = euCountries.indexOf(stats.getEucountry());
                    entries.add(new PieEntry((float) (stats.getVoteCount() * mult) + mult / 5, mEuCountries[index % mEuCountries.length]));
                    //writetoJsonDb(stats);
                }
            }*/

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


    }



    /*public static class PieChartItemData extends RecyclerView.ViewHolder {


        View mView;
        private Typeface mTf;
        private SpannableString mCenterText;

        protected static final int TYPE_BARCHART = 0;
        protected static final int TYPE_LINECHART = 1;
        protected static final int TYPE_PIECHART = 2;

       protected ChartData<?> mChartData;



        public PieChartItemData(View itemView) {
            super(itemView);
            mView = itemView;
            mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
            mCenterText = generateCenterText();
        }

        public int getItemType() {
            return TYPE_PIECHART;
        }

        public View getView(int position, View convertView, Context c) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();


                convertView = LayoutInflater.from(c).inflate(R.layout.activity_pie_polyline_chart_vote, null);
                holder.chart = (PieChart) convertView.findViewById(R.id.chart1);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            holder.chart.setDescription("");
            holder.chart.setHoleRadius(52f);
            holder.chart.setTransparentCircleRadius(57f);
            holder.chart.setCenterText(mCenterText);
            holder.chart.setCenterTextTypeface(mTf);
            holder.chart.setCenterTextSize(9f);
            holder.chart.setUsePercentValues(true);
            holder.chart.setExtraOffsets(5, 10, 50, 10);

            mChartData.setValueFormatter(new PercentFormatter());
            mChartData.setValueTypeface(mTf);
            mChartData.setValueTextSize(11f);
            mChartData.setValueTextColor(Color.WHITE);
            // set data
            holder.chart.setData((PieData) mChartData);

            Legend l = holder.chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateY(900);

            return convertView;
        }

        private SpannableString generateCenterText() {
            SpannableString s = new SpannableString("MPAndroidChart\ncreated by\nPhilipp Jahoda");
            s.setSpan(new RelativeSizeSpan(1.6f), 0, 14, 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0);
            s.setSpan(new RelativeSizeSpan(.9f), 14, 25, 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, 25, 0);
            s.setSpan(new RelativeSizeSpan(1.4f), 25, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 25, s.length(), 0);
            return s;
        }

        private static class ViewHolder {
        aaleksandra
            460PieChart chart;
        }
    }*/

    public class PieChartItem extends ChartItem {

        private Typeface mTf;
        private SpannableString mCenterText;

        public PieChartItem(ChartData<?> cd, Context c) {
            super(cd);

            mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
            mCenterText = generateCenterText();
        }

        @Override
        public int getItemType() {
            return TYPE_PIECHART;
        }

        @Override
        public View getView(int position, View convertView, Context c) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                // convertView = LayoutInflater.from(c).inflate(
                //      R.layout.list_item_piechart, null);
                // holder.chart = (PieChart) convertView.findViewById(R.id.chart);

                convertView = LayoutInflater.from(c).inflate(R.layout.activity_pie_polyline_chart_vote, null);
                holder.chart = (PieChart) convertView.findViewById(R.id.chart1);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            holder.chart.setDescription("");
            holder.chart.setHoleRadius(52f);
            holder.chart.setTransparentCircleRadius(57f);
            holder.chart.setCenterText(mCenterText);
            holder.chart.setCenterTextTypeface(mTf);
            holder.chart.setCenterTextSize(9f);
            holder.chart.setUsePercentValues(true);
            holder.chart.setExtraOffsets(5, 10, 50, 10);

            mChartData.setValueFormatter(new PercentFormatter());
            mChartData.setValueTypeface(mTf);
            mChartData.setValueTextSize(11f);
            mChartData.setValueTextColor(Color.WHITE);
            // set data
            holder.chart.setData((PieData) mChartData);

            Legend l = holder.chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateY(900);

            return convertView;
        }

        private SpannableString generateCenterText() {
            SpannableString s = new SpannableString("MPAndroidChart\ncreated by\nPhilipp Jahoda");
            s.setSpan(new RelativeSizeSpan(1.6f), 0, 14, 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0);
            s.setSpan(new RelativeSizeSpan(.9f), 14, 25, 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, 25, 0);
            s.setSpan(new RelativeSizeSpan(1.4f), 25, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 25, s.length(), 0);
            return s;
        }

        public class ViewHolder {
            PieChart chart;
        }
    }


}


