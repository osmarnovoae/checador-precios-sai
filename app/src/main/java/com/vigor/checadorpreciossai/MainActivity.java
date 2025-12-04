package com.vigor.checadorpreciossai;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.net.ConnectivityManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.OutputStream;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity {

    private EditText txtProducto;
    private ListView listProducts;
    private Button btnBuscar;
    private Button btnLeerCb;
    private JSONArray jsonArrayProducts;
    private ListAdapter productsAdapter;
    private File file;
    private ProgressDialog progressDialog;



   private String token = "";
    //private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Conectando al SAI");
        progressDialog.setMessage("ESTAMOS CONSULTADO LA INFORMACION EN EL SERVIDOR PORFAVOR ESPERE...");
        progressDialog.setIndeterminate(true);
        this.setTitle("CATALOGO DE PRODUCTOS");
        setContentView(R.layout.activity_main);
        this.file = getFilesDir();
        getComponents();
        setEvents();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            txtProducto.setText(scanResult.getContents());
        }
    }

    private void getComponents(){
        this.btnBuscar = (Button)findViewById(R.id.btnBuscar);
        this.txtProducto = (EditText)findViewById(R.id.txtProducto);
        this.listProducts = (ListView) findViewById(R.id.listProducts);

    }

    private void setEvents(){

        this.btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                onButtonClick();
            }
        });

        this.listProducts.setOnItemClickListener(getExistencias());
    }

    private void leerCodigoBarras() {

        new IntentIntegrator(MainActivity.this).initiateScan();


    }


    public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            return isConnected;
        }





    private void makeProductsRequest(){

                String url = "http://xx.xx.xx.xxxx/api/products/getproductsprices?sucursal="+Util.SUC_NAME;
                Log.d("consulta", url);
                RequestQueue queue = Volley.newRequestQueue(this);


                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if(response.length()<0){
                                    Toast.makeText(MainActivity.this, "NO SE ENCONTRARON RESULTADOS", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                                Log.d("RESULTADO",response.toString());
                                jsonArrayProducts = response;
                                saveProductsResourceInMemory();
                                createTable();

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
                        headers.put("User-Agent", "vigor-sai-app");
                        headers.put("Authorization", token);

                        return headers;
                    }


                };

                queue.add(request);

    }



    private AdapterView.OnItemClickListener getExistencias() {

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ExistenciasView.class);
                TextView txtCveProd = (TextView)view.findViewById(R.id.txtCve_prod);
                TextView txtDescripcion = (TextView)view.findViewById(R.id.txtDescripcion);
                String cve_prod = txtCveProd.getText().toString();
                intent.putExtra("cve_prod",cve_prod);
                intent.putExtra("descripcion",txtDescripcion.getText());
                startActivity(intent);
            }

        };

        return listener;
    }


    private void onButtonClick() {

            if (isOnline()) {
                makeProductsRequest();

            }else{
                try {

                    File products_json_file = new File(this.file.getAbsolutePath(), "products.json");
                    if (products_json_file.exists()) {
                        Scanner sc = new Scanner(products_json_file);
                        jsonArrayProducts = new JSONArray(sc.useDelimiter("\\A").next());
                        createTable();

                    }else{
                        new AlertDialog.Builder(this).setMessage("ES NECESARIO QUE TE CONECTES A INTERNET PARA BAJAR UNA LISTA DE PRECIOS. Y PODER TRABAJAR SIN CONEXION.");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }


    }

    public void createTable(){

        JSONObject obj;
        JSONArray jsonFiltered = new JSONArray();

        for (int i=0; i<jsonArrayProducts.length(); i++){
            try {
                obj = jsonArrayProducts.getJSONObject(i);

                if(obj.getString("desc_prod").contains(txtProducto.getText()) ||
                        obj.getString("activo").contains(txtProducto.getText())
                      // obj.getString("codbar").equals(txtProducto.getText().toString())
                  ){
                    jsonFiltered.put(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ListView listProducts = (ListView)findViewById(R.id.listProducts);
        ListAdapter productsAdapter = new ProductListAdapter(getApplicationContext(),jsonFiltered);
        listProducts.setAdapter(productsAdapter);
        progressDialog.dismiss();
    }



    //guarda un .json resultado del request al ws
    public void saveProductsResourceInMemory(){

        Long required_space = new Long("1000000");

        if(this.file.getFreeSpace() > required_space){

            File products_json_file = new File(this.file.getAbsolutePath(),"products.json");
            try{
                OutputStream out = openFileOutput(products_json_file.getName(),MODE_PRIVATE);
                out.write(jsonArrayProducts.toString().getBytes());
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

}
