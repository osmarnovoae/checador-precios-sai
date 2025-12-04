package com.vigor.checadorpreciossai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ConsultarSaldoClientes extends BaseActivity {

    Button btnBuscar;
    EditText txtNombreCliente;
    String token = "";
    Spinner tipoSaldoSpin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosulta_facturas_vencidas);

        this.btnBuscar = (Button)findViewById(R.id.btnBuscarCliente);
        this.txtNombreCliente = (EditText) findViewById(R.id.txtCliente);
        this.tipoSaldoSpin = (Spinner)findViewById(R.id.spin_tipo_saldo);

        ArrayAdapter<CharSequence> tipoSaldoSpinAdapter = ArrayAdapter.createFromResource(this,R.array.array_adapter_tipo_saldo,android.R.layout.simple_spinner_item);
        tipoSaldoSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        this.tipoSaldoSpin.setAdapter(tipoSaldoSpinAdapter);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSaldoClientesRequest();

            }
        });

    }


    private void getSaldoClientesRequest(){

        String tipoSaldo = tipoSaldoSpin.getSelectedItem().toString();
        String base_url = "http://xxx.xx.xx.xxx/api/products/";
        String url;

        if(tipoSaldo.equals("VENCIDO")) {
            url = base_url+"getSaldoVencidoCliente";
        }else{
            url = base_url+"getSaldoNoVencidoCliente";
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {

             @Override
             public void onResponse(String response) {

                  try {
                       JSONArray data = new JSONArray(response);
                        Log.d("result",data.toString());
                       fillSaldoClientesListView(data);
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
                headers.put("User-Agent", "vigor-sai-app");
                headers.put("Authorization", token);
                headers.put("Accept", "application/json; encode=utf-8");
                // headers.put("Content-type", "application/x-www-form-urlencoded");
                headers.put("Host", "localhost");

                return headers;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("nombreCliente",txtNombreCliente.getText().toString());
                return params;


            }
        };

        queue.add(request);


    }


    private void fillSaldoClientesListView(JSONArray data){
        String tipoSaldo = tipoSaldoSpin.getSelectedItem().toString();
        BaseAdapter saldoClientesAdapter = new ConsultaSaldoClientesAdapter(this,data,tipoSaldo);
        ListView saldoClientesListView = (ListView) findViewById(R.id.listSaldoClientes);
        saldoClientesListView.setAdapter(saldoClientesAdapter);

    }



}
