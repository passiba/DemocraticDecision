package org.crackeu.democraticdecision.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.auth.AnonymousAuthActivity;
import org.crackeu.democraticdecision.auth.CustomAuthActivity;
import org.crackeu.democraticdecision.auth.EmailPasswordActivity;
import org.crackeu.democraticdecision.auth.FacebookLoginActivity;
import org.crackeu.democraticdecision.auth.GoogleSignInActivity;
import org.crackeu.democraticdecision.vote.BaseVoteActivity;
import org.crackeu.democraticdecision.vote.VoteActivity;
import org.crackeu.democraticdecision.vote.VoteSuggestionActivity;

import java.util.ArrayList;

public class PiePolylineChartVoteActivity extends BaseVoteActivity implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private static final String TAG = " PiePolylineChartVoteActivity";

    private PieChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    private Typeface tf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_polyline_chart_vote);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarY.setProgress(10);

        mSeekBarX.setOnSeekBarChangeListener(this);
        mSeekBarY.setOnSeekBarChangeListener(this);

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

        setData(mEuCountries.length, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        tvX.setText("" + (mSeekBarX.getProgress()));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress(), mSeekBarY.getProgress());

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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


            case R.id.sign_out_menu:

                startActivity(new Intent(this, VoteActivity.class));
                return true;

            case R.id.sign_in_goolge_credientials_menu:
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
                return true;


            default:
                return super.onOptionsItemSelected(item);
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


