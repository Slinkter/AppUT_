package com.cudpast.apputdemo.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class ShowPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    public static final String TAG = ShowPdfActivity.class.getSimpleName();
    public static final String folderpdf = "/arsi21.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_show_pdf);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
        pdfView = findViewById(R.id.pdfView2);
        init();


    }

    private void init() {
        try {
            file = new File(Environment.getExternalStorageDirectory(), folderpdf);
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .load();
            //
            Log.e(TAG, "file archivo  " + file.toString());
        } catch (Exception e) {
            Log.e(TAG, " error: " + e.getMessage());
        }
    }

    private void sendEmail() {
        //
        String currentusermail = Common.currentUser.getEmail();
        String message = "Documento Generado por " + Common.currentUser.getName() + "\n Saludos";
        File root = Environment.getExternalStorageDirectory();
        String filelocation = root.getAbsolutePath() + folderpdf;
        //
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        //  intent.setDataAndType(Uri.parse("file://" + filelocation),"text/plain");
        intent.setData(Uri.parse("mailto:" + currentusermail));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unidades ARSI : " + Common.unidadTrabajoSelected.getNameUT());
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //
        Log.e(TAG, "sendEmail()");
        Log.e(TAG, "currentusermail  : " + currentusermail);
        Log.e(TAG, "sendEmail : filelocation " + filelocation);
        startActivity(intent);
    }

}