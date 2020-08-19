package com.cudpast.apputdemo.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cudpast.apputdemo.Common.Common;
import com.cudpast.apputdemo.Model.MetricasPersonal;
import com.cudpast.apputdemo.Model.Personal;
import com.cudpast.apputdemo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ViewChartActivity extends AppCompatActivity {



    public static final String TAG = ViewChartActivity.class.getSimpleName();

    private TextInputLayout visual_dni_layout;
    private TextInputEditText visual_dni;
    private Button btn_visual_dni;

    private FirebaseDatabase database;
    private Personal personal;
    private TextView show_name_visual_dni, meanTempe, meanOxig, meanPulse;
    private LinearLayout visual_linerlayout;

    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listtemp;


    private List<String> listDate;
    private List<String> listTemperatura;
    private List<Integer> listSaturacion;
    private List<Integer> listPulso;

    LineChartView lineChartViewTemperatura;
    LineChartView lineChartViewOxigeno;
    LineChartView lineChartViewPulse;


    Button btnGenerarChart, btn_returnAllChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_view_chart);
        //
        database = FirebaseDatabase.getInstance();
        //
        show_name_visual_dni = findViewById(R.id.show_name_visual_dni);
        visual_dni_layout = findViewById(R.id.visual_dni_layout);
        visual_dni = findViewById(R.id.visual_dni);
        btn_visual_dni = findViewById(R.id.btn_visual_dni);
        //
        meanTempe = findViewById(R.id.meanTempe);
        meanOxig = findViewById(R.id.meanOxig);
        meanPulse = findViewById(R.id.meanPulse);

        lineChartViewTemperatura = findViewById(R.id.chart1);
        lineChartViewOxigeno = findViewById(R.id.chart2);
        lineChartViewPulse = findViewById(R.id.chart3);

        btn_visual_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validar
                String dni_personal = visual_dni.getText().toString();
                consultarDniPersonal(dni_personal);

            }
        });

        btnGenerarChart = findViewById(R.id.btnGenerarChart);


        btn_returnAllChart = findViewById(R.id.btn_returnAllChart);
        btn_returnAllChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewChartActivity.this, AllActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

                personal = dataSnapshot.getValue(Personal.class);

                if (personal != null) {

                    visual_dni_layout.setError(null);
                    show_name_visual_dni.setText(personal.getName() + " " + personal.getLast());

                    loadChart(dni_personal);

                    Log.e(TAG, "nombre : " + personal.getName());
                    Log.e(TAG, "dni : " + personal.getDni());

                } else {

                    visual_dni_layout.setError("El trabajador no exsite en la base de datos");

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

    private void loadChart(String dni) {

        listtemp = new ArrayList<>();
        listDate = new ArrayList<String>();
        listTemperatura = new ArrayList<>();
        listSaturacion = new ArrayList<>();
        listPulso = new ArrayList<>();

        Query query = database
                .getReference(Common.db_unidad_trabajo_data) // db_unidad_trabajo_data
                .child(Common.currentUser.getUid()) // Vad7hyj0fgQ0jG97fUaf5ZPQNH
                .child(Common.unidadTrabajoSelected.getIdUT()) // -MBCCbyJQJV3a-vxq7 UT
                .child(dni)
                .orderByKey()
                .limitToFirst(15);

        Log.e(TAG, "path query" + query);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Log.e(TAG, "dataSnapshot " + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //   Log.e(TAG, "for snapshot " + snapshot);
                    //   Log.e(TAG, "getKey  " + snapshot.getKey());
                    //   Log.e(TAG, "getValue  " + snapshot.getValue(MetricasPersonal.class));
                    //   Log.e(TAG, "getChildren  " + snapshot.getChildren());
                    MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);


                    Log.e(TAG, "metricasPersonal.getTempurature() =  " + metricasPersonal.getTempurature());
                    Log.e(TAG, "metricasPersonal.getTempurature() =  " + metricasPersonal.getTempurature());

                    Log.e(TAG, "metricasPersonal.getTempurature() =  " + metricasPersonal.getTempurature());
                    Log.e(TAG, "metricasPersonal.getSo2() =  " + metricasPersonal.getSo2());
                    Log.e(TAG, "metricasPersonal.getPulse() =  " + metricasPersonal.getPulse());

                    listtemp.add(metricasPersonal);
                    //
                    listDate.add(metricasPersonal.getDateRegister().substring(8, 10));
                    listTemperatura.add((metricasPersonal.getTempurature()));
                    listSaturacion.add(Integer.parseInt(metricasPersonal.getSo2()));
                    listPulso.add(Integer.parseInt(metricasPersonal.getPulse()));

                }

                btnGenerarChart.setEnabled(true);
                btnGenerarChart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempShowChart();
                        oxigShowChart();
                        pulseShowChart();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error" + " : " + databaseError.getMessage());
            }
        });


    }

    private void pulseShowChart() {

        if (listDate != null && listPulso != null) {

            List<String> list = new ArrayList<>();
            list.addAll(listDate);
            // String[] axisData = {"2020-05-12", "2020-05-12", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; /// fecha
            // int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
            String[] axisData = list.toArray(new String[0]);
            int[] yAxisData = new int[listPulso.size()];
            double suma = 0;
            double promedio = 0.0f;
            try {
                for (int i = 0; i < listPulso.size(); i++) {
                    suma = suma + Double.parseDouble(listPulso.get(i).toString());
                    yAxisData[i] = (int) (Double.parseDouble(listPulso.get(i).toString()));
                    // -> Log
                    Log.e(TAG, " " + (int) (Double.parseDouble(listPulso.get(i).toString())));
                    Log.e(TAG, "suma = " + suma);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "[error]" + e.getMessage());
            }

            promedio = suma / listPulso.size();

            String cad = String.valueOf(promedio);

            meanPulse.setText("Promedio pulso : " + cad.substring(0, 4));
            meanPulse.setTextColor(Color.parseColor("#03A9F4"));


            List yAxisValues = new ArrayList();
            List axisValues = new ArrayList();

            Line line = new Line(yAxisValues).setColor(Color.parseColor("#11E6A5"));

            for (int i = 0; i < axisData.length; i++) {
                Log.e(TAG, "axisData " + i + " = " + axisData[i]);
                axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
            }

            for (int i = 0; i < yAxisData.length; i++) {
                yAxisValues.add(new PointValue(i, (int) (yAxisData[i])));
            }

            List lines = new ArrayList();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(16);
            axis.setName("días");
            axis.setTextColor(Color.parseColor("#03A9F4"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
            yAxis.setName("Pulse");
            yAxis.setTextColor(Color.parseColor("#03A9F4"));
            yAxis.setTextSize(16);
            data.setAxisYLeft(yAxis);


            lineChartViewPulse.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartViewPulse.getMaximumViewport());
            viewport.bottom = 50;
            viewport.top = 115;
            lineChartViewPulse.setMaximumViewport(viewport);
            lineChartViewPulse.setCurrentViewport(viewport);
        } else {
            Log.e(TAG, "lista data null");
        }
    }

    private void oxigShowChart() {
        if (listDate != null && listSaturacion != null) {

            List<String> list = new ArrayList<>();
            list.addAll(listDate);
            // String[] axisData = {"2020-05-12", "2020-05-12", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; /// fecha
            // int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
            String[] axisData = list.toArray(new String[0]);
            int[] yAxisData = new int[listSaturacion.size()];
            double suma = 0;
            double promedio = 0.0f;
            try {
                for (int i = 0; i < listSaturacion.size(); i++) {
                    suma = suma + Double.parseDouble(listSaturacion.get(i).toString());
                    yAxisData[i] = (int) (Double.parseDouble(listSaturacion.get(i).toString()));
                    // -> Log
                    Log.e(TAG, " " + (int) (Double.parseDouble(listSaturacion.get(i).toString())));
                    Log.e(TAG, "suma = " + suma);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "[error]" + e.getMessage());
            }

            promedio = suma / listSaturacion.size();

            String cad = String.valueOf(promedio);

            meanOxig.setText("Promedio oxigeno : " + cad.substring(0, 4));
            meanOxig.setTextColor(Color.parseColor("#03A9F4"));


            List yAxisValues = new ArrayList();
            List axisValues = new ArrayList();

            Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

            for (int i = 0; i < axisData.length; i++) {
                Log.e(TAG, "axisData " + i + " = " + axisData[i]);
                axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
            }

            for (int i = 0; i < yAxisData.length; i++) {
                yAxisValues.add(new PointValue(i, (int) (yAxisData[i])));
            }

            List lines = new ArrayList();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(16);
            axis.setName("días");
            axis.setTextColor(Color.parseColor("#03A9F4"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
            yAxis.setName("Oxigeno");
            yAxis.setTextColor(Color.parseColor("#03A9F4"));
            yAxis.setTextSize(16);
            data.setAxisYLeft(yAxis);


            lineChartViewOxigeno.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartViewOxigeno.getMaximumViewport());
            viewport.bottom = 80;
            viewport.top = 110;
            lineChartViewOxigeno.setMaximumViewport(viewport);
            lineChartViewOxigeno.setCurrentViewport(viewport);
        } else {
            Log.e(TAG, "lista data null");
        }

    }

    private void tempShowChart() {

        if (listDate != null && listTemperatura != null) {

            List<String> list = new ArrayList<>();
            list.addAll(listDate);
            String[] axisData = list.toArray(new String[0]);
            // String[] axisData = {"2020-05-12", "2020-05-12", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; /// fecha
            // int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
            int[] yAxisData = new int[listTemperatura.size()];
            double suma = 0;
            double promedio = 0.0f;
            try {
                for (int i = 0; i < listTemperatura.size(); i++) {
                    Log.e(TAG, " " + (int) (Double.parseDouble(listTemperatura.get(i).toString())));
                    suma = suma + Double.parseDouble(listTemperatura.get(i).toString());
                    Log.e(TAG, "suma = " + suma);
                    yAxisData[i] = (int) (Double.parseDouble(listTemperatura.get(i).toString()));

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "[error]" + e.getMessage());
            }

            promedio = suma / listTemperatura.size();

            String cad = String.valueOf(promedio);

            meanTempe.setText("Promedio temperatura : " + cad.substring(0, 4));
            meanTempe.setTextColor(Color.parseColor("#03A9F4"));


            List yAxisValues = new ArrayList();
            List axisValues = new ArrayList();

            Line line = new Line(yAxisValues).setColor(Color.parseColor("#FF2626"));

            for (int i = 0; i < axisData.length; i++) {
                Log.e(TAG, "axisData " + i + " = " + axisData[i]);
                axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
            }

            for (int i = 0; i < yAxisData.length; i++) {
                yAxisValues.add(new PointValue(i, (int) (yAxisData[i])));
            }

            List lines = new ArrayList();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(16);
            axis.setName("días");
            axis.setTextColor(Color.parseColor("#03A9F4"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
            yAxis.setName("Temperatura");
            yAxis.setTextColor(Color.parseColor("#03A9F4"));
            yAxis.setTextSize(16);
            data.setAxisYLeft(yAxis);

            lineChartViewTemperatura.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartViewTemperatura.getMaximumViewport());
            viewport.bottom = 30;
            viewport.top = 45;
            lineChartViewTemperatura.setMaximumViewport(viewport);
            lineChartViewTemperatura.setCurrentViewport(viewport);
        } else {
            Log.e(TAG, "lista data null");
        }


    }


}