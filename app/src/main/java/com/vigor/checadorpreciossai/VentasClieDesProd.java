package com.vigor.checadorpreciossai;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.renderscript.Sampler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class VentasClieDesProd extends AppCompatActivity  {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearTableTask = new CrearTablaRunnable();
        setContentView(R.layout.activity_ventas_clie_des_prod);
        this.setTitle("VENTAS POR CLIENTE DESGLOSADO POR PRODUCTO");
        setProgressDialog();
         df = new DecimalFormat("$###,###,###.00");

         totalVentasV = findViewById(R.id.totalVentasV);
        noPaginasV = findViewById(R.id.paginasView);
         btnSig = findViewById(R.id.btnSig);
         btnPrev = findViewById(R.id.btnPrev);
         btnPrev.setEnabled(false);
        tableVentas = findViewById(R.id.tableVentas);
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

        String url = "http://xx.xx.xx.xxx/api/products/getVentasNetasPorClienteDesglosadoPorProducto";
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
                                getTotalVentaNeta();

                                crearTableTask.run();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(VentasClieDesProd.this, "NO SE ENCONTRARON VENTAS EN EL RANGO DE FECHAS SELECCINADAS, PRUEBA OTRA FECHA.", Toast.LENGTH_SHORT).show();
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
                headers.put("User-Agent", "xx-xx-xx");
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
                params.put("cliente", bundle.get("cliente").toString());
                params.put("producto", bundle.get("producto").toString());
                params.put("sucursal",bundle.get("sucursal").toString());

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

    public void getTotalVentaNeta(){
        Bundle extras = getIntent().getExtras();

        final String fechaIni = extras.get("fechaInicio").toString();
        final String fechaFin = extras.get("fechaFin").toString();
        //Spinner sp = findViewById(R.id.statusFacSpin);
        //String status_fac = sp.getSelectedItem().toString();

        String url = "http://xxx.xxx.xxx.xxx/api/products/getTotalVentasNetasPorClienteDesglosadoPorProducto";
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String data) {

                        //modifica la etiqueta total ventas con el valor total de la ventas netas

                        JSONArray response= null;
                        try {
                            response = new JSONArray(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String total_venta_neta = "";

                        try {

                               total_venta_neta =  response.getJSONObject(0).getString("sum_venta_neta").trim();
                               if (!total_venta_neta.equals("null")){
                                   total_venta_neta=df.format(new Double(total_venta_neta));
                               }
                            } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        totalVentasV.setText("TOTAL FAC: "+total_venta_neta);

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
                Map<String,String> headers = new HashMap<>();
                headers.put("User-Agent", "xx-xxx-xxx");
                headers.put("Authorization", token);
                headers.put("Host", "localhost");
                headers.put("Accept", "application/json; encode=utf-8");

                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Bundle bundle = getIntent().getExtras();

                Map<String,String> params = new HashMap<>();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("cliente", bundle.get("cliente").toString());
                params.put("producto", bundle.get("producto").toString());
                params.put("sucursal",bundle.get("sucursal").toString());

                return params;


            }


        };
        queue.add(request);
    }



    class CrearTablaRunnable extends Thread {

        Context context = VentasClieDesProd.this;


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
                cte_actual=datos.getJSONObject(indice_actual).getString("nom_cte").trim();
                if (i==0 && nom_cte_anterior.equals("")){

                   crearRowCliente(cte_actual.trim());
                   crearEncabezadoTabla();
                }
                if(!cte_actual.equals(nom_cte_anterior) && !nom_cte_anterior.equals("")){
                    createTotalPorClienteRow(CalcularTotalXcliente(nom_cte_anterior));
                    crearRowCliente(cte_actual.trim());
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
            progressDialog.dismiss();
        }

        public double CalcularTotalXcliente(String nom_cte) throws JSONException {
            double total_cliente=0;

                for (int i=0; i<datos.length(); i++){
                    if (datos.getJSONObject(i).getString("nom_cte").trim().equals(nom_cte)){
                        total_cliente+=new Double(datos.getJSONObject(i).get("venta_neta").toString().trim());
                    }
                }

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
            clienteView.setText(nom_cte.trim());

            rowCliente.addView(clienteView);
            tableVentas.addView(rowCliente);


        }

        private void crearEncabezadoTabla() {

            TableRow encabezadoRow = new TableRow(context);
            encabezadoRow.setBackgroundColor(Color.LTGRAY);

            TextView nofacView = new TextView(context);
            TextView descripcionView = new TextView(context);
            TextView cantView = new TextView(context);
            TextView ventaView = new TextView(context);

           nofacView.setText("NO FAC");
            descripcionView.setText("PRODUCTO");
            cantView.setText("CANT");
            ventaView.setText("VENTA NETA");


           /* nofacView.setPadding(0,5,5,5);
            descripcionView.setPadding(0,5,5,5);
            cantView.setPadding(0,5,5,5);
            ventaView.setPadding(0,5,10,5);*/

            nofacView.setTextSize(7);
            descripcionView.setTextSize(7);
            cantView.setTextSize(7);
            ventaView.setTextSize(7);


            encabezadoRow.addView(nofacView);
            encabezadoRow.addView(descripcionView);
            encabezadoRow.addView(cantView);
            encabezadoRow.addView(ventaView);

            tableVentas.addView(encabezadoRow);


        }

        private void imprimeFacturasClienteRow(JSONObject json) throws JSONException {



            TableRow encabezadoRow = new TableRow(context);
            // encabezadoRow.setBackgroundColor(Color.LTGRAY);

            TextView nofacView = new TextView(context);
            TextView descripcionView = new TextView(context);
            TextView cantView = new TextView(context);
            TextView ventaView = new TextView(context);

            nofacView.setText(json.getString("no_fac").trim());

            String nom_prd = json.getString("desc_prod");
            String nom_trimed;
            nom_trimed = nom_prd.trim();
            // se añade esa rutinapor para recortar nombres de productos por que algunos estan muy largos
            try{
                Log.d("recortado", nom_trimed.substring(0,30));
                descripcionView.setText(nom_trimed.substring(0,30));
            }catch (StringIndexOutOfBoundsException e){
                descripcionView.setText(nom_trimed);
            }




            cantView.setText(json.getString("cant_surt").trim());
            ventaView.setText(df.format(new Double(json.getString("venta_neta").trim())));

            nofacView.setTextSize(7);
            nofacView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            descripcionView.setTextSize(7);
            descripcionView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            cantView.setTextSize(7);
            cantView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            ventaView.setTextSize(7);

           /*nofacView.setPadding(0,5,5,5);
            descripcionView.setPadding(0,5,5,5);
            cantView.setPadding(0,5,5,5);
            ventaView.setPadding(0,5,5,5);*/

            encabezadoRow.addView(nofacView);
            encabezadoRow.addView(descripcionView);
            encabezadoRow.addView(cantView);
            encabezadoRow.addView(ventaView);

            tableVentas.addView(encabezadoRow);

        }

    }






}
