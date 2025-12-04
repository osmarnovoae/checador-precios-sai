package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Sampler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class VentasNetas_xcdp extends BaseActivity  {

    private DatePickerDialog.OnClickListener mDatePickerClickListener;
    EditText txtFechaInicio;
    EditText txtFechaFin;
    EditText txtCliente;
    EditText txtProducto;
    Spinner spinnerSucs;
    String sucursal_seleccionada;
    String[] arraySucursales = {"TODAS","CO1","CO2", "MAT","CIH","PIN","CDG","JO","COAH"};

    String sucursalSeleccionada;
    String NomCliente;
    String producto;
    DatePickerDialog datePickerDialog;

    AdapterView.OnItemSelectedListener onItemSelectedListenerSpinnerSucs = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sucursalSeleccionada = ((TextView) view).getText().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            return;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("APLICAR FILTROS");
        setContentView(R.layout.activity_ventas_netas_xcdp);
        txtProducto = findViewById(R.id.txtProducto);
        txtFechaInicio  = findViewById(R.id.txtFechaInicio);
        txtFechaFin = findViewById(R.id.txtFechaFin);
        txtCliente = findViewById(R.id.txtCliente);

        spinnerSucs =  findViewById(R.id.spinnerSucs);
        SpinnerAdapter arrayAdapterSucs = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arraySucursales);
        spinnerSucs.setAdapter(arrayAdapterSucs);

        spinnerSucs.setOnItemSelectedListener(onItemSelectedListenerSpinnerSucs);



        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String now = format.format(date);



        txtFechaInicio.setText(now);
        txtFechaFin.setText(now);

        // setSpinnerValues();
        setDatePickers();
        Button btnConsultar = findViewById(R.id.btnConsultar);

        btnConsultar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VentasNetas_xcdp.this,VentasClieDesProd.class);
                intent.putExtra("fechaInicio",txtFechaInicio.getText().toString());
                intent.putExtra("fechaFin",txtFechaFin.getText().toString());
                intent.putExtra("cliente",txtCliente.getText().toString().trim());
                intent.putExtra("producto",txtProducto.getText().toString().trim());
                intent.putExtra("sucursal", sucursalSeleccionada);

                startActivity(intent);
                //crearReporte();
            }
        });


    }

    private void setDatePickers(){
        Activity activity = this;
        Util.createDatePicker(txtFechaInicio, activity);
        Util.createDatePicker(txtFechaFin, activity);

    }


}


