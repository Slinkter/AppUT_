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

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWorkerActivity extends AppCompatActivity {

    public static final String TAG = AddWorkerActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_worker);

        // todo cuando registra al nuevo personal se repite el dni .

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
        btn_personal_create_user = findViewById(R.id.btn_personal_create_user);
        btn_personal_back_main = findViewById(R.id.btn_personal_back_main);
        //
        btn_personal_create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWorkerActivity.this.createNewPersonal();
            }
        });
        btn_personal_back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWorkerActivity.this.gotoMAin();
            }
        });
    }


    private void createNewPersonal() {
        checkWorker(personal_name.getText().toString());
        /*
        if (submitForm()) {

            mDialog = new ProgressDialog(AddWorkerActivity.this);
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
                        Toast.makeText(AddWorkerActivity.this, "El trabajador ha sido registrado ", Toast.LENGTH_SHORT).show();
                        gotoMAin();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddWorkerActivity.this, "Trabajador no ha sido Registrado", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "[createNewPersonal()] error : " + e.getMessage());
                        mDialog.dismiss();
                    });
        }

        */
    }

    private void gotoMAin() {
        Intent intent = new Intent(AddWorkerActivity.this, AllActivity.class);
        startActivity(intent);
        finish();
    }

    // checko DNI en base de datos si existe

    public void checkWorker(String dni) {
        // todo cuando registra al nuevo personal se repite el dni .
        Log.e(TAG, " dni : " + dni);


        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(Common.db_unidad_trabajo_personal)
                .child(Common.currentUser.getUid())
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(dni);
        Log.e(TAG, "url" + postRef);

        postRef.orderByChild("dni").equalTo(dni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "url" + postRef.orderByChild("dni"));
                Log.e(TAG, "1" + dataSnapshot.getKey());
                Log.e(TAG, "2" + dataSnapshot.getChildren());
                Log.e(TAG, "3" + dataSnapshot.getValue());

                if (dataSnapshot.exists()) {
                    Log.e(TAG, "existe el dni : " + dataSnapshot);
                } else {
                    Log.e(TAG, "no existe el dni : " + dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " errror : " + databaseError.getMessage());
            }
        });


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

        if (!checkDNI()) {
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