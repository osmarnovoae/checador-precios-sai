package com.vigor.checadorpreciossai;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Process;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ExistenciasView extends BaseActivity {

    TableLayout tableLayout;
     private String token = "";

    private JSONArray arrayExite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("EXISTENCIAS");
        setContentView(R.layout.activity_existencias_view);
        this.tableLayout = (TableLayout)findViewById(R.id.tableExiste);
        estableceFuenteDatos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cerrarSesion) {

                this.finishAffinity();
                return true;

        }else{

            return super.onOptionsItemSelected(item);

        }
    }



    private void estableceFuenteDatos(){

        if (isOnline()){
            getExistenciasJson();

        }else{

            useExistenciasFileJSON();

        }


    }


    private void useExistenciasFileJSON(){

        try {

            File existe_json = new File(getFilesDir().getAbsolutePath(), "existencias.json");
            if (existe_json.exists()) {
                Scanner sc = new Scanner(existe_json);
                arrayExite = new JSONArray(sc.useDelimiter("\\A").next());
                makeTable();

            }else{
                new AlertDialog.Builder(this).setMessage("ES NECESARIO QUE TE CONECTES A INTERNET PARA BAJAR UNA LISTA DE PRECIOS. Y PODER TRABAJAR SIN CONEXION.");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    //guarda un .json resultado del request al ws
    public void createExistenciasFileJSON(){

        Long required_space = new Long("1000000");

        if(getFilesDir().getFreeSpace() > required_space){

            File products_json_file = new File(getFilesDir().getAbsolutePath(),"existencias.json");
            try{
                OutputStream out = openFileOutput(products_json_file.getName(),MODE_PRIVATE);
                out.write(arrayExite.toString().getBytes());
                out.close();


            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else{
            new AlertDialog
                    .Builder(this)
                    .setMessage("No tienes el espacio suficiciente en la memoria interna de tu smartphone, para trabajar sin conexion")
                    .show();
        }

    }

   private void makeTable() {

        JSONArray existe = arrayExite;
        Log.d("existencias json",existe.toString());
        String cve_prod = getIntent().getStringExtra("cve_prod");
        TextView lblExisteTotal = (TextView)findViewById(R.id.lblExisteTotal);
        TextView txtProducto = (TextView)findViewById(R.id.descProd);

        txtProducto.setText(getIntent().getStringExtra("descripcion"));

        for (int i = 0; i<existe.length(); i++){
            try {
                JSONObject obj = existe.getJSONObject(i);
                TableRow row = new TableRow(this);
                lblExisteTotal.setText("Existencia total: "+obj.getString("total"));
               String lugar = "";
                switch (obj.getString("lugar").trim()){
                    case "1A":
                        lugar ="ALMACEN PRINCIPAL";
                        break;
                    case "1" :
                        lugar ="TECOMAN MOSTRADOR";
                        break;
                    case "1B" :
                        lugar ="TECUANILLO";
                        break;
                    case "2" :
                        lugar ="COLIMA 2";
                        break;
                    case "2D" :
                        lugar ="PROCESO";
                        break;
                    case "3" :
                        lugar ="CIHUATLAN";
                        break;
                    case "3B" :
                        lugar ="TEMPORAL (PROD. EN DIQUE)";
                        break;
                    case "4" :
                        lugar ="COLIMA 1";
                        break;
                    case "4C" :
                        lugar ="GANADERIA MINATITLAN";
                        break;
                    case "5" :
                        lugar ="PINO SUAREZ";
                        break;
                    case "5E" :
                        lugar ="ALM. PRODUCTO PARA PRUEBA";
                        break;
                    case "6" :
                        lugar ="CD GUZMAN";
                        break;
                    case "6F" :
                        lugar ="APOYO A COLIMA";
                        break;
                    case "HV" :
                        lugar ="SAYULA";
                        break;
                    case "PR" :
                        lugar ="PRANI";
                        break;
                    case "7"  :
                        lugar = "JOCOTEPEC";
                        break;
                    case "SAUL MARAV"  :
                        lugar = "SAUL MARAVILLA";
                        break;
                    case "ABUNDIO M."  :
                        lugar = "EDGAR ABUNDIO";
                        break;
                    case "8"  :
                        lugar = "COAHUAYANA";
                        break;
                    default:
                        lugar = obj.getString("lugar").trim();
                        break;

                }
                TextView txtLugar = new TextView(this);
                txtLugar.setText(lugar);
                txtLugar.setGravity(Gravity.CENTER);
                txtLugar.setTextSize(10);
                txtLugar.setBackgroundColor(Color.WHITE);
                txtLugar.setPadding(10,10,10,10);

                row.addView(txtLugar);

                TextView txtExiste = new TextView(this);
                txtExiste.setText(obj.getString("existencia"));
                txtExiste.setGravity(Gravity.CENTER);
                txtExiste.setGravity(Gravity.CENTER);
                txtExiste.setTextSize(10);
                txtExiste.setBackgroundColor(Color.WHITE);
                txtExiste.setPadding(10,10,10,10);
                row.addView(txtExiste);


                tableLayout.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void getExistenciasJson(){

        String url = "http://xx.xx.xx.xx/api/products/getexistencias?cve_prod="+getIntent().getStringExtra("cve_prod");
        RequestQueue queue = Volley.newRequestQueue(this);


        JsonArrayRequest stringRequest = new JsonArrayRequest( url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        arrayExite = response;
                        createExistenciasFileJSON();
                        makeTable();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("User-Agent", "xxx-xxx-xxx");
                headers.put("Authorization", token);

                return headers;
            }


        };

        queue.add(stringRequest);

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }



}
