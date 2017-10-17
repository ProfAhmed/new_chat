package com.pro.ahmed.navigationdrawertest.otherfragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.CommentsAdapter;
import com.pro.ahmed.navigationdrawertest.dialogs.CustomDialogContextMenuLikesNames;
import com.pro.ahmed.navigationdrawertest.emoji.Emojicon;
import com.pro.ahmed.navigationdrawertest.emoji.EmojiconEditText;
import com.pro.ahmed.navigationdrawertest.emoji.EmojiconGridView;
import com.pro.ahmed.navigationdrawertest.emoji.EmojiconsPopup;
import com.pro.ahmed.navigationdrawertest.model.Comments;
import com.pro.ahmed.navigationdrawertest.model.Like;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayVideoFragment extends Fragment {

    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    List<Comments> comments;
    List<Like> likes;
    boolean likeStat = true;

    public PlayVideoFragment() {
        // Required empty public constructor
    }

    public static PlayVideoFragment newInstance(String videoPath, String userId, String videoId) {
        PlayVideoFragment fragment = new PlayVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoPath", videoPath);
        bundle.putString("userId", userId);
        bundle.putString("videoId", videoId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("comments");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("likes");
        firebaseAuth = FirebaseAuth.getInstance();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean((getArguments().getString("videoId")), likeStat);
        editor.commit();
        final String videoPath = getArguments().getString("videoPath");
        final String userId = getArguments().getString("userId");
        View view = inflater.inflate(R.layout.fragment_play_video, container, false);
        final ImageView imLike = (ImageView) view.findViewById(R.id.imLikePlayVideoFrag);
        final VideoView videoView = (VideoView) view.findViewById(R.id.vvPlayVideoFrag);
        final CircleImageView cim = (CircleImageView) view.findViewById(R.id.cimProfilePlayVideoFrag);
        final TextView textView = (TextView) view.findViewById(R.id.tvUserNamePlayVideoFrag);
        final EmojiconEditText emojiconEditText = (EmojiconEditText) view.findViewById(R.id.emojicon_edit_text);
        final View rootView = view.findViewById(R.id.root_view2);
        final ImageView emojiButton = (ImageView) view.findViewById(R.id.emoji_btn);
        final ImageView submitButton = (ImageView) view.findViewById(R.id.submit_btn);
        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvCommentsVideo);
        final TextView tvLikes = (TextView) view.findViewById(R.id.tvLikes);
        comments = new ArrayList<>();
        likeStat = sharedPreferences.getBoolean(getArguments().getString("videoId"), true);
        Toast.makeText(getActivity(), "Likestat: " + String.valueOf(likeStat), Toast.LENGTH_SHORT).show();
        likes = new ArrayList<>();
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                imLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String likeId = databaseReference.push().getKey();

                        if (imLike.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp).getConstantState()) {
                            imLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                            Like like = new Like(likeId, firebaseAuth.getCurrentUser().getUid(), getArguments().getString("videoId"));
                            databaseReference3.child(likeId).setValue(like);
                            Toast.makeText(getActivity(), "AAdd" + String.valueOf(likeStat), Toast.LENGTH_SHORT).show();
                        } else if (imLike.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_black).getConstantState()) {
                            for (DataSnapshot likesSnapshot : dataSnapshot.getChildren()) {
                                Like like = likesSnapshot.getValue(Like.class);
                                if (like.getUserId().equals(firebaseAuth.getCurrentUser().getUid()) &&
                                        like.getVideoId().equals(getArguments().getString("videoId"))) {
                                    databaseReference3.child(like.getLikeId()).removeValue();
                                    likeStat = true;
                                    editor.putBoolean((getArguments().getString("videoId")), likeStat);
                                    editor.commit();
                                    break;
                                }
                            }
                            Toast.makeText(getActivity(), "Delete" + String.valueOf(likeStat), Toast.LENGTH_SHORT).show();
                            imLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.clear();
                for (DataSnapshot likesSnapshot : dataSnapshot.getChildren()) {
                    Like like = likesSnapshot.getValue(Like.class);
                    Activity activity = getActivity();
                    if (like.getVideoId().equals(getArguments().getString("videoId"))) {
                        likes.add(like);
                        if (like.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                            if (isAdded() && activity != null) {
                                imLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                            }
                        }
                    }
                }

                tvLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomDialogContextMenuLikesNames dialog = new CustomDialogContextMenuLikesNames(getContext(), likes);
                        dialog.show();
                    }
                });
                if (likes.size() != 0) {
                    tvLikes.setText(String.valueOf(likes.size()));
                } else {
                    tvLikes.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot commentsnSnapshot : dataSnapshot.getChildren()) {
                    Comments comment = commentsnSnapshot.getValue(Comments.class);
                    if (comment.getVideoId().equals(getArguments().getString("videoId"))) {
                        comments.add(comment);
                    }
                }
                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rv.setHasFixedSize(true);
                rv.setLayoutManager(llm);
                CommentsAdapter adapter = new CommentsAdapter(getContext(), comments);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.child(userId).getValue(UserDetails.class);
                try {
                    Glide.with(getActivity().getApplicationContext())
                            .load(userDetails.getUserPhoto())
                            .into(cim);
                } catch (Exception e) {
                }
                try {
                    textView.setText(userDetails.getUserName());
                    DisplayMetrics metrics = new DisplayMetrics();

                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                    params.width = metrics.widthPixels;
                    params.height = metrics.heightPixels;
                    params.leftMargin = 0;
                    videoView.setLayoutParams(params);

                    MediaController vidControl = new MediaController(getActivity());
                    videoView.setVideoURI(Uri.parse(videoPath));
                    videoView.setMediaController(vidControl);
                    videoView.start();


                    // Emojicons Code
                    // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
                    final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());

                    //Will automatically set size according to the soft keyboard size
                    popup.setSizeForSoftKeyboard();

                    //If the emoji popup is dismissed, change emojiButton to smiley icon
                    popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

                        @Override
                        public void onDismiss() {
                            changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
                        }
                    });

                    //If the text keyboard closes, also dismiss the emoji popup
                    popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

                        @Override
                        public void onKeyboardOpen(int keyBoardHeight) {

                        }

                        @Override
                        public void onKeyboardClose() {
                            if (popup.isShowing())
                                popup.dismiss();
                        }
                    });

                    //On emoji clicked, add it to edittext
                    popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

                        @Override
                        public void onEmojiconClicked(Emojicon emojicon) {
                            if (emojiconEditText == null || emojicon == null) {
                                return;
                            }

                            int start = emojiconEditText.getSelectionStart();
                            int end = emojiconEditText.getSelectionEnd();
                            if (start < 0) {
                                emojiconEditText.append(emojicon.getEmoji());
                            } else {
                                emojiconEditText.getText().replace(Math.min(start, end),
                                        Math.max(start, end), emojicon.getEmoji(), 0,
                                        emojicon.getEmoji().length());
                            }
                        }
                    });

                    //On backspace clicked, emulate the KEYCODE_DEL key event
                    popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

                        @Override
                        public void onEmojiconBackspaceClicked(View v) {
                            KeyEvent event = new KeyEvent(
                                    0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                            emojiconEditText.dispatchKeyEvent(event);
                        }
                    });

                    // To toggle between text keyboard and emoji keyboard keyboard(Popup)
                    emojiButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            //If popup is not showing => emoji keyboard is not visible, we need to show it
                            if (!popup.isShowing()) {

                                //If keyboard is visible, simply show the emoji popup
                                if (popup.isKeyBoardOpen()) {
                                    popup.showAtBottom();
                                    changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                                }

                                //else, open the text keyboard first and immediately after that show the emoji popup
                                else {
                                    emojiconEditText.setFocusableInTouchMode(true);
                                    emojiconEditText.requestFocus();
                                    popup.showAtBottomPending();
                                    final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                                    changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                                }
                            }

                            //If popup is showing, simply dismiss it to show the undelying text keyboard
                            else {
                                popup.dismiss();
                            }
                        }
                    });

                    //On submit, add the edittext text to listview and clear the edittext
                    submitButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String commentString = emojiconEditText.getText().toString();
                            String commentId = databaseReference.push().getKey();
                            String userId2 = firebaseAuth.getCurrentUser().getUid();
                            String videoId = getArguments().getString("videoId");
                            Comments comment = new Comments(commentId, commentString, userId2, videoId);
                            databaseReference2.child(commentId).setValue(comment);
                            emojiconEditText.getText().clear();
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
}