package com.example.gmail.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmail.databinding.EmailItemLayoutBinding;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.List;

public class MailListAdapter extends RecyclerView.Adapter<MailListAdapter.MessageViewHolder> {

    private List<Message> mails;

    public MailListAdapter(List<Message> mails) {
        this.mails = mails;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        EmailItemLayoutBinding binding = EmailItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        holder.binding.setDate(mails.get(holder.getAdapterPosition()).getInternalDate());
        for (MessagePartHeader header : mails.get(holder.getAdapterPosition()).getPayload().getHeaders()) {
            switch (header.getName()) {
                case "Subject":
                    holder.binding.setSubject(header.getValue());
                    break;
                case "From":
                    holder.binding.setSender(header.getValue());
                    break;
            }
        }

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mails.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        EmailItemLayoutBinding binding;

        MessageViewHolder(@NonNull EmailItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
