package com.vigor.checadorpreciossai;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class v_netas_clase_table extends BaseActivity {


    private TableLayout tableVentas;
    private ProgressDialog progressDialog;
    private String token = "";
    //private String token = "";
    //no de clientes por pagina
    DecimalFormat df;
    JSONArray datos;
    int indice_actual=0;
    int no_paginas=0;
    int pagina_actual=1;
    String nom_cte_anterior="";
    TextView totalVentasV;
    Button btnSig;
    Button btnPrev;
    TextView noPaginasV;
    Thread crearTableTask;
    Double granTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        granTotal=0.00;
        crearTableTask = new CrearTablaRunnable();
        setContentView(R.layout.activity_v_netas_clase_table);
        setProgressDialog();
        df = new DecimalFormat("$###,###,###.00");

        totalVentasV = findViewById(R.id.totalVentasV);
        noPaginasV = findViewById(R.id.paginasView);
        btnSig = findViewById(R.id.btnSig);
        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setEnabled(false);
        tableVentas = findViewById(R.id.tableVentasNetasPorClase);
        crearReporte();
        btnSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagina_actual++;
                if (pagina_actual==no_paginas){
                    btnSig.setEnabled(false);

                }
                btnPrev.setEnabled(true);
                noPaginasV.setText("Pag. "+String.valueOf(pagina_actual)+" de "+String.valueOf(no_paginas));
                indice_actual++;
                crearTableTask.run();


            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagina_actual--;
                nom_cte_anterior="";
                if (pagina_actual==1){
                    btnPrev.setEnabled(false);

                }
                btnSig.setEnabled(true);
                noPaginasV.setText("Pag. "+String.valueOf(pagina_actual)+" de "+String.valueOf(no_paginas));
                //calcular pagina anterior
                indice_actual=(pagina_actual*50)-50;
                crearTableTask.run();

            }

        });


    }


    public void getPaginaAnterior() {
        crearTableTask.run();


    }

    public void setProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando");
        progressDialog.setMessage("Estamos obteniendo la informacion para este reporte... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    private void crearReporte(){

        Bundle extras = getIntent().getExtras();

        final String fechaIni = extras.get("fechaInicio").toString();
        final String fechaFin = extras.get("fechaFin").toString();

        //Spinner sp = findViewById(R.id.statusFacSpin);
        //String status_fac = sp.getSelectedItem().toString();

        String url = "http://xx.xx.xx.xx/api/products/getVentasNetasPorAlmacenDesglosadasPorClase";
        RequestQueue queue = Volley.newRequestQueue(this);
        tableVentas.removeAllViews();



        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.v("datos",response.toString());

                        if (response.length()>0) {
                            try {
                                datos = new JSONArray(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            no_paginas=calcularNoPaginas();
                            noPaginasV.setText("Pag. "+String.valueOf(pagina_actual)+" de "+String.valueOf(calcularNoPaginas()));
                            if (no_paginas==1){
                                btnSig.setEnabled(false);
                                btnPrev.setEnabled(false);
                            }

                            crearTableTask.run();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(v_netas_clase_table.this, "NO SE ENCONTRARON VENTAS EN EL RANGO DE FECHAS SELECCINADAS, PRUEBA OTRA FECHA.", Toast.LENGTH_SHORT).show();
                            finish();
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
                Bundle bundle = getIntent().getExtras();

                Map<String,String> params = new HashMap<>();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("sucursal",bundle.get("sucursal").toString());
                Log.d("sucursal", bundle.get("sucursal").toString());
                params.put("cse_prod",bundle.get("cse_prod").toString());

                return params;


            }
        };
        progressDialog.show();
        queue.add(request);
    }

    public int calcularNoPaginas(){
        if (this.datos.length()>0){
            float datos = (float)this.datos.length();
            float limite = Float.valueOf(50);


            return (int)Math.ceil(datos/limite);

        }else{
            return 1;
        }

    }


    class CrearTablaRunnable extends Thread {

        Context context = v_netas_clase_table.this;


        @Override
        public synchronized void run() {

            try {
                imprimeDatosReporte();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        public void imprimeDatosReporte() throws JSONException {

            tableVentas.removeAllViews();
            String cte_actual;
            int mostrar_n_rows = 50;
            if (datos.length()<mostrar_n_rows){
                mostrar_n_rows=datos.length();
            }

            for (int i=0; i<mostrar_n_rows && indice_actual<=datos.length(); i++){
                cte_actual=datos.getJSONObject(indice_actual).getString("des_lug").trim();
                if (i==0 && nom_cte_anterior.equals("")){

                    crearRowCliente(cte_actual);
                    crearEncabezadoTabla();
                }
                if(!cte_actual.equals(nom_cte_anterior) && !nom_cte_anterior.equals("")){
                    createTotalPorClienteRow(CalcularTotalXcliente(nom_cte_anterior));
                    crearRowCliente(cte_actual);
                    crearEncabezadoTabla();
                }

                imprimeFacturasClienteRow(datos.getJSONObject(indice_actual));
                nom_cte_anterior=cte_actual;
                indice_actual++;
                //si ya es el ultimo registro que imprima tambien el total
                if(indice_actual==datos.length()){
                    createTotalPorClienteRow(CalcularTotalXcliente(cte_actual));
                }

            }
            totalVentasV.setText("VENTAS NETAS: "+String.valueOf(df.format(granTotal)));
            progressDialog.dismiss();
        }

        public double CalcularTotalXcliente(String nom_cte) throws JSONException {
            double total_cliente=0;

            for (int i=0; i<datos.length(); i++){
                if (datos.getJSONObject(i).getString("des_lug").trim().equals(nom_cte)){
                    total_cliente+=new Double(datos.getJSONObject(i).get("sum_total").toString().trim());

                }
            }
            granTotal+=total_cliente;
            return total_cliente;
        }


        private void createTotalPorClienteRow(double total_x_cliente){


            TableRow row = new TableRow(context);
            row.setBackgroundColor(Color.LTGRAY);
            TableRow.LayoutParams rowClienteSpan = new TableRow.LayoutParams();
            rowClienteSpan.span=4;

            TextView view = new TextView(context);
            view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            view.setTextSize(9);
            view.setText("Total x cliente en MX: "+df.format(total_x_cliente));
            row.addView(view);
            tableVentas.addView(row);



        }

        private void crearRowCliente(String nom_cte) {


            TableRow rowCliente = new TableRow(context);
            rowCliente.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TableRow.LayoutParams rowClienteSpan = new TableRow.LayoutParams();
            rowClienteSpan.span=4;
            TextView clienteView = new TextView(context);
            clienteView.setLayoutParams(rowClienteSpan);
            clienteView.setText(nom_cte);

            rowCliente.addView(clienteView);
            tableVentas.addView(rowCliente);


        }

        private void crearEncabezadoTabla() {

            TableRow encabezadoRow = new TableRow(context);
            encabezadoRow.setBackgroundColor(Color.LTGRAY);


            TextView descripcionView = new TextView(context);
            TextView cantView = new TextView(context);
            TextView ventaView = new TextView(context);


            descripcionView.setText("CLASE");
            cantView.setText("CANT");
            ventaView.setText("VENTA/MX");



            descripcionView.setPadding(0,5,10,5);
            cantView.setPadding(0,5,10,5);
            ventaView.setPadding(0,5,10,5);


            descripcionView.setTextSize(7);
            cantView.setTextSize(7);
            ventaView.setTextSize(7);


            encabezadoRow.addView(descripcionView);
            encabezadoRow.addView(cantView);
            encabezadoRow.addView(ventaView);

            tableVentas.addView(encabezadoRow);


        }

        private void imprimeFacturasClienteRow(JSONObject json) throws JSONException {



            TableRow encabezadoRow = new TableRow(context);
            // encabezadoRow.setBackgroundColor(Color.LTGRAY);


            TextView descripcionView = new TextView(context);
            TextView cantView = new TextView(context);
            TextView ventaView = new TextView(context);

            descripcionView.setText(json.getString("cse_prod").trim());
            cantView.setText(json.getString("cant_surt").trim());
            ventaView.setText(df.format(new Double(json.getString("sum_total").trim())));


            descripcionView.setTextSize(7);
            cantView.setTextSize(7);
            cantView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            ventaView.setTextSize(7);


            descripcionView.setPadding(0,5,10,5);
            cantView.setPadding(0,5,10,5);
            ventaView.setPadding(0,5,10,5);

            encabezadoRow.addView(descripcionView);
            encabezadoRow.addView(cantView);
            encabezadoRow.addView(ventaView);

            tableVentas.addView(encabezadoRow);


        }


    }



}
