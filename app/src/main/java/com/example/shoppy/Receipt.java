package com.example.shoppy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Receipt extends AppCompatActivity {

    FirebaseAuth mAuth;

    String userID;
    String x, y, z, time, w, a = "";
    Bitmap bmp, scaledBitmap;
    Date dateObj;
    DateFormat dateFormat;

    int pageWidth = 2000;
    public static String cName, cPhone;

    DatabaseReference dbReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        dbReference = FirebaseDatabase.getInstance().getReference("Receipts");

//        Put image logo on PDF
//        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_shoppy);
//        scaledBitmap = Bitmap.createScaledBitmap(bmp, 550,500,false);

        //Get total price from Cart
        Intent intent = getIntent();
        String totalPrice = intent.getStringExtra(CartActivity.EXTRA_NUMBER);

        String[] pID = intent.getStringArrayExtra("pID");
        String[] pName = intent.getStringArrayExtra("pName");
        String[] pPrice = intent.getStringArrayExtra("pPrice");
        String[] pQuantity = intent.getStringArrayExtra("pQuantity");

        String count = intent.getStringExtra("count");
        int Count = Integer.parseInt(count);

        cName = intent.getStringExtra("cName");
        cPhone = intent.getStringExtra("cPhone");


        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        createPDF(totalPrice, pID, pName, pPrice, pQuantity, Count, cPhone, cName);
    }


    private void createPDF(final String totalPrice, final String[] pID, final String[] pName, final String[] pPrice, final String[] pQuantity, final int count, String cPhone, String cName) {
        dateObj = new Date();

        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(2000,6000,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(70);
        canvas.drawText("INVOICE", pageWidth / 2, 500, titlePaint);


        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Costumer Name: " + cName, 40,590, myPaint);
        canvas.drawText("Contact No: " + cPhone, 40,640, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Invoice No: " + System.currentTimeMillis(), 40, 690, myPaint);

        dateFormat = new SimpleDateFormat("dd/MM/yy");
        canvas.drawText("Date: " + dateFormat.format(dateObj), 40, 740, myPaint);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        canvas.drawText("Time: " + dateFormat.format(dateObj), 40, 790, myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20, 810, pageWidth - 150, 860, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Product Id", 65, 850, myPaint);
        canvas.drawText("Item Name", 380, 850, myPaint);
        canvas.drawText("Price", 1230, 850, myPaint);
        canvas.drawText("Qty", 1410, 850, myPaint);
        canvas.drawText("Total", 1580, 850, myPaint);

        canvas.drawLine(360, 820, 360, 850, myPaint);
        canvas.drawLine(1170, 820, 1170, 850, myPaint);
        canvas.drawLine(1380, 820, 1380, 850, myPaint);
        canvas.drawLine(1500, 820, 1500, 850, myPaint);

//        image in pdf
//        canvas.drawBitmap(scaledBitmap, 30, 40, myPaint);

        int b = 900;

        for (int i = 0; i < count; i++) {

            canvas.drawText(pID[i], 65, b, myPaint);
            canvas.drawText(pName[i], 380, b, myPaint);
            canvas.drawText(pPrice[i], 1230, b, myPaint);
            canvas.drawText(pQuantity[i], 1410, b, myPaint);
            b = b + 50;
        }

        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(20, b - 20, pageWidth - 150, b - 20, myPaint);
        canvas.drawText("" + totalPrice, 1580, b + 15, myPaint);

        myPdfDocument.finishPage(myPage);

        dateFormat = new SimpleDateFormat("HHmmss");
        time = dateFormat.format(dateObj);
        dateFormat = new SimpleDateFormat("ddMMyy");
        y = dateFormat.format(dateObj);

//        File file = new File(Environment.getExternalStorageDirectory(), time + "_" + y + ".pdf");
        File file = new File(this.getExternalFilesDir("/"), time + "_" + y + ".pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(String.valueOf(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();


        Intent intent = new Intent(Receipt.this, PurchaseHistory.class);
        startActivity(intent);

        uploadPDFFile(file);

        Toast.makeText(Receipt.this, "Receipt Downloaded", Toast.LENGTH_SHORT).show();
    }


    private void uploadPDFFile(File data) {
        dateObj = new Date();

        dateFormat = new SimpleDateFormat("ddMMyy");
        z = "INVOICE" + "_" + dateFormat.format(dateObj);
        a = dateFormat.format(dateObj);
        dateFormat = new SimpleDateFormat("HHmmss");
        time = dateFormat.format(dateObj);
        w = z +"_"+ time;

        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReferenceFromUrl("gs://shoppy-dafbb.appspot.com");
        StorageReference reference = storageReference.child(cPhone + "/" + w +".pdf");
        UploadTask uploadTask = reference.putFile(Uri.fromFile(data));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();

                dateFormat = new SimpleDateFormat("ddMMyy");
                x = cPhone + "_" + dateFormat.format(dateObj);
                uploadPDF uploadPDF = new uploadPDF("INVOICE_" + a + "_" + time + ".pdf", url.toString());
                dbReference.child(cPhone).child(dbReference.push().getKey()).setValue(uploadPDF);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        });
    }
}
