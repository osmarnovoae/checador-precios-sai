package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.app.DatePickerDialog;
import com.google.android.material.chip.Chip;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductosMasVendidosPorAgente extends BaseActivity {

    EditText txtFechaInicio;
    EditText txtFechaFin;
    Button btnConsultar;
    Spinner spAgentes;
    DatePickerDialog datePickerDialog;
    TextView txtTotalVendido;
    Double totalVendido=0.00;

    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_mas_vendidos_por_agente);

        //definir las opciones pera el spinner de agentes
        this.setTitle("PRODUCTOS MAS VENDIDOS POR AGENTE");

        spAgentes =  findViewById(R.id.spAgentes);
        txtFechaInicio  = findViewById(R.id.txFechaInicio);
        txtFechaFin = findViewById(R.id.txFechaFin);
        btnConsultar = findViewById(R.id.btnConsultar);
        txtTotalVendido = findViewById(R.id.txtTotalVendido);

        // setSpinnerValues();
        setDatePickers();

        //antes de que se cargue el activity cargar los agentes
        getAgentesRequest();

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productosMasVendidosPorAgente();
            }
        });

    }

    //en el spinner el nombre viene concadenado con la clave del agente hay que separar solo la clave y retornar solo la clave
    private String getAgenteId(){
        String clave_agente = spAgentes.getSelectedItem().toString();
        Log.d("CLAVE AGENTE",clave_agente.substring(2));
        return clave_agente.substring(0,1).trim();
    }


    public void getAgentesRequest(){

        String url_request  = "http:/xx.xx.xx.xxx/api/products/getAgentes";
        RequestQueue requestQueue =  Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url_request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("RESULTADO DE RESPONSE",response.toString().replace("  ", ""));
                        String agente = "";
                        ArrayList<String> agentesArray = new ArrayList<String>();

                        try {
                            JSONArray data = new JSONArray(response.toString().replace("  ", ""));
                            for (int i=0; i < data.length(); i++) {
                               // Log.v("string formado: ",data.getJSONObject(i).getString("exp_1")+" "+data.getJSONObject(i).getString("nom_age"));
                                agente =  data.getJSONObject(i).getString("nom_age");
                                agentesArray.add(agente.trim());
                            }

                            spAgentes.setAdapter(new ArrayAdapter<String>(
                                    ProductosMasVendidosPorAgente.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    agentesArray
                            ));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        )
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            Map headers = new HashMap<>();
            headers.put("User-Agent", "xxx-xx-xxx");
            headers.put("Authorization", token);
            headers.put("Accept", "application/json; encode=utf-8");
            // headers.put("Content-type", "application/x-www-form-urlencoded");
            headers.put("Host", "localhost");

                return headers;
             }

        };


        requestQueue.add(stringRequest);
    }


    public void productosMasVendidosPorAgente(){

        String url_request = "http://xx.xx.xx.xxx/api/products/getProductosMasVendidosPorAgente";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url_request,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray data = new JSONArray(response);
                            for(int i=0; i<data.length(); i++){
                                 totalVendido+=Double.parseDouble(data.getJSONObject(i).getString("total"));

                            }


                            DecimalFormat twoPlaces = new DecimalFormat("0.00");
                            txtTotalVendido.setText("Total: $"+twoPlaces.format(totalVendido));

                            fillProductosMasVendidosPorAgente(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }


        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<>();
                headers.put("User-Agent", "xxx-xxx-xx");
                headers.put("Authorization", token);
                headers.put("Accept", "application/json; encode=utf-8");
                // headers.put("Content-type", "application/x-www-form-urlencoded");
                headers.put("Host", "localhost");

                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("fechaIni",txtFechaInicio.getText().toString());
                params.put("fechaFin",txtFechaFin.getText().toString());
                params.put("cve_age",spAgentes.getSelectedItem().toString().trim());
                return params;

            }
        };

        queue.add(request);

    }

    private void fillProductosMasVendidosPorAgente(JSONArray data) {

        BaseAdapter productosMasVendidosPorAgenteAdapter = new ProductosMasVendidosPorAgenteAdapter(this, data);
        ListView lvProductosMasVendidosClientes = (ListView) findViewById(R.id.lvProductosMasVendidosPorAgente);
        lvProductosMasVendidosClientes.setAdapter(productosMasVendidosPorAgenteAdapter);

    }

    private void setDatePickers(){
        Activity activity  = this;
        Util.createDatePicker(txtFechaInicio, activity);
        Util.createDatePicker(txtFechaFin, activity);

    }




}