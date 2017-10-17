package com.pro.ahmed.navigationdrawertest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.model.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 17/10/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    List<Message> messages;
    Context context;
    String curr_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MessagesAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_chat, parent, false);
        return new MessagesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getUser().equals(curr_Id)) {
            holder.tvMessageChat.setText("You:-\n" + message.getMessage());
            holder.tvMessageChat.setBackground(context.getResources().getDrawable(R.drawable.bubble_in));
        } else {
            holder.tvMessageChat.setText(message.getUser() + ":-\n" + message.getMessage());
            holder.tvMessageChat.setBackground(context.getResources().getDrawable(R.drawable.bubble_out));

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cimMessagePhoto)
        CircleImageView cimMessagePhoto;
        @BindView(R.id.tvMessageChat)
        TextView tvMessageChat;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
