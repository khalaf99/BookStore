package com.example.khalaf.bookstore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.data.Publisher;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.example.khalaf.bookstore.util.Constans;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView ivProfile;
    String mCurrentPhotoPath;
    File imageFile;
    Uri selectedImageUri;
    Uri photoURI;
    private static final int GALLERY_REQUEST = 1;
    private static final int Camera_request = 2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String Email, Address, Name, Password;
    private Publisher publisher;

    EditText etname, etadress, etemail, etpassword;
    ProgressBar pro;
    Button register;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        initview();
        // doll mn el 2a5er 3shan liston to user sign in or sign out
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // da mn el a5er lw 3ml sign in el mauth ha fire fa hayegy hena
                if (user != null) {
                    // User is signed in
                    // saving info and profile image
                    publisher = new Publisher(Email, user.getUid());
                    publisher.setAddress(Address);
                    publisher.setName(Name);
                    if(imageFile !=null){
                        uploadeuserprofileimage(user.getUid());
                    }
                    else{
                        addnewPublisher();
                    }

                    uploadeuserprofileimage(user.getUid());

                } else {
                    // User is signed out
                }

            }
        };

    }

    // upload user profile image
    private void uploadeuserprofileimage(String id) {

        //
        photoURI = FileProvider.getUriForFile(RegistrationActivity.this, "com.example.khalaf.bookstore.Fileprovider", imageFile);
        StorageReference profileImagresRef = mStorageRef.child(Constans.PROFILE_IMAGES_FOLDER + id + ".jpg");

        profileImagresRef.putFile(photoURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String ImageUrl = taskSnapshot.getDownloadUrl().toString();
                        publisher.setImageuri(ImageUrl);
                        addnewPublisher();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        publisher.setImageuri("");
                        addnewPublisher();
                    }
                });

    }

    // upload publisher info to database
    private void addnewPublisher() {
        // Write a message to the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constans.REF_PUBLISHER);

        myRef.child(publisher.getId()).setValue(publisher).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pro.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);
                ActivityLauncter.openMyBooksActivity(RegistrationActivity.this);
                finish();
            }
        });


    }

    // trboot el muth be listener
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // dol onsave we onrestore 3shan lw t3mel rotate we el design yab2a mo5tlef el 7araka de bet5aly el values kolha trg3 ll default
    // fa7taag l dool 3shan t7fz el instance beta3na we kol 7aga tb2a tamam
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Path", mCurrentPhotoPath);
        outState.putSerializable("File", imageFile);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath = savedInstanceState.getString("Path");
        imageFile = (File) savedInstanceState.getSerializable("File");
    }

    private void initview() {

        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        etname = (EditText) findViewById(R.id.et_name);
        etadress = (EditText) findViewById(R.id.et_address);
        etemail = (EditText) findViewById(R.id.et_email);
        etpassword = (EditText) findViewById(R.id.et_password);
        register = (Button) findViewById(R.id.btn_register);

        pro = (ProgressBar) findViewById(R.id.progress_register);
        pro.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        register.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
    }


    private void showImagePickerDialog() {
        // da el diaolge elly byta3
        // cancelable da ma3nah lw dosst fe ay 7taa haroo7 el dialoge
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose")
                .setCancelable(true)
                .setItems(new String[]{getString(R.string.gallery), getString(R.string.camera)}, new DialogInterface.OnClickListener() {
                    @Override
                    // da el dialoge we which de elly 25tyaar beta3k
                    public void onClick(DialogInterface dialog, int which) {
                        // choose gallery
                        if (which == 0) {
                            // intent pick we ba2olo el setdata da lazemo eno ypick sora
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALLERY_REQUEST);
                            // choose camera // image capture da elly howa bysawer soraa
                        } else {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // Ensure that there's a camera activity to handle the intent
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    ex.printStackTrace();
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(RegistrationActivity.this, "com.example.khalaf.bookstore.Fileprovider", photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, Camera_request);
                                }
                            }
                        }
                    }
                }).show();
    }

    // da elly el mafroud y7oot el sora profile 2wel mayerg3 ll app
    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivProfile.getWidth();
        int targetH = ivProfile.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        // Determine how much to scale down the image
        if (targetH != 0 && targetW != 0 && photoW != 0 && photoH != 0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivProfile.setImageBitmap(bitmap);
    }

    // create file unique
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    // de el result beta3t start activityforresult elly fe fun show img
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readFileFromSelectedURI();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else if (requestCode == Camera_request && resultCode == RESULT_OK) {
            setPic();
        }
    }

    // de el result elly tal3 mn el permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // low dost accept
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readFileFromSelectedURI();
            // lw dost denay
        } else {
            Toast.makeText(this, R.string.Cannot_pickup_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileFromSelectedURI() {
        // uri da fe ma3loomat kteer fa 25taar elly enta 3awzo mn el uri da // ahy DATA de ell path // control q 3aliha
        Cursor cursor = getContentResolver().query(selectedImageUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(0);

            cursor.close();
            imageFile = new File(imagePath);
            // de mn el 2a5er ellly betsgel beha el sora
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            ivProfile.setImageBitmap(image);
        }
    }

    // on click photo
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_profile) {
            // da byftaaa7 dialog
            showImagePickerDialog();
            //press button register
        } else if (view.getId() == R.id.btn_register) {
            registernewuser();
        }
    }

    private void registernewuser() {

        Email = etemail.getText().toString();
        Address = etadress.getText().toString();
        Password = etpassword.getText().toString();
        Name = etname.getText().toString();

        if (Name.isEmpty()) {
            etname.setError(getString(R.string.Enter_name));
        } else if (Address.isEmpty()) {
            etadress.setError(getString(R.string.Enter_Adress));
        } else if (Password.length() < 6) {
            etpassword.setError(getString(R.string.Password_must_be_pluse_6));
        } else if (Email.isEmpty()) {
            etemail.setError(getString(R.string.Enter_email));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            etemail.setError(getString(R.string.Email_Not_formatted_well));
        } else {

            pro.setVisibility(View.VISIBLE);
            register.setVisibility(View.GONE);
            // ActivityLauncter.openMyBooksActivity(this);

            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                pro.setVisibility(View.GONE);
                                register.setVisibility(View.VISIBLE);
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegistrationActivity.this, R.string.Email_already_exists,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, R.string.Errore_in_connection,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            // ...
                        }
                    });
        }
    }
}
