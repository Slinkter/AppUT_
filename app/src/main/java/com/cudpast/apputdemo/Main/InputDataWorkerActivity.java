package com.cudpast.apputdemo.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.MetricasPersonal;
import com.cudpast.apputdemo.Model.Personal;
import com.cudpast.apputdemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputDataWorkerActivity extends AppCompatActivity {

    public static final String TAG = InputDataWorkerActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private TextView show_consulta_fisrtname, show_consulta_lastname;
    private TextInputLayout show_consulta_nombre_layout, show_consulta_last_layout;
    private TextInputLayout input_dni_layout, input_temperatura_layout, input_saturacion_layout, input_pulso_layout, input_sintomas_layout;
    LinearLayout input_new_sintomas_layout, input_examen_layout;
    private TextInputEditText input_dni, input_temperatura, input_saturacion, input_pulso;
    private EditText input_sintomas;
    private CheckBox input_test_yes, input_test_no;

    private CheckBox s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15;
    private Boolean sa1, sa2, sa3, sa4, sa5, sa6, sa7, sa8, sa9, sa10, sa11, sa12, sa13, sa14, sa15;
    private boolean testfastcovid;
    private Personal personal;
    private Button btn_input_consulta, btn_input_saveData, btn_input_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_input_data_worker);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        show_consulta_fisrtname = findViewById(R.id.show_consulta_fisrtname);
        show_consulta_lastname = findViewById(R.id.show_consulta_lastname);

        show_consulta_nombre_layout = findViewById(R.id.show_consulta_nombre_layout);
        show_consulta_last_layout = findViewById(R.id.show_consulta_last_layout);

        input_dni_layout = findViewById(R.id.input_dni_layout);
        input_temperatura_layout = findViewById(R.id.input_temperatura_layout);
        input_saturacion_layout = findViewById(R.id.input_saturacion_layout);
        input_pulso_layout = findViewById(R.id.input_pulso_layout);


        //input_sintomas_layout = findViewById(R.id.input_sintomas_layout);

        input_new_sintomas_layout = findViewById(R.id.input_new_sintomas_layout);
        input_examen_layout = findViewById(R.id.input_examen_layout);


        input_dni = findViewById(R.id.input_dni);
        input_temperatura = findViewById(R.id.input_temperatura);
        input_saturacion = findViewById(R.id.input_saturacion);
        input_pulso = findViewById(R.id.input_pulso);

        input_sintomas = findViewById(R.id.input_sintomas);
        //
        btn_input_consulta = findViewById(R.id.btn_input_consulta);
        btn_input_saveData = findViewById(R.id.btn_input_data);
        btn_input_back = findViewById(R.id.btn_input_back);
        //
        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        s4 = findViewById(R.id.s4);
        s5 = findViewById(R.id.s5);
        s6 = findViewById(R.id.s6);
        s7 = findViewById(R.id.s7);
        s8 = findViewById(R.id.s8);
        s9 = findViewById(R.id.s9);
        s10 = findViewById(R.id.s10);
        s11 = findViewById(R.id.s11);
        s12 = findViewById(R.id.s12);
        s13 = findViewById(R.id.s13);
        s14 = findViewById(R.id.s14);
        s15 = findViewById(R.id.s15);
        s1.setText("Tos");
        s2.setText("Dolor de Garganta");
        s3.setText("Fiebre");
        s4.setText("Dificultad respitoria");
        s5.setText("Diarrea");
        s6.setText("Dolor abdominal");
        s7.setText("Dolor pecho");
        sa1 = false;
        sa2 = false;
        sa3 = false;
        sa4 = false;
        sa5 = false;
        sa6 = false;
        sa7 = false;
        //
        s8.setVisibility(View.GONE);
        s9.setVisibility(View.GONE);
        s10.setVisibility(View.GONE);
        s11.setVisibility(View.GONE);
        s12.setVisibility(View.GONE);
        s13.setVisibility(View.GONE);
        s14.setVisibility(View.GONE);
        s15.setVisibility(View.GONE);

        //
        input_test_yes = findViewById(R.id.input_test_yes);
        input_test_no = findViewById(R.id.input_test_no);
        //
        s1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa1 = isChecked;
            Log.e(TAG, " s1  = " + sa1);
        });

        s2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa2 = isChecked;
            Log.e(TAG, " s2  = " + sa2);
        });

        s3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa3 = isChecked;
            Log.e(TAG, " s3  = " + sa3);
        });

        s4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa4 = isChecked;
            Log.e(TAG, " s4  = " + sa4);
        });

        s5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa5 = isChecked;
            Log.e(TAG, " s5  = " + sa5);
        });

        s6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa6 = isChecked;
            Log.e(TAG, " s6  = " + sa6);
        });

        s7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa7 = isChecked;
            Log.e(TAG, " s7  = " + sa7);
        });


        //
        toggleCheck();
        // Button
        btn_input_consulta.setOnClickListener(v -> {
            String dni_personal = input_dni.getText().toString();
            consultarDniPersonal(dni_personal);
        });
        // Button Save
        btn_input_saveData.setOnClickListener(v -> {

            if (submitForm()) {
                savePersonalData();
            }

        });
        // Button Salir
        btn_input_back.setOnClickListener(v -> {
            Intent intent = new Intent(InputDataWorkerActivity.this, AllActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void toggleCheck() {

        input_test_yes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_test_no.setEnabled(false);
                testfastcovid = true;
            } else {
                input_test_no.setEnabled(true);
            }
        });

        input_test_no.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_test_yes.setEnabled(false);
                testfastcovid = false;
            } else {
                input_test_yes.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        notEnable();
    }

    private void notEnable() {
        input_temperatura_layout.setEnabled(false);
        input_saturacion_layout.setEnabled(false);
        input_pulso_layout.setEnabled(false);
        //  input_sintomas_layout.setEnabled(false);
        show_consulta_nombre_layout.setEnabled(false);
        show_consulta_last_layout.setEnabled(false);
        s1.setEnabled(false);
        s2.setEnabled(false);
        s3.setEnabled(false);
        s4.setEnabled(false);
        s5.setEnabled(false);
        s6.setEnabled(false);
        s7.setEnabled(false);
        input_test_yes.setEnabled(false);
        input_test_no.setEnabled(false);

        //
        input_new_sintomas_layout.setEnabled(false);
        input_examen_layout.setEnabled(false);


    }

    private void checkEnable() {
        input_temperatura_layout.setEnabled(true);
        input_saturacion_layout.setEnabled(true);
        input_pulso_layout.setEnabled(true);
        //    input_sintomas_layout.setEnabled(true);
        s1.setEnabled(true);
        s2.setEnabled(true);
        s3.setEnabled(true);
        s4.setEnabled(true);
        s5.setEnabled(true);
        s6.setEnabled(true);
        s7.setEnabled(true);
        input_test_yes.setEnabled(true);
        input_test_no.setEnabled(true);

        //
        //
        input_new_sintomas_layout.setEnabled(true);
        input_examen_layout.setEnabled(true);
    }


    private void savePersonalData() {

        Log.e(TAG, "save Personal Data");
        // fecha
        final String date_atention = getCurrentTimeStamp();
        //
        String tempurature = input_temperatura.getText().toString();
        String so2 = input_saturacion.getText().toString();
        String pulse = input_pulso.getText().toString();
        String symptoms = input_sintomas.getText().toString();
        String dateRegister = date_atention;

        //String tempurature, String so2, String pulse, String symptoms, String dateRegister
        MetricasPersonal metricasPersonal = new MetricasPersonal();
        metricasPersonal.setTempurature(tempurature);
        metricasPersonal.setSo2(so2);
        metricasPersonal.setPulse(pulse);
        metricasPersonal.setSymptoms(symptoms);
        metricasPersonal.setDateRegister(dateRegister);
        metricasPersonal.setWho_user_register(Common.currentUser.getUid()); // requerido
        metricasPersonal.setS1(sa1);
        metricasPersonal.setS2(sa2);
        metricasPersonal.setS3(sa3);
        metricasPersonal.setS4(sa4);
        metricasPersonal.setS5(sa5);
        metricasPersonal.setS6(sa6);
        metricasPersonal.setS7(sa7);


        metricasPersonal.setTestpruebarapida(testfastcovid);

        DatabaseReference ref_db_mina_personal_data = database.getReference(Common.db_unidad_trabajo_data).child(Common.currentUser.getUid());

        ref_db_mina_personal_data
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(personal.getDni())
                .child(date_atention)
                .setValue(metricasPersonal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "datos registrado");
                        Toast.makeText(InputDataWorkerActivity.this, "Datos registrados correctamente", Toast.LENGTH_SHORT).show();
                        gotoMAin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputDataWorkerActivity.this, "Error al ingresar los datos", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "datos no registrado");
                    }
                });

    }

    private void gotoMAin() {
        Intent intent = new Intent(InputDataWorkerActivity.this, AllActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void consultarDniPersonal(String dni_personal) {

        DatabaseReference ref_mina = database
                .getReference(Common.db_unidad_trabajo_personal)
                .child(Common.currentUser.getUid())
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(dni_personal);

        ref_mina.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //
                personal = dataSnapshot.getValue(Personal.class);
                //
                if (personal != null) {
                    //

                    show_consulta_fisrtname.setText(personal.getName().toUpperCase());
                    show_consulta_lastname.setText(personal.getLast().toUpperCase());
                    //
                    show_consulta_fisrtname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_cardview));
                    show_consulta_lastname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_cardview));
                    input_dni_layout.setError(null);
                    checkEnable();
                    //
                    Log.e(TAG, "nombre : " + personal.getName());
                    Log.e(TAG, "dni : " + personal.getDni());
                    Log.e(TAG, "dirección : " + personal.getAddress());
                    Log.e(TAG, "phone 1 : " + personal.getPhone1());
                } else {
                    show_consulta_fisrtname.setText("");
                    show_consulta_fisrtname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                    show_consulta_lastname.setText("");
                    input_dni_layout.setError("El trabajador no exsite en la base de datos");
                    notEnable();
                    //
                    Log.e(TAG, "el trabjador no existe en la base de datos  ");
                }

                Log.e(TAG, "key : " + dataSnapshot.getKey());
                Log.e(TAG, "children : " + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " [consultarDniPersonal] error = " + databaseError.getMessage());
            }
        });
        Log.e(TAG, "[consultarDniPersonal] path = " + ref_mina.toString());
    }

    //Validacion
    private boolean checkDNI() {
        if (input_dni.getText().toString().trim().isEmpty()) {
            input_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            input_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkTemperatura() {

        try {
            if (input_temperatura.getText().toString().trim().isEmpty() && input_temperatura.getText().toString() != null) {
                input_temperatura_layout.setError("*Campo obligatorio");
                input_temperatura_layout.requestFocus();
                Log.e("number", " int   " + Integer.parseInt(input_temperatura.getText().toString()));
                return false;
            } else {
                input_temperatura_layout.setError(null);
                int value = Integer.parseInt(input_temperatura.getText().toString());
                if (value < 35 || value > 43) {
                    input_temperatura_layout.setError("ERROR: rango no válido para  temperatura ");

                    input_temperatura_layout.requestFocus();
                    return false;
                }
                Log.e(TAG, " number int   " + Integer.parseInt(input_temperatura.getText().toString()));
            }


        } catch (Exception e) {
            e.getMessage();
        }

        return true;
    }

    private boolean checkSaturacion() {
        if (input_saturacion.getText().toString().trim().isEmpty() && input_saturacion.getText().toString().trim() != null) {
            input_saturacion_layout.setError("*Campo obligatorio");
            input_saturacion_layout.requestFocus();
            return false;
        } else {
            input_saturacion_layout.setError(null);
            //
            if (input_saturacion.getText().toString().trim() != null) {
                int value = Integer.parseInt(input_saturacion.getText().toString());
                if (value < 85 || value > 100) {
                    input_saturacion_layout.setError("ERROR: rango no válido para SO2");

                    input_saturacion_layout.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkPulso() {
        if (input_pulso.getText().toString().trim().isEmpty() && input_pulso.getText().toString() != null) {
            input_pulso_layout.setError("*Campo obligatorio");
            input_pulso_layout.requestFocus();
            return false;
        } else {
            input_pulso_layout.setError(null);
            //
            if (input_pulso.getText().toString().trim() != null) {
                int value = Integer.parseInt(input_pulso.getText().toString());
                if (value < 50 || value > 115) {
                    input_pulso_layout.setError("ERROR: rango no válido para  pulso");
                    input_pulso_layout.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkSintomas() {
        if (input_sintomas.getText().toString().trim().isEmpty()) {
            input_sintomas_layout.setError("falta ingresar los sistomas del paciente");
            return false;
        } else {
            input_sintomas_layout.setError(null);
        }
        return true;
    }

    //
    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }

        if (!checkTemperatura()) {
            return false;
        }

        if (!checkSaturacion()) {
            return false;
        }

        if (!checkPulso()) {
            return false;
        }


        /*
        if (!checkSintomas()) {
            return false;
        }
        */


        if ((input_test_no.isChecked() || !input_test_yes.isChecked()) && (!input_test_no.isChecked() || input_test_yes.isChecked())) {
            Toast.makeText(this, "falta  prueba rapida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //
    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //
}