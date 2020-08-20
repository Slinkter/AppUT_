package com.cudpast.apputdemo.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.Personal;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditWorkerActivity extends AppCompatActivity {

    public static final String TAG = EditWorkerActivity.class.getSimpleName();


    private TextInputLayout query_personal_dni_layout;
    private TextInputEditText query_personal_dni;
    private TextView show_delete_personal;


    TextInputLayout update_personal_name_layout, update_personal_last_layout, update_personal_address_layout, update_personal_phone1_layout;
    TextInputEditText update_personal_name, update_personal_last, update_personal_address, update_personal_phone1;


    private Button btn_update_personal, btn_exit_back, btn_query_dni_personal;
    private FirebaseDatabase database;
    private Personal personal;
    private LinearLayout layout_btn_update, update_layout_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_worker);

        database = FirebaseDatabase.getInstance();

        query_personal_dni_layout = findViewById(R.id.query_personal_dni_layout);
        query_personal_dni = findViewById(R.id.query_personal_dni);
        btn_query_dni_personal = findViewById(R.id.btn_query_dni_personal);

        update_personal_name_layout = findViewById(R.id.update_personal_name_layout);
        update_personal_last_layout = findViewById(R.id.update_personal_last_layout);
        update_personal_address_layout = findViewById(R.id.update_personal_address_layout);
        update_personal_phone1_layout = findViewById(R.id.update_personal_phone1_layout);


        update_personal_name = findViewById(R.id.update_personal_name);
        update_personal_last = findViewById(R.id.update_personal_last);
        update_personal_address = findViewById(R.id.update_personal_address);
        update_personal_phone1 = findViewById(R.id.update_personal_phone1);


        layout_btn_update = findViewById(R.id.layout_btn_update);

        update_layout_show = findViewById(R.id.update_layout_show);
        btn_update_personal = findViewById(R.id.btn_update_personal);
        btn_exit_back = findViewById(R.id.btn_exit_back);

        show_delete_personal = findViewById(R.id.show_delete_personal);


        // Realiza la consulta del dni si exsite el trabajador
        btn_query_dni_personal.setOnClickListener(v -> {
            if (submitForm()) {
                String dni = query_personal_dni.getText().toString();
                consultarDniPersonal(dni);
            }
        });

        //
        btn_exit_back.setOnClickListener(v -> {
            Intent intent = new Intent(EditWorkerActivity.this, AllActivity.class);
            startActivity(intent);
            finish();
        });


        disable();

    }

    private void able() {
        update_layout_show.setEnabled(true);
        update_personal_name_layout.setEnabled(true);
        update_personal_last_layout.setEnabled(true);
        update_personal_address_layout.setEnabled(true);
        update_personal_phone1_layout.setEnabled(true);
    }

    private void disable() {
        update_layout_show.setEnabled(false);
        update_personal_name_layout.setEnabled(false);
        update_personal_last_layout.setEnabled(false);
        update_personal_address_layout.setEnabled(false);
        update_personal_phone1_layout.setEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    private void consultarDniPersonal(String dni_personal) {


        DatabaseReference ref_db_mina_personal = database
                .getReference(Common.db_unidad_trabajo_personal)
                .child(Common.currentUser.getUid())
                .child(Common.unidadTrabajoSelected.getIdUT())
                .child(dni_personal);




        ref_db_mina_personal
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {

                            able();
                            Log.e(TAG, "nombre : " + personal.getName());
                            Log.e(TAG, "dni : " + personal.getDni());
                            Log.e(TAG, "dirección : " + personal.getAddress());
                            Log.e(TAG, "phone 1 : " + personal.getPhone1());
                            show_delete_personal.setText(personal.getName() + " " + personal.getLast());
                            show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                            query_personal_dni_layout.setError(null);
                            layout_btn_update.setVisibility(View.VISIBLE);
                            update_layout_show.setEnabled(true);
                            //
                            update_personal_name.setText(personal.getName());
                            update_personal_last.setText(personal.getLast());
                            update_personal_address.setText(personal.getAddress());
                            update_personal_phone1.setText(personal.getPhone1());
                            //

                            // Realiza la actualizacion del trabajador
                            btn_update_personal.setOnClickListener(v -> {

                                String dni, name, last, age, address, born, date, phone1, phone2;

                                dni = personal.getDni();
                                name = update_personal_name.getText().toString();
                                last = update_personal_last.getText().toString();
                                age = personal.getAge();
                                address = update_personal_address.getText().toString();
                                born = personal.getBorn();
                                date = personal.getDate();
                                phone1 = update_personal_phone1.getText().toString();
                                phone2 = personal.getPhone2();

                                Personal updatepersonal = new Personal();

                                updatepersonal.setDni(dni);
                                updatepersonal.setName(name);
                                updatepersonal.setLast(last);
                                updatepersonal.setAge(age);
                                updatepersonal.setAddress(address);
                                updatepersonal.setBorn(born);
                                updatepersonal.setDate(date);
                                updatepersonal.setPhone1(phone1);
                                updatepersonal.setPhone2(phone2);

                                updateWorker(updatepersonal);

                            });

                            // Salir
                            btn_exit_back.setOnClickListener(v -> {
                                Intent intent = new Intent(EditWorkerActivity.this, AllActivity.class);
                                startActivity(intent);
                                finish();
                            });


                        } else {
                            disable();
                            Log.e(TAG, "el trabjador no existe en  ");
                            show_delete_personal.setText("");
                            show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                            query_personal_dni_layout.setError("El trabajador no exsite en la base de datos");
                            query_personal_dni_layout.requestFocus();
                            layout_btn_update.setVisibility(View.INVISIBLE);
                            update_layout_show.setEnabled(false);
                            //
                            update_personal_name.setText(" ");
                            update_personal_last.setText(" ");
                            update_personal_address.setText(" ");
                            update_personal_phone1.setText(" ");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });

    }


    private boolean checkDNI() {
        if (query_personal_dni.getText().toString().trim().isEmpty()) {
            query_personal_dni_layout.setError("Ingrese su DNI");
            query_personal_dni_layout.requestFocus();
            return false;
        } else {
            query_personal_dni_layout.setError(null);
        }
        return true;
    }


    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }

        return true;
    }


    public void updateWorker(Personal updatepersonal) {


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_delete_personal, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        ProgressDialog mDialog = new ProgressDialog(view.getContext());
        mDialog.setMessage(" Actualizando datos ... ");
        mDialog.show();
        //
        Button btn_update_confirm_yes = view.findViewById(R.id.btn_update_confirm_yes);
        Button btn_update_confirm_no = view.findViewById(R.id.btn_update_confirm_no);

        btn_update_confirm_yes.setOnClickListener(v -> {

            DatabaseReference ref_db_mina_personal = database.getReference(Common.db_unidad_trabajo_personal)
                    .child(Common.currentUser.getUid())
                    .child(Common.unidadTrabajoSelected.getIdUT())
                    .child(updatepersonal.getDni());


            ref_db_mina_personal.setValue(updatepersonal);

            ref_db_mina_personal.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dialog.dismiss();
                    mDialog.dismiss();
                    Log.e(TAG, "EXITO");
                    Log.e(TAG, "getKey = " + dataSnapshot.getKey());
                    Log.e(TAG, "getValue = " + dataSnapshot.getValue());

                    Toast.makeText(view.getContext(), " Se actualizado  ", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    dialog.dismiss();
                    mDialog.dismiss();
                    Toast.makeText(view.getContext(), " error al actualizar  ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "FAIL = " + databaseError.getMessage());
                }
            });


        });


        btn_update_confirm_no.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }


    /// --> old
    /*
    public void showDiaglo(final String dni) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_delete_personal, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        dialog.show();
        //
        Button btn_delete_no = view.findViewById(R.id.btn_delete_no);
        btn_delete_no.setOnClickListener(view1 -> dialog.dismiss());

        Button btn_delete_yes = view.findViewById(R.id.btn_delete_yes);
        btn_delete_yes.setOnClickListener(viewDelete -> {
            DatabaseReference ref_1 = database.getReference(Common.db_unidad_trabajo_personal).child(Common.currentUser.getUid()).child(Common.unidadTrabajoSelected.getIdUT()).child(dni);
            DatabaseReference ref_2 = database.getReference(Common.db_unidad_trabajo_data).child(Common.currentUser.getUid()).child(Common.unidadTrabajoSelected.getIdUT()).child(dni);

            Log.e(TAG, "db_unidad_trabajo_personal = " + ref_1);
            Log.e(TAG, "db_unidad_trabajo_data = " + ref_2);
            ref_1.removeValue()
                    .addOnSuccessListener(aVoid ->
                            ref_2.removeValue()
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(EditWorkerActivity.this, "Trabajador Eliminado ", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditWorkerActivity.this, "Trabajador no  Eliminado", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditWorkerActivity.this, "Trabjador no  Eliminado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });


        });


    }
    */


}