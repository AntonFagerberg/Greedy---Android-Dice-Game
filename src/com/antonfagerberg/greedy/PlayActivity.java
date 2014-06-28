package com.antonfagerberg.greedy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;


public class PlayActivity extends Activity {
    public final static String
        ROUNDS = "com.antonfagerberg.greedy.rounds",
        SCORE = "com.antonfagerberg.greedy.score",
		VALUES = "com.antonfagerberg.greedy.values",
        TOTAL_SCORE = "com.antonfagerberg.greedy.total_score",
        TOGGLED_DICE = "com.antonfagerberg.greedy.toggled_dice",
        BUTTONS_DICE_ENABLED = "com.antonfagerberg.greedy.buttons_dice_enabled",
        BUTTON_THROW = "com.antonfagerberg.greedy.button_throw",
        BUTTON_SCORE = "com.antonfagerberg.greedy.button_score",
        BUTTON_SAVE = "com.antonfagerberg.greedy.button_save",
        FIRST_THROW_IN_ROUND = "com.antonfagerberg.greedy.first_trow_in_run";

    private static int
	    MAX_SCORE = 10000,
	    FIRST_ROUND_LIMIT = 300,
	    DICE_COUNT = 6;
    
    private Button[] diceButtons = new Button[PlayActivity.DICE_COUNT];
    private int[] values = new int[PlayActivity.DICE_COUNT];
    private boolean[] toggledDiceButtons = new boolean[PlayActivity.DICE_COUNT];
    private boolean firstThrowInRound = true;
    private Random random = new Random();
    private Button 
    	buttonSave, 
    	buttonThrow, 
    	buttonScore;
    private int 
    	rounds, 
    	score, 
    	totalScore;
    private TextView 
    	textScore, 
    	textTotalScore;
    private int[] whiteImage = {
        R.drawable.white1,
        R.drawable.white2,
        R.drawable.white3,
        R.drawable.white4,
        R.drawable.white5,
        R.drawable.white6
    };
    private int[] redImage = {
        R.drawable.red1,
        R.drawable.red2,
        R.drawable.red3,
        R.drawable.red4,
        R.drawable.red5,
        R.drawable.red6
    };
    private int[] greyImage = {
        R.drawable.grey1,
        R.drawable.grey2,
        R.drawable.grey3,
        R.drawable.grey4,
        R.drawable.grey5,
        R.drawable.grey6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        diceButtons[0] = (Button) findViewById(R.id.one);
        diceButtons[1] = (Button) findViewById(R.id.two);
        diceButtons[2] = (Button) findViewById(R.id.three);
        diceButtons[3] = (Button) findViewById(R.id.four);
        diceButtons[4] = (Button) findViewById(R.id.five);
        diceButtons[5] = (Button) findViewById(R.id.six);
        
        // Change default initialization from 0  to avoid index out of bounds since 0 is not a valid score. 
        for (int i = 0; i != values.length; i++) {
        	values[i] = i + 1;
        }

        buttonSave = (Button) findViewById(R.id.btn_save);
        buttonThrow = (Button) findViewById(R.id.btn_throw);
        buttonScore = (Button) findViewById(R.id.btn_score);

        textScore = (TextView) findViewById(R.id.txt_score);
        textTotalScore = (TextView) findViewById(R.id.txt_total_score);

        if (savedInstanceState == null) {
        	// Setup a new game.
        	setupGame();
        } else {
        	// Load settings after rotation.
        	toggledDiceButtons = savedInstanceState.getBooleanArray(PlayActivity.TOGGLED_DICE);
        	values = savedInstanceState.getIntArray(PlayActivity.VALUES);
        	
        	boolean[] enabledDiceButtons = savedInstanceState.getBooleanArray(PlayActivity.BUTTONS_DICE_ENABLED);
        	for (int i = 0; i != enabledDiceButtons.length; i++) {
        		if (enabledDiceButtons[i]) {
        			diceButtons[i].setBackgroundResource(toggledDiceButtons[i] ? redImage[values[i] - 1] : whiteImage[values[i] - 1]);
        		} else {
        			diceButtons[i].setEnabled(false);
        			diceButtons[i].setBackgroundResource(greyImage[values[i] - 1]);
        		}
        	}

        	rounds = savedInstanceState.getInt(PlayActivity.ROUNDS);
        	score = savedInstanceState.getInt(PlayActivity.SCORE);
        	totalScore = savedInstanceState.getInt(PlayActivity.TOTAL_SCORE);
        	firstThrowInRound = savedInstanceState.getBoolean(PlayActivity.FIRST_THROW_IN_ROUND);
        	
        	updateScoreText(score, totalScore);
        	
        	buttonSave.setEnabled(savedInstanceState.getBoolean(PlayActivity.BUTTON_SAVE));
        	buttonThrow.setEnabled(savedInstanceState.getBoolean(PlayActivity.BUTTON_THROW));
        	buttonScore.setEnabled(savedInstanceState.getBoolean(PlayActivity.BUTTON_SCORE));
        }
    }
    
