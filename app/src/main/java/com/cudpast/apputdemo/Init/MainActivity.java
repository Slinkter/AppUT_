package com.cudpast.apputdemo.Init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Main.AllActivity;
import com.cudpast.apputdemo.Model.UnidadTrabajo;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //todo : cuando se cambiar de version premiun a version free liminitar a 10 unidades

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btnContinuar;
    private Spinner spinner_unidadMinera;
    private ArrayList<String> listaUnidadMinera;
    private ArrayList<UnidadTrabajo> listaUT;
    TextView user_current, user_status;
    ImageView addUT;
    private int numUT;
    Boolean checkUT;

    private Animation mAnimationBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mAnimationBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        user_current = findViewById(R.id.user_current);
        user_status = findViewById(R.id.user_status);
        addUT = findViewById(R.id.addUT);


        listaUnidadMinera = new ArrayList<>();
        listaUT = new ArrayList<>();
        user_current.setText(Common.currentUser.getName());

        getDataFromUser();
        checkUserStatus();

        listaUnidadMinera.add("Buscar Unidad");
        for (int i = 0; i < listaUT.size(); i++) {
            listaUnidadMinera.add(listaUT.get(i).getNameUT().toString());
        }
        spinner_unidadMinera = (Spinner) findViewById(R.id.spinner_ut);
        ArrayAdapter<CharSequence> adapter_spinner_um = new ArrayAdapter(this, R.layout.spinner_adapter_ut, listaUnidadMinera);
        spinner_unidadMinera.setAdapter(adapter_spinner_um);
        spinner_unidadMinera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (listaUnidadMinera.get(position).equalsIgnoreCase("Seleccione Unidad")) {
                        btnContinuar.setVisibility(View.INVISIBLE);
                    } else {
                        Log.e(TAG, " position " + (position - 1));
                        Log.e(TAG, " getNameUT" + listaUT.get(position - 1).getNameUT());

                        Common.unidadTrabajoSelected = listaUT.get(position - 1);
                        Log.e(TAG, "Common.unidadTrabajoSelected.getNameUT() " + Common.unidadTrabajoSelected.getNameUT());
                        btnContinuar.setVisibility(View.VISIBLE);
                        btnContinuar.setAnimation(mAnimationBtn);
                    }
                } else {
                    btnContinuar.setVisibility(View.INVISIBLE);
                    btnContinuar.setAnimation(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btnContinuar.setAnimation(null);
            }

        });

        btnContinuar = findViewById(R.id.btnContinuar);
        btnContinuar.setVisibility(View.INVISIBLE);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent all = new Intent(MainActivity.this, AllActivity.class);
                startActivity(all);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {

        Boolean status = Common.currentUser.getStatus();

        if (status) {

            user_status.setText("Premiun");
            final int numUT = 20;
            final int userNumUT = Common.currentUser.getNumUT();

            if (numUT < userNumUT) {
                Toast.makeText(this, "Usted llego  al límite para crear  UT ", Toast.LENGTH_SHORT).show();
                addUT.setEnabled(false);
                addUT.setVisibility(View.INVISIBLE);
                Log.e(TAG, numUT + " < " + userNumUT);
            } else {
                int diff = numUT - userNumUT;
                Toast.makeText(this, "te faltan " + diff + " UT para el límite", Toast.LENGTH_SHORT).show();
            }

            addUT.setOnClickListener(v -> showPopUp_addUT());
        } else {
            //Version free
            user_status.setText("Free");
            final int numUT = 3;
            final int userNumUT = Common.currentUser.getNumUT();
            Log.e(TAG, " ut count = " + numUT);
            Log.e(TAG, "user ut count = " + userNumUT);
            addUT.setOnClickListener(v -> {
                if (userNumUT < numUT) {
                    showPopUp_addUT();
                    Toast.makeText(MainActivity.this, "tiene menos 3 unidades", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, " compre la versión completa  ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void getDataFromUser() {
        Log.e(TAG, "getDataFromUser()");
        DatabaseReference ref_db_ut = FirebaseDatabase.getInstance()
                .getReference(Common.db_unidad_trabajo)
                .child(Common.currentUser.getUid());

        ref_db_ut.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "dataSnapshot_item = " + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UnidadTrabajo element = snapshot.getValue(UnidadTrabajo.class);
                    listaUT.add(element);
                    listaUnidadMinera.add(element.getNameUT().toString());
                    //  Log.e(TAG, "ui  = " + element.getIdUT());
                    //  Log.e(TAG, "name = " + element.getNameUT());
                    //  Log.e(TAG, "user_uid = " + element.getUser_ui());
                }

                numUT = (int) dataSnapshot.getChildrenCount();
                DatabaseReference ref_db_user = FirebaseDatabase.getInstance()
                        .getReference(Common.db_user);

                ref_db_user
                        .child(Common.currentUser.getUid())
                        .child("numUT")
                        .setValue(numUT);

                Log.e(TAG, " numUT = " + numUT);
                Log.e(TAG, " listaUnidadMinera.size() = " + listaUnidadMinera.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error = " + databaseError.getMessage());
            }
        });
    }

    //
    public void showPopUp_addUT() {
        //
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_add_ut, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        final TextInputLayout ti_ut_layout = view.findViewById(R.id.ti_ut_layout);
        final TextInputEditText ti_ut = view.findViewById(R.id.ti_ut);

        Button mUTCreateBtn = view.findViewById(R.id.mUTCreateBtn);
        Button mUTCloseBtn = view.findViewById(R.id.mUTCloseBtn);

        mUTCreateBtn.setOnClickListener(v -> {


            if (ti_ut.getText().toString().trim().isEmpty()) {
                ti_ut_layout.setError("Ingresar nuevo UT");
            } else {
                ti_ut_layout.setError(null);
                String nameUT = ti_ut.getText().toString().trim();
                checkUTFirebase(nameUT, v, ti_ut_layout);
            }
        });

        mUTCloseBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void checkUTFirebase(String nameUT, View v, TextInputLayout textInputLayout) {

        checkUT = false;

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(Common.db_unidad_trabajo)
                .child(Common.currentUser.getUid());

        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Obtener todas la unidades de trabajo
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            // setteando
                            UnidadTrabajo unidadTrabajo = d.getValue(UnidadTrabajo.class);
                            String nameUTFirebase = unidadTrabajo.getNameUT();
                            //Preguntars si existe
                            if (nameUT.equals(nameUTFirebase)) {
                                Log.e(TAG, " ya existe en nombre de UT");
                                textInputLayout.setError(" Ya existe la unidad de trabajo");
                                checkUT = true;
                                break;
                            }
                        }

                        if (!checkUT) {
                            createUT(nameUT, v);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error = " + databaseError.getMessage());
                    }
                });


    }


    public void createUT(String nameUT, final View view) {

        final ProgressDialog mDialogUT;
        mDialogUT = new ProgressDialog(view.getContext());
        mDialogUT.setMessage("Obteniendo datos ...");
        mDialogUT.show();
        //db_unidad_trabajo - dni - unidad de trabajo
        DatabaseReference ref_db_ut = FirebaseDatabase.getInstance()
                .getReference(Common.db_unidad_trabajo)
                .child(Common.currentUser.getUid());
        //
        ref_db_ut
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Obtener lista de UT
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UnidadTrabajo element = snapshot.getValue(UnidadTrabajo.class);
                            listaUT.add(element);
                        }
                        // Cantidad de UT
                        numUT = (int) dataSnapshot.getChildrenCount();
                        Log.e("integer data.count()", "" + numUT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("error", databaseError.getMessage());
                    }
                });
        // create UT
        String idUt = ref_db_ut.push().getKey();
        final UnidadTrabajo new_ut = new UnidadTrabajo();
        new_ut.setIdUT(idUt);
        new_ut.setNameUT(nameUT);
        new_ut.setUser_ui(Common.currentUser.getUid());
        Log.e("idUT ", idUt);
        //
        ref_db_ut
                .child(idUt)
                .setValue(new_ut)
                .addOnSuccessListener(aVoid -> {
                    mDialogUT.dismiss();
                    Toast.makeText(view.getContext(), "Se creo unidad trabajo", Toast.LENGTH_SHORT).show();
                    DatabaseReference ref_db_user = FirebaseDatabase.getInstance()
                            .getReference(Common.db_user);

                    ref_db_user
                            .child(Common.currentUser.getUid())
                            .child("numUT")
                            .setValue(numUT);
                })
                .addOnFailureListener(e -> {
                    mDialogUT.dismiss();
                    Toast.makeText(view.getContext(), "no se creo la unidad de trabajo", Toast.LENGTH_SHORT).show();
                });
    }

}