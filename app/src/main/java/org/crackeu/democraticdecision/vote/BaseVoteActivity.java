package org.crackeu.democraticdecision.vote;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.crackeu.democraticdecision.R;

import java.util.ArrayList;

/**
 * Created on 11.7.2016.
 */
public class BaseVoteActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;


    private static final String TAG = "BaseVoteActivity";

    protected static ArrayList<String> euCountries = new ArrayList<>();


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }


}