    private void updateScoreText(int score, int totalScore) {
    	textScore.setText(getText(R.string.score) + " " + score);
        textTotalScore.setText(getText(R.string.total_score) + " " + totalScore);
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	boolean[] enabledDiceButton = new boolean[diceButtons.length];
    	
    	for (int i = 0; i != diceButtons.length; i++) {
    		enabledDiceButton[i] = diceButtons[i].isEnabled();
    	}
    	
    	savedInstanceState.putBooleanArray(PlayActivity.BUTTONS_DICE_ENABLED, enabledDiceButton);
    	savedInstanceState.putIntArray(PlayActivity.VALUES, values);
    	savedInstanceState.putBooleanArray(PlayActivity.TOGGLED_DICE, toggledDiceButtons);
    	savedInstanceState.putInt(PlayActivity.ROUNDS, rounds);
    	savedInstanceState.putInt(PlayActivity.SCORE, score);
    	savedInstanceState.putInt(PlayActivity.TOTAL_SCORE, totalScore);
    	
    	savedInstanceState.putBoolean(PlayActivity.BUTTON_SAVE, buttonSave.isEnabled());
    	savedInstanceState.putBoolean(PlayActivity.BUTTON_THROW, buttonThrow.isEnabled());
    	savedInstanceState.putBoolean(PlayActivity.BUTTON_SCORE, buttonScore.isEnabled());
    	savedInstanceState.putBoolean(PlayActivity.FIRST_THROW_IN_ROUND, firstThrowInRound);
    }

    /** Set start conditions for a new game.
     *
     */
    private void setupGame() {
        score = totalScore = rounds = 0;

        buttonSave.setEnabled(false);
        buttonThrow.setEnabled(true);
        buttonScore.setEnabled(false);

        // Force a new throw.
        for (int i = 0; i != diceButtons.length; i++) {
            toggledDiceButtons[i] = false;
            diceButtons[i].setEnabled(false);
            diceButtons[i].setBackgroundResource(greyImage[i]);
        }

        textScore.setText(getText(R.string.score) + " 0");
        textTotalScore.setText(getText(R.string.total_score) + " 0");
    }

    /** Make a new throw of the active dice.
     *
     * @param view Throw button.
     */
    public void throwButtonClick(View view) {
        // Enable all dice buttons if all of them are disabled.
        if (!diceButtons[0].isEnabled() && !diceButtons[1].isEnabled() && !diceButtons[2].isEnabled() && !diceButtons[3].isEnabled() && !diceButtons[4].isEnabled() && !diceButtons[5].isEnabled()) {
            for (int i = 0; i != diceButtons.length; i++) {
                diceButtons[i].setEnabled(true);
            }
        }

        // Randomise all enabled dice.
        for (int i = 0; i < diceButtons.length; i++) {
            if (diceButtons[i].isEnabled()) {
                values[i] = random.nextInt(6) + 1;
                diceButtons[i].setBackgroundResource(whiteImage[values[i] - 1]);
                toggledDiceButtons[i] = false;
            }
        }

        buttonSave.setEnabled(false);
        buttonThrow.setEnabled(false);
        buttonScore.setEnabled(false);
    }

    /** End a round and calculate scores.
     *
     * @param invalidScore Should the score be ignored? (Was not valid).
     */
    private void endRound(boolean invalidScore) {
        if (invalidScore) {
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.not_enough_score), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 130);
            toast.show();

