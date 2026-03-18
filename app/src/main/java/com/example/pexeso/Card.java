package com.example.pexeso;

/**
 * Model class representing a single memory card in the Pexeso game.
 */
public class Card {

    private final int id;
    private final int pairId;
    private boolean isFaceUp;
    private boolean isMatched;

    public Card(int id, int pairId) {
        this.id = id;
        this.pairId = pairId;
        this.isFaceUp = false;
        this.isMatched = false;
    }

    public int getId() {
        return id;
    }

    public int getPairId() {
        return pairId;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
        if (matched) {
            isFaceUp = true;
        }
    }
}
