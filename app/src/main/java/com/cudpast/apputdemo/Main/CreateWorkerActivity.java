package com.cudpast.apputdemo.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.Personal;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateWorkerActivity extends AppCompatActivity {
    // todo : limitar a registrar 10 trabajdo en version free

    public static final String TAG = CreateWorkerActivity.class.getSimpleName();

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    Personal personal;

    private TextInputLayout
            personal_dni_layout,
            personal_name_layout,
            personal_last_layout,
            personal_age_layout,
            personal_address_layout,
            personal_born_layout,
            personal_date_layout,
            personal_phone1_layout,
            personal_phone2_layout;

    private TextInputEditText
            personal_dni,
            personal_name,
            personal_last,
            personal_age,
            personal_address,
            personal_born,
            personal_date,
            personal_phone1,
            personal_phone2;

    private Button btn_personal_create_user, btn_personal_back_main;
    private ProgressDialog mDialog;

    LinearLayout linearLayoutRegistre, linearLayoutRegistreShowMjs;

    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_worker);
        //
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //
        personal_dni = findViewById(R.id.personal_dni);
        personal_name = findViewById(R.id.personal_name);
        personal_last = findViewById(R.id.personal_last);
        personal_age = findViewById(R.id.personal_age);
        personal_address = findViewById(R.id.personal_address);
        personal_born = findViewById(R.id.personal_born);
        personal_date = findViewById(R.id.personal_date);
        personal_phone1 = findViewById(R.id.personal_phone1);
        personal_phone2 = findViewById(R.id.personal_phone2);
        //
        personal_dni_layout = findViewById(R.id.personal_dni_layout);
        personal_name_layout = findViewById(R.id.personal_name_layout);
        personal_last_layout = findViewById(R.id.personal_last_layout);
        personal_age_layout = findViewById(R.id.personal_age_layout);
        personal_address_layout = findViewById(R.id.personal_address_layout);
        personal_born_layout = findViewById(R.id.personal_born_layout);
        personal_date_layout = findViewById(R.id.personal_date_layout);
        personal_phone1_layout = findViewById(R.id.personal_phone1_layout);
        personal_phone2_layout = findViewById(R.id.personal_phone2_layout);
        //
        linearLayoutRegistre = findViewById(R.id.linearLayoutRegistre);
        linearLayoutRegistreShowMjs = findViewById(R.id.linearLayoutRegistreShowMjs);
        //
        btn_personal_create_user = findViewById(R.id.btn_personal_create_user);
        btn_personal_back_main = findViewById(R.id.btn_personal_back_main);
        //
        btn_personal_create_user.setOnClickListener(v -> CreateWorkerActivity.this.createNewPersonal());
        btn_personal_back_main.setOnClickListener(v -> CreateWorkerActivity.this.gotoMAin());


        try {
            checkUserStatus();
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }


    }


    private void checkUserStatus() {

        Boolean status = Common.currentUser.getStatus();

        if (status) {
            // Version Premiun

        } else {
            //Version free
            // db_unidad_trabajo_data > usuario uid [Vad7hyj0fgQ0jG97fUaf5ZPQNH83] >  UT [-MBCCbyJQJV3a-vxq7RK]   String firebaseUserUid = mAuth.getCurrentUser().getUid(); DatabaseReference ref =   database.getReference(Common.db_unidad_trabajo_data).child(Common.currentUser.getUid()).child(Common.unidadTrabajoSelected.getAliasUT());
            OpenAll();

            String firebaseUserUid = mAuth.getCurrentUser().getUid();

            database.getReference()
                    .child(Common.db_unidad_trabajo_personal)
                    .child(firebaseUserUid)
                    .child(Common.unidadTrabajoSelected.getIdUT()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numLimite = 10;
                    int numTrabajdores = (int) dataSnapshot.getChildrenCount();
                    Log.e(TAG, " NUM DE TRABAJDORES " + numTrabajdores);


                    if (numLimite <= numTrabajdores) {
                        blockAll();
                        //activar blockall() si es veradero
                        //10 <= 11 ---> verdadero
                        //10 <= 5 --> Falso
                    } else {
                        OpenAll();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private void blockAll() {
        linearLayoutRegistre.setVisibility(View.GONE);
        btn_personal_create_user.setVisibility(View.GONE);
        linearLayoutRegistreShowMjs.setVisibility(View.VISIBLE);

    }

    private void OpenAll() {

        linearLayoutRegistre.setVisibility(View.VISIBLE);
        btn_personal_create_user.setVisibility(View.VISIBLE);
        linearLayoutRegistreShowMjs.setVisibility(View.GONE);
    }


    private void createNewPersonal() {


        if (submitForm()) {

            try {
                mDialog = new ProgressDialog(CreateWorkerActivity.this);
                mDialog.setMessage(" Registrando trabajador ...");
                mDialog.show();

                final String dni, name, last, age, address, born, date, phone1, phone2;
                dni = personal_dni.getText().toString();
                name = personal_name.getText().toString();
                last = personal_last.getText().toString();
                age = personal_age.getText().toString();
                address = personal_address.getText().toString();
                born = personal_born.getText().toString();
                date = personal_date.getText().toString();
                phone1 = personal_phone1.getText().toString();
                phone2 = personal_phone2.getText().toString();

                Personal worker = new Personal(dni, name, last, age, address, born, date, phone1, phone2);
                String firebaseUserUid = mAuth.getCurrentUser().getUid();

                database.getReference()
                        .child(Common.db_unidad_trabajo_personal)
                        .child(firebaseUserUid)
                        .child(Common.unidadTrabajoSelected.getIdUT())
                        .child(worker.getDni())
                        .setValue(worker)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreateWorkerActivity.this, "El trabajador ha sido registrado ", Toast.LENGTH_SHORT).show();
                            gotoMAin();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreateWorkerActivity.this, "Trabajador no ha sido Registrado", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "[createNewPersonal()] error : " + e.getMessage());
                            mDialog.dismiss();
                        });

            } catch (Exception e) {
                Log.e(TAG, "erro try-cath " + e.getMessage());
            }

        }


    }

    private void gotoMAin() {
        Intent intent = new Intent(CreateWorkerActivity.this, AllActivity.class);
        startActivity(intent);
        finish();
    }

    // checko DNI en base de datos si existe

    public void checkWorker(String dni) {

        check = false;

        DatabaseReference ref_1 = database
                .getReference(Common.db_unidad_trabajo_personal)
                .child(Common.currentUser.getUid())
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(dni);

        ref_1
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personal = dataSnapshot.getValue(Personal.class);

                        if (personal != null) {
                            //    InputMethodManager imn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            //    imn.showSoftInput(personal_dni, InputMethodManager.SHOW_IMPLICIT);
                            Log.e(TAG, "nombre : " + personal.getName());
                            Log.e(TAG, "dni : " + personal.getDni());
                            personal_dni_layout.setError("EL DNI  ya se encuentra registrado");
                            personal_dni_layout.setFocusable(true);
                            personal_dni_layout.requestFocus();
                            check = true;
                        } else {
                            Log.e(TAG, "el trabjador no existe en  ");
                            //   personal_dni_layout.setError("EL DNI disponible");
                            check = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                        check = false;

                    }
                });

    }

    private boolean checkExistDNI() {
        Log.e(TAG, "check 1 " + check);
        if (!check) {
            return true;
        } else {
            personal_dni_layout.setError(null);
        }

        return false;
    }

    //Validacion
    private boolean checkDNI() {
        if (personal_dni.getText().toString().trim().isEmpty()) {
            personal_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            personal_dni_layout.setError(null);
        }
        return true;
    }


    private boolean checkName() {
        if (personal_name.getText().toString().trim().isEmpty()) {
            personal_name_layout.setError("Ingrese su nombre");
            return false;
        } else {
            personal_name_layout.setError(null);
        }
        return true;
    }

    private boolean checkAge() {
        if (personal_age.getText().toString().trim().isEmpty()) {
            personal_age_layout.setError("Ingrese su edad");
            return false;
        } else {
            personal_age_layout.setError(null);
        }
        return true;
    }

    private boolean checkAddress() {
        if (personal_address.getText().toString().trim().isEmpty()) {
            personal_address_layout.setError("Ingrese su dirección");
            return false;
        } else {
            personal_address_layout.setError(null);
        }
        return true;
    }

    private boolean checkBorn() {
        if (personal_born.getText().toString().trim().isEmpty()) {
            personal_born_layout.setError("Ingrese su lugar de nacimiento");
            return false;
        } else {
            personal_born_layout.setError(null);
        }
        return true;
    }

    private boolean checkDate() {
        if (personal_date.getText().toString().trim().isEmpty()) {
            personal_date_layout.setError("Ingrese su fecha de nacimiento");
            return false;
        } else {
            personal_date_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone1() {
        if (personal_phone1.getText().toString().trim().isEmpty()) {
            personal_phone1_layout.setError("Ingrese su telefono principal");
            return false;
        } else {
            personal_phone1_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone2() {
        if (personal_phone2.getText().toString().trim().isEmpty()) {
            personal_phone2_layout.setError("Ingrese su telefono secundario");
            return false;
        } else {
            personal_phone2_layout.setError(null);
        }
        return true;
    }

    private boolean submitForm() {

        checkWorker(personal_dni.getText().toString().trim());


        if (!checkDNI()) {
            return false;
        }

        if (!checkExistDNI()) {
            return false;
        }

        if (!checkName()) {
            return false;
        }

        if (!checkAge()) {
            return false;
        }

        if (!checkAddress()) {
            return false;
        }

        if (!checkBorn()) {
            return false;
        }

        if (!checkDate()) {
            return false;
        }

        if (!checkPhone1()) {
            return false;
        }

        /*
        if (!checkPhone2()) {
            return false;
        }

         */

        return true;
    }
}