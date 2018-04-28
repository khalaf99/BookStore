package com.example.khalaf.bookstore.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.data.Book;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.example.khalaf.bookstore.util.Constans;
import com.example.khalaf.bookstore.util.DatePickerFragment;
import com.example.khalaf.bookstore.util.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditBookActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {


    // 3shan a open project tany we msh 3ayez yefta7 lazem tany ta5od el 7agat mn el gradle wrapper distributionUrl=https\:
    // services.gradle.org/distributions/gradle-3.3-all.zip and past into the project we td5ol 3la el ta5od (classpath 'com.android.tools.build:gradle:2.3.3')
    // we t7tto fel project

    Button btnBookDate, btnAddPdfFile, btnsavechanges;
    ImageView ivBook;
    EditText etBookName, etBookDesc, etprice;
    private static final int GALLERY_REQUEST = 1;
    private static final int Camera_request = 2;
    private static final int PICK_FILE_REQUEST_CODE = 3;
    File imageFile;

    String mCurrentPhotoPath;
    Uri selectedImageUri;
    Uri PdffileUri;

    // fragment da 7aga keda bett7t 3la el activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        btnBookDate = findViewById(R.id.btn_book_date);
        btnAddPdfFile = findViewById(R.id.btn_add_pdf_file);
        btnsavechanges = findViewById(R.id.btn_save_book);
        ivBook = findViewById(R.id.iv_book);
        etBookName = findViewById(R.id.et_book_name);
        etprice = findViewById(R.id.et_book_price);
        etBookDesc = findViewById(R.id.et_book_desc);

        btnBookDate.setOnClickListener(this);
        btnAddPdfFile.setOnClickListener(this);
        btnsavechanges.setOnClickListener(this);
        ivBook.setOnClickListener(this);

        // to get object book elly ana ba3ttoo 2ma byrooo8 3la activity edit book
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            book = (Book) bundle.getSerializable(ActivityLauncter.BOOK_KEY);
            fillbookdata();
        }else{
            finish();
        }

    }

    private void fillbookdata() {
        etBookName.setText(book.getTitle());
        etBookDesc.setText(book.getDesc());
        etprice.setText(String.valueOf(book.getPrice()));
        btnBookDate.setText(book.getDate()==null ? "" : book.getDate());
        Glide.with(this).load(book.getImageUrl()).into(ivBook);
        btnAddPdfFile.setText(book.getPdftitle() == null ? "" : book.getPdftitle());
        btnAddPdfFile.setTextColor(Color.RED);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_book_date:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.iv_book:
                showImagePickerDialog();
                break;
            case R.id.btn_add_pdf_file:
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, PICK_FILE_REQUEST_CODE);
                break;
            case R.id.btn_save_book:
                updateBookData();
                break;
        }
    }

    Book book;

    private void updateBookData() {

        Utilities.showLoadingDialog(EditBookActivity.this, Color.WHITE);
        String title = etBookName.getText().toString();
        String Desc = etBookDesc.getText().toString();
        String date = btnBookDate.getText().toString();
        double price = Double.parseDouble(etprice.getText().toString());
        String pdftitle = btnAddPdfFile.getText().toString();

        book.setDate(date);
        book.setTitle(title);
        book.setPrice(price);
        book.setDesc(Desc);
        book.setPdftitle(pdftitle);

        if(PdffileUri !=null){
            UploadpdfFile();
        }
        else if(imageFile != null)
            uploadBookImage();
        else
            saveBookData();

    }

    private void UploadpdfFile() {
        FirebaseStorage.getInstance()
                .getReference()
                .child(Constans.BOOK_PDF_FOLDER + book.getId() + ".pdf")
                .putFile(PdffileUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String pdfUrl = task.getResult().getDownloadUrl().toString();
                            book.setPdfUri(pdfUrl);
                        }
                        uploadBookImage();
                    }
                });


    }

    private void uploadBookImage() {
        Uri photoURI = FileProvider.getUriForFile(EditBookActivity.this, "com.example.khalaf.bookstore.Fileprovider", imageFile);
        FirebaseStorage.getInstance()
                .getReference()
                .child(Constans.BOOk_IMAGES_FOLDER + book.getId() + ".jpg")
                .putFile(photoURI)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String imageUrl = task.getResult().getDownloadUrl().toString();
                            book.setImageUrl(imageUrl);
                        }
                        saveBookData();
                    }
                });
    }

    private void saveBookData() {
        FirebaseDatabase.getInstance()
                .getReference(Constans.REF_BOOK)
                .child(book.getId())
                .setValue(book)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utilities.dismissLoadingDialog();
                            finish();
                        } else {
                            Toast.makeText(EditBookActivity.this, "error_in_connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // profile pic
    private void showImagePickerDialog() {
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
                                    Uri photoURI = FileProvider.getUriForFile(EditBookActivity.this, "com.example.khalaf.bookstore.Fileprovider", photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, Camera_request);
                                }
                            }
                        }
                    }
                }).show();
    }

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
        } else if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // get uri of my selected pdf
            PdffileUri = (data.getData());
            getPdfFiletitle();

        }
    }

    private void getPdfFiletitle() {
        Cursor cursor = getContentResolver().query(PdffileUri, new String[]{MediaStore.Files.FileColumns.DISPLAY_NAME}, null, null, null);
        String PdffileName ="File Selected";
        if (cursor != null) {
            cursor.moveToFirst();
            PdffileName = cursor.getString(0);
            cursor.close();
        } else {
            String[] split = PdffileUri.toString().split("/");
            if (split.length > 0 && split[split.length - 1] != null) {
                PdffileName = split[split.length - 1].replace("%20", " ");
            }
        }
        btnAddPdfFile.setText(PdffileName);
        btnAddPdfFile.setTextColor(Color.RED);

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
            ivBook.setImageBitmap(image);
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivBook.getWidth();
        int targetH = ivBook.getHeight();

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
        ivBook.setImageBitmap(bitmap);
    }
    ///////////////////////////////////////////////////

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        btnBookDate.setText(dayOfMonth + " / " + (month + 1) + " / " + year);
    }
}

