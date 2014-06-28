package com.antonfagerberg.greedy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity {
	private int totalScore, rounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        if (savedInstanceState == null) {
	        // Get score and rounds from parent activity.
	        totalScore = getIntent().getIntExtra(PlayActivity.TOTAL_SCORE, -1);
	        rounds = getIntent().getIntExtra(PlayActivity.ROUNDS, -1);
        } else {
        	// Get score and rounds from saved instance.
        	totalScore = savedInstanceState.getInt(PlayActivity.TOTAL_SCORE);
        	rounds = savedInstanceState.getInt(PlayActivity.ROUNDS);
        }

        ((TextView) findViewById(R.id.result)).setText("Total score: " + totalScore + ", in " + rounds + " rounds.");
    }

    /** Return to parent activity.
     *
     * @param view Go back button.
     */
    public void goBack(View view) {
        finish();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	savedInstanceState.putInt(PlayActivity.TOTAL_SCORE, totalScore);
    	savedInstanceState.putInt(PlayActivity.ROUNDS, rounds);
    }
}
