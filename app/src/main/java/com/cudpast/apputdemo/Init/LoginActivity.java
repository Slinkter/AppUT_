package com.cudpast.apputdemo.Init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.User;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher {


    Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private TextInputLayout log_email_layout, log_password_layout;
    private TextInputEditText log_email, log_password;
    private ProgressDialog mDialog;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final String TAG = LoginActivity.class.getSimpleName();
    //Check
    private CheckBox checkbox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String PREF_NAME = "prefs";
    public static final String KEY_REMEMBER = "remeber";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        log_email_layout = findViewById(R.id.log_email_layout);
        log_password_layout = findViewById(R.id.log_password_layout);
        mAuth = FirebaseAuth.getInstance();
        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        // Btn - Login
        btnLogin.setOnClickListener(v -> initLogin());
        // Btn - Register
        btnRegister.setOnClickListener(v -> initRegistro());
        // CheckBox
        checkbox = (CheckBox) findViewById(R.id.checkbox_rem);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }
        // editText text changed
        log_email.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        log_password.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
        log_email.addTextChangedListener(this);
        log_password.addTextChangedListener(this);
        checkbox.setOnCheckedChangeListener(this);
    }


    //CheckBox
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChange();
    }


    private void checkChange() {
        if (checkbox.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_USERNAME, log_email.getText().toString().trim());
            editor.putString(KEY_PASSWORD, log_password.getText().toString().trim());
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
            editor.apply();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkChange();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.currentUser != null) {
            updateUI(Common.currentUser);
        }
    }

    // End CheckBox
    // Login
    private void initLogin() {
        if (submitForm()) {
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Un momento por favor ...");
            mDialog.show();
            //
            String email = log_email.getText().toString();
            String password = log_password.getText().toString();
            //
            mAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        //
                        String user_uid = authResult.getUser().getUid();
                        DatabaseReference ref_db_user = database.getReference(Common.db_user).child(user_uid);
                        ref_db_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Get data user from Firebase
                                User mUser = dataSnapshot.getValue(User.class);
                                if (mUser == null) {
                                    updateUI(null);
                                } else {
                                    updateUI(mUser);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                updateUI(null);
                                mDialog.dismiss();
                                Log.e(TAG, "databaseError : " + databaseError.getMessage());
                            }
                        });
                    })
                    .addOnFailureListener(e -> {

                        if (e.getMessage().equalsIgnoreCase("The email address is badly formatted")) {
                            Toast.makeText(LoginActivity.this, "Formato invalido", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else if (e.getMessage().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            Toast.makeText(LoginActivity.this, "usuario no registrador", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }


                    });
        }
    }

    // End Login
    private void initRegistro() {
        Intent internt_register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(internt_register);
        finish();
    }

    private void updateUI(User user) {
        if (user != null) {
            Common.currentUser = user;
            Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent_login);
            finish();
        } else {
            Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
        }
    }

    //Validacion
    private boolean checkEmail() {
        if (log_email.getText().toString().trim().isEmpty()) {
            log_email_layout.setError("Ingrese su correo");
            return false;
        } else {
            log_email_layout.setError(null);
        }
        return true;
    }

    private boolean checkPassword() {
        if (log_password.getText().toString().trim().isEmpty()) {
            log_password_layout.setError("Ingrese su contraseña");
            return false;
        } else {
            log_password_layout.setError(null);
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
        return true;
    }
    //End Validacion

    // Button Registro
    public void mRegistroBtn(View view) {
        Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intentRegister);
    }
}