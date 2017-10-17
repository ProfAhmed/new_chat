package com.pro.ahmed.navigationdrawertest.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pro.ahmed.navigationdrawertest.MainActivity;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.model.ChatUser;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;
import com.pro.ahmed.navigationdrawertest.otherfragments.ChatFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hp on 25/09/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List<UserDetails> users;
    Context context;

    public UsersAdapter(Context context, List<UserDetails> users) {
        this.users = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_users_list, parent, false);
        return new ViewHolder(v);
    }

    private Context getContext() {
        return context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDetails userDetails = users.get(position);
        holder.usersUserName.setText(userDetails.getUserName());
        holder.usersDistance.setText(String.valueOf(userDetails.getDistance()) + "km");
        holder.usersState.setText(userDetails.getUserState());
        holder.usersStatu.setText(userDetails.getStatus());
        Glide.with(context.getApplicationContext())
                .load(userDetails.getUserPhoto())
                .into(holder.usersPhoto);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.usersPhoto)
        ImageView usersPhoto;
        @BindView(R.id.usersUserName)
        TextView usersUserName;
        @BindView(R.id.usersDistance)
        TextView usersDistance;
        @BindView(R.id.usersState)
        TextView usersState;
        @BindView(R.id.usersStatu)
        TextView usersStatu;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            UserDetails userDetails = users.get(getAdapterPosition());
            ChatUser.chatWith = userDetails.getUserId();
            ChatFragment chatFragment = ChatFragment.newInstance();
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_right, R.animator.in_from_right, R.animator.out_to_left);
            transaction.replace(R.id.content_main, chatFragment).addToBackStack(null);
            transaction.commit();
        }
    }
}
