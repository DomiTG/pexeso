package com.example.pexeso;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main game activity — displays the 4x8 Pexeso grid and manages all game logic.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int COLUMNS = 4;
    private static final int PAIRS = 16;
    private static final long FLIP_BACK_DELAY_MS = 800;

    // 16 distinct animal emojis used as card faces
    private static final String[] EMOJIS = {
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
            "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔"
    };

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private TextView tvMoves;

    private List<Card> cards;
    private ScoreManager scoreManager;

    private int firstFlippedIndex = -1;
    private int moves = 0;
    private int matchedPairs = 0;
    private boolean isChecking = false;  // blocks input while evaluating a pair

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvMoves = findViewById(R.id.tv_moves);
        recyclerView = findViewById(R.id.recycler_view);

        scoreManager = new ScoreManager(this);

        setupGame();
    }

    // -------------------------------------------------------------------------
    // Game setup
    // -------------------------------------------------------------------------

    private void setupGame() {
        moves = 0;
        matchedPairs = 0;
        firstFlippedIndex = -1;
        isChecking = false;
        updateMovesDisplay();

        cards = generateShuffledCards();

        adapter = new GameAdapter(cards, EMOJIS);
        adapter.setOnCardClickListener(this::onCardClicked);

        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "Game started — " + cards.size() + " cards generated.");
    }

    /**
     * Builds 16 pairs of cards, assigns sequential IDs, then shuffles them.
     */
    private List<Card> generateShuffledCards() {
        List<Card> deck = new ArrayList<>(PAIRS * 2);
        for (int pairId = 0; pairId < PAIRS; pairId++) {
            deck.add(new Card(pairId * 2, pairId));
            deck.add(new Card(pairId * 2 + 1, pairId));
        }
        Collections.shuffle(deck);
        return deck;
    }

    // -------------------------------------------------------------------------
    // Card click logic
    // -------------------------------------------------------------------------

    private void onCardClicked(int position) {
        if (isChecking) {
            Log.d(TAG, "Input blocked — waiting for flip-back animation.");
            return;
        }

        Card clicked = cards.get(position);

        if (clicked.isFaceUp() || clicked.isMatched()) {
            Log.d(TAG, "Card at " + position + " already face-up or matched — ignoring.");
            return;
        }

        // Flip the clicked card face up
        clicked.setFaceUp(true);
        adapter.notifyItemChanged(position);
        Log.d(TAG, "Card flipped at position " + position + ", pairId=" + clicked.getPairId());

        if (firstFlippedIndex == -1) {
            // First card of a pair — wait for the second
            firstFlippedIndex = position;
        } else {
            // Second card — evaluate the pair
            moves++;
            updateMovesDisplay();
            isChecking = true;

            int secondIndex = position;
            int firstIndex = firstFlippedIndex;
            firstFlippedIndex = -1;

            handler.postDelayed(() -> evaluatePair(firstIndex, secondIndex), FLIP_BACK_DELAY_MS);
        }
    }

    private void evaluatePair(int firstIndex, int secondIndex) {
        Card first = cards.get(firstIndex);
        Card second = cards.get(secondIndex);

        if (first.getPairId() == second.getPairId()) {
            // Match!
            first.setMatched(true);
            second.setMatched(true);
            matchedPairs++;
            adapter.notifyItemChanged(firstIndex);
            adapter.notifyItemChanged(secondIndex);
            Log.d(TAG, "Match found! pairId=" + first.getPairId() + ". Matched pairs: " + matchedPairs);

            if (matchedPairs == PAIRS) {
                onGameComplete();
            }
        } else {
            // No match — flip both back
            first.setFaceUp(false);
            second.setFaceUp(false);
            adapter.notifyItemChanged(firstIndex);
            adapter.notifyItemChanged(secondIndex);
            Log.d(TAG, "No match. Flipping back positions " + firstIndex + " and " + secondIndex);
        }

        isChecking = false;
    }

    // -------------------------------------------------------------------------
    // Game over
    // -------------------------------------------------------------------------

    private void onGameComplete() {
        Log.d(TAG, "Game complete in " + moves + " moves.");
        boolean newHighScore = scoreManager.updateHighScore(moves);

        String message = getString(R.string.dialog_game_over_message, moves);
        if (newHighScore) {
            message += "\n" + getString(R.string.dialog_new_high_score);
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_game_over_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_play_again, (dialog, which) -> setupGame())
                .setNegativeButton(R.string.dialog_view_high_score, (dialog, which) -> openHighScoreActivity())
                .show();
    }

    // -------------------------------------------------------------------------
    // UI helpers
    // -------------------------------------------------------------------------

    private void updateMovesDisplay() {
        tvMoves.setText(getString(R.string.moves_label, moves));
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_high_score) {
            openHighScoreActivity();
            return true;
        }
        if (item.getItemId() == R.id.action_new_game) {
            setupGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openHighScoreActivity() {
        startActivity(new Intent(this, HighScoreActivity.class));
    }
}
