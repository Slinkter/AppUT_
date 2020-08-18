package com.cudpast.apputdemo.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ShowPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    public static final String TAG = ShowPdfActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_show_pdf);
        pdfView = findViewById(R.id.pdfView);
        init();


    }
    private void init() {
        try {
            file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
            Log.e(TAG, "file archivo  " + file.toString());
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .load();


            //    sendEmail2();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendEmail2() {
        Log.e(TAG, "sendEmail()  2 ");
        File root = Environment.getExternalStorageDirectory();
        String filelocation = root.getAbsolutePath() + "/arsi21.pdf";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        String message = "Documento Generado por " + Common.currentUser.getName();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unidades ARSI : " + Common.unidadTrabajoSelected.getAliasUT() + "\n Saludos");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setData(Uri.parse("mailto:luis.j.cueva@gmail.com"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "sendEmail 2  -->  filelocation " + filelocation);
        startActivity(intent);
    }


    private void initApp() {

        file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        Log.e(TAG, "file archivo initApp() " + file.toString());
        if (file.exists()) {
            try {
                Log.e(TAG, "archivo existe ");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.e(TAG, "1 .. 2");
                this.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader&hl=en")));
                Toast.makeText(this, "no cuenta con una aplicacion de pdf", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "no cuenta con una aplicacion de pdf", Toast.LENGTH_SHORT).show();
        }

    }
}