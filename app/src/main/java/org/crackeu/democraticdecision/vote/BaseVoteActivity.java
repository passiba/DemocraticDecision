package org.crackeu.democraticdecision.vote;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.crackeu.democraticdecision.R;
import org.crackeu.democraticdecision.auth.FacebookLoginActivity;
import org.crackeu.democraticdecision.chart.PiePolylineChartVoteActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11.7.2016.
 */
public class BaseVoteActivity extends AppCompatActivity {


    protected static final String INDEX_COUNTRY = "eucountry", INDEX_LEAVE = "isLeave";
    private static final String TAG = "BaseVoteActivity";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected static ArrayList<String> euCountries = new ArrayList<>();
    //protected static ArrayList<VoteStats> euCountriesstats = new ArrayList();
    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    protected String[] mParties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };
    protected String[] mEuCountries = new String[]{
            "Austria", "Belgium", "Bulgaria", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Estonia",
            "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy", "Latvia",
            "Lithuania", "Luxembourg", "Malta", "Neatherlands", "Poland", "Portugal", "Romania", "Slovakia",
            "Slovenia", "Spain", "Sweden"
    };
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    protected static ArrayList<Stats> euCountrieStat = new ArrayList<>();
    public Map<String, Stats> euCountryKeys = new HashMap<>();

    protected static void initializeEUCountries() {

        BaseVoteActivity.euCountries.add("Austria");
        BaseVoteActivity.euCountries.add("Belgium");
        BaseVoteActivity.euCountries.add("Bulgaria");
        BaseVoteActivity.euCountries.add("Croatia");
        BaseVoteActivity.euCountries.add("Cyprus");
        BaseVoteActivity.euCountries.add("Czech Republic");
        BaseVoteActivity.euCountries.add("Denmark");
        BaseVoteActivity.euCountries.add("Estonia");
        BaseVoteActivity.euCountries.add("Finland");
        BaseVoteActivity.euCountries.add("France");
        BaseVoteActivity.euCountries.add("Germany");
        BaseVoteActivity.euCountries.add("Greece");
        BaseVoteActivity.euCountries.add("Hungary");
        BaseVoteActivity.euCountries.add("Ireland");
        BaseVoteActivity.euCountries.add("Italy");
        BaseVoteActivity.euCountries.add("Latvia");
        BaseVoteActivity.euCountries.add("Lithuania");
        ix
        BaseVoteActivity.euCountries.add("Luxembourg");
        BaseVoteActivity.euCountries.add("Malta");
        BaseVoteActivity.euCountries.add("Neatherlands");
        BaseVoteActivity.euCountries.add("Poland");
        BaseVoteActivity.euCountries.add("Portugal");
        BaseVoteActivity.euCountries.add("Romania");
        BaseVoteActivity.euCountries.add("Slovakia");
        BaseVoteActivity.euCountries.add("Slovenia");
        BaseVoteActivity.euCountries.add("Spain");
        BaseVoteActivity.euCountries.add("Sweden");
        for (String eucounrty : euCountries) {
            Stats votestats = new Stats(eucounrty);
            euCountrieStat.add(votestats);
        }


    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


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

    public static class Stats {
        /* public Long voteCount = new Long( 0);
         public Long yesVoteCount=new Long(0),noVoteCount=new Long(0);*/
        public long voteCount = 0, yesVoteCount = 0, noVoteCount = 0;
        public Map<String, String> votes = new HashMap<>();
        String uid = "statistics";
        String eucontryKey;
        String eucountry;
        String countryflagPhotUrl;


        public Stats() {
        }

        public Stats(String country) {
            super();
            this.eucountry = country;


        }

        public synchronized void isLeavingEuCount(boolean leaveEu) {
            if (leaveEu) {
                this.yesVoteCount += 1;
            } else {
                this.noVoteCount += 1;
            }
            this.voteCount += 1;

        }

       /* public void setVoteCount(long voteCount) {
            this.voteCount = voteCount;
        }

        public void setYesVoteCount(long yesVoteCount) {
            this.yesVoteCount = yesVoteCount;
        }

        public void setNoVoteCount(long noVoteCount) {
            this.noVoteCount = noVoteCount;
        }*/

        public String getEucontryKey() {
            return eucontryKey;
        }

        public void setEucontryKey(String eucontryKey) {
            this.eucontryKey = eucontryKey;
        }

        public long getVoteCount() {
            return voteCount;
        }

        public long getYesVoteCount() {
            return yesVoteCount;
        }

        public long getNoVoteCount() {
            return noVoteCount;
        }

        public String getEucountry() {
            return eucountry;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCountryflagPhotUrl() {
            return countryflagPhotUrl;
        }

        public void setCountryflagPhotUrl(String countryflagPhotUrl) {
            this.countryflagPhotUrl = countryflagPhotUrl;
        }

        // [START post_to_map]@Exclude

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("eucountry", uid);
            result.put("eucountry", eucountry);
            result.put("eucontryKey", eucontryKey);
       /* result.put("voteCount", String.valueOf(voteCount));
         result.put("noVoteCount",String.valueOf(noVoteCount));
        result.put("yesVoteCount",String.valueOf(noVoteCount));*/
            result.put("voteCount", voteCount);
            result.put("noVoteCount", noVoteCount);
            result.put("yesVoteCount", noVoteCount);

            return result;
        }
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


            case R.id.sign_in_facebook_credientials_menu:
                startActivity(new Intent(this, FacebookLoginActivity.class));
                return true;

           /*


  case R.id.vote_explore_menu:

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



            case R.id.sign_in_goolge_credientials_menu:
                startActivity(new Intent(this, GoogleSignInActivity.class));
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




}
