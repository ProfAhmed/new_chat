package com.pro.ahmed.navigationdrawertest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.model.Comments;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 10/10/2017.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comments> comments;
    Context context;
    DatabaseReference databaseReference;
    private String userUid;

    public CommentsAdapter(Context context, List<Comments> comments) {
        this.context = context;
        this.comments = comments;
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_comments_list, parent, false);
        return new CommentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, final int position) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Comments comment = comments.get(position);
                userUid = comment.getUserId();
                UserDetails userDetails = dataSnapshot.child(userUid).getValue(UserDetails.class);

                holder.tvComment.setText(comment.getCommentString());
                Glide.with(context.getApplicationContext()).
                        load(userDetails.getUserPhoto())
                        .into(holder.cimComment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cimComment)
        CircleImageView cimComment;
        @BindView(R.id.tvComment)
        TextView tvComment;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
