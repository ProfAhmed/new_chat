package com.pro.ahmed.navigationdrawertest.myVideosFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pro.ahmed.navigationdrawertest.MainActivity;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.MyVideosAdapter;
import com.pro.ahmed.navigationdrawertest.model.Video;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class MyVideosFragment extends Fragment {
    private Uri fileUri; // file url to store image/video
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    MainActivity mainActivity;
    FloatingActionButton fabUploadVideo;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";


    FirebaseStorage storage;
    //progress dialog
    private ProgressDialog progressDialog;
    StorageReference storageRef, imageRef;
    UploadTask uploadTask;
    Uri downloadUrl;
    private FirebaseAuth firebaseAuth;
    //firebase database ref;
    DatabaseReference databaseReference;

    public MyVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        //accessing the firebase storage
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();
        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Liked Videos");
        View view = inflater.inflate(R.layout.fragment_my_videos, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.myVideosTabsViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.myVideosTabs);
        MyVideosAdapter adapter = new MyVideosAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        mViewPager.setCurrentItem(0);
                        mainActivity.toolbar.setTitle("Liked Videos");
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        mainActivity.toolbar.setTitle("Unlocked Videos");
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        mainActivity.toolbar.setTitle("My Uploads");
                        break;

                    default:

                        mViewPager.setCurrentItem(tab.getPosition());
                        mainActivity.toolbar.setTitle("Liked Videos");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }
        fabUploadVideo = (FloatingActionButton) view.findViewById(R.id.fabUploadVideo);
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            getActivity().finish();
        }
        fabUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlayVideo().execute();

                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean isDeviceSupportCamera() {
        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }


    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                upload();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void upload() {
        progressDialog.setMessage("Uploading Please Wait...");
        progressDialog.show();
        Toast.makeText(mainActivity, "Iam here", Toast.LENGTH_SHORT).show();
        imageRef = storageRef.child(firebaseAuth.getCurrentUser().getUid() + ": " + firebaseAuth.getCurrentUser().getDisplayName()).child("videos/" + fileUri.getLastPathSegment());
        uploadTask = imageRef.putFile(fileUri);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(mainActivity, "Iam Progress", Toast.LENGTH_SHORT).show();

            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Error in uploading!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                downloadUrl = taskSnapshot.getDownloadUrl();
                String videoId = databaseReference.push().getKey();
                String uId = firebaseAuth.getCurrentUser().getUid();
                Video video = new Video(videoId, uId, downloadUrl.toString());
                databaseReference.child(videoId).setValue(video);
                Toast.makeText(getActivity(), "Success Uploading", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class PlayVideo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            recordVideo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
