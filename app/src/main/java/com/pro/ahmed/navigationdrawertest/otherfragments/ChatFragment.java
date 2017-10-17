package com.pro.ahmed.navigationdrawertest.otherfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.model.ChatUser;
import com.pro.ahmed.navigationdrawertest.model.Message;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    DatabaseReference reference1, referenceChats;
    DatabaseReference reference2, referenceUsers;

    String curr_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        layout = (LinearLayout) view.findViewById(R.id.layout1);
        sendButton = (ImageView) view.findViewById(R.id.sendButton);
        messageArea = (EditText) view.findViewById(R.id.messageArea);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        reference1 = FirebaseDatabase.getInstance().getReference("messages/" + curr_Id + "_" + ChatUser.chatWith);
        reference2 = FirebaseDatabase.getInstance().getReference("messages/" + ChatUser.chatWith + "_" + curr_Id);
        referenceUsers = FirebaseDatabase.getInstance().getReference("users");
        referenceChats = FirebaseDatabase.getInstance().getReference("chats");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    //Map<String, String> map = new HashMap<String, String>();
                    //map.put("message", messageText);
                    // map.put("user", curr_Id);
                    Message message = new Message(curr_Id, messageText);
                    String messageKey1 = reference1.push().getKey();
                    String messageKey2 = reference2.push().getKey();
                    reference1.child(messageKey1).setValue(message);
                    reference2.child(messageKey2).setValue(message);
                    referenceUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserDetails userDetails = dataSnapshot.child(ChatUser.chatWith).getValue(UserDetails.class);
                            UserDetails userDetails1 = new UserDetails(userDetails.getUserName(),
                                    userDetails.getUserPhoto(), userDetails.getUserId(), curr_Id
                                    , userDetails.getUserState(), userDetails.getStatus());
                            referenceChats.child(curr_Id + "_" + ChatUser.chatWith).setValue(userDetails1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    messageArea.setText("");
                }
            }
        });


        reference1.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Message messageModel = dataSnapshot.getValue(Message.class);
                String message = messageModel.getMessage();
                String userName = messageModel.getUser();

                if (userName.equals(curr_Id)) {
                    addMessageBox(message, curr_Id);
                } else {
                    addMessageBox(message, userName);
                }
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void addMessageBox(final String message, final String userId) {
        referenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserDetails userDetails = dataSnapshot.child(userId).getValue(UserDetails.class);
                //referenceChats.child(ChatUser.chatWith).setValue(userDetails);

                try {
                    TextView textView = new TextView(getActivity());
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp2.weight = 1.0f;
                    textView.setLayoutParams(lp2);

                    if (userDetails.getUserId().equals(curr_Id)) {
                        textView.setText("you:-" + "\n" + message);
                        lp2.gravity = Gravity.LEFT;
                        textView.setBackgroundResource(R.drawable.bubble_in);
                    } else {
                        textView.setText(userDetails.getUserName() + "\n" + message);
                        lp2.gravity = Gravity.RIGHT;
                        textView.setBackgroundResource(R.drawable.bubble_out);
                    }
                    layout.addView(textView);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
