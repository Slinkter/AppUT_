package com.cudpast.apputdemo.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.AllPersonalMetricas;
import com.cudpast.apputdemo.Model.MetricasPersonal;
import com.cudpast.apputdemo.Model.Personal;
import com.cudpast.apputdemo.R;
import com.cudpast.apputdemo.Support.ShowPdfActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ReportDataWorkerActivity extends AppCompatActivity {

    public static final String TAG = ReportDataWorkerActivity.class.getSimpleName();
    public static final String folderpdf = "/arsi21.pdf";
    //
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref_datos_paciente;

    private List<MetricasPersonal> list_MetricasPersonales;
    private List<Personal> listaPersonal;
    private MaterialDatePicker mdp;
    private MaterialDatePicker.Builder builder;
    //
    private String seletedDate;
    private ProgressDialog mDialog;
    private ImageView img_reportdatepdf, img_reportmailpdf, img_reportworkpdf, img_reportworkgmail, img_reportexampdf, img_reportexamemail;
    //
    List<String> listDate;
    List<String> listTemperatura;
    List<Integer> listSaturacion;
    List<Integer> listPulso;


    private List<MetricasPersonal> listtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_report_data_worker);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //xml

        //
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //
        checkStatusUser();
    }

    private void checkStatusUser() {

        if (Common.currentUser.getStatus()) {
            enableAll();
        } else {
            disenablefAll();

        }
    }
    //===============================================================================

    public void btn_entrada_pdf(View view) {

        String metodo = "pdf";
        Boolean horario = true;
        //
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        mdp = builder.build();
        mdp.show(getSupportFragmentManager(), "DATE_PICKER");
        mdp.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) dateSelected -> {
            // Show Dialog-waiting
            mDialog = new ProgressDialog(ReportDataWorkerActivity.this);
            mDialog.setMessage("Obteniendo datos ...");
            mDialog.show();
            // Transform date selected
            seletedDate = timeStampToString(dateSelected);
            // Init arrays
            list_MetricasPersonales = new ArrayList<>();
            listaPersonal = new ArrayList<>();
            // Get Data From Firebase and init reference
            ref_datos_paciente = database
                    .getReference(Common.db_unidad_trabajo_data)
                    .child(Common.currentUser.getUid())
                    .child(Common.unidadTrabajoSelected.getIdUT());

            Log.e(TAG, "select Date --> " + ref_datos_paciente.toString());
            ref_datos_paciente.keepSynced(true);
            ref_datos_paciente.orderByKey();
            ref_datos_paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        dateTurnSelected(dataSnapshot, metodo, horario);
                    } else {
                        Log.e(TAG, "no existe unidad minera");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });
            //
        });
    }

    public void btn_salida_pdf(View view) {
        String metodo = "pdf";
        Boolean horario = false;
        //
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        mdp = builder.build();
        mdp.show(getSupportFragmentManager(), "DATE_PICKER");
        mdp.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) dateSelected -> {
            // Show Dialog-waiting
            mDialog = new ProgressDialog(ReportDataWorkerActivity.this);
            mDialog.setMessage("Obteniendo datos ...");
            mDialog.show();
            // Transform date selected
            seletedDate = timeStampToString(dateSelected);
            // Init arrays
            list_MetricasPersonales = new ArrayList<>();
            listaPersonal = new ArrayList<>();
            // Get Data From Firebase and init reference
            ref_datos_paciente = database
                    .getReference(Common.db_unidad_trabajo_data)
                    .child(Common.currentUser.getUid())
                    .child(Common.unidadTrabajoSelected.getIdUT());

            Log.e(TAG, "select Date --> " + ref_datos_paciente.toString());
            ref_datos_paciente.keepSynced(true);
            ref_datos_paciente.orderByKey();
            ref_datos_paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        dateTurnSelected(dataSnapshot, metodo, horario);
                    } else {
                        Log.e(TAG, "no existe unidad minera");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });
            //
        });
    }

    public void btn_worker_pdf(View view) {
        showPdfDialog("pdf");
    }
    //===============================================================================

    private void enableAll() {


        String mensaje = "Función solo para usuarios de pago ";


    }

    private void disenablefAll() {

        String mensaje = "Función solo para usuarios free ";


    }

    private void dateTurnSelected(DataSnapshot dataAll, String metodo, boolean horario) {

        ArrayList<String> arrayListDni = new ArrayList<String>();
        for (DataSnapshot snapshot : dataAll.getChildren()) { //<--- Toda la DATA
            // Get key = dni worker
            String dni = snapshot.getKey();
            Log.e(TAG, "[onDataChange] dni = " + dni);
            if (dni != null) {
                //  Array
                for (DataSnapshot itemDate : snapshot.getChildren()) {  // <-- Todas la metricas por fechas de 1 persona
                    if (itemDate != null) {
                        // Check Date
                        String registerDate = itemDate.getKey().substring(0, 10).trim();
                        boolean checkdate = seletedDate.toString().equalsIgnoreCase(registerDate);
                        if (checkdate) {
                            // Enlistar datos
                            MetricasPersonal data = itemDate.getValue(MetricasPersonal.class);
                            if (data.getHorario() == null) {
                                data.setHorario(false);
                            }

                            if (data.getHorario() == horario) {
                                list_MetricasPersonales.add(data);
                                arrayListDni.add(dni);
                                Log.e(TAG, "[onDataChange] listaMetricasPersonales.size() : " + list_MetricasPersonales.size());
                            }

                        }
                    }
                }
            }
        }
        if (arrayListDni.size() == 0) {
            mDialog.dismiss();
            Log.e(TAG, " No hay datos para esta fecha");
            Toast.makeText(this, "No hay datos para esta fecha", Toast.LENGTH_SHORT).show();
        }
        //===================================================
        //--> Generar lista por horario
        for (int i = 0; i < arrayListDni.size(); i++) {
            Log.e(TAG, "dni : " + arrayListDni.get(i).toString());
            String dni = arrayListDni.get(i);
            DatabaseReference ref_mina = database
                    .getReference(Common.db_unidad_trabajo_personal)
                    .child(Common.currentUser.getUid())
                    .child(Common.unidadTrabajoSelected.getIdUT())
                    .child(dni);

            Log.e(TAG, "ref_mina " + ref_mina.toString());
            ref_mina.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //
                    MetricasPersonal mp = dataSnapshot.getValue(MetricasPersonal.class);
                    Personal personal = dataSnapshot.getValue(Personal.class);
                    //
                    Log.e(TAG, " dataSnapshot: " + dataSnapshot);
                    Log.e(TAG, " dataSnapshot.getKey() : " + dataSnapshot.getKey());
                    Log.e(TAG, " mp.getTempurature() : " + mp.getTempurature());
                    Log.e(TAG, " personal: " + personal.getLast());
                    //
                    if (personal != null) {
                        listaPersonal.add(personal);
                        Log.e(TAG, " listaPersonal.size() : " + listaPersonal.size());
                    }
                    if (listaPersonal.size() == arrayListDni.size()) {
                        // -------------------------------->
                        generarListaporFechaPdf(list_MetricasPersonales, listaPersonal, seletedDate, metodo, horario);
                        //  <--------------------------------
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                }
            });
        }
    }

    private void generarListaporFechaPdf(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate, String metodo, boolean horario) {

        List<AllPersonalMetricas> list_workers = new ArrayList<>();
        int nCountWorkers = listMetricasPersonal.size();
        //
        Log.e(TAG, "nCountWorkers = " + nCountWorkers);
        //
        for (int i = 0; i < nCountWorkers; i++) {
            try {

                AllPersonalMetricas worker = new AllPersonalMetricas();
                //
                worker.setDni(listPersonal.get(i).getDni());
                worker.setName(listPersonal.get(i).getName());
                worker.setLast(listPersonal.get(i).getLast());
                worker.setAge(listPersonal.get(i).getAge());
                worker.setAddress(listPersonal.get(i).getAddress());
                worker.setBorn(listPersonal.get(i).getBorn());
                worker.setDate(listPersonal.get(i).getDate());
                worker.setPhone1(listPersonal.get(i).getPhone1());
                worker.setPhone2(listPersonal.get(i).getPhone2());
                //
                worker.setTempurature(listMetricasPersonal.get(i).getTempurature());
                worker.setSo2(listMetricasPersonal.get(i).getSo2());
                worker.setPulse(listMetricasPersonal.get(i).getPulse());
                worker.setSymptoms(listMetricasPersonal.get(i).getSymptoms());
                worker.setDateRegister(listMetricasPersonal.get(i).getDateRegister());
                worker.setWho_user_register(listMetricasPersonal.get(i).getWho_user_register());
                worker.setTestpruebarapida(listMetricasPersonal.get(i).getTestpruebarapida());
                worker.setHorario(listMetricasPersonal.get(i).getHorario());
                //
                worker.setS1(listMetricasPersonal.get(i).getS1());
                worker.setS2(listMetricasPersonal.get(i).getS2());
                worker.setS3(listMetricasPersonal.get(i).getS3());
                worker.setS4(listMetricasPersonal.get(i).getS4());
                worker.setS5(listMetricasPersonal.get(i).getS5());
                worker.setS6(listMetricasPersonal.get(i).getS6());
                worker.setS7(listMetricasPersonal.get(i).getS7());
                //
                list_workers.add(worker);
            } catch (Exception e) {
                Log.e(TAG, " try-catch nCountWorkers " + e.getMessage());
            }

        }
        // Tratameindo : order lista por Apellido
        if (list_workers.size() >= 1) {
            Collections.sort(list_workers, (o1, o2) -> o1.getLast().compareToIgnoreCase(o2.getLast()));
        }
        //
        if (list_workers.size() >= 1) {
            //
            int pageWidth = 1200;
            int pageHeigt = 2010;
            Date currentDate = new Date();
            //
            java.text.DateFormat dateFormat;
            //
            PdfDocument pdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            //
            PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
            Canvas cansas01 = myPage01.getCanvas();
            //
            Paint title = new Paint();
            title.setTextSize(70);
            title.setTextAlign(Paint.Align.CENTER);
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            title.setColor(Color.BLACK);
            cansas01.drawText("UNIDAD", pageWidth / 2, 80, title);
            cansas01.drawText(Common.unidadTrabajoSelected.getNameUT(), pageWidth / 2, 150, title);

            Paint fecha = new Paint();
            fecha.setTextSize(25f);
            fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            fecha.setTextAlign(Paint.Align.RIGHT);
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            cansas01.drawText("FECHA DE CONSULTA ", pageWidth - 20, 60, fecha);
            fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 80, 90, fecha);
            String fechapdf = dateFormat.format(currentDate);

            dateFormat = new SimpleDateFormat("HH:mm:ss");
            fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            cansas01.drawText("HORA ", pageWidth - 100, 120, fecha);
            fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 90, 150, fecha);

            //
            Paint info = new Paint();
            info.setTextSize(35f);
            info.setTextAlign(Paint.Align.LEFT);
            info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            info.setColor(Color.BLACK);


            String cad_horario = "";
            if (horario) {
                cad_horario = "Entrada";
            } else {
                cad_horario = "Salida";
            }
            cansas01.drawText("Horario  : " + cad_horario, 20, 220, info);
            cansas01.drawText("Responsable : " + Common.currentUser.getName(), 20, 270, info);
            cansas01.drawText("Fecha de medición : " + seletedDate, 20, 320, info);

            // Encabezados
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            myPaint.setTextSize(25f);
            cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
            //
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);

            Paint celda = new Paint();
            celda.setStyle(Paint.Style.STROKE);
            celda.setStrokeWidth(2);
            celda.setTextSize(23f);

            cansas01.drawText("Nro.", 50, 415, celda);
            cansas01.drawText("DNI", 170, 415, celda);
            cansas01.drawText("APELLIDOS Y NOMBRES ", 330, 415, celda);
            cansas01.drawText("TEMPERATURA", 760, 415, celda);
            cansas01.drawText("SO2.", 990, 415, celda);
            cansas01.drawText("PULSO", 1090, 415, celda);
            // se dibuja los recuerado o celdas de los campos
            cansas01.drawLine(120, 380, 120, 430, celda);
            cansas01.drawLine(280, 380, 280, 430, celda);
            cansas01.drawLine(730, 380, 730, 430, celda);
            cansas01.drawLine(960, 380, 960, 430, celda);
            cansas01.drawLine(1070, 380, 1070, 430, celda);
            //
            // el aumento para cada fila para los empleados
            int yInit = 480;
            int ysum = 0;
            //
            Paint temp = new Paint();
            Paint so = new Paint();
            so.setTextSize(25f);
            Paint pulse = new Paint();
            pulse.setTextSize(25f);
            //
            if (nCountWorkers <= 28) {
                //-------------------------------------------------------------------------------
                //---> Pagina 01 Pagina 01 : [0-28]
                //metricas
                try {
                    for (int i = 0; i < nCountWorkers; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);

                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //---> Cierre
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch : Page 01 " + e.getMessage());
                }

                //-------------------------------------------------------------------------------
            } else if (listMetricasPersonal.size() >= 29 && listMetricasPersonal.size() <= 63) {
                Toast.makeText(this, "pagina 2", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-02
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-02 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;
                    for (int i = 29; i < nCountWorkers; i++) {
                        // Up FullName
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch : Page 02 " + e.getMessage());
                }
            } else if (listMetricasPersonal.size() >= 67 && listMetricasPersonal.size() <= 90) {
                Toast.makeText(this, "nCountWorkers >= 67 && nCountWorkers <= 90", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-03
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-03 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-03
                    // [66-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    for (int i = 64; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch : Page 03 " + e.getMessage());
                }
            } else if (listMetricasPersonal.size() >= 99 && listMetricasPersonal.size() <= 133) {

                Toast.makeText(this, "nCountWorkers >= 99 && nCountWorkers <= 133", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-04
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-04 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-04
                    // [66-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    listTemp = 90;

                    for (int i = 64; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);

                    //----------------------------------------------->
                    // Pagina 04-04
                    // [99-133]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas04 = myPage4.getCanvas();
                    //
                    for (int i = 99; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas04.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas04.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas04.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage4);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();

                } catch (Exception e) {
                    Log.e(TAG, "try-catch : Page 04 " + e.getMessage());
                }

            } else if (listMetricasPersonal.size() >= 134 && listMetricasPersonal.size() <= 150) {
                Toast.makeText(this, "nCountWorkers >= 134 && nCountWorkers <= 150", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-04
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-04 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);

                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-04
                    // [64-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    listTemp = 90;

                    for (int i = 91; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);

                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);

                    //----------------------------------------------->
                    // Pagina 04-04
                    // [99-133]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas04 = myPage4.getCanvas();
                    //
                    listTemp = 133;
                    for (int i = 99; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas04.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas04.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas04.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage4);

                    //----------------------------------------------->
                    // Pagina 05-05
                    // [134-150]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo5 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage5 = pdfDocument.startPage(myPageInfo5);
                    Canvas canvas05 = myPage5.getCanvas();
                    //
                    for (int i = 134; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas05.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas05.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas05.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage5);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();

                } catch (Exception e) {
                    Log.e(TAG, "try-catch : Page 05 " + e.getMessage());
                }


            } else {
                Log.e(TAG, "ERROR problemas de index");
                Log.e(TAG, "maximo 150 personas");
            }


            if (metodo.equalsIgnoreCase("pdf")) {
                Log.e(TAG, " metodo pdf ");
                Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            } else {
                Log.e(TAG, " metodo sendEmail ");
                sendEmail();
                mDialog.dismiss();
            }


        }
        mDialog.dismiss();

    }

    private void setColorPulso(int valuePulso, Paint pulse) {
        if (valuePulso >= 86) {
            pulse.setColor(Color.rgb(17, 230, 165));
        } else if (valuePulso >= 70 && valuePulso <= 84) {
            pulse.setColor(Color.rgb(255, 235, 59));
        } else if (valuePulso >= 62 && valuePulso <= 68) {
            pulse.setColor(Color.rgb(255, 38, 38));
        } else {
            pulse.setColor(Color.rgb(255, 38, 38));
        }
    }

    private void setColorSaturacion(int valueSatura, Paint so) {
        if (valueSatura >= 95 && valueSatura <= 99) {
            so.setColor(Color.rgb(17, 230, 165));
        } else if (valueSatura >= 91 && valueSatura <= 94) {
            so.setColor(Color.rgb(255, 235, 59));
        } else if (valueSatura >= 86 && valueSatura <= 90) {
            so.setColor(Color.rgb(255, 38, 38));
        } else {
            so.setColor(Color.rgb(255, 38, 38));
        }


    }

    //===============================================================================

    private void generarListaTrabajadores_pdf(String nombre, String metodo) {

        Log.e(TAG, "---> generarListaporPersonalPdf()  ");
        Log.e(TAG, "generarListaporPersonalPdf list_MetricasPersonales : " + list_MetricasPersonales.size());
        Log.e(TAG, "generarListaporPersonalPdf listDate : " + listDate.size());
        Log.e(TAG, "generarListaporPersonalPdf listTemperatura : " + listTemperatura.size());
        Log.e(TAG, "generarListaporPersonalPdf listSaturacion : " + listSaturacion.size());
        Log.e(TAG, "generarListaporPersonalPdf listPulso : " + listPulso.size());

        int pageWidth = 1200;
        Date currentDate = new Date();
        //
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        //
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(70);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDAD", pageWidth / 2, 80, title);

        Paint fecha = new Paint();
        fecha.setTextSize(25f);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        fecha.setTextAlign(Paint.Align.RIGHT);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cansas01.drawText("FECHA DE CONSULTA ", pageWidth - 20, 60, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 80, 90, fecha);
        String fechapdf = dateFormat.format(currentDate);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        cansas01.drawText("HORA ", pageWidth - 100, 120, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 90, 150, fecha);

        //
        Paint info = new Paint();
        info.setTextSize(35f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);
        cansas01.drawText("Trabajador  : " + nombre, 20, 200, info);
        cansas01.drawText("Unidad de Trabajo : " + Common.unidadTrabajoSelected.getNameUT(), 20, 250, info);
        cansas01.drawText("Responsable : " + Common.currentUser.getName().toString(), 20, 300, info);

        // Encabezados
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 35, 415, myPaint);
        cansas01.drawText("Fecha", 120, 415, myPaint);
        cansas01.drawText("Temp.", 290, 415, myPaint);
        cansas01.drawText("SO2", 390, 415, myPaint);
        cansas01.drawText("Pulso.", 490, 415, myPaint);

        cansas01.drawText("s1.", 600, 415, myPaint);
        cansas01.drawText("s2.", 680, 415, myPaint);
        cansas01.drawText("s3.", 760, 415, myPaint);
        cansas01.drawText("s4.", 840, 415, myPaint);
        cansas01.drawText("s5.", 920, 415, myPaint);
        cansas01.drawText("s6.", 1000, 415, myPaint);
        cansas01.drawText("s7.", 1080, 415, myPaint);


        //
        int ytext = 400;
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
        //

        Paint temp = new Paint();
        Paint so = new Paint();
        so.setTextSize(25f);
        Paint pulse = new Paint();
        pulse.setTextSize(25f);
        //
        //-------------------------------------------------------------------------------
        //---> Pagina 01 Pagina 01 : [0-28]
        //metricas
        double sumaTemp = 0;
        double promedioTemp = 0.0f;

        double sumaSatura = 0;
        double promedioSatura = 0.0f;


        double sumaPulse = 0;
        double promedioPulse = 0.0f;
        //-------------------------------------------------------------------------------
        int size = listDate.size();
        // info trabajador

        for (int i = 0; i < size; i++) {
            Log.e(TAG, "list_MetricasPersonales " + i + "  s1 " + list_MetricasPersonales.get(i).getS1());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s2 " + list_MetricasPersonales.get(i).getS2());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s3 " + list_MetricasPersonales.get(i).getS3());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s4 " + list_MetricasPersonales.get(i).getS4());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s5 " + list_MetricasPersonales.get(i).getS5());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s6 " + list_MetricasPersonales.get(i).getS6());
            Log.e(TAG, "list_MetricasPersonales " + i + "  s7 " + list_MetricasPersonales.get(i).getS7());
            ysumname = ysumname + 50;
            cansas01.drawText(listDate.get(i).substring(0, 10).trim(), 100, ytextname + ysumname, myPaint);
            /*
            cansas01.drawText(list_MetricasPersonales.get(i).getS2().toString(), 600, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS2().toString(), 680, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS3().toString(), 760, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS4().toString(), 840, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS5().toString(), 920, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS6().toString(), 1000, ytextname + ysumname, myPaint);
            cansas01.drawText(list_MetricasPersonales.get(i).getS7().toString(), 1080, ytextname + ysumname, myPaint);
            */
            if (list_MetricasPersonales.get(i).getS1()) {
                cansas01.drawText("si", 600, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 600, ytextname + ysumname, myPaint);
            }
            if (list_MetricasPersonales.get(i).getS2()) {
                cansas01.drawText("si", 680, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 680, ytextname + ysumname, myPaint);
            }
            if (list_MetricasPersonales.get(i).getS3()) {
                cansas01.drawText("si", 760, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 760, ytextname + ysumname, myPaint);
            }
            if (list_MetricasPersonales.get(i).getS4()) {
                cansas01.drawText("si", 840, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 840, ytextname + ysumname, myPaint);
            }

            if (list_MetricasPersonales.get(i).getS5()) {
                cansas01.drawText("si", 920, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 920, ytextname + ysumname, myPaint);
            }

            if (list_MetricasPersonales.get(i).getS6()) {
                cansas01.drawText("si", 1000, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 1000, ytextname + ysumname, myPaint);
            }
            if (list_MetricasPersonales.get(i).getS7()) {
                cansas01.drawText("si", 1080, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 1080, ytextname + ysumname, myPaint);
            }

        }


        for (int i = 0; i < size; i++) {
            ysum = ysum + 50;
            // Nro
            cansas01.drawText(i + 1 + " ", 42, ytext + ysum, myPaint);
            // Temperatura
            cansas01.drawText(listTemperatura.get(i).toString(), 300, ytext + ysum, myPaint);
            sumaTemp = sumaTemp + Double.parseDouble(listTemperatura.get(i).toString());

            // Saturacion
            int valueSatura = Integer.parseInt(listSaturacion.get(i).toString());

            sumaSatura = sumaSatura + valueSatura;

            if (valueSatura >= 95 && valueSatura <= 99) {
                so.setColor(Color.rgb(17, 230, 165));
            } else if (valueSatura >= 91 && valueSatura <= 94) {
                so.setColor(Color.rgb(255, 235, 59));
            } else if (valueSatura >= 86 && valueSatura <= 90) {
                so.setColor(Color.rgb(255, 38, 38));
            } else {
                so.setColor(Color.rgb(255, 38, 38));
            }
            cansas01.drawText(listSaturacion.get(i).toString(), 400, ytext + ysum, so);
            // Pulse
            int valuePulso = Integer.parseInt(listPulso.get(i).toString());

            sumaPulse = sumaPulse + valuePulso;

            if (valuePulso >= 86) {
                pulse.setColor(Color.rgb(17, 230, 165));
            } else if (valuePulso >= 70 && valuePulso <= 84) {
                pulse.setColor(Color.rgb(255, 235, 59));
            } else if (valuePulso >= 62 && valuePulso <= 68) {
                pulse.setColor(Color.rgb(255, 38, 38));
            } else {
                pulse.setColor(Color.rgb(255, 38, 38));
            }
            cansas01.drawText(listPulso.get(i).toString(), 500, ytext + ysum, pulse);
            //


        }

        try {
            promedioTemp = sumaTemp / size;
            promedioSatura = sumaSatura / size;
            promedioPulse = sumaPulse / size;

            String cadTemp = String.valueOf(promedioTemp);
            String cadSa = String.valueOf(promedioSatura);
            String cadPulse = String.valueOf(promedioPulse);

            int reducir = 50;

            Paint fontLeyenda = new Paint();
            fontLeyenda.setTextSize(40);
            fontLeyenda.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            fontLeyenda.setColor(Color.BLACK);

            cansas01.drawText("Promedios ", 35, 1350 - reducir, fontLeyenda);
            //
            cansas01.drawText("Promedio de los ultimos " + size + " medidas ", 35, 1400 - reducir, myPaint);
            cansas01.drawText("Promedio temperatura : " + cadTemp.substring(0, 4), 35, 1450 - reducir, myPaint);
            cansas01.drawText("Promedio SO2  : " + cadSa.substring(0, 4), 35, 1500 - reducir, myPaint);
            cansas01.drawText("Promedio pulso : " + cadPulse.substring(0, 4), 35, 1550 - reducir, myPaint);


            cansas01.drawText("Leyenda ", 35, 1600 - 20, fontLeyenda);
            //
            cansas01.drawText("s1 : Tos ", 35, 1650, myPaint);
            cansas01.drawText("s2 : Dolor de garganta ", 35, 1700, myPaint);
            cansas01.drawText("s3 : Fiebre ", 35, 1750, myPaint);
            cansas01.drawText("s4 : Dificultad respitoria", 35, 1800, myPaint);
            cansas01.drawText("s5 : Diarrea ", 35, 1850, myPaint);
            cansas01.drawText("s6 : Dolor abdominal ", 35, 1900, myPaint);
            cansas01.drawText("s7 : Dolor Pecho ", 35, 1950, myPaint);


            //-------------------------------------------------------------------------------
        } catch (Exception e) {
            Log.e("error promedio  :  ", " --> " + e.getMessage());
        }


        pdfDocument.finishPage(myPage01);
        //---> Cierre
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            Log.e(TAG, "TRY-CATH : " + e.getMessage());
        }
        pdfDocument.close();
        //


        if (metodo.equalsIgnoreCase("pdf")) {
            Log.e(TAG, " metodo pdf ");
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
            startActivity(intent);
            mDialog.dismiss();
        } else if (metodo.equalsIgnoreCase("email")) {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }

    private void generarTrabajador_pdf(String dni, String nombre, String metodo) {
        Log.e(TAG, "-----> funcion  : getDataFromFirebase");

        list_MetricasPersonales = new ArrayList<>();
        listDate = new ArrayList<String>();
        listTemperatura = new ArrayList<>();
        listSaturacion = new ArrayList<>();
        listPulso = new ArrayList<>();

        Query query = FirebaseDatabase
                .getInstance()
                .getReference(Common.db_unidad_trabajo_data)
                .child(Common.currentUser.getUid())
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(dni)
                .orderByKey()
                .limitToLast(15);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);
                        if (metricasPersonal != null) {
                            list_MetricasPersonales.add(metricasPersonal);
                            listDate.add(metricasPersonal.getDateRegister().toString());
                            listTemperatura.add((metricasPersonal.getTempurature()));
                            listSaturacion.add(Integer.parseInt(metricasPersonal.getSo2()));
                            listPulso.add(Integer.parseInt(metricasPersonal.getPulse()));
                        } else {
                            mDialog.dismiss();
                            Log.e(TAG, "getDataFromFirebase --> MetricasPersonal = NULL");
                        }
                    }
                    //
                    generarListaTrabajadores_pdf(nombre, metodo);
                    //
                    Log.e(TAG, "---> list_MetricasPersonales.size() : " + list_MetricasPersonales.size());
                    Log.e(TAG, "---> listDate.size() : " + listDate.size());
                    Log.e(TAG, "---> listTemperatura.size() : " + listTemperatura.size());
                    Log.e(TAG, "---> listSaturacion.size() : " + listSaturacion.size());
                    Log.e(TAG, "---> listPulso.size() : " + listPulso.size());
                } catch (Exception e) {
                    Log.e(TAG, "ERROR --> getDataFromFirebase : " + e.getMessage());
                    Toast.makeText(ReportDataWorkerActivity.this, "Error al Generar PDF ", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error" + " : " + databaseError.getMessage());
                mDialog.dismiss();
            }
        });


    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }

    private void sendEmail() {
        Log.e(TAG, "sendEmail()  2 ");
        File root = Environment.getExternalStorageDirectory();
        String filelocation = root.getAbsolutePath() + "/arsi21.pdf";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        String message = "Documento Generado por " + Common.currentUser.getName();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unidades: " + Common.unidadTrabajoSelected.getNameUT() + "\n Saludos");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        String currentusermail = Common.currentUser.getEmail();
        Log.e(TAG, "currentusermail  : " + currentusermail);
        intent.setData(Uri.parse("mailto:" + currentusermail));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "sendEmail 2  -->  filelocation " + filelocation);
        startActivity(intent);
    }

    public void showPdfDialog(String metodo) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_report_dni, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();

        TextInputLayout report_dni_layout = view.findViewById(R.id.report_dni_layout);
        TextInputEditText report_dni = view.findViewById(R.id.report_dni);

        Button btn_report_dni_close = view.findViewById(R.id.btn_report_dni_close);
        Button btn_report_dni = view.findViewById(R.id.btn_report_dni);

        btn_report_dni.setOnClickListener(v -> {
            String dni = report_dni.getText().toString();
            if (report_dni.getText().toString().trim().isEmpty()) {
                report_dni_layout.setError("Ingrese su DNI");
                dialog.dismiss();
            } else {
                report_dni_layout.setError(null);
                mDialog = new ProgressDialog(view.getContext());
                mDialog.setMessage("Obteniendo datos ...");
                mDialog.show();
                //
                Log.e(TAG, "-----> funcion  : consultarDatosPaciente");
                Log.e(TAG, " dni : " + dni);
                //
                DatabaseReference ref_mina = database
                        .getReference(Common.db_unidad_trabajo_personal)
                        .child(Common.currentUser.getUid())
                        .child(Common.unidadTrabajoSelected.getIdUT())
                        .child(dni);

                ref_mina.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }
                            Log.e(TAG, " personal.getName() : " + personal.getName());
                            report_dni_layout.setError(null);
                            String fullname = personal.getName() + " " + personal.getLast();
                            generarTrabajador_pdf(dni, fullname, metodo);
                        } else {
                            Log.e(TAG, " personal.getName() : null ");
                            mDialog.dismiss();
                            report_dni_layout.setError("El trabajador no exsite en la base de datos");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mDialog.dismiss();
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });

            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showEmailoDialog() {
        String metodo = "email";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_report_dni, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();

        TextInputLayout report_dni_layout = view.findViewById(R.id.report_dni_layout);
        TextInputEditText report_dni = view.findViewById(R.id.report_dni);

        Button btn_report_dni_close = view.findViewById(R.id.btn_report_dni_close);
        Button btn_report_dni = view.findViewById(R.id.btn_report_dni);


        btn_report_dni.setOnClickListener(v -> {
            String dni = report_dni.getText().toString();

            if (report_dni.getText().toString().trim().isEmpty()) {
                report_dni_layout.setError("Ingrese su DNI");
                dialog.dismiss();
            } else {
                report_dni_layout.setError(null);
                mDialog = new ProgressDialog(view.getContext());
                mDialog.setMessage("Obteniendo datos ...");
                mDialog.show();
                //
                Log.e(TAG, "-----> funcion  : consultarDatosPaciente");
                Log.e(TAG, " dni : " + dni);
                //CreateUT

                DatabaseReference ref_mina = database
                        .getReference(Common.db_unidad_trabajo_personal)
                        .child(Common.currentUser.getUid())
                        .child(Common.unidadTrabajoSelected.getIdUT())
                        .child(dni);

                ref_mina.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }
                            Log.e(TAG, " personal.getName() : " + personal.getName());
                            report_dni_layout.setError(null);
                            String fullname = personal.getName() + " " + personal.getLast();
                            generarTrabajador_pdf(dni, fullname, metodo);
                        } else {
                            Log.e(TAG, " personal.getName() : null ");
                            mDialog.dismiss();
                            report_dni_layout.setError("El trabajador no exsite en la base de datos");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mDialog.dismiss();
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });
                Toast.makeText(this, "Documento generado", Toast.LENGTH_SHORT).show();
            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //===============================================================================

    public void showTestEmailDNI(final String metodo) {


        AlertDialog.Builder builder = new AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_test_fast, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        //
        TextInputLayout test_dni_layout = view.findViewById(R.id.test_dni_layout);
        TextInputEditText test_dni = view.findViewById(R.id.test_dni);


        //
        Button btn_test_dni, btn_test_dni_close;
        btn_test_dni = view.findViewById(R.id.btn_test_dni);
        btn_test_dni_close = view.findViewById(R.id.btn_test_dni_close);

        btn_test_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dni = test_dni.getText().toString();
                Log.e(TAG, "[TextInputEditText] DNI " + dni);

                DatabaseReference ref_mina = database
                        .getReference(Common.db_unidad_trabajo_personal)
                        .child(Common.currentUser.getUid())
                        .child(Common.unidadTrabajoSelected.getIdUT())
                        .child(dni);

                ref_mina.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            test_dni_layout.setError(null);
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }

                            Log.e(TAG, "NOMBRE DEL TRABJADOR  = " + personal.getName() + " " + personal.getLast());
                            ref_datos_paciente = database
                                    .getReference(Common.db_unidad_trabajo_data)
                                    .child(Common.currentUser.getUid())
                                    .child(Common.unidadTrabajoSelected.getIdUT())
                                    .child(dni);

                            ref_datos_paciente.keepSynced(true);
                            ref_datos_paciente.orderByKey();
                            ref_datos_paciente.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    listtemp = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);
                                        if (metricasPersonal != null) {
                                            Log.e(TAG, " [ref_datos_paciente] date : " + metricasPersonal.getTestpruebarapida());
                                            Log.e(TAG, " [ref_datos_paciente] temperature : " + metricasPersonal.getTestpruebarapida());
                                            Log.e(TAG, " [ref_datos_paciente] s1 : " + metricasPersonal.getTestpruebarapida());
                                            Log.e(TAG, " [ref_datos_paciente] test : " + metricasPersonal.getTestpruebarapida());

                                            if (metricasPersonal.getTestpruebarapida() != null) {
                                                boolean test = metricasPersonal.getTestpruebarapida();
                                                if (test) {
                                                    listtemp.add(metricasPersonal);
                                                }
                                            }
                                        }
                                    }

                                    try {
                                        String nombre = personal.getName() + " " + personal.getLast();
                                        generarPDFTestFast(listtemp, nombre, metodo);
                                    } catch (Exception e) {
                                        Log.e(TAG, "try-catch : " + e.getMessage());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "  [ref_datos_paciente] error : " + databaseError.getMessage());
                                }
                            });


                        } else {
                            Log.e(TAG, "personal  = null");
                            test_dni_layout.setError("El trabajador no exsite en la base de datos");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });
            }
        });

        btn_test_dni_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void generarPDFTestFast(List<MetricasPersonal> listtemp, String nombre, String metodo) {
        Log.e(TAG, "---> generarListaporPersonalPdf()  ");
        Log.e(TAG, " Tamaño : " + listtemp.size());


        //
        int pageWidth = 1200;
        Date currentDate = new Date();
        //
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        //
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(70);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDAD", pageWidth / 2, 80, title);

        Paint fecha = new Paint();
        fecha.setTextSize(25f);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        fecha.setTextAlign(Paint.Align.RIGHT);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cansas01.drawText("FECHA DE CONSULTA ", pageWidth - 20, 60, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 80, 90, fecha);
        String fechapdf = dateFormat.format(currentDate);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        cansas01.drawText("HORA ", pageWidth - 100, 120, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 90, 150, fecha);

        //
        Paint info = new Paint();
        info.setTextSize(35f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);

        cansas01.drawText("Unidad de trabajo      :  " + Common.unidadTrabajoSelected.getNameUT(), 20, 200, info);
        cansas01.drawText("Responsable  :  " + Common.currentUser.getName().toString(), 20, 250, info);
        cansas01.drawText("Trabajador   :  " + nombre, 20, 300, info);


        // Encabezados
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 65, 415, myPaint);
        cansas01.drawText("Fecha", 220, 415, myPaint);
        //   cansas01.drawText("Temperatura", 470, 415, myPaint);
        cansas01.drawText("Prueba Rápida.", 470, 415, myPaint);
        cansas01.drawText("Próxima Prueba Rápida.", 820, 415, myPaint);

        cansas01.drawLine(140, 380, 140, 430, myPaint);
        cansas01.drawLine(410, 380, 410, 430, myPaint);
        cansas01.drawLine(700, 380, 700, 430, myPaint);

        //
        int ytext = 400;
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
        //

        Paint temp = new Paint();
        Paint so = new Paint();
        so.setTextSize(25f);
        Paint pulse = new Paint();
        pulse.setTextSize(25f);
        //
        //-------------------------------------------------------------------------------
        //---> Pagina 01 Pagina 01 : [0-28]
        //metricas

        //-------------------------------------------------------------------------------
        int size = listtemp.size();
        // info trabajador

        for (int i = 0; i < size; i++) {
            Log.e(TAG, " x : " + i);
            Log.e(TAG, " y : " + listtemp.get(i).getDateRegister());
            Log.e(TAG, " z : " + listtemp.get(i).getTempurature());
            Log.e(TAG, " r : " + listtemp.get(i).getTestpruebarapida());
            boolean test = listtemp.get(i).getTestpruebarapida();
            String testfast = "";
            if (test) {
                testfast = "Si";
                ysum = ysum + 50;
                // Nro
                cansas01.drawText(i + 1 + " ", 80, ytext + ysum, myPaint);
                // Fecha
                cansas01.drawText(listtemp.get(i).getDateRegister(), 160, ytext + ysum, myPaint);
                // Temperatura
                //       cansas01.drawText(listtemp.get(i).getTempurature(), 520, ytext + ysum, myPaint);
                cansas01.drawText(testfast, 520, ytext + ysum, so);
                // Prueba Rapida

                calcular(listtemp.get(i).getDateRegister(), cansas01, 880, ytext + ysum, so);


                //cansas01.drawText(listtemp.get(i).getDateRegister(), 880, ytext + ysum, so);
                //
            }
        }
        pdfDocument.finishPage(myPage01);
        //---> Cierre
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            Log.e(TAG, "TRY-CATH : " + e.getMessage());
        }
        pdfDocument.close();
        //


        if (metodo.equalsIgnoreCase("pdf")) {
            Log.e(TAG, " metodo pdf ");
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
            startActivity(intent);
            mDialog.dismiss();
        } else if (metodo.equalsIgnoreCase("email")) {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }

    private void calcular(String dateRegister, Canvas cansas01, int i, int i1, Paint so) {

        final String cadena;
        int dias = 15;


        String sDate1 = dateRegister.substring(0, 10).trim().toString();

        try {
            Date date1;
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(sDate1);
            Log.e(TAG, "sDate1 = " + sDate1);
            Log.e(TAG, "date1 b  = " + date1);

            Date fecha = date1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha); // Configuramos la fecha que se recibe
            calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0


            cadena = calendar.getTime().toString();
            String nuevotest = DateFormat.format("yyyy-MM-dd", calendar).toString();

            cansas01.drawText(nuevotest, i, i1, so);
            Log.e(TAG, "date1 a = " + date1);
            Log.e(TAG, "cadena = " + cadena);
        } catch (Exception e) {
            Log.e(TAG, "try : " + e.getMessage());
        }


    }

    public void btnReturnAllMain(View view) {
        Intent intent = new Intent(ReportDataWorkerActivity.this, AllActivity.class);
        startActivity(intent);
        finish();
    }


    public void btn_test_fast(View view) {
        showTestEmailDNI("pdf");
    }
}
