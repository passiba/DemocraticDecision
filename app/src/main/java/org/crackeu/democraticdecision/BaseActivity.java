package org.crackeu.democraticdecision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.crackeu.democraticdecision.auth.AnonymousAuthActivity;
import org.crackeu.democraticdecision.auth.CustomAuthActivity;
import org.crackeu.democraticdecision.auth.EmailPasswordActivity;
import org.crackeu.democraticdecision.auth.GoogleSignInActivity;
import org.crackeu.democraticdecision.chart.PiePolylineChartVoteActivity;
import org.crackeu.democraticdecision.vote.VoteActivity;
import org.crackeu.democraticdecision.vote.VoteSuggestionActivity;
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
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

}
