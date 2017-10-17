package com.pro.ahmed.navigationdrawertest.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.MainActivity;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.model.Like;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;
import com.pro.ahmed.navigationdrawertest.model.Video;
import com.pro.ahmed.navigationdrawertest.otherfragments.PlayVideoFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 06/10/2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    List<Video> videos;
    List<Like> likes;
    Context context;
    DatabaseReference databaseReference, databaseReference3;
    private String userUid;


    public VideosAdapter(Context context, List<Video> videos) {
        this.videos = videos;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("likes");
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_videos_list, parent, false);
        return new VideosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.ViewHolder holder, final int position) {

        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes = new ArrayList<>();
                Video video = videos.get(position);
                for (DataSnapshot likesSnapshot : dataSnapshot.getChildren()) {
                    Like like = likesSnapshot.getValue(Like.class);
                    if (like.getVideoId().equals(video.getId())) {
                        likes.add(like);
                    }
                }
                if (likes.size() != 0) {
                    holder.tvFavorite.setText(String.valueOf(likes.size()));
                } else {
                    holder.tvFavorite.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Video video = videos.get(position);
                userUid = video.getUserId();
                UserDetails userDetails = dataSnapshot.child(userUid).getValue(UserDetails.class);
                Glide.with(context.getApplicationContext()).
                        load(userDetails.getUserPhoto())
                        .into(holder.cimThumbnailProfile);
                if (!((Activity) context).isFinishing()) {
                    new Thumbnail(holder.imThumbnail, video.getVideoPath()).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ImThumbnail)
        ImageView imThumbnail;
        @BindView(R.id.TvFavorite)
        TextView tvFavorite;
        @BindView(R.id.CImThumbnailProfile)
        CircleImageView cimThumbnailProfile;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            imThumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Video video = videos.get(getAdapterPosition());
            PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(video.getVideoPath(), video.getUserId(), video.getId());
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_right, R.animator.in_from_right, R.animator.out_to_left);
            transaction.replace(R.id.content_main, playVideoFragment).addToBackStack(null);
            transaction.commit();
        }
    }


    public class Thumbnail extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;
        String url;


        public Thumbnail(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            //String videoPath = "http://techslides.com/demos/sample-videos/small.mp4";
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    // no headers included
                    mediaMetadataRetriever.setDataSource(url, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(url);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null)
                    mediaMetadataRetriever.release();
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public interface NotifyChange {

        void onItemClicked(int pos, List<Video> videos);
    }
}
