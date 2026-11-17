package com.zone.messanger.text.PANDA_text.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.databinding.ItemEmojiBinding;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {

    public interface OnEmojiClickListener {
        void onEmojiClick(String emoji);
    }

    private String[] emojis = new String[0];
    private final OnEmojiClickListener listener;

    public EmojiAdapter(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    public void submitList(String[] newEmojis) {
        this.emojis = newEmojis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmojiBinding binding = ItemEmojiBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EmojiViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        String emoji = emojis[position];
        holder.binding.txtEmoji.setText(emoji);
        holder.binding.txtEmoji.setOnClickListener(v -> listener.onEmojiClick(emoji));
    }

    @Override
    public int getItemCount() {
        return emojis.length;
    }

    static class EmojiViewHolder extends RecyclerView.ViewHolder {
        final ItemEmojiBinding binding;

        EmojiViewHolder(ItemEmojiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
