package com.cudpast.apputdemo.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Init.LoginActivity;
import com.cudpast.apputdemo.R;

public class AllActivity extends AppCompatActivity {


    private TextView tv_selectedunidadminera, tv_currentuser;
    private Animation mAnimationBtn;
    private LinearLayout allmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);

        tv_selectedunidadminera = findViewById(R.id.tv_selectedunidadminera);
        tv_selectedunidadminera.setText(Common.unidadTrabajoSelected.getNameUT());
        mAnimationBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        allmain = findViewById(R.id.allmain);
        //   allmain.setAnimation(mAnimationBtn);


        //
        if (Common.currentUser != null) {
            tv_currentuser = findViewById(R.id.tv_currentuser);
            tv_currentuser.setText(Common.currentUser.getName());
        } else {
            Intent intent = new Intent(AllActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void btnAddWorker(View view) {
        Intent intent = new Intent(AllActivity.this, AddWorkerActivity.class);
        startActivity(intent);

    }

    public void btnDeleteWorker(View view) {
        Intent intent = new Intent(AllActivity.this, DeleteWorkerActivity.class);
        startActivity(intent);

    }

    public void btnInputData(View view) {
        Intent intent = new Intent(AllActivity.this, InputDataWorkerActivity.class);
        startActivity(intent);

    }

    public void btnReportData(View view) {
        Intent intent = new Intent(AllActivity.this, ReportDataWorkerActivity.class);
        startActivity(intent);

    }


    // metodos de baja
    public void btnQueryMinero(View view) {
        Toast.makeText(this, "Solo admin", Toast.LENGTH_SHORT).show();
        //   Intent intent = new Intent(AllActivity.this, ConsultaPersonalActivity.class);
        //   startActivity(intent);
    }

    public void btnVisualData(View view) {
        Toast.makeText(this, "Solo admin", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, ViewChartActivity.class);
        startActivity(intent);
    }
}