            // Force a new throw.
            for (int i = 0; i != diceButtons.length; i++) {
                diceButtons[i].setEnabled(false);
            }
        } else {
            totalScore += score;
        }

        score = 0;
        rounds++;

        updateScoreText(score, totalScore);

        buttonSave.setEnabled(false);
        buttonThrow.setEnabled(true);
        buttonScore.setEnabled(false);

        for (int i = 0; i != toggledDiceButtons.length; i++) {
            toggledDiceButtons[i] = false;
            diceButtons[i].setBackgroundResource(greyImage[values[i] - 1]);
        }

        if (totalScore >= PlayActivity.MAX_SCORE) {
            Intent intent = new Intent(PlayActivity.this, ScoreActivity.class);
            intent.putExtra(PlayActivity.TOTAL_SCORE, totalScore);
            intent.putExtra(PlayActivity.ROUNDS, rounds);
            setupGame();
            startActivity(intent);
        }
    }

    /** Save the score for the round.
     *
     * @param saveButton Save button.
     */
    public void saveButtonClick(View saveButton) {
        // Reset and force a new throw.
        for (int i = 0; i != diceButtons.length; i++) {
            diceButtons[i].setBackgroundResource(greyImage[values[i] - 1]);
            diceButtons[i].setEnabled(false);
            toggledDiceButtons[i] = false;
        }

        saveButton.setEnabled(false);
        firstThrowInRound = true;
        endRound(false);
    }

    /** Calculate score of selected dice.
     *
     * @param view Score button.
     */
    public void scoreButtonClicked(View view) {
        if (firstThrowInRound) {
            firstThrowInRound = false;
            score = 0;
        }

        int startScore = score;

        int[] sortedValues = Arrays.copyOf(values, values.length);
        Arrays.sort(sortedValues);

        // Check for "straight".
        boolean straight = true;
        for (int i = 0; i < diceButtons.length; i++) {
            if (!diceButtons[i].isEnabled() || !toggledDiceButtons[i] || sortedValues[i] != i + 1) {
                straight = false;
                i = diceButtons.length;
            }
        }

        if (straight) {
            score += 1000;
        } else {
            // Check for "three of a kind".
            for (int x = 0; x != diceButtons.length - 2; x++) {
                for (int y = x + 1; y != diceButtons.length - 1; y++) {
                    for (int z = y + 1; z != diceButtons.length; z++) {
                        if (toggledDiceButtons[x] &&  toggledDiceButtons[y]  && toggledDiceButtons[z] && diceButtons[x].isEnabled() && diceButtons[y].isEnabled() && diceButtons[z].isEnabled() && values[x] == values[y] && values[y] == values[z]) {
                            score += values[x] == 1 ? 1000 : values[x] * 100;
                            diceButtons[x].setEnabled(false);
                            diceButtons[y].setEnabled(false);
                            diceButtons[z].setEnabled(false);
                            diceButtons[x].setBackgroundResource(greyImage[values[x] - 1]);
                            diceButtons[y].setBackgroundResource(greyImage[values[y] - 1]);
                            diceButtons[z].setBackgroundResource(greyImage[values[z] - 1]);
                        }
                    }
                }
            }

            // Check for fives or ones.
            for (int i = 0; i != diceButtons.length; i++) {
                if (diceButtons[i].isEnabled() && toggledDiceButtons[i] && (values[i] == 1 || values[i] == 5)) {
                    score += values[i] == 1 ? 100 :  50;
                    diceButtons[i].setEnabled(false);
                    diceButtons[i].setBackgroundResource(greyImage[values[i] - 1]);
                }
            }
        }

        // Score to low to continue.
        if ((startScore == 0 && score < FIRST_ROUND_LIMIT) || score == startScore) {
            endRound(true);
        } else {
            buttonSave.setEnabled(true);
            buttonThrow.setEnabled(true);
            buttonScore.setEnabled(false);

            updateScoreText(score, totalScore);

            boolean allButtonsDisabled = true;

            for (int i = 0; i != diceButtons.length; i++) {
                if (diceButtons[i].isEnabled()) {
                    allButtonsDisabled = false;
                }
            }

            // Re-enable all buttons if they are all disabled.
            if (allButtonsDisabled) {
                for (int i = 0; i != diceButtons.length; i++) {
                    diceButtons[i].setEnabled(false);
                    diceButtons[i].setBackgroundResource(greyImage[values[i] - 1]);
                }
            }
        }
    }

    /** Click a die to enable it for score calculation.
     *
     * @param view Die button.
     */
    public void toggleDiceButton(View view) {
        int id = -1;
        switch (view.getId()) {
            case R.id.one:
                id = 0;
                break;
            case R.id.two:
                id = 1;
                break;
            case R.id.three:
                id = 2;
                break;
            case R.id.four:
                id = 3;
                break;
            case R.id.five:
                id = 4;
                break;
            case R.id.six:
                id = 5;
                break;
        }

        // Color die button red or white depending on if it's toggled or note.
        view.setBackgroundResource(toggledDiceButtons[id] ? whiteImage[values[id] - 1] : redImage[values[id] - 1]);
        toggledDiceButtons[id] = !toggledDiceButtons[id];

        boolean toggle = false;

        // Enable score button if one die is toggled.
        for (int i = 0; i != toggledDiceButtons.length; i++) {
            if (toggledDiceButtons[i] && diceButtons[i].isEnabled()) {
                toggle = true;
            }
        }

        buttonScore.setEnabled(toggle);
    }
}