package com.example.pexeso;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the high score (best/lowest move count) using SharedPreferences.
 */
public class ScoreManager {

    private static final String PREFS_NAME = "pexeso_prefs";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final int NO_HIGH_SCORE = -1;

    private final SharedPreferences prefs;

    public ScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the stored high score (lowest move count), or -1 if none recorded.
     */
    public int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, NO_HIGH_SCORE);
    }

    /**
     * Updates the high score if the given move count is lower than the current best.
     *
     * @param moves Number of moves in the completed game.
     * @return true if a new high score was set.
     */
    public boolean updateHighScore(int moves) {
        int current = getHighScore();
        if (current == NO_HIGH_SCORE || moves < current) {
            prefs.edit().putInt(KEY_HIGH_SCORE, moves).apply();
            return true;
        }
        return false;
    }

    /**
     * Removes the stored high score.
     */
    public void resetHighScore() {
        prefs.edit().remove(KEY_HIGH_SCORE).apply();
    }
}
