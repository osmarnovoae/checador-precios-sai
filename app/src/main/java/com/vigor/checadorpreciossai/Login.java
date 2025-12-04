package com.vigor.checadorpreciossai;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;

import static java.lang.String.valueOf;

public class Login extends AppCompatActivity {


    EditText txtPass;
    Button btnIngresar;
    String isImeiValid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("INICIAR SESION");


        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        this.txtPass = (EditText) findViewById(R.id.txtPass);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarCuenta();
            }
        });

    }



    public void ValidarCuenta() {

        String pass = this.txtPass.getText().toString();

        switch (pass){

            case "JOSE16":
                Util.IS_ADMIN = 1;
                Util.SUC_NAME="ADMIN";
                startMainActivity();
                //initialize();
                break;
            case "LEO01":
                Util.IS_ADMIN = 1;
                Util.SUC_NAME="ADMIN";
                startMainActivity();

               // initialize();
                break;
            case "JROMEO":
                Util.IS_ADMIN = 1;
                Util.SUC_NAME="ADMIN";
                startMainActivity();
               //initialize();
                break;
            case "ALVAROG":
                Util.IS_ADMIN = 1;
                Util.SUC_NAME="ADMIN";
                startMainActivity();
               // initialize();
                break;
            case "COL01":
                Util.SUC_NAME="COL";
                Util.IS_ADMIN = 0;
               startMainActivity();
               //initialize();
                break;
            case "COL02":
                Util.SUC_NAME="COL";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "TEC01":
                Util.SUC_NAME="TEC";
                Util.IS_ADMIN = 0;
                startMainActivity();
              // initialize();
                break;
            case "TEC02":
                Util.SUC_NAME="TEC";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "CIHUA01":
                Util.SUC_NAME="CIH";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "VGUZMAN":
                Util.SUC_NAME="GZM";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "VPS01":
                Util.SUC_NAME="PIN";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "JOCO2020":
                Util.SUC_NAME="JOC";
                Util.IS_ADMIN = 0;
                startMainActivity();
                //initialize();
                break;
            case "SAYULEI2025":
                Util.SUC_NAME="GZM";
                Util.IS_ADMIN = 0;
                Util._LISTA_PRECIOS = 4;
                startMainActivity();
                //initialize();
                break;


            default:
                Toast.makeText(this, "USUARIO O CONTRASEÑA INCORRECTOS VUELVE A INTENTAR.", Toast.LENGTH_LONG).show();
                break;

        }


    }

    public void initialize(){
        
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkImei();
        }else{
           // checkImeiInBd();
        }


    }



    public void checkImei() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permiso requerido")
                        .setMessage("Este permiso es obligatorio para poder usar esta app.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermission();

                            }
                        });
                builder.show();

            } else {
                requestPermission();
            }


        } else {



        }


    }

    //verifica si esta dado de alta el imei en la bd
   /* public void checkImeiInBd(){
        Log.d("EL IMEI ", getImei());
        // Instantiate the RequestQueue.
        String imei = getImei();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.vigor.mx/checkDevices.php?imei="+imei;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    org.json.JSONObject json = new org.json.JSONObject(response);
                    if (json.getString("imei_exists")=="true"){

                        startMainActivity();

                    }else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        builder.setTitle("Alerta de seguridad")
                                .setMessage("Estas intentando acceder desde un dispositivo no autorizado, si deseas utilizar esta aplicacion solicita el acceso al departamento de sistemas de fertilizantes vigor sa de cv.")
                                .setPositiveButton(android.R.string.ok,null)
                                .show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);

    }*/

    private void startMainActivity(){

        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }



    public void requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
    }

    /*public String getImei(){

        String imei = "";
        TelephonyManager myTm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            imei = myTm.getImei();

        }else{
            imei = myTm.getDeviceId();
        }

        return imei;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            //read phone state
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   // checkImeiInBd();

                } else {
                    requestPermission();
                }
        }
    }


}


