package com.pro.ahmed.navigationdrawertest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pro.ahmed.navigationdrawertest.captcha.TextCaptcha;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.toolbarRegister)
    Toolbar toolbarRegister;
    @BindView(R.id.EtEmailRegistration)
    EditText EtEmailRegistration;
    @BindView(R.id.EtPasswordRegistration)
    EditText EtPasswordRegistration;
    @BindView(R.id.EtConfirmPasswordRegistration)
    EditText EtConfirmPasswordRegistration;
    @BindView(R.id.EtUsernameRegistration)
    EditText EtUsernameRegistration;
    @BindView(R.id.EtCaptchaAnswer)
    EditText ETCaptchaAnswer;
    @BindView(R.id.ImVCaptchaImage)
    ImageView ImVCaptchaImage;
    @BindView(R.id.ImageViewAvatar)
    CircleImageView ImageViewAvatar;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.TvBirthday)
    TextView TvBirthday;
    @BindView(R.id.ConstraintLayoutBirthday)
    ConstraintLayout ConstraintLayoutBirthday;
    @BindView(R.id.BTnRegister)
    Button BTnRegister;
    ////////
    TextCaptcha textCaptcha;
    Calendar myCalendar;
    UserDetails userDetails;
    ////////
    CropImage.ActivityResult result;
    private static String email, password, confirmPassword, userName, captchaAnswer, birthDay, sex;
    boolean invalidBirthday = false, invalidAvatar = false;
    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private static Uri selectedImage;
    private Uri mCropImageUri;
    Uri downloadUrl;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    //firebase database ref;
    DatabaseReference databaseReference;

    //progress dialog
    private ProgressDialog progressDialog;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        //accessing the firebase storage
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();
        progressDialog = new ProgressDialog(this);

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        setSupportActionBar(toolbarRegister);

        textCaptcha = new TextCaptcha(200, 150, 3, TextCaptcha.TextOptions.NUMBERS_ONLY);
        ImVCaptchaImage.setImageBitmap(textCaptcha.getImage());
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        ConstraintLayoutBirthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                invalidBirthday = true;
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        ImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
                invalidAvatar = true;
            }
        });
        buttonRegister();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        TvBirthday.setText(sdf.format(myCalendar.getTime()));
        birthDay = TvBirthday.getText().toString().trim();
    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageViewAvatar.setImageURI(result.getUri());
                selectedImage = result.getUri();
                SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = shre.edit();
                edit.putString("imagepath", selectedImage.toString());
                edit.commit();
                Toast.makeText(this, "nice pic", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_forward2:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
        }
        return true;
    }

    private void buttonRegister() {
        BTnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                email = EtEmailRegistration.getText().toString().trim();
                password = EtPasswordRegistration.getText().toString().trim();
                confirmPassword = EtConfirmPasswordRegistration.getText().toString().trim();
                userName = EtUsernameRegistration.getText().toString().trim();
                captchaAnswer = ETCaptchaAnswer.getText().toString().trim();

                if (email.length() == 0) {
                    EtEmailRegistration.setError("is Required");
                } else if (password.length() == 0) {
                    EtPasswordRegistration.setError("is Required");
                } else if (password.length() < 6) {
                    EtPasswordRegistration.setError("at least 6 character ");
                } else if (!(confirmPassword.equals(password))) {
                    EtConfirmPasswordRegistration.setError("Not Match");
                } else if (userName.length() == 0) {
                    EtUsernameRegistration.setError("is Required");
                } else if (captchaAnswer.length() == 0) {
                    ETCaptchaAnswer.setError("is Required");
                } else if (!textCaptcha.checkAnswer(captchaAnswer)) {
                    ETCaptchaAnswer.setError("Captcha is not match");
                    ETCaptchaAnswer.setText(null);
                    textCaptcha = new TextCaptcha(200, 150, 3, TextCaptcha.TextOptions.NUMBERS_ONLY);
                    ImVCaptchaImage.setImageBitmap(textCaptcha.getImage());
                } else if (!invalidAvatar) {
                    Toast.makeText(RegistrationActivity.this, "Avatar image is Required", Toast.LENGTH_SHORT).show();
                } else if (selectedId == -1) {
                    Toast.makeText(RegistrationActivity.this, "Sex is Required", Toast.LENGTH_SHORT).show();
                } else if (!invalidBirthday) {
                    Toast.makeText(RegistrationActivity.this, "Birthday is Required", Toast.LENGTH_SHORT).show();
                } else if (myCalendar.get(Calendar.YEAR) > 1999) {
                    Toast.makeText(RegistrationActivity.this, "must be at least 18 years old  " + selectedId, Toast.LENGTH_SHORT).show();
                } else {
                    sex = radioButton.getText().toString().trim();
                    registerUser(email, password);

                }

            }
        });
    }

    private void registerUser(final String email, final String password) {
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            finish();
                            uploadImage();
                            Intent intent = new Intent(RegistrationActivity.this, MapsActivity.class);
                            intent.putExtra("email", email);
                            progressDialog.dismiss();
                            startActivity(intent);
                        } else {
                            //display some message here
                            EtEmailRegistration.setError("Please Enter Invalid Email");
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public void uploadImage() {
        imageRef = storageRef.child(firebaseAuth.getCurrentUser().getUid() + ": " + userName).child("images/" + selectedImage.getLastPathSegment());
        //starting upload
        uploadTask = imageRef.putFile(selectedImage);

        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(RegistrationActivity.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                String userId = firebaseAuth.getCurrentUser().getUid();

                userDetails = new UserDetails(downloadUrl.toString(), birthDay, sex, password, email, userName, userId);
                userDetails.setStatus("I am ready to chat");
                userDetails.setUserState("online");
                databaseReference.child(userId).setValue(userDetails);
            }
        });
    }

    public static String getUserName() {
        return userName;
    }
}

