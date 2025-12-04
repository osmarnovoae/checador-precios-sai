package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VnetasAlmacenClase extends BaseActivity {

    EditText txtFechaIni;
    EditText txtFechaFin;
    Button btnConsultar;
    String[] arraySucursales = {"TODAS", "CO1", "MAT", "CIH", "PIN", "CDG","JOC", "COAH"};
    String[] arrayClasesProd = {"TODAS", "A", "B", "C", "G", "AA"};
    Spinner spinnerSucs;
    Spinner spinnerClasesProd;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Ventas netas deglosadas por clase");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnetas_almacen_clase);
        initializeControls();
    }


    public void initializeControls() {

        this.txtFechaIni = findViewById(R.id.txtFechaIni);
        this.txtFechaFin = findViewById(R.id.txtFechaFin);
        this.btnConsultar = findViewById(R.id.btnConsultaRepoClase);
        spinnerSucs = findViewById(R.id.spinnerAlmacen);
        SpinnerAdapter arrayAdapterSucs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySucursales);
        spinnerSucs.setAdapter(arrayAdapterSucs);

        spinnerClasesProd = findViewById(R.id.spinnerClasesProd);
        SpinnerAdapter arrayAdapterClases = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayClasesProd);
        spinnerClasesProd.setAdapter(arrayAdapterClases);

        //llama a la funcion que crea los datepickers
        setDatePickers();

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent vnetasclaseIntent = new Intent(getBaseContext(), v_netas_clase_table.class);
                vnetasclaseIntent.putExtra("fechaInicio", txtFechaIni.getText());
                vnetasclaseIntent.putExtra("fechaFin", txtFechaFin.getText());
                String sucursal = getSucursalId(spinnerSucs.getSelectedItem().toString());
                vnetasclaseIntent.putExtra("sucursal",sucursal);
                vnetasclaseIntent.putExtra("cse_prod",spinnerClasesProd.getSelectedItem().toString());
                startActivity(vnetasclaseIntent);
            }
        });

    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    private String getSucursalId(String sucursal){

        switch (sucursal){
            case "CO1":
                return "4";
            case "MAT":
                return "1";
            case "CIH":
                return "3";
            case "PIN":
                return "5";
            case "CDG":
                return "6";
            case "JOC":
                return "7";
            case "COAH":
                return "8";
            default:
                return "TODAS";
        }

    }


    private void setDatePickers(){
        Activity activity = this;
        Util.createDatePicker(txtFechaIni, activity);
        Util.createDatePicker(txtFechaFin, activity);
    }



}

