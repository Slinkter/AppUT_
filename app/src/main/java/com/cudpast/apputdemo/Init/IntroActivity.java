package com.cudpast.apputdemo.Init;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.apputdemo.Adapter.IntroViewPagerAdapter;
import com.cudpast.apputdemo.R;
import com.cudpast.apputdemo.Support.ScreenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private IntroViewPagerAdapter adapter;
    private TabLayout tabIndicator;

    private ImageView btn_img_next_arrow;
    private Button mStartedBtn;

    private int position;

    private Animation mAnimationBtn;
    private List<ScreenItem> mList;

    private TextView tv_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //full-screen
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);
        //
        tv_skip = findViewById(R.id.tv_skip);
        viewPager = findViewById(R.id.viewPager);
        tabIndicator = findViewById(R.id.tab_indicator);
        btn_img_next_arrow = findViewById(R.id.btn_img_next_arrow);
        mStartedBtn = findViewById(R.id.btn_get_started);
        mAnimationBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        //
        InitData();
        //
        adapter = new IntroViewPagerAdapter(this, mList);
        viewPager.setAdapter(adapter);
        tabIndicator.setupWithViewPager(viewPager);
//
        btn_img_next_arrow.setOnClickListener(v -> {
            position = viewPager.getCurrentItem();
            if (mList.size() - 1 == position) {
                loadLastScreen();
            }
            if (mList.size() - 1 > position) {
                position++;
                viewPager.setCurrentItem(position);
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mList.size() - 1 == tab.getPosition()) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tv_skip.setOnClickListener(v -> goIntent());


    }

    private void goIntent() {
        Intent goMain = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goMain);
        finish();
    }

    private void InitData() {
        mList = new ArrayList<>();
        mList.add(new ScreenItem("ARSI UT", "Aplicativo para el registro de síntomas de personal", R.drawable.ic_logo_app));
        mList.add(new ScreenItem("Síntomas", "Register los síntomas de manera fácil y rápida de sus trabajadores", R.drawable.ic_intro_register));
        mList.add(new ScreenItem("Personal", "Register a sus personal con sus datos personales ", R.drawable.ic_intro_employee));
        mList.add(new ScreenItem("Reportes", "Genere reportes diarios por fecha , trabajador y/o prueba rápida", R.drawable.ic_intro_report));
        mList.add(new ScreenItem("Correo", "Exporte sus reportes vía email para tener un registro documentado fácil de visualizar", R.drawable.ic_gmail));
        mList.add(new ScreenItem("Datos", "Visualice sus información de su personal en tiempo Real", R.drawable.ic_intro_data));

    }

    private void loadLastScreen() {
        btn_img_next_arrow.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        mStartedBtn.setVisibility(View.VISIBLE);
        mStartedBtn.setAnimation(mAnimationBtn);
        mStartedBtn.setOnClickListener(v -> goIntent());
    }
}