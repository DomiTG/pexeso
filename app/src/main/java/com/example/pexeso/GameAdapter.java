package com.example.pexeso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for the 4x8 Pexeso card grid.
 * Renders each card as either its back face ("?") or its front face (emoji).
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final List<Card> cards;
    private final String[] emojis;
    private OnCardClickListener listener;

    public GameAdapter(List<Card> cards, String[] emojis) {
        this.cards = cards;
        this.emojis = emojis;
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.bind(card, emojis[card.getPairId()]);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView tvFront;
        private final TextView tvBack;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvFront = itemView.findViewById(R.id.tv_card_front);
            tvBack = itemView.findViewById(R.id.tv_card_back);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onCardClick(pos);
                    }
                }
            });
        }

        void bind(Card card, String emoji) {
            if (card.isMatched()) {
                // Matched: show front with green background, disable click feedback
                tvFront.setText(emoji);
                tvFront.setVisibility(View.VISIBLE);
                tvBack.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.card_matched));
            } else if (card.isFaceUp()) {
                // Face up but not yet matched
                tvFront.setText(emoji);
                tvFront.setVisibility(View.VISIBLE);
                tvBack.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.card_front));
            } else {
                // Face down
                tvFront.setVisibility(View.GONE);
                tvBack.setVisibility(View.VISIBLE);
                cardView.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.card_back));
            }
        }
    }
}
