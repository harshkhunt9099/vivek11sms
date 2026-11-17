package com.zone.messanger.text.PANDA_text.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.databinding.ItemContactBinding;
import com.zone.messanger.text.PANDA_text.model.SmsContact;
import com.zone.messanger.text.PANDA_text.util.ContactUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(SmsContact contact);
    }

    private final List<SmsContact> items = new ArrayList<>();
    private final OnContactClickListener listener;

    public ContactAdapter(OnContactClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<SmsContact> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactBinding binding = ItemContactBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final ItemContactBinding binding;

        ContactViewHolder(ItemContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SmsContact contact, OnContactClickListener listener) {
            binding.txtContactName.setText(contact.getName());
            binding.txtContactNumber.setText(contact.getNumber());
            binding.txtAvatar.setText(ContactUtils.initialFor(contact.getName()));
            binding.txtAvatar.getBackground().mutate().setTint(
                    ContactUtils.avatarColorFor(binding.getRoot().getContext(), contact.getName()));
            binding.getRoot().setOnClickListener(v -> listener.onContactClick(contact));
        }
    }
}
