package com.cudpast.apputdemo.Init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.User;
import com.cudpast.apputdemo.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnCreateUser, btnBackMain;
    private TextInputEditText reg_email, reg_password, reg_name, reg_dni, reg_phone;
    private TextInputLayout reg_email_layout, reg_password_layout, reg_name_layout, reg_dni_layout, reg_phone_layout;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //xml
        btnCreateUser = findViewById(R.id.btnCreateUser);

        reg_email = (TextInputEditText) findViewById(R.id.reg_email);
        reg_password = (TextInputEditText) findViewById(R.id.reg_password);
        reg_name = (TextInputEditText) findViewById(R.id.reg_name);
        reg_dni = (TextInputEditText) findViewById(R.id.reg_dni);
        reg_phone = (TextInputEditText) findViewById(R.id.reg_phone);

        reg_email_layout = findViewById(R.id.reg_email_layout);
        reg_password_layout = findViewById(R.id.reg_password_layout);
        reg_name_layout = findViewById(R.id.reg_name_layout);
        reg_dni_layout = findViewById(R.id.reg_dni_layout);
        reg_phone_layout = findViewById(R.id.reg_phone_layout);

        btnCreateUser.setOnClickListener(v -> createUser(v));

    }

    private void createUser(View v) {
        if (submitForm()) {
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.setMessage("Registrando nuevo usuario...");
            mDialog.show();
            String email = reg_email.getText().toString();
            String password = reg_password.getText().toString();
            // create user with firebase
            mAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        String email1 = reg_email.getText().toString();
                        String password1 = reg_password.getText().toString();
                        String name = reg_name.getText().toString();
                        String dni = reg_dni.getText().toString();
                        String phone = reg_phone.getText().toString();
                        Boolean status = false;// por default todos los usuarios son Free ,
                        int numUT = 0; // por default;
                        //Crear usuario
                        User user = new User(uid, email1, password1, name, dni, phone, status, numUT);
                        saveUserDB(user);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, " [createUserWithEmailAndPassword] Error :" + e.getMessage());
                        if (e.getMessage().equalsIgnoreCase("The email address is already in use by another account.")){
                            //Toast.makeText(this, "Este correo ya esta registrador ... intente con otros", Toast.LENGTH_SHORT).show();
                            Snackbar.make(v,"correo ya esta registrado !!!",Snackbar.LENGTH_SHORT).show();
                            reg_email.setText("");
                            reg_password.setText("");
                            reg_name.setText("");
                            reg_dni.setText("");
                            reg_phone.setText("");
                            reg_email.requestFocus();
                        }
                        mDialog.dismiss();
                    });
        }
    }

    private void saveUserDB(final User user) {
        DatabaseReference ref_db_user = database.getReference(Common.db_user);
        ref_db_user.child(user.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    backToLogin();
                    Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "[saveUserDB] : se registro con el uid : " + user.getUid());
                    mDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                 
                    Toast.makeText(RegisterActivity.this, "Error al registrar al usuario ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "[saveUserDB] :  no se registro");
                    mDialog.dismiss();
                });
    }

    private void backToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //Validacion
    private boolean checkEmail() {
        if (reg_email.getText().toString().trim().isEmpty()) {
            reg_email_layout.setError("Ingrese su correo");
            return false;
        } else {
            reg_email_layout.setError(null);
        }
        return true;
    }

    private boolean checkPassword() {
        if (reg_password.getText().toString().trim().isEmpty()) {
            reg_password_layout.setError("Ingrese su contraseña");
            return false;
        } else {
            reg_password_layout.setError(null);
        }
        return true;
    }

    private boolean checkName() {
        if (reg_name.getText().toString().trim().isEmpty()) {
            reg_name_layout.setError("Ingrese su nombre");
            return false;
        } else {
            reg_name_layout.setError(null);
        }
        return true;
    }

    private boolean checkDNI() {
        if (reg_dni.getText().toString().trim().isEmpty()) {
            reg_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            reg_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone() {
        if (reg_phone.getText().toString().trim().isEmpty()) {
            reg_phone_layout.setError("Ingrese su telefono");
            return false;
        } else {
            reg_phone_layout.setError(null);
        }
        return true;
    }

    private boolean submitForm() {
        if (!checkEmail()) {
            return false;
        }

        if (!checkPassword()) {
            return false;
        }

        if (!checkName()) {
            return false;
        }

        if (!checkDNI()) {
            return false;
        }

        if (!checkPhone()) {
            return false;
        }
        return true;
    }


    // Return Login
    public void mBackLoginBtn(View view) {
        Intent goToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }
}