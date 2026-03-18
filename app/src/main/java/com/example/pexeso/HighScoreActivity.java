package com.example.pexeso;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Displays the all-time best score and provides an option to reset it.
 */
public class HighScoreActivity extends AppCompatActivity {

    private static final String TAG = "HighScoreActivity";

    private TextView tvHighScore;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.high_score_title);
        }

        tvHighScore = findViewById(R.id.tv_high_score);
        Button btnReset = findViewById(R.id.btn_reset_high_score);
        Button btnBack = findViewById(R.id.btn_back);

        scoreManager = new ScoreManager(this);

        refreshHighScoreDisplay();

        btnReset.setOnClickListener(v -> confirmReset());
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHighScoreDisplay();
    }

    private void refreshHighScoreDisplay() {
        int highScore = scoreManager.getHighScore();
        Log.d(TAG, "High score loaded: " + highScore);
        if (highScore == -1) {
            tvHighScore.setText(R.string.high_score_none);
        } else {
            tvHighScore.setText(getString(R.string.high_score_value, highScore));
        }
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_confirm_title)
                .setMessage(R.string.reset_confirm_message)
                .setPositiveButton(R.string.reset_confirm_yes, (dialog, which) -> {
                    scoreManager.resetHighScore();
                    Log.d(TAG, "High score reset.");
                    refreshHighScoreDisplay();
                })
                .setNegativeButton(R.string.reset_confirm_no, null)
                .show();
    }
}